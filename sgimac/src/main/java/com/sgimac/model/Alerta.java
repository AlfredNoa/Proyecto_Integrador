package com.sgimac.model;

import java.time.LocalDateTime;

/**
 * Modelo (MVC): representa la tabla "alerta".
 */
public class Alerta {

    public static final String CADUCIDAD = "CADUCIDAD";
    public static final String STOCK_BAJO = "STOCK_BAJO";

    public static final String NIVEL_BAJO = "BAJO";
    public static final String NIVEL_MEDIO = "MEDIO";
    public static final String NIVEL_ALTO = "ALTO";

    public static final String PENDIENTE = "PENDIENTE";
    public static final String ATENDIDA = "ATENDIDA";

    private int idAlerta;
    private int idLote;
    private String codigoQrLote;  // via JOIN
    private String nombreInsumo;  // via JOIN
    private java.time.LocalDate fechaVencimientoLote; // via JOIN
    private Integer idUsuarioAtiende;
    private String nombreUsuarioAtiende; // via JOIN
    private String tipo;
    private String nivel;
    private String estado;
    private LocalDateTime fechaGeneracion;
    private String mensaje; // calculado, no es columna propia

    public Alerta() {
    }

    public int getIdAlerta() {
        return idAlerta;
    }

    public void setIdAlerta(int idAlerta) {
        this.idAlerta = idAlerta;
    }

    public int getIdLote() {
        return idLote;
    }

    public void setIdLote(int idLote) {
        this.idLote = idLote;
    }

    public String getCodigoQrLote() {
        return codigoQrLote;
    }

    public void setCodigoQrLote(String codigoQrLote) {
        this.codigoQrLote = codigoQrLote;
    }

    public String getNombreInsumo() {
        return nombreInsumo;
    }

    public void setNombreInsumo(String nombreInsumo) {
        this.nombreInsumo = nombreInsumo;
    }

    public java.time.LocalDate getFechaVencimientoLote() {
        return fechaVencimientoLote;
    }

    public void setFechaVencimientoLote(java.time.LocalDate fechaVencimientoLote) {
        this.fechaVencimientoLote = fechaVencimientoLote;
    }

    public Integer getIdUsuarioAtiende() {
        return idUsuarioAtiende;
    }

    public void setIdUsuarioAtiende(Integer idUsuarioAtiende) {
        this.idUsuarioAtiende = idUsuarioAtiende;
    }

    public String getNombreUsuarioAtiende() {
        return nombreUsuarioAtiende;
    }

    public void setNombreUsuarioAtiende(String nombreUsuarioAtiende) {
        this.nombreUsuarioAtiende = nombreUsuarioAtiende;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getNivel() {
        return nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaGeneracion() {
        return fechaGeneracion;
    }

    public void setFechaGeneracion(LocalDateTime fechaGeneracion) {
        this.fechaGeneracion = fechaGeneracion;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
