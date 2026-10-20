package com.sgimac.dao;

import com.sgimac.model.Alerta;

import java.util.List;

/**
 * Patrón DAO: contrato de acceso a datos para la tabla {@code alerta}. Los controladores dependen
 * de esta interfaz y nunca de su implementación JDBC.
 *
 * @author Equipo SGIMAC
 * @version 1.0
 */
public interface AlertaDao {

    /**
     * Lista las alertas pendientes, primero las de nivel más alto y, dentro de cada nivel, las más
     * recientes.
     *
     * @return lista de alertas pendientes
     */
    List<Alerta> listarPendientes();

    /**
     * Lista todas las alertas (pendientes y atendidas), de la más reciente a la más antigua.
     *
     * @return lista de alertas
     */
    List<Alerta> listar();

    /**
     * Evita duplicar alertas: indica si ya existe una alerta pendiente del mismo tipo para el lote.
     *
     * @param idLote identificador del lote
     * @param tipo tipo de alerta (caducidad o stock bajo)
     * @return {@code true} si ya hay una alerta pendiente de ese tipo para ese lote
     */
    boolean existePendiente(int idLote, String tipo);

    /**
     * Registra una nueva alerta en estado pendiente.
     *
     * @param alerta alerta a registrar
     * @return identificador generado para la nueva alerta
     */
    int insertar(Alerta alerta);

    /**
     * Marca una alerta como atendida y guarda quién la atendió.
     *
     * @param idAlerta identificador de la alerta
     * @param idUsuarioAtiende identificador del usuario que la atiende
     * @return {@code true} si se actualizó algún registro
     */
    boolean marcarAtendida(int idAlerta, int idUsuarioAtiende);
}
