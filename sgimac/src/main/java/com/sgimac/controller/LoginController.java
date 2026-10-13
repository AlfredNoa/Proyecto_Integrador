package com.sgimac.controller;

import com.sgimac.dao.AlertaDao;
import com.sgimac.dao.InsumoDao;
import com.sgimac.dao.LogAuditoriaDao;
import com.sgimac.dao.LoteDao;
import com.sgimac.dao.UsuarioDao;
import com.sgimac.model.Alerta;
import com.sgimac.model.Lote;
import com.sgimac.model.Usuario;
import com.sgimac.util.PasswordUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

/**
 * Controlador (MVC) de autenticacion.
 */
@Controller
public class LoginController {

    private final UsuarioDao usuarioDao;
    private final LogAuditoriaDao logAuditoriaDao;
    private final InsumoDao insumoDao;
    private final LoteDao loteDao;
    private final AlertaDao alertaDao;

    public LoginController(UsuarioDao usuarioDao, LogAuditoriaDao logAuditoriaDao,
                           InsumoDao insumoDao, LoteDao loteDao, AlertaDao alertaDao) {
        this.usuarioDao = usuarioDao;
        this.logAuditoriaDao = logAuditoriaDao;
        this.insumoDao = insumoDao;
        this.loteDao = loteDao;
        this.alertaDao = alertaDao;
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

        // Indicadores del panel principal
        List<Lote> lotes = loteDao.listar();
        long vigentes = lotes.stream().filter(l -> !l.estaVencido() && l.diasParaVencer() > 90).count();
        long criticos = lotes.stream().filter(l -> !l.estaVencido() && l.diasParaVencer() <= 30).count();
        long vencidos = lotes.stream().filter(Lote::estaVencido).count();
        List<Alerta> pendientes = alertaDao.listarPendientes();

        model.addAttribute("totalInsumos", insumoDao.listar().size());
        model.addAttribute("lotesVigentes", vigentes);
        model.addAttribute("lotesCriticos", criticos);
        model.addAttribute("lotesVencidos", vencidos);
        model.addAttribute("totalAlertas", pendientes.size());
        model.addAttribute("alertasRecientes", pendientes.stream().limit(5).toList());
        model.addAttribute("proximos", loteDao.listarProximosAVencer(90).stream().limit(5).toList());
        return "panel";
    }

    @GetMapping("/logout")
    public String cerrarSesion(HttpSession sesion) {
        sesion.invalidate();
        return "redirect:/login";
    }
}
