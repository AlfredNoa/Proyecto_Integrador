package com.sgimac.controller;

import com.sgimac.dao.*;
import com.sgimac.model.Alerta;
import com.sgimac.model.Insumo;
import com.sgimac.model.Lote;
import com.sgimac.model.Movimiento;
import com.sgimac.model.Notificacion;
import com.sgimac.model.Usuario;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Controlador (MVC) para el registro de movimientos (entradas y salidas).
 *
 * <p>Implementa la lógica del diagrama secuencial «Registro de salida de un insumo»: valida la
 * caducidad, valida el stock, registra el movimiento, actualiza la cantidad del lote y, si
 * corresponde, genera una alerta y notifica al personal médico.
 *
 * @author Equipo SGIMAC
 * @version 1.0
 */
@Controller
@RequestMapping("/movimientos")
public class MovimientoController {

    /** Nombre del rol al que se notifica cuando se genera una alerta de nivel alto. */
    private static final String ROL_PERSONAL_MEDICO = "Personal Médico";

    /** DAO de movimiento (inyectado por el constructor). */
    private final MovimientoDao movimientoDao;
    /** DAO de lote (inyectado por el constructor). */
    private final LoteDao loteDao;
    /** DAO de insumo (inyectado por el constructor). */
    private final InsumoDao insumoDao;
    /** DAO de alerta (inyectado por el constructor). */
    private final AlertaDao alertaDao;
    /** DAO de notificacion (inyectado por el constructor). */
    private final NotificacionDao notificacionDao;
    /** DAO de usuario (inyectado por el constructor). */
    private final UsuarioDao usuarioDao;
    /** DAO de logauditoria (inyectado por el constructor). */
    private final LogAuditoriaDao logAuditoriaDao;

    /**
     * Crea el controlador con los DAO que Spring inyecta.
     *
     * @param movimientoDao DAO de movimientos
     * @param loteDao DAO de lotes
     * @param insumoDao DAO de insumos
     * @param alertaDao DAO de alertas
     * @param notificacionDao DAO de notificaciones
     * @param usuarioDao DAO de usuarios
     * @param logAuditoriaDao DAO de auditoría
     */
    public MovimientoController(MovimientoDao movimientoDao, LoteDao loteDao,
                                 InsumoDao insumoDao, AlertaDao alertaDao,
                                 NotificacionDao notificacionDao, UsuarioDao usuarioDao,
                                 LogAuditoriaDao logAuditoriaDao) {
        this.movimientoDao = movimientoDao;
        this.loteDao = loteDao;
        this.insumoDao = insumoDao;
        this.alertaDao = alertaDao;
        this.notificacionDao = notificacionDao;
        this.usuarioDao = usuarioDao;
        this.logAuditoriaDao = logAuditoriaDao;
    }

    /**
     * Atiende {@code GET /movimientos}: muestra el formulario de registro y el historial de
     * movimientos.
     *
     * @param model modelo de Spring donde se cargan los datos que usa la vista
     * @return nombre de la vista Thymeleaf ({@code movimientos/lista})
     */
    @GetMapping
    public String listar(Model model) {
        cargarDatosComunes(model);
        return "movimientos/lista";
    }

    /**
     * Atiende {@code POST /movimientos/registrar}: registra una entrada o una salida de un lote.
     *
     * <p>Pasos: (1) si es una salida y el lote está vencido, se bloquea; (2) si es una salida y la
     * cantidad supera el stock del lote, se bloquea; (3) se ajusta la cantidad del lote, se guarda
     * el movimiento y se registra en la auditoría; (4) se evalúan las alertas de caducidad y de
     * stock bajo.
     *
     * @param idLote identificador del lote afectado
     * @param tipo tipo de movimiento ({@code ENTRADA} o {@code SALIDA})
     * @param cantidad cantidad de unidades, mayor que cero
     * @param sesion sesión HTTP del usuario
     * @param model modelo de Spring donde se cargan los mensajes y los datos de la vista
     * @return la vista {@code movimientos/lista} con un mensaje de éxito o de error, o redirección
     *     a {@code /login} si no hay sesión iniciada
     */
    @PostMapping("/registrar")
    public String registrar(@RequestParam int idLote,
                             @RequestParam String tipo,
                             @RequestParam int cantidad,
                             HttpSession sesion,
                             Model model) {

        Object idUsuarioSesion = sesion.getAttribute("usuarioId");
        if (idUsuarioSesion == null) {
            return "redirect:/login";
        }

        Optional<Lote> loteOpt = loteDao.buscarPorId(idLote);
        if (loteOpt.isEmpty()) {
            model.addAttribute("error", "El lote seleccionado no existe.");
            cargarDatosComunes(model);
            return "movimientos/lista";
        }
        Lote lote = loteOpt.get();

        if (cantidad <= 0) {
            model.addAttribute("error", "Ingrese una cantidad válida.");
            cargarDatosComunes(model);
            return "movimientos/lista";
        }

        // --- Paso 1: validar caducidad (solo aplica a salidas) ---
        if (Movimiento.SALIDA.equals(tipo) && lote.estaVencido()) {
            model.addAttribute("error", "Salida bloqueada: el lote está vencido.");
            cargarDatosComunes(model);
            return "movimientos/lista";
        }

        // --- Paso 2: validar stock disponible (solo aplica a salidas) ---
        if (Movimiento.SALIDA.equals(tipo) && cantidad > lote.getCantidadActual()) {
            model.addAttribute("error",
                    "Stock insuficiente: el lote solo tiene " + lote.getCantidadActual() +
                    " " + lote.getUnidadMedida().toLowerCase() + ".");
            cargarDatosComunes(model);
            return "movimientos/lista";
        }

        // --- Paso 3: registrar el movimiento y actualizar el stock del lote ---
        int delta = Movimiento.SALIDA.equals(tipo) ? -cantidad : cantidad;
        boolean actualizado = loteDao.ajustarCantidad(idLote, delta);
        if (!actualizado) {
            model.addAttribute("error", "No se pudo actualizar el stock del lote. Intente nuevamente.");
            cargarDatosComunes(model);
            return "movimientos/lista";
        }

        Movimiento movimiento = new Movimiento();
        movimiento.setIdLote(idLote);
        movimiento.setIdUsuario((int) idUsuarioSesion);
        movimiento.setTipo(tipo);
        movimiento.setCantidad(cantidad);
        movimientoDao.insertar(movimiento);

        logAuditoriaDao.registrar((int) idUsuarioSesion,
                tipo.equals(Movimiento.SALIDA) ? "Registró salida" : "Registró entrada",
                "lote:" + lote.getCodigoQr());

        // --- Paso 4: evaluar alertas de caducidad y de stock bajo ---
        evaluarYGenerarAlertas(idLote);

        model.addAttribute("mensaje", "Movimiento registrado correctamente.");
        cargarDatosComunes(model);
        return "movimientos/lista";
    }

