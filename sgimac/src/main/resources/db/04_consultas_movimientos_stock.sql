-- Consultas 02 y 03 para demostración en MySQL Workbench.
-- Solo lectura: no reinicia datos ni modifica tablas. Seleccionar primero sgimac.
-- Si el esquema tiene otro nombre, seleccionarlo en el panel SCHEMAS de Workbench.

-- Consulta 02: fechas de los movimientos, no del vencimiento de los lotes.
SET @insumo = 0; -- 0: todos; otro valor: id_insumo existente
SET @desde = '2026-11-01';
SET @hasta = '2026-11-30';
SET @tipo = 'TODOS'; -- TODOS, ENTRADA o SALIDA
SELECT m.fecha, i.nombre AS insumo, l.codigo_qr AS lote, m.tipo,
       m.cantidad, i.unidad_medida AS unidad, u.nombres AS usuario
FROM movimiento m
JOIN lote l ON l.id_lote = m.id_lote
JOIN insumo i ON i.id_insumo = l.id_insumo
JOIN usuario u ON u.id_usuario = m.id_usuario
WHERE m.fecha >= CAST(@desde AS DATETIME)
  AND m.fecha <= TIMESTAMP(@hasta, '23:59:59.999999')
  AND (@insumo = 0 OR i.id_insumo = @insumo)
  AND (@tipo = 'TODOS' OR m.tipo = @tipo)
ORDER BY m.fecha DESC, m.id_movimiento DESC;

-- Consulta 03: déficit calculado con existencias no vencidas (incluye el día actual).
-- Utilizable se refiere solo a fecha de vencimiento, no a condiciones físicas.
SET @categoria = 0;
SELECT i.nombre AS insumo, c.nombre AS categoria, i.unidad_medida AS unidad,
       COALESCE(SUM(l.cantidad_actual), 0) AS stock,
       COALESCE(SUM(CASE WHEN l.fecha_vencimiento < CURRENT_DATE THEN l.cantidad_actual ELSE 0 END), 0) AS vencido,
       COALESCE(SUM(CASE WHEN l.fecha_vencimiento >= CURRENT_DATE THEN l.cantidad_actual ELSE 0 END), 0) AS utilizable,
       i.stock_minimo AS minimo,
       i.stock_minimo - COALESCE(SUM(CASE WHEN l.fecha_vencimiento >= CURRENT_DATE THEN l.cantidad_actual ELSE 0 END), 0) AS faltante
FROM insumo i
JOIN categoria c ON c.id_categoria = i.id_categoria
LEFT JOIN lote l ON l.id_insumo = i.id_insumo
WHERE (@categoria = 0 OR i.id_categoria = @categoria)
GROUP BY i.id_insumo, i.nombre, c.id_categoria, c.nombre, i.unidad_medida, i.stock_minimo
HAVING utilizable < i.stock_minimo
ORDER BY c.nombre, i.nombre, i.id_insumo;

-- Apoyo opcional para elegir fechas de prueba con los datos realmente cargados.
SELECT MIN(fecha) AS primer_movimiento, MAX(fecha) AS ultimo_movimiento,
       COUNT(*) AS total_movimientos FROM movimiento;
