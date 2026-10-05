package com.sgimac.controller;

import com.sgimac.dao.AlertaDao;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador (MVC) para la gestion de alertas.
 */
@Controller
@RequestMapping("/alertas")
public class AlertaController {

    private final AlertaDao alertaDao;

    public AlertaController(AlertaDao alertaDao) {
        this.alertaDao = alertaDao;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("alertas", alertaDao.listarPendientes());
        return "alertas/lista";
    }

    @PostMapping("/{id}/atender")
    public String atender(@PathVariable("id") int idAlerta, HttpSession sesion) {
        Object idUsuarioSesion = sesion.getAttribute("usuarioId");
        if (idUsuarioSesion == null) {
            return "redirect:/login";
        }
        alertaDao.marcarAtendida(idAlerta, (int) idUsuarioSesion);
        return "redirect:/alertas";
    }
}
