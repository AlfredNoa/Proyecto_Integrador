package com.sgimac.dao;

import com.sgimac.model.Rol;

import java.util.List;

/**
 * Patron DAO: contrato de acceso a datos para la tabla "rol".
 */
public interface RolDao {

    List<Rol> listar();
}
