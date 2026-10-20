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
 * Controlador (MVC) de autenticación: inicio y cierre de sesión, y panel principal con los
 * indicadores del sistema.
 *
 * <p>Al iniciar sesión valida el correo, que el usuario esté activo y que el hash SHA-256 de la
 * contraseña coincida; luego guarda en la sesión HTTP el identificador, el nombre y el rol del
 * usuario y registra el inicio de sesión en la auditoría.
 *
 * @author Equipo SGIMAC
 * @version 1.0
 */
@Controller
public class LoginController {

    /** DAO de usuario (inyectado por el constructor). */
    private final UsuarioDao usuarioDao;
    /** DAO de logauditoria (inyectado por el constructor). */
    private final LogAuditoriaDao logAuditoriaDao;
    /** DAO de insumo (inyectado por el constructor). */
    private final InsumoDao insumoDao;
    /** DAO de lote (inyectado por el constructor). */
    private final LoteDao loteDao;
    /** DAO de alerta (inyectado por el constructor). */
    private final AlertaDao alertaDao;

    /**
     * Crea el controlador con los DAO que Spring inyecta.
     *
     * @param usuarioDao DAO de usuarios
     * @param logAuditoriaDao DAO de auditoría
     * @param insumoDao DAO de insumos
     * @param loteDao DAO de lotes
     * @param alertaDao DAO de alertas
     */
    public LoginController(UsuarioDao usuarioDao, LogAuditoriaDao logAuditoriaDao,
                           InsumoDao insumoDao, LoteDao loteDao, AlertaDao alertaDao) {
        this.usuarioDao = usuarioDao;
        this.logAuditoriaDao = logAuditoriaDao;
        this.insumoDao = insumoDao;
        this.loteDao = loteDao;
        this.alertaDao = alertaDao;
    }

    /**
     * Atiende {@code GET /login}: muestra el formulario de inicio de sesión.
     *
     * @return nombre de la vista Thymeleaf ({@code login})
     */
    @GetMapping("/login")
    public String formulario() {
        return "login";
    }

    /**
     * Atiende {@code POST /login}: valida las credenciales e inicia la sesión.
     *
     * @param correo correo electrónico del usuario
     * @param contrasena contraseña en texto plano escrita en el formulario
     * @param sesion sesión HTTP del usuario
     * @param model modelo de Spring donde se carga el mensaje de error
     * @return redirección a {@code /panel} si las credenciales son válidas; de lo contrario, la
     *     vista {@code login} con un mensaje de error
     */
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

    /**
     * Atiende {@code GET /panel}: muestra el panel principal con los indicadores (insumos, lotes
     * vigentes, críticos y vencidos, alertas pendientes, alertas recientes y lotes próximos a
     * vencer).
     *
     * @param sesion sesión HTTP del usuario
     * @param model modelo de Spring donde se cargan los datos que usa la vista
     * @return nombre de la vista Thymeleaf ({@code panel}), o redirección a {@code /login} si no
     *     hay sesión iniciada
     */
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

    /**
     * Atiende {@code GET /logout}: invalida la sesión actual.
     *
     * @param sesion sesión HTTP del usuario
     * @return redirección a {@code /login}
     */
    @GetMapping("/logout")
    public String cerrarSesion(HttpSession sesion) {
        sesion.invalidate();
        return "redirect:/login";
    }
}
