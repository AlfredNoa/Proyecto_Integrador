package com.sgimac.model;

/**
 * Modelo (MVC) que representa la tabla {@code ubicacion} de la base de datos {@code sgimac}. Es el
 * lugar físico (por ejemplo, un almacén) donde se guarda un lote.
 *
 * @author Equipo SGIMAC
 * @version 1.0
 */
public class Ubicacion {

    /** El identificador de la ubicación. Clave primaria autoincremental. */
    private int idUbicacion;
    /** El nombre de la ubicación. */
    private String nombre;
    /** El tipo de ubicación. */
    private String tipo;

    /**
     * Crea un objeto vacío; los atributos se completan luego con los métodos <i>set</i> o desde el
     * {@code RowMapper} del DAO.
     */
    public Ubicacion() {
    }

    /**
     * Devuelve el identificador de la ubicación.
     *
     * @return el identificador de la ubicación
     */
    public int getIdUbicacion() {
        return idUbicacion;
    }

    /**
     * Establece el identificador de la ubicación.
     *
     * @param idUbicacion el identificador de la ubicación
     */
    public void setIdUbicacion(int idUbicacion) {
        this.idUbicacion = idUbicacion;
    }

    /**
     * Devuelve el nombre de la ubicación.
     *
     * @return el nombre de la ubicación
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre de la ubicación.
     *
     * @param nombre el nombre de la ubicación
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Devuelve el tipo de ubicación.
     *
     * @return el tipo de ubicación
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * Establece el tipo de ubicación.
     *
     * @param tipo el tipo de ubicación
     */
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
