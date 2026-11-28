package com.sgimac.service;
import java.util.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
/** Propuesta FEFO de solo lectura: reparte una cantidad entre lotes no vencidos.
 * La confirmación sigue siendo explícita, un lote a la vez, y revalida existencias. */
@Service
public class FefoService {
    private final JdbcTemplate jdbc;
    public FefoService(JdbcTemplate jdbc){this.jdbc=jdbc;}
    /**
  * @param insumo producto elegido
  * @param cantidad cantidad solicitada
     *
  * @return propuesta ordenada por vencimiento e identificador; nunca reserva stock. */
    public Map<String,Object> sugerir(int insumo,int cantidad){
        if(insumo<=0||cantidad<=0)throw new IllegalArgumentException("Seleccione un insumo y una cantidad mayor que cero.");
        var lotes=jdbc.queryForList("""
          SELECT l.id_lote,l.codigo_qr,l.fecha_vencimiento,l.cantidad_actual,u.nombre AS ubicacion,i.unidad_medida AS unidad
          FROM lote l JOIN ubicacion u ON u.id_ubicacion=l.id_ubicacion JOIN insumo i ON i.id_insumo=l.id_insumo
          WHERE l.id_insumo=? AND l.fecha_vencimiento>=CURRENT_DATE AND l.cantidad_actual>0
          ORDER BY l.fecha_vencimiento,l.id_lote
          """,insumo);
        long pendiente=cantidad, disponible=0;
        for(var l:lotes){long stock=((Number)l.get("cantidad_actual")).longValue();disponible+=stock;
            long tomar=Math.min(pendiente,stock);l.put("sugerida",tomar);pendiente-=tomar;}
        return Map.of("lotes",lotes,"solicitado",cantidad,"disponible",disponible,"faltante",pendiente);
    }
}
