package com.sgimac.dao;

import com.sgimac.model.Rol;

import java.util.List;

/**
 * Patrón DAO: contrato de acceso a datos para la tabla {@code rol}. Los controladores dependen de
 * esta interfaz y nunca de su implementación JDBC.
 *
 * @author Equipo SGIMAC
 * @version 1.0
 */
public interface RolDao {

    /**
     * Lista todos los roles ordenados por su identificador.
     *
     * @return lista de roles (vacía si no hay ninguno)
     */
    List<Rol> listar();
}
