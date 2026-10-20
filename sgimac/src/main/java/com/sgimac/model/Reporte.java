package com.sgimac.model;

import java.time.LocalDateTime;

/**
 * Modelo (MVC) que representa la tabla {@code reporte} de la base de datos {@code sgimac}. Deja
 * constancia de cada reporte generado: quién lo pidió, de qué tipo y en qué formato.
 *
 * @author Equipo SGIMAC
 * @version 1.0
 */
public class Reporte {

    /** Formato de reporte: PDF. */
    public static final String PDF = "PDF";
    /** Formato de reporte: Excel. */
    public static final String EXCEL = "EXCEL";

    /** El identificador del reporte. Clave primaria autoincremental. */
    private int idReporte;
    /** El identificador del usuario que generó el reporte. Clave foránea hacia {@code usuario}. */
    private int idUsuario;
    /** El nombre del usuario que generó el reporte. Se completa con un {@code JOIN}. */
    private String nombreUsuario;
    /**
     * El tipo de reporte. Toma los valores {@code inventario}, {@code proximos} (próximos a vencer)
     * o {@code movimientos}.
     */
    private String tipo;
    /** El formato elegido para el reporte. Toma los valores {@link #PDF} o {@link #EXCEL}. */
    private String formato;
    /** La fecha y hora en que se generó el reporte. La asigna la base de datos. */
    private LocalDateTime fechaGeneracion;

    /**
     * Crea un objeto vacío; los atributos se completan luego con los métodos <i>set</i> o desde el
     * {@code RowMapper} del DAO.
     */
    public Reporte() {
    }

    /**
     * Devuelve el identificador del reporte.
     *
     * @return el identificador del reporte
     */
    public int getIdReporte() {
        return idReporte;
    }

    /**
     * Establece el identificador del reporte.
     *
     * @param idReporte el identificador del reporte
     */
    public void setIdReporte(int idReporte) {
        this.idReporte = idReporte;
    }

    /**
     * Devuelve el identificador del usuario que generó el reporte.
     *
     * @return el identificador del usuario que generó el reporte
     */
    public int getIdUsuario() {
        return idUsuario;
    }

    /**
     * Establece el identificador del usuario que generó el reporte.
     *
     * @param idUsuario el identificador del usuario que generó el reporte
     */
    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    /**
     * Devuelve el nombre del usuario que generó el reporte.
     *
     * @return el nombre del usuario que generó el reporte
     */
    public String getNombreUsuario() {
        return nombreUsuario;
    }

    /**
     * Establece el nombre del usuario que generó el reporte.
     *
     * @param nombreUsuario el nombre del usuario que generó el reporte
     */
    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    /**
     * Devuelve el tipo de reporte.
     *
     * @return el tipo de reporte
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * Establece el tipo de reporte.
     *
     * @param tipo el tipo de reporte
     */
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    /**
     * Devuelve el formato elegido para el reporte.
     *
     * @return el formato elegido para el reporte
     */
    public String getFormato() {
        return formato;
    }

    /**
     * Establece el formato elegido para el reporte.
     *
     * @param formato el formato elegido para el reporte
     */
    public void setFormato(String formato) {
        this.formato = formato;
    }

    /**
     * Devuelve la fecha y hora en que se generó el reporte.
     *
     * @return la fecha y hora en que se generó el reporte
     */
    public LocalDateTime getFechaGeneracion() {
        return fechaGeneracion;
    }

    /**
     * Establece la fecha y hora en que se generó el reporte.
     *
     * @param fechaGeneracion la fecha y hora en que se generó el reporte
     */
    public void setFechaGeneracion(LocalDateTime fechaGeneracion) {
        this.fechaGeneracion = fechaGeneracion;
    }
}
