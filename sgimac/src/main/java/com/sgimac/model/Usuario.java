package com.sgimac.model;

/**
 * Modelo (MVC) que representa la tabla {@code usuario} de la base de datos {@code sgimac}. Es una
 * persona con acceso al sistema, con un rol asignado.
 *
 * <p>La contraseña nunca se guarda en texto plano: se almacena su hash SHA-256 (ver
 * {@code PasswordUtil}).
 *
 * @author Equipo SGIMAC
 * @version 1.0
 */
public class Usuario {

    /** El identificador del usuario. Clave primaria autoincremental. */
    private int idUsuario;
    /** El identificador del rol del usuario. Clave foránea hacia {@code rol}. */
    private int idRol;
    /**
     * El nombre del rol del usuario. Se completa con un {@code JOIN} y no es una columna de la
     * tabla {@code usuario}.
     */
    private String nombreRol;
    /** Los nombres del usuario. */
    private String nombres;
    /** El correo electrónico del usuario. Es único y se usa para iniciar sesión. */
    private String correo;
    /** El hash SHA-256 de la contraseña del usuario. */
    private String contrasenaHash;
    /** El indicador de usuario activo. Un usuario inactivo no puede iniciar sesión. */
    private boolean activo;

    /**
     * Crea un objeto vacío; los atributos se completan luego con los métodos <i>set</i> o desde el
     * {@code RowMapper} del DAO.
     */
    public Usuario() {
    }

    /**
     * Crea un usuario con todos sus datos propios (sin el nombre del rol, que viene del
     * {@code JOIN}).
     *
     * @param idUsuario identificador del usuario
     * @param idRol identificador del rol
     * @param nombres nombres del usuario
     * @param correo correo electrónico
     * @param contrasenaHash hash SHA-256 de la contraseña
     * @param activo {@code true} si el usuario está activo
     */
    public Usuario(int idUsuario, int idRol, String nombres, String correo,
                   String contrasenaHash, boolean activo) {
        this.idUsuario = idUsuario;
        this.idRol = idRol;
        this.nombres = nombres;
        this.correo = correo;
        this.contrasenaHash = contrasenaHash;
        this.activo = activo;
    }

    /**
     * Devuelve el identificador del usuario.
     *
     * @return el identificador del usuario
     */
    public int getIdUsuario() {
        return idUsuario;
    }

    /**
     * Establece el identificador del usuario.
     *
     * @param idUsuario el identificador del usuario
     */
    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    /**
     * Devuelve el identificador del rol del usuario.
     *
     * @return el identificador del rol del usuario
     */
    public int getIdRol() {
        return idRol;
    }

    /**
     * Establece el identificador del rol del usuario.
     *
     * @param idRol el identificador del rol del usuario
     */
    public void setIdRol(int idRol) {
        this.idRol = idRol;
    }

    /**
     * Devuelve el nombre del rol del usuario.
     *
     * @return el nombre del rol del usuario
     */
    public String getNombreRol() {
        return nombreRol;
    }

    /**
     * Establece el nombre del rol del usuario.
     *
     * @param nombreRol el nombre del rol del usuario
     */
    public void setNombreRol(String nombreRol) {
        this.nombreRol = nombreRol;
    }

    /**
     * Devuelve los nombres del usuario.
     *
     * @return los nombres del usuario
     */
    public String getNombres() {
        return nombres;
    }

    /**
     * Establece los nombres del usuario.
     *
     * @param nombres los nombres del usuario
     */
    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    /**
     * Devuelve el correo electrónico del usuario.
     *
     * @return el correo electrónico del usuario
     */
    public String getCorreo() {
        return correo;
    }

    /**
     * Establece el correo electrónico del usuario.
     *
     * @param correo el correo electrónico del usuario
     */
    public void setCorreo(String correo) {
        this.correo = correo;
    }

    /**
     * Devuelve el hash SHA-256 de la contraseña del usuario.
     *
     * @return el hash SHA-256 de la contraseña del usuario
     */
    public String getContrasenaHash() {
        return contrasenaHash;
    }

    /**
     * Establece el hash SHA-256 de la contraseña del usuario.
     *
     * @param contrasenaHash el hash SHA-256 de la contraseña del usuario
     */
    public void setContrasenaHash(String contrasenaHash) {
        this.contrasenaHash = contrasenaHash;
    }

    /**
     * Devuelve el indicador de usuario activo.
     *
     * @return el indicador de usuario activo
     */
    public boolean isActivo() {
        return activo;
    }

    /**
     * Establece el indicador de usuario activo.
     *
     * @param activo el indicador de usuario activo
     */
    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
