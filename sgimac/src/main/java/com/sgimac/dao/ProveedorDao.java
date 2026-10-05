package com.sgimac.dao;

import com.sgimac.model.Proveedor;

import java.util.List;
import java.util.Optional;

/**
 * Patron DAO: contrato de acceso a datos para la tabla "proveedor".
 */
public interface ProveedorDao {

    List<Proveedor> listar();

    Optional<Proveedor> buscarPorId(int idProveedor);

    int insertar(Proveedor proveedor);

    boolean actualizar(Proveedor proveedor);
}
