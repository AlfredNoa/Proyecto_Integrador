package com.sgimac.controller;

import com.sgimac.dao.RolDao;
import com.sgimac.model.Rol;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * Controlador (MVC): pantalla de inicio que verifica la conexion con MySQL.
 */
@Controller
public class InicioController {

    private final RolDao rolDao;

    public InicioController(RolDao rolDao) {
        this.rolDao = rolDao;
    }

    @GetMapping("/")
    public String inicio(Model model) {
        try {
            List<Rol> roles = rolDao.listar();
            model.addAttribute("conectado", true);
            model.addAttribute("roles", roles);
        } catch (Exception e) {
            model.addAttribute("conectado", false);
            model.addAttribute("error", e.getMessage());
        }
        return "index";
    }
}
