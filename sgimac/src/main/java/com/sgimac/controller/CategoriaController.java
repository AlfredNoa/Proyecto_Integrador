package com.sgimac.controller;

import com.sgimac.dao.CategoriaDao;
import com.sgimac.model.Categoria;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador (MVC) para la gestion de categorias de insumos.
 */
@Controller
@RequestMapping("/categorias")
public class CategoriaController {

    private final CategoriaDao categoriaDao;

    public CategoriaController(CategoriaDao categoriaDao) {
        this.categoriaDao = categoriaDao;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("categorias", categoriaDao.listar());
        return "categorias/lista";
    }

    @PostMapping("/nuevo")
    public String crear(@RequestParam String nombre, @RequestParam String descripcion) {
        Categoria categoria = new Categoria();
        categoria.setNombre(nombre);
        categoria.setDescripcion(descripcion);
        categoriaDao.insertar(categoria);
        return "redirect:/categorias";
    }

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
