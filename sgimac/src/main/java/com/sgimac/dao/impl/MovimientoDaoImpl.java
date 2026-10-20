package com.sgimac.dao.impl;

import com.sgimac.dao.MovimientoDao;
import com.sgimac.model.Movimiento;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

/**
 * Implementación JDBC de {@link MovimientoDao} para la tabla {@code movimiento}, basada en
 * {@link org.springframework.jdbc.core.JdbcTemplate}.
 *
 * <p>Solo inserta el registro; la lógica de validar caducidad y stock, y de actualizar la cantidad del
 * lote, vive en {@code MovimientoController} (diagrama secuencial 6.7).
 *
 * @author Equipo SGIMAC
 * @version 1.0
 */
@Repository
public class MovimientoDaoImpl implements MovimientoDao {

    /**
     * Plantilla JDBC de Spring con la que se ejecutan las consultas SQL (inyectada por el
     * constructor).
     */
    private final JdbcTemplate jdbc;

    /** Consulta base: une {@code movimiento} con {@code lote}, {@code insumo} y {@code usuario}. */
    private static final String SELECT_BASE =
            "SELECT m.id_movimiento, m.id_lote, l.codigo_qr, i.nombre AS nombre_insumo, " +
            "       m.id_usuario, u.nombres AS nombre_usuario, m.tipo, m.cantidad, m.fecha " +
            "FROM movimiento m " +
            "JOIN lote l ON l.id_lote = m.id_lote " +
            "JOIN insumo i ON i.id_insumo = l.id_insumo " +
            "JOIN usuario u ON u.id_usuario = m.id_usuario ";

    /** Convierte cada fila del {@code ResultSet} en un objeto del modelo. */
    private final RowMapper<Movimiento> mapper = (rs, fila) -> {
        Movimiento m = new Movimiento();
        m.setIdMovimiento(rs.getInt("id_movimiento"));
        m.setIdLote(rs.getInt("id_lote"));
        m.setCodigoQrLote(rs.getString("codigo_qr"));
        m.setNombreInsumo(rs.getString("nombre_insumo"));
        m.setIdUsuario(rs.getInt("id_usuario"));
        m.setNombreUsuario(rs.getString("nombre_usuario"));
        m.setTipo(rs.getString("tipo"));
        m.setCantidad(rs.getInt("cantidad"));
        Timestamp f = rs.getTimestamp("fecha");
        m.setFecha(f != null ? f.toLocalDateTime() : null);
        return m;
    };

    /**
     * Crea el DAO con la plantilla JDBC que Spring inyecta.
     *
     * @param jdbc plantilla JDBC configurada con el {@code DataSource} de MySQL
     */
    public MovimientoDaoImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /** {@inheritDoc} */
    @Override
    public List<Movimiento> listar() {
        return jdbc.query(SELECT_BASE + "ORDER BY m.fecha DESC", mapper);
    }

    /** {@inheritDoc} */
    @Override
    public void insertar(Movimiento movimiento) {
        jdbc.update(
                "INSERT INTO movimiento (id_lote, id_usuario, tipo, cantidad) VALUES (?, ?, ?, ?)",
                movimiento.getIdLote(), movimiento.getIdUsuario(),
                movimiento.getTipo(), movimiento.getCantidad());
    }
}
