/**
 * Implementaciones JDBC de las interfaces de {@link com.sgimac.dao}.
 *
 * <p>Cada clase usa un {@code JdbcTemplate} de Spring con consultas SQL parametrizadas (sin concatenar
 * datos del usuario) y un {@code RowMapper} que convierte cada fila en un objeto del modelo.
 */
package com.sgimac.dao.impl;
