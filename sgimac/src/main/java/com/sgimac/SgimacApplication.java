package com.sgimac;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal de SGIMAC, el Sistema de Gestión de Insumos Médicos y Alertas de Caducidad del
 * Hospital José Agurto Tello.
 *
 * <p>La anotación {@code @SpringBootApplication} activa la configuración automática de Spring Boot y
 * el escaneo de componentes del paquete {@code com.sgimac}.
 *
 * @author Equipo SGIMAC
 * @version 1.0
 */
@SpringBootApplication
public class SgimacApplication {

    /**
     * Punto de entrada: arranca la aplicación Spring Boot (servidor web, conexión a MySQL y
     * controladores).
     *
     * @param args argumentos de línea de comandos
     */
    public static void main(String[] args) {
        SpringApplication.run(SgimacApplication.class, args);
    }
}
