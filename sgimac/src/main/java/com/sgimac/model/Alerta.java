package com.sgimac.model;

import java.time.LocalDateTime;

/**
 * Modelo (MVC) que representa la tabla {@code alerta} de la base de datos {@code sgimac}. Avisa que
 * un lote está por vencer (o vencido) o que el stock de su insumo quedó bajo el mínimo.
 *
 * <p>Una alerta nace en estado {@link #PENDIENTE} y pasa a {@link #ATENDIDA} cuando un usuario la
 * atiende.
 *
 * @author Equipo SGIMAC
 * @version 1.0
 */
public class Alerta {

    /** Tipo de alerta: el lote está vencido o próximo a vencer. */
    public static final String CADUCIDAD = "CADUCIDAD";
    /** Tipo de alerta: el stock vigente del insumo quedó por debajo de su mínimo. */
    public static final String STOCK_BAJO = "STOCK_BAJO";

    /** Nivel de criticidad bajo. */
    public static final String NIVEL_BAJO = "BAJO";
    /** Nivel de criticidad medio (lote que vence en 90 días o menos, o stock bajo). */
    public static final String NIVEL_MEDIO = "MEDIO";
    /** Nivel de criticidad alto (lote vencido o que vence en 30 días o menos). */
    public static final String NIVEL_ALTO = "ALTO";

    /** Estado inicial de una alerta: todavía nadie la atendió. */
    public static final String PENDIENTE = "PENDIENTE";
    /** Estado de una alerta que ya fue atendida por un usuario. */
    public static final String ATENDIDA = "ATENDIDA";

    /** El identificador de la alerta. Clave primaria autoincremental. */
    private int idAlerta;
    /** El identificador del lote que originó la alerta. Clave foránea hacia {@code lote}. */
    private int idLote;
    /** El código QR del lote que originó la alerta. Se completa con un {@code JOIN}. */
    private String codigoQrLote;
    /** El nombre del insumo del lote que originó la alerta. Se completa con un {@code JOIN}. */
    private String nombreInsumo;
    /** La fecha de vencimiento del lote que originó la alerta. Se completa con un {@code JOIN}. */
    private java.time.LocalDate fechaVencimientoLote;
    /**
     * El identificador del usuario que atendió la alerta. Es {@code null} mientras la alerta está
     * pendiente.
     */
    private Integer idUsuarioAtiende;
    /** El nombre del usuario que atendió la alerta. Se completa con un {@code JOIN}. */
    private String nombreUsuarioAtiende;
    /** El tipo de alerta. Toma los valores {@link #CADUCIDAD} o {@link #STOCK_BAJO}. */
    private String tipo;
    /**
     * El nivel de criticidad de la alerta. Toma los valores {@link #NIVEL_BAJO},
     * {@link #NIVEL_MEDIO} o {@link #NIVEL_ALTO}.
     */
    private String nivel;
    /** El estado de la alerta. Toma los valores {@link #PENDIENTE} o {@link #ATENDIDA}. */
    private String estado;
    /** La fecha y hora en que se generó la alerta. La asigna la base de datos. */
    private LocalDateTime fechaGeneracion;
    /**
     * El mensaje descriptivo de la alerta. Es un dato calculado y no es una columna de la tabla
     * {@code alerta}.
     */
    private String mensaje;

    /**
     * Crea un objeto vacío; los atributos se completan luego con los métodos <i>set</i> o desde el
     * {@code RowMapper} del DAO.
     */
    public Alerta() {
    }

    /**
     * Devuelve el identificador de la alerta.
     *
     * @return el identificador de la alerta
     */
    public int getIdAlerta() {
        return idAlerta;
    }

    /**
     * Establece el identificador de la alerta.
     *
     * @param idAlerta el identificador de la alerta
     */
    public void setIdAlerta(int idAlerta) {
        this.idAlerta = idAlerta;
    }

    /**
     * Devuelve el identificador del lote que originó la alerta.
     *
     * @return el identificador del lote que originó la alerta
     */
    public int getIdLote() {
        return idLote;
    }

    /**
     * Establece el identificador del lote que originó la alerta.
     *
     * @param idLote el identificador del lote que originó la alerta
     */
    public void setIdLote(int idLote) {
        this.idLote = idLote;
    }

