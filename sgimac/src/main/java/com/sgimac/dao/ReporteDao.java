package com.sgimac.dao;

import com.sgimac.model.Reporte;

import java.util.List;

/**
 * Patron DAO: contrato de acceso a datos para la tabla "reporte".
 */
public interface ReporteDao {

    List<Reporte> listar();

    int insertar(Reporte reporte);
}
