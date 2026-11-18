package com.sgimac.reportes;

import com.sgimac.controller.ConsultaLotesController;
import com.sgimac.dao.*;
import java.time.LocalDate;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.web.server.ResponseStatusException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/** Casos de validación y correspondencia del resultado con la descarga. */
class ConsultaLotesTest {
    @Test void validaFechasYNoConsultaSiSonInvalidas() {
        var dao=mock(ConsultaLotesDao.class);var insumos=mock(InsumoDao.class);
        when(insumos.listar()).thenReturn(List.of());
        var c=new ConsultaLotesController(dao,insumos,new OperativoPdfService());
        var s=new MockHttpSession();s.setAttribute("usuarioId",1);
        var m=new ExtendedModelMap();
        assertEquals("consultas/formulario",c.consultar(Map.of("desde","2026-12-01","hasta","2026-11-01"),s,m));
        assertTrue(m.containsAttribute("error"));verifyNoInteractions(dao);
        m.clear();
        c.consultar(Map.of("desde","texto","hasta","2026-11-01"),s,m);
        assertTrue(m.containsAttribute("error"));verifyNoInteractions(dao);
    }
    @Test void separaPantallasYRechazaOtraSesion() {
        var dao=mock(ConsultaLotesDao.class);
        when(dao.consultar(0,LocalDate.of(2026,11,1),LocalDate.of(2026,11,30))).thenReturn(List.of());
        var c=new ConsultaLotesController(dao,mock(InsumoDao.class),new OperativoPdfService());
        var s=new MockHttpSession();s.setAttribute("usuarioId",1);s.setAttribute("usuarioNombre","Ana");
        String ruta=c.consultar(Map.of("desde","2026-11-01","hasta","2026-11-30"),s,new ExtendedModelMap());
        assertTrue(ruta.startsWith("redirect:/consultas/resultado?id="));
        String id=ruta.substring(ruta.indexOf('=')+1);
        var m=new ExtendedModelMap();assertEquals("consultas/resultado",c.resultado(id,s,m));
        assertEquals("Lotes encontrados: 0",((ConsultaOperativa)m.get("reporte")).getResumen());
        var otra=new MockHttpSession();otra.setAttribute("usuarioId",2);
        assertThrows(ResponseStatusException.class,()->c.resultado(id,otra,new ExtendedModelMap()));
        assertThrows(ResponseStatusException.class,()->c.descargar("anterior",s));
    }
    @Test void pdfVacioYMultipaginaConLogo() throws Exception {
        var pdf=new OperativoPdfService();
        for(int cantidad:List.of(0,50)) {
            List<Map<String,?>> filas=new ArrayList<>();
            for(int i=0;i<cantidad;i++)filas.add(Map.of("insumo","Insumo "+i,"lote","LOT-"+i,
                "vence","30/11/2026","stock","25","unidad","Cajas","ubicacion","Almacén Central - A1",
                "proveedor","Distribuidora Sanitaria Altavista"));
            var r=new ConsultaOperativa("Alejandro Torres","Todos los insumos | 01/11/2026 a 30/11/2026",filas,"Lotes encontrados: "+cantidad);
            byte[] bytes=pdf.generar("consulta_lotes",r);
            String contenido=new String(bytes,java.nio.charset.StandardCharsets.ISO_8859_1);
            assertTrue(contenido.startsWith("%PDF"));assertTrue(contenido.contains("/Subtype/Image") || contenido.contains("/Subtype /Image"));
            assertEquals(cantidad,r.getFilas().size());
            filas.clear();assertEquals(cantidad,r.getFilas().size());
        }
    }
}
