-- SGIMAC: reinicio de DATOS DE PRUEBA. MySQL 8.0. No ejecutar en producción.
-- Borra el contenido de las 12 tablas. Hacer respaldo antes y cerrar sesiones de la app.
-- Conserva SQL_SAFE_UPDATES al finalizar, incluso si falla una inserción.
-- Todos los nombres de personas y empresas son ficticios. RUC y teléfonos no válidos para trámites.
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
    DECLARE v_safe_updates BOOLEAN DEFAULT @@SESSION.sql_safe_updates;
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET SESSION SQL_SAFE_UPDATES = v_safe_updates;
        RESIGNAL;
    END;
    IF confirmacion <> 'BORRAR_DATOS_Y_CARGAR_DEMO' OR confirmacion IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Confirmacion de borrado no valida';
    END IF;
    SET SESSION SQL_SAFE_UPDATES = 0;
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
        (1,3,'Administrador de Almacén','admin@sgimac.pe','240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9',TRUE),
        (2,2,'Mariana Rivas','medico@sgimac.pe','240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9',TRUE),
        (3,1,'Diego Salcedo','usuario03@example.invalid','240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9',TRUE),
        (4,1,'Valeria Paredes','usuario04@example.invalid','240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9',TRUE),
        (5,1,'Javier Córdova','usuario05@example.invalid','240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9',TRUE),
        (6,1,'Camila Mendoza','usuario06@example.invalid','240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9',TRUE),
        (7,1,'Andrés Palomino','usuario07@example.invalid','240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9',TRUE),
        (8,1,'Lucía Benavides','usuario08@example.invalid','240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9',TRUE),
        (9,1,'Rodrigo Valverde','usuario09@example.invalid','240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9',TRUE),
        (10,1,'Paola Cárdenas','usuario10@example.invalid','240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9',TRUE),
        (11,1,'Sergio Villanueva','usuario11@example.invalid','240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9',TRUE),
        (12,1,'Daniela Espinoza','usuario12@example.invalid','240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9',TRUE),
        (13,1,'Gabriel Luján','usuario13@example.invalid','240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9',TRUE),
        (14,1,'Renata Cabrera','usuario14@example.invalid','240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9',TRUE),
        (15,1,'Álvaro Peralta','usuario15@example.invalid','240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9',TRUE),
        (16,1,'Natalia Fuentes','usuario16@example.invalid','240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9',TRUE),
        (17,1,'Mateo Bustamante','usuario17@example.invalid','240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9',TRUE),
        (18,1,'Carolina Ponce','usuario18@example.invalid','240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9',TRUE),
        (19,1,'Bruno Arévalo','usuario19@example.invalid','240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9',TRUE),
        (20,1,'Elena Zambrano','usuario20@example.invalid','240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9',TRUE);
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
        (1,'Suministros Médicos Valle Claro','00000000001','000000000'),
        (2,'Distribuidora Sanitaria Altavista','00000000002','000000000'),
        (3,'Insumos Clínicos Río Azul','00000000003','000000000'),
        (4,'Abastecimientos Médicos Horizonte','00000000004','000000000'),
        (5,'Soluciones Hospitalarias Mirador','00000000005','000000000'),
        (6,'Material Médico Costa Serena','00000000006','000000000'),
        (7,'Distribuciones Biomédicas Ladera','00000000007','000000000'),
        (8,'Comercial Sanitaria Buenavista','00000000008','000000000'),
        (9,'Suministros Hospitalarios Cedral','00000000009','000000000'),
        (10,'Insumos Médicos Portal Andino','00000000010','000000000'),
        (11,'Distribuidora Clínica Alameda','00000000011','000000000'),
        (12,'Materiales Sanitarios Sierra Clara','00000000012','000000000'),
        (13,'Equipamiento Médico Los Olivos Azules','00000000013','000000000'),
        (14,'Abastecimientos Clínicos Nueva Aurora','00000000014','000000000'),
        (15,'Suministros Quirúrgicos Monte Claro','00000000015','000000000'),
        (16,'Distribuciones Médicas Puente Verde','00000000016','000000000'),
        (17,'Insumos Sanitarios Brisa Andina','00000000017','000000000'),
        (18,'Comercial Hospitalaria Valle Norte','00000000018','000000000'),
        (19,'Soluciones Clínicas Sendero','00000000019','000000000'),
        (20,'Distribuidora Médica Cumbre Azul','00000000020','000000000');
    INSERT INTO ubicacion (id_ubicacion,nombre,tipo) VALUES
        (1,'Almacén Central - A1','Almacén central'),
        (2,'Almacén Central - A2','Almacén central'),
        (3,'Almacén Central - A3','Almacén central'),
        (4,'Almacén Central - A4','Almacén central'),
        (5,'Almacén Central - A5','Almacén central'),
        (6,'Almacén Central - B1','Almacén central'),
        (7,'Almacén Central - B2','Almacén central'),
        (8,'Almacén Central - B3','Almacén central'),
        (9,'Almacén Central - B4','Almacén central'),
        (10,'Almacén Central - B5','Almacén central'),
        (11,'Almacén Central - C1','Almacén central'),
        (12,'Almacén Central - C2','Almacén central'),
        (13,'Almacén Central - C3','Almacén central'),
        (14,'Almacén Central - C4','Almacén central'),
        (15,'Almacén Central - C5','Almacén central'),
        (16,'Almacén Central - D1','Almacén central'),
        (17,'Almacén Central - D2','Almacén central'),
        (18,'Almacén Central - D3','Almacén central'),
        (19,'Almacén Central - D4','Almacén central'),
        (20,'Almacén Central - D5','Almacén central');
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
        (1,1,1,1,'LOT-001',DATE_ADD(v_hoy, INTERVAL -12 DAY),10),
        (2,2,2,2,'LOT-002',DATE_ADD(v_hoy, INTERVAL -5 DAY),65),
        (3,3,3,3,'LOT-003',DATE_ADD(v_hoy, INTERVAL -1 DAY),80),
        (4,4,4,4,'LOT-004',DATE_ADD(v_hoy, INTERVAL -30 DAY),120),
        (5,5,5,5,'LOT-005',DATE_ADD(v_hoy, INTERVAL -2 DAY),45),
        (6,6,6,6,'LOT-006',DATE_ADD(v_hoy, INTERVAL 0 DAY),15),
        (7,7,7,7,'LOT-007',DATE_ADD(v_hoy, INTERVAL 1 DAY),70),
        (8,8,8,8,'LOT-008',DATE_ADD(v_hoy, INTERVAL 18 DAY),90),
        (9,9,9,9,'LOT-009',DATE_ADD(v_hoy, INTERVAL 29 DAY),110),
        (10,10,10,10,'LOT-010',DATE_ADD(v_hoy, INTERVAL 30 DAY),150),
        (11,11,11,11,'LOT-011',DATE_ADD(v_hoy, INTERVAL 31 DAY),20),
        (12,12,12,12,'LOT-012',DATE_ADD(v_hoy, INTERVAL 45 DAY),60),
        (13,13,13,13,'LOT-013',DATE_ADD(v_hoy, INTERVAL 55 DAY),100),
        (14,14,14,14,'LOT-014',DATE_ADD(v_hoy, INTERVAL 89 DAY),75),
        (15,15,15,15,'LOT-015',DATE_ADD(v_hoy, INTERVAL 90 DAY),130),
        (16,16,16,16,'LOT-016',DATE_ADD(v_hoy, INTERVAL 91 DAY),25),
        (17,1,17,17,'LOT-017',DATE_ADD(v_hoy, INTERVAL 120 DAY),5),
        (18,2,18,18,'LOT-018',DATE_ADD(v_hoy, INTERVAL 180 DAY),85),
        (19,3,19,19,'LOT-019',DATE_ADD(v_hoy, INTERVAL 210 DAY),95),
        (20,4,20,20,'LOT-020',DATE_ADD(v_hoy, INTERVAL 400 DAY),140);
    INSERT INTO movimiento (id_movimiento,id_lote,id_usuario,tipo,cantidad,fecha) VALUES
        (1,1,1,'ENTRADA',20,TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 60 DAY), '08:00:00')),
        (2,1,1,'SALIDA',10,TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 45 DAY), '09:00:00')),
        (3,2,2,'ENTRADA',75,TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 60 DAY), '08:00:00')),
        (4,2,2,'SALIDA',10,TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 45 DAY), '09:00:00')),
        (5,3,3,'ENTRADA',90,TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 60 DAY), '08:00:00')),
        (6,3,3,'SALIDA',10,TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 45 DAY), '09:00:00')),
        (7,4,4,'ENTRADA',130,TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 60 DAY), '08:00:00')),
        (8,4,4,'SALIDA',10,TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 45 DAY), '09:00:00')),
        (9,5,5,'ENTRADA',55,TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 60 DAY), '08:00:00')),
        (10,5,5,'SALIDA',10,TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 45 DAY), '09:00:00')),
        (11,6,6,'ENTRADA',25,TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 60 DAY), '08:00:00')),
        (12,6,6,'SALIDA',10,TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 45 DAY), '09:00:00')),
        (13,7,7,'ENTRADA',80,TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 60 DAY), '08:00:00')),
        (14,7,7,'SALIDA',10,TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 45 DAY), '09:00:00')),
        (15,8,8,'ENTRADA',100,TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 60 DAY), '08:00:00')),
        (16,8,8,'SALIDA',10,TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 45 DAY), '09:00:00')),
        (17,9,9,'ENTRADA',120,TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 60 DAY), '08:00:00')),
        (18,9,9,'SALIDA',10,TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 45 DAY), '09:00:00')),
        (19,10,10,'ENTRADA',160,TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 60 DAY), '08:00:00')),
        (20,10,10,'SALIDA',10,TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 45 DAY), '09:00:00')),
        (21,11,11,'ENTRADA',30,TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 60 DAY), '08:00:00')),
        (22,11,11,'SALIDA',10,TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 45 DAY), '09:00:00')),
        (23,12,12,'ENTRADA',70,TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 60 DAY), '08:00:00')),
        (24,12,12,'SALIDA',10,TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 45 DAY), '09:00:00')),
        (25,13,13,'ENTRADA',110,TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 60 DAY), '08:00:00')),
        (26,13,13,'SALIDA',10,TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 45 DAY), '09:00:00')),
        (27,14,14,'ENTRADA',85,TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 60 DAY), '08:00:00')),
        (28,14,14,'SALIDA',10,TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 45 DAY), '09:00:00')),
        (29,15,15,'ENTRADA',140,TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 60 DAY), '08:00:00')),
        (30,15,15,'SALIDA',10,TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 45 DAY), '09:00:00')),
        (31,16,16,'ENTRADA',35,TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 60 DAY), '08:00:00')),
        (32,16,16,'SALIDA',10,TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 45 DAY), '09:00:00')),
        (33,17,17,'ENTRADA',15,TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 60 DAY), '08:00:00')),
        (34,17,17,'SALIDA',10,TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 45 DAY), '09:00:00')),
        (35,18,18,'ENTRADA',95,TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 60 DAY), '08:00:00')),
        (36,18,18,'SALIDA',10,TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 45 DAY), '09:00:00')),
        (37,19,19,'ENTRADA',105,TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 60 DAY), '08:00:00')),
        (38,19,19,'SALIDA',10,TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 45 DAY), '09:00:00')),
        (39,20,20,'ENTRADA',150,TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 60 DAY), '08:00:00')),
        (40,20,20,'SALIDA',10,TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 45 DAY), '09:00:00'));
    INSERT INTO alerta (id_alerta,id_lote,id_usuario_atiende,tipo,nivel,estado,fecha_generacion) VALUES
        (1,1,NULL,'CADUCIDAD','ALTO','PENDIENTE',TIMESTAMP(v_hoy)),
        (2,2,NULL,'CADUCIDAD','ALTO','PENDIENTE',TIMESTAMP(v_hoy)),
        (3,3,NULL,'CADUCIDAD','ALTO','PENDIENTE',TIMESTAMP(v_hoy)),
        (4,4,NULL,'CADUCIDAD','ALTO','PENDIENTE',TIMESTAMP(v_hoy)),
        (5,5,NULL,'CADUCIDAD','ALTO','PENDIENTE',TIMESTAMP(v_hoy)),
        (6,6,NULL,'CADUCIDAD','ALTO','PENDIENTE',TIMESTAMP(v_hoy)),
        (7,7,NULL,'CADUCIDAD','ALTO','PENDIENTE',TIMESTAMP(v_hoy)),
        (8,8,NULL,'CADUCIDAD','ALTO','PENDIENTE',TIMESTAMP(v_hoy)),
        (9,9,NULL,'CADUCIDAD','ALTO','PENDIENTE',TIMESTAMP(v_hoy)),
        (10,10,NULL,'CADUCIDAD','ALTO','PENDIENTE',TIMESTAMP(v_hoy)),
        (11,11,NULL,'CADUCIDAD','MEDIO','PENDIENTE',TIMESTAMP(v_hoy)),
        (12,12,NULL,'CADUCIDAD','MEDIO','PENDIENTE',TIMESTAMP(v_hoy)),
        (13,13,NULL,'CADUCIDAD','MEDIO','PENDIENTE',TIMESTAMP(v_hoy)),
        (14,14,NULL,'CADUCIDAD','MEDIO','PENDIENTE',TIMESTAMP(v_hoy)),
        (15,15,NULL,'CADUCIDAD','MEDIO','PENDIENTE',TIMESTAMP(v_hoy)),
        (16,1,NULL,'STOCK_BAJO','MEDIO','PENDIENTE',TIMESTAMP(v_hoy)),
        (17,6,NULL,'STOCK_BAJO','MEDIO','PENDIENTE',TIMESTAMP(v_hoy)),
        (18,11,NULL,'STOCK_BAJO','MEDIO','PENDIENTE',TIMESTAMP(v_hoy)),
        (19,16,NULL,'STOCK_BAJO','MEDIO','PENDIENTE',TIMESTAMP(v_hoy)),
        (20,17,NULL,'STOCK_BAJO','MEDIO','PENDIENTE',TIMESTAMP(v_hoy));
    INSERT INTO notificacion (id_notificacion,id_alerta,id_usuario,mensaje,leida,fecha_envio) VALUES
        (1,1,1,'Revisar el lote LOT-001: fecha de vencimiento alcanzada o próxima.',FALSE,TIMESTAMP(v_hoy)),
        (2,2,2,'Revisar el lote LOT-002: fecha de vencimiento alcanzada o próxima.',FALSE,TIMESTAMP(v_hoy)),
        (3,3,3,'Revisar el lote LOT-003: fecha de vencimiento alcanzada o próxima.',FALSE,TIMESTAMP(v_hoy)),
        (4,4,4,'Revisar el lote LOT-004: fecha de vencimiento alcanzada o próxima.',FALSE,TIMESTAMP(v_hoy)),
        (5,5,5,'Revisar el lote LOT-005: fecha de vencimiento alcanzada o próxima.',FALSE,TIMESTAMP(v_hoy)),
        (6,6,6,'Revisar el lote LOT-006: fecha de vencimiento alcanzada o próxima.',FALSE,TIMESTAMP(v_hoy)),
        (7,7,7,'Revisar el lote LOT-007: fecha de vencimiento alcanzada o próxima.',FALSE,TIMESTAMP(v_hoy)),
        (8,8,8,'Revisar el lote LOT-008: fecha de vencimiento alcanzada o próxima.',FALSE,TIMESTAMP(v_hoy)),
        (9,9,9,'Revisar el lote LOT-009: fecha de vencimiento alcanzada o próxima.',FALSE,TIMESTAMP(v_hoy)),
        (10,10,10,'Revisar el lote LOT-010: fecha de vencimiento alcanzada o próxima.',FALSE,TIMESTAMP(v_hoy)),
        (11,11,11,'Revisar el lote LOT-011: fecha de vencimiento alcanzada o próxima.',FALSE,TIMESTAMP(v_hoy)),
        (12,12,12,'Revisar el lote LOT-012: fecha de vencimiento alcanzada o próxima.',FALSE,TIMESTAMP(v_hoy)),
        (13,13,13,'Revisar el lote LOT-013: fecha de vencimiento alcanzada o próxima.',FALSE,TIMESTAMP(v_hoy)),
        (14,14,14,'Revisar el lote LOT-014: fecha de vencimiento alcanzada o próxima.',FALSE,TIMESTAMP(v_hoy)),
        (15,15,15,'Revisar el lote LOT-015: fecha de vencimiento alcanzada o próxima.',FALSE,TIMESTAMP(v_hoy)),
        (16,16,16,'Revisar el lote LOT-001: existencias por debajo del mínimo.',FALSE,TIMESTAMP(v_hoy)),
        (17,17,17,'Revisar el lote LOT-006: existencias por debajo del mínimo.',FALSE,TIMESTAMP(v_hoy)),
        (18,18,18,'Revisar el lote LOT-011: existencias por debajo del mínimo.',FALSE,TIMESTAMP(v_hoy)),
        (19,19,19,'Revisar el lote LOT-016: existencias por debajo del mínimo.',FALSE,TIMESTAMP(v_hoy)),
        (20,20,20,'Revisar el lote LOT-017: existencias por debajo del mínimo.',FALSE,TIMESTAMP(v_hoy));
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
    SET SESSION SQL_SAFE_UPDATES = v_safe_updates;
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
