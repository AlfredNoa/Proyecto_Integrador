# Movimientos y seguimiento de alertas
Con el proyecto en ejecución, inicie sesión y abra **Reportes**. El selector permite alternar entre inventario (01), movimientos (02) y alertas (03). No es necesario modificar ni recargar MySQL.

**Movimientos:** deje las fechas vacías para ver todo el historial; ambos límites son opcionales e inclusivos. Seleccione Entrada o Salida para acotar el resultado. Cada fila indica la cantidad en la unidad del insumo. El resumen cuenta operaciones.

**Alertas:** combine tipo, nivel y estado. El responsable aparece como «—» si no hay usuario de atención registrado. Un filtro sin coincidencias genera un informe vacío identificado como tal.

En ambos casos pulse **Consultar**. Use **Abrir vista previa PDF**, despliegue el PDF dentro del formulario o pulse **Exportar PDF**. Compruebe que el escudo, el filtro y la cantidad de registros se muestran en el PDF y que el detalle coincide con el formulario. Una consulta nueva sustituye la anterior para ese informe y sesión.

Plantillas: src/main/resources/reports/movimientos.jrxml y alertas.jrxml. El parámetro LOGO recibe el PNG desde static/img/hospital.png. Las consultas están en ConsultaOperativaDao.java y la generación del PDF en OperativoPdfService.java.
Desde la carpeta del pom.xml: mvn clean test y mvn spring-boot:run. Abra http://localhost:8080/login. Los comentarios de las clases y sus métodos principales pueden incluirse en el Javadoc con mvn javadoc:javadoc.
