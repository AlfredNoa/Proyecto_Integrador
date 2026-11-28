package com.sgimac.controller;

import com.sgimac.dao.AlertaDao;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador (MVC) para la gestión de alertas: listado de pendientes y atención.
 *
 * @author Equipo SGIMAC
 * @version 1.0
 */
@Controller
@RequestMapping("/alertas")
public class AlertaController {
    @org.springframework.beans.factory.annotation.Autowired private com.sgimac.service.ListadosService listados;
    @org.springframework.beans.factory.annotation.Autowired private com.sgimac.service.AlertasService revision;


    /** DAO de alerta (inyectado por el constructor). */
    private final AlertaDao alertaDao;

    /**
     * Crea el controlador con los DAO que Spring inyecta.
     *
     * @param alertaDao DAO de alertas
     */
    public AlertaController(AlertaDao alertaDao) {
        this.alertaDao = alertaDao;
    }

    /**
     * Atiende {@code GET /alertas}: lista las alertas pendientes.
     *
     * @param model modelo de Spring donde se cargan los datos que usa la vista
     * @return nombre de la vista Thymeleaf ({@code alertas/lista})
     
     * @param q búsqueda por nombre o código
     * @param estado filtro del listado
     * @param pagina página solicitada
     * @param tam cantidad de filas por página
     */
    @GetMapping
    public String listar(@RequestParam(defaultValue="") String q,@RequestParam(defaultValue="PENDIENTE") String estado,
       @RequestParam(defaultValue="1") int pagina,@RequestParam(defaultValue="20") int tam,Model model) {
        revision.actualizar();listados.cargar(model,"alertas",q,estado,pagina,tam);return "alertas/lista";
    }

    /**
     * Atiende {@code POST /alertas/{id}/atender}: marca la alerta como atendida por el usuario en
     * sesión.
     *
     * @param idAlerta identificador de la alerta (variable {@code id} de la ruta)
     * @param sesion sesión HTTP del usuario
     * @return redirección a {@code /alertas}, o a {@code /login} si no hay sesión iniciada
     */
    @PostMapping("/{id}/atender")
    public String atender(@PathVariable("id") int idAlerta, HttpSession sesion) {
        Object idUsuarioSesion = sesion.getAttribute("usuarioId");
        if (idUsuarioSesion == null) {
            return "redirect:/login";
        }
        revision.atender(idAlerta, (int) idUsuarioSesion);
        return "redirect:/alertas";
    }
}
