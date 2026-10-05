package com.sgimac.model;

import java.time.LocalDateTime;

/**
 * Modelo (MVC): representa la tabla "log_auditoria".
 */
public class LogAuditoria {

    private int idLog;
    private int idUsuario;
    private String nombreUsuario; // via JOIN
    private String accion;
    private String entidad;
    private LocalDateTime fecha;

    public LogAuditoria() {
    }

    public LogAuditoria(int idUsuario, String accion, String entidad) {
        this.idUsuario = idUsuario;
        this.accion = accion;
        this.entidad = entidad;
    }

    public int getIdLog() {
        return idLog;
    }

    public void setIdLog(int idLog) {
        this.idLog = idLog;
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

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public String getEntidad() {
        return entidad;
    }

    public void setEntidad(String entidad) {
        this.entidad = entidad;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }
}
