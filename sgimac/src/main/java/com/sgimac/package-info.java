/**
 * SGIMAC: Sistema de Gestión de Insumos Médicos y Alertas de Caducidad del Hospital José Agurto Tello.
 *
 * <p>La aplicación sigue el patrón <b>MVC</b> (modelo, vista y controlador) y accede a MySQL mediante el
 * patrón <b>DAO</b>:
 *
 * <ul>
 *   <li>{@link com.sgimac.model}: clases del modelo (una por tabla de la base de datos).
 *   <li>{@link com.sgimac.dao}: interfaces DAO (contratos de acceso a datos).
 *   <li>{@link com.sgimac.dao.impl}: implementaciones JDBC de los DAO con {@code JdbcTemplate}.
 *   <li>{@link com.sgimac.controller}: controladores Spring MVC que atienden las rutas web.
 *   <li>{@link com.sgimac.config}: configuración y verificación de la conexión a MySQL.
 *   <li>{@link com.sgimac.util}: utilidades (hash de contraseñas).
 * </ul>
 *
 * <p>Las vistas son plantillas Thymeleaf ubicadas en {@code src/main/resources/templates}.
 */
package com.sgimac;
