package com.sgimac.dao;

import com.sgimac.model.Notificacion;

import java.util.List;

/**
 * Patrón DAO: contrato de acceso a datos para la tabla {@code notificacion}. Los controladores
 * dependen de esta interfaz y nunca de su implementación JDBC.
 *
 * @author Equipo SGIMAC
 * @version 1.0
 */
public interface NotificacionDao {

    /**
     * Lista las notificaciones de un usuario, de la más reciente a la más antigua.
     *
     * @param idUsuario identificador del usuario destinatario
     * @return notificaciones del usuario
     */
    List<Notificacion> listarPorUsuario(int idUsuario);

    /**
     * Cuenta las notificaciones que un usuario todavía no ha leído (se muestra como insignia en el
     * menú).
     *
     * @param idUsuario identificador del usuario destinatario
     * @return cantidad de notificaciones no leídas
     */
    int contarNoLeidas(int idUsuario);

    /**
     * Registra una nueva notificación (sin leer).
     *
     * @param notificacion notificación a registrar
     */
    void insertar(Notificacion notificacion);

    /**
     * Marca una notificación como leída.
     *
     * @param idNotificacion identificador de la notificación
     * @return {@code true} si se actualizó algún registro
     */
    boolean marcarLeida(int idNotificacion);
}
