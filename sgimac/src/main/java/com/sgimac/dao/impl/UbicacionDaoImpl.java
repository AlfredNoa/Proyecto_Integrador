package com.sgimac.dao.impl;

import com.sgimac.dao.UbicacionDao;
import com.sgimac.model.Ubicacion;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

/**
 * Implementacion JDBC del DAO de ubicaciones.
 */
@Repository
public class UbicacionDaoImpl implements UbicacionDao {

    private final JdbcTemplate jdbc;

    private final RowMapper<Ubicacion> mapper = (rs, fila) -> {
        Ubicacion u = new Ubicacion();
        u.setIdUbicacion(rs.getInt("id_ubicacion"));
        u.setNombre(rs.getString("nombre"));
        u.setTipo(rs.getString("tipo"));
        return u;
    };

    public UbicacionDaoImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<Ubicacion> listar() {
        return jdbc.query(
                "SELECT id_ubicacion, nombre, tipo FROM ubicacion ORDER BY nombre",
                mapper);
    }

    @Override
    public Optional<Ubicacion> buscarPorId(int idUbicacion) {
        List<Ubicacion> resultado = jdbc.query(
                "SELECT id_ubicacion, nombre, tipo FROM ubicacion WHERE id_ubicacion = ?",
                mapper, idUbicacion);
        return resultado.stream().findFirst();
    }

    @Override
    public int insertar(Ubicacion ubicacion) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO ubicacion (nombre, tipo) VALUES (?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, ubicacion.getNombre());
            ps.setString(2, ubicacion.getTipo());
            return ps;
        }, keyHolder);
        return keyHolder.getKey().intValue();
    }

    @Override
    public boolean actualizar(Ubicacion ubicacion) {
        int filas = jdbc.update(
                "UPDATE ubicacion SET nombre = ?, tipo = ? WHERE id_ubicacion = ?",
                ubicacion.getNombre(), ubicacion.getTipo(), ubicacion.getIdUbicacion());
        return filas > 0;
    }
}
