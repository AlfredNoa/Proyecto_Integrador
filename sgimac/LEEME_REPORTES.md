# SGIMAC — Reporte 01 de inventario por lote — revisión 2

## Qué contiene esta entrega

- Formulario de consulta de inventario: búsqueda por insumo o código de lote y filtro por estado de vencimiento.
- Resultado en pantalla, vista previa PDF dentro del formulario o en otra pestaña y descarga PDF mediante JasperReports Library 6.21.5.
- Plantilla editable `src/main/resources/reports/inventario_lotes.jrxml`, en A4 horizontal, con fecha, usuario, filtros, encabezados, páginas y conteos de lotes.
- Comentarios Javadoc en las clases nuevas y actualización de los comentarios del controlador de reportes.
- Datos de demostración consistentes y pruebas automatizadas para ejecutar con Maven.

Base de esta entrega: ZIP `Proyecto_Integrador-main (2)(1).zip` compartido por el usuario el 3 de noviembre de 2026. Se comparó su código fuente con el ZIP anterior con Javadoc y se conservó la corrección del comentario de MovimientoController. No se ha subido ningún cambio a GitHub. El paquete omite archivos compilados de `target` y preferencias locales del IDE; se regeneran al compilar o abrir el proyecto.

## 1. Preparar una copia de trabajo

Descomprime el ZIP en otra carpeta y abre la carpeta `sgimac`, donde está `pom.xml`, como proyecto Maven en tu IDE. Conserva tu proyecto anterior como respaldo. Requisitos: JDK 17 (incluye javac y javadoc), Maven y MySQL 8.0. NetBeans puede ejecutar Maven integrado; la primera compilación necesita Internet para descargar las dependencias.

## 2. Preparar la base de datos

**El archivo `02_reiniciar_datos_demo.sql` BORRA los datos de las doce tablas y carga datos ficticios. Haz un respaldo antes.** Detén la aplicación y cierra las sesiones existentes; los identificadores de usuarios se reemplazan.

- Base existente: conserva las tablas y ejecuta solamente `src/main/resources/db/02_reiniciar_datos_demo.sql` desde MySQL Workbench, como script completo.
- Base nueva: ejecuta primero `src/main/resources/db/sgimac_bd_fisica.sql` una sola vez; luego ejecuta el script de reinicio.
- El usuario SQL debe disponer de permisos para eliminar/insertar datos y crear procedimientos. La carga usa una transacción y revierte las filas si ocurre un error. Guarda SQL_SAFE_UPDATES, lo desactiva solo para la carga y restaura el valor anterior tanto al finalizar como en el manejador de errores. No modifica la configuración global ni desactiva las claves foráneas.
- No ejecutes solo una selección accidental del script. La llamada al procedimiento incluye la confirmación explícita del borrado.

Conteos al terminar la carga:

| Tabla | Filas |
|---|---:|
| rol | 3 |
| usuario | 20 |
| categoria | 20 |
| proveedor | 20 |
| ubicacion | 20 |
| insumo | 20 |
| lote | 20 |
| movimiento | 40 |
| alerta | 20 |
| notificacion | 20 |
| reporte | 20 |
| log_auditoria | 20 |

Los 3 roles corresponden a funciones reales del sistema. Los 40 movimientos son una entrada y una salida por cada lote; su diferencia coincide con el stock. La última consulta del script debe devolver cero inconsistencias. Las fechas se calculan respecto del día de carga: inicialmente habrá cinco lotes en cada estado de vencimiento. Las salidas de prueba son anteriores al vencimiento. El stock es variado. Hay 15 alertas de caducidad y 5 de stock bajo, cada una justificada por los datos. Los insumos 1 a 4 tienen dos lotes; los insumos 5 a 16 tienen uno; los insumos 17 a 20 están en el catálogo pero aún no tienen recepciones. Se mantienen exactamente 20 lotes. Todos los nombres de personas y empresas son ficticios.

Proveedores, teléfonos y RUC son ficticios; no son datos fiscales válidos. Los registros iniciales de reporte se identifican como `inventario_demo_simulado`: son datos de demostración, no documentos ya emitidos. Los registros iniciales de auditoría también indican que son simulados. Las nuevas descargas generadas desde la aplicación se registran como `inventario`.

Acceso de demostración: `aletor@sgimac.pe`, contraseña `admin123`. Todas las cuentas sembradas utilizan esa contraseña de prueba. No utilizar esta carga en una base real.

## 3. Compilar e iniciar

Revisa los datos de conexión en `src/main/resources/application.properties` y adáptalos a tu instalación local. Desde una terminal abierta en `sgimac`:

```sh
mvn clean test
mvn spring-boot:run
```

También puedes ejecutar esos objetivos desde Maven en tu IDE. Si aparecen errores de dependencias, conserva el mensaje completo para diagnosticarlo; no omitas las pruebas para dar la integración por validada.

Abre `http://localhost:8080`, inicia sesión y selecciona **Reportes**. Se abre el formulario de inventario. Pulsa **Consultar** para cargar los lotes y después **Exportar PDF**. La vista previa incrustada depende del visor PDF del navegador; también hay un enlace para abrirla en otra pestaña.

## 4. Comprobar la entrega

