package com.sgimac.service;
import com.sgimac.dao.ListadosDao;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
/** Prepara atributos uniformes de filtros y navegación para cuatro pantallas. */
@Service public class ListadosService {
 private final ListadosDao dao;
 public ListadosService(ListadosDao dao){this.dao=dao;}
 /**
  * @param m modelo
  * @param modulo listado
  * @param q texto
  * @param estado filtro
  * @param pagina página
  * @param tam tamaño */
 public void cargar(Model m,String modulo,String q,String estado,int pagina,int tam){m.addAllAttributes(dao.listar(modulo,q,estado,pagina,tam));m.addAttribute("modulo",modulo);}
}
