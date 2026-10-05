package com.sgimac.model;

import java.time.LocalDateTime;

/**
 * Modelo (MVC): representa la tabla "movimiento".
 */
public class Movimiento {

    public static final String ENTRADA = "ENTRADA";
    public static final String SALIDA = "SALIDA";

    private int idMovimiento;
    private int idLote;
    private String codigoQrLote;   // via JOIN
    private String nombreInsumo;   // via JOIN
    private int idUsuario;
    private String nombreUsuario;  // via JOIN
    private String tipo;
    private int cantidad;
    private LocalDateTime fecha;

    public Movimiento() {
    }

    public int getIdMovimiento() {
        return idMovimiento;
    }

    public void setIdMovimiento(int idMovimiento) {
        this.idMovimiento = idMovimiento;
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

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }
}
