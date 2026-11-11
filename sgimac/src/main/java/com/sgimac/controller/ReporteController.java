package com.sgimac.controller;

import com.sgimac.dao.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador (MVC) para los reportes del sistema.
 *
 * <p>Muestra las consultas de próximos a vencer y movimientos y el historial.
 * La exportación PDF de inventario y su registro en historial se implementan en
 * {@link InventarioReporteController}. Los informes 02 y 03 se atienden en {@link OperativoReporteController}.
 *
 * @author Equipo SGIMAC
 * @version 1.0
 */
@Controller
@RequestMapping("/reportes")
public class ReporteController {

    /** DAO de reporte (inyectado por el constructor). */
    private final ReporteDao reporteDao;
    /** DAO de lote (inyectado por el constructor). */
    private final LoteDao loteDao;
    /** DAO de movimiento (inyectado por el constructor). */
    private final MovimientoDao movimientoDao;
    /** DAO de logauditoria (inyectado por el constructor). */
    private final LogAuditoriaDao logAuditoriaDao;

    /**
     * Crea el controlador con los DAO que Spring inyecta.
     *
     * @param reporteDao DAO de reportes
     * @param loteDao DAO de lotes
     * @param movimientoDao DAO de movimientos
     * @param logAuditoriaDao DAO de auditoría
     */
    public ReporteController(ReporteDao reporteDao, LoteDao loteDao,
                              MovimientoDao movimientoDao, LogAuditoriaDao logAuditoriaDao) {
        this.reporteDao = reporteDao;
        this.loteDao = loteDao;
        this.movimientoDao = movimientoDao;
        this.logAuditoriaDao = logAuditoriaDao;
    }

    /**
     * Atiende {@code GET /reportes}: muestra el reporte del tipo elegido y el historial de reportes
     * generados.
     *
     * @param tipo tipo de reporte: {@code inventario} (por defecto), {@code proximos} o
     *     {@code movimientos}
     * @param model modelo de Spring donde se cargan los datos que usa la vista
     * @return nombre de la vista Thymeleaf ({@code reportes/lista})
     */
    @GetMapping
    public String listar(@RequestParam(defaultValue = "inventario") String tipo, Model model) {
        if ("movimientos".equals(tipo) || "alertas".equals(tipo)) return "redirect:/reportes/" + tipo;
        if ("inventario".equals(tipo)) return "redirect:/reportes/inventario";
        cargarDatosComunes(model, tipo);
        return "reportes/lista";
    }

    /**
     * Atiende {@code POST /reportes/generar}: redirige al reporte de inventario.
     * Los informes operativos redirigen a sus formularios.
     *
     * @param tipo tipo de reporte generado
     * @param formato formato elegido ({@code PDF} o {@code EXCEL})
     * @param sesion sesión HTTP del usuario
     * @param model modelo de Spring donde se cargan los datos que usa la vista
     * @return la vista {@code reportes/lista} con un mensaje informativo, o redirección a
     *     {@code /login} si no hay sesión iniciada
     */
    @PostMapping("/generar")
    public String generar(@RequestParam String tipo,
                           @RequestParam String formato,
                           HttpSession sesion,
                           Model model) {
        Object idUsuarioSesion = sesion.getAttribute("usuarioId");
        if (idUsuarioSesion == null) {
            return "redirect:/login";
        }

        if ("movimientos".equals(tipo) || "alertas".equals(tipo)) return "redirect:/reportes/" + tipo;
        if ("inventario".equals(tipo)) return "redirect:/reportes/inventario";
        model.addAttribute("mensaje", "Este tipo de reporte dispone únicamente de consulta en pantalla.");
        cargarDatosComunes(model, tipo);
        return "reportes/lista";
    }

    /**
     * Carga en el modelo los datos de los tres reportes y el historial.
     *
     * @param model modelo de Spring donde se cargan los datos que usa la vista
     * @param tipo tipo de reporte que se está mostrando
     */
    private void cargarDatosComunes(Model model, String tipo) {
        model.addAttribute("tipo", tipo);
        model.addAttribute("lotes", loteDao.listar());
        model.addAttribute("proximosAVencer", loteDao.listarProximosAVencer(90));
        model.addAttribute("movimientos", movimientoDao.listar());
        model.addAttribute("historial", reporteDao.listar());
    }
}
