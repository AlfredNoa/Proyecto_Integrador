package com.sgimac.model;

import java.time.LocalDateTime;

/**
 * Modelo (MVC) que representa la tabla {@code movimiento} de la base de datos {@code sgimac}.
 * Registra una entrada o una salida de unidades de un lote realizada por un usuario.
 *
 * <p>La validación de caducidad y de stock se hace en {@code MovimientoController}; esta clase solo
 * transporta los datos.
 *
 * @author Equipo SGIMAC
 * @version 1.0
 */
public class Movimiento {

    /** Tipo de movimiento: ingreso de unidades al lote. */
    public static final String ENTRADA = "ENTRADA";
    /** Tipo de movimiento: retiro de unidades del lote. */
    public static final String SALIDA = "SALIDA";

    /** El identificador del movimiento. Clave primaria autoincremental. */
    private int idMovimiento;
    /** El identificador del lote afectado. Clave foránea hacia {@code lote}. */
    private int idLote;
    /** El código QR del lote afectado. Se completa con un {@code JOIN}. */
    private String codigoQrLote;
    /** El nombre del insumo del lote afectado. Se completa con un {@code JOIN}. */
    private String nombreInsumo;
    /** El identificador del usuario que registró el movimiento. Clave foránea hacia {@code usuario}. */
    private int idUsuario;
    /** El nombre del usuario que registró el movimiento. Se completa con un {@code JOIN}. */
    private String nombreUsuario;
    /** El tipo de movimiento. Toma los valores {@link #ENTRADA} o {@link #SALIDA}. */
    private String tipo;
    /** La cantidad de unidades del movimiento. Debe ser mayor que cero. */
    private int cantidad;
    /** La fecha y hora en que se registró el movimiento. La asigna la base de datos. */
    private LocalDateTime fecha;

    /**
     * Crea un objeto vacío; los atributos se completan luego con los métodos <i>set</i> o desde el
     * {@code RowMapper} del DAO.
     */
    public Movimiento() {
    }

    /**
     * Devuelve el identificador del movimiento.
     *
     * @return el identificador del movimiento
     */
    public int getIdMovimiento() {
        return idMovimiento;
    }

    /**
     * Establece el identificador del movimiento.
     *
     * @param idMovimiento el identificador del movimiento
     */
    public void setIdMovimiento(int idMovimiento) {
        this.idMovimiento = idMovimiento;
    }

    /**
     * Devuelve el identificador del lote afectado.
     *
     * @return el identificador del lote afectado
     */
    public int getIdLote() {
        return idLote;
    }

    /**
     * Establece el identificador del lote afectado.
     *
     * @param idLote el identificador del lote afectado
     */
    public void setIdLote(int idLote) {
        this.idLote = idLote;
    }

    /**
     * Devuelve el código QR del lote afectado.
     *
     * @return el código QR del lote afectado
     */
    public String getCodigoQrLote() {
        return codigoQrLote;
    }

    /**
     * Establece el código QR del lote afectado.
     *
     * @param codigoQrLote el código QR del lote afectado
     */
    public void setCodigoQrLote(String codigoQrLote) {
        this.codigoQrLote = codigoQrLote;
    }

    /**
     * Devuelve el nombre del insumo del lote afectado.
     *
     * @return el nombre del insumo del lote afectado
     */
    public String getNombreInsumo() {
        return nombreInsumo;
    }

    /**
     * Establece el nombre del insumo del lote afectado.
     *
     * @param nombreInsumo el nombre del insumo del lote afectado
     */
    public void setNombreInsumo(String nombreInsumo) {
        this.nombreInsumo = nombreInsumo;
    }

    /**
     * Devuelve el identificador del usuario que registró el movimiento.
     *
     * @return el identificador del usuario que registró el movimiento
     */
    public int getIdUsuario() {
        return idUsuario;
    }

    /**
     * Establece el identificador del usuario que registró el movimiento.
     *
     * @param idUsuario el identificador del usuario que registró el movimiento
     */
    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    /**
     * Devuelve el nombre del usuario que registró el movimiento.
     *
     * @return el nombre del usuario que registró el movimiento
     */
    public String getNombreUsuario() {
        return nombreUsuario;
    }

    /**
     * Establece el nombre del usuario que registró el movimiento.
     *
     * @param nombreUsuario el nombre del usuario que registró el movimiento
     */
    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    /**
     * Devuelve el tipo de movimiento.
     *
     * @return el tipo de movimiento
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * Establece el tipo de movimiento.
     *
     * @param tipo el tipo de movimiento
     */
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    /**
     * Devuelve la cantidad de unidades del movimiento.
     *
     * @return la cantidad de unidades del movimiento
     */
    public int getCantidad() {
        return cantidad;
    }

    /**
     * Establece la cantidad de unidades del movimiento.
     *
     * @param cantidad la cantidad de unidades del movimiento
     */
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    /**
     * Devuelve la fecha y hora en que se registró el movimiento.
     *
     * @return la fecha y hora en que se registró el movimiento
     */
    public LocalDateTime getFecha() {
        return fecha;
    }

    /**
     * Establece la fecha y hora en que se registró el movimiento.
     *
     * @param fecha la fecha y hora en que se registró el movimiento
     */
    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }
}
