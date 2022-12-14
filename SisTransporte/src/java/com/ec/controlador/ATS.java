/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ec.controlador;

import com.ec.entidad.Acumuladoventas;
import com.ec.entidad.CabeceraCompra;
import com.ec.entidad.Tipoambiente;
import com.ec.seguridad.EnumSesion;
import com.ec.seguridad.UserCredential;
import com.ec.servicio.ServicioAcumuladoVentas;
import com.ec.servicio.ServicioCompra;
import com.ec.servicio.ServicioDetalleCompra;
import com.ec.servicio.ServicioFactura;
import com.ec.servicio.ServicioTipoAmbiente;
import com.ec.untilitario.GenerarATS;
import com.ec.untilitario.Totales;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Filedownload;

/**
 *
 * @author gato
 */
public class ATS {

    ServicioDetalleCompra servicioDetalleCompra = new ServicioDetalleCompra();
    ServicioCompra servicioCompra = new ServicioCompra();
    ServicioFactura servicioFactura = new ServicioFactura();

    ServicioAcumuladoVentas servicioAcumuladoVentas = new ServicioAcumuladoVentas();

    private List<CabeceraCompra> listaCabeceraCompras = new ArrayList<CabeceraCompra>();
    private List<Acumuladoventas> listaAcumuladoventas = new ArrayList<Acumuladoventas>();
    private String buscar = "";
    private Date inicio = new Date();
    private Date fin = new Date();

    ServicioTipoAmbiente servicioTipoAmbiente = new ServicioTipoAmbiente();
    private Tipoambiente amb = new Tipoambiente();
    private String amRuc = "";
    UserCredential credential = new UserCredential();

    public ATS() {
        Session sess = Sessions.getCurrent();
        credential = (UserCredential) sess.getAttribute(EnumSesion.userCredential.getNombre());
//        amRuc = credential.getUsuarioSistema().getUsuRuc();
        amb = servicioTipoAmbiente.findALlTipoambientePorUsuario(credential.getUsuarioSistema());
    }

    private void findByBetweenFecha() {
        listaCabeceraCompras = servicioCompra.findByBetweenFecha(inicio, fin, amb);
        listaAcumuladoventas = servicioAcumuladoVentas.findAcumuladoventas(inicio, fin);
    }

    @Command
    @NotifyChange({"listaCabeceraCompras", "listaAcumuladoventas", "inicio", "fin"})
    public void buscarForFechas() {
        findByBetweenFecha();
    }

    @Command
    public void descargaATS() throws FileNotFoundException {
        Boolean valida = Boolean.TRUE;

        List<Totales> totalesesVenta = servicioFactura.totalVenta(inicio, fin);
//        List<Totales> totalesesCompra = servicioCompra.totalCompra(inicio, fin);
        if (totalesesVenta.size() > 0) {
            valida = Boolean.TRUE;
        } else {
            valida = Boolean.FALSE;
        }
        if (valida) {
            GenerarATS generarATS = new GenerarATS();
            File f = new File(generarATS.generaXMLFactura(servicioAcumuladoVentas.findAcumuladoventas(inicio, fin),
                        totalesesVenta.get(0).getTotal(),
                        servicioCompra.findByBetweenFecha(inicio, fin,amb),
                        inicio, fin));
            Filedownload.save(f, null);
            Clients.showNotification("ATS generado correctamente..", "info", null, "end_before", 2000, true);
        } else {
            Clients.showNotification("Verifique la informacion para generar el ATS", "error", null, "start_before", 2000, true);
        }
    }

    public String getBuscar() {
        return buscar;
    }

    public void setBuscar(String buscar) {
        this.buscar = buscar;
    }

    public List<CabeceraCompra> getListaCabeceraCompras() {
        return listaCabeceraCompras;
    }

    public void setListaCabeceraCompras(List<CabeceraCompra> listaCabeceraCompras) {
        this.listaCabeceraCompras = listaCabeceraCompras;
    }

    public Date getInicio() {
        return inicio;
    }

    public void setInicio(Date inicio) {
        this.inicio = inicio;
    }

    public Date getFin() {
        return fin;
    }

    public void setFin(Date fin) {
        this.fin = fin;
    }

    public List<Acumuladoventas> getListaAcumuladoventas() {
        return listaAcumuladoventas;
    }

    public void setListaAcumuladoventas(List<Acumuladoventas> listaAcumuladoventas) {
        this.listaAcumuladoventas = listaAcumuladoventas;
    }

}
