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
        assertEquals(401, assertThrows(ResponseStatusException.class, () -> controller.pdf("x", sesion)).getStatusCode().value());
        sesion.setAttribute("usuarioId", 1);
        assertEquals(410, assertThrows(ResponseStatusException.class, () -> controller.pdf("x", sesion)).getStatusCode().value());
        verifyNoInteractions(pdf);
    }

    @Test void descargaRepetidaRegistraUnaVezYVistaPreviaNoRegistra() throws Exception {
        InventarioPdfService pdf = mock(InventarioPdfService.class);
        when(pdf.generar(any())).thenReturn("%PDF-prueba".getBytes(StandardCharsets.US_ASCII));
        var controller = new InventarioReporteController(mock(LoteDao.class), pdf);
        MockHttpSession sesion = new MockHttpSession(); sesion.setAttribute("usuarioId", 1);
        var snapshot = new InventarioSnapshot(List.of(), "", "TODOS", "Usuario", LocalDateTime.now());
        sesion.setAttribute("inventarioReporteSnapshot", snapshot);
        controller.pdf(snapshot.getId(), sesion);
        verify(pdf, never()).registrarExportacion(anyInt());
        assertTrue(controller.exportar(snapshot.getId(), sesion).getHeaders().getFirst("Content-Disposition").startsWith("attachment"));
        controller.exportar(snapshot.getId(), sesion);
        verify(pdf, times(1)).registrarExportacion(1);
    }

    @Test void falloDeHistorialEntregaPdfYPermiteReintentar() throws Exception {
        InventarioPdfService pdf = mock(InventarioPdfService.class);
        byte[] bytes = "%PDF-prueba".getBytes(StandardCharsets.US_ASCII);
        when(pdf.generar(any())).thenReturn(bytes);
        doThrow(new org.springframework.dao.DataAccessResourceFailureException("Sin conexión"))
            .doNothing().when(pdf).registrarExportacion(1);
        var controller = new InventarioReporteController(mock(LoteDao.class), pdf);
        MockHttpSession sesion = new MockHttpSession(); sesion.setAttribute("usuarioId", 1);
        var snapshot = new InventarioSnapshot(List.of(), "", "TODOS", "Usuario", LocalDateTime.now());
        sesion.setAttribute("inventarioReporteSnapshot", snapshot);
        var respuesta = controller.exportar(snapshot.getId(), sesion);
        assertArrayEquals(bytes, respuesta.getBody());
        assertEquals("pendiente", respuesta.getHeaders().getFirst("X-SGIMAC-Registro"));
        assertEquals("registrado", controller.exportar(snapshot.getId(), sesion).getHeaders().getFirst("X-SGIMAC-Registro"));
        controller.exportar(snapshot.getId(), sesion);
        verify(pdf, times(2)).registrarExportacion(1);
    }

    @Test void snapshotSeSerializaYConservaMarcaDeExportacion() throws Exception {
        Lote lote = new Lote(); lote.setIdLote(1); lote.setCodigoQr("LOT-001");
        lote.setNombreInsumo("Gasa"); lote.setCantidadActual(10); lote.setFechaVencimiento(LocalDate.now());
        var original = new InventarioSnapshot(List.of(lote), "", "TODOS", "Usuario", LocalDateTime.now());
        original.registrarExportacionUnaVez(() -> { });
        var buffer = new java.io.ByteArrayOutputStream();
        try (var out = new java.io.ObjectOutputStream(buffer)) { out.writeObject(original); }
        try (var in = new java.io.ObjectInputStream(new java.io.ByteArrayInputStream(buffer.toByteArray()))) {
            var copia = (InventarioSnapshot) in.readObject();
            assertEquals(original.getFilas(), copia.getFilas());
            assertEquals(original.getId(), copia.getId());
            copia.registrarExportacionUnaVez(() -> fail("No debe duplicar el registro tras recuperar la sesión"));
        }
    }

    @Test void peticionesSimultaneasNoDuplicanHistorial() throws Exception {
        var snapshot = new InventarioSnapshot(List.of(), "", "TODOS", "Usuario", LocalDateTime.now());
        var contador = new java.util.concurrent.atomic.AtomicInteger();
        var executor = java.util.concurrent.Executors.newFixedThreadPool(4);
        try {
            List<java.util.concurrent.Callable<Void>> llamadas = new ArrayList<>();
            for (int i = 0; i < 12; i++) llamadas.add(() -> { snapshot.registrarExportacionUnaVez(contador::incrementAndGet); return null; });
            for (var resultado : executor.invokeAll(llamadas)) resultado.get();
            assertEquals(1, contador.get());
        } finally { executor.shutdownNow(); }
    }

    @Test void identificadorAjenoOAnteriorSeRechazaTambienEnPost() {
        InventarioPdfService pdf = mock(InventarioPdfService.class);
        var controller = new InventarioReporteController(mock(LoteDao.class), pdf);
        var actual = new InventarioSnapshot(List.of(), "", "TODOS", "Usuario", LocalDateTime.now());
        var anterior = new InventarioSnapshot(List.of(), "", "TODOS", "Usuario", LocalDateTime.now());
        MockHttpSession propia = new MockHttpSession(); propia.setAttribute("usuarioId", 1);
        propia.setAttribute("inventarioReporteSnapshot", actual);
        MockHttpSession ajena = new MockHttpSession(); ajena.setAttribute("usuarioId", 2);
        ajena.setAttribute("inventarioReporteSnapshot", anterior);
        assertEquals(410, assertThrows(ResponseStatusException.class,
            () -> controller.exportar(anterior.getId(), propia)).getStatusCode().value());
        assertEquals(410, assertThrows(ResponseStatusException.class,
            () -> controller.exportar(actual.getId(), ajena)).getStatusCode().value());
        assertEquals(401, assertThrows(ResponseStatusException.class,
            () -> controller.exportar(actual.getId(), new MockHttpSession())).getStatusCode().value());
        verifyNoInteractions(pdf);
    }
}
