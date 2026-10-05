package com.sgimac.model;

/**
 * Modelo (MVC): representa la tabla "usuario".
 */
public class Usuario {

    private int idUsuario;
    private int idRol;
    private String nombreRol; // se completa al hacer join con "rol", no es columna propia
    private String nombres;
    private String correo;
    private String contrasenaHash;
    private boolean activo;

    public Usuario() {
    }

    public Usuario(int idUsuario, int idRol, String nombres, String correo,
                   String contrasenaHash, boolean activo) {
        this.idUsuario = idUsuario;
        this.idRol = idRol;
        this.nombres = nombres;
        this.correo = correo;
        this.contrasenaHash = contrasenaHash;
        this.activo = activo;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdRol() {
        return idRol;
    }

    public void setIdRol(int idRol) {
        this.idRol = idRol;
    }

    public String getNombreRol() {
        return nombreRol;
    }

    public void setNombreRol(String nombreRol) {
        this.nombreRol = nombreRol;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasenaHash() {
        return contrasenaHash;
    }

    public void setContrasenaHash(String contrasenaHash) {
        this.contrasenaHash = contrasenaHash;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
