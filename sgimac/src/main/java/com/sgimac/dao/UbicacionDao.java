package com.sgimac.dao;

import com.sgimac.model.Ubicacion;

import java.util.List;
import java.util.Optional;

/**
 * Patrón DAO: contrato de acceso a datos para la tabla {@code ubicacion}. Los controladores
 * dependen de esta interfaz y nunca de su implementación JDBC.
 *
 * @author Equipo SGIMAC
 * @version 1.0
 */
public interface UbicacionDao {

    /**
     * Lista todas las ubicaciones ordenadas por nombre.
     *
     * @return lista de ubicaciones
     */
    List<Ubicacion> listar();

    /**
     * Busca una ubicación por su identificador.
     *
     * @param idUbicacion identificador de la ubicación
     * @return la ubicación, o un {@code Optional} vacío si no existe
     */
    Optional<Ubicacion> buscarPorId(int idUbicacion);

    /**
     * Registra una nueva ubicación.
     *
     * @param ubicacion ubicación a registrar
     * @return identificador generado para la nueva ubicación
     */
    int insertar(Ubicacion ubicacion);

    /**
     * Actualiza el nombre y el tipo de una ubicación.
     *
     * @param ubicacion ubicación con los datos nuevos; se identifica por su {@code idUbicacion}
     * @return {@code true} si se actualizó algún registro
     */
    boolean actualizar(Ubicacion ubicacion);
}
