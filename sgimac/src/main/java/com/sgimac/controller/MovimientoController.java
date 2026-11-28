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
    @org.springframework.beans.factory.annotation.Autowired private com.sgimac.service.ListadosService listados;
    @org.springframework.beans.factory.annotation.Autowired private com.sgimac.service.AlertasService revision;
    @org.springframework.beans.factory.annotation.Autowired private com.sgimac.service.FefoService fefo;


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
     
     * @param q búsqueda por nombre o código
     * @param estado filtro del listado
     * @param pagina página solicitada
     * @param tam cantidad de filas por página
     * @param insumoSugerido insumo para la propuesta FEFO
     * @param cantidadSugerida cantidad solicitada para FEFO
     */
    @GetMapping
    public String listar(@RequestParam(defaultValue="") String q,@RequestParam(defaultValue="TODOS") String estado,
       @RequestParam(defaultValue="1") int pagina,@RequestParam(defaultValue="20") int tam,
       @RequestParam(defaultValue="0") int insumoSugerido,@RequestParam(defaultValue="1") int cantidadSugerida,Model model) {
        cargarDatosComunes(model);listados.cargar(model,"movimientos",q,estado,pagina,tam);
        model.addAttribute("insumoSugerido",insumoSugerido);model.addAttribute("cantidadSugerida",cantidadSugerida);
        if(insumoSugerido>0)try{model.addAttribute("fefo",fefo.sugerir(insumoSugerido,cantidadSugerida));}
        catch(IllegalArgumentException e){model.addAttribute("error",e.getMessage());}
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
     * @param flash mensaje que se conserva después del registro
     * @return redirección al historial en caso de éxito, vista con error o redirección
     *     a {@code /login} si no hay sesión iniciada
     */
    @org.springframework.transaction.annotation.Transactional(isolation=org.springframework.transaction.annotation.Isolation.READ_COMMITTED)
    @PostMapping("/registrar")
    public String registrar(@RequestParam int idLote,
                             @RequestParam String tipo,
                             @RequestParam int cantidad,
                             HttpSession sesion,
                             Model model, org.springframework.web.servlet.mvc.support.RedirectAttributes flash) {

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

        if (cantidad <= 0 || !java.util.Set.of("ENTRADA","SALIDA").contains(tipo)) {
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
        revision.actualizar();

        flash.addFlashAttribute("mensaje", "Movimiento registrado correctamente. Actualice la sugerencia de lotes antes de la siguiente salida.");
        return "redirect:/movimientos";
    }

    /**
     * Carga en el modelo la lista de lotes y el historial de movimientos que necesita la vista.
     *
     * @param model modelo de Spring donde se cargan los datos que usa la vista
     */
    private void cargarDatosComunes(Model model) {
        model.addAttribute("lotes", loteDao.listar());
        model.addAttribute("insumos",insumoDao.listar());
        model.addAttribute("insumoSugerido",0);model.addAttribute("cantidadSugerida",1);
        listados.cargar(model,"movimientos","","TODOS",1,20);
    }
}
