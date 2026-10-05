package com.sgimac.dao.impl;

import com.sgimac.dao.NotificacionDao;
import com.sgimac.model.Notificacion;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

/**
 * Implementacion JDBC del DAO de notificaciones.
 */
@Repository
public class NotificacionDaoImpl implements NotificacionDao {

    private final JdbcTemplate jdbc;

    private final RowMapper<Notificacion> mapper = (rs, fila) -> {
        Notificacion n = new Notificacion();
        n.setIdNotificacion(rs.getInt("id_notificacion"));
        n.setIdAlerta(rs.getInt("id_alerta"));
        n.setIdUsuario(rs.getInt("id_usuario"));
        n.setMensaje(rs.getString("mensaje"));
        n.setLeida(rs.getBoolean("leida"));
        Timestamp f = rs.getTimestamp("fecha_envio");
        n.setFechaEnvio(f != null ? f.toLocalDateTime() : null);
        return n;
    };

    public NotificacionDaoImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<Notificacion> listarPorUsuario(int idUsuario) {
        return jdbc.query(
                "SELECT id_notificacion, id_alerta, id_usuario, mensaje, leida, fecha_envio " +
                "FROM notificacion WHERE id_usuario = ? ORDER BY fecha_envio DESC",
                mapper, idUsuario);
    }

    @Override
    public int contarNoLeidas(int idUsuario) {
        Integer total = jdbc.queryForObject(
                "SELECT COUNT(*) FROM notificacion WHERE id_usuario = ? AND leida = FALSE",
                Integer.class, idUsuario);
        return total != null ? total : 0;
    }

    @Override
    public void insertar(Notificacion notificacion) {
        jdbc.update(
                "INSERT INTO notificacion (id_alerta, id_usuario, mensaje) VALUES (?, ?, ?)",
                notificacion.getIdAlerta(), notificacion.getIdUsuario(), notificacion.getMensaje());
    }

    @Override
    public boolean marcarLeida(int idNotificacion) {
        int filas = jdbc.update(
                "UPDATE notificacion SET leida = TRUE WHERE id_notificacion = ?", idNotificacion);
        return filas > 0;
    }
}
