package com.sgimac.controller;

import com.sgimac.dao.LogAuditoriaDao;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controlador (MVC) para consultar la auditoría del sistema (pensado para el rol Administrador).
 *
 * @author Equipo SGIMAC
 * @version 1.0
 */
@Controller
@RequestMapping("/auditoria")
public class AuditoriaController {

    /** DAO de logauditoria (inyectado por el constructor). */
    private final LogAuditoriaDao logAuditoriaDao;

    /**
     * Crea el controlador con los DAO que Spring inyecta.
     *
     * @param logAuditoriaDao DAO de auditoría
     */
    public AuditoriaController(LogAuditoriaDao logAuditoriaDao) {
        this.logAuditoriaDao = logAuditoriaDao;
    }

    /**
     * Atiende {@code GET /auditoria}: lista el historial de acciones registradas.
     *
     * @param model modelo de Spring donde se cargan los datos que usa la vista
     * @return nombre de la vista Thymeleaf ({@code auditoria/lista})
     */
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("logs", logAuditoriaDao.listar());
        return "auditoria/lista";
    }
}
