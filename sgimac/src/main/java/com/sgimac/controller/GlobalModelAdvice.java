package com.sgimac.controller;

import com.sgimac.dao.AlertaDao;
import com.sgimac.dao.NotificacionDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Datos compartidos por todas las vistas: contadores que se muestran
 * como insignias en el menu lateral (alertas y notificaciones pendientes).
 */
@ControllerAdvice
public class GlobalModelAdvice {

    private final AlertaDao alertaDao;
    private final NotificacionDao notificacionDao;

    public GlobalModelAdvice(AlertaDao alertaDao, NotificacionDao notificacionDao) {
        this.alertaDao = alertaDao;
        this.notificacionDao = notificacionDao;
    }

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

    private Object usuarioActual(HttpServletRequest request) {
        HttpSession sesion = request.getSession(false);
        return sesion == null ? null : sesion.getAttribute("usuarioId");
    }
}
