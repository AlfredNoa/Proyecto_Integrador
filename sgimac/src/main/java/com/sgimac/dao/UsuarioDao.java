package com.sgimac.dao;

import com.sgimac.model.Usuario;

import java.util.List;
import java.util.Optional;

/**
 * Patron DAO: contrato de acceso a datos para la tabla "usuario".
 * Las clases superiores (Controlador) dependen de esta interfaz,
 * nunca de la implementacion JDBC directamente.
 */
public interface UsuarioDao {

    List<Usuario> listar();

    Optional<Usuario> buscarPorId(int idUsuario);

    Optional<Usuario> buscarPorCorreo(String correo);

    int insertar(Usuario usuario);

    boolean actualizar(Usuario usuario);

    boolean cambiarEstado(int idUsuario, boolean activo);

    /** Usuarios activos que tienen el rol indicado (ej. "Personal Médico"), para notificarles alertas. */
    List<Usuario> listarPorRol(String nombreRol);
}
