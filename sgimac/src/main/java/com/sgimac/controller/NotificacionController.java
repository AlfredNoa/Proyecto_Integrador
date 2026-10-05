package com.sgimac.controller;

import com.sgimac.dao.NotificacionDao;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador (MVC) para las notificaciones del usuario en sesion
 * (pensado principalmente para el rol Personal Medico).
 */
@Controller
@RequestMapping("/notificaciones")
public class NotificacionController {

    private final NotificacionDao notificacionDao;

    public NotificacionController(NotificacionDao notificacionDao) {
        this.notificacionDao = notificacionDao;
    }

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

    @PostMapping("/{id}/leida")
    public String marcarLeida(@PathVariable("id") int idNotificacion) {
        notificacionDao.marcarLeida(idNotificacion);
        return "redirect:/notificaciones";
    }
}
