package com.sgimac.controller;

import com.sgimac.dao.ProveedorDao;
import com.sgimac.model.Proveedor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador (MVC) para la gestión de proveedores.
 *
 * @author Equipo SGIMAC
 * @version 1.0
 */
@Controller
@RequestMapping("/proveedores")
public class ProveedorController {

    /** DAO de proveedor (inyectado por el constructor). */
    private final ProveedorDao proveedorDao;

    /**
     * Crea el controlador con los DAO que Spring inyecta.
     *
     * @param proveedorDao DAO de proveedores
     */
    public ProveedorController(ProveedorDao proveedorDao) {
        this.proveedorDao = proveedorDao;
    }

    /**
     * Atiende {@code GET /proveedores}: lista los proveedores.
     *
     * @param model modelo de Spring donde se cargan los datos que usa la vista
     * @return nombre de la vista Thymeleaf ({@code proveedores/lista})
     */
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("proveedores", proveedorDao.listar());
        return "proveedores/lista";
    }

    /**
     * Atiende {@code POST /proveedores/nuevo}: registra un proveedor.
     *
     * @param razonSocial razón social
     * @param ruc RUC de 11 dígitos
     * @param telefono teléfono de contacto
     * @return redirección a {@code /proveedores}
     */
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
