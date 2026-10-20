package com.sgimac.dao;

import com.sgimac.model.Proveedor;

import java.util.List;
import java.util.Optional;

/**
 * Patrón DAO: contrato de acceso a datos para la tabla {@code proveedor}. Los controladores
 * dependen de esta interfaz y nunca de su implementación JDBC.
 *
 * @author Equipo SGIMAC
 * @version 1.0
 */
public interface ProveedorDao {

    /**
     * Lista todos los proveedores ordenados por razón social.
     *
     * @return lista de proveedores
     */
    List<Proveedor> listar();

    /**
     * Busca un proveedor por su identificador.
     *
     * @param idProveedor identificador del proveedor
     * @return el proveedor, o un {@code Optional} vacío si no existe
     */
    Optional<Proveedor> buscarPorId(int idProveedor);

    /**
     * Registra un nuevo proveedor.
     *
     * @param proveedor proveedor a registrar
     * @return identificador generado para el nuevo proveedor
     */
    int insertar(Proveedor proveedor);

    /**
     * Actualiza la razón social, el RUC y el teléfono de un proveedor.
     *
     * @param proveedor proveedor con los datos nuevos; se identifica por su {@code idProveedor}
     * @return {@code true} si se actualizó algún registro
     */
    boolean actualizar(Proveedor proveedor);
}
