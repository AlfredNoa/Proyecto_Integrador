package com.sgimac.controller;

import com.sgimac.dao.AlertaDao;
import com.sgimac.dao.NotificacionDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Datos compartidos por todas las vistas: contadores que se muestran como insignias en el menú
 * lateral (alertas pendientes y notificaciones sin leer).
 *
 * <p>Al ser un {@code @ControllerAdvice}, Spring agrega estos atributos al modelo de cada petición sin
 * que los controladores tengan que hacerlo.
 *
 * @author Equipo SGIMAC
 * @version 1.0
 */
@ControllerAdvice
public class GlobalModelAdvice {

    /** DAO de alerta (inyectado por el constructor). */
    private final AlertaDao alertaDao;
    /** DAO de notificacion (inyectado por el constructor). */
    private final NotificacionDao notificacionDao;

    /**
     * Crea el componente con los DAO que Spring inyecta.
     *
     * @param alertaDao DAO de alertas
     * @param notificacionDao DAO de notificaciones
     */
    public GlobalModelAdvice(AlertaDao alertaDao, NotificacionDao notificacionDao) {
        this.alertaDao = alertaDao;
        this.notificacionDao = notificacionDao;
    }

    /**
     * Atributo de modelo {@code alertasPendientes}: cantidad de alertas pendientes.
     *
     * @param request petición HTTP actual
     * @return cantidad de alertas pendientes; 0 si no hay sesión iniciada o si ocurre un error
     */
    @ModelAttribute("alertasPendientes")
    public int alertasPendientes(HttpServletRequest request) {
        if (usuarioActual(request) == null) {
            return 0;
        }
        try {
            return alertaDao.listarPendientes().size();
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Atributo de modelo {@code notificacionesNoLeidas}: cantidad de notificaciones sin leer del
     * usuario en sesión.
     *
     * @param request petición HTTP actual
     * @return cantidad de notificaciones sin leer; 0 si no hay sesión iniciada o si ocurre un error
     */
    @ModelAttribute("notificacionesNoLeidas")
    public int notificacionesNoLeidas(HttpServletRequest request) {
        Object idUsuario = usuarioActual(request);
        if (idUsuario == null) {
            return 0;
        }
        try {
            return notificacionDao.contarNoLeidas((int) idUsuario);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Obtiene el identificador del usuario en sesión sin crear una sesión nueva.
     *
     * @param request petición HTTP actual
     * @return identificador del usuario, o {@code null} si no hay sesión iniciada
     */
    private Object usuarioActual(HttpServletRequest request) {
        HttpSession sesion = request.getSession(false);
        return sesion == null ? null : sesion.getAttribute("usuarioId");
    }
}
