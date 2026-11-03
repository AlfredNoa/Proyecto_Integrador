package com.sgimac.controller;

import com.sgimac.dao.LoteDao;
import com.sgimac.reportes.InventarioPdfService;
import com.sgimac.reportes.InventarioSnapshot;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Formulario, consulta y exportación del reporte 01 de inventario por lote.
 * Conserva la última consulta de cada sesión para exportar exactamente sus datos.
 * @author Equipo SGIMAC
 * @version 1.1
 */
@Controller
@RequestMapping("/reportes/inventario")
public class InventarioReporteController {
    private static final String SNAPSHOT = "inventarioReporteSnapshot";
    private static final Logger LOG = LoggerFactory.getLogger(InventarioReporteController.class);
    private final LoteDao loteDao;
    private final InventarioPdfService pdfService;
    /** @param loteDao acceso al inventario
     * @param pdfService generador del PDF */
    public InventarioReporteController(LoteDao loteDao, InventarioPdfService pdfService) {
        this.loteDao = loteDao; this.pdfService = pdfService;
    }
    /**
     * Muestra el formulario y, si se solicita, obtiene una nueva instantánea del inventario.
     * @param q texto parcial de insumo o lote
     * @param estado filtro de caducidad
     * @param consultar true al pulsar Consultar
     * @param sesion sesión del usuario
     * @param model atributos de la vista
     * @return formulario o acceso al login si no hay autenticación
     */
    @GetMapping
    public String formulario(@RequestParam(defaultValue = "") String q,
            @RequestParam(defaultValue = "TODOS") String estado,
            @RequestParam(defaultValue = "false") boolean consultar,
            HttpSession sesion, Model model) {
        if (sesion.getAttribute("usuarioId") == null) return "redirect:/login";
        model.addAttribute("q", q); model.addAttribute("estado", estado);
        if (consultar) {
            try {
                InventarioSnapshot reporte = new InventarioSnapshot(loteDao.listar(), q, estado,
                        (String) sesion.getAttribute("usuarioNombre"), LocalDateTime.now());
                sesion.setAttribute(SNAPSHOT, reporte);
                model.addAttribute("reporte", reporte);
            } catch (IllegalArgumentException e) {
                model.addAttribute("error", e.getMessage());
            } catch (org.springframework.dao.DataAccessException e) {
                LOG.error("No se pudo consultar el inventario", e);
                model.addAttribute("error", "No se pudo consultar la base de datos. Intente nuevamente.");
            }
        }
        return "reportes/inventario";
    }
    /**
     * Previsualiza o descarga el PDF de la última consulta. Un identificador anterior se rechaza
     * para evitar exportar silenciosamente el resultado de otra pestaña.
     * @param id identificador visible de la consulta
     * @param descargar true para descargar y registrar la exportación; false para vista previa
     * @param sesion sesión autenticada que conserva los datos
     * @return PDF con disposición inline o attachment y sin caché pública
     */
    @GetMapping("/pdf")
    public ResponseEntity<byte[]> pdf(@RequestParam String id,
            @RequestParam(defaultValue = "false") boolean descargar, HttpSession sesion) {
        if (sesion.getAttribute("usuarioId") == null)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Inicie sesión para ver el reporte.");
        Object dato = sesion.getAttribute(SNAPSHOT);
        if (!(dato instanceof InventarioSnapshot reporte) || !reporte.getId().equals(id))
            throw new ResponseStatusException(HttpStatus.GONE, "La consulta cambió o expiró. Vuelva a consultar el inventario.");
        try {
            byte[] bytes = pdfService.generar(reporte);
            if (descargar) pdfService.registrarExportacion((int) sesion.getAttribute("usuarioId"));
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF)
                    .cacheControl(CacheControl.noStore())
                    .header(HttpHeaders.CONTENT_DISPOSITION, (descargar ? "attachment" : "inline") + "; filename=Inventario_SGIMAC.pdf")
                    .body(bytes);
        } catch (Exception e) {
            LOG.error("Error al generar o registrar el reporte PDF", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "No se pudo completar el PDF. Regrese al formulario e intente nuevamente.");
        }
    }
}
