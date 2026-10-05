package com.sgimac.dao;

import com.sgimac.model.Lote;

import java.util.List;
import java.util.Optional;

/**
 * Patron DAO: contrato de acceso a datos para la tabla "lote".
 */
public interface LoteDao {

    List<Lote> listar();

    List<Lote> listarProximosAVencer(int dias);

    Optional<Lote> buscarPorId(int idLote);

    Optional<Lote> buscarPorCodigoQr(String codigoQr);

    int insertar(Lote lote);

    /** Suma (positivo) o resta (negativo) unidades del stock actual del lote. */
    boolean ajustarCantidad(int idLote, int delta);

    /** Suma el stock vigente (no vencido) de todos los lotes de un insumo. */
    int sumarStockVigente(int idInsumo);
}
