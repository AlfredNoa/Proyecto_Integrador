package com.sgimac.config;

import com.sgimac.dao.RolDao;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Al iniciar la aplicación imprime en consola si la conexión a MySQL funciona, listando los roles
 * de la base de datos.
 *
 * <p>Es la verificación automática de la conexión: si todo está bien muestra «Conexion a MySQL OK» y
 * la cantidad de roles encontrados.
 *
 * @author Equipo SGIMAC
 * @version 1.0
 */
@Component
public class ConexionRunner implements CommandLineRunner {

    /** DAO de rol (inyectado por el constructor). */
    private final RolDao rolDao;

    /**
     * Crea el verificador con el DAO de roles que Spring inyecta.
     *
     * @param rolDao DAO de roles
     */
    public ConexionRunner(RolDao rolDao) {
        this.rolDao = rolDao;
    }

    /**
     * Se ejecuta una vez, justo después de que Spring Boot arranca. Intenta listar los roles e
     * imprime el resultado; si falla, imprime el mensaje del error en lugar de detener la
     * aplicación.
     *
     * @param args argumentos de línea de comandos (no se usan)
     */
    @Override
    public void run(String... args) {
        try {
            int total = rolDao.listar().size();
            System.out.println("[SGIMAC] Conexion a MySQL OK. Roles encontrados: " + total);
        } catch (Exception e) {
            System.out.println("[SGIMAC] No se pudo conectar a MySQL: " + e.getMessage());
        }
    }
}
