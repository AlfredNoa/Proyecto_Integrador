# Actualización de inventario — SGIMAC

## Instalar sobre la base de datos existente

1. Detenga la aplicación anterior y haga un respaldo de `sgimac` desde MySQL Workbench.
2. Extraiga este proyecto en una carpeta nueva. Abra su `pom.xml` en IntelliJ como proyecto Maven.
3. Abra y ejecute completo `src/main/resources/db/05_mejoras_inventario.sql` en Workbench. También se entrega por separado como `SGIMAC_Actualizar_Inventario.sql`. Es el mismo archivo: basta ejecutar uno. Si su base tiene otro nombre, ajuste `USE sgimac`.
4. Revise la conexión a MySQL en `src/main/resources/application.properties` y conserve las credenciales que ya le funcionaban.
5. Recargue Maven y ejecute la clase principal como en la versión anterior. Se requiere Java 17 o posterior.

**No ejecute el script 02 para esta actualización:** reinicia los datos. La migración 05 conserva sus registros y permite alertas por insumo y el estado RESUELTA. La aplicación nueva necesita esa migración antes de arrancar. Las cuentas y contraseñas existentes no cambian.

Para una instalación vacía se mantiene el esquema completo actualizado y la carga de 100 registros. No vuelva a crear el esquema sobre la base que desea conservar.

## Qué cambia

### 1. Stock registrado, vencido y utilizable

En Insumos y Consulta 03 aparecen las tres cantidades. Registrado es la suma de todos los lotes; vencido corresponde a fechas anteriores a la fecha actual; utilizable incluye hoy y fechas posteriores. El faltante es mínimo menos utilizable, y solo se muestran insumos con déficit. Un insumo sin lotes cuenta como cero. No se suman cantidades de insumos con unidades diferentes.

“Utilizable” evalúa exclusivamente la fecha: no verifica conservación, cuarentena o calidad física. El PDF de la Consulta 03 y su SQL de demostración se actualizaron para usar el mismo criterio. Por ello, las cifras de sus capturas anteriores pueden variar.

### 2. Sugerencia FEFO

Abra Movimientos, elija un insumo y la cantidad, y pulse Calcular sugerencia. Se proponen primero los lotes no vencidos con fecha de vencimiento más próxima. Los agotados se excluyen; se informa si no alcanza el stock.

“Usar este lote” llena el formulario, pero no registra ni reserva existencias. Revise y confirme cada salida. Después de registrar una, vuelva a calcular la cantidad pendiente. El formulario vuelve a validar vencimiento y stock. Registrar y refrescar la página no repite automáticamente el envío del formulario.

### 3. Alertas coherentes

La caducidad se controla por lote con existencias, y el stock bajo por insumo, incluido uno sin lotes. Las revisiones repetidas no crean una alerta nueva mientras continúa la misma condición. Las alertas antiguas duplicadas se conservan como resueltas y queda una activa por condición.

- PENDIENTE: requiere revisión.
- ATENDIDA: una persona revisó el caso; la condición puede seguir activa.
- RESUELTA: la condición desapareció o la alerta se consolidó con otra equivalente.

Al aumentar una alerta de caducidad de nivel medio a alto vuelve a pendiente. Si una condición desaparece y luego reaparece, se crea un nuevo registro para conservar el historial. Las alertas altas notifican al personal médico activo.

Se revisan al abrir los listados y el panel, al consultar el reporte de alertas, al registrar movimientos y al crear insumos/lotes. También se revisan cada 60 segundos mientras la aplicación está encendida. La primera revisión periódica ocurre después de 60 segundos. Los cambios no actualizan solos una página ya abierta: recargue para verlos.

El número de alertas y notificaciones puede cambiar respecto a la carga inicial de 100: ahora refleja las condiciones reales y conserva historial. Los insumos, proveedores y demás datos no se borran.

### 4. Paginación y búsqueda

Insumos, Lotes, Movimientos y Alertas permiten búsqueda por insumo/código, filtros propios y páginas de 10, 20 o 50 filas. La paginación se realiza en SQL y conserva los filtros. Los demás catálogos mantienen su comportamiento anterior.

### 6. Panel principal

Incluye indicadores de insumos, lotes vencidos con stock, lotes que vencen entre hoy y 90 días, insumos con déficit utilizable y alertas activas. Las tarjetas enlazan a los listados correspondientes. Muestra hasta ocho próximos vencimientos, ocho déficits y seis movimientos recientes.

## Archivos principales para explicar el código

- `dao/StockDao.java`: cálculo compartido de existencias.
- `service/FefoService.java`: propuesta de lotes.
- `service/AlertasService.java`: conciliación y atención de alertas.
- `config/RevisionAlertas.java`: revisión periódica.
- `dao/ListadosDao.java`: filtros y paginación SQL.
- `service/PanelService.java`: indicadores.
- `controller/MovimientoController.java`: validaciones y registro transaccional.
- `src/main/resources/reports/consulta_stock.jrxml`: plantilla de Consulta 03.
- `src/main/resources/db/04_consultas_movimientos_stock.sql`: SQL de demostración actualizado.

Las clases nuevas y los métodos modificados incluyen documentación para Javadoc. Se mantienen las plantillas y el logo del hospital.

## Prueba rápida después de instalar

1. Entre con su cuenta habitual y abra el panel.
2. Revise un insumo con lotes vencidos: registrado debe ser vencido más utilizable.
3. En Movimientos, solicite una cantidad que requiera dos lotes y revise su orden FEFO; compruebe también una cantidad superior a lo disponible.
4. Abra Alertas varias veces: no deben aparecer duplicados activos por la misma condición. Marcar Atendida no debe ocultar una carencia todavía existente.
5. Pruebe búsqueda, estados y páginas en los cuatro listados.
6. Genere la Consulta 03 y su PDF: compruebe las tres columnas de existencias y el faltante.

## Verificación realizada

Compilación con destino Java 17 y 28 pruebas automatizadas superadas sobre H2 en modo MySQL. Se comprueban cálculos, límites de fechas, FEFO, concurrencia de revisiones, alertas, paginación y exportación PDF; también se procesan las vistas Thymeleaf con datos y sin resultados. La migración debe comprobarse en su instancia MySQL: este entorno no dispone de ese servidor. No se pudo completar la prueba visual en un navegador por falta del ejecutable de Chromium.

## Ajuste de textos de la interfaz

El acceso del panel se denomina «Registrar salida de insumos». En Movimientos se muestra «Sugerir lotes por vencimiento». FEFO se mantiene como término técnico en la documentación y en el código. Este ajuste de textos no requiere ejecutar SQL adicional si ya aplicó la migración 05.
