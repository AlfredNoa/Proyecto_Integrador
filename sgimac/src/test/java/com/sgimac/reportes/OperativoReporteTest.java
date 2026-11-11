package com.sgimac.reportes;

import java.nio.charset.StandardCharsets;
import java.util.*;
import com.sgimac.controller.OperativoReporteController;
import com.sgimac.dao.ConsultaOperativaDao;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.web.server.ResponseStatusException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/** Pruebas sin MySQL: PDF con imagen, resumen, filtros inválidos y consultas de otra sesión. */
class OperativoReporteTest {
    @Test void generaAmbasPlantillasConLogoInclusoSinFilas() throws Exception {
        var servicio = new OperativoPdfService();
        for(String informe:List.of("movimientos","alertas")){
            Map<String,String> fila=Map.of("fecha","10/11/2026","insumo","Gasa","lote","L-01",
                "tipo",informe.equals("movimientos")?"ENTRADA":"CADUCIDAD","estado","PENDIENTE",
                "nivel","ALTO","responsable","—","cantidad","8","unidad","Cajas","usuario","Ana");
            for(int n:List.of(0,40)){
                var r=new ConsultaOperativa("Ana","Todos",Collections.nCopies(n,fila),informe.equals("movimientos"));
                String bytes=new String(servicio.generar(informe,r),StandardCharsets.ISO_8859_1);
                assertTrue(bytes.startsWith("%PDF-"));
                assertTrue(java.util.regex.Pattern.compile("/Subtype\\s*/Image").matcher(bytes).find());
                assertTrue(r.getResumen().contains((informe.equals("movimientos")?"Entradas: ":"Pendientes: ")+n));
            }
        }
    }
    @Test void rechazaFechasInvertidasYSesionAjena(){
        var dao=mock(ConsultaOperativaDao.class);var pdf=mock(OperativoPdfService.class);
        var c=new OperativoReporteController(dao,pdf,mock(InventarioPdfService.class));
        var s=new MockHttpSession();s.setAttribute("usuarioId",1);var m=new ExtendedModelMap();
        c.formulario("movimientos",Map.of("consultar","true","desde","2026-11-11","hasta","2026-11-10"),s,m);
        assertTrue(m.containsAttribute("error"));verifyNoInteractions(dao);
        assertEquals(410,assertThrows(ResponseStatusException.class,()->c.previa("alertas","ajena",s)).getStatusCode().value());
        verifyNoInteractions(pdf);
    }
}
