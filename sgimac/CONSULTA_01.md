# Consulta 01 — Lotes por insumo y vencimiento

## Abrir la aplicación
1. Detener la instancia anterior.
2. Extraer este proyecto en una carpeta nueva y abrir sgimac/pom.xml.
3. Mantener los datos de conexión de su instalación en application.properties.
4. Iniciar la aplicación como antes. Entrar al menú Consultas.
No se requiere ejecutar ningún SQL de reinicio ni modificar las tablas existentes.

## Funcionamiento
- Formulario de entrada: insumo (o todos) y fechas de vencimiento desde/hasta.
- Botón Consultar lotes: abre una pantalla de respuesta independiente.
- Se incluyen ambas fechas y los lotes con stock cero, ordenados por vencimiento.
- Resultado: insumo, lote, vencimiento, stock, unidad, ubicación y proveedor.
- Descargar PDF: usa JasperReports y el logo del hospital. Exporta exactamente la copia de la consulta mostrada, sin volver a leer datos actualizados.
- Nueva consulta: vuelve al formulario. Una nueva consulta sustituye la copia anterior de esa sesión.
- Si no hay coincidencias, se informa en pantalla y en el PDF.
- La descarga de consultas no se añade al historial de los tres reportes anteriores.

## Archivos para explicar al profesor
- src/main/java/com/sgimac/dao/ConsultaLotesDao.java: SQL con SELECT, JOIN, BETWEEN, parámetro opcional por insumo y ORDER BY; parámetros JDBC.
- src/main/java/com/sgimac/controller/ConsultaLotesController.java: valida, consulta, conserva resultado y coordina las dos pantallas y el PDF.
- src/main/java/com/sgimac/reportes/ConsultaOperativa.java: copia de filas y criterios.
- src/main/java/com/sgimac/reportes/OperativoPdfService.java: compila, llena y exporta Jasper.
- src/main/resources/reports/consulta_lotes.jrxml: plantilla editable en Jaspersoft Studio compatible con JasperReports 6.x.
- src/main/resources/templates/consultas/formulario.html y resultado.html: pantallas.
- src/test/java/com/sgimac/reportes/ConsultaLotesTest.java: validación, sesión, PDF vacío y 50 filas.
Se añadieron comentarios Javadoc. La plantilla se compila desde recursos del proyecto.

## Evidencias para 6.2.1
1. 6_2_1_01_Formulario.png: criterios antes de consultar.
2. 6_2_1_02_Resultado.png: respuesta, filtros y primeras filas.
3. 6_2_1_03_PDF.png: PDF de esa misma consulta.
4. 6_2_1_04_SQL.png: método consultar de ConsultaLotesDao.java.
Elegir un intervalo que contenga fechas de sus lotes. Las fechas predeterminadas son hoy a 90 días, por lo que no necesariamente mostrarán los 50 lotes.

## Validación realizada
- Compilación y compilación de pruebas con dependencias reales; JDK 21 del entorno usando source/target 17. Se conserva Java 17 en el pom.xml del proyecto.
- 13 pruebas JUnit aprobadas, incluidos los tres reportes anteriores y la Consulta 01. Se ejecutaron con JUnit Console y mock-maker-subclass por restricciones de la herramienta de pruebas del entorno.
- Las dos vistas procesadas con Thymeleaf y PDF Jasper real de 50 filas generado en cinco páginas.
- SQL comprobado en SQLite para fechas límite, orden, selección de insumo, stock cero y resultado vacío. Pendiente prueba integral en la instalación MySQL del usuario.
- Ajuste de una prueba previa de inventario: los parámetros entregados a Jasper ahora usan un HashMap modificable.
- Ejemplo opcional para mostrar en Workbench: src/main/resources/db/03_consulta_lotes.sql. No es necesario ejecutarlo para habilitar el módulo.
