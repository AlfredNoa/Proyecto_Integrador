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

    /**
     * Suma todos los lotes, incluidos los vencidos; no representa stock apto para uso.
     * LEFT JOIN incluye insumos sin lotes con stock cero. HAVING selecciona totales
     * estrictamente menores que el mínimo del insumo. No suma distintas unidades.
     * @param categoria identificador o cero para todas
     * @return una fila por insumo con déficit, ordenada por categoría y nombre
     * @throws IllegalArgumentException si el identificador es negativo
     */
    public List<Map<String, ?>> stockBajo(int categoria) {
        if (categoria < 0) throw new IllegalArgumentException("Categoría inválida.");
        String sql = """
            SELECT i.nombre AS insumo, c.nombre AS categoria, i.unidad_medida AS unidad,
                   COALESCE(SUM(l.cantidad_actual), 0) AS stock,
                   i.stock_minimo AS minimo,
                   i.stock_minimo - COALESCE(SUM(l.cantidad_actual), 0) AS faltante
            FROM insumo i
            JOIN categoria c ON c.id_categoria = i.id_categoria
            LEFT JOIN lote l ON l.id_insumo = i.id_insumo
            """;
        List<Object> params = new ArrayList<>();
        if (categoria > 0) { sql += " WHERE i.id_categoria = ?"; params.add(categoria); }
        sql += """
             GROUP BY i.id_insumo, i.nombre, c.id_categoria, c.nombre,
                      i.unidad_medida, i.stock_minimo
             HAVING COALESCE(SUM(l.cantidad_actual), 0) < i.stock_minimo
             ORDER BY c.nombre, i.nombre, i.id_insumo
            """;
        return jdbc.query(sql, (rs, n) -> {
            Map<String, String> fila = new LinkedHashMap<>();
            for (String campo : List.of("insumo", "categoria", "unidad", "stock", "minimo", "faltante"))
                fila.put(campo, rs.getString(campo));
            return fila;
        }, params.toArray());
    }
}
