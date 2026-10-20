package com.sgimac.controller;

import com.sgimac.dao.RolDao;
import com.sgimac.dao.UsuarioDao;
import com.sgimac.model.Usuario;
import com.sgimac.util.PasswordUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador (MVC) para la gestión de usuarios (alta, listado, activar y desactivar). Pensado para
 * el rol Administrador.
 *
 * <p>Al crear un usuario, la contraseña se guarda como hash SHA-256 mediante {@code PasswordUtil}.
 *
 * @author Equipo SGIMAC
 * @version 1.0
 */
@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    /** DAO de usuario (inyectado por el constructor). */
    private final UsuarioDao usuarioDao;
    /** DAO de rol (inyectado por el constructor). */
    private final RolDao rolDao;

    /**
     * Crea el controlador con los DAO que Spring inyecta.
     *
     * @param usuarioDao DAO de usuarios
     * @param rolDao DAO de roles
     */
    public UsuarioController(UsuarioDao usuarioDao, RolDao rolDao) {
        this.usuarioDao = usuarioDao;
        this.rolDao = rolDao;
    }

    /**
     * Atiende {@code GET /usuarios}: lista los usuarios.
     *
     * @param model modelo de Spring donde se cargan los datos que usa la vista
     * @return nombre de la vista Thymeleaf ({@code usuarios/lista})
     */
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioDao.listar());
        return "usuarios/lista";
    }

    /**
     * Atiende {@code GET /usuarios/nuevo}: muestra el formulario de alta con la lista de roles.
     *
     * @param model modelo de Spring donde se cargan los datos que usa la vista
     * @return nombre de la vista Thymeleaf ({@code usuarios/formulario})
     */
    @GetMapping("/nuevo")
    public String formularioNuevo(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("roles", rolDao.listar());
        return "usuarios/formulario";
    }

    /**
     * Atiende {@code POST /usuarios/nuevo}: registra un usuario activo con la contraseña convertida
     * en hash.
     *
     * @param nombres nombres del usuario
     * @param correo correo electrónico
     * @param contrasena contraseña en texto plano
     * @param idRol identificador del rol asignado
     * @return redirección a {@code /usuarios}
     */
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

    /**
     * Atiende {@code POST /usuarios/{id}/estado}: activa o desactiva a un usuario.
     *
     * @param idUsuario identificador del usuario (variable {@code id} de la ruta)
     * @param activo {@code true} para activar, {@code false} para desactivar
     * @return redirección a {@code /usuarios}
     */
    @PostMapping("/{id}/estado")
    public String cambiarEstado(@PathVariable("id") int idUsuario,
                                 @RequestParam boolean activo) {
        usuarioDao.cambiarEstado(idUsuario, activo);
        return "redirect:/usuarios";
    }
}