1. Sin filtros: comprobar 20 lotes y cinco por cada estado, si se consulta el mismo día de carga y el equipo y MySQL usan la misma fecha local.
2. Buscar `LOT-001`: verificar un solo lote. Buscar un texto inexistente: verificar el mensaje sin resultados, también en PDF.
3. Seleccionar cada estado y comparar las filas de la pantalla y el PDF. Revisar todas las páginas, acentos y columnas.
4. Confirmar que la primera descarga exitosa de una consulta añade un registro `inventario` y su auditoría. Las descargas posteriores de esa misma consulta no duplican el historial; una nueva consulta permite un nuevo registro. La vista previa GET no registra nada; el botón Exportar PDF envía POST. Si falla la base al registrar, el PDF se entrega y se escribe una advertencia en el log; la cabecera X-SGIMAC-Registro indica pendiente. Descargar de nuevo reintenta el registro. Esta deduplicación se realiza en la sesión de una única instancia de la aplicación, suficiente para la demostración local.
5. Abrir una consulta en una segunda pestaña: el enlace de la primera consulta debe rechazarse si ya fue reemplazada. Volver a pulsar Consultar.
6. Si cambia el stock después de consultar, el PDF conserva los valores de esa consulta. Consultar otra vez para obtener los datos nuevos.

Los estados son: vencido (fecha anterior a hoy), crítico (0 a 30 días), por vencer (31 a 90 días) y vigente (más de 90 días). No se suman cantidades expresadas en unidades distintas. El resumen cuenta lotes.

## Prueba del JAR empaquetado

El compilador ECJ/JDT 3.33.0 se incorpora como dependencia y `jasperreports.properties` selecciona JRJdtCompiler para Java 17. La plantilla se lee como recurso mediante InputStream y se compila una sola vez, al pedir el primer PDF; no requiere un archivo JRXML externo. Esta configuración reduce el riesgo señalado sobre el classpath, pero debe comprobarse con el JAR real.

```sh
mvn clean package
java -jar target/sgimac-1.0.0.jar
```

Con MySQL iniciado, repetir la consulta, la vista previa y la descarga desde ese proceso. No ejecutar el IDE y el JAR al mismo tiempo en el puerto 8080. Para la exposición, usar el modo de ejecución que haya pasado estas comprobaciones en el equipo.

## 5. Generar el nuevo Javadoc

En la carpeta del proyecto:

```sh
mvn javadoc:javadoc
```

Abre `target/site/apidocs/index.html`. El plugin Maven Javadoc está configurado con codificación UTF-8. El HTML se regenera desde el código; no se adjunta una versión antigua. Las clases nuevas documentan su propósito, parámetros, valores de retorno y excepciones relevantes.

## 6. Archivos principales del cambio

- `pom.xml`: motor JasperReports, fuentes, ECJ/JDT, pruebas y plugin Javadoc.
- `src/main/java/com/sgimac/controller/InventarioReporteController.java`: formulario y respuesta PDF.
- `src/main/java/com/sgimac/reportes/InventarioSnapshot.java`: filtros, estados y copia inmutable de los datos consultados.
- `src/main/java/com/sgimac/reportes/InventarioPdfService.java`: compilación de plantilla, llenado, PDF y registro de exportación.
- `src/main/resources/jasperreports.properties`: selección del compilador de expresiones JDT.
- `src/main/java/com/sgimac/reportes/package-info.java`: documentación del paquete.
- `src/main/java/com/sgimac/controller/ReporteController.java`: acceso al nuevo reporte; los otros tipos conservan su consulta en pantalla.
- `src/main/resources/templates/reportes/inventario.html`: formulario, tabla y vista previa.
- `src/main/resources/templates/reportes/lista.html`: enlaces actualizados.
- `src/main/resources/reports/inventario_lotes.jrxml`: diseño del reporte, compatible con JasperReports 6.x. Si se edita en Studio, conservar la compatibilidad 6.x.
- `src/main/resources/db/02_reiniciar_datos_demo.sql`: reinicio y carga de prueba.
- `src/test/java/com/sgimac/reportes/`: pruebas de límites, filtros, inmutabilidad de datos, PDF, paginación, aislamiento de sesión, descargas repetidas, fallos de registro, concurrencia y serialización de la consulta.

## 7. Qué se ha validado y qué falta ejecutar

Realizado: comparación con el ZIP reciente; revisión del flujo y Javadoc; XML bien formado y anchura de columnas; conteos, claves foráneas, saldos, fechas de salidas, múltiples lotes por insumo y justificación de las alertas mediante una adaptación temporal a SQLite; revisión visual del Word.

Pendiente en un equipo con las herramientas completas: compilación Maven, ejecución de las pruebas Java/JasperReports, generación real y revisión visual del PDF de inventario, generación completa de Javadoc, ejecución nativa del procedimiento MySQL y prueba de la aplicación conectada a esa base. El entorno utilizado no disponía de javac, javadoc, Maven ni MySQL y no permitió descargar las dependencias. La comprobación con SQLite no demuestra compatibilidad del procedimiento con MySQL.

Esta es una implementación preparada para la comprobación local, no una certificación de funcionamiento ya probado de extremo a extremo.

## 8. Documento académico

El Word separado contiene dos páginas de desarrollo (teoría y descripción del reporte) y una página de bibliografía con cinco fuentes. Las citas autor–año y referencias están redactadas para ISO 690; aún deben vincularse manualmente con Mendeley, según lo acordado. Las capturas de funcionamiento se incorporarán después de ejecutar y comprobar el sistema.
