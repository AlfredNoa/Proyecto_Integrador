package com.sgimac.dao;

import com.sgimac.model.Insumo;

import java.util.List;
import java.util.Optional;

/**
 * Patron DAO: contrato de acceso a datos para la tabla "insumo".
 */
public interface InsumoDao {

    List<Insumo> listar();

    List<Insumo> buscar(String texto);

    Optional<Insumo> buscarPorId(int idInsumo);

    int insertar(Insumo insumo);

    boolean actualizar(Insumo insumo);
}
