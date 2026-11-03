package com.sgimac.reportes;

import com.sgimac.model.Lote;
import java.time.*;
import java.util.*;

/** Pruebas de reglas del reporte ejecutables con Java 17, sin base de datos. */
public final class InventarioSnapshotCheck {
    private InventarioSnapshotCheck() { }
    /** @param args argumentos no utilizados */
    public static void main(String[] args) {
        LocalDate hoy = LocalDate.of(2026, 11, 3);
        int[] dias = {-1, 0, 30, 31, 90, 91};
        String[] estados = {"VENCIDO", "CRITICO", "CRITICO", "POR_VENCER", "POR_VENCER", "VIGENTE"};
        List<Lote> lotes = new ArrayList<>();
        for (int i = 0; i < dias.length; i++) {
            Lote l = new Lote(); l.setIdLote(i + 1); l.setCodigoQr("LOTE-" + i);
            l.setNombreInsumo("Gasa estéril"); l.setCantidadActual(20 + i);
            l.setFechaVencimiento(hoy.plusDays(dias[i])); lotes.add(l);
            check(InventarioSnapshot.clasificar(l.getFechaVencimiento(), hoy).equals(estados[i]), "Límite " + dias[i]);
        }
        Collections.reverse(lotes);
        InventarioSnapshot s = new InventarioSnapshot(lotes, " GASA ", "TODOS", "Prueba", hoy.atStartOfDay());
        check(s.getTotal() == 6, "Búsqueda sin distinguir mayúsculas");
        check(s.getFilas().get(0).get("lote").equals("LOTE-0"), "Orden de vencimiento");
        lotes.get(0).setCantidadActual(999);
        check(s.getFilas().get(5).get("cantidad").equals(25), "Copia independiente");
        check(new InventarioSnapshot(lotes, "LOTE-3", "POR_VENCER", "Prueba", hoy.atStartOfDay()).getTotal() == 1, "Combinación de filtros");
        check(new InventarioSnapshot(lotes, "inexistente", "TODOS", "Prueba", hoy.atStartOfDay()).getTotal() == 0, "Sin resultados");
        try { new InventarioSnapshot(lotes, "", "INVALIDO", "Prueba", hoy.atStartOfDay()); throw new AssertionError("Estado inválido aceptado"); }
        catch (IllegalArgumentException esperado) { }
        try { new InventarioSnapshot(lotes, "a".repeat(101), "TODOS", "Prueba", hoy.atStartOfDay()); throw new AssertionError("Búsqueda larga aceptada"); }
        catch (IllegalArgumentException esperado) { }
        try { s.getFilas().clear(); throw new AssertionError("Lista modificable"); }
        catch (UnsupportedOperationException esperado) { }
        try { s.getFilas().get(0).clear(); throw new AssertionError("Fila modificable"); }
        catch (UnsupportedOperationException esperado) { }
        check(InventarioSnapshot.clasificar(null, hoy).equals("SIN_FECHA"), "Fecha ausente");
        System.out.println("OK: límites de caducidad, filtros, orden, copia, inmutabilidad y validación.");
    }
    private static void check(boolean condicion, String caso) {
        if (!condicion) throw new AssertionError(caso);
    }
}
