package com.sgimac.config;

import com.sgimac.dao.RolDao;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Al iniciar la aplicacion imprime en consola si la conexion a MySQL funciona.
 */
@Component
public class ConexionRunner implements CommandLineRunner {

    private final RolDao rolDao;

    public ConexionRunner(RolDao rolDao) {
        this.rolDao = rolDao;
    }

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
