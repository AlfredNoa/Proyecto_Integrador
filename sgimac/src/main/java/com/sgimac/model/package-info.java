/**
 * Modelo (MVC): una clase por cada una de las 12 tablas de la base de datos {@code sgimac}
 * (Rol, Usuario, Categoria, Proveedor, Ubicacion, Insumo, Lote, Movimiento, Alerta, Notificacion,
 * Reporte y LogAuditoria).
 *
 * <p>Son clases simples (POJO) con atributos privados y métodos <i>get</i>/<i>set</i>. Algunos atributos
 * (por ejemplo, {@code nombreInsumo}) no son columnas propias de la tabla: se completan con un {@code JOIN}
 * para mostrarlos en las vistas.
 */
package com.sgimac.model;
