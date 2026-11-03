-- SGIMAC: reinicio de DATOS DE PRUEBA. MySQL 8.0. No ejecutar en producción.
-- Borra el contenido de las 12 tablas. Hacer respaldo antes y cerrar sesiones de la app.
-- No modifica el esquema ni desactiva claves foráneas. Requiere permiso CREATE ROUTINE.
-- Si falla una inserción o validación, el manejador revierte la transacción completa.
-- Base inicial: src/main/resources/db/sgimac_bd_fisica.sql, ejecutar solo si la BD no existe.
USE sgimac;
SET NAMES utf8mb4;
DROP PROCEDURE IF EXISTS cargar_demo_reportes;
DELIMITER $$
CREATE PROCEDURE cargar_demo_reportes(IN confirmacion VARCHAR(60))
BEGIN
    DECLARE v_hoy DATE DEFAULT CURDATE();
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;
    IF confirmacion <> 'BORRAR_DATOS_Y_CARGAR_DEMO' OR confirmacion IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Confirmacion de borrado no valida';
    END IF;
    START TRANSACTION;
    DELETE FROM notificacion;
    DELETE FROM alerta;
    DELETE FROM movimiento;
    DELETE FROM reporte;
    DELETE FROM log_auditoria;
    DELETE FROM lote;
    DELETE FROM insumo;
    DELETE FROM proveedor;
    DELETE FROM ubicacion;
    DELETE FROM categoria;
    DELETE FROM usuario;
    DELETE FROM rol;
    INSERT INTO rol (id_rol,nombre,descripcion) VALUES
        (1,'Personal de Almacén','Control de insumos, lotes y movimientos'),
        (2,'Personal Médico','Consulta y uso de insumos'),
        (3,'Administrador','Administración del sistema');
    INSERT INTO usuario (id_usuario,id_rol,nombres,correo,contrasena_hash,activo) VALUES
        (1,3,'Administrador Demo','admin@sgimac.pe','240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9',TRUE),
        (2,2,'Usuario Demo 2','medico@sgimac.pe','240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9',TRUE),
        (3,1,'Usuario Demo 3','demo03@example.invalid','240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9',TRUE),
        (4,1,'Usuario Demo 4','demo04@example.invalid','240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9',TRUE),
        (5,1,'Usuario Demo 5','demo05@example.invalid','240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9',TRUE),
        (6,1,'Usuario Demo 6','demo06@example.invalid','240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9',TRUE),
        (7,1,'Usuario Demo 7','demo07@example.invalid','240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9',TRUE),
        (8,1,'Usuario Demo 8','demo08@example.invalid','240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9',TRUE),
        (9,1,'Usuario Demo 9','demo09@example.invalid','240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9',TRUE),
        (10,1,'Usuario Demo 10','demo10@example.invalid','240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9',TRUE),
        (11,1,'Usuario Demo 11','demo11@example.invalid','240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9',TRUE),
        (12,1,'Usuario Demo 12','demo12@example.invalid','240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9',TRUE),
        (13,1,'Usuario Demo 13','demo13@example.invalid','240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9',TRUE),
        (14,1,'Usuario Demo 14','demo14@example.invalid','240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9',TRUE),
        (15,1,'Usuario Demo 15','demo15@example.invalid','240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9',TRUE),
        (16,1,'Usuario Demo 16','demo16@example.invalid','240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9',TRUE),
        (17,1,'Usuario Demo 17','demo17@example.invalid','240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9',TRUE),
        (18,1,'Usuario Demo 18','demo18@example.invalid','240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9',TRUE),
        (19,1,'Usuario Demo 19','demo19@example.invalid','240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9',TRUE),
        (20,1,'Usuario Demo 20','demo20@example.invalid','240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9',TRUE);
    INSERT INTO categoria (id_categoria,nombre,descripcion) VALUES
        (1,'Analgésicos','Categoría de inventario de prueba'),
        (2,'Antibióticos','Categoría de inventario de prueba'),
        (3,'Antiinflamatorios','Categoría de inventario de prueba'),
        (4,'Antisépticos','Categoría de inventario de prueba'),
        (5,'Soluciones intravenosas','Categoría de inventario de prueba'),
        (6,'Guantes','Categoría de inventario de prueba'),
        (7,'Jeringas','Categoría de inventario de prueba'),
        (8,'Gasas','Categoría de inventario de prueba'),
        (9,'Vendajes','Categoría de inventario de prueba'),
        (10,'Catéteres','Categoría de inventario de prueba'),
        (11,'Suturas','Categoría de inventario de prueba'),
        (12,'Mascarillas','Categoría de inventario de prueba'),
        (13,'Protección ocular','Categoría de inventario de prueba'),
        (14,'Batas','Categoría de inventario de prueba'),
        (15,'Campos quirúrgicos','Categoría de inventario de prueba'),
        (16,'Recolección de muestras','Categoría de inventario de prueba'),
        (17,'Diagnóstico','Categoría de inventario de prueba'),
        (18,'Oxigenoterapia','Categoría de inventario de prueba'),
        (19,'Cuidado de heridas','Categoría de inventario de prueba'),
        (20,'Desinfección','Categoría de inventario de prueba');
    INSERT INTO proveedor (id_proveedor,razon_social,ruc,telefono) VALUES
        (1,'Proveedor Demo 01','00000000001','000000000'),
        (2,'Proveedor Demo 02','00000000002','000000000'),
        (3,'Proveedor Demo 03','00000000003','000000000'),
        (4,'Proveedor Demo 04','00000000004','000000000'),
        (5,'Proveedor Demo 05','00000000005','000000000'),
        (6,'Proveedor Demo 06','00000000006','000000000'),
        (7,'Proveedor Demo 07','00000000007','000000000'),
        (8,'Proveedor Demo 08','00000000008','000000000'),
        (9,'Proveedor Demo 09','00000000009','000000000'),
        (10,'Proveedor Demo 10','00000000010','000000000'),
        (11,'Proveedor Demo 11','00000000011','000000000'),
        (12,'Proveedor Demo 12','00000000012','000000000'),
        (13,'Proveedor Demo 13','00000000013','000000000'),
        (14,'Proveedor Demo 14','00000000014','000000000'),
        (15,'Proveedor Demo 15','00000000015','000000000'),
        (16,'Proveedor Demo 16','00000000016','000000000'),
        (17,'Proveedor Demo 17','00000000017','000000000'),
        (18,'Proveedor Demo 18','00000000018','000000000'),
        (19,'Proveedor Demo 19','00000000019','000000000'),
        (20,'Proveedor Demo 20','00000000020','000000000');
    INSERT INTO ubicacion (id_ubicacion,nombre,tipo) VALUES
        (1,'Estante Demo 01','Almacén de prueba'),
        (2,'Estante Demo 02','Almacén de prueba'),
        (3,'Estante Demo 03','Almacén de prueba'),
        (4,'Estante Demo 04','Almacén de prueba'),
        (5,'Estante Demo 05','Almacén de prueba'),
        (6,'Estante Demo 06','Almacén de prueba'),
        (7,'Estante Demo 07','Almacén de prueba'),
        (8,'Estante Demo 08','Almacén de prueba'),
        (9,'Estante Demo 09','Almacén de prueba'),
        (10,'Estante Demo 10','Almacén de prueba'),
        (11,'Estante Demo 11','Almacén de prueba'),
        (12,'Estante Demo 12','Almacén de prueba'),
        (13,'Estante Demo 13','Almacén de prueba'),
        (14,'Estante Demo 14','Almacén de prueba'),
        (15,'Estante Demo 15','Almacén de prueba'),
        (16,'Estante Demo 16','Almacén de prueba'),
        (17,'Estante Demo 17','Almacén de prueba'),
        (18,'Estante Demo 18','Almacén de prueba'),
        (19,'Estante Demo 19','Almacén de prueba'),
        (20,'Estante Demo 20','Almacén de prueba');
    INSERT INTO insumo (id_insumo,id_categoria,codigo,nombre,unidad_medida,stock_minimo) VALUES
        (1,1,'INS-001','Paracetamol 500 mg','Tabletas',50),
        (2,2,'INS-002','Amoxicilina 500 mg','Cápsulas',50),
        (3,3,'INS-003','Ibuprofeno 400 mg','Tabletas',50),
        (4,4,'INS-004','Alcohol 70 % 1 L','Frascos',50),
        (5,5,'INS-005','Suero fisiológico 0.9 % 1 L','Frascos',50),
        (6,6,'INS-006','Guantes de examen talla M','Cajas',50),
        (7,7,'INS-007','Jeringa descartable 5 ml','Unidades',50),
        (8,8,'INS-008','Gasa estéril 10 x 10 cm','Paquetes',50),
        (9,9,'INS-009','Venda elástica 10 cm','Rollos',50),
        (10,10,'INS-010','Catéter periférico 22 G','Unidades',50),
        (11,11,'INS-011','Sutura nylon 3-0','Unidades',50),
        (12,12,'INS-012','Mascarilla quirúrgica','Cajas',50),
        (13,13,'INS-013','Lentes protectores','Unidades',50),
        (14,14,'INS-014','Bata descartable','Unidades',50),
        (15,15,'INS-015','Campo quirúrgico estéril','Paquetes',50),
        (16,16,'INS-016','Tubo de muestra EDTA','Unidades',50),
        (17,17,'INS-017','Tira reactiva glucosa','Cajas',50),
        (18,18,'INS-018','Cánula nasal adulto','Unidades',50),
        (19,19,'INS-019','Apósito estéril','Unidades',50),
        (20,20,'INS-020','Desinfectante de superficies 1 L','Frascos',50);
    INSERT INTO lote (id_lote,id_insumo,id_proveedor,id_ubicacion,codigo_qr,fecha_vencimiento,cantidad_actual) VALUES
        (1,1,1,1,'DEMO-L-001',DATE_ADD(v_hoy, INTERVAL -12 DAY),21),
        (2,2,2,2,'DEMO-L-002',DATE_ADD(v_hoy, INTERVAL -5 DAY),22),
        (3,3,3,3,'DEMO-L-003',DATE_ADD(v_hoy, INTERVAL -1 DAY),23),
        (4,4,4,4,'DEMO-L-004',DATE_ADD(v_hoy, INTERVAL -30 DAY),24),
        (5,5,5,5,'DEMO-L-005',DATE_ADD(v_hoy, INTERVAL -2 DAY),25),
        (6,6,6,6,'DEMO-L-006',DATE_ADD(v_hoy, INTERVAL 0 DAY),26),
        (7,7,7,7,'DEMO-L-007',DATE_ADD(v_hoy, INTERVAL 1 DAY),27),
        (8,8,8,8,'DEMO-L-008',DATE_ADD(v_hoy, INTERVAL 18 DAY),28),
        (9,9,9,9,'DEMO-L-009',DATE_ADD(v_hoy, INTERVAL 29 DAY),29),
        (10,10,10,10,'DEMO-L-010',DATE_ADD(v_hoy, INTERVAL 30 DAY),30),
        (11,11,11,11,'DEMO-L-011',DATE_ADD(v_hoy, INTERVAL 31 DAY),31),
        (12,12,12,12,'DEMO-L-012',DATE_ADD(v_hoy, INTERVAL 45 DAY),32),
        (13,13,13,13,'DEMO-L-013',DATE_ADD(v_hoy, INTERVAL 55 DAY),33),
        (14,14,14,14,'DEMO-L-014',DATE_ADD(v_hoy, INTERVAL 89 DAY),34),
        (15,15,15,15,'DEMO-L-015',DATE_ADD(v_hoy, INTERVAL 90 DAY),35),
        (16,16,16,16,'DEMO-L-016',DATE_ADD(v_hoy, INTERVAL 91 DAY),36),
        (17,17,17,17,'DEMO-L-017',DATE_ADD(v_hoy, INTERVAL 120 DAY),37),
        (18,18,18,18,'DEMO-L-018',DATE_ADD(v_hoy, INTERVAL 180 DAY),38),
        (19,19,19,19,'DEMO-L-019',DATE_ADD(v_hoy, INTERVAL 210 DAY),39),
        (20,20,20,20,'DEMO-L-020',DATE_ADD(v_hoy, INTERVAL 400 DAY),40);
    INSERT INTO movimiento (id_movimiento,id_lote,id_usuario,tipo,cantidad,fecha) VALUES
        (1,1,1,'ENTRADA',31,TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 60 DAY), '08:00:00')),
        (2,1,1,'SALIDA',10,TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 45 DAY), '09:00:00')),
        (3,2,2,'ENTRADA',32,TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 60 DAY), '08:00:00')),
        (4,2,2,'SALIDA',10,TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 45 DAY), '09:00:00')),
        (5,3,3,'ENTRADA',33,TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 60 DAY), '08:00:00')),
        (6,3,3,'SALIDA',10,TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 45 DAY), '09:00:00')),
        (7,4,4,'ENTRADA',34,TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 60 DAY), '08:00:00')),
        (8,4,4,'SALIDA',10,TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 45 DAY), '09:00:00')),
        (9,5,5,'ENTRADA',35,TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 60 DAY), '08:00:00')),
        (10,5,5,'SALIDA',10,TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 45 DAY), '09:00:00')),
        (11,6,6,'ENTRADA',36,TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 60 DAY), '08:00:00')),
        (12,6,6,'SALIDA',10,TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 45 DAY), '09:00:00')),
        (13,7,7,'ENTRADA',37,TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 60 DAY), '08:00:00')),
        (14,7,7,'SALIDA',10,TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 45 DAY), '09:00:00')),
        (15,8,8,'ENTRADA',38,TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 60 DAY), '08:00:00')),
        (16,8,8,'SALIDA',10,TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 45 DAY), '09:00:00')),
        (17,9,9,'ENTRADA',39,TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 60 DAY), '08:00:00')),
        (18,9,9,'SALIDA',10,TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 45 DAY), '09:00:00')),
        (19,10,10,'ENTRADA',40,TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 60 DAY), '08:00:00')),
        (20,10,10,'SALIDA',10,TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 45 DAY), '09:00:00')),
        (21,11,11,'ENTRADA',41,TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 60 DAY), '08:00:00')),
        (22,11,11,'SALIDA',10,TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 45 DAY), '09:00:00')),
        (23,12,12,'ENTRADA',42,TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 60 DAY), '08:00:00')),
        (24,12,12,'SALIDA',10,TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 45 DAY), '09:00:00')),
        (25,13,13,'ENTRADA',43,TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 60 DAY), '08:00:00')),
        (26,13,13,'SALIDA',10,TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 45 DAY), '09:00:00')),
        (27,14,14,'ENTRADA',44,TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 60 DAY), '08:00:00')),
        (28,14,14,'SALIDA',10,TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 45 DAY), '09:00:00')),
        (29,15,15,'ENTRADA',45,TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 60 DAY), '08:00:00')),
        (30,15,15,'SALIDA',10,TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 45 DAY), '09:00:00')),
        (31,16,16,'ENTRADA',46,TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 60 DAY), '08:00:00')),
        (32,16,16,'SALIDA',10,TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 45 DAY), '09:00:00')),
        (33,17,17,'ENTRADA',47,TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 60 DAY), '08:00:00')),
        (34,17,17,'SALIDA',10,TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 45 DAY), '09:00:00')),
        (35,18,18,'ENTRADA',48,TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 60 DAY), '08:00:00')),
        (36,18,18,'SALIDA',10,TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 45 DAY), '09:00:00')),
        (37,19,19,'ENTRADA',49,TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 60 DAY), '08:00:00')),
        (38,19,19,'SALIDA',10,TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 45 DAY), '09:00:00')),
        (39,20,20,'ENTRADA',50,TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 60 DAY), '08:00:00')),
        (40,20,20,'SALIDA',10,TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 45 DAY), '09:00:00'));
    INSERT INTO alerta (id_alerta,id_lote,id_usuario_atiende,tipo,nivel,estado,fecha_generacion) VALUES
        (1,1,NULL,'STOCK_BAJO','MEDIO','PENDIENTE',TIMESTAMP(v_hoy)),
        (2,2,NULL,'STOCK_BAJO','MEDIO','PENDIENTE',TIMESTAMP(v_hoy)),
        (3,3,NULL,'STOCK_BAJO','MEDIO','PENDIENTE',TIMESTAMP(v_hoy)),
        (4,4,NULL,'STOCK_BAJO','MEDIO','PENDIENTE',TIMESTAMP(v_hoy)),
        (5,5,NULL,'STOCK_BAJO','MEDIO','PENDIENTE',TIMESTAMP(v_hoy)),
        (6,6,NULL,'STOCK_BAJO','MEDIO','PENDIENTE',TIMESTAMP(v_hoy)),
        (7,7,NULL,'STOCK_BAJO','MEDIO','PENDIENTE',TIMESTAMP(v_hoy)),
        (8,8,NULL,'STOCK_BAJO','MEDIO','PENDIENTE',TIMESTAMP(v_hoy)),
        (9,9,NULL,'STOCK_BAJO','MEDIO','PENDIENTE',TIMESTAMP(v_hoy)),
        (10,10,NULL,'STOCK_BAJO','MEDIO','PENDIENTE',TIMESTAMP(v_hoy)),
        (11,11,NULL,'STOCK_BAJO','MEDIO','PENDIENTE',TIMESTAMP(v_hoy)),
        (12,12,NULL,'STOCK_BAJO','MEDIO','PENDIENTE',TIMESTAMP(v_hoy)),
        (13,13,NULL,'STOCK_BAJO','MEDIO','PENDIENTE',TIMESTAMP(v_hoy)),
        (14,14,NULL,'STOCK_BAJO','MEDIO','PENDIENTE',TIMESTAMP(v_hoy)),
        (15,15,NULL,'STOCK_BAJO','MEDIO','PENDIENTE',TIMESTAMP(v_hoy)),
        (16,16,NULL,'STOCK_BAJO','MEDIO','PENDIENTE',TIMESTAMP(v_hoy)),
        (17,17,NULL,'STOCK_BAJO','MEDIO','PENDIENTE',TIMESTAMP(v_hoy)),
        (18,18,NULL,'STOCK_BAJO','MEDIO','PENDIENTE',TIMESTAMP(v_hoy)),
        (19,19,NULL,'STOCK_BAJO','MEDIO','PENDIENTE',TIMESTAMP(v_hoy)),
        (20,20,NULL,'STOCK_BAJO','MEDIO','PENDIENTE',TIMESTAMP(v_hoy));
    INSERT INTO notificacion (id_notificacion,id_alerta,id_usuario,mensaje,leida,fecha_envio) VALUES
        (1,1,1,'DEMO: revisar existencias del lote DEMO-L-001; stock por debajo del mínimo.',FALSE,TIMESTAMP(v_hoy)),
        (2,2,2,'DEMO: revisar existencias del lote DEMO-L-002; stock por debajo del mínimo.',FALSE,TIMESTAMP(v_hoy)),
        (3,3,3,'DEMO: revisar existencias del lote DEMO-L-003; stock por debajo del mínimo.',FALSE,TIMESTAMP(v_hoy)),
        (4,4,4,'DEMO: revisar existencias del lote DEMO-L-004; stock por debajo del mínimo.',FALSE,TIMESTAMP(v_hoy)),
        (5,5,5,'DEMO: revisar existencias del lote DEMO-L-005; stock por debajo del mínimo.',FALSE,TIMESTAMP(v_hoy)),
        (6,6,6,'DEMO: revisar existencias del lote DEMO-L-006; stock por debajo del mínimo.',FALSE,TIMESTAMP(v_hoy)),
        (7,7,7,'DEMO: revisar existencias del lote DEMO-L-007; stock por debajo del mínimo.',FALSE,TIMESTAMP(v_hoy)),
        (8,8,8,'DEMO: revisar existencias del lote DEMO-L-008; stock por debajo del mínimo.',FALSE,TIMESTAMP(v_hoy)),
        (9,9,9,'DEMO: revisar existencias del lote DEMO-L-009; stock por debajo del mínimo.',FALSE,TIMESTAMP(v_hoy)),
        (10,10,10,'DEMO: revisar existencias del lote DEMO-L-010; stock por debajo del mínimo.',FALSE,TIMESTAMP(v_hoy)),
        (11,11,11,'DEMO: revisar existencias del lote DEMO-L-011; stock por debajo del mínimo.',FALSE,TIMESTAMP(v_hoy)),
        (12,12,12,'DEMO: revisar existencias del lote DEMO-L-012; stock por debajo del mínimo.',FALSE,TIMESTAMP(v_hoy)),
        (13,13,13,'DEMO: revisar existencias del lote DEMO-L-013; stock por debajo del mínimo.',FALSE,TIMESTAMP(v_hoy)),
        (14,14,14,'DEMO: revisar existencias del lote DEMO-L-014; stock por debajo del mínimo.',FALSE,TIMESTAMP(v_hoy)),
        (15,15,15,'DEMO: revisar existencias del lote DEMO-L-015; stock por debajo del mínimo.',FALSE,TIMESTAMP(v_hoy)),
        (16,16,16,'DEMO: revisar existencias del lote DEMO-L-016; stock por debajo del mínimo.',FALSE,TIMESTAMP(v_hoy)),
        (17,17,17,'DEMO: revisar existencias del lote DEMO-L-017; stock por debajo del mínimo.',FALSE,TIMESTAMP(v_hoy)),
        (18,18,18,'DEMO: revisar existencias del lote DEMO-L-018; stock por debajo del mínimo.',FALSE,TIMESTAMP(v_hoy)),
        (19,19,19,'DEMO: revisar existencias del lote DEMO-L-019; stock por debajo del mínimo.',FALSE,TIMESTAMP(v_hoy)),
        (20,20,20,'DEMO: revisar existencias del lote DEMO-L-020; stock por debajo del mínimo.',FALSE,TIMESTAMP(v_hoy));
    INSERT INTO reporte (id_reporte,id_usuario,tipo,formato,fecha_generacion) VALUES
        (1,1,'inventario_demo_simulado','PDF',TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 1 DAY))),
        (2,2,'inventario_demo_simulado','PDF',TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 1 DAY))),
        (3,3,'inventario_demo_simulado','PDF',TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 1 DAY))),
        (4,4,'inventario_demo_simulado','PDF',TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 1 DAY))),
        (5,5,'inventario_demo_simulado','PDF',TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 1 DAY))),
        (6,6,'inventario_demo_simulado','PDF',TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 1 DAY))),
        (7,7,'inventario_demo_simulado','PDF',TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 1 DAY))),
        (8,8,'inventario_demo_simulado','PDF',TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 1 DAY))),
        (9,9,'inventario_demo_simulado','PDF',TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 1 DAY))),
        (10,10,'inventario_demo_simulado','PDF',TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 1 DAY))),
        (11,11,'inventario_demo_simulado','PDF',TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 1 DAY))),
        (12,12,'inventario_demo_simulado','PDF',TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 1 DAY))),
        (13,13,'inventario_demo_simulado','PDF',TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 1 DAY))),
        (14,14,'inventario_demo_simulado','PDF',TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 1 DAY))),
        (15,15,'inventario_demo_simulado','PDF',TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 1 DAY))),
        (16,16,'inventario_demo_simulado','PDF',TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 1 DAY))),
        (17,17,'inventario_demo_simulado','PDF',TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 1 DAY))),
        (18,18,'inventario_demo_simulado','PDF',TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 1 DAY))),
        (19,19,'inventario_demo_simulado','PDF',TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 1 DAY))),
        (20,20,'inventario_demo_simulado','PDF',TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 1 DAY)));
    INSERT INTO log_auditoria (id_log,id_usuario,accion,entidad,fecha) VALUES
        (1,1,'Carga de datos DEMO (evento simulado)','lote:1',TIMESTAMP(v_hoy)),
        (2,2,'Carga de datos DEMO (evento simulado)','lote:2',TIMESTAMP(v_hoy)),
        (3,3,'Carga de datos DEMO (evento simulado)','lote:3',TIMESTAMP(v_hoy)),
        (4,4,'Carga de datos DEMO (evento simulado)','lote:4',TIMESTAMP(v_hoy)),
        (5,5,'Carga de datos DEMO (evento simulado)','lote:5',TIMESTAMP(v_hoy)),
        (6,6,'Carga de datos DEMO (evento simulado)','lote:6',TIMESTAMP(v_hoy)),
        (7,7,'Carga de datos DEMO (evento simulado)','lote:7',TIMESTAMP(v_hoy)),
        (8,8,'Carga de datos DEMO (evento simulado)','lote:8',TIMESTAMP(v_hoy)),
        (9,9,'Carga de datos DEMO (evento simulado)','lote:9',TIMESTAMP(v_hoy)),
        (10,10,'Carga de datos DEMO (evento simulado)','lote:10',TIMESTAMP(v_hoy)),
        (11,11,'Carga de datos DEMO (evento simulado)','lote:11',TIMESTAMP(v_hoy)),
        (12,12,'Carga de datos DEMO (evento simulado)','lote:12',TIMESTAMP(v_hoy)),
        (13,13,'Carga de datos DEMO (evento simulado)','lote:13',TIMESTAMP(v_hoy)),
        (14,14,'Carga de datos DEMO (evento simulado)','lote:14',TIMESTAMP(v_hoy)),
        (15,15,'Carga de datos DEMO (evento simulado)','lote:15',TIMESTAMP(v_hoy)),
        (16,16,'Carga de datos DEMO (evento simulado)','lote:16',TIMESTAMP(v_hoy)),
        (17,17,'Carga de datos DEMO (evento simulado)','lote:17',TIMESTAMP(v_hoy)),
        (18,18,'Carga de datos DEMO (evento simulado)','lote:18',TIMESTAMP(v_hoy)),
        (19,19,'Carga de datos DEMO (evento simulado)','lote:19',TIMESTAMP(v_hoy)),
        (20,20,'Carga de datos DEMO (evento simulado)','lote:20',TIMESTAMP(v_hoy));
    IF EXISTS (
        SELECT l.id_lote FROM lote l LEFT JOIN movimiento m ON m.id_lote=l.id_lote
        GROUP BY l.id_lote, l.cantidad_actual
        HAVING l.cantidad_actual <> COALESCE(SUM(CASE WHEN m.tipo='ENTRADA' THEN m.cantidad ELSE -m.cantidad END),0)
    ) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Stock inconsistente con movimientos';
    END IF;
    COMMIT;
