package com.sgimac.dao;

import com.sgimac.model.Movimiento;

import java.util.List;

/**
 * Patron DAO: contrato de acceso a datos para la tabla "movimiento".
 */
public interface MovimientoDao {

    List<Movimiento> listar();

    void insertar(Movimiento movimiento);
}
