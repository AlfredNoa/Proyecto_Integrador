package com.sgimac.dao.impl;

import com.sgimac.dao.LogAuditoriaDao;
import com.sgimac.model.LogAuditoria;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

/**
 * Implementacion JDBC del DAO de auditoria.
 */
@Repository
public class LogAuditoriaDaoImpl implements LogAuditoriaDao {

    private final JdbcTemplate jdbc;

    private final RowMapper<LogAuditoria> mapper = (rs, fila) -> {
        LogAuditoria log = new LogAuditoria();
        log.setIdLog(rs.getInt("id_log"));
        log.setIdUsuario(rs.getInt("id_usuario"));
        log.setNombreUsuario(rs.getString("nombre_usuario"));
        log.setAccion(rs.getString("accion"));
        log.setEntidad(rs.getString("entidad"));
        Timestamp f = rs.getTimestamp("fecha");
        log.setFecha(f != null ? f.toLocalDateTime() : null);
        return log;
    };

    public LogAuditoriaDaoImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<LogAuditoria> listar() {
        return jdbc.query(
                "SELECT g.id_log, g.id_usuario, u.nombres AS nombre_usuario, " +
                "       g.accion, g.entidad, g.fecha " +
                "FROM log_auditoria g " +
                "JOIN usuario u ON u.id_usuario = g.id_usuario " +
                "ORDER BY g.fecha DESC",
                mapper);
    }

    @Override
    public void registrar(int idUsuario, String accion, String entidad) {
        jdbc.update(
                "INSERT INTO log_auditoria (id_usuario, accion, entidad) VALUES (?, ?, ?)",
                idUsuario, accion, entidad);
    }
}