    /**
     * Replica el bloque {@code evaluarAlertas(lote)} del diagrama secuencial 6.7: revisa si el lote
     * está por vencer y si el insumo quedó bajo su stock mínimo; si es así, genera la alerta (sin
     * duplicar una ya pendiente) y, cuando el nivel es alto, notifica al personal médico.
     *
     * <p>Niveles de caducidad: ALTO si el lote está vencido o vence en 30 días o menos; MEDIO si vence
     * en 90 días o menos. La alerta de stock bajo se genera con nivel MEDIO.
     *
     * @param idLote identificador del lote recién modificado
     */
    private void evaluarYGenerarAlertas(int idLote) {
        Optional<Lote> loteActualizado = loteDao.buscarPorId(idLote);
        if (loteActualizado.isEmpty()) {
            return;
        }
        Lote lote = loteActualizado.get();

        // --- Alerta por caducidad ---
        long dias = lote.diasParaVencer();
        String nivelCaducidad = null;
        if (lote.estaVencido() || dias <= 30) {
            nivelCaducidad = Alerta.NIVEL_ALTO;
        } else if (dias <= 90) {
            nivelCaducidad = Alerta.NIVEL_MEDIO;
        }
        if (nivelCaducidad != null && !alertaDao.existePendiente(idLote, Alerta.CADUCIDAD)) {
            Alerta alerta = new Alerta();
            alerta.setIdLote(idLote);
            alerta.setTipo(Alerta.CADUCIDAD);
            alerta.setNivel(nivelCaducidad);
            int idAlerta = alertaDao.insertar(alerta);

            String mensaje = lote.estaVencido()
                    ? lote.getNombreInsumo() + " — lote " + lote.getCodigoQr() + " venció hace " + (-dias) + " días."
                    : lote.getNombreInsumo() + " — lote " + lote.getCodigoQr() + " vence en " + dias + " días.";

            if (Alerta.NIVEL_ALTO.equals(nivelCaducidad)) {
                notificarPersonalMedico(idAlerta, mensaje);
            }
        }

        // --- Alerta por stock bajo (evaluada sobre el insumo del lote) ---
        Optional<Insumo> insumoOpt = insumoDao.buscarPorId(lote.getIdInsumo());
        if (insumoOpt.isPresent()) {
            Insumo insumo = insumoOpt.get();
            int stockVigente = loteDao.sumarStockVigente(insumo.getIdInsumo());
            if (stockVigente < insumo.getStockMinimo()
                    && !alertaDao.existePendiente(idLote, Alerta.STOCK_BAJO)) {
                // La tabla "alerta" referencia un lote (no un insumo directamente),
                // asi que la alerta de stock bajo queda asociada al lote que
                // disparo la revision, como representante del insumo.
                Alerta alerta = new Alerta();
                alerta.setIdLote(idLote);
                alerta.setTipo(Alerta.STOCK_BAJO);
                alerta.setNivel(Alerta.NIVEL_MEDIO);
                alertaDao.insertar(alerta);
            }
        }
    }

    /**
     * Envía una notificación con el mensaje indicado a cada usuario activo con el rol «Personal
     * Médico».
     *
     * @param idAlerta identificador de la alerta que originó la notificación
     * @param mensaje texto de la notificación
     */
    private void notificarPersonalMedico(int idAlerta, String mensaje) {
        List<Usuario> personalMedico = usuarioDao.listarPorRol(ROL_PERSONAL_MEDICO);
        for (Usuario medico : personalMedico) {
            Notificacion notificacion = new Notificacion();
            notificacion.setIdAlerta(idAlerta);
            notificacion.setIdUsuario(medico.getIdUsuario());
            notificacion.setMensaje(mensaje);
            notificacionDao.insertar(notificacion);
        }
    }

    /**
     * Carga en el modelo la lista de lotes y el historial de movimientos que necesita la vista.
     *
     * @param model modelo de Spring donde se cargan los datos que usa la vista
     */
    private void cargarDatosComunes(Model model) {
        model.addAttribute("lotes", loteDao.listar());
        model.addAttribute("movimientos", movimientoDao.listar());
    }
}
