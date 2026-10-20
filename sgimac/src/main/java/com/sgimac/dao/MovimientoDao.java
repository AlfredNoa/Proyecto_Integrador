package com.sgimac.dao;

import com.sgimac.model.Movimiento;

import java.util.List;

/**
 * Patrón DAO: contrato de acceso a datos para la tabla {@code movimiento}. Los controladores
 * dependen de esta interfaz y nunca de su implementación JDBC.
 *
 * @author Equipo SGIMAC
 * @version 1.0
 */
public interface MovimientoDao {

    /**
     * Lista todos los movimientos, del más reciente al más antiguo, con el código del lote, el
     * insumo y el usuario.
     *
     * @return lista de movimientos
     */
    List<Movimiento> listar();

    /**
     * Registra un movimiento (entrada o salida). Solo inserta el registro: la validación y la
     * actualización del stock del lote las hace el controlador.
     *
     * @param movimiento movimiento a registrar
     */
    void insertar(Movimiento movimiento);
}
