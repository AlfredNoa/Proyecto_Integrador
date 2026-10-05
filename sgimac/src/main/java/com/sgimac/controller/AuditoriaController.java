package com.sgimac.controller;

import com.sgimac.dao.LogAuditoriaDao;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controlador (MVC) para consultar la auditoria del sistema (pensado
 * para el rol Administrador).
 */
@Controller
@RequestMapping("/auditoria")
public class AuditoriaController {

    private final LogAuditoriaDao logAuditoriaDao;

    public AuditoriaController(LogAuditoriaDao logAuditoriaDao) {
        this.logAuditoriaDao = logAuditoriaDao;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("logs", logAuditoriaDao.listar());
        return "auditoria/lista";
    }
}
