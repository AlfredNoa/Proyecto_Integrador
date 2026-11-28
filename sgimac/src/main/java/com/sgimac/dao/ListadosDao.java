package com.sgimac.dao;
import java.util.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
/** Listados con filtros parametrizados, orden estable y paginación SQL (10, 20 o 50 filas). */
@Repository
public class ListadosDao {
 private final JdbcTemplate jdbc;
 public ListadosDao(JdbcTemplate jdbc){this.jdbc=jdbc;}
 /**
  * @param modulo insumos, lotes, movimientos o alertas
  * @param q búsqueda literal
  *
  * @param estado filtro específico
  * @param pagina página empezando en uno
  * @param tam tamaño permitido
  *
  * @return filas, conteo y página efectiva; ajusta páginas fuera del rango. */
 public Map<String,Object> listar(String modulo,String q,String estado,int pagina,int tam){
  int size=Set.of(10,20,50).contains(tam)?tam:20; q=q==null?"":q.strip();if(q.length()>100)q=q.substring(0,100);
  String base=switch(modulo){
   case "insumos" -> "SELECT s.*, minimo-utilizable AS faltante FROM ("+StockDao.BASE+") s";
   case "lotes" -> "SELECT l.*,CASE WHEN l.cantidad_actual=0 THEN 'Agotado' WHEN l.fecha_vencimiento<CURRENT_DATE THEN 'Vencido' WHEN l.fecha_vencimiento<=CURRENT_DATE + INTERVAL '30' DAY THEN 'Crítico' WHEN l.fecha_vencimiento<=CURRENT_DATE + INTERVAL '90' DAY THEN 'Por vencer' ELSE 'Vigente' END AS situacion,i.nombre AS insumo,i.unidad_medida AS unidad,u.nombre AS ubicacion,p.razon_social AS proveedor FROM lote l JOIN insumo i ON i.id_insumo=l.id_insumo JOIN ubicacion u ON u.id_ubicacion=l.id_ubicacion JOIN proveedor p ON p.id_proveedor=l.id_proveedor";
   case "movimientos" -> "SELECT m.*,i.nombre AS insumo,i.unidad_medida AS unidad,l.codigo_qr,u.nombres AS usuario FROM movimiento m JOIN lote l ON l.id_lote=m.id_lote JOIN insumo i ON i.id_insumo=l.id_insumo JOIN usuario u ON u.id_usuario=m.id_usuario";
   case "alertas" -> "SELECT a.*,i.nombre AS insumo,COALESCE(l.codigo_qr,'Por insumo') AS codigo_qr,COALESCE(u.nombres,'Sin asignar') AS usuario FROM alerta a LEFT JOIN lote l ON l.id_lote=a.id_lote JOIN insumo i ON i.id_insumo=COALESCE(a.id_insumo,l.id_insumo) LEFT JOIN usuario u ON u.id_usuario=a.id_usuario_atiende";
   default -> throw new IllegalArgumentException("Listado desconocido");
  };
  String filtro=" WHERE 1=1";List<Object> args=new ArrayList<>();
  if(!q.isEmpty()){String texto=q.replace("!","!!").replace("%","!%").replace("_","!_");
   filtro+=" AND (LOWER(insumo) LIKE ? ESCAPE '!' OR LOWER("+(modulo.equals("insumos")?"codigo":"codigo_qr")+") LIKE ? ESCAPE '!')";
   args.add("%"+texto.toLowerCase(java.util.Locale.ROOT)+"%");args.add(args.get(0));}
  if(modulo.equals("insumos")&&"BAJO".equals(estado))filtro+=" AND utilizable<minimo";
  if(modulo.equals("lotes"))filtro+=switch(estado){case "VENCIDO"->" AND fecha_vencimiento<CURRENT_DATE AND cantidad_actual>0";case "UTILIZABLE"->" AND fecha_vencimiento>=CURRENT_DATE AND cantidad_actual>0";case "PROXIMO"->" AND fecha_vencimiento>=CURRENT_DATE AND fecha_vencimiento<=(CURRENT_DATE + INTERVAL '90' DAY) AND cantidad_actual>0";case "AGOTADO"->" AND cantidad_actual=0";default->"";};
  if((modulo.equals("movimientos")&&Set.of("ENTRADA","SALIDA").contains(estado))||(modulo.equals("alertas")&&Set.of("PENDIENTE","ATENDIDA","RESUELTA").contains(estado))){filtro+=" AND "+(modulo.equals("movimientos")?"tipo":"estado")+"=?";args.add(estado);}
  String from=" FROM ("+base+") datos"+filtro;
  long total=jdbc.queryForObject("SELECT COUNT(*)"+from,Long.class,args.toArray());
  int pages=(int)Math.max(1,(total+size-1)/size),actual=Math.min(Math.max(1,pagina),pages);
  String orden=switch(modulo){case "insumos"->"insumo,id_insumo";case "lotes"->"fecha_vencimiento,id_lote";case "movimientos"->"fecha DESC,id_movimiento DESC";default->"fecha_generacion DESC,id_alerta DESC";};
  args.add(size);args.add((actual-1)*size);
  return Map.of("filas",jdbc.queryForList("SELECT *"+from+" ORDER BY "+orden+" LIMIT ? OFFSET ?",args.toArray()),"total",total,"pagina",actual,"paginas",pages,"tam",size,"q",q,"estado",estado);
 }
}
