package com.sgimac.controller;

import com.sgimac.dao.*;
import com.sgimac.model.Reporte;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador (MVC) para los reportes del sistema.
 * Muestra los datos en pantalla (inventario, proximos a vencer, movimientos)
 * y registra cada generacion en la tabla "reporte" con su formato elegido.
 */
@Controller
@RequestMapping("/reportes")
public class ReporteController {

    private final ReporteDao reporteDao;
    private final LoteDao loteDao;
    private final MovimientoDao movimientoDao;
    private final LogAuditoriaDao logAuditoriaDao;

    public ReporteController(ReporteDao reporteDao, LoteDao loteDao,
                              MovimientoDao movimientoDao, LogAuditoriaDao logAuditoriaDao) {
        this.reporteDao = reporteDao;
        this.loteDao = loteDao;
        this.movimientoDao = movimientoDao;
        this.logAuditoriaDao = logAuditoriaDao;
    }

    @GetMapping
    public String listar(@RequestParam(defaultValue = "inventario") String tipo, Model model) {
        cargarDatosComunes(model, tipo);
        return "reportes/lista";
    }

    @PostMapping("/generar")
    public String generar(@RequestParam String tipo,
                           @RequestParam String formato,
                           HttpSession sesion,
                           Model model) {
        Object idUsuarioSesion = sesion.getAttribute("usuarioId");
        if (idUsuarioSesion == null) {
            return "redirect:/login";
        }
        int idUsuario = (int) idUsuarioSesion;

        Reporte reporte = new Reporte();
        reporte.setIdUsuario(idUsuario);
        reporte.setTipo(tipo);
        reporte.setFormato(formato);
        reporteDao.insertar(reporte);

        logAuditoriaDao.registrar(idUsuario, "Generó reporte (" + formato + ")", "reporte:" + tipo);

        model.addAttribute("mensaje", "Reporte generado y registrado correctamente.");
        cargarDatosComunes(model, tipo);
        return "reportes/lista";
    }

    private void cargarDatosComunes(Model model, String tipo) {
        model.addAttribute("tipo", tipo);
        model.addAttribute("lotes", loteDao.listar());
        model.addAttribute("proximosAVencer", loteDao.listarProximosAVencer(90));
        model.addAttribute("movimientos", movimientoDao.listar());
        model.addAttribute("historial", reporteDao.listar());
    }
}
