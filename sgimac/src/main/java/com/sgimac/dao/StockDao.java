package com.sgimac.dao;
import java.util.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
/** Criterio común de stock: utilizable significa no vencido por fecha (incluye hoy).
 * No evalúa cuarentenas, conservación ni otras condiciones físicas. */
@Repository
public class StockDao {
    /** Agregación por insumo; LEFT JOIN incluye insumos sin lotes. */
    public static final String BASE = """
        SELECT i.id_insumo, i.codigo, i.nombre AS insumo, i.id_categoria,
        c.nombre AS categoria, i.unidad_medida AS unidad, i.stock_minimo AS minimo,
        COALESCE(SUM(l.cantidad_actual),0) AS stock,
        COALESCE(SUM(CASE WHEN l.fecha_vencimiento >= CURRENT_DATE THEN l.cantidad_actual ELSE 0 END),0) AS utilizable,
        COALESCE(SUM(CASE WHEN l.fecha_vencimiento < CURRENT_DATE THEN l.cantidad_actual ELSE 0 END),0) AS vencido
        FROM insumo i JOIN categoria c ON c.id_categoria=i.id_categoria
        LEFT JOIN lote l ON l.id_insumo=i.id_insumo
        GROUP BY i.id_insumo,i.codigo,i.nombre,i.id_categoria,c.nombre,i.unidad_medida,i.stock_minimo
        """;
    private final JdbcTemplate jdbc;
    public StockDao(JdbcTemplate jdbc){this.jdbc=jdbc;}
    /** @return totales por producto, sin sumar unidades de productos distintos. */
    public List<Map<String,Object>> todos(){return jdbc.queryForList(BASE);}
    /** @param categoria cero para todas @return déficits calculados con stock no vencido. */
    public List<Map<String,?>> bajos(int categoria){
        if(categoria<0)throw new IllegalArgumentException("Categoría inválida");
        String sql="SELECT s.*, minimo-utilizable AS faltante FROM ("+BASE+") s WHERE utilizable < minimo";
        List<Object> p=new ArrayList<>();if(categoria>0){sql+=" AND id_categoria=?";p.add(categoria);}
        return jdbc.query(sql+" ORDER BY categoria,insumo,id_insumo",(rs,n)->{
            Map<String,String> f=new LinkedHashMap<>();
            for(String key:List.of("insumo","categoria","unidad","stock","utilizable","vencido","minimo","faltante"))f.put(key,rs.getString(key));
            return f;
        },p.toArray());
    }
}
