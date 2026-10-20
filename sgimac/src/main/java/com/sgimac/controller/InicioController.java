package com.sgimac.controller;

import com.sgimac.dao.RolDao;
import com.sgimac.model.Rol;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * Controlador (MVC) de la pantalla de inicio ({@code /}), que verifica la conexión con MySQL al
 * listar los roles.
 *
 * @author Equipo SGIMAC
 * @version 1.0
 */
@Controller
public class InicioController {

    /** DAO de rol (inyectado por el constructor). */
    private final RolDao rolDao;

    /**
     * Crea el controlador con los DAO que Spring inyecta.
     *
     * @param rolDao DAO de roles
     */
    public InicioController(RolDao rolDao) {
        this.rolDao = rolDao;
    }

    /**
     * Atiende {@code GET /}. Intenta listar los roles: si funciona, informa que la conexión a MySQL
     * está activa; si falla, muestra el mensaje de error.
     *
     * @param model modelo de Spring donde se cargan los datos que usa la vista
     * @return nombre de la vista Thymeleaf ({@code index})
     */
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
