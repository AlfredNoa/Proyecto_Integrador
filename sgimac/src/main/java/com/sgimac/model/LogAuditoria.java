package com.sgimac.model;

import java.time.LocalDateTime;

/**
 * Modelo (MVC) que representa la tabla {@code log_auditoria} de la base de datos {@code sgimac}.
 * Guarda el historial de acciones que los usuarios realizan en el sistema (auditoría).
 *
 * @author Equipo SGIMAC
 * @version 1.0
 */
public class LogAuditoria {

    /** El identificador del registro de auditoría. Clave primaria autoincremental. */
    private int idLog;
    /** El identificador del usuario que realizó la acción. Clave foránea hacia {@code usuario}. */
    private int idUsuario;
    /** El nombre del usuario que realizó la acción. Se completa con un {@code JOIN}. */
    private String nombreUsuario;
    /** La acción realizada. Por ejemplo, «Inicio de sesión». */
    private String accion;
    /** La entidad o recurso sobre el que se realizó la acción. */
    private String entidad;
    /** La fecha y hora en que se realizó la acción. La asigna la base de datos. */
    private LocalDateTime fecha;

    /**
     * Crea un objeto vacío; los atributos se completan luego con los métodos <i>set</i> o desde el
     * {@code RowMapper} del DAO.
     */
    public LogAuditoria() {
    }

    /**
     * Crea un registro de auditoría listo para guardarse; la fecha la asigna la base de datos.
     *
     * @param idUsuario identificador del usuario que realizó la acción
     * @param accion acción realizada
     * @param entidad entidad o recurso afectado
     */
    public LogAuditoria(int idUsuario, String accion, String entidad) {
        this.idUsuario = idUsuario;
        this.accion = accion;
        this.entidad = entidad;
    }

    /**
     * Devuelve el identificador del registro de auditoría.
     *
     * @return el identificador del registro de auditoría
     */
    public int getIdLog() {
        return idLog;
    }

    /**
     * Establece el identificador del registro de auditoría.
     *
     * @param idLog el identificador del registro de auditoría
     */
    public void setIdLog(int idLog) {
        this.idLog = idLog;
    }

    /**
     * Devuelve el identificador del usuario que realizó la acción.
     *
     * @return el identificador del usuario que realizó la acción
     */
    public int getIdUsuario() {
        return idUsuario;
    }

    /**
     * Establece el identificador del usuario que realizó la acción.
     *
     * @param idUsuario el identificador del usuario que realizó la acción
     */
    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    /**
     * Devuelve el nombre del usuario que realizó la acción.
     *
     * @return el nombre del usuario que realizó la acción
     */
    public String getNombreUsuario() {
        return nombreUsuario;
    }

    /**
     * Establece el nombre del usuario que realizó la acción.
     *
     * @param nombreUsuario el nombre del usuario que realizó la acción
     */
    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    /**
     * Devuelve la acción realizada.
     *
     * @return la acción realizada
     */
    public String getAccion() {
        return accion;
    }

    /**
     * Establece la acción realizada.
     *
     * @param accion la acción realizada
     */
    public void setAccion(String accion) {
        this.accion = accion;
    }

    /**
     * Devuelve la entidad o recurso sobre el que se realizó la acción.
     *
     * @return la entidad o recurso sobre el que se realizó la acción
     */
    public String getEntidad() {
        return entidad;
    }

    /**
     * Establece la entidad o recurso sobre el que se realizó la acción.
     *
     * @param entidad la entidad o recurso sobre el que se realizó la acción
     */
    public void setEntidad(String entidad) {
        this.entidad = entidad;
    }

    /**
     * Devuelve la fecha y hora en que se realizó la acción.
     *
     * @return la fecha y hora en que se realizó la acción
     */
    public LocalDateTime getFecha() {
        return fecha;
    }

    /**
     * Establece la fecha y hora en que se realizó la acción.
     *
     * @param fecha la fecha y hora en que se realizó la acción
     */
    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }
}
