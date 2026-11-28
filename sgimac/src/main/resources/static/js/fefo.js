document.addEventListener('click', function(event) {
 const button=event.target.closest('.elegir-fefo'); if(!button)return;
 document.getElementById('idLote').value=button.dataset.lote;
 document.getElementById('tipoRegistro').value='SALIDA';
 document.getElementById('cantidadRegistro').value=button.dataset.cantidad;
 document.getElementById('registro').scrollIntoView({behavior:'smooth',block:'center'});
 document.getElementById('cantidadRegistro').focus();
});
