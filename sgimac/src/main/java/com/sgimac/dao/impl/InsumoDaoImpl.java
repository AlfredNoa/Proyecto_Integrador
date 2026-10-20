package com.sgimac.dao.impl;

import com.sgimac.dao.InsumoDao;
import com.sgimac.model.Insumo;
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
 * Implementación JDBC de {@link InsumoDao} para la tabla {@code insumo}, basada en
 * {@link org.springframework.jdbc.core.JdbcTemplate}.
 *
 * <p>Cada consulta hace un {@code JOIN} con {@code categoria} para traer también su nombre.
 *
 * @author Equipo SGIMAC
 * @version 1.0
 */
@Repository
public class InsumoDaoImpl implements InsumoDao {

    /**
     * Plantilla JDBC de Spring con la que se ejecutan las consultas SQL (inyectada por el
     * constructor).
     */
    private final JdbcTemplate jdbc;

    /** Consulta base: une {@code insumo} con {@code categoria} para traer el nombre de la categoría. */
    private static final String SELECT_BASE =
            "SELECT i.id_insumo, i.id_categoria, c.nombre AS nombre_categoria, " +
            "       i.codigo, i.nombre, i.unidad_medida, i.stock_minimo " +
            "FROM insumo i " +
            "JOIN categoria c ON c.id_categoria = i.id_categoria ";

    /** Convierte cada fila del {@code ResultSet} en un objeto del modelo. */
    private final RowMapper<Insumo> mapper = (rs, fila) -> {
        Insumo i = new Insumo();
        i.setIdInsumo(rs.getInt("id_insumo"));
        i.setIdCategoria(rs.getInt("id_categoria"));
        i.setNombreCategoria(rs.getString("nombre_categoria"));
        i.setCodigo(rs.getString("codigo"));
        i.setNombre(rs.getString("nombre"));
        i.setUnidadMedida(rs.getString("unidad_medida"));
        i.setStockMinimo(rs.getInt("stock_minimo"));
        return i;
    };

    /**
     * Crea el DAO con la plantilla JDBC que Spring inyecta.
     *
     * @param jdbc plantilla JDBC configurada con el {@code DataSource} de MySQL
     */
    public InsumoDaoImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /** {@inheritDoc} */
    @Override
    public List<Insumo> listar() {
        return jdbc.query(SELECT_BASE + "ORDER BY i.nombre", mapper);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Usa {@code LIKE '%texto%'} sobre el nombre y el código, con parámetros enlazados (no
     * concatena el texto en el SQL).
     */
    @Override
    public List<Insumo> buscar(String texto) {
        String patron = "%" + texto + "%";
        return jdbc.query(
                SELECT_BASE + "WHERE i.nombre LIKE ? OR i.codigo LIKE ? ORDER BY i.nombre",
                mapper, patron, patron);
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Insumo> buscarPorId(int idInsumo) {
        List<Insumo> resultado = jdbc.query(
                SELECT_BASE + "WHERE i.id_insumo = ?", mapper, idInsumo);
        return resultado.stream().findFirst();
    }

    /** {@inheritDoc} */
    @Override
    public int insertar(Insumo insumo) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO insumo (id_categoria, codigo, nombre, unidad_medida, stock_minimo) " +
                    "VALUES (?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, insumo.getIdCategoria());
            ps.setString(2, insumo.getCodigo());
            ps.setString(3, insumo.getNombre());
            ps.setString(4, insumo.getUnidadMedida());
            ps.setInt(5, insumo.getStockMinimo());
            return ps;
        }, keyHolder);
        return keyHolder.getKey().intValue();
    }

    /** {@inheritDoc} */
    @Override
    public boolean actualizar(Insumo insumo) {
        int filas = jdbc.update(
                "UPDATE insumo SET id_categoria = ?, codigo = ?, nombre = ?, " +
                "unidad_medida = ?, stock_minimo = ? WHERE id_insumo = ?",
                insumo.getIdCategoria(), insumo.getCodigo(), insumo.getNombre(),
                insumo.getUnidadMedida(), insumo.getStockMinimo(), insumo.getIdInsumo());
        return filas > 0;
    }
}
