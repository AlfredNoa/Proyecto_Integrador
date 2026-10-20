package com.sgimac.dao;

import com.sgimac.model.Lote;

import java.util.List;
import java.util.Optional;

/**
 * Patrón DAO: contrato de acceso a datos para la tabla {@code lote}. Los controladores dependen de
 * esta interfaz y nunca de su implementación JDBC.
 *
 * @author Equipo SGIMAC
 * @version 1.0
 */
public interface LoteDao {

    /**
     * Lista todos los lotes ordenados por fecha de vencimiento (los más próximos primero).
     *
     * @return lista de lotes
     */
    List<Lote> listar();

    /**
     * Lista los lotes que vencen dentro de los próximos días indicados, incluidos los que ya
     * vencieron.
     *
     * @param dias cantidad de días hacia adelante desde hoy
     * @return lotes con fecha de vencimiento menor o igual a hoy más {@code dias}, ordenados por
     *     fecha
     */
    List<Lote> listarProximosAVencer(int dias);

    /**
     * Busca un lote por su identificador.
     *
     * @param idLote identificador del lote
     * @return el lote, o un {@code Optional} vacío si no existe
     */
    Optional<Lote> buscarPorId(int idLote);

    /**
     * Busca un lote por su código QR.
     *
     * @param codigoQr código QR del lote
     * @return el lote, o un {@code Optional} vacío si no existe
     */
    Optional<Lote> buscarPorCodigoQr(String codigoQr);

    /**
     * Registra un nuevo lote.
     *
     * @param lote lote a registrar
     * @return identificador generado para el nuevo lote
     */
    int insertar(Lote lote);

    /**
     * Suma (valor positivo) o resta (valor negativo) unidades a la cantidad actual del lote.
     *
     * @param idLote identificador del lote
     * @param delta unidades a sumar o restar
     * @return {@code true} si se aplicó el ajuste; {@code false} si el lote no existe o el ajuste
     *     dejaría la cantidad en negativo
     */
    boolean ajustarCantidad(int idLote, int delta);

    /**
     * Suma el stock vigente (lotes no vencidos) de todos los lotes de un insumo.
     *
     * @param idInsumo identificador del insumo
     * @return total de unidades vigentes; 0 si no hay lotes vigentes
     */
    int sumarStockVigente(int idInsumo);
}
