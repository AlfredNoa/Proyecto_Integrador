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
     * Previsualiza el PDF sin modificar el historial de exportaciones.
     * @param id identificador de la última consulta
     * @param sesion sesión autenticada
     * @return PDF para visualizar en el navegador
     */
    @GetMapping("/pdf")
    public ResponseEntity<byte[]> pdf(@RequestParam String id, HttpSession sesion) {
        return responder(obtenerConsulta(id, sesion), false, sesion);
    }

    /**
     * Descarga el PDF mediante POST. Registra una única exportación por consulta.
     * El identificador aleatorio debe coincidir con el de la sesión autenticada.
     * @param id identificador enviado por el formulario de exportación
     * @param sesion sesión autenticada
     * @return PDF descargable, aunque temporalmente falle el historial
     */
    @PostMapping("/exportar")
    public ResponseEntity<byte[]> exportar(@RequestParam String id, HttpSession sesion) {
        return responder(obtenerConsulta(id, sesion), true, sesion);
    }

    /** Valida la autenticación y evita utilizar una consulta anterior o de otra sesión. */
    private InventarioSnapshot obtenerConsulta(String id, HttpSession sesion) {
        if (sesion.getAttribute("usuarioId") == null)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Inicie sesión para ver el reporte.");
        Object dato = sesion.getAttribute(SNAPSHOT);
        if (!(dato instanceof InventarioSnapshot reporte) || !reporte.getId().equals(id))
            throw new ResponseStatusException(HttpStatus.GONE, "La consulta cambió o expiró. Vuelva a consultar el inventario.");
        return reporte;
    }

    /** Genera el PDF y trata por separado un posible fallo del registro de exportación. */
    private ResponseEntity<byte[]> responder(InventarioSnapshot reporte, boolean descargar, HttpSession sesion) {
        final byte[] bytes;
        try {
            bytes = pdfService.generar(reporte);
        } catch (Exception e) {
            LOG.error("Error al generar el reporte PDF", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "No se pudo generar el PDF. Regrese al formulario e intente nuevamente.");
        }
        String registro = "no-aplica";
        if (descargar) {
            try {
                reporte.registrarExportacionUnaVez(() ->
                        pdfService.registrarExportacion((int) sesion.getAttribute("usuarioId")));
                registro = "registrado";
            } catch (org.springframework.dao.DataAccessException | org.springframework.transaction.TransactionException e) {
                registro = "pendiente";
                LOG.warn("PDF disponible; no se pudo registrar la exportación de la consulta {}. Se reintentará al descargar de nuevo.", reporte.getId(), e);
            }
        }
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF)
                .cacheControl(CacheControl.noStore())
                .header("X-SGIMAC-Registro", registro)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        (descargar ? "attachment" : "inline") + "; filename=Inventario_SGIMAC.pdf")
                .body(bytes);
    }
}
