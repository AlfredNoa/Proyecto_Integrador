-- SGIMAC: ejecutar UNA VEZ antes de iniciar la versión mejorada. No borra filas.
-- Detener la aplicación y respaldar la base antes de modificar el esquema.
USE sgimac;
DROP PROCEDURE IF EXISTS migrar_alertas_05;
DELIMITER $$
CREATE PROCEDURE migrar_alertas_05()
BEGIN
 IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema=DATABASE() AND table_name='alerta' AND column_name='id_insumo') THEN
  ALTER TABLE alerta ADD COLUMN id_insumo INT NULL,
    ADD CONSTRAINT fk_alerta_insumo FOREIGN KEY(id_insumo) REFERENCES insumo(id_insumo) ON UPDATE CASCADE ON DELETE RESTRICT;
 END IF;
 ALTER TABLE alerta MODIFY id_lote INT NULL,
   MODIFY estado ENUM('PENDIENTE','ATENDIDA','RESUELTA') NOT NULL DEFAULT 'PENDIENTE';
END$$
DELIMITER ;
CALL migrar_alertas_05();
DROP PROCEDURE migrar_alertas_05;
-- Las alertas anteriores se conservan. La aplicación normaliza las activas en su primera revisión.
