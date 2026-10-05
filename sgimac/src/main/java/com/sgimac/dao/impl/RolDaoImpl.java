package com.sgimac.dao.impl;

import com.sgimac.dao.RolDao;
import com.sgimac.model.Rol;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Implementacion JDBC del DAO de roles.
 */
@Repository
public class RolDaoImpl implements RolDao {

    private final JdbcTemplate jdbc;

    private final RowMapper<Rol> mapper = (rs, fila) -> new Rol(
            rs.getInt("id_rol"),
            rs.getString("nombre"),
            rs.getString("descripcion"));

    public RolDaoImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<Rol> listar() {
        return jdbc.query(
                "SELECT id_rol, nombre, descripcion FROM rol ORDER BY id_rol",
                mapper);
    }
}
