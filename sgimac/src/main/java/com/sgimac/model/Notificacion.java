package com.sgimac.model;

import java.time.LocalDateTime;

/**
 * Modelo (MVC) que representa la tabla {@code notificacion} de la base de datos {@code sgimac}. Es
 * el aviso que se envía a un usuario a raíz de una alerta de nivel alto.
 *
 * @author Equipo SGIMAC
 * @version 1.0
 */
public class Notificacion {

    /** El identificador de la notificación. Clave primaria autoincremental. */
    private int idNotificacion;
    /**
     * El identificador de la alerta que originó la notificación. Clave foránea hacia
     * {@code alerta}.
     */
    private int idAlerta;
    /** El identificador del usuario destinatario. Clave foránea hacia {@code usuario}. */
    private int idUsuario;
    /** El texto de la notificación. */
    private String mensaje;
    /**
     * El indicador de lectura de la notificación. Es {@code false} hasta que el usuario la marca
     * como leída.
     */
    private boolean leida;
    /** La fecha y hora de envío de la notificación. La asigna la base de datos. */
    private LocalDateTime fechaEnvio;

    /**
     * Crea un objeto vacío; los atributos se completan luego con los métodos <i>set</i> o desde el
     * {@code RowMapper} del DAO.
     */
    public Notificacion() {
    }

    /**
     * Devuelve el identificador de la notificación.
     *
     * @return el identificador de la notificación
     */
    public int getIdNotificacion() {
        return idNotificacion;
    }

    /**
     * Establece el identificador de la notificación.
     *
     * @param idNotificacion el identificador de la notificación
     */
    public void setIdNotificacion(int idNotificacion) {
        this.idNotificacion = idNotificacion;
    }

    /**
     * Devuelve el identificador de la alerta que originó la notificación.
     *
     * @return el identificador de la alerta que originó la notificación
     */
    public int getIdAlerta() {
        return idAlerta;
    }

    /**
     * Establece el identificador de la alerta que originó la notificación.
     *
     * @param idAlerta el identificador de la alerta que originó la notificación
     */
    public void setIdAlerta(int idAlerta) {
        this.idAlerta = idAlerta;
    }

    /**
     * Devuelve el identificador del usuario destinatario.
     *
     * @return el identificador del usuario destinatario
     */
    public int getIdUsuario() {
        return idUsuario;
    }

    /**
     * Establece el identificador del usuario destinatario.
     *
     * @param idUsuario el identificador del usuario destinatario
     */
    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    /**
     * Devuelve el texto de la notificación.
     *
     * @return el texto de la notificación
     */
    public String getMensaje() {
        return mensaje;
    }

    /**
     * Establece el texto de la notificación.
     *
     * @param mensaje el texto de la notificación
     */
    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    /**
     * Devuelve el indicador de lectura de la notificación.
     *
     * @return el indicador de lectura de la notificación
     */
    public boolean isLeida() {
        return leida;
    }

    /**
     * Establece el indicador de lectura de la notificación.
     *
     * @param leida el indicador de lectura de la notificación
     */
    public void setLeida(boolean leida) {
        this.leida = leida;
    }

    /**
     * Devuelve la fecha y hora de envío de la notificación.
     *
     * @return la fecha y hora de envío de la notificación
     */
    public LocalDateTime getFechaEnvio() {
        return fechaEnvio;
    }

    /**
     * Establece la fecha y hora de envío de la notificación.
     *
     * @param fechaEnvio la fecha y hora de envío de la notificación
     */
    public void setFechaEnvio(LocalDateTime fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }
}
