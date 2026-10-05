# SGIMAC — Proyecto Spring Boot

## Requisitos
- JDK 17 o superior
- MySQL 8 en ejecución
- Un IDE con soporte Maven (IntelliJ IDEA, Eclipse/STS o VS Code con Extension Pack for Java)

## Pasos
1. En MySQL, ejecutar el script `src/main/resources/db/sgimac_bd_fisica.sql`
   (crea la base `sgimac`, las 12 tablas y los 3 roles iniciales).
2. En `src/main/resources/application.properties`, cambiar el usuario y la contraseña de MySQL.
3. Abrir la carpeta del proyecto en el IDE como proyecto Maven y ejecutar `SgimacApplication`.
   (O desde consola, si tienen Maven: `mvn spring-boot:run`)
4. En la consola debe aparecer: `[SGIMAC] Conexion a MySQL OK. Roles encontrados: 3`
5. Abrir http://localhost:8080/login e iniciar sesión con:
   - Correo: `admin@sgimac.pe`
   - Contraseña: `admin123`
6. Desde el panel se puede entrar a "Gestionar usuarios" (http://localhost:8080/usuarios)
   para ver, crear y activar/desactivar usuarios.

## Estructura
- `model/`       Modelo (MVC): clases de las entidades (Rol, Usuario)
- `dao/`         Patrón DAO: interfaces de acceso a datos
- `dao/impl/`    Implementaciones con JdbcTemplate
- `controller/`  Controlador (MVC): login, panel, gestión de usuarios
- `util/`        Utilidades (hash de contraseñas)
- `config/`      Configuración y verificación de conexión
- `resources/templates/`  Vistas Thymeleaf (login, panel, usuarios/)

## Avance de entidades (Modelo + DAO + Controlador + Vista)
Completas las 12: Rol, Usuario, Categoria, Insumo, Proveedor, Ubicacion,
Lote, Movimiento, Alerta, Notificacion, Reporte, LogAuditoria.

## Sobre Reporte
`/reportes` muestra en pantalla los tres reportes del diagrama de procesos
(inventario por lote, próximos a vencer, movimientos), con pestañas. El
botón "Generar reporte" registra en la tabla `reporte` quién lo generó,
de qué tipo y en qué formato (PDF o Excel). No se implementó la
exportación real a archivo PDF/Excel (requeriría una librería adicional,
como Apache POI o iText, fuera del alcance de este entregable); si el
profesor lo pide, se puede agregar después sobre esta misma base.

## Sobre LogAuditoria
`/auditoria` (pensada para el Administrador) muestra el historial de
acciones. Por ahora se registran automáticamente: el inicio de sesión
(LoginController), el registro de movimientos (MovimientoController) y
la generación de reportes (ReporteController). Para auditar más acciones
(crear insumo, crear usuario, etc.), se replica el mismo patrón:
inyectar `LogAuditoriaDao` en el controlador y llamar a
`logAuditoriaDao.registrar(idUsuario, "acción", "entidad")`.

## Sobre Movimiento (registro de entradas/salidas)
`MovimientoController` implementa la validación del diagrama secuencial 6.7:
1. Si es SALIDA y el lote está vencido → bloquea con mensaje de error.
2. Si es SALIDA y no hay stock suficiente → bloquea con mensaje de error.
3. Si es válido → registra el movimiento y actualiza `cantidad_actual` del lote.
4. Evalúa alertas: si el lote quedó vencido o próximo a vencer, o si el
   insumo quedó bajo su stock mínimo, genera una alerta (sin duplicar una
   ya pendiente). Si el nivel es ALTO (vencido o ≤30 días), notifica a
   todos los usuarios con rol "Personal Médico".

Para probar el bloqueo por vencimiento: en /movimientos, elegir el lote
`L-2026-001` (Paracetamol, vencido) y tipo Salida.
Para probar el bloqueo por stock: pedir una cantidad mayor a la disponible
en cualquier lote.
Para probar la generación de alertas y notificaciones: registrar una
salida válida del lote `L-2026-003` (Amoxicilina, crítico, vence en 18
días) y luego revisar /alertas y, con el usuario médico, /notificaciones.

## Usuarios de prueba
- Administrador: `admin@sgimac.pe` / `admin123`
- Personal Médico (recibe notificaciones): `medico@sgimac.pe` / `admin123`
