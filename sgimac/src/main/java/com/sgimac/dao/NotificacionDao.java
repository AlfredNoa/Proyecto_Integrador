package com.sgimac.dao;

import com.sgimac.model.Notificacion;

import java.util.List;

/**
 * Patron DAO: contrato de acceso a datos para la tabla "notificacion".
 */
public interface NotificacionDao {

    List<Notificacion> listarPorUsuario(int idUsuario);

    int contarNoLeidas(int idUsuario);

    void insertar(Notificacion notificacion);

    boolean marcarLeida(int idNotificacion);
}
