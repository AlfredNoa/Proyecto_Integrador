package com.sgimac.config;
import com.sgimac.service.AlertasService;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.*;
/** Revisión periódica mientras la aplicación está encendida, además de la revisión al consultar. */
@Configuration @EnableScheduling
public class RevisionAlertas {
 private final AlertasService alertas;
 public RevisionAlertas(AlertasService alertas){this.alertas=alertas;}
 /** Detecta también el paso de días sin movimientos. Intervalo configurable en milisegundos. */
 @Scheduled(initialDelay=60000,fixedDelayString="${sgimac.alertas.intervalo-ms:60000}")
 public void revisar(){try{alertas.actualizar();}catch(Exception e){org.slf4j.LoggerFactory.getLogger(getClass()).error("No se actualizaron alertas. Compruebe conexión y migración 05.",e);}}
}
