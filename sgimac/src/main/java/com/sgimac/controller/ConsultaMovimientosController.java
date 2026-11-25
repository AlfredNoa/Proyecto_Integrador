package com.sgimac.controller;

import com.sgimac.dao.*;
import com.sgimac.reportes.*;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import org.slf4j.*;
import org.springframework.dao.DataAccessException;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/** Consulta 02: criterios, respuesta independiente y PDF de la copia conservada en sesión. */
@Controller
@RequestMapping("/consultas/movimientos")
public class ConsultaMovimientosController {
    private static final String CLAVE="consulta02_movimientos";
    private static final Logger LOG=LoggerFactory.getLogger(ConsultaMovimientosController.class);
    private final ConsultaAdicionalDao dao;
    private final InsumoDao insumos;
    private final OperativoPdfService pdf;
    /**
     * Crea el controlador con los servicios compartidos de la aplicación.
     * @param dao consultas parametrizadas de lectura
     * @param insumos catálogo para validar los criterios
     * @param pdf generador de documentos JasperReports
     */
    public ConsultaMovimientosController(ConsultaAdicionalDao dao, InsumoDao insumos, OperativoPdfService pdf) {
        this.dao=dao; this.insumos=insumos; this.pdf=pdf;
    }
    /**
     * Presenta los criterios iniciales sin ejecutar la consulta.
     * @param s sesión del usuario
     * @param m datos de la vista
     * @return vista del formulario o redirección al inicio de sesión
     */
    @GetMapping
    public String formulario(HttpSession s,Model m) {
        if(s.getAttribute("usuarioId")==null)return "redirect:/login";
        m.addAttribute("insumo","0");m.addAttribute("desde",LocalDate.now().minusDays(30).toString());
        m.addAttribute("hasta",LocalDate.now().toString());
        m.addAttribute("tipo", "TODOS");
        return cargarFormulario(m);
    }
    private String cargarFormulario(Model m) {
        try { m.addAttribute("insumos",insumos.listar()); }
        catch(DataAccessException e) {
            LOG.error("No se pudo cargar el catálogo de insumos",e);
            m.addAttribute("insumos",java.util.List.of());
            m.addAttribute("error","No se pudo cargar el catálogo. Intente nuevamente.");
        }
        return "consultas/movimientos_formulario";
    }
    /**
     * Valida los criterios, ejecuta SQL y abre la pantalla de respuesta.
     * Conserva una copia en sesión para que el PDF no repita la consulta.
     * @param f criterios enviados por el formulario
     * @param s sesión autenticada donde se conserva el resultado
     * @param m modelo para volver a mostrar los criterios si hay errores
     * @return redirección a la respuesta o formulario con mensaje de error
     */
    @PostMapping("/resultado")
    public String consultar(@RequestParam Map<String,String> f,HttpSession s,Model m) {
        if(s.getAttribute("usuarioId")==null)return "redirect:/login";
        s.removeAttribute(CLAVE);
        String tipo=f.getOrDefault("tipo","TODOS");
        m.addAttribute("tipo",tipo);
        String id=f.getOrDefault("insumo","0"),d=f.getOrDefault("desde",""),h=f.getOrDefault("hasta","");
        m.addAttribute("insumo",id);m.addAttribute("desde",d);m.addAttribute("hasta",h);
        try {
            int seleccionado=Integer.parseInt(id);
            if(seleccionado<0 || !java.util.Set.of("TODOS","ENTRADA","SALIDA").contains(tipo))throw new IllegalArgumentException();
            LocalDate desde=LocalDate.parse(d),hasta=LocalDate.parse(h);
            if(desde.getYear()<1000 || hasta.getYear()>9999 || desde.isAfter(hasta))
                throw new IllegalArgumentException();
            String nombre=seleccionado==0?"Todos los insumos":insumos.buscarPorId(seleccionado)
                .orElseThrow(IllegalArgumentException::new).getNombre();
            var filas=dao.movimientos(seleccionado,desde,hasta,tipo);
            var formato=DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String filtros="Insumo: "+nombre+" | Movimientos desde: "+desde.format(formato)+" | Hasta: "+hasta.format(formato)+" | Tipo: "+tipo;
            var resultado=new ConsultaOperativa((String)s.getAttribute("usuarioNombre"),filtros,filas,
                "Movimientos encontrados: "+filas.size());
            s.setAttribute(CLAVE,resultado);
            return "redirect:/consultas/movimientos/resultado?id="+resultado.getId();
        } catch(java.time.format.DateTimeParseException|IllegalArgumentException e) {
            m.addAttribute("error","Seleccione un insumo y un tipo válidos e indique fechas válidas, desde menor o igual que hasta (años 1000–9999).");
        } catch(DataAccessException e) {
            LOG.error("Error en Consulta 02",e);
            m.addAttribute("error","No se pudo consultar la base de datos. Intente nuevamente.");
        }
        return cargarFormulario(m);
    }
    /**
     * Muestra la respuesta únicamente en la sesión que la obtuvo.
     * @param id identificador de la consulta conservada
     * @param s sesión del usuario
     * @param m modelo para la tabla de resultados
     * @return vista independiente de resultados
     * @throws ResponseStatusException si el resultado cambió o expiró
     */
    @GetMapping("/resultado")
    public String resultado(@RequestParam String id,HttpSession s,Model m) {
        if(s.getAttribute("usuarioId")==null)return "redirect:/login";
        m.addAttribute("reporte",obtener(id,s));
        return "consultas/movimientos_resultado";
    }
    private ConsultaOperativa obtener(String id,HttpSession s) {
        if(s.getAttribute("usuarioId")==null)throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        if(!(s.getAttribute(CLAVE) instanceof ConsultaOperativa r) || !r.getId().equals(id))
            throw new ResponseStatusException(HttpStatus.GONE,"La consulta cambió o expiró. Realice una nueva consulta.");
        return r;
    }
    /**
     * Descarga el PDF con las mismas filas de la pantalla, sin repetir SQL.
     * @param id identificador de la consulta conservada
     * @param s sesión que debe contener ese resultado
     * @return respuesta PDF como archivo adjunto
     * @throws ResponseStatusException si no hay sesión, el resultado expiró o falla Jasper
     */
    @GetMapping("/pdf")
    public ResponseEntity<byte[]> descargar(@RequestParam String id,HttpSession s) {
        var r=obtener(id,s);
        try {
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF)
                .cacheControl(CacheControl.noStore())
                .header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=consulta02_movimientos.pdf")
                .body(pdf.generar("consulta_movimientos",r));
        } catch(Exception e) {
            LOG.error("No se pudo generar el PDF de Consulta 02",e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"No se pudo generar el PDF.");
        }
    }
}
