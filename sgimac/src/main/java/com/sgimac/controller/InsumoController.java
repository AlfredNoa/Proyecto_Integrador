package com.sgimac.controller;

import com.sgimac.dao.CategoriaDao;
import com.sgimac.dao.InsumoDao;
import com.sgimac.model.Insumo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador (MVC) para la gestión de insumos: listado con búsqueda y alta.
 *
 * @author Equipo SGIMAC
 * @version 1.0
 */
@Controller
@RequestMapping("/insumos")
public class InsumoController {

    /** DAO de insumo (inyectado por el constructor). */
    private final InsumoDao insumoDao;
    /** DAO de categoria (inyectado por el constructor). */
    private final CategoriaDao categoriaDao;

    /**
     * Crea el controlador con los DAO que Spring inyecta.
     *
     * @param insumoDao DAO de insumos
     * @param categoriaDao DAO de categorías
     */
    public InsumoController(InsumoDao insumoDao, CategoriaDao categoriaDao) {
        this.insumoDao = insumoDao;
        this.categoriaDao = categoriaDao;
    }

    /**
     * Atiende {@code GET /insumos}: lista los insumos; si se envía un texto de búsqueda, filtra por
     * nombre o código.
     *
     * @param q texto a buscar (opcional)
     * @param model modelo de Spring donde se cargan los datos que usa la vista
     * @return nombre de la vista Thymeleaf ({@code insumos/lista})
     */
    @GetMapping
    public String listar(@RequestParam(required = false) String q, Model model) {
        boolean hayBusqueda = q != null && !q.isBlank();
        model.addAttribute("insumos", hayBusqueda ? insumoDao.buscar(q) : insumoDao.listar());
        model.addAttribute("q", q == null ? "" : q);
        return "insumos/lista";
    }

    /**
     * Atiende {@code GET /insumos/nuevo}: muestra el formulario de alta con la lista de categorías.
     *
     * @param model modelo de Spring donde se cargan los datos que usa la vista
     * @return nombre de la vista Thymeleaf ({@code insumos/formulario})
     */
    @GetMapping("/nuevo")
    public String formularioNuevo(Model model) {
        model.addAttribute("insumo", new Insumo());
        model.addAttribute("categorias", categoriaDao.listar());
        return "insumos/formulario";
    }

    /**
     * Atiende {@code POST /insumos/nuevo}: registra un insumo.
     *
     * @param codigo código único del insumo
     * @param nombre nombre del insumo
     * @param unidadMedida unidad de medida
     * @param stockMinimo stock mínimo permitido
     * @param idCategoria identificador de la categoría
     * @return redirección a {@code /insumos}
     */
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
