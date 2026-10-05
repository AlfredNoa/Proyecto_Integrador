package com.sgimac.controller;

import com.sgimac.dao.CategoriaDao;
import com.sgimac.dao.InsumoDao;
import com.sgimac.model.Insumo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador (MVC) para la gestion de insumos.
 */
@Controller
@RequestMapping("/insumos")
public class InsumoController {

    private final InsumoDao insumoDao;
    private final CategoriaDao categoriaDao;

    public InsumoController(InsumoDao insumoDao, CategoriaDao categoriaDao) {
        this.insumoDao = insumoDao;
        this.categoriaDao = categoriaDao;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String q, Model model) {
        boolean hayBusqueda = q != null && !q.isBlank();
        model.addAttribute("insumos", hayBusqueda ? insumoDao.buscar(q) : insumoDao.listar());
        model.addAttribute("q", q == null ? "" : q);
        return "insumos/lista";
    }

    @GetMapping("/nuevo")
    public String formularioNuevo(Model model) {
        model.addAttribute("insumo", new Insumo());
        model.addAttribute("categorias", categoriaDao.listar());
        return "insumos/formulario";
    }

    @PostMapping("/nuevo")
    public String crear(@RequestParam String codigo,
                         @RequestParam String nombre,
                         @RequestParam String unidadMedida,
                         @RequestParam int stockMinimo,
                         @RequestParam int idCategoria) {
        Insumo insumo = new Insumo();
        insumo.setCodigo(codigo);
        insumo.setNombre(nombre);
        insumo.setUnidadMedida(unidadMedida);
        insumo.setStockMinimo(stockMinimo);
        insumo.setIdCategoria(idCategoria);
        insumoDao.insertar(insumo);
        return "redirect:/insumos";
    }
}
