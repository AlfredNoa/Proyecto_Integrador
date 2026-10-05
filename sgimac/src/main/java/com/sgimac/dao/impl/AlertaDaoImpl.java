package com.sgimac.dao.impl;

import com.sgimac.dao.AlertaDao;
import com.sgimac.model.Alerta;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;

/**
 * Implementacion JDBC del DAO de alertas.
 */
@Repository
public class AlertaDaoImpl implements AlertaDao {

    private final JdbcTemplate jdbc;

    private static final String SELECT_BASE =
            "SELECT a.id_alerta, a.id_lote, l.codigo_qr, l.fecha_vencimiento, i.nombre AS nombre_insumo, " +
            "       a.id_usuario_atiende, ua.nombres AS nombre_usuario_atiende, " +
            "       a.tipo, a.nivel, a.estado, a.fecha_generacion " +
            "FROM alerta a " +
            "JOIN lote l ON l.id_lote = a.id_lote " +
            "JOIN insumo i ON i.id_insumo = l.id_insumo " +
            "LEFT JOIN usuario ua ON ua.id_usuario = a.id_usuario_atiende ";

    private final RowMapper<Alerta> mapper = (rs, fila) -> {
        Alerta a = new Alerta();
        a.setIdAlerta(rs.getInt("id_alerta"));
        a.setIdLote(rs.getInt("id_lote"));
        a.setCodigoQrLote(rs.getString("codigo_qr"));
        a.setNombreInsumo(rs.getString("nombre_insumo"));
        java.sql.Date fv = rs.getDate("fecha_vencimiento");
        a.setFechaVencimientoLote(fv != null ? fv.toLocalDate() : null);
        int idAtiende = rs.getInt("id_usuario_atiende");
        a.setIdUsuarioAtiende(rs.wasNull() ? null : idAtiende);
        a.setNombreUsuarioAtiende(rs.getString("nombre_usuario_atiende"));
        a.setTipo(rs.getString("tipo"));
        a.setNivel(rs.getString("nivel"));
        a.setEstado(rs.getString("estado"));
        Timestamp f = rs.getTimestamp("fecha_generacion");
        a.setFechaGeneracion(f != null ? f.toLocalDateTime() : null);
        return a;
    };

    public AlertaDaoImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<Alerta> listarPendientes() {
        return jdbc.query(
                SELECT_BASE + "WHERE a.estado = 'PENDIENTE' " +
                "ORDER BY FIELD(a.nivel, 'ALTO','MEDIO','BAJO'), a.fecha_generacion DESC",
                mapper);
    }

    @Override
    public List<Alerta> listar() {
        return jdbc.query(SELECT_BASE + "ORDER BY a.fecha_generacion DESC", mapper);
    }

    @Override
    public boolean existePendiente(int idLote, String tipo) {
        Integer total = jdbc.queryForObject(
                "SELECT COUNT(*) FROM alerta WHERE id_lote = ? AND tipo = ? AND estado = 'PENDIENTE'",
                Integer.class, idLote, tipo);
        return total != null && total > 0;
    }

    @Override
    public int insertar(Alerta alerta) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO alerta (id_lote, tipo, nivel, estado) VALUES (?, ?, ?, 'PENDIENTE')",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, alerta.getIdLote());
            ps.setString(2, alerta.getTipo());
            ps.setString(3, alerta.getNivel());
            return ps;
        }, keyHolder);
        return keyHolder.getKey().intValue();
    }

    @Override
    public boolean marcarAtendida(int idAlerta, int idUsuarioAtiende) {
        int filas = jdbc.update(
                "UPDATE alerta SET estado = 'ATENDIDA', id_usuario_atiende = ? WHERE id_alerta = ?",
                idUsuarioAtiende, idAlerta);
        return filas > 0;
    }
}
