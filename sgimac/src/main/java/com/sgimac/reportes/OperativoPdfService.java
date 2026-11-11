package com.sgimac.reportes;

import java.io.*;
import java.util.*;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

/** Genera los informes 02 y 03 con el escudo incluido en los recursos del proyecto. */
@Service
public class OperativoPdfService {
    private final Map<String,JasperReport> plantillas=new HashMap<>();
    private synchronized JasperReport plantilla(String tipo) throws JRException,IOException {
        if(!Set.of("movimientos","alertas").contains(tipo))throw new IllegalArgumentException("Informe desconocido");
        if(!plantillas.containsKey(tipo)){
            try(var xml=new ClassPathResource("reports/"+tipo+".jrxml").getInputStream()){
                plantillas.put(tipo,JasperCompileManager.compileReport(xml));
            }
        }
        return plantillas.get(tipo);
    }
    /** Llena la plantilla con la copia de la consulta y devuelve los bytes del PDF. */
    public byte[] generar(String tipo,ConsultaOperativa r) throws IOException,JRException {
        try(var logo=new ClassPathResource("static/img/hospital.png").getInputStream()){
            Map<String,Object> p=new HashMap<>();
            p.put("LOGO",logo);p.put("FECHA",r.getFecha());p.put("USUARIO",r.getUsuario());
            p.put("FILTROS",r.getFiltros());p.put("RESUMEN",r.getResumen());
            return JasperExportManager.exportReportToPdf(JasperFillManager.fillReport(
                plantilla(tipo),p,new JRMapCollectionDataSource(r.getFilas())));
        }
    }
}
