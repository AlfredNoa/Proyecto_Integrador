-- SGIMAC: reinicio de DATOS DE PRUEBA. MySQL 8.0. No ejecutar en producción.
-- Borra el contenido de las 12 tablas. Hacer respaldo antes y cerrar sesiones de la app.
-- Conserva SQL_SAFE_UPDATES al finalizar, incluso si falla una inserción.
-- Datos sintéticos para la presentación académica; no corresponden a operaciones reales.
-- RUC y teléfonos generados al azar, sin verificación de titularidad ni validez fiscal.
-- El historial de reportes y auditoría se precarga como escenario ficticio; no acredita documentos emitidos.
-- Acceso administrador: aletor@sgimac.pe / admin123. Todos los correos siguen nombre y apellido abreviados.
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
        (1,3,'Alejandro Torres','aletor@sgimac.pe','240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9',TRUE),
        (2,2,'Mariana Rivas','marriv@sgimac.pe','240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9',TRUE),
        (3,1,'Diego Salcedo','diesal@sgimac.pe','240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9',TRUE),
        (4,1,'Valeria Paredes','valpar@sgimac.pe','240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9',TRUE),
        (5,1,'Javier Córdova','javcor@sgimac.pe','240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9',TRUE),
        (6,1,'Camila Mendoza','cammen@sgimac.pe','240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9',TRUE),
        (7,1,'Andrés Palomino','andpal@sgimac.pe','240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9',TRUE),
        (8,1,'Lucía Benavides','lucben@sgimac.pe','240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9',TRUE),
        (9,1,'Rodrigo Valverde','rodval@sgimac.pe','240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9',TRUE),
        (10,1,'Paola Cárdenas','paocar@sgimac.pe','240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9',TRUE),
        (11,1,'Sergio Villanueva','servil@sgimac.pe','240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9',TRUE),
        (12,1,'Daniela Espinoza','danesp@sgimac.pe','240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9',TRUE),
        (13,1,'Gabriel Luján','gabluj@sgimac.pe','240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9',TRUE),
        (14,1,'Renata Cabrera','rencab@sgimac.pe','240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9',TRUE),
        (15,1,'Álvaro Peralta','alvper@sgimac.pe','240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9',TRUE),
        (16,1,'Natalia Fuentes','natfue@sgimac.pe','240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9',TRUE),
        (17,1,'Mateo Bustamante','matbus@sgimac.pe','240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9',TRUE),
        (18,1,'Carolina Ponce','carpon@sgimac.pe','240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9',TRUE),
        (19,1,'Bruno Arévalo','bruare@sgimac.pe','240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9',TRUE),
        (20,1,'Elena Zambrano','elezam@sgimac.pe','240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9',TRUE);
    INSERT INTO categoria (id_categoria,nombre,descripcion) VALUES
        (1,'Analgésicos','Medicamentos destinados al alivio del dolor.'),
        (2,'Antibióticos','Medicamentos utilizados en el tratamiento de infecciones bacterianas.'),
        (3,'Antiinflamatorios','Medicamentos destinados a reducir procesos inflamatorios.'),
        (4,'Antisépticos','Productos para la antisepsia de piel y tejidos según su indicación.'),
        (5,'Soluciones intravenosas','Soluciones estériles para administración por vía intravenosa.'),
        (6,'Guantes','Guantes de protección para la atención y los procedimientos clínicos.'),
        (7,'Jeringas','Dispositivos para la administración o extracción de líquidos.'),
        (8,'Gasas','Material absorbente para limpieza, curación y cobertura de heridas.'),
        (9,'Vendajes','Materiales para sujeción de apósitos, soporte y protección.'),
        (10,'Catéteres','Dispositivos para acceso, administración o drenaje de fluidos.'),
        (11,'Suturas','Materiales empleados para aproximar y cerrar tejidos.'),
        (12,'Mascarillas','Elementos de protección utilizados durante la atención sanitaria.'),
        (13,'Protección ocular','Lentes y protectores faciales para resguardar los ojos.'),
        (14,'Batas','Indumentaria de protección para el personal de salud.'),
        (15,'Campos quirúrgicos','Materiales para delimitar y cubrir el área de intervención quirúrgica.'),
        (16,'Recolección de muestras','Recipientes y accesorios para obtener y conservar muestras clínicas.'),
        (17,'Diagnóstico','Insumos utilizados en pruebas y procedimientos de diagnóstico.'),
        (18,'Oxigenoterapia','Dispositivos y accesorios para la administración de oxígeno.'),
        (19,'Cuidado de heridas','Apósitos y materiales para la protección y cuidado de heridas.'),
        (20,'Desinfección','Productos para la desinfección de superficies y equipos compatibles.');
    INSERT INTO proveedor (id_proveedor,razon_social,ruc,telefono) VALUES
        (1,'Suministros Médicos Valle Claro','20895309651','984801454'),
        (2,'Distribuidora Sanitaria Altavista','20324461738','943348472'),
        (3,'Insumos Clínicos Río Azul','20498157942','966944362'),
        (4,'Abastecimientos Médicos Horizonte','20734942636','902714565'),
        (5,'Soluciones Hospitalarias Mirador','20344858050','951987318'),
        (6,'Material Médico Costa Serena','20106996185','987155782'),
        (7,'Distribuciones Biomédicas Ladera','20681485434','978658905'),
        (8,'Comercial Sanitaria Buenavista','20975376611','983112360'),
        (9,'Suministros Hospitalarios Cedral','20844189663','909883314'),
        (10,'Insumos Médicos Portal Andino','20591138068','950416685'),
        (11,'Distribuidora Clínica Alameda','20130428323','960165539'),
        (12,'Materiales Sanitarios Sierra Clara','20833555195','921506644'),
        (13,'Equipamiento Médico Los Olivos Azules','20687641511','963244691'),
        (14,'Abastecimientos Clínicos Nueva Aurora','20857801415','932120742'),
        (15,'Suministros Quirúrgicos Monte Claro','20997024846','915481972'),
        (16,'Distribuciones Médicas Puente Verde','20935202814','938354045'),
        (17,'Insumos Sanitarios Brisa Andina','20839724196','939078459'),
        (18,'Comercial Hospitalaria Valle Norte','20328432671','914912288'),
        (19,'Soluciones Clínicas Sendero','20926812196','991602382'),
        (20,'Distribuidora Médica Cumbre Azul','20443130323','973876092');
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
        (1,1,'inventario','PDF',TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 1 DAY))),
        (2,2,'inventario','PDF',TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 1 DAY))),
        (3,3,'inventario','PDF',TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 1 DAY))),
        (4,4,'inventario','PDF',TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 1 DAY))),
        (5,5,'inventario','PDF',TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 1 DAY))),
        (6,6,'inventario','PDF',TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 1 DAY))),
        (7,7,'inventario','PDF',TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 1 DAY))),
        (8,8,'inventario','PDF',TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 1 DAY))),
        (9,9,'inventario','PDF',TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 1 DAY))),
        (10,10,'inventario','PDF',TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 1 DAY))),
        (11,11,'inventario','PDF',TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 1 DAY))),
        (12,12,'inventario','PDF',TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 1 DAY))),
        (13,13,'inventario','PDF',TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 1 DAY))),
        (14,14,'inventario','PDF',TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 1 DAY))),
        (15,15,'inventario','PDF',TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 1 DAY))),
        (16,16,'inventario','PDF',TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 1 DAY))),
        (17,17,'inventario','PDF',TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 1 DAY))),
        (18,18,'inventario','PDF',TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 1 DAY))),
        (19,19,'inventario','PDF',TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 1 DAY))),
        (20,20,'inventario','PDF',TIMESTAMP(DATE_SUB(v_hoy, INTERVAL 1 DAY)));
    INSERT INTO log_auditoria (id_log,id_usuario,accion,entidad,fecha) VALUES
        (1,1,'Registró lote','lote:1',TIMESTAMP(v_hoy)),
        (2,2,'Registró lote','lote:2',TIMESTAMP(v_hoy)),
        (3,3,'Registró lote','lote:3',TIMESTAMP(v_hoy)),
        (4,4,'Registró lote','lote:4',TIMESTAMP(v_hoy)),
        (5,5,'Registró lote','lote:5',TIMESTAMP(v_hoy)),
        (6,6,'Registró lote','lote:6',TIMESTAMP(v_hoy)),
        (7,7,'Registró lote','lote:7',TIMESTAMP(v_hoy)),
        (8,8,'Registró lote','lote:8',TIMESTAMP(v_hoy)),
        (9,9,'Registró lote','lote:9',TIMESTAMP(v_hoy)),
        (10,10,'Registró lote','lote:10',TIMESTAMP(v_hoy)),
        (11,11,'Registró lote','lote:11',TIMESTAMP(v_hoy)),
        (12,12,'Registró lote','lote:12',TIMESTAMP(v_hoy)),
        (13,13,'Registró lote','lote:13',TIMESTAMP(v_hoy)),
        (14,14,'Registró lote','lote:14',TIMESTAMP(v_hoy)),
        (15,15,'Registró lote','lote:15',TIMESTAMP(v_hoy)),
        (16,16,'Registró lote','lote:16',TIMESTAMP(v_hoy)),
        (17,17,'Registró lote','lote:17',TIMESTAMP(v_hoy)),
        (18,18,'Registró lote','lote:18',TIMESTAMP(v_hoy)),
        (19,19,'Registró lote','lote:19',TIMESTAMP(v_hoy)),
        (20,20,'Registró lote','lote:20',TIMESTAMP(v_hoy));
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
