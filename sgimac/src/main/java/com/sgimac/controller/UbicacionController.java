package com.sgimac.controller;

import com.sgimac.dao.UbicacionDao;
import com.sgimac.model.Ubicacion;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador (MVC) para la gestión de ubicaciones (almacenes).
 *
 * @author Equipo SGIMAC
 * @version 1.0
 */
@Controller
@RequestMapping("/ubicaciones")
public class UbicacionController {

    /** DAO de ubicacion (inyectado por el constructor). */
    private final UbicacionDao ubicacionDao;

    /**
     * Crea el controlador con los DAO que Spring inyecta.
     *
     * @param ubicacionDao DAO de ubicaciones
     */
    public UbicacionController(UbicacionDao ubicacionDao) {
        this.ubicacionDao = ubicacionDao;
    }

    /**
     * Atiende {@code GET /ubicaciones}: lista las ubicaciones.
     *
     * @param model modelo de Spring donde se cargan los datos que usa la vista
     * @return nombre de la vista Thymeleaf ({@code ubicaciones/lista})
     */
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("ubicaciones", ubicacionDao.listar());
        return "ubicaciones/lista";
    }

    /**
     * Atiende {@code POST /ubicaciones/nuevo}: registra una ubicación.
     *
     * @param nombre nombre de la ubicación
     * @param tipo tipo de ubicación
     * @return redirección a {@code /ubicaciones}
     */
    @PostMapping("/nuevo")
    public String crear(@RequestParam String nombre, @RequestParam String tipo) {
        Ubicacion ubicacion = new Ubicacion();
        ubicacion.setNombre(nombre);
        ubicacion.setTipo(tipo);
        ubicacionDao.insertar(ubicacion);
        return "redirect:/ubicaciones";
    }
}
