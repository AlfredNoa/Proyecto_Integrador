package com.sgimac.dao;

import com.sgimac.model.Categoria;

import java.util.List;
import java.util.Optional;

/**
 * Patrón DAO: contrato de acceso a datos para la tabla {@code categoria}. Los controladores
 * dependen de esta interfaz y nunca de su implementación JDBC.
 *
 * @author Equipo SGIMAC
 * @version 1.0
 */
public interface CategoriaDao {

    /**
     * Lista todas las categorías ordenadas por nombre.
     *
     * @return lista de categorías
     */
    List<Categoria> listar();

    /**
     * Busca una categoría por su identificador.
     *
     * @param idCategoria identificador de la categoría
     * @return la categoría, o un {@code Optional} vacío si no existe
     */
    Optional<Categoria> buscarPorId(int idCategoria);

    /**
     * Registra una nueva categoría.
     *
     * @param categoria categoría a registrar
     * @return identificador generado para la nueva categoría
     */
    int insertar(Categoria categoria);

    /**
     * Actualiza el nombre y la descripción de una categoría.
     *
     * @param categoria categoría con los datos nuevos; se identifica por su {@code idCategoria}
     * @return {@code true} si se actualizó algún registro
     */
    boolean actualizar(Categoria categoria);

    /**
     * Elimina una categoría. Si hay insumos que la usan (llave foránea) no se elimina.
     *
     * @param idCategoria identificador de la categoría
     * @return {@code true} si se eliminó; {@code false} si no existe o tiene insumos asociados
     */
    boolean eliminar(int idCategoria);
}
