package com.sgimac.controller;

import com.sgimac.dao.CategoriaDao;
import com.sgimac.model.Categoria;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador (MVC) para la gestión de categorías de insumos.
 *
 * @author Equipo SGIMAC
 * @version 1.0
 */
@Controller
@RequestMapping("/categorias")
public class CategoriaController {

    /** DAO de categoria (inyectado por el constructor). */
    private final CategoriaDao categoriaDao;

    /**
     * Crea el controlador con los DAO que Spring inyecta.
     *
     * @param categoriaDao DAO de categorías
     */
    public CategoriaController(CategoriaDao categoriaDao) {
        this.categoriaDao = categoriaDao;
    }

    /**
     * Atiende {@code GET /categorias}: lista las categorías.
     *
     * @param model modelo de Spring donde se cargan los datos que usa la vista
     * @return nombre de la vista Thymeleaf ({@code categorias/lista})
     */
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("categorias", categoriaDao.listar());
        return "categorias/lista";
    }

    /**
     * Atiende {@code POST /categorias/nuevo}: registra una categoría.
     *
     * @param nombre nombre de la categoría
     * @param descripcion descripción de la categoría
     * @return redirección a {@code /categorias}
     */
    @PostMapping("/nuevo")
    public String crear(@RequestParam String nombre, @RequestParam String descripcion) {
        Categoria categoria = new Categoria();
        categoria.setNombre(nombre);
        categoria.setDescripcion(descripcion);
        categoriaDao.insertar(categoria);
        return "redirect:/categorias";
    }

    /**
     * Atiende {@code POST /categorias/{id}/eliminar}: elimina una categoría. Si tiene insumos
     * asociados no se elimina y se muestra un mensaje de error.
     *
     * @param idCategoria identificador de la categoría (variable {@code id} de la ruta)
     * @param model modelo de Spring donde se carga el mensaje de error
     * @return redirección a {@code /categorias} si se eliminó; de lo contrario, la vista
     *     {@code categorias/lista} con el error
     */
    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable("id") int idCategoria, Model model) {
        boolean eliminada = categoriaDao.eliminar(idCategoria);
        if (!eliminada) {
            model.addAttribute("categorias", categoriaDao.listar());
            model.addAttribute("error", "No se puede eliminar: hay insumos que usan esta categoría.");
            return "categorias/lista";
        }
        return "redirect:/categorias";
    }
}
