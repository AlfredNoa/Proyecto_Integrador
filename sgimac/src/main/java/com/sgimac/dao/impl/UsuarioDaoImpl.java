package com.sgimac.dao.impl;

import com.sgimac.dao.UsuarioDao;
import com.sgimac.model.Usuario;
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
 * Implementacion JDBC del DAO de usuarios, usando JdbcTemplate.
 */
@Repository
public class UsuarioDaoImpl implements UsuarioDao {

    private final JdbcTemplate jdbc;

    // Trae tambien el nombre del rol mediante JOIN, para no depender de otro DAO.
    private static final String SELECT_BASE =
            "SELECT u.id_usuario, u.id_rol, r.nombre AS nombre_rol, u.nombres, " +
            "       u.correo, u.contrasena_hash, u.activo " +
            "FROM usuario u " +
            "JOIN rol r ON r.id_rol = u.id_rol ";

    private final RowMapper<Usuario> mapper = (rs, fila) -> {
        Usuario u = new Usuario();
        u.setIdUsuario(rs.getInt("id_usuario"));
        u.setIdRol(rs.getInt("id_rol"));
        u.setNombreRol(rs.getString("nombre_rol"));
        u.setNombres(rs.getString("nombres"));
        u.setCorreo(rs.getString("correo"));
        u.setContrasenaHash(rs.getString("contrasena_hash"));
        u.setActivo(rs.getBoolean("activo"));
        return u;
    };

    public UsuarioDaoImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<Usuario> listar() {
        return jdbc.query(SELECT_BASE + "ORDER BY u.nombres", mapper);
    }

    @Override
    public Optional<Usuario> buscarPorId(int idUsuario) {
        List<Usuario> resultado = jdbc.query(
                SELECT_BASE + "WHERE u.id_usuario = ?", mapper, idUsuario);
        return resultado.stream().findFirst();
    }

    @Override
    public Optional<Usuario> buscarPorCorreo(String correo) {
        List<Usuario> resultado = jdbc.query(
                SELECT_BASE + "WHERE u.correo = ?", mapper, correo);
        return resultado.stream().findFirst();
    }

    @Override
    public int insertar(Usuario usuario) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO usuario (id_rol, nombres, correo, contrasena_hash, activo) " +
                    "VALUES (?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, usuario.getIdRol());
            ps.setString(2, usuario.getNombres());
            ps.setString(3, usuario.getCorreo());
            ps.setString(4, usuario.getContrasenaHash());
            ps.setBoolean(5, usuario.isActivo());
            return ps;
        }, keyHolder);
        return keyHolder.getKey().intValue();
    }

    @Override
    public boolean actualizar(Usuario usuario) {
        int filas = jdbc.update(
                "UPDATE usuario SET id_rol = ?, nombres = ?, correo = ? WHERE id_usuario = ?",
                usuario.getIdRol(), usuario.getNombres(), usuario.getCorreo(),
                usuario.getIdUsuario());
        return filas > 0;
    }

    @Override
    public boolean cambiarEstado(int idUsuario, boolean activo) {
        int filas = jdbc.update(
                "UPDATE usuario SET activo = ? WHERE id_usuario = ?",
                activo, idUsuario);
        return filas > 0;
    }

    @Override
    public List<Usuario> listarPorRol(String nombreRol) {
        return jdbc.query(
                SELECT_BASE + "WHERE r.nombre = ? AND u.activo = TRUE",
                mapper, nombreRol);
    }
}
