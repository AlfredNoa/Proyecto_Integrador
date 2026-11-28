package com.sgimac.service;
import com.sgimac.dao.*;
import java.time.*;
import java.util.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.*;
import org.springframework.transaction.support.TransactionTemplate;
/** Prueba SQL real en H2 MySQL, límites de fechas, FEFO, alertas y páginas. */
class MejorasInventarioTest {
 JdbcTemplate db;StockDao stock;AlertasService alerts;TransactionTemplate tx;
 @BeforeEach void setup(){
  var ds=new DriverManagerDataSource("jdbc:h2:mem:"+UUID.randomUUID()+";MODE=MySQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1","sa","");
  db=new JdbcTemplate(ds);tx=new TransactionTemplate(new DataSourceTransactionManager(ds));stock=new StockDao(db);alerts=new AlertasService(db,stock);
  db.execute("CREATE TABLE rol(id_rol INT PRIMARY KEY,nombre VARCHAR(80))");
  db.execute("CREATE TABLE usuario(id_usuario INT PRIMARY KEY,id_rol INT,nombres VARCHAR(80),activo BOOLEAN)");
  db.execute("CREATE TABLE categoria(id_categoria INT PRIMARY KEY,nombre VARCHAR(80))");
  db.execute("CREATE TABLE insumo(id_insumo INT PRIMARY KEY,id_categoria INT,codigo VARCHAR(30),nombre VARCHAR(80),unidad_medida VARCHAR(30),stock_minimo INT)");
  db.execute("CREATE TABLE ubicacion(id_ubicacion INT PRIMARY KEY,nombre VARCHAR(80))");
  db.execute("CREATE TABLE proveedor(id_proveedor INT PRIMARY KEY,razon_social VARCHAR(80))");
  db.execute("CREATE TABLE lote(id_lote INT PRIMARY KEY,id_insumo INT,id_ubicacion INT,id_proveedor INT,codigo_qr VARCHAR(30),fecha_vencimiento DATE,cantidad_actual INT CHECK(cantidad_actual>=0))");
  db.execute("CREATE TABLE alerta(id_alerta INT AUTO_INCREMENT PRIMARY KEY,id_lote INT NULL,id_insumo INT NULL,id_usuario_atiende INT NULL,tipo VARCHAR(30),nivel VARCHAR(20),estado VARCHAR(20),fecha_generacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
  db.execute("CREATE TABLE notificacion(id_notificacion INT AUTO_INCREMENT PRIMARY KEY,id_alerta INT,id_usuario INT,mensaje VARCHAR(255),leida BOOLEAN,fecha_envio TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
  db.execute("CREATE TABLE movimiento(id_movimiento INT PRIMARY KEY,id_lote INT,id_usuario INT,tipo VARCHAR(20),cantidad INT,fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
  db.update("INSERT INTO rol VALUES(3,'Administrador'),(2,'Personal Médico')");db.update("INSERT INTO usuario VALUES(1,3,'Ana',TRUE),(2,2,'María',TRUE)");
  db.update("INSERT INTO categoria VALUES(1,'Analgésicos')");db.update("INSERT INTO ubicacion VALUES(1,'A1')");db.update("INSERT INTO proveedor VALUES(1,'Proveedor')");
  db.update("INSERT INTO insumo VALUES(1,1,'INS-1','Paracetamol','Tabletas',50),(2,1,'INS-2','Sin lotes','Cajas',10),(3,1,'INS-3','Igual mínimo','Frascos',5)");
  lote(1,1,-1,10);lote(2,1,0,3);lote(3,1,30,2);lote(4,1,31,0);lote(5,3,90,5);lote(6,1,91,0);
 }
 void lote(int id,int i,int dias,int cantidad){db.update("INSERT INTO lote VALUES(?,?,1,1,?,?,?)",id,i,"LOT-"+id,LocalDate.now().plusDays(dias),cantidad);}
 void sync(){tx.executeWithoutResult(s->alerts.actualizar());}
 int n(String sql){return db.queryForObject(sql,Integer.class);}
 @Test void stockIncluyeHoyExcluyeVencidosYConservaSinLotes(){
  var rows=stock.bajos(1);assertEquals(2,rows.size());var p=rows.stream().filter(f->f.get("insumo").equals("Paracetamol")).findFirst().orElseThrow();
  assertEquals("15",p.get("stock"));assertEquals("10",p.get("vencido"));assertEquals("5",p.get("utilizable"));assertEquals("45",p.get("faltante"));
  assertEquals("0",rows.stream().filter(f->f.get("insumo").equals("Sin lotes")).findFirst().orElseThrow().get("utilizable"));
  assertTrue(stock.bajos(99).isEmpty());
 }
 @Test void fefoDistribuyePorFechaYNoIncluyeAgotadosNiVencidos(){
  var service=new FefoService(db);var r=service.sugerir(1,4);var list=(List<Map<String,Object>>)r.get("lotes");
  assertEquals(2,list.size());assertEquals(2,((Number)list.get(0).get("id_lote")).intValue());assertEquals(3L,list.get(0).get("sugerida"));assertEquals(1L,list.get(1).get("sugerida"));assertEquals(0L,r.get("faltante"));
  assertEquals(5L,service.sugerir(1,10).get("faltante"));assertEquals(2L,service.sugerir(2,2).get("faltante"));
  assertThrows(IllegalArgumentException.class,()->service.sugerir(1,0));assertEquals(15,n("SELECT SUM(cantidad_actual) FROM lote WHERE id_insumo=1"));
 }
 @Test void alertasNoDuplicanYAtendidaNoSeReabreSinCambio(){
  sync();int count=n("SELECT COUNT(*) FROM alerta"),notificaciones=n("SELECT COUNT(*) FROM notificacion");assertEquals(6,count);
  assertEquals(1,n("SELECT COUNT(*) FROM alerta WHERE id_insumo=2 AND tipo='STOCK_BAJO' AND id_lote IS NULL"));
  int id=n("SELECT id_alerta FROM alerta WHERE id_insumo=1 AND tipo='STOCK_BAJO'");tx.executeWithoutResult(t->alerts.atender(id,1));sync();sync();
  assertEquals(count,n("SELECT COUNT(*) FROM alerta"));assertEquals(notificaciones,n("SELECT COUNT(*) FROM notificacion"));
  assertEquals("ATENDIDA",db.queryForObject("SELECT estado FROM alerta WHERE id_alerta=?",String.class,id));
 }
 @Test void resuelvePorReposicionYReabreSoloNuevaCondicion(){
  sync();db.update("UPDATE lote SET cantidad_actual=50 WHERE id_lote=2");sync();
  assertEquals(1,n("SELECT COUNT(*) FROM alerta WHERE tipo='STOCK_BAJO' AND id_insumo=1 AND estado='RESUELTA'"));
  db.update("UPDATE lote SET cantidad_actual=1 WHERE id_lote=2");sync();assertEquals(1,n("SELECT COUNT(*) FROM alerta WHERE tipo='STOCK_BAJO' AND id_insumo=1 AND estado='PENDIENTE'"));
  db.update("UPDATE lote SET cantidad_actual=0 WHERE id_lote=1");sync();assertEquals("RESUELTA",db.queryForObject("SELECT estado FROM alerta WHERE id_lote=1",String.class));
 }
 @Test void subeNivelAlPasarDe31A30DiasYNotificaUnaVez(){
  db.update("UPDATE lote SET cantidad_actual=5 WHERE id_lote=4");sync();int id=n("SELECT id_alerta FROM alerta WHERE id_lote=4");alerts.atender(id,1);int antes=n("SELECT COUNT(*) FROM notificacion");
  db.update("UPDATE lote SET fecha_vencimiento=? WHERE id_lote=4",LocalDate.now().plusDays(30));sync();
  assertEquals("ALTO",db.queryForObject("SELECT nivel FROM alerta WHERE id_alerta=?",String.class,id));assertEquals("PENDIENTE",db.queryForObject("SELECT estado FROM alerta WHERE id_alerta=?",String.class,id));
  assertEquals(antes+1,n("SELECT COUNT(*) FROM notificacion"));sync();assertEquals(antes+1,n("SELECT COUNT(*) FROM notificacion"));
 }
 @Test void consolidaAlertasAntiguasPorInsumo(){
  db.update("INSERT INTO alerta(id_lote,tipo,nivel,estado) VALUES(2,'STOCK_BAJO','MEDIO','PENDIENTE'),(3,'STOCK_BAJO','MEDIO','PENDIENTE')");sync();
  assertEquals(1,n("SELECT COUNT(*) FROM alerta WHERE tipo='STOCK_BAJO' AND id_insumo=1 AND estado<>'RESUELTA'"));
  assertEquals(1,n("SELECT COUNT(*) FROM alerta WHERE tipo='STOCK_BAJO' AND estado='RESUELTA'"));
 }
 @Test void paginasFiltrosLiteralesYClamping(){
  for(int i=4;i<34;i++)db.update("INSERT INTO insumo VALUES(?,1,?,?, 'Tabletas',0)",i,"INS-"+i,"Producto "+i);
  var dao=new ListadosDao(db);var a=dao.listar("insumos","","TODOS",1,10);var b=dao.listar("insumos","","TODOS",2,10);
  assertEquals(33L,a.get("total"));assertEquals(10,((List<?>)a.get("filas")).size());assertNotEquals(a.get("filas"),b.get("filas"));
  assertEquals(4,dao.listar("insumos","","TODOS",999,10).get("pagina"));assertEquals(1,dao.listar("insumos","","TODOS",-1,10).get("pagina"));
  assertEquals(0L,dao.listar("insumos","%","TODOS",1,20).get("total"));assertEquals(2L,dao.listar("insumos","","BAJO",1,20).get("total"));
  assertEquals(1L,dao.listar("lotes","","VENCIDO",1,20).get("total"));assertEquals(3L,dao.listar("lotes","","PROXIMO",1,20).get("total"));sync();assertEquals(6L,dao.listar("alertas","","PENDIENTE",1,20).get("total"));
 }
 @Test void reporteAlertasIncluyeInsumoSinLoteYResueltas(){sync();var dao=new ConsultaOperativaDao(db);assertEquals(6,dao.alertas("TODOS","TODOS","TODOS").size());db.update("UPDATE lote SET cantidad_actual=0 WHERE id_lote=1");sync();assertEquals(1,dao.alertas("TODOS","TODOS","RESUELTA").size());}
 @Test void revisionesSimultaneasNoDuplican() throws Exception {
  var pool=java.util.concurrent.Executors.newFixedThreadPool(4);
  try {var tareas=new ArrayList<java.util.concurrent.Callable<Void>>();
   for(int i=0;i<8;i++)tareas.add(()->{sync();return null;});
   for(var f:pool.invokeAll(tareas))f.get();
   assertEquals(6,n("SELECT COUNT(*) FROM alerta"));
   assertEquals(3,n("SELECT COUNT(*) FROM notificacion"));
  } finally {pool.shutdownNow();}
 }
}
