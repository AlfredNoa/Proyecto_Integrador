package com.sgimac.reportes;

import com.sgimac.model.Lote;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Copia inmutable del inventario consultado. Evita que el PDF cambie si el stock se
 * modifica después de mostrar el formulario. No suma cantidades de unidades distintas.
 * @author Equipo SGIMAC
 * @version 1.1
 */
public final class InventarioSnapshot {
    private final String id = UUID.randomUUID().toString();
    private final String consulta;
    private final String estado;
    private final String generadoPor;
    private final LocalDateTime fecha;
    private final List<Map<String, ?>> filas;

    /**
     * Filtra y copia los lotes con una única fecha de referencia.
     * @param lotes lotes obtenidos de la base de datos
     * @param consulta texto parcial de insumo o código de lote
     * @param estado TODOS, VENCIDO, CRITICO, POR_VENCER o VIGENTE
     * @param generadoPor nombre del usuario autenticado
     * @param fecha instante de consulta; se usa también para calcular caducidad
     * @throws IllegalArgumentException si el filtro de estado no es válido o el texto supera 100 caracteres
     */
    public InventarioSnapshot(List<Lote> lotes, String consulta, String estado,
                              String generadoPor, LocalDateTime fecha) {
        this.consulta = consulta == null ? "" : consulta.strip();
        this.estado = estado == null ? "TODOS" : estado;
        if (this.consulta.length() > 100 || !Set.of("TODOS", "VENCIDO", "CRITICO", "POR_VENCER", "VIGENTE").contains(this.estado))
            throw new IllegalArgumentException("Filtros de inventario no válidos.");
        this.generadoPor = generadoPor == null ? "Usuario SGIMAC" : generadoPor;
        this.fecha = Objects.requireNonNull(fecha);
        List<Map<String, ?>> datos = new ArrayList<>();
        lotes.stream().sorted(Comparator.comparing(Lote::getFechaVencimiento,
                Comparator.nullsLast(Comparator.naturalOrder())).thenComparingInt(Lote::getIdLote)).forEach(l -> {
            String clave = clasificar(l.getFechaVencimiento(), fecha.toLocalDate());
            String texto = (seguro(l.getNombreInsumo()) + " " + seguro(l.getCodigoQr())).toLowerCase(Locale.ROOT);
            if (!texto.contains(this.consulta.toLowerCase(Locale.ROOT)) ||
                (!this.estado.equals("TODOS") && !this.estado.equals(clave))) return;
            Map<String, Object> fila = new LinkedHashMap<>();
            fila.put("insumo", seguro(l.getNombreInsumo()));
            fila.put("lote", seguro(l.getCodigoQr()));
            fila.put("vencimiento", l.getFechaVencimiento() == null ? "Sin fecha" : l.getFechaVencimiento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            fila.put("cantidad", l.getCantidadActual());
            fila.put("unidad", seguro(l.getUnidadMedida()));
            fila.put("ubicacion", seguro(l.getNombreUbicacion()));
            fila.put("proveedor", seguro(l.getRazonSocialProveedor()));
            fila.put("estado", etiqueta(clave));
            datos.add(Collections.unmodifiableMap(fila));
        });
        this.filas = List.copyOf(datos);
    }

    /**
     * Aplica los umbrales del módulo de lotes; el día de vencimiento aún es crítico.
     * @param vencimiento fecha del lote, o null
     * @param hoy fecha de corte del reporte
     * @return clave del estado; SIN_FECHA si falta la fecha
     */
    public static String clasificar(LocalDate vencimiento, LocalDate hoy) {
        if (vencimiento == null) return "SIN_FECHA";
        long dias = ChronoUnit.DAYS.between(hoy, vencimiento);
        if (dias < 0) return "VENCIDO";
        if (dias <= 30) return "CRITICO";
        if (dias <= 90) return "POR_VENCER";
        return "VIGENTE";
    }
    /** @param clave clave interna
     * @return nombre legible del estado */
    public static String etiqueta(String clave) {
        return switch (clave) {
            case "VENCIDO" -> "Vencido"; case "CRITICO" -> "Crítico";
            case "POR_VENCER" -> "Por vencer"; case "VIGENTE" -> "Vigente";
            case "TODOS" -> "Todos"; default -> "Sin fecha";
        };
    }
    private static String seguro(String valor) { return valor == null ? "" : valor; }
    /** @return identificador de la consulta conservada en sesión */
    public String getId() { return id; }
    /** @return texto utilizado para filtrar */
    public String getConsulta() { return consulta; }
    /** @return clave del estado seleccionado */
    public String getEstado() { return estado; }
    /** @return usuario que consultó el reporte */
    public String getGeneradoPor() { return generadoPor; }
    /** @return fecha de corte en formato legible */
    public String getFechaTexto() { return fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")); }
    /** @return descripción de los filtros aplicada tanto a la vista como al PDF */
    public String getFiltros() { return "Búsqueda: " + (consulta.isEmpty() ? "Todos los insumos" : consulta) + " | Estado: " + etiqueta(estado); }
    /** @return filas inmutables del inventario */
    public List<Map<String, ?>> getFilas() { return filas; }
    /** @return total de lotes incluidos, sin sumar unidades heterogéneas */
    public int getTotal() { return filas.size(); }
    /** @return conteos por estado correspondientes únicamente a las filas filtradas */
    public String getResumen() {
        return "Lotes: " + getTotal() + " | Vencidos: " + contar("Vencido") + " | Críticos: " + contar("Crítico") +
            " | Por vencer: " + contar("Por vencer") + " | Vigentes: " + contar("Vigente");
    }
    private long contar(String estado) { return filas.stream().filter(f -> estado.equals(f.get("estado"))).count(); }
}
