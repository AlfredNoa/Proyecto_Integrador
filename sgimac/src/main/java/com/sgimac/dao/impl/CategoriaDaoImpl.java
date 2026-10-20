package com.sgimac.dao.impl;

import com.sgimac.dao.CategoriaDao;
import com.sgimac.model.Categoria;
import org.springframework.dao.DataIntegrityViolationException;
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
 * Implementación JDBC de {@link CategoriaDao} para la tabla {@code categoria}, basada en
 * {@link org.springframework.jdbc.core.JdbcTemplate}.
 *
 * @author Equipo SGIMAC
 * @version 1.0
 */
@Repository
public class CategoriaDaoImpl implements CategoriaDao {

    /**
     * Plantilla JDBC de Spring con la que se ejecutan las consultas SQL (inyectada por el
     * constructor).
     */
    private final JdbcTemplate jdbc;

    /** Convierte cada fila del {@code ResultSet} en un objeto del modelo. */
    private final RowMapper<Categoria> mapper = (rs, fila) -> new Categoria(
            rs.getInt("id_categoria"),
            rs.getString("nombre"),
            rs.getString("descripcion"));

    /**
     * Crea el DAO con la plantilla JDBC que Spring inyecta.
     *
     * @param jdbc plantilla JDBC configurada con el {@code DataSource} de MySQL
     */
    public CategoriaDaoImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /** {@inheritDoc} */
    @Override
    public List<Categoria> listar() {
        return jdbc.query(
                "SELECT id_categoria, nombre, descripcion FROM categoria ORDER BY nombre",
                mapper);
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Categoria> buscarPorId(int idCategoria) {
        List<Categoria> resultado = jdbc.query(
                "SELECT id_categoria, nombre, descripcion FROM categoria WHERE id_categoria = ?",
                mapper, idCategoria);
        return resultado.stream().findFirst();
    }

    /** {@inheritDoc} */
    @Override
    public int insertar(Categoria categoria) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO categoria (nombre, descripcion) VALUES (?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, categoria.getNombre());
            ps.setString(2, categoria.getDescripcion());
            return ps;
        }, keyHolder);
        return keyHolder.getKey().intValue();
    }

    /** {@inheritDoc} */
    @Override
    public boolean actualizar(Categoria categoria) {
        int filas = jdbc.update(
                "UPDATE categoria SET nombre = ?, descripcion = ? WHERE id_categoria = ?",
                categoria.getNombre(), categoria.getDescripcion(), categoria.getIdCategoria());
        return filas > 0;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Captura la {@code DataIntegrityViolationException} que lanza MySQL cuando la categoría está
     * referenciada por algún insumo y, en ese caso, devuelve {@code false} en lugar de propagar el
     * error.
     */
    @Override
    public boolean eliminar(int idCategoria) {
        try {
            int filas = jdbc.update("DELETE FROM categoria WHERE id_categoria = ?", idCategoria);
            return filas > 0;
        } catch (DataIntegrityViolationException e) {
            // Hay insumos que usan esta categoria (llave foranea): no se puede eliminar.
            return false;
        }
    }
}
