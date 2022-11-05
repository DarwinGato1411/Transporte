/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ec.controlador;

import com.ec.dao.DetalleFacturaDAO;
import com.ec.entidad.DetalleFactura;
import com.ec.entidad.DetalleKardex;
import com.ec.entidad.Factura;
import com.ec.entidad.Kardex;
import com.ec.entidad.Tipokardex;
import com.ec.servicio.ServicioDetalleFactura;
import com.ec.servicio.ServicioDetalleKardex;
import com.ec.servicio.ServicioFactura;
import com.ec.servicio.ServicioKardex;
import com.ec.servicio.ServicioTipoKardex;
import com.ec.untilitario.TotalKardex;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Window;

/**
 *
 * @author gato
 */
public class CambioEstadoFactura {
    
    @Wire
    Window windowEstFact;
    private Factura facturar;
    private String estado;
    private String descripcionAnula;
    ServicioFactura servicioFactura = new ServicioFactura();
    private Boolean descargarKardex = Boolean.TRUE;
    ServicioTipoKardex servicioTipoKardex = new ServicioTipoKardex();
    ServicioKardex servicioKardex = new ServicioKardex();
    ServicioDetalleKardex servicioDetalleKardex = new ServicioDetalleKardex();
    
    ServicioDetalleFactura servicioDetalleFactura = new ServicioDetalleFactura();
    
    @AfterCompose
    public void afterCompose(@ExecutionArgParam("valor") Factura valor, @ContextParam(ContextType.VIEW) Component view) {
        Selectors.wireComponents(view, this, false);
        this.facturar = valor;
        estado = valor.getEstadosri() != null ? valor.getEstadosri() : "";
    }
    
    @Command
    public void guardar() {
        
        servicioFactura.modificar(facturar);
        if (facturar.getEstadosri().equals("ANULADA")) {
            regrarInventario(facturar);
        }
        
        Clients.showNotification("Guardado correctamente",
                    Clients.NOTIFICATION_TYPE_INFO, null, "end_center", 1000, true);
        windowEstFact.detach();
        
    }
    
    private void regrarInventario(Factura factura) {
        if (descargarKardex) {
            /*INGRESAMOS LO MOVIMIENTOS AL KARDEX*/
            Kardex kardex = null;
            DetalleKardex detalleKardex = null;
            
            List<DetalleFactura> listaDetalle = servicioDetalleFactura.findDetalleForIdFactuta(factura);
            for (DetalleFactura item : listaDetalle) {
                if (item.getIdProducto() != null) {
                    
                    Tipokardex tipokardex = servicioTipoKardex.findByTipkSigla("ING");
                    
                    detalleKardex = new DetalleKardex();
                    kardex = servicioKardex.FindALlKardexs(item.getIdProducto());
                    detalleKardex.setIdKardex(kardex);
                    detalleKardex.setDetkFechakardex(factura.getFacFecha());
                    detalleKardex.setDetkFechacreacion(new Date());
                    detalleKardex.setIdTipokardex(tipokardex);
                    detalleKardex.setDetkKardexmanual(Boolean.FALSE);
                    detalleKardex.setDetkDetalles("Aumenta al kardex por ANULACION de factura: " + factura.getFacNumeroText());
                    detalleKardex.setIdFactura(factura);
                    detalleKardex.setDetkCantidad(item.getDetCantidad());
                    servicioDetalleKardex.crear(detalleKardex);
                    /*ACTUALIZA EL TOTAL DEL KARDEX*/
                    TotalKardex totales = servicioKardex.totalesForKardex(kardex);
                    BigDecimal total = totales.getTotalKardex();
                    kardex.setKarTotal(total);
                    servicioKardex.modificar(kardex);
                    
                }
            }
            
        }
    }
    
    public Factura getFacturar() {
        return facturar;
    }
    
    public void setFacturar(Factura facturar) {
        this.facturar = facturar;
    }
    
    public String getEstado() {
        return estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    public String getDescripcionAnula() {
        return descripcionAnula;
    }
    
    public void setDescripcionAnula(String descripcionAnula) {
        this.descripcionAnula = descripcionAnula;
    }
    
}
