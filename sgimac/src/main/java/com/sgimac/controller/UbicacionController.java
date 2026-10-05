package com.sgimac.controller;

import com.sgimac.dao.UbicacionDao;
import com.sgimac.model.Ubicacion;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador (MVC) para la gestion de ubicaciones (almacenes).
 */
@Controller
@RequestMapping("/ubicaciones")
public class UbicacionController {

    private final UbicacionDao ubicacionDao;

    public UbicacionController(UbicacionDao ubicacionDao) {
        this.ubicacionDao = ubicacionDao;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("ubicaciones", ubicacionDao.listar());
        return "ubicaciones/lista";
    }

    @PostMapping("/nuevo")
    public String crear(@RequestParam String nombre, @RequestParam String tipo) {
        Ubicacion ubicacion = new Ubicacion();
        ubicacion.setNombre(nombre);
        ubicacion.setTipo(tipo);
        ubicacionDao.insertar(ubicacion);
        return "redirect:/ubicaciones";
    }
}
