> Actualización: lea primero **LEEME_MEJORAS_INVENTARIO.md**. Sobre una base existente se aplica la migración 05, sin reiniciar datos. La Consulta 03 ahora calcula déficit con stock no vencido y las alertas admiten RESUELTA. Las instrucciones de carga que siguen son para instalaciones nuevas o reinicios deliberados.

# Carga de 100 registros — SGIMAC

La actualización conserva el código de las tres consultas y los tres reportes. Solo cambia el script de reinicio de datos y esta documentación.

## Ejecución en MySQL Workbench
1. Respaldar los datos si se desean conservar. El script BORRA y vuelve a poblar las 12 tablas de sgimac; no agrega registros sobre los existentes.
2. Detener la aplicación Java y cerrar sus sesiones.
3. Abrir SGIMAC_Reiniciar_Datos_100.sql, o src/main/resources/db/02_reiniciar_datos_demo.sql dentro del proyecto. Son el mismo archivo; ejecutar solo uno, completo.
4. Ejecutar el script completo en Workbench con una cuenta con permisos para crear procedimientos y modificar datos.
5. Verificar los conteos finales y que la última consulta devuelva cero filas (ninguna diferencia de stock).
6. Iniciar la aplicación e ingresar nuevamente.

No ejecutar sgimac_bd_fisica.sql sobre una base existente. Ese archivo solo sirve para crear la base inicialmente.

## Conteos esperados al terminar
- rol: 3 (los roles existentes, sin crear roles de relleno).
- movimiento: 200 (una entrada y una salida por lote).
- usuario, categoria, proveedor, ubicacion, insumo, lote, alerta, notificacion, reporte y log_auditoria: 100 cada una.

Total: 1203 registros. La aplicación puede generar nuevos registros de auditoría o reportes después de la carga.

## Acceso
Administrador: aletor@sgimac.pe
Contraseña: admin123 (también para las otras cuentas precargadas).

## Datos y consultas
Se conserva la carga anterior de 50 registros y se incorporan 50 adicionales por tabla, salvo movimientos (100 adicionales) y roles (sin cambios). No conserva cambios manuales hechos en la base desde aquella carga.
Hay nombres y descripciones específicas, correos abreviados, RUC de 11 dígitos y teléfonos de 9 dígitos. Son identidades y operaciones sintéticas; los números no acreditan titularidad ni validez fiscal.
Las fechas se calculan respecto al día de ejecución. Para movimientos nuevos se puede consultar desde 60 días antes hasta el día de carga. En vencimientos se pueden buscar fechas entre 30 días antes y un año después.
Los insumos 17–20 continúan sin lotes, y los primeros cuatro insumos mantienen más de un lote. Esto conserva casos útiles para las consultas. La consulta de stock bajo suma todos los lotes, incluidos vencidos.
El historial precargado de reportes y auditoría forma parte del escenario académico: no genera archivos PDF por sí mismo.

## Integridad y alcance de comprobación
El procedimiento valida conteos y saldo por lote antes de confirmar; ante un error SQL revierte la carga y restaura SQL_SAFE_UPDATES. No desactiva claves foráneas.
Se verificaron los datos mediante ejecución de los INSERT en SQLite con esquema equivalente, incluyendo claves foráneas, valores únicos, conteos, saldos, fechas y límites de longitud. Esta comprobación no sustituye la ejecución del procedimiento en MySQL Workbench; en este entorno no hay servidor MySQL.
