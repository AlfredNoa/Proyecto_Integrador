package com.sgimac.model;

/**
 * Modelo (MVC) que representa la tabla {@code rol} de la base de datos {@code sgimac}. Define el
 * perfil de acceso de los usuarios del sistema.
 *
 * <p>Los tres roles iniciales (cargados por el script SQL) son Personal de Almacén, Personal Médico y
 * Administrador.
 *
 * @author Equipo SGIMAC
 * @version 1.0
 */
public class Rol {

    /** El identificador del rol. Clave primaria autoincremental. */
    private int idRol;
    /** El nombre del rol. Es único en la tabla. */
    private String nombre;
    /** La descripción de las funciones del rol. */
    private String descripcion;

    /**
     * Crea un objeto vacío; los atributos se completan luego con los métodos <i>set</i> o desde el
     * {@code RowMapper} del DAO.
     */
    public Rol() {
    }

    /**
     * Crea un rol con todos sus datos.
     *
     * @param idRol identificador del rol
     * @param nombre nombre del rol
     * @param descripcion descripción de las funciones del rol
     */
    public Rol(int idRol, String nombre, String descripcion) {
        this.idRol = idRol;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    /**
     * Devuelve el identificador del rol.
     *
     * @return el identificador del rol
     */
    public int getIdRol() {
        return idRol;
    }

    /**
     * Establece el identificador del rol.
     *
     * @param idRol el identificador del rol
     */
    public void setIdRol(int idRol) {
        this.idRol = idRol;
    }

    /**
     * Devuelve el nombre del rol.
     *
     * @return el nombre del rol
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre del rol.
     *
     * @param nombre el nombre del rol
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Devuelve la descripción de las funciones del rol.
     *
     * @return la descripción de las funciones del rol
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Establece la descripción de las funciones del rol.
     *
     * @param descripcion la descripción de las funciones del rol
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
