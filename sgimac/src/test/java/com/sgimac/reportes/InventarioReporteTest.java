package com.sgimac.reportes;

import com.sgimac.controller.InventarioReporteController;
import com.sgimac.dao.*;
import com.sgimac.model.Lote;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.util.*;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.server.ResponseStatusException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/** Verifica reglas, generación real de PDF y aislamiento de consultas, sin MySQL. */
class InventarioReporteTest {
    @Test void reglasDeInventario() { InventarioSnapshotCheck.main(new String[0]); }

    @Test void plantillaGeneraPdfVacioYVariasPaginas() throws Exception {
        List<Lote> lotes = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Lote l = new Lote(); l.setIdLote(i + 1); l.setNombreInsumo("Insumo de prueba con descripción extensa " + i);
            l.setCodigoQr("DEMO-" + i); l.setCantidadActual(20); l.setUnidadMedida("Unidades");
            l.setFechaVencimiento(LocalDate.of(2027, 1, 1)); lotes.add(l);
        }
        InventarioPdfService service = new InventarioPdfService(mock(ReporteDao.class), mock(LogAuditoriaDao.class));
        for (List<Lote> datos : List.of(lotes, Collections.<Lote>emptyList())) {
            InventarioSnapshot s = new InventarioSnapshot(datos, "", "TODOS", "Prueba", LocalDateTime.of(2026, 11, 3, 10, 0));
            byte[] pdf = service.generar(s);
            assertEquals("%PDF-", new String(pdf, 0, 5, StandardCharsets.US_ASCII));
            assertTrue(pdf.length > 1000);
        }
        try (var entrada = getClass().getResourceAsStream("/reports/inventario_lotes.jrxml")) {
            JasperReport plantilla = JasperCompileManager.compileReport(Objects.requireNonNull(entrada));
            InventarioSnapshot s = new InventarioSnapshot(lotes, "", "TODOS", "Prueba", LocalDateTime.now());
            JasperPrint impreso = JasperFillManager.fillReport(plantilla,
                Map.of("FECHA", s.getFechaTexto(), "USUARIO", s.getGeneradoPor(), "FILTROS", s.getFiltros(), "RESUMEN", s.getResumen()),
                new JRMapCollectionDataSource(s.getFilas()));
            assertTrue(impreso.getPages().size() >= 2, "Veinte filas deben paginar sin perderse");
            long filas = impreso.getPages().stream().flatMap(p -> p.getElements().stream())
                .filter(e -> e instanceof JRPrintText t && t.getFullText().startsWith("DEMO-")).count();
            assertEquals(20, filas);
        }
    }

    @Test void consultaAnteriorOSesionAjenaNoExporta() {
        InventarioPdfService pdf = mock(InventarioPdfService.class);
        var controller = new InventarioReporteController(mock(LoteDao.class), pdf);
        MockHttpSession sesion = new MockHttpSession();
        assertEquals(401, assertThrows(ResponseStatusException.class, () -> controller.pdf("x", false, sesion)).getStatusCode().value());
        sesion.setAttribute("usuarioId", 1);
        assertEquals(410, assertThrows(ResponseStatusException.class, () -> controller.pdf("x", false, sesion)).getStatusCode().value());
        verifyNoInteractions(pdf);
    }
}
