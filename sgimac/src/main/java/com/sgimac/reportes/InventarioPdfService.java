package com.sgimac.reportes;

import com.sgimac.dao.ReporteDao;
import com.sgimac.dao.LogAuditoriaDao;
import com.sgimac.model.Reporte;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Compila la plantilla del classpath y genera el PDF a partir de la consulta conservada.
 * El diseño compilado se reutiliza; los datos de cada usuario se mantienen separados.
 * @author Equipo SGIMAC
 * @version 1.1
 */
@Service
public class InventarioPdfService {
    private final ReporteDao reporteDao;
    private final LogAuditoriaDao auditoriaDao;
    private volatile JasperReport plantilla;

    /** @param reporteDao historial de reportes
     * @param auditoriaDao registro de auditoría */
    public InventarioPdfService(ReporteDao reporteDao, LogAuditoriaDao auditoriaDao) {
        this.reporteDao = reporteDao; this.auditoriaDao = auditoriaDao;
    }
    /**
     * Genera un PDF real sin volver a consultar los lotes.
     * @param snapshot copia de los datos mostrados al usuario
     * @return bytes de un documento PDF
     * @throws JRException si falla la compilación, el llenado o la exportación
     * @throws IOException si no se puede leer la plantilla
     */
    public byte[] generar(InventarioSnapshot snapshot) throws JRException, IOException {
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("FECHA", snapshot.getFechaTexto());
        parametros.put("USUARIO", snapshot.getGeneradoPor());
        parametros.put("FILTROS", snapshot.getFiltros());
        parametros.put("RESUMEN", snapshot.getResumen());
        JasperPrint documento = JasperFillManager.fillReport(plantilla(), parametros,
                new JRMapCollectionDataSource(snapshot.getFilas()));
        return JasperExportManager.exportReportToPdf(documento);
    }
    private synchronized JasperReport plantilla() throws JRException, IOException {
        if (plantilla == null) {
            try (InputStream entrada = new ClassPathResource("reports/inventario_lotes.jrxml").getInputStream()) {
                plantilla = JasperCompileManager.compileReport(entrada);
            }
        }
        return plantilla;
    }
    /**
     * Registra una exportación después de generar el PDF. Ambas inserciones son atómicas.
     * @param usuarioId identificador del usuario de sesión
     */
    @Transactional
    public void registrarExportacion(int usuarioId) {
        Reporte reporte = new Reporte(); reporte.setIdUsuario(usuarioId);
        reporte.setTipo("inventario"); reporte.setFormato("PDF");
        int id = reporteDao.insertar(reporte);
        auditoriaDao.registrar(usuarioId, "Exportó inventario PDF", "reporte:" + id);
    }
}
