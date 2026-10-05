package com.sgimac.dao;

import com.sgimac.model.Categoria;

import java.util.List;
import java.util.Optional;

/**
 * Patron DAO: contrato de acceso a datos para la tabla "categoria".
 */
public interface CategoriaDao {

    List<Categoria> listar();

    Optional<Categoria> buscarPorId(int idCategoria);

    int insertar(Categoria categoria);

    boolean actualizar(Categoria categoria);

    boolean eliminar(int idCategoria);
}
