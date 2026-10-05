package com.sgimac.controller;

import com.sgimac.dao.ProveedorDao;
import com.sgimac.model.Proveedor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador (MVC) para la gestion de proveedores.
 */
@Controller
@RequestMapping("/proveedores")
public class ProveedorController {

    private final ProveedorDao proveedorDao;

    public ProveedorController(ProveedorDao proveedorDao) {
        this.proveedorDao = proveedorDao;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("proveedores", proveedorDao.listar());
        return "proveedores/lista";
    }

    @PostMapping("/nuevo")
    public String crear(@RequestParam String razonSocial,
                         @RequestParam String ruc,
                         @RequestParam String telefono) {
        Proveedor proveedor = new Proveedor();
        proveedor.setRazonSocial(razonSocial);
        proveedor.setRuc(ruc);
        proveedor.setTelefono(telefono);
        proveedorDao.insertar(proveedor);
        return "redirect:/proveedores";
    }
}
