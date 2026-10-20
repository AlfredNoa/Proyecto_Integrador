package com.sgimac.model;

import java.time.LocalDate;

/**
 * Modelo (MVC) que representa la tabla {@code lote} de la base de datos {@code sgimac}. Es una
 * partida de un insumo con su propia fecha de vencimiento, cantidad y código QR.
 *
 * <p>Es la entidad central del sistema: los movimientos, las alertas de caducidad y las existencias se
 * controlan por lote.
 *
 * @author Equipo SGIMAC
 * @version 1.0
 */
public class Lote {

    /** El identificador del lote. Clave primaria autoincremental. */
    private int idLote;
    /** El identificador del insumo del lote. Clave foránea hacia {@code insumo}. */
    private int idInsumo;
    /** El nombre del insumo del lote. Se completa con un {@code JOIN}. */
    private String nombreInsumo;
    /** La unidad de medida del insumo del lote. Se completa con un {@code JOIN}. */
    private String unidadMedida;
    /** El identificador del proveedor del lote. Clave foránea hacia {@code proveedor}. */
    private int idProveedor;
    /** La razón social del proveedor del lote. Se completa con un {@code JOIN}. */
    private String razonSocialProveedor;
    /**
     * El identificador de la ubicación donde se almacena el lote. Clave foránea hacia
     * {@code ubicacion}.
     */
    private int idUbicacion;
    /** El nombre de la ubicación donde se almacena el lote. Se completa con un {@code JOIN}. */
    private String nombreUbicacion;
    /** El código QR que identifica al lote. Es único en la tabla. */
    private String codigoQr;
    /** La fecha de vencimiento del lote. */
    private LocalDate fechaVencimiento;
    /** La cantidad de unidades disponibles en el lote. No puede ser negativa. */
    private int cantidadActual;

    /**
     * Crea un objeto vacío; los atributos se completan luego con los métodos <i>set</i> o desde el
     * {@code RowMapper} del DAO.
     */
    public Lote() {
    }

    /**
     * Devuelve el identificador del lote.
     *
     * @return el identificador del lote
     */
    public int getIdLote() {
        return idLote;
    }

    /**
     * Establece el identificador del lote.
     *
     * @param idLote el identificador del lote
     */
    public void setIdLote(int idLote) {
        this.idLote = idLote;
    }

    /**
     * Devuelve el identificador del insumo del lote.
     *
     * @return el identificador del insumo del lote
     */
    public int getIdInsumo() {
        return idInsumo;
    }

    /**
     * Establece el identificador del insumo del lote.
     *
     * @param idInsumo el identificador del insumo del lote
     */
    public void setIdInsumo(int idInsumo) {
        this.idInsumo = idInsumo;
    }

    /**
     * Devuelve el nombre del insumo del lote.
     *
     * @return el nombre del insumo del lote
     */
    public String getNombreInsumo() {
        return nombreInsumo;
    }

    /**
     * Establece el nombre del insumo del lote.
     *
     * @param nombreInsumo el nombre del insumo del lote
     */
    public void setNombreInsumo(String nombreInsumo) {
        this.nombreInsumo = nombreInsumo;
    }

    /**
     * Devuelve la unidad de medida del insumo del lote.
     *
     * @return la unidad de medida del insumo del lote
     */
    public String getUnidadMedida() {
        return unidadMedida;
    }

    /**
     * Establece la unidad de medida del insumo del lote.
     *
     * @param unidadMedida la unidad de medida del insumo del lote
     */
    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    /**
     * Devuelve el identificador del proveedor del lote.
     *
     * @return el identificador del proveedor del lote
     */
    public int getIdProveedor() {
        return idProveedor;
    }

    /**
     * Establece el identificador del proveedor del lote.
     *
     * @param idProveedor el identificador del proveedor del lote
     */
    public void setIdProveedor(int idProveedor) {
        this.idProveedor = idProveedor;
    }

    /**
     * Devuelve la razón social del proveedor del lote.
     *
     * @return la razón social del proveedor del lote
     */
    public String getRazonSocialProveedor() {
        return razonSocialProveedor;
    }

    /**
     * Establece la razón social del proveedor del lote.
     *
     * @param razonSocialProveedor la razón social del proveedor del lote
     */
    public void setRazonSocialProveedor(String razonSocialProveedor) {
        this.razonSocialProveedor = razonSocialProveedor;
    }

    /**
     * Devuelve el identificador de la ubicación donde se almacena el lote.
     *
     * @return el identificador de la ubicación donde se almacena el lote
     */
    public int getIdUbicacion() {
        return idUbicacion;
    }

    /**
     * Establece el identificador de la ubicación donde se almacena el lote.
     *
     * @param idUbicacion el identificador de la ubicación donde se almacena el lote
     */
    public void setIdUbicacion(int idUbicacion) {
        this.idUbicacion = idUbicacion;
    }

    /**
     * Devuelve el nombre de la ubicación donde se almacena el lote.
     *
     * @return el nombre de la ubicación donde se almacena el lote
     */
    public String getNombreUbicacion() {
        return nombreUbicacion;
    }

    /**
     * Establece el nombre de la ubicación donde se almacena el lote.
     *
     * @param nombreUbicacion el nombre de la ubicación donde se almacena el lote
     */
    public void setNombreUbicacion(String nombreUbicacion) {
        this.nombreUbicacion = nombreUbicacion;
    }

    /**
     * Devuelve el código QR que identifica al lote.
     *
     * @return el código QR que identifica al lote
     */
    public String getCodigoQr() {
        return codigoQr;
    }

    /**
     * Establece el código QR que identifica al lote.
     *
     * @param codigoQr el código QR que identifica al lote
     */
    public void setCodigoQr(String codigoQr) {
        this.codigoQr = codigoQr;
    }

    /**
     * Devuelve la fecha de vencimiento del lote.
     *
     * @return la fecha de vencimiento del lote
     */
    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    /**
     * Establece la fecha de vencimiento del lote.
     *
     * @param fechaVencimiento la fecha de vencimiento del lote
     */
    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    /**
     * Devuelve la cantidad de unidades disponibles en el lote.
     *
     * @return la cantidad de unidades disponibles en el lote
     */
    public int getCantidadActual() {
        return cantidadActual;
    }

    /**
     * Establece la cantidad de unidades disponibles en el lote.
     *
     * @param cantidadActual la cantidad de unidades disponibles en el lote
     */
    public void setCantidadActual(int cantidadActual) {
        this.cantidadActual = cantidadActual;
    }

    /**
     * Indica si el lote está vencido (regla de negocio): su fecha de vencimiento ya pasó.
     *
     * @return {@code true} si la fecha de vencimiento es anterior a hoy; {@code false} si no lo es
     *     o si el lote no tiene fecha
     */
    public boolean estaVencido() {
        return fechaVencimiento != null && fechaVencimiento.isBefore(LocalDate.now());
    }

    /**
     * Calcula los días que faltan para que el lote venza, contados desde hoy.
     *
     * @return cantidad de días hasta el vencimiento (negativa si ya venció) o
     *     {@link Long#MAX_VALUE} si el lote no tiene fecha de vencimiento
     */
    public long diasParaVencer() {
        if (fechaVencimiento == null) {
            return Long.MAX_VALUE;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), fechaVencimiento);
    }
}
