package com.sgimac.model;

/**
 * Modelo (MVC) que representa la tabla {@code insumo} de la base de datos {@code sgimac}. Es un
 * insumo médico del catálogo del hospital.
 *
 * <p>El insumo describe el producto; sus existencias físicas se controlan por lotes (ver
 * {@link Lote}).
 *
 * @author Equipo SGIMAC
 * @version 1.0
 */
public class Insumo {

    /** El identificador del insumo. Clave primaria autoincremental. */
    private int idInsumo;
    /** El identificador de la categoría del insumo. Clave foránea hacia {@code categoria}. */
    private int idCategoria;
    /**
     * El nombre de la categoría del insumo. Se completa con un {@code JOIN} y no es una columna de
     * la tabla {@code insumo}.
     */
    private String nombreCategoria;
    /** El código del insumo. Es único en la tabla. */
    private String codigo;
    /** El nombre del insumo. */
    private String nombre;
    /** La unidad de medida en que se cuenta el insumo. */
    private String unidadMedida;
    /**
     * El stock mínimo del insumo. Si el stock vigente queda por debajo de este valor se genera una
     * alerta de stock bajo. No puede ser negativo.
     */
    private int stockMinimo;

    /**
     * Crea un objeto vacío; los atributos se completan luego con los métodos <i>set</i> o desde el
     * {@code RowMapper} del DAO.
     */
    public Insumo() {
    }

    /**
     * Devuelve el identificador del insumo.
     *
     * @return el identificador del insumo
     */
    public int getIdInsumo() {
        return idInsumo;
    }

    /**
     * Establece el identificador del insumo.
     *
     * @param idInsumo el identificador del insumo
     */
    public void setIdInsumo(int idInsumo) {
        this.idInsumo = idInsumo;
    }

    /**
     * Devuelve el identificador de la categoría del insumo.
     *
     * @return el identificador de la categoría del insumo
     */
    public int getIdCategoria() {
        return idCategoria;
    }

    /**
     * Establece el identificador de la categoría del insumo.
     *
     * @param idCategoria el identificador de la categoría del insumo
     */
    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    /**
     * Devuelve el nombre de la categoría del insumo.
     *
     * @return el nombre de la categoría del insumo
     */
    public String getNombreCategoria() {
        return nombreCategoria;
    }

    /**
     * Establece el nombre de la categoría del insumo.
     *
     * @param nombreCategoria el nombre de la categoría del insumo
     */
    public void setNombreCategoria(String nombreCategoria) {
        this.nombreCategoria = nombreCategoria;
    }

    /**
     * Devuelve el código del insumo.
     *
     * @return el código del insumo
     */
    public String getCodigo() {
        return codigo;
    }

    /**
     * Establece el código del insumo.
     *
     * @param codigo el código del insumo
     */
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    /**
     * Devuelve el nombre del insumo.
     *
     * @return el nombre del insumo
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre del insumo.
     *
     * @param nombre el nombre del insumo
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Devuelve la unidad de medida en que se cuenta el insumo.
     *
     * @return la unidad de medida en que se cuenta el insumo
     */
    public String getUnidadMedida() {
        return unidadMedida;
    }

    /**
     * Establece la unidad de medida en que se cuenta el insumo.
     *
     * @param unidadMedida la unidad de medida en que se cuenta el insumo
     */
    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    /**
     * Devuelve el stock mínimo del insumo.
     *
     * @return el stock mínimo del insumo
     */
    public int getStockMinimo() {
        return stockMinimo;
    }

    /**
     * Establece el stock mínimo del insumo.
     *
     * @param stockMinimo el stock mínimo del insumo
     */
    public void setStockMinimo(int stockMinimo) {
        this.stockMinimo = stockMinimo;
    }
}
