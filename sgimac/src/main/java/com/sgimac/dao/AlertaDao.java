package com.sgimac.dao;

import com.sgimac.model.Alerta;

import java.util.List;

/**
 * Patron DAO: contrato de acceso a datos para la tabla "alerta".
 */
public interface AlertaDao {

    List<Alerta> listarPendientes();

    List<Alerta> listar();

    /** Evita duplicar alertas: true si ya existe una PENDIENTE del mismo tipo para ese lote. */
    boolean existePendiente(int idLote, String tipo);

    int insertar(Alerta alerta);

    boolean marcarAtendida(int idAlerta, int idUsuarioAtiende);
}