    /**
     * Devuelve el código QR del lote que originó la alerta.
     *
     * @return el código QR del lote que originó la alerta
     */
    public String getCodigoQrLote() {
        return codigoQrLote;
    }

    /**
     * Establece el código QR del lote que originó la alerta.
     *
     * @param codigoQrLote el código QR del lote que originó la alerta
     */
    public void setCodigoQrLote(String codigoQrLote) {
        this.codigoQrLote = codigoQrLote;
    }

    /**
     * Devuelve el nombre del insumo del lote que originó la alerta.
     *
     * @return el nombre del insumo del lote que originó la alerta
     */
    public String getNombreInsumo() {
        return nombreInsumo;
    }

    /**
     * Establece el nombre del insumo del lote que originó la alerta.
     *
     * @param nombreInsumo el nombre del insumo del lote que originó la alerta
     */
    public void setNombreInsumo(String nombreInsumo) {
        this.nombreInsumo = nombreInsumo;
    }

    /**
     * Devuelve la fecha de vencimiento del lote que originó la alerta.
     *
     * @return la fecha de vencimiento del lote que originó la alerta
     */
    public java.time.LocalDate getFechaVencimientoLote() {
        return fechaVencimientoLote;
    }

    /**
     * Establece la fecha de vencimiento del lote que originó la alerta.
     *
     * @param fechaVencimientoLote la fecha de vencimiento del lote que originó la alerta
     */
    public void setFechaVencimientoLote(java.time.LocalDate fechaVencimientoLote) {
        this.fechaVencimientoLote = fechaVencimientoLote;
    }

    /**
     * Devuelve el identificador del usuario que atendió la alerta.
     *
     * @return el identificador del usuario que atendió la alerta
     */
    public Integer getIdUsuarioAtiende() {
        return idUsuarioAtiende;
    }

    /**
     * Establece el identificador del usuario que atendió la alerta.
     *
     * @param idUsuarioAtiende el identificador del usuario que atendió la alerta
     */
    public void setIdUsuarioAtiende(Integer idUsuarioAtiende) {
        this.idUsuarioAtiende = idUsuarioAtiende;
    }

    /**
     * Devuelve el nombre del usuario que atendió la alerta.
     *
     * @return el nombre del usuario que atendió la alerta
     */
    public String getNombreUsuarioAtiende() {
        return nombreUsuarioAtiende;
    }

    /**
     * Establece el nombre del usuario que atendió la alerta.
     *
     * @param nombreUsuarioAtiende el nombre del usuario que atendió la alerta
     */
    public void setNombreUsuarioAtiende(String nombreUsuarioAtiende) {
        this.nombreUsuarioAtiende = nombreUsuarioAtiende;
    }

    /**
     * Devuelve el tipo de alerta.
     *
     * @return el tipo de alerta
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * Establece el tipo de alerta.
     *
     * @param tipo el tipo de alerta
     */
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    /**
     * Devuelve el nivel de criticidad de la alerta.
     *
     * @return el nivel de criticidad de la alerta
     */
    public String getNivel() {
        return nivel;
    }

    /**
     * Establece el nivel de criticidad de la alerta.
     *
     * @param nivel el nivel de criticidad de la alerta
     */
    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    /**
     * Devuelve el estado de la alerta.
     *
     * @return el estado de la alerta
     */
    public String getEstado() {
        return estado;
    }

    /**
     * Establece el estado de la alerta.
     *
     * @param estado el estado de la alerta
     */
    public void setEstado(String estado) {
        this.estado = estado;
    }

    /**
     * Devuelve la fecha y hora en que se generó la alerta.
     *
     * @return la fecha y hora en que se generó la alerta
     */
    public LocalDateTime getFechaGeneracion() {
        return fechaGeneracion;
    }

    /**
     * Establece la fecha y hora en que se generó la alerta.
     *
     * @param fechaGeneracion la fecha y hora en que se generó la alerta
     */
    public void setFechaGeneracion(LocalDateTime fechaGeneracion) {
        this.fechaGeneracion = fechaGeneracion;
    }

    /**
     * Devuelve el mensaje descriptivo de la alerta.
     *
     * @return el mensaje descriptivo de la alerta
     */
    public String getMensaje() {
        return mensaje;
    }

    /**
     * Establece el mensaje descriptivo de la alerta.
     *
     * @param mensaje el mensaje descriptivo de la alerta
     */
    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
