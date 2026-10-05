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
 * Implementacion JDBC del DAO de insumos.
 */
@Repository
public class InsumoDaoImpl implements InsumoDao {

    private final JdbcTemplate jdbc;

    private static final String SELECT_BASE =
            "SELECT i.id_insumo, i.id_categoria, c.nombre AS nombre_categoria, " +
            "       i.codigo, i.nombre, i.unidad_medida, i.stock_minimo " +
            "FROM insumo i " +
            "JOIN categoria c ON c.id_categoria = i.id_categoria ";

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

    public InsumoDaoImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<Insumo> listar() {
        return jdbc.query(SELECT_BASE + "ORDER BY i.nombre", mapper);
    }

    @Override
    public List<Insumo> buscar(String texto) {
        String patron = "%" + texto + "%";
        return jdbc.query(
                SELECT_BASE + "WHERE i.nombre LIKE ? OR i.codigo LIKE ? ORDER BY i.nombre",
                mapper, patron, patron);
    }

    @Override
    public Optional<Insumo> buscarPorId(int idInsumo) {
        List<Insumo> resultado = jdbc.query(
                SELECT_BASE + "WHERE i.id_insumo = ?", mapper, idInsumo);
        return resultado.stream().findFirst();
    }

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
