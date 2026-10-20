package com.sgimac.dao;

import com.sgimac.model.Reporte;

import java.util.List;

/**
 * Patrón DAO: contrato de acceso a datos para la tabla {@code reporte}. Los controladores dependen
 * de esta interfaz y nunca de su implementación JDBC.
 *
 * @author Equipo SGIMAC
 * @version 1.0
 */
public interface ReporteDao {

    /**
     * Lista el historial de reportes generados, del más reciente al más antiguo, con el nombre de
     * quien los generó.
     *
     * @return lista de reportes
     */
    List<Reporte> listar();

    /**
     * Registra la generación de un reporte.
     *
     * @param reporte reporte a registrar
     * @return identificador generado para el nuevo registro
     */
    int insertar(Reporte reporte);
}
