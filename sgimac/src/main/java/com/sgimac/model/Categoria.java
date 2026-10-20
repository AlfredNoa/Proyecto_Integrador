package com.sgimac.model;

/**
 * Modelo (MVC) que representa la tabla {@code categoria} de la base de datos {@code sgimac}. Es la
 * clasificación a la que pertenece cada insumo médico.
 *
 * @author Equipo SGIMAC
 * @version 1.0
 */
public class Categoria {

    /** El identificador de la categoría. Clave primaria autoincremental. */
    private int idCategoria;
    /** El nombre de la categoría. Es único en la tabla. */
    private String nombre;
    /** La descripción de la categoría. */
    private String descripcion;

    /**
     * Crea un objeto vacío; los atributos se completan luego con los métodos <i>set</i> o desde el
     * {@code RowMapper} del DAO.
     */
    public Categoria() {
    }

    /**
     * Crea una categoría con todos sus datos.
     *
     * @param idCategoria identificador de la categoría
     * @param nombre nombre de la categoría
     * @param descripcion descripción de la categoría
     */
    public Categoria(int idCategoria, String nombre, String descripcion) {
        this.idCategoria = idCategoria;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    /**
     * Devuelve el identificador de la categoría.
     *
     * @return el identificador de la categoría
     */
    public int getIdCategoria() {
        return idCategoria;
    }

    /**
     * Establece el identificador de la categoría.
     *
     * @param idCategoria el identificador de la categoría
     */
    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    /**
     * Devuelve el nombre de la categoría.
     *
     * @return el nombre de la categoría
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre de la categoría.
     *
     * @param nombre el nombre de la categoría
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Devuelve la descripción de la categoría.
     *
     * @return la descripción de la categoría
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Establece la descripción de la categoría.
     *
     * @param descripcion la descripción de la categoría
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
