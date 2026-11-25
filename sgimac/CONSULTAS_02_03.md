# Consultas 02 y 03

## Abrir esta versión

1. Descomprimir el proyecto en una carpeta nueva y abrir `sgimac/pom.xml` en el IDE.
2. Mantener Java 17 o superior y recargar el proyecto Maven. Se conserva la versión de Spring Boot y JasperReports que ya usa el proyecto.
3. Revisar `src/main/resources/application.properties` con los datos de conexión de tu instalación. Usar la misma base que ya tiene los 50 registros cargados.
4. Ejecutar `SgimacApplication` como antes. Entrar a **Consultas**: arriba aparecen los accesos 01 · Vencimientos, 02 · Movimientos y 03 · Stock bajo.

**No es necesario volver a ejecutar el SQL de reinicio.** Esta actualización no cambia el esquema ni los datos. El archivo `04_consultas_movimientos_stock.sql` es opcional para mostrar SELECT en Workbench; no es una migración. Las consultas de la aplicación se ejecutan desde Java.

## Consulta 02 — Movimientos por insumo y periodo

Ruta: `/consultas/movimientos`.

Filtros: insumo (o todos), fecha inicial y final del movimiento, tipo (entradas y salidas, entrada o salida). Ambos días se incluyen completos, incluso operaciones del último día a las 23:59:59. La fecha inicial no puede superar la final. Los criterios se validan en el servidor; el tipo y los identificadores no se insertan directamente en el texto SQL.

La respuesta se abre en otra pantalla y muestra fecha/hora, insumo, lote, tipo, cantidad, unidad y usuario que registró el movimiento. El orden es del más reciente al más antiguo. El contador cuenta operaciones; no suma cajas, frascos y unidades entre sí. El usuario de cada fila registró la operación; el usuario del encabezado ejecutó la consulta.

A diferencia del reporte general de movimientos existente, esta consulta añade selección por insumo y presenta la respuesta en un formulario independiente, según la consigna. No sustituye el reporte del apartado 6.1.2.

Para probar: comenzar con todos los insumos, ambos tipos y un intervalo que incluya las fechas de tus movimientos. Se puede usar todo 2026 si los movimientos cargados están en ese año. Luego elegir un insumo que aparezca y filtrar SALIDA. El SELECT final del archivo SQL permite conocer MIN(fecha) y MAX(fecha) de la base actual. No se promete un número fijo de filas: depende de tus registros.

## Consulta 03 — Insumos con stock bajo por categoría

Ruta: `/consultas/stock`.

Filtro: categoría o todas. Se obtiene una fila por insumo, sumando `cantidad_actual` de TODOS sus lotes. La condición es estricta: stock total < stock mínimo. Si el stock coincide con el mínimo, no aparece. Los insumos sin lotes cuentan como cero; aparecen si tienen mínimo mayor que cero. El faltante es mínimo menos stock total.

Ejemplo: un insumo tiene mínimo 10 y dos lotes de 6 y 5. Su total es 11 y no debe aparecer como stock bajo. Si otro tiene lotes de 2 y 3 y mínimo 10, su total es 5 y el faltante es 5.

**Alcance:** el total es stock registrado e incluye los lotes vencidos. No representa stock apto para uso ni sustituye la Consulta 01 de vencimientos. Esta aclaración está en formulario, resultado y PDF. Las cantidades se expresan por unidad de cada insumo.

Usa JOIN, LEFT JOIN, SUM, COALESCE, GROUP BY y HAVING. No necesita alertas precargadas: calcula a partir del inventario actual. Para probar, consultar todas las categorías; después elegir una de las categorías que aparezca. Una respuesta sin coincidencias es válida y también se puede exportar.

## PDF y sesiones

Cada consulta conserva su propia copia en sesión. Descargar el PDF no vuelve a ejecutar SELECT y utiliza las mismas filas de la pantalla. Ejecutar la Consulta 03 no borra la respuesta de la 02. Una nueva ejecución de la misma consulta sustituye su copia anterior: enlaces antiguos o de otra sesión no permiten descargar ese resultado.

Los PDF son A4 horizontal e incluyen logo y nombre del hospital, usuario, fecha, filtros, tabla, contador y paginación. Plantillas: `src/main/resources/reports/consulta_movimientos.jrxml` y `consulta_stock.jrxml`, compatibles con JasperReports 6.x. Si se abren en Studio, conservar compatibilidad con la versión 6.21.5 del proyecto.

## Archivos para explicar al profesor

- `src/main/java/com/sgimac/controller/ConsultaMovimientosController.java`: formulario, validación, respuesta y descarga de la Consulta 02.
- `src/main/java/com/sgimac/controller/ConsultaStockController.java`: flujo equivalente de la Consulta 03.
- `src/main/java/com/sgimac/dao/ConsultaAdicionalDao.java`: SQL de ambas consultas y parámetros JDBC.
- `src/main/java/com/sgimac/reportes/OperativoPdfService.java`: compilar, llenar y exportar JasperReports. Se amplió su lista de plantillas.
- `src/main/java/com/sgimac/reportes/ConsultaOperativa.java`: copia del resultado (reutilizada).
- `src/main/resources/templates/consultas/`: formularios y respuestas; la Consulta 01 conserva sus rutas y ahora incluye accesos a las otras consultas.
- `src/main/resources/db/04_consultas_movimientos_stock.sql`: equivalente de lectura para demostrarlo en Workbench.

Se incorporaron comentarios Javadoc en el nuevo código. Para regenerar la documentación: `mvn javadoc:javadoc` desde la carpeta que contiene pom.xml; salida habitual `target/reports/apidocs` o `target/site/apidocs`, según la configuración del plugin.

## Pruebas y alcance de verificación

El proyecto se compiló con nivel de lenguaje Java 17 mediante ECJ sobre Java 21. Se ejecutaron 19 pruebas JUnit, incluidas las anteriores, sin fallos. Las nuevas comprueban suma de varios lotes, insumos sin lotes, igualdad con el mínimo, filtros combinados, último día completo, criterios inválidos, aislamiento de sesión, copia del resultado y PDF vacío o multipágina con logo.

El SQL se ejecutó en una base aislada H2 en modo MySQL para las pruebas; no se conectó a tu servidor MySQL. H2 se agregó solamente con alcance test: no cambia la base usada por la aplicación. Para repetir las pruebas en el IDE o una instalación Maven completa: `mvn test`. Se procesaron también las nuevas vistas Thymeleaf y se generaron ambos PDF para revisión visual.

## Capturas para los apartados 6.2.2 y 6.2.3

Por cada consulta: formulario con criterios, resultado, primera página del PDF y método SQL abierto en el IDE. Mantener los mismos criterios entre formulario, resultado y PDF.

Nombres sugeridos:
- `6_2_2_01_Formulario.png`, `6_2_2_02_Resultado.png`, `6_2_2_03_PDF.png`, `6_2_2_04_SQL.png`.
- `6_2_3_01_Formulario.png`, `6_2_3_02_Resultado.png`, `6_2_3_03_PDF.png`, `6_2_3_04_SQL.png`.

## Texto sugerido para el commit

Añade consultas de movimientos por insumo y stock bajo por categoría

Incorpora filtros, respuestas independientes y exportación PDF con JasperReports y logo del hospital. Agrega consultas SQL parametrizadas, cálculo de stock total por insumo, validación de criterios, documentación Javadoc y pruebas de las nuevas consultas.
