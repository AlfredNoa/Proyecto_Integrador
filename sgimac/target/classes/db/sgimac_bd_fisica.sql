-- =====================================================================
-- SGIMAC — Sistema de Gestión de Insumos Médicos y Alertas de Caducidad
-- Script de base de datos física — MySQL 8.0
-- Hospital José Agurto Tello
-- =====================================================================

CREATE DATABASE IF NOT EXISTS sgimac
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE sgimac;

-- ---------------------------------------------------------------------
-- 1. ROL
-- ---------------------------------------------------------------------
CREATE TABLE rol (
    id_rol      INT AUTO_INCREMENT PRIMARY KEY,
    nombre      VARCHAR(50)  NOT NULL UNIQUE,
    descripcion VARCHAR(150)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------------------
-- 2. USUARIO
-- ---------------------------------------------------------------------
CREATE TABLE usuario (
    id_usuario       INT AUTO_INCREMENT PRIMARY KEY,
    id_rol           INT NOT NULL,
    nombres          VARCHAR(100) NOT NULL,
    correo           VARCHAR(120) NOT NULL UNIQUE,
    contrasena_hash  VARCHAR(255) NOT NULL,
    activo           BOOLEAN NOT NULL DEFAULT TRUE,
    creado_en        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_usuario_rol FOREIGN KEY (id_rol)
        REFERENCES rol (id_rol)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_usuario_rol ON usuario (id_rol);

-- ---------------------------------------------------------------------
-- 3. CATEGORIA
-- ---------------------------------------------------------------------
CREATE TABLE categoria (
    id_categoria INT AUTO_INCREMENT PRIMARY KEY,
    nombre       VARCHAR(80) NOT NULL UNIQUE,
    descripcion  VARCHAR(150)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------------------
-- 4. PROVEEDOR
-- ---------------------------------------------------------------------
CREATE TABLE proveedor (
    id_proveedor  INT AUTO_INCREMENT PRIMARY KEY,
    razon_social  VARCHAR(150) NOT NULL,
    ruc           CHAR(11) NOT NULL UNIQUE,
    telefono      VARCHAR(20)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------------------
-- 5. UBICACION
-- ---------------------------------------------------------------------
CREATE TABLE ubicacion (
    id_ubicacion INT AUTO_INCREMENT PRIMARY KEY,
    nombre       VARCHAR(80) NOT NULL,
    tipo         VARCHAR(40) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------------------
-- 6. INSUMO
-- ---------------------------------------------------------------------
CREATE TABLE insumo (
    id_insumo      INT AUTO_INCREMENT PRIMARY KEY,
    id_categoria   INT NOT NULL,
    codigo         VARCHAR(30) NOT NULL UNIQUE,
    nombre         VARCHAR(150) NOT NULL,
    unidad_medida  VARCHAR(30) NOT NULL,
    stock_minimo   INT NOT NULL DEFAULT 0,
    CONSTRAINT fk_insumo_categoria FOREIGN KEY (id_categoria)
        REFERENCES categoria (id_categoria)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT chk_insumo_stock_min CHECK (stock_minimo >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_insumo_categoria ON insumo (id_categoria);
CREATE INDEX idx_insumo_nombre ON insumo (nombre);

-- ---------------------------------------------------------------------
-- 7. LOTE
-- ---------------------------------------------------------------------
CREATE TABLE lote (
    id_lote            INT AUTO_INCREMENT PRIMARY KEY,
    id_insumo          INT NOT NULL,
    id_proveedor       INT NOT NULL,
    id_ubicacion       INT NOT NULL,
    codigo_qr          VARCHAR(50) NOT NULL UNIQUE,
    fecha_vencimiento  DATE NOT NULL,
    cantidad_actual    INT NOT NULL DEFAULT 0,
    CONSTRAINT fk_lote_insumo FOREIGN KEY (id_insumo)
        REFERENCES insumo (id_insumo)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_lote_proveedor FOREIGN KEY (id_proveedor)
        REFERENCES proveedor (id_proveedor)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_lote_ubicacion FOREIGN KEY (id_ubicacion)
        REFERENCES ubicacion (id_ubicacion)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT chk_lote_cantidad CHECK (cantidad_actual >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_lote_insumo ON lote (id_insumo);
CREATE INDEX idx_lote_vencimiento ON lote (fecha_vencimiento);
CREATE INDEX idx_lote_ubicacion ON lote (id_ubicacion);

-- ---------------------------------------------------------------------
-- 8. MOVIMIENTO
-- ---------------------------------------------------------------------
CREATE TABLE movimiento (
    id_movimiento INT AUTO_INCREMENT PRIMARY KEY,
    id_lote       INT NOT NULL,
    id_usuario    INT NOT NULL,
    tipo          ENUM('ENTRADA','SALIDA') NOT NULL,
    cantidad      INT NOT NULL,
    fecha         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_movimiento_lote FOREIGN KEY (id_lote)
        REFERENCES lote (id_lote)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_movimiento_usuario FOREIGN KEY (id_usuario)
        REFERENCES usuario (id_usuario)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT chk_movimiento_cantidad CHECK (cantidad > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_movimiento_lote ON movimiento (id_lote);
CREATE INDEX idx_movimiento_usuario ON movimiento (id_usuario);
CREATE INDEX idx_movimiento_fecha ON movimiento (fecha);

-- ---------------------------------------------------------------------
-- 9. ALERTA
-- ---------------------------------------------------------------------
CREATE TABLE alerta (
    id_alerta          INT AUTO_INCREMENT PRIMARY KEY,
    id_lote            INT NOT NULL,
    id_usuario_atiende INT NULL,
    tipo               ENUM('CADUCIDAD','STOCK_BAJO') NOT NULL,
    nivel              ENUM('BAJO','MEDIO','ALTO') NOT NULL,
    estado             ENUM('PENDIENTE','ATENDIDA') NOT NULL DEFAULT 'PENDIENTE',
    fecha_generacion   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_alerta_lote FOREIGN KEY (id_lote)
        REFERENCES lote (id_lote)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_alerta_usuario FOREIGN KEY (id_usuario_atiende)
        REFERENCES usuario (id_usuario)
        ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_alerta_lote ON alerta (id_lote);
CREATE INDEX idx_alerta_estado ON alerta (estado);

-- ---------------------------------------------------------------------
-- 10. NOTIFICACION
-- ---------------------------------------------------------------------
CREATE TABLE notificacion (
    id_notificacion INT AUTO_INCREMENT PRIMARY KEY,
    id_alerta       INT NOT NULL,
    id_usuario      INT NOT NULL,
    mensaje         VARCHAR(255) NOT NULL,
    leida           BOOLEAN NOT NULL DEFAULT FALSE,
    fecha_envio     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notificacion_alerta FOREIGN KEY (id_alerta)
        REFERENCES alerta (id_alerta)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_notificacion_usuario FOREIGN KEY (id_usuario)
        REFERENCES usuario (id_usuario)
        ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_notificacion_usuario ON notificacion (id_usuario, leida);

-- ---------------------------------------------------------------------
-- 11. REPORTE
-- ---------------------------------------------------------------------
CREATE TABLE reporte (
    id_reporte       INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario       INT NOT NULL,
    tipo             VARCHAR(60) NOT NULL,
    formato          ENUM('PDF','EXCEL') NOT NULL,
    fecha_generacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_reporte_usuario FOREIGN KEY (id_usuario)
        REFERENCES usuario (id_usuario)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------------------
-- 12. LOGAUDITORIA
-- ---------------------------------------------------------------------
CREATE TABLE log_auditoria (
    id_log     INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    accion     VARCHAR(100) NOT NULL,
    entidad    VARCHAR(60) NOT NULL,
    fecha      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_log_usuario FOREIGN KEY (id_usuario)
        REFERENCES usuario (id_usuario)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_log_usuario ON log_auditoria (id_usuario);
CREATE INDEX idx_log_fecha ON log_auditoria (fecha);

-- =====================================================================
-- Datos iniciales (roles del sistema)
-- =====================================================================
INSERT INTO rol (nombre, descripcion) VALUES
    ('Personal de Almacén', 'Registra insumos, lotes y movimientos de entrada/salida'),
    ('Personal Médico', 'Consulta stock, alertas y registra uso de insumos'),
    ('Administrador', 'Gestiona usuarios, categorías, reportes y configuración del sistema');

-- =====================================================================
-- Usuario administrador de prueba
-- Correo: admin@sgimac.pe
-- Contraseña: admin123  (guardada como hash SHA-256, ver PasswordUtil.java)
-- =====================================================================
INSERT INTO usuario (id_rol, nombres, correo, contrasena_hash, activo) VALUES
    (3, 'Administrador SGIMAC', 'admin@sgimac.pe',
     '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', TRUE),
    (2, 'Dra. Lucía Fernández', 'medico@sgimac.pe',
     '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', TRUE);

-- =====================================================================
-- Categorías e insumos de ejemplo
-- =====================================================================
INSERT INTO categoria (nombre, descripcion) VALUES
    ('Analgésicos', 'Medicamentos para el dolor y la fiebre'),
    ('Antibióticos', 'Medicamentos para infecciones bacterianas'),
    ('Material de curación', 'Gasas, guantes y afines'),
    ('Soluciones', 'Sueros y soluciones intravenosas');

INSERT INTO insumo (id_categoria, codigo, nombre, unidad_medida, stock_minimo) VALUES
    (1, 'INS-001', 'Paracetamol 500 mg', 'Tabletas', 300),
    (2, 'INS-002', 'Amoxicilina 500 mg', 'Cápsulas', 200),
    (4, 'INS-003', 'Suero fisiológico 0.9% 1 L', 'Frascos', 80),
    (3, 'INS-004', 'Jeringa descartable 5 ml', 'Unidades', 500),
    (3, 'INS-005', 'Gasa estéril 10x10 cm', 'Paquetes', 150);

-- =====================================================================
-- Proveedores y ubicaciones de ejemplo
-- =====================================================================
INSERT INTO proveedor (razon_social, ruc, telefono) VALUES
    ('Droguería Andina S.A.C.', '20456789101', '01-4567890'),
    ('Farmacorp E.I.R.L.', '20512345678', '01-2223344'),
    ('Laboratorios Sur S.A.', '20601122334', '01-5556677');

INSERT INTO ubicacion (nombre, tipo) VALUES
    ('Almacén A-1', 'Almacén central'),
    ('Almacén A-2', 'Almacén central'),
    ('Almacén B-1', 'Almacén de farmacia'),
    ('Almacén C-1', 'Almacén de material médico');

-- =====================================================================
-- Lotes de ejemplo (uno vencido y uno crítico, para probar las validaciones)
-- =====================================================================
INSERT INTO lote (id_insumo, id_proveedor, id_ubicacion, codigo_qr, fecha_vencimiento, cantidad_actual) VALUES
    (1, 1, 1, 'L-2026-001', DATE_SUB(CURDATE(), INTERVAL 12 DAY), 120),   -- Paracetamol, VENCIDO
    (1, 1, 1, 'L-2026-002', DATE_ADD(CURDATE(), INTERVAL 210 DAY), 600), -- Paracetamol, vigente
    (2, 2, 2, 'L-2026-003', DATE_ADD(CURDATE(), INTERVAL 18 DAY), 90),   -- Amoxicilina, crítico
    (3, 3, 3, 'L-2026-004', DATE_ADD(CURDATE(), INTERVAL 55 DAY), 40),   -- Suero, por vencer
    (4, 2, 4, 'L-2026-005', DATE_ADD(CURDATE(), INTERVAL 400 DAY), 1200); -- Jeringas, vigente
