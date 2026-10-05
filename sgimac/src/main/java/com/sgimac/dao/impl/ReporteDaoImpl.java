package com.sgimac.dao.impl;

import com.sgimac.dao.ReporteDao;
import com.sgimac.model.Reporte;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;

/**
 * Implementacion JDBC del DAO de reportes.
 */
@Repository
public class ReporteDaoImpl implements ReporteDao {

    private final JdbcTemplate jdbc;

    private static final String SELECT_BASE =
            "SELECT r.id_reporte, r.id_usuario, u.nombres AS nombre_usuario, " +
            "       r.tipo, r.formato, r.fecha_generacion " +
            "FROM reporte r " +
            "JOIN usuario u ON u.id_usuario = r.id_usuario ";

    private final RowMapper<Reporte> mapper = (rs, fila) -> {
        Reporte r = new Reporte();
        r.setIdReporte(rs.getInt("id_reporte"));
        r.setIdUsuario(rs.getInt("id_usuario"));
        r.setNombreUsuario(rs.getString("nombre_usuario"));
        r.setTipo(rs.getString("tipo"));
        r.setFormato(rs.getString("formato"));
        Timestamp f = rs.getTimestamp("fecha_generacion");
        r.setFechaGeneracion(f != null ? f.toLocalDateTime() : null);
        return r;
    };

    public ReporteDaoImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<Reporte> listar() {
        return jdbc.query(SELECT_BASE + "ORDER BY r.fecha_generacion DESC", mapper);
    }

    @Override
    public int insertar(Reporte reporte) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO reporte (id_usuario, tipo, formato) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, reporte.getIdUsuario());
            ps.setString(2, reporte.getTipo());
            ps.setString(3, reporte.getFormato());
            return ps;
        }, keyHolder);
        return keyHolder.getKey().intValue();
    }
}
