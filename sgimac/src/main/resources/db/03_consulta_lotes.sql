-- Consulta 01 para demostrar la misma operación en MySQL Workbench.
-- Solo lectura de tablas: no borra datos ni cambia la estructura.
USE sgimac;
SET @insumo = 0; -- 0 = todos; otro valor = id_insumo seleccionado.
SET @desde = '2026-11-01';
SET @hasta = '2026-12-31';

SELECT i.nombre AS insumo, l.codigo_qr AS lote, l.fecha_vencimiento,
       l.cantidad_actual AS stock, i.unidad_medida AS unidad,
       u.nombre AS ubicacion, p.razon_social AS proveedor
FROM lote l
JOIN insumo i ON i.id_insumo = l.id_insumo
JOIN ubicacion u ON u.id_ubicacion = l.id_ubicacion
JOIN proveedor p ON p.id_proveedor = l.id_proveedor
WHERE l.fecha_vencimiento BETWEEN @desde AND @hasta
  AND (@insumo = 0 OR l.id_insumo = @insumo)
ORDER BY l.fecha_vencimiento, l.id_lote;
-- En Java los criterios se enlazan mediante parámetros ?, no por concatenación.
