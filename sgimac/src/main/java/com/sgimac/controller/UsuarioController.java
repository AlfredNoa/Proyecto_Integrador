package com.sgimac.controller;

import com.sgimac.dao.RolDao;
import com.sgimac.dao.UsuarioDao;
import com.sgimac.model.Usuario;
import com.sgimac.util.PasswordUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador (MVC) para la gestion de usuarios (alta, listado, activar/desactivar).
 * Pensado para el rol Administrador.
 */
@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioDao usuarioDao;
    private final RolDao rolDao;

    public UsuarioController(UsuarioDao usuarioDao, RolDao rolDao) {
        this.usuarioDao = usuarioDao;
        this.rolDao = rolDao;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioDao.listar());
        return "usuarios/lista";
    }

    @GetMapping("/nuevo")
    public String formularioNuevo(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("roles", rolDao.listar());
        return "usuarios/formulario";
    }

    @PostMapping("/nuevo")
    public String crear(@RequestParam String nombres,
                         @RequestParam String correo,
                         @RequestParam String contrasena,
                         @RequestParam int idRol) {
        Usuario usuario = new Usuario();
        usuario.setNombres(nombres);
        usuario.setCorreo(correo);
        usuario.setContrasenaHash(PasswordUtil.hash(contrasena));
        usuario.setIdRol(idRol);
        usuario.setActivo(true);

        usuarioDao.insertar(usuario);
        return "redirect:/usuarios";
    }

    @PostMapping("/{id}/estado")
    public String cambiarEstado(@PathVariable("id") int idUsuario,
                                 @RequestParam boolean activo) {
        usuarioDao.cambiarEstado(idUsuario, activo);
        return "redirect:/usuarios";
    }
}
