package com.sgimac.dao.impl;

import com.sgimac.dao.LoteDao;
import com.sgimac.model.Lote;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

/**
 * Implementacion JDBC del DAO de lotes.
 */
@Repository
public class LoteDaoImpl implements LoteDao {

    private final JdbcTemplate jdbc;

    private static final String SELECT_BASE =
            "SELECT l.id_lote, l.id_insumo, i.nombre AS nombre_insumo, i.unidad_medida, " +
            "       l.id_proveedor, p.razon_social, " +
            "       l.id_ubicacion, u.nombre AS nombre_ubicacion, " +
            "       l.codigo_qr, l.fecha_vencimiento, l.cantidad_actual " +
            "FROM lote l " +
            "JOIN insumo i ON i.id_insumo = l.id_insumo " +
            "JOIN proveedor p ON p.id_proveedor = l.id_proveedor " +
            "JOIN ubicacion u ON u.id_ubicacion = l.id_ubicacion ";

    private final RowMapper<Lote> mapper = (rs, fila) -> {
        Lote l = new Lote();
        l.setIdLote(rs.getInt("id_lote"));
        l.setIdInsumo(rs.getInt("id_insumo"));
        l.setNombreInsumo(rs.getString("nombre_insumo"));
        l.setUnidadMedida(rs.getString("unidad_medida"));
        l.setIdProveedor(rs.getInt("id_proveedor"));
        l.setRazonSocialProveedor(rs.getString("razon_social"));
        l.setIdUbicacion(rs.getInt("id_ubicacion"));
        l.setNombreUbicacion(rs.getString("nombre_ubicacion"));
        l.setCodigoQr(rs.getString("codigo_qr"));
        Date fv = rs.getDate("fecha_vencimiento");
        l.setFechaVencimiento(fv != null ? fv.toLocalDate() : null);
        l.setCantidadActual(rs.getInt("cantidad_actual"));
        return l;
    };

    public LoteDaoImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<Lote> listar() {
        return jdbc.query(SELECT_BASE + "ORDER BY l.fecha_vencimiento", mapper);
    }

    @Override
    public List<Lote> listarProximosAVencer(int dias) {
        return jdbc.query(
                SELECT_BASE +
                "WHERE l.fecha_vencimiento <= DATE_ADD(CURDATE(), INTERVAL ? DAY) " +
                "ORDER BY l.fecha_vencimiento",
                mapper, dias);
    }

    @Override
    public Optional<Lote> buscarPorId(int idLote) {
        List<Lote> resultado = jdbc.query(SELECT_BASE + "WHERE l.id_lote = ?", mapper, idLote);
        return resultado.stream().findFirst();
    }

    @Override
    public Optional<Lote> buscarPorCodigoQr(String codigoQr) {
        List<Lote> resultado = jdbc.query(SELECT_BASE + "WHERE l.codigo_qr = ?", mapper, codigoQr);
        return resultado.stream().findFirst();
    }

    @Override
    public int insertar(Lote lote) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO lote (id_insumo, id_proveedor, id_ubicacion, codigo_qr, " +
                    "fecha_vencimiento, cantidad_actual) VALUES (?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, lote.getIdInsumo());
            ps.setInt(2, lote.getIdProveedor());
            ps.setInt(3, lote.getIdUbicacion());
            ps.setString(4, lote.getCodigoQr());
            ps.setDate(5, Date.valueOf(lote.getFechaVencimiento()));
            ps.setInt(6, lote.getCantidadActual());
            return ps;
        }, keyHolder);
        return keyHolder.getKey().intValue();
    }

    @Override
    public boolean ajustarCantidad(int idLote, int delta) {
        // La condicion "cantidad_actual + ? >= 0" evita quedar en negativo
        // aunque dos solicitudes lleguen casi al mismo tiempo (concurrencia).
        int filas = jdbc.update(
                "UPDATE lote SET cantidad_actual = cantidad_actual + ? " +
                "WHERE id_lote = ? AND cantidad_actual + ? >= 0",
                delta, idLote, delta);
        return filas > 0;
    }

    @Override
    public int sumarStockVigente(int idInsumo) {
        Integer total = jdbc.queryForObject(
                "SELECT COALESCE(SUM(cantidad_actual), 0) FROM lote " +
                "WHERE id_insumo = ? AND fecha_vencimiento >= CURDATE()",
                Integer.class, idInsumo);
        return total != null ? total : 0;
    }
}
