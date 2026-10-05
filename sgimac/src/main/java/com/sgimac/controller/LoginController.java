package com.sgimac.controller;

import com.sgimac.dao.LogAuditoriaDao;
import com.sgimac.dao.UsuarioDao;
import com.sgimac.model.Usuario;
import com.sgimac.util.PasswordUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

/**
 * Controlador (MVC) de autenticacion.
 */
@Controller
public class LoginController {

    private final UsuarioDao usuarioDao;
    private final LogAuditoriaDao logAuditoriaDao;

    public LoginController(UsuarioDao usuarioDao, LogAuditoriaDao logAuditoriaDao) {
        this.usuarioDao = usuarioDao;
        this.logAuditoriaDao = logAuditoriaDao;
    }

    @GetMapping("/login")
    public String formulario() {
        return "login";
    }

    @PostMapping("/login")
    public String iniciarSesion(@RequestParam String correo,
                                 @RequestParam String contrasena,
                                 HttpSession sesion,
                                 Model model) {
        Optional<Usuario> encontrado = usuarioDao.buscarPorCorreo(correo);

        boolean credencialesValidas = encontrado.isPresent()
                && encontrado.get().isActivo()
                && PasswordUtil.coincide(contrasena, encontrado.get().getContrasenaHash());

        if (!credencialesValidas) {
            model.addAttribute("error", "Correo, contraseña o usuario inactivo.");
            return "login";
        }

        Usuario usuario = encontrado.get();
        sesion.setAttribute("usuarioId", usuario.getIdUsuario());
        sesion.setAttribute("usuarioNombre", usuario.getNombres());
        sesion.setAttribute("usuarioRol", usuario.getNombreRol());

        logAuditoriaDao.registrar(usuario.getIdUsuario(), "Inicio de sesión", "usuario");

        return "redirect:/panel";
    }

    @GetMapping("/panel")
    public String panel(HttpSession sesion, Model model) {
        if (sesion.getAttribute("usuarioId") == null) {
            return "redirect:/login";
        }
        model.addAttribute("nombre", sesion.getAttribute("usuarioNombre"));
        model.addAttribute("rol", sesion.getAttribute("usuarioRol"));
        return "panel";
    }

    @GetMapping("/logout")
    public String cerrarSesion(HttpSession sesion) {
        sesion.invalidate();
        return "redirect:/login";
    }
}
