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
 * Controlador (MVC) para la gestion de lotes.
 */
@Controller
@RequestMapping("/lotes")
public class LoteController {

    private final LoteDao loteDao;
    private final InsumoDao insumoDao;
    private final ProveedorDao proveedorDao;
    private final UbicacionDao ubicacionDao;

    public LoteController(LoteDao loteDao, InsumoDao insumoDao,
                           ProveedorDao proveedorDao, UbicacionDao ubicacionDao) {
        this.loteDao = loteDao;
        this.insumoDao = insumoDao;
        this.proveedorDao = proveedorDao;
        this.ubicacionDao = ubicacionDao;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("lotes", loteDao.listar());
        model.addAttribute("hoy", LocalDate.now());
        return "lotes/lista";
    }

    @GetMapping("/nuevo")
    public String formularioNuevo(Model model) {
        model.addAttribute("insumos", insumoDao.listar());
        model.addAttribute("proveedores", proveedorDao.listar());
        model.addAttribute("ubicaciones", ubicacionDao.listar());
        return "lotes/formulario";
    }

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
