# Cambios de la revisión 2

- MySQL Workbench: SQL_SAFE_UPDATES se conserva y restaura incluso si la transacción falla. No se fuerza a 1 si originalmente era 0.
- Datos de prueba: nombres ficticios naturales, 20 ubicaciones por estante, stock variado, cuatro insumos con dos lotes, 15 alertas de caducidad y 5 de stock bajo. Los historiales sembrados continúan identificados como simulados.
- Vista previa mediante GET sin escrituras. Descarga mediante POST con identificador aleatorio de la consulta validado contra la sesión.
- Una exportación registrada por consulta; sincronización para evitar duplicados por peticiones simultáneas en la misma sesión.
- Un fallo de acceso a datos o de transacción al registrar no impide entregar el PDF. Se registra una advertencia, se informa en X-SGIMAC-Registro y se permite reintentar en otra descarga.
- La consulta implementa Serializable y conserva la marca de registro al serializarse. Los datos consultados siguen siendo inmutables.
- ECJ/JDT explícito para compilar expresiones usando las clases de la aplicación. La comprobación con java -jar sigue pendiente.
- Javadoc e instrucciones actualizados; pruebas adicionales incluidas, pendientes de ejecución Maven.

Validación ejecutada: revisión de fuentes, XML y simulación relacional de los datos en SQLite. No equivale a ejecutar Java, JasperReports o MySQL. No se modifica la teoría del Word porque estos cambios no alteran su contenido conceptual.
