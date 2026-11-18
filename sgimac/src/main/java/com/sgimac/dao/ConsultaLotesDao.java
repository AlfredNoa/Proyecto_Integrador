package com.sgimac.dao;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/** Consulta 01: une lotes con sus insumos, ubicaciones y proveedores sin alterar datos. */
@Repository
public class ConsultaLotesDao {
    private final JdbcTemplate jdbc;
    public ConsultaLotesDao(JdbcTemplate jdbc) { this.jdbc=jdbc; }

    /**
     * Recupera una fila por lote, incluyendo ambos extremos del período.
     * Los valores se envían como parámetros JDBC, separados del texto SQL.
     * @param insumo identificador; cero selecciona todos los insumos
     * @param desde primera fecha de vencimiento incluida
     * @param hasta última fecha de vencimiento incluida
     * @return filas ordenadas por vencimiento e identificador de lote
     */
    public List<Map<String, ?>> consultar(int insumo, LocalDate desde, LocalDate hasta) {
        if(insumo<0 || desde==null || hasta==null || desde.isAfter(hasta))
            throw new IllegalArgumentException("Criterios de consulta inválidos.");
        String sql="""
            SELECT i.nombre AS insumo, l.codigo_qr AS lote, l.fecha_vencimiento,
                   l.cantidad_actual AS stock, i.unidad_medida AS unidad,
                   u.nombre AS ubicacion, p.razon_social AS proveedor
            FROM lote l
            JOIN insumo i ON i.id_insumo=l.id_insumo
            JOIN ubicacion u ON u.id_ubicacion=l.id_ubicacion
            JOIN proveedor p ON p.id_proveedor=l.id_proveedor
            WHERE l.fecha_vencimiento BETWEEN ? AND ?
            """;
        List<Object> params=new ArrayList<>();
        params.add(java.sql.Date.valueOf(desde)); params.add(java.sql.Date.valueOf(hasta));
        if(insumo>0) { sql+=" AND l.id_insumo = ?"; params.add(insumo); }
        sql+=" ORDER BY l.fecha_vencimiento, l.id_lote";
        return jdbc.query(sql,(rs,n)->{
            Map<String,String> f=new LinkedHashMap<>();
            for(String campo:List.of("insumo","lote","stock","unidad","ubicacion","proveedor"))
                f.put(campo,rs.getString(campo));
            f.put("vence",rs.getDate("fecha_vencimiento").toLocalDate()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            return f;
        },params.toArray());
    }
}
