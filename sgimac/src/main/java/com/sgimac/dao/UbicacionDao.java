package com.sgimac.dao;

import com.sgimac.model.Ubicacion;

import java.util.List;
import java.util.Optional;

/**
 * Patron DAO: contrato de acceso a datos para la tabla "ubicacion".
 */
public interface UbicacionDao {

    List<Ubicacion> listar();

    Optional<Ubicacion> buscarPorId(int idUbicacion);

    int insertar(Ubicacion ubicacion);

    boolean actualizar(Ubicacion ubicacion);
}
