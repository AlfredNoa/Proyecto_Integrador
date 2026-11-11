package com.sgimac.reportes;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/** Resultado conservado en sesión: el formulario y el PDF recorren las mismas filas. */
public final class ConsultaOperativa implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String id = UUID.randomUUID().toString();
    private final String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    private final String usuario, filtros, resumen;
    private final List<Map<String, ?>> filas;
    private boolean registrada;

    /** Copia los campos ya formateados para que la descarga no repita la consulta SQL. */
    public ConsultaOperativa(String usuario, String filtros, List<Map<String, ?>> datos, boolean movimientos) {
        this.usuario = usuario; this.filtros = filtros;
        List<Map<String, ?>> copia = new ArrayList<>();
        for (Map<String, ?> fila : datos) copia.add(Map.copyOf(fila));
        filas = List.copyOf(copia);
        long n = filas.stream().filter(f -> movimientos ? "ENTRADA".equals(f.get("tipo")) : "PENDIENTE".equals(f.get("estado"))).count();
        resumen = movimientos ? "Movimientos: " + filas.size() + " | Entradas: " + n + " | Salidas: " + (filas.size()-n)
                              : "Alertas: " + filas.size() + " | Pendientes: " + n + " | Atendidas: " + (filas.size()-n);
    }
    /** Registra una sola descarga por consulta; permite reintentar si falla el historial. */
    public synchronized void registrarExportacionUnaVez(Runnable registro) {
        if (!registrada) { registro.run(); registrada = true; }
    }
    public String getId(){return id;}
    public String getFecha(){return fecha;}
    public String getUsuario(){return usuario;}
    public String getFiltros(){return filtros;}
    public String getResumen(){return resumen;}
    public List<Map<String, ?>> getFilas(){return filas;}
}
