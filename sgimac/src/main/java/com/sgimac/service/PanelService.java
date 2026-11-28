package com.sgimac.service;
import com.sgimac.dao.*;
import java.util.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
/** Indicadores por producto o lote, evitando sumar unidades heterogéneas. */
@Service public class PanelService {
 private final JdbcTemplate jdbc;private final StockDao stock;private final AlertasService alertas;
 public PanelService(JdbcTemplate jdbc,StockDao stock,AlertasService alertas){this.jdbc=jdbc;this.stock=stock;this.alertas=alertas;}
 /** @param m modelo del panel; los indicadores se recalculan al abrirlo. */
 public void cargar(Model m){
  alertas.actualizar();var hoy=jdbc.queryForObject("SELECT CURRENT_DATE",java.sql.Date.class).toLocalDate();m.addAttribute("totalInsumos",jdbc.queryForObject("SELECT COUNT(*) FROM insumo",Integer.class));
  m.addAttribute("lotesVencidos",jdbc.queryForObject("SELECT COUNT(*) FROM lote WHERE cantidad_actual>0 AND fecha_vencimiento<CURRENT_DATE",Integer.class));
  m.addAttribute("proximos",jdbc.queryForList("SELECT l.*,i.nombre AS insumo,i.unidad_medida AS unidad FROM lote l JOIN insumo i ON i.id_insumo=l.id_insumo WHERE l.cantidad_actual>0 AND l.fecha_vencimiento BETWEEN ? AND ? ORDER BY l.fecha_vencimiento,l.id_lote LIMIT 8",hoy,hoy.plusDays(90)));
  m.addAttribute("lotesProximos",jdbc.queryForObject("SELECT COUNT(*) FROM lote WHERE cantidad_actual>0 AND fecha_vencimiento BETWEEN ? AND ?",Integer.class,hoy,hoy.plusDays(90)));
  var bajos=stock.bajos(0);m.addAttribute("totalBajos",bajos.size());m.addAttribute("bajos",bajos.stream().limit(8).toList());
  m.addAttribute("ultimos",jdbc.queryForList("SELECT m.*,i.nombre AS insumo,i.unidad_medida AS unidad,l.codigo_qr FROM movimiento m JOIN lote l ON l.id_lote=m.id_lote JOIN insumo i ON i.id_insumo=l.id_insumo ORDER BY m.fecha DESC,m.id_movimiento DESC LIMIT 6"));
  m.addAttribute("activos",jdbc.queryForObject("SELECT COUNT(*) FROM alerta WHERE estado<>'RESUELTA'",Integer.class));
 }
}
