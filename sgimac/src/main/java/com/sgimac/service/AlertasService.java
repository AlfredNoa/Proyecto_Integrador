package com.sgimac.service;
import com.sgimac.dao.StockDao;
import java.time.*;
import java.util.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
/** Reconcilia alertas por condición. Caducidad por lote con stock; déficit por insumo.
 * ATENDIDA significa revisada; RESUELTA significa que cesó la condición. */
@Service
public class AlertasService {
    private final JdbcTemplate jdbc; private final StockDao stocks;
    public AlertasService(JdbcTemplate jdbc,StockDao stocks){this.jdbc=jdbc;this.stocks=stocks;}
    /** Actualiza niveles y resuelve condiciones desaparecidas. Serializa reconciliaciones
     * con una fila de rol para evitar duplicados entre peticiones e instancias. */
    @Transactional(isolation=org.springframework.transaction.annotation.Isolation.READ_COMMITTED)
    public void actualizar(){
        jdbc.queryForList("SELECT id_rol FROM rol WHERE id_rol=3 FOR UPDATE");
        LocalDate hoy=jdbc.queryForObject("SELECT CURRENT_DATE",java.sql.Date.class).toLocalDate();
        Map<String,Map<String,Object>> objetivos=new LinkedHashMap<>();
        for(var l:jdbc.queryForList("SELECT id_lote,id_insumo,fecha_vencimiento FROM lote WHERE cantidad_actual>0 ORDER BY id_lote")){
            long dias=java.time.temporal.ChronoUnit.DAYS.between(hoy,((java.sql.Date)l.get("fecha_vencimiento")).toLocalDate());
            if(dias<=90)objetivos.put("CADUCIDAD:"+l.get("id_lote"),Map.of("lote",l.get("id_lote"),"insumo",l.get("id_insumo"),"nivel",dias<=30?"ALTO":"MEDIO"));
        }
        for(var s:stocks.todos())if(((Number)s.get("utilizable")).longValue()<((Number)s.get("minimo")).longValue())
            objetivos.put("STOCK_BAJO:"+s.get("id_insumo"),Map.of("insumo",s.get("id_insumo"),"nivel","MEDIO"));
        // Incluye las alertas de la carga anterior asociadas solo a un lote.
        for(var a:jdbc.queryForList("""
            SELECT a.*,COALESCE(a.id_insumo,l.id_insumo) AS producto FROM alerta a
            LEFT JOIN lote l ON l.id_lote=a.id_lote WHERE a.estado<>'RESUELTA' ORDER BY a.id_alerta
            """)){
            String tipo=(String)a.get("tipo"),key=tipo+":"+(tipo.equals("CADUCIDAD")?a.get("id_lote"):a.get("producto"));
            var objetivo=objetivos.remove(key);int id=((Number)a.get("id_alerta")).intValue();
            if(objetivo==null){jdbc.update("UPDATE alerta SET estado='RESUELTA' WHERE id_alerta=?",id);
                jdbc.update("UPDATE notificacion SET leida=TRUE WHERE id_alerta=?",id);continue;}
            boolean escala="ALTO".equals(objetivo.get("nivel"))&&!"ALTO".equals(a.get("nivel"));
            jdbc.update("UPDATE alerta SET id_insumo=?, id_lote=?, nivel=?, estado=?, id_usuario_atiende=? WHERE id_alerta=?",
                objetivo.get("insumo"),objetivo.get("lote"),objetivo.get("nivel"),escala?"PENDIENTE":a.get("estado"),escala?null:a.get("id_usuario_atiende"),id);
            if(escala)notificar(id);
        }
        for(var entry:objetivos.entrySet()){
            var o=entry.getValue();String tipo=entry.getKey().split(":")[0];
            org.springframework.jdbc.support.KeyHolder keys=new org.springframework.jdbc.support.GeneratedKeyHolder();
            jdbc.update(con->{var ps=con.prepareStatement("INSERT INTO alerta(id_lote,id_insumo,tipo,nivel,estado) VALUES (?,?,?,?,'PENDIENTE')",new String[]{"id_alerta"});
                ps.setObject(1,o.get("lote"));ps.setObject(2,o.get("insumo"));ps.setString(3,tipo);ps.setString(4,(String)o.get("nivel"));return ps;},keys);
            if("ALTO".equals(o.get("nivel")))notificar(keys.getKey().intValue());
        }
    }
    private void notificar(int id){
        jdbc.update("""
            INSERT INTO notificacion(id_alerta,id_usuario,mensaje,leida)
            SELECT ?,u.id_usuario,'Revisar alerta de caducidad de nivel alto en el módulo Alertas.',FALSE
            FROM usuario u JOIN rol r ON r.id_rol=u.id_rol
            WHERE u.activo=TRUE AND r.nombre='Personal Médico'
            """,id);
    }
    /** Registra la revisión humana sin ocultar que la condición puede continuar activa.
     * @param id identificador de alerta @param usuario persona que la atiende */
    @Transactional(isolation=org.springframework.transaction.annotation.Isolation.READ_COMMITTED)
    public void atender(int id,int usuario){
        jdbc.queryForList("SELECT id_rol FROM rol WHERE id_rol=3 FOR UPDATE");
        jdbc.update("UPDATE alerta SET estado='ATENDIDA',id_usuario_atiende=? WHERE id_alerta=? AND estado='PENDIENTE'",usuario,id);
    }
}
