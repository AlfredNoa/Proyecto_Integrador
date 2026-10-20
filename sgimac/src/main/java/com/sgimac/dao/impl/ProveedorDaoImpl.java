package com.sgimac.dao.impl;

import com.sgimac.dao.ProveedorDao;
import com.sgimac.model.Proveedor;
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
 * Implementación JDBC de {@link ProveedorDao} para la tabla {@code proveedor}, basada en
 * {@link org.springframework.jdbc.core.JdbcTemplate}.
 *
 * @author Equipo SGIMAC
 * @version 1.0
 */
@Repository
public class ProveedorDaoImpl implements ProveedorDao {

    /**
     * Plantilla JDBC de Spring con la que se ejecutan las consultas SQL (inyectada por el
     * constructor).
     */
    private final JdbcTemplate jdbc;

    /** Convierte cada fila del {@code ResultSet} en un objeto del modelo. */
    private final RowMapper<Proveedor> mapper = (rs, fila) -> {
        Proveedor p = new Proveedor();
        p.setIdProveedor(rs.getInt("id_proveedor"));
        p.setRazonSocial(rs.getString("razon_social"));
        p.setRuc(rs.getString("ruc"));
        p.setTelefono(rs.getString("telefono"));
        return p;
    };

    /**
     * Crea el DAO con la plantilla JDBC que Spring inyecta.
     *
     * @param jdbc plantilla JDBC configurada con el {@code DataSource} de MySQL
     */
    public ProveedorDaoImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /** {@inheritDoc} */
    @Override
    public List<Proveedor> listar() {
        return jdbc.query(
                "SELECT id_proveedor, razon_social, ruc, telefono FROM proveedor ORDER BY razon_social",
                mapper);
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Proveedor> buscarPorId(int idProveedor) {
        List<Proveedor> resultado = jdbc.query(
                "SELECT id_proveedor, razon_social, ruc, telefono FROM proveedor WHERE id_proveedor = ?",
                mapper, idProveedor);
        return resultado.stream().findFirst();
    }

    /** {@inheritDoc} */
    @Override
    public int insertar(Proveedor proveedor) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO proveedor (razon_social, ruc, telefono) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, proveedor.getRazonSocial());
            ps.setString(2, proveedor.getRuc());
            ps.setString(3, proveedor.getTelefono());
            return ps;
        }, keyHolder);
        return keyHolder.getKey().intValue();
    }

    /** {@inheritDoc} */
    @Override
    public boolean actualizar(Proveedor proveedor) {
        int filas = jdbc.update(
                "UPDATE proveedor SET razon_social = ?, ruc = ?, telefono = ? WHERE id_proveedor = ?",
                proveedor.getRazonSocial(), proveedor.getRuc(), proveedor.getTelefono(),
                proveedor.getIdProveedor());
        return filas > 0;
    }
}
