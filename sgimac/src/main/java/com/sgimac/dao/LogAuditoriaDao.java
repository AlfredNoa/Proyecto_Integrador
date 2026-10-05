package com.sgimac.dao;

import com.sgimac.model.LogAuditoria;

import java.util.List;

/**
 * Patron DAO: contrato de acceso a datos para la tabla "log_auditoria".
 */
public interface LogAuditoriaDao {

    List<LogAuditoria> listar();

    void registrar(int idUsuario, String accion, String entidad);
}
