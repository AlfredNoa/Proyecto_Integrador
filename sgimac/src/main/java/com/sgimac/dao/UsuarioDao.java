package com.sgimac.dao;

import com.sgimac.model.Usuario;

import java.util.List;
import java.util.Optional;

/**
 * Patrón DAO: contrato de acceso a datos para la tabla {@code usuario}. Los controladores dependen
 * de esta interfaz y nunca de su implementación JDBC.
 *
 * @author Equipo SGIMAC
 * @version 1.0
 */
public interface UsuarioDao {

    /**
     * Lista todos los usuarios, ordenados por nombre, incluyendo el nombre de su rol.
     *
     * @return lista de usuarios
     */
    List<Usuario> listar();

    /**
     * Busca un usuario por su identificador.
     *
     * @param idUsuario identificador del usuario
     * @return el usuario, o un {@code Optional} vacío si no existe
     */
    Optional<Usuario> buscarPorId(int idUsuario);

    /**
     * Busca un usuario por su correo electrónico (se usa en el inicio de sesión).
     *
     * @param correo correo electrónico del usuario
     * @return el usuario, o un {@code Optional} vacío si no existe
     */
    Optional<Usuario> buscarPorCorreo(String correo);

    /**
     * Registra un nuevo usuario.
     *
     * @param usuario usuario a registrar (con la contraseña ya convertida en hash)
     * @return identificador generado para el nuevo usuario
     */
    int insertar(Usuario usuario);

    /**
     * Actualiza el rol, los nombres y el correo de un usuario. No modifica la contraseña ni el
     * estado.
     *
     * @param usuario usuario con los datos nuevos; se identifica por su {@code idUsuario}
     * @return {@code true} si se actualizó algún registro
     */
    boolean actualizar(Usuario usuario);

    /**
     * Activa o desactiva a un usuario.
     *
     * @param idUsuario identificador del usuario
     * @param activo {@code true} para activarlo, {@code false} para desactivarlo
     * @return {@code true} si se actualizó algún registro
     */
    boolean cambiarEstado(int idUsuario, boolean activo);

    /**
     * Lista los usuarios activos que tienen un rol determinado (por ejemplo, «Personal Médico»). Se
     * usa para saber a quién notificar una alerta.
     *
     * @param nombreRol nombre exacto del rol
     * @return usuarios activos con ese rol
     */
    List<Usuario> listarPorRol(String nombreRol);
}
