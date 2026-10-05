package com.sgimac.dao.impl;

import com.sgimac.dao.MovimientoDao;
import com.sgimac.model.Movimiento;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

/**
 * Implementacion JDBC del DAO de movimientos.
 * Solo inserta el registro; la logica de validar caducidad y stock,
 * y de actualizar la cantidad del lote, vive en el Controlador
 * (movimiento.MovimientoController), como muestra el diagrama secuencial 6.7.
 */
@Repository
public class MovimientoDaoImpl implements MovimientoDao {

    private final JdbcTemplate jdbc;

    private static final String SELECT_BASE =
            "SELECT m.id_movimiento, m.id_lote, l.codigo_qr, i.nombre AS nombre_insumo, " +
            "       m.id_usuario, u.nombres AS nombre_usuario, m.tipo, m.cantidad, m.fecha " +
            "FROM movimiento m " +
            "JOIN lote l ON l.id_lote = m.id_lote " +
            "JOIN insumo i ON i.id_insumo = l.id_insumo " +
            "JOIN usuario u ON u.id_usuario = m.id_usuario ";

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

    public MovimientoDaoImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<Movimiento> listar() {
        return jdbc.query(SELECT_BASE + "ORDER BY m.fecha DESC", mapper);
    }

    @Override
    public void insertar(Movimiento movimiento) {
        jdbc.update(
                "INSERT INTO movimiento (id_lote, id_usuario, tipo, cantidad) VALUES (?, ?, ?, ?)",
                movimiento.getIdLote(), movimiento.getIdUsuario(),
                movimiento.getTipo(), movimiento.getCantidad());
    }
}
