package com.sgimac.dao.impl;

import com.sgimac.dao.LogAuditoriaDao;
import com.sgimac.model.LogAuditoria;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

/**
 * Implementación JDBC de {@link LogAuditoriaDao} para la tabla {@code log_auditoria}, basada en
 * {@link org.springframework.jdbc.core.JdbcTemplate}.
 *
 * @author Equipo SGIMAC
 * @version 1.0
 */
@Repository
public class LogAuditoriaDaoImpl implements LogAuditoriaDao {

    /**
     * Plantilla JDBC de Spring con la que se ejecutan las consultas SQL (inyectada por el
     * constructor).
     */
    private final JdbcTemplate jdbc;

    /** Convierte cada fila del {@code ResultSet} en un objeto del modelo. */
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

    /**
     * Crea el DAO con la plantilla JDBC que Spring inyecta.
     *
     * @param jdbc plantilla JDBC configurada con el {@code DataSource} de MySQL
     */
    public LogAuditoriaDaoImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
    @Override
    public void registrar(int idUsuario, String accion, String entidad) {
        jdbc.update(
                "INSERT INTO log_auditoria (id_usuario, accion, entidad) VALUES (?, ?, ?)",
                idUsuario, accion, entidad);
    }
}
