package com.sgimac.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/** Consultas de solo lectura para los informes 02 y 03. Cada fila identifica un evento. */
@Repository
public class ConsultaOperativaDao {
    private final JdbcTemplate jdbc;
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    public ConsultaOperativaDao(JdbcTemplate jdbc){this.jdbc=jdbc;}

    /** Incluye ambos días del período. Un límite vacío deja el período abierto por ese extremo. */
    public List<Map<String, ?>> movimientos(LocalDate desde, LocalDate hasta, String tipo) {
        String sql = """
            SELECT m.fecha, i.nombre AS insumo, l.codigo_qr AS lote, m.tipo, m.cantidad,
                   i.unidad_medida AS unidad, u.nombres AS usuario
            FROM movimiento m JOIN lote l ON l.id_lote=m.id_lote
            JOIN insumo i ON i.id_insumo=l.id_insumo JOIN usuario u ON u.id_usuario=m.id_usuario WHERE 1=1
            """;
        List<Object> p = new ArrayList<>();
        if(desde!=null){sql+=" AND m.fecha >= ?"; p.add(desde.atStartOfDay());}
        if(hasta!=null){sql+=" AND m.fecha < ?"; p.add(hasta.plusDays(1).atStartOfDay());}
        if(!tipo.equals("TODOS")){sql+=" AND m.tipo = ?";p.add(tipo);}
        return filas(sql+" ORDER BY m.fecha DESC, m.id_movimiento DESC",p);
    }

    /** El LEFT JOIN conserva alertas pendientes sin usuario de atención. */
    public List<Map<String, ?>> alertas(String tipo, String nivel, String estado) {
        String sql = """
            SELECT a.fecha_generacion AS fecha, i.nombre AS insumo, l.codigo_qr AS lote,
                   a.tipo, a.nivel, a.estado, COALESCE(u.nombres,'—') AS responsable
            FROM alerta a JOIN lote l ON l.id_lote=a.id_lote JOIN insumo i ON i.id_insumo=l.id_insumo
            LEFT JOIN usuario u ON u.id_usuario=a.id_usuario_atiende WHERE 1=1
            """;
        List<Object> p = new ArrayList<>();
        if(!tipo.equals("TODOS")){sql+=" AND a.tipo = ?";p.add(tipo);}
        if(!nivel.equals("TODOS")){sql+=" AND a.nivel = ?";p.add(nivel);}
        if(!estado.equals("TODOS")){sql+=" AND a.estado = ?";p.add(estado);}
        return filas(sql+" ORDER BY a.fecha_generacion DESC, a.id_alerta DESC",p);
    }
    private List<Map<String, ?>> filas(String sql,List<Object> params){
        return jdbc.query(sql,(rs,i)->{
            Map<String,String> fila=new LinkedHashMap<>();
            var md=rs.getMetaData();
            for(int n=1;n<=md.getColumnCount();n++){
                String key=md.getColumnLabel(n);
                fila.put(key,key.equals("fecha")?rs.getTimestamp(n).toLocalDateTime().format(FECHA):rs.getString(n));
            }
            return fila;
        }, params.toArray());
    }
}
