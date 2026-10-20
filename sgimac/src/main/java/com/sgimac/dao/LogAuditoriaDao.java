package com.sgimac.dao;

import com.sgimac.model.LogAuditoria;

import java.util.List;

/**
 * Patrón DAO: contrato de acceso a datos para la tabla {@code log_auditoria}. Los controladores
 * dependen de esta interfaz y nunca de su implementación JDBC.
 *
 * @author Equipo SGIMAC
 * @version 1.0
 */
public interface LogAuditoriaDao {

    /**
     * Lista el historial de auditoría, del más reciente al más antiguo, con el nombre del usuario.
     *
     * @return lista de registros de auditoría
     */
    List<LogAuditoria> listar();

    /**
     * Registra una acción en el historial de auditoría. La fecha la asigna la base de datos.
     *
     * @param idUsuario identificador del usuario que realizó la acción
     * @param accion acción realizada, por ejemplo «Inicio de sesión»
     * @param entidad entidad o recurso afectado
     */
    void registrar(int idUsuario, String accion, String entidad);
}
