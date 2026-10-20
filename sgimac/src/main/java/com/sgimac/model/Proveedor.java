package com.sgimac.model;

/**
 * Modelo (MVC) que representa la tabla {@code proveedor} de la base de datos {@code sgimac}. Es la
 * empresa que abastece los lotes de insumos.
 *
 * @author Equipo SGIMAC
 * @version 1.0
 */
public class Proveedor {

    /** El identificador del proveedor. Clave primaria autoincremental. */
    private int idProveedor;
    /** La razón social del proveedor. */
    private String razonSocial;
    /** El RUC del proveedor. Tiene 11 caracteres y es único en la tabla. */
    private String ruc;
    /** El teléfono de contacto del proveedor. */
    private String telefono;

    /**
     * Crea un objeto vacío; los atributos se completan luego con los métodos <i>set</i> o desde el
     * {@code RowMapper} del DAO.
     */
    public Proveedor() {
    }

    /**
     * Devuelve el identificador del proveedor.
     *
     * @return el identificador del proveedor
     */
    public int getIdProveedor() {
        return idProveedor;
    }

    /**
     * Establece el identificador del proveedor.
     *
     * @param idProveedor el identificador del proveedor
     */
    public void setIdProveedor(int idProveedor) {
        this.idProveedor = idProveedor;
    }

    /**
     * Devuelve la razón social del proveedor.
     *
     * @return la razón social del proveedor
     */
    public String getRazonSocial() {
        return razonSocial;
    }

    /**
     * Establece la razón social del proveedor.
     *
     * @param razonSocial la razón social del proveedor
     */
    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    /**
     * Devuelve el RUC del proveedor.
     *
     * @return el RUC del proveedor
     */
    public String getRuc() {
        return ruc;
    }

    /**
     * Establece el RUC del proveedor.
     *
     * @param ruc el RUC del proveedor
     */
    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    /**
     * Devuelve el teléfono de contacto del proveedor.
     *
     * @return el teléfono de contacto del proveedor
     */
    public String getTelefono() {
        return telefono;
    }

    /**
     * Establece el teléfono de contacto del proveedor.
     *
     * @param telefono el teléfono de contacto del proveedor
     */
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}
