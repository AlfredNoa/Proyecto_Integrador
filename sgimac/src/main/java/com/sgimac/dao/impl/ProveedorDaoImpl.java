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
 * Implementacion JDBC del DAO de proveedores.
 */
@Repository
public class ProveedorDaoImpl implements ProveedorDao {

    private final JdbcTemplate jdbc;

    private final RowMapper<Proveedor> mapper = (rs, fila) -> {
        Proveedor p = new Proveedor();
        p.setIdProveedor(rs.getInt("id_proveedor"));
        p.setRazonSocial(rs.getString("razon_social"));
        p.setRuc(rs.getString("ruc"));
        p.setTelefono(rs.getString("telefono"));
        return p;
    };

    public ProveedorDaoImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<Proveedor> listar() {
        return jdbc.query(
                "SELECT id_proveedor, razon_social, ruc, telefono FROM proveedor ORDER BY razon_social",
                mapper);
    }

    @Override
    public Optional<Proveedor> buscarPorId(int idProveedor) {
        List<Proveedor> resultado = jdbc.query(
                "SELECT id_proveedor, razon_social, ruc, telefono FROM proveedor WHERE id_proveedor = ?",
                mapper, idProveedor);
        return resultado.stream().findFirst();
    }

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

    @Override
    public boolean actualizar(Proveedor proveedor) {
        int filas = jdbc.update(
                "UPDATE proveedor SET razon_social = ?, ruc = ?, telefono = ? WHERE id_proveedor = ?",
                proveedor.getRazonSocial(), proveedor.getRuc(), proveedor.getTelefono(),
                proveedor.getIdProveedor());
        return filas > 0;
    }
}
