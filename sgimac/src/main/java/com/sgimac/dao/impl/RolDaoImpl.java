package com.sgimac.dao.impl;

import com.sgimac.dao.RolDao;
import com.sgimac.model.Rol;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Implementación JDBC de {@link RolDao} para la tabla {@code rol}, basada en
 * {@link org.springframework.jdbc.core.JdbcTemplate}.
 *
 * @author Equipo SGIMAC
 * @version 1.0
 */
@Repository
public class RolDaoImpl implements RolDao {

    /**
     * Plantilla JDBC de Spring con la que se ejecutan las consultas SQL (inyectada por el
     * constructor).
     */
    private final JdbcTemplate jdbc;

    /** Convierte cada fila del {@code ResultSet} en un objeto del modelo. */
    private final RowMapper<Rol> mapper = (rs, fila) -> new Rol(
            rs.getInt("id_rol"),
            rs.getString("nombre"),
            rs.getString("descripcion"));

    /**
     * Crea el DAO con la plantilla JDBC que Spring inyecta.
     *
     * @param jdbc plantilla JDBC configurada con el {@code DataSource} de MySQL
     */
    public RolDaoImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /** {@inheritDoc} */
    @Override
    public List<Rol> listar() {
        return jdbc.query(
                "SELECT id_rol, nombre, descripcion FROM rol ORDER BY id_rol",
                mapper);
    }
}
