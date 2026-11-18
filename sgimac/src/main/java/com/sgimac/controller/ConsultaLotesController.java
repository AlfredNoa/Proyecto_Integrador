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

/** Dos pantallas independientes para consultar vencimientos y descargar su resultado. */
@Controller
@RequestMapping("/consultas")
public class ConsultaLotesController {
    private static final String CLAVE="consulta01_lotes";
    private static final Logger LOG=LoggerFactory.getLogger(ConsultaLotesController.class);
    private final ConsultaLotesDao dao;
    private final InsumoDao insumos;
    private final OperativoPdfService pdf;
    public ConsultaLotesController(ConsultaLotesDao dao, InsumoDao insumos, OperativoPdfService pdf) {
        this.dao=dao; this.insumos=insumos; this.pdf=pdf;
    }
    /** Presenta los criterios sin ejecutar todavía la consulta de lotes. */
    @GetMapping
    public String formulario(HttpSession s,Model m) {
        if(s.getAttribute("usuarioId")==null)return "redirect:/login";
        m.addAttribute("insumo","0");m.addAttribute("desde",LocalDate.now().toString());
        m.addAttribute("hasta",LocalDate.now().plusDays(90).toString());
        return cargarFormulario(m);
    }
    private String cargarFormulario(Model m) {
        try { m.addAttribute("insumos",insumos.listar()); }
        catch(DataAccessException e) {
            LOG.error("No se pudo cargar el catálogo de insumos",e);
            m.addAttribute("insumos",java.util.List.of());
            m.addAttribute("error","No se pudo cargar el catálogo. Intente nuevamente.");
        }
        return "consultas/formulario";
    }
    /**
     * Valida los criterios, ejecuta SQL y abre la pantalla de respuesta.
     * Conserva una copia en sesión para que el PDF no repita la consulta.
     */
    @PostMapping("/resultado")
    public String consultar(@RequestParam Map<String,String> f,HttpSession s,Model m) {
        if(s.getAttribute("usuarioId")==null)return "redirect:/login";
        s.removeAttribute(CLAVE);
        String id=f.getOrDefault("insumo","0"),d=f.getOrDefault("desde",""),h=f.getOrDefault("hasta","");
        m.addAttribute("insumo",id);m.addAttribute("desde",d);m.addAttribute("hasta",h);
        try {
            int seleccionado=Integer.parseInt(id);
            if(seleccionado<0)throw new IllegalArgumentException();
            LocalDate desde=LocalDate.parse(d),hasta=LocalDate.parse(h);
            if(desde.getYear()<1000 || hasta.getYear()>9999 || desde.isAfter(hasta))
                throw new IllegalArgumentException();
            String nombre=seleccionado==0?"Todos los insumos":insumos.buscarPorId(seleccionado)
                .orElseThrow(IllegalArgumentException::new).getNombre();
            var filas=dao.consultar(seleccionado,desde,hasta);
            var formato=DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String filtros="Insumo: "+nombre+" | Vencimiento desde: "+desde.format(formato)+" | Hasta: "+hasta.format(formato);
            var resultado=new ConsultaOperativa((String)s.getAttribute("usuarioNombre"),filtros,filas,
                "Lotes encontrados: "+filas.size());
            s.setAttribute(CLAVE,resultado);
            return "redirect:/consultas/resultado?id="+resultado.getId();
        } catch(java.time.format.DateTimeParseException|IllegalArgumentException e) {
            m.addAttribute("error","Seleccione un insumo válido e indique fechas válidas, desde menor o igual que hasta (años 1000–9999).");
        } catch(DataAccessException e) {
            LOG.error("Error en Consulta 01",e);
            m.addAttribute("error","No se pudo consultar la base de datos. Intente nuevamente.");
        }
        return cargarFormulario(m);
    }
    /** Muestra la respuesta únicamente al usuario de la sesión que la obtuvo. */
    @GetMapping("/resultado")
    public String resultado(@RequestParam String id,HttpSession s,Model m) {
        if(s.getAttribute("usuarioId")==null)return "redirect:/login";
        m.addAttribute("reporte",obtener(id,s));
        return "consultas/resultado";
    }
    private ConsultaOperativa obtener(String id,HttpSession s) {
        if(s.getAttribute("usuarioId")==null)throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        if(!(s.getAttribute(CLAVE) instanceof ConsultaOperativa r) || !r.getId().equals(id))
            throw new ResponseStatusException(HttpStatus.GONE,"La consulta cambió o expiró. Realice una nueva consulta.");
        return r;
    }
    /** Compila y llena la plantilla Jasper con las mismas filas de la pantalla. */
    @GetMapping("/pdf")
    public ResponseEntity<byte[]> descargar(@RequestParam String id,HttpSession s) {
        var r=obtener(id,s);
        try {
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF)
                .cacheControl(CacheControl.noStore())
                .header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=consulta01_lotes.pdf")
                .body(pdf.generar("consulta_lotes",r));
        } catch(Exception e) {
            LOG.error("No se pudo generar el PDF de Consulta 01",e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"No se pudo generar el PDF.");
        }
    }
}
