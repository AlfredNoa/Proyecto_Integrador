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