END$$
DELIMITER ;
-- Esta llamada es la que confirma y ejecuta el borrado.
CALL cargar_demo_reportes('BORRAR_DATOS_Y_CARGAR_DEMO');
DROP PROCEDURE cargar_demo_reportes;

-- Conteo esperado: rol=3; movimiento=40; las otras diez tablas=20.

SELECT 'rol' AS tabla, COUNT(*) AS registros FROM rol
UNION ALL
SELECT 'usuario' AS tabla, COUNT(*) AS registros FROM usuario
UNION ALL
SELECT 'categoria' AS tabla, COUNT(*) AS registros FROM categoria
UNION ALL
SELECT 'proveedor' AS tabla, COUNT(*) AS registros FROM proveedor
UNION ALL
SELECT 'ubicacion' AS tabla, COUNT(*) AS registros FROM ubicacion
UNION ALL
SELECT 'insumo' AS tabla, COUNT(*) AS registros FROM insumo
UNION ALL
SELECT 'lote' AS tabla, COUNT(*) AS registros FROM lote
UNION ALL
SELECT 'movimiento' AS tabla, COUNT(*) AS registros FROM movimiento
UNION ALL
SELECT 'alerta' AS tabla, COUNT(*) AS registros FROM alerta
UNION ALL
SELECT 'notificacion' AS tabla, COUNT(*) AS registros FROM notificacion
UNION ALL
SELECT 'reporte' AS tabla, COUNT(*) AS registros FROM reporte
UNION ALL
SELECT 'log_auditoria' AS tabla, COUNT(*) AS registros FROM log_auditoria;
-- Esta comprobación debe devolver CERO filas.
SELECT l.id_lote,l.cantidad_actual,SUM(CASE WHEN m.tipo='ENTRADA' THEN m.cantidad ELSE -m.cantidad END) AS saldo
FROM lote l JOIN movimiento m ON m.id_lote=l.id_lote
GROUP BY l.id_lote,l.cantidad_actual HAVING saldo <> l.cantidad_actual;
