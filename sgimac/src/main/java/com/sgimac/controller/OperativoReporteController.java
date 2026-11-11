package com.sgimac.controller;

import com.sgimac.dao.ConsultaOperativaDao;
import com.sgimac.reportes.*;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import org.slf4j.*;
import org.springframework.dao.DataAccessException;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/** Formularios, vista previa y descarga de los reportes de movimientos y alertas. */
@Controller
@RequestMapping("/reportes/{informe:movimientos|alertas}")
public class OperativoReporteController {
    private static final Logger LOG=LoggerFactory.getLogger(OperativoReporteController.class);
    private final ConsultaOperativaDao dao;
    private final OperativoPdfService pdf;
    private final InventarioPdfService historial;
    public OperativoReporteController(ConsultaOperativaDao dao, OperativoPdfService pdf, InventarioPdfService historial){
        this.dao=dao;this.pdf=pdf;this.historial=historial;
    }
    private static String opcion(String s,String... permitidos){
        if(!List.of(permitidos).contains(s))throw new IllegalArgumentException("Seleccione una opción válida.");
        return s;
    }
    private static LocalDate fecha(String s){
        if(s.isBlank())return null;
        try {
            LocalDate f=LocalDate.parse(s);
            if(f.getYear()<1000 || f.getYear()>9998)throw new IllegalArgumentException("Año fuera del rango admitido.");
            return f;
        } catch(DateTimeParseException e){throw new IllegalArgumentException("Introduzca una fecha válida.");}
    }
    /** Consulta por período y tipo, o por tipo, nivel y estado. No cambia el inventario. */
    @GetMapping
    public String formulario(@PathVariable String informe,@RequestParam Map<String,String> filtros,HttpSession sesion,Model model){
        if(sesion.getAttribute("usuarioId")==null)return "redirect:/login";
        boolean mov=informe.equals("movimientos");
        String tipo=filtros.getOrDefault("tipo","TODOS"),nivel=filtros.getOrDefault("nivel","TODOS"),
            estado=filtros.getOrDefault("estado","TODOS"),desde=filtros.getOrDefault("desde",""),hasta=filtros.getOrDefault("hasta","");
        model.addAttribute("informe",informe);model.addAttribute("tipo",tipo);model.addAttribute("nivel",nivel);model.addAttribute("estado",estado);
        model.addAttribute("desde",desde);model.addAttribute("hasta",hasta);
        if(filtros.containsKey("consultar"))try{
            List<Map<String,?>> filas;String criterios;
            if(mov){
                opcion(tipo,"TODOS","ENTRADA","SALIDA");LocalDate d=fecha(desde),h=fecha(hasta);
                if(d!=null && h!=null && d.isAfter(h))throw new IllegalArgumentException("La fecha inicial debe ser anterior o igual a la final.");
                filas=dao.movimientos(d,h,tipo);
                criterios="Desde: "+(d==null?"Sin límite":d)+" | Hasta: "+(h==null?"Sin límite":h)+" | Tipo: "+tipo;
            }else{
                opcion(tipo,"TODOS","CADUCIDAD","STOCK_BAJO");opcion(nivel,"TODOS","BAJO","MEDIO","ALTO");opcion(estado,"TODOS","PENDIENTE","ATENDIDA");
                filas=dao.alertas(tipo,nivel,estado);criterios="Tipo: "+tipo.replace('_',' ')+" | Nivel: "+nivel+" | Estado: "+estado;
            }
            var r=new ConsultaOperativa((String)sesion.getAttribute("usuarioNombre"),criterios,filas,mov);
            sesion.setAttribute("consulta_"+informe,r);model.addAttribute("reporte",r);
        }catch(IllegalArgumentException e){model.addAttribute("error",e.getMessage());}
        catch(DataAccessException e){LOG.error("Error al consultar "+informe,e);model.addAttribute("error","No se pudo consultar la base de datos.");}
        return "reportes/"+informe;
    }
    /** Abre el PDF de esta sesión sin registrar una descarga. */
    @GetMapping("/pdf")
    public ResponseEntity<byte[]> previa(@PathVariable String informe,@RequestParam String id,HttpSession s){return salida(informe,id,s,false);}
    /** Exporta las mismas filas mostradas y registra la descarga en el historial. */
    @PostMapping("/exportar")
    public ResponseEntity<byte[]> exportar(@PathVariable String informe,@RequestParam String id,HttpSession s){return salida(informe,id,s,true);}
    private ResponseEntity<byte[]> salida(String informe,String id,HttpSession s,boolean descarga){
        if(s.getAttribute("usuarioId")==null)throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        if(!(s.getAttribute("consulta_"+informe) instanceof ConsultaOperativa r) || !r.getId().equals(id))
            throw new ResponseStatusException(HttpStatus.GONE,"La consulta cambió o expiró.");
        byte[] bytes;
        try{bytes=pdf.generar(informe,r);}catch(Exception e){LOG.error("Error al generar "+informe,e);throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"No se pudo generar el PDF.");}
        String registro="no-aplica";
        if(descarga)try{r.registrarExportacionUnaVez(()->historial.registrarExportacion((int)s.getAttribute("usuarioId"),informe));registro="registrado";}
        catch(DataAccessException|TransactionException e){LOG.warn("Descarga disponible; registro pendiente.",e);registro="pendiente";}
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).cacheControl(CacheControl.noStore())
            .header("X-SGIMAC-Registro",registro).header(HttpHeaders.CONTENT_DISPOSITION,(descarga?"attachment":"inline")+"; filename="+informe+".pdf").body(bytes);
    }
}
