package com.sgimac.dao;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/** Consultas 02 y 03 de solo lectura, con criterios enviados como parámetros JDBC. */
@Repository
public class ConsultaAdicionalDao {
    private final JdbcTemplate jdbc;
    /** @param jdbc acceso a la conexión configurada por Spring */
    public ConsultaAdicionalDao(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    /**
     * Recupera movimientos de un insumo o de todos, incluyendo ambos días completos.
     * @param insumo identificador o cero para todos
     * @param desde primer día del periodo
     * @param hasta último día del periodo
     * @param tipo TODOS, ENTRADA o SALIDA
     * @return filas ordenadas de la más reciente a la más antigua
     * @throws IllegalArgumentException si los criterios son inválidos
     */
    public List<Map<String, ?>> movimientos(int insumo, LocalDate desde, LocalDate hasta, String tipo) {
        if (insumo < 0 || desde == null || hasta == null || desde.isAfter(hasta)
                || desde.getYear() < 1000 || hasta.getYear() > 9999
                || tipo == null || !Set.of("TODOS", "ENTRADA", "SALIDA").contains(tipo))
            throw new IllegalArgumentException("Criterios de movimientos inválidos.");
        String sql = """
            SELECT m.fecha, i.nombre AS insumo, l.codigo_qr AS lote, m.tipo,
                   m.cantidad, i.unidad_medida AS unidad, u.nombres AS usuario
            FROM movimiento m
            JOIN lote l ON l.id_lote = m.id_lote
            JOIN insumo i ON i.id_insumo = l.id_insumo
            JOIN usuario u ON u.id_usuario = m.id_usuario
            WHERE m.fecha >= ? AND m.fecha <= ?
            """;
        List<Object> params = new ArrayList<>();
        params.add(desde.atStartOfDay());
        params.add(hasta.atTime(23, 59, 59, 999999000));
        if (insumo > 0) { sql += " AND i.id_insumo = ?"; params.add(insumo); }
        if (!"TODOS".equals(tipo)) { sql += " AND m.tipo = ?"; params.add(tipo); }
        sql += " ORDER BY m.fecha DESC, m.id_movimiento DESC";
        return jdbc.query(sql, (rs, n) -> {
            Map<String, String> fila = new LinkedHashMap<>();
            fila.put("fecha", rs.getTimestamp("fecha").toLocalDateTime()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            for (String campo : List.of("insumo", "lote", "tipo", "cantidad", "unidad", "usuario"))
                fila.put(campo, rs.getString(campo));
            return fila;
        }, params.toArray());
    }

    /** Consulta el déficit respecto al stock utilizable por fecha, incluidos insumos sin lotes.
     * @param categoria cero para todas las categorías
     * @return total registrado, vencido, utilizable, mínimo y faltante por insumo */
    public List<Map<String, ?>> stockBajo(int categoria) { return new StockDao(jdbc).bajos(categoria); }
}
