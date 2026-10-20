package com.sgimac.controller;

import com.sgimac.dao.NotificacionDao;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador (MVC) para las notificaciones del usuario en sesión (pensado principalmente para el
 * rol Personal Médico).
 *
 * @author Equipo SGIMAC
 * @version 1.0
 */
@Controller
@RequestMapping("/notificaciones")
public class NotificacionController {

    /** DAO de notificacion (inyectado por el constructor). */
    private final NotificacionDao notificacionDao;

    /**
     * Crea el controlador con los DAO que Spring inyecta.
     *
     * @param notificacionDao DAO de notificaciones
     */
    public NotificacionController(NotificacionDao notificacionDao) {
        this.notificacionDao = notificacionDao;
    }

    /**
     * Atiende {@code GET /notificaciones}: lista las notificaciones del usuario en sesión.
     *
     * @param sesion sesión HTTP del usuario
     * @param model modelo de Spring donde se cargan los datos que usa la vista
     * @return nombre de la vista Thymeleaf ({@code notificaciones/lista}), o redirección a
     *     {@code /login} si no hay sesión iniciada
     */
    @GetMapping
    public String listar(HttpSession sesion, Model model) {
        Object idUsuarioSesion = sesion.getAttribute("usuarioId");
        if (idUsuarioSesion == null) {
            return "redirect:/login";
        }
        int idUsuario = (int) idUsuarioSesion;
        model.addAttribute("notificaciones", notificacionDao.listarPorUsuario(idUsuario));
        return "notificaciones/lista";
    }

    /**
     * Atiende {@code POST /notificaciones/{id}/leida}: marca una notificación como leída.
     *
     * @param idNotificacion identificador de la notificación (variable {@code id} de la ruta)
     * @return redirección a {@code /notificaciones}
     */
    @PostMapping("/{id}/leida")
    public String marcarLeida(@PathVariable("id") int idNotificacion) {
        notificacionDao.marcarLeida(idNotificacion);
        return "redirect:/notificaciones";
    }
}
