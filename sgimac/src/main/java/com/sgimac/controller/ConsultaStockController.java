package com.sgimac.controller;

import com.sgimac.dao.*;
import com.sgimac.reportes.*;
import jakarta.servlet.http.HttpSession;
import java.util.Map;
import org.slf4j.*;
import org.springframework.dao.DataAccessException;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/** Consulta 03: criterios, respuesta independiente y PDF de la copia conservada en sesión. */
@Controller
@RequestMapping("/consultas/stock")
public class ConsultaStockController {
    private static final String CLAVE="consulta03_stock";
    private static final Logger LOG=LoggerFactory.getLogger(ConsultaStockController.class);
    private final ConsultaAdicionalDao dao;
    private final CategoriaDao categorias;
    private final OperativoPdfService pdf;
    /**
     * Crea el controlador con los servicios compartidos de la aplicación.
     * @param dao consultas parametrizadas de lectura
     * @param categorias catálogo para validar los criterios
     * @param pdf generador de documentos JasperReports
     */
    public ConsultaStockController(ConsultaAdicionalDao dao, CategoriaDao categorias, OperativoPdfService pdf) {
        this.dao=dao; this.categorias=categorias; this.pdf=pdf;
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
        m.addAttribute("categoria","0");
        return cargarFormulario(m);
    }
    private String cargarFormulario(Model m) {
        try { m.addAttribute("categorias",categorias.listar()); }
        catch(DataAccessException e) {
            LOG.error("No se pudo cargar el catálogo de categorías",e);
            m.addAttribute("categorias",java.util.List.of());
            m.addAttribute("error","No se pudo cargar el catálogo. Intente nuevamente.");
        }
        return "consultas/stock_formulario";
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
        String id=f.getOrDefault("categoria","0");
        m.addAttribute("categoria",id);
        try {
            int seleccionado=Integer.parseInt(id);
            if(seleccionado<0)throw new IllegalArgumentException();
            String nombre=seleccionado==0?"Todas las categorías":categorias.buscarPorId(seleccionado)
                .orElseThrow(IllegalArgumentException::new).getNombre();
            var filas=dao.stockBajo(seleccionado);
            String filtros="Categoría: "+nombre+" | Stock registrado de todos los lotes, incluidos vencidos.";
            var resultado=new ConsultaOperativa((String)s.getAttribute("usuarioNombre"),filtros,filas,
                "Insumos con stock bajo: "+filas.size());
            s.setAttribute(CLAVE,resultado);
            return "redirect:/consultas/stock/resultado?id="+resultado.getId();
        } catch(IllegalArgumentException e) {
            m.addAttribute("error","Seleccione una categoría válida.");
        } catch(DataAccessException e) {
            LOG.error("Error en Consulta 03",e);
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
        return "consultas/stock_resultado";
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
                .header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=consulta03_stock.pdf")
                .body(pdf.generar("consulta_stock",r));
        } catch(Exception e) {
            LOG.error("No se pudo generar el PDF de Consulta 03",e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"No se pudo generar el PDF.");
        }
    }
}
