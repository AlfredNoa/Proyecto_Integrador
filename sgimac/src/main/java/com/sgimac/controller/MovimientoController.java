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
 * Implementa la logica del diagrama secuencial 6.7 "Registro de salida de
 * un insumo": valida caducidad, valida stock, registra el movimiento,
 * actualiza la cantidad del lote y, si corresponde, genera una alerta
 * y notifica al personal medico.
 */
@Controller
@RequestMapping("/movimientos")
public class MovimientoController {

    private static final String ROL_PERSONAL_MEDICO = "Personal Médico";

    private final MovimientoDao movimientoDao;
    private final LoteDao loteDao;
    private final InsumoDao insumoDao;
    private final AlertaDao alertaDao;
    private final NotificacionDao notificacionDao;
    private final UsuarioDao usuarioDao;
    private final LogAuditoriaDao logAuditoriaDao;

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

    @GetMapping
    public String listar(Model model) {
        cargarDatosComunes(model);
        return "movimientos/lista";
    }

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
     * Replica el bloque "evaluarAlertas(lote)" del diagrama secuencial 6.7:
     * revisa si el lote esta por vencer y si el insumo quedo bajo su stock
     * minimo; si es asi, genera la alerta (evitando duplicar una ya
     * pendiente) y notifica al personal medico cuando el nivel es ALTO.
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

    private void cargarDatosComunes(Model model) {
        model.addAttribute("lotes", loteDao.listar());
        model.addAttribute("movimientos", movimientoDao.listar());
    }
}
