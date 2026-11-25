package com.sgimac.reportes;

import com.sgimac.dao.*;
import com.sgimac.controller.*;
import java.time.LocalDate;
import java.util.*;
import org.junit.jupiter.api.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.mock.web.*;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.web.server.ResponseStatusException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/** Verifica SQL real en H2 modo MySQL, filtros, aislamiento de sesión y exportación. */
class ConsultasAdicionalesTest {
    JdbcTemplate jdbc;
    ConsultaAdicionalDao dao;
    @BeforeEach void crearDatos() {
        var ds=new DriverManagerDataSource("jdbc:h2:mem:"+UUID.randomUUID()+";MODE=MySQL;DB_CLOSE_DELAY=-1","sa","");
        jdbc=new JdbcTemplate(ds);dao=new ConsultaAdicionalDao(jdbc);
        jdbc.execute("CREATE TABLE categoria(id_categoria INT PRIMARY KEY,nombre VARCHAR(80))");
        jdbc.execute("CREATE TABLE insumo(id_insumo INT PRIMARY KEY,id_categoria INT,nombre VARCHAR(80),unidad_medida VARCHAR(30),stock_minimo INT)");
        jdbc.execute("CREATE TABLE lote(id_lote INT PRIMARY KEY,id_insumo INT,codigo_qr VARCHAR(30),cantidad_actual INT)");
        jdbc.execute("CREATE TABLE usuario(id_usuario INT PRIMARY KEY,nombres VARCHAR(80))");
        jdbc.execute("CREATE TABLE movimiento(id_movimiento INT PRIMARY KEY,id_lote INT,id_usuario INT,fecha TIMESTAMP(6),tipo VARCHAR(20),cantidad INT)");
        jdbc.update("INSERT INTO categoria VALUES(1,'Material médico'),(2,'Medicamentos')");
        jdbc.update("INSERT INTO insumo VALUES(1,1,'Guantes','Cajas',10),(2,1,'Gasas','Paquetes',10),(3,2,'Alcohol','Frascos',5),(4,2,'Suero','Frascos',0),(5,2,'Tabletas','Tabletas',10)");
        jdbc.update("INSERT INTO lote VALUES(1,1,'LOT-001',6),(2,1,'LOT-002',5),(3,2,'LOT-003',2),(4,2,'LOT-004',3),(5,5,'LOT-005',10)");
        jdbc.update("INSERT INTO usuario VALUES(1,'Ana Pérez')");
        jdbc.update("INSERT INTO movimiento VALUES(1,1,1,'2026-11-01 00:00:00','ENTRADA',6),(2,1,1,'2026-11-15 23:59:59.999999','SALIDA',2),(3,3,1,'2026-11-16 00:00:00','ENTRADA',2),(4,3,1,'2026-11-10 12:00:00','ENTRADA',3)");
    }
    @Test void sumaLotesIncluyeSinLotesYExcluyeIgualAlMinimo() {
        var filas=dao.stockBajo(0);
        assertEquals(2,filas.size());
        var gasas=filas.stream().filter(f->"Gasas".equals(f.get("insumo"))).findFirst().orElseThrow();
        assertEquals("5",gasas.get("stock"));assertEquals("5",gasas.get("faltante"));
        var alcohol=filas.stream().filter(f->"Alcohol".equals(f.get("insumo"))).findFirst().orElseThrow();
        assertEquals("0",alcohol.get("stock"));assertEquals("5",alcohol.get("faltante"));
        assertEquals(1,dao.stockBajo(1).size());assertTrue(dao.stockBajo(99).isEmpty());
    }
    @Test void movimientosIncluyeDiasCompletosYCombinaFiltros() {
        LocalDate desde=LocalDate.of(2026,11,1),hasta=LocalDate.of(2026,11,15);
        assertEquals(3,dao.movimientos(0,desde,hasta,"TODOS").size());
        var filas=dao.movimientos(1,desde,hasta,"SALIDA");
        assertEquals(1,filas.size());assertEquals("15/11/2026 23:59",filas.get(0).get("fecha"));
        assertEquals(1,dao.movimientos(2,desde,hasta,"ENTRADA").size());
        assertThrows(IllegalArgumentException.class,()->dao.movimientos(0,hasta,desde,"TODOS"));
        assertThrows(IllegalArgumentException.class,()->dao.movimientos(0,desde,hasta,"SALIDA' OR 1=1"));
    }
    private MockHttpSession sesion() {var s=new MockHttpSession();s.setAttribute("usuarioId",1);s.setAttribute("usuarioNombre","Ana Pérez");return s;}
    @Test void controllerValidaSinEjecutarSql() {
        var db=mock(ConsultaAdicionalDao.class);var c=new ConsultaMovimientosController(db,mock(InsumoDao.class),new OperativoPdfService());
        for(var criterios:List.of(Map.of("desde","2026-11-15","hasta","2026-11-01"),Map.of("desde","2026-11-01","hasta","2026-11-15","tipo","X"),Map.of("insumo","999","desde","2026-11-01","hasta","2026-11-15"))) {
            var m=new ExtendedModelMap();assertEquals("consultas/movimientos_formulario",c.consultar(criterios,sesion(),m));assertTrue(m.containsKey("error"));
        }
        var stock=new ConsultaStockController(db,mock(CategoriaDao.class),new OperativoPdfService());
        var m=new ExtendedModelMap();assertEquals("consultas/stock_formulario",stock.consultar(Map.of("categoria","999"),sesion(),m));assertTrue(m.containsKey("error"));
        verifyNoInteractions(db);
    }
    @Test void pdfConservaCopiaYRechazaConsultaAjenaOAnterior() throws Exception {
        var db=spy(dao);var s=sesion();var c=new ConsultaStockController(db,mock(CategoriaDao.class),new OperativoPdfService());
        var ruta=c.consultar(Map.of("categoria","0"),s,new ExtendedModelMap());String id=ruta.substring(ruta.indexOf('=')+1);
        assertTrue(ruta.startsWith("redirect:/consultas/stock/resultado?id="));
        jdbc.update("UPDATE lote SET cantidad_actual=99");
        assertTrue(new String(c.descargar(id,s).getBody(),java.nio.charset.StandardCharsets.ISO_8859_1).startsWith("%PDF"));
        var m=new ExtendedModelMap();c.resultado(id,s,m);assertEquals(2,((ConsultaOperativa)m.get("reporte")).getFilas().size());
        verify(db,times(1)).stockBajo(0);
        assertThrows(ResponseStatusException.class,()->c.descargar(id,sesion()));
        c.consultar(Map.of("categoria","0"),s,new ExtendedModelMap());
        assertThrows(ResponseStatusException.class,()->c.descargar(id,s));
    }
    @Test void consultasNoSobrescribenResultadosEntreSi() {
        var s=sesion();var pdf=new OperativoPdfService();
        var c2=new ConsultaMovimientosController(dao,mock(InsumoDao.class),pdf);
        var c3=new ConsultaStockController(dao,mock(CategoriaDao.class),pdf);
        var ruta=c2.consultar(Map.of("desde","2026-11-01","hasta","2026-11-15"),s,new ExtendedModelMap());
        c3.consultar(Map.of("categoria","0"),s,new ExtendedModelMap());
        assertEquals("consultas/movimientos_resultado",c2.resultado(ruta.substring(ruta.indexOf('=')+1),s,new ExtendedModelMap()));
        assertEquals("redirect:/login",c2.formulario(new MockHttpSession(),new ExtendedModelMap()));
    }
    @Test void nuevasPlantillasPdfVaciasYMultipagina() throws Exception {
        var pdf=new OperativoPdfService();
        for(String tipo:List.of("consulta_movimientos","consulta_stock")) for(int n:List.of(0,50)) {
            List<Map<String,?>> filas=new ArrayList<>();
            for(int i=0;i<n;i++)filas.add(tipo.endsWith("stock")?Map.of("insumo","Gasa estéril 10 x 10 cm","categoria","Material médico","stock","5","minimo","10","faltante","5","unidad","Paquetes"):
                Map.of("fecha","15/11/2026 23:59","insumo","Gasa estéril 10 x 10 cm","lote","LOT-001","tipo","SALIDA","cantidad","5","unidad","Paquetes","usuario","Ana Pérez"));
            byte[] bytes=pdf.generar(tipo,new ConsultaOperativa("Ana Pérez","Prueba de filtros",filas,"Resultados: "+n));
            var reader=new com.lowagie.text.pdf.PdfReader(bytes);
            assertTrue(reader.getNumberOfPages()>=1);if(n==50)assertTrue(reader.getNumberOfPages()>1);
            String raw=new String(bytes,java.nio.charset.StandardCharsets.ISO_8859_1);assertTrue(raw.contains("/Subtype/Image")||raw.contains("/Subtype /Image"));reader.close();
        }
    }
}
