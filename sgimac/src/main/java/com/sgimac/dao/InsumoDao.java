package com.sgimac.dao;

import com.sgimac.model.Insumo;

import java.util.List;
import java.util.Optional;

/**
 * Patrón DAO: contrato de acceso a datos para la tabla {@code insumo}. Los controladores dependen
 * de esta interfaz y nunca de su implementación JDBC.
 *
 * @author Equipo SGIMAC
 * @version 1.0
 */
public interface InsumoDao {

    /**
     * Lista todos los insumos ordenados por nombre, incluyendo el nombre de su categoría.
     *
     * @return lista de insumos
     */
    List<Insumo> listar();

    /**
     * Busca insumos cuyo nombre o código contenga el texto indicado.
     *
     * @param texto texto a buscar (coincidencia parcial)
     * @return insumos que coinciden, ordenados por nombre
     */
    List<Insumo> buscar(String texto);

    /**
     * Busca un insumo por su identificador.
     *
     * @param idInsumo identificador del insumo
     * @return el insumo, o un {@code Optional} vacío si no existe
     */
    Optional<Insumo> buscarPorId(int idInsumo);

    /**
     * Registra un nuevo insumo.
     *
     * @param insumo insumo a registrar
     * @return identificador generado para el nuevo insumo
     */
    int insertar(Insumo insumo);

    /**
     * Actualiza todos los datos propios de un insumo.
     *
     * @param insumo insumo con los datos nuevos; se identifica por su {@code idInsumo}
     * @return {@code true} si se actualizó algún registro
     */
    boolean actualizar(Insumo insumo);
}
