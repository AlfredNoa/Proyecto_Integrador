package com.sgimac.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utilidad para el hash de contraseñas (SHA-256).
 *
 * <p>En un sistema en producción se recomienda BCrypt; aquí se usa SHA-256 por simplicidad, ya que no
 * agrega dependencias adicionales al proyecto.
 *
 * <p>Es una clase de utilidad: no se puede instanciar y todos sus métodos son estáticos.
 *
 * @author Equipo SGIMAC
 * @version 1.0
 */
public final class PasswordUtil {

    /**
     * Constructor privado: evita que se creen instancias de la clase de utilidad.
     */
    private PasswordUtil() {
    }

    /**
     * Calcula el hash SHA-256 de un texto y lo devuelve en hexadecimal (64 caracteres en
     * minúsculas).
     *
     * @param textoPlano texto a convertir, normalmente la contraseña
     * @return hash SHA-256 en hexadecimal
     * @throws RuntimeException si el algoritmo SHA-256 o la codificación UTF-8 no están disponibles
     */
    public static String hash(String textoPlano) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(textoPlano.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException | java.io.UnsupportedEncodingException e) {
            throw new RuntimeException("No se pudo generar el hash de la contrasena", e);
        }
    }

    /**
     * Verifica si un texto plano corresponde a un hash guardado.
     *
     * @param textoPlano contraseña escrita por el usuario
     * @param hashGuardado hash almacenado en la base de datos
     * @return {@code true} si el hash del texto plano es igual al hash guardado
     */
    public static boolean coincide(String textoPlano, String hashGuardado) {
        return hash(textoPlano).equals(hashGuardado);
    }
}
