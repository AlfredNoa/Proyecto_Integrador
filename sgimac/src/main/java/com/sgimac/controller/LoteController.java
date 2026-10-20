package com.sgimac.controller;

import com.sgimac.dao.InsumoDao;
import com.sgimac.dao.LoteDao;
import com.sgimac.dao.ProveedorDao;
import com.sgimac.dao.UbicacionDao;
import com.sgimac.model.Lote;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * Controlador (MVC) para la gestión de lotes: listado y alta.
 *
 * @author Equipo SGIMAC
 * @version 1.0
 */
@Controller
@RequestMapping("/lotes")
public class LoteController {

    /** DAO de lote (inyectado por el constructor). */
    private final LoteDao loteDao;
    /** DAO de insumo (inyectado por el constructor). */
    private final InsumoDao insumoDao;
    /** DAO de proveedor (inyectado por el constructor). */
    private final ProveedorDao proveedorDao;
    /** DAO de ubicacion (inyectado por el constructor). */
    private final UbicacionDao ubicacionDao;

    /**
     * Crea el controlador con los DAO que Spring inyecta.
     *
     * @param loteDao DAO de lotes
     * @param insumoDao DAO de insumos
     * @param proveedorDao DAO de proveedores
     * @param ubicacionDao DAO de ubicaciones
     */
    public LoteController(LoteDao loteDao, InsumoDao insumoDao,
                           ProveedorDao proveedorDao, UbicacionDao ubicacionDao) {
        this.loteDao = loteDao;
        this.insumoDao = insumoDao;
        this.proveedorDao = proveedorDao;
        this.ubicacionDao = ubicacionDao;
    }

    /**
     * Atiende {@code GET /lotes}: lista los lotes y envía la fecha de hoy a la vista para marcar
     * los vencidos.
     *
     * @param model modelo de Spring donde se cargan los datos que usa la vista
     * @return nombre de la vista Thymeleaf ({@code lotes/lista})
     */
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("lotes", loteDao.listar());
        model.addAttribute("hoy", LocalDate.now());
        return "lotes/lista";
    }

    /**
     * Atiende {@code GET /lotes/nuevo}: muestra el formulario de alta con las listas de insumos,
     * proveedores y ubicaciones.
     *
     * @param model modelo de Spring donde se cargan los datos que usa la vista
     * @return nombre de la vista Thymeleaf ({@code lotes/formulario})
     */
    @GetMapping("/nuevo")
    public String formularioNuevo(Model model) {
        model.addAttribute("insumos", insumoDao.listar());
        model.addAttribute("proveedores", proveedorDao.listar());
        model.addAttribute("ubicaciones", ubicacionDao.listar());
        return "lotes/formulario";
    }

    /**
     * Atiende {@code POST /lotes/nuevo}: registra un lote.
     *
     * @param idInsumo identificador del insumo
     * @param idProveedor identificador del proveedor
     * @param idUbicacion identificador de la ubicación
     * @param codigoQr código QR único del lote
     * @param fechaVencimiento fecha de vencimiento en formato {@code yyyy-MM-dd}
     * @param cantidadActual cantidad inicial de unidades
     * @return redirección a {@code /lotes}
     * @throws java.time.format.DateTimeParseException si la fecha de vencimiento no tiene el
     *     formato {@code yyyy-MM-dd}
     */
    @PostMapping("/nuevo")
    public String crear(@RequestParam int idInsumo,
                         @RequestParam int idProveedor,
                         @RequestParam int idUbicacion,
                         @RequestParam String codigoQr,
                         @RequestParam String fechaVencimiento,
                         @RequestParam int cantidadActual) {
        Lote lote = new Lote();
        lote.setIdInsumo(idInsumo);
        lote.setIdProveedor(idProveedor);
        lote.setIdUbicacion(idUbicacion);
        lote.setCodigoQr(codigoQr);
        lote.setFechaVencimiento(LocalDate.parse(fechaVencimiento));
        lote.setCantidadActual(cantidadActual);
        loteDao.insertar(lote);
        return "redirect:/lotes";
    }
}
