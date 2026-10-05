package com.sgimac.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utilidad para el hash de contrasenas (SHA-256).
 * En un sistema en produccion se recomienda BCrypt; aqui se usa SHA-256
 * por simplicidad, ya que no agrega dependencias adicionales al proyecto.
 */
public final class PasswordUtil {

    private PasswordUtil() {
    }

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

    public static boolean coincide(String textoPlano, String hashGuardado) {
        return hash(textoPlano).equals(hashGuardado);
    }
}
