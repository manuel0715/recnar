/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import filter.Audited;
import implement.ApiImpl;
import implement.ComprasImpl;
import implement.MantenimientoImpl;
import interfaz.ComprasInterfaz;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import util.Usuario;

/**
 *
 * @author Administrator
 */
@Path("/compras")
public class ComprasResource {
    @Context
    private ContainerRequestContext requestContext;
    private ComprasInterfaz api;
    private Usuario usuario = null;
    
    /**
     * Creates a new instance of ComprasResource
     */
    public ComprasResource(){
    }
    
    @GET
    @Path("/get-productos/{option}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getProductos(@PathParam("option") int id_categoria) {
        api = new ComprasImpl();
        return api.getProductos(id_categoria);
    }
    
    @POST
    @Path("/insert-producto")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response insertProducto(String json) {
        api = new ComprasImpl();
        usuario=(Usuario)requestContext.getProperty("user");
        return api.insertProducto(json,usuario);
    } 
    
    @POST
    @Path("/save-solicitud-compra")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response saveSolicitudDespacho(String json) {
        api = new ComprasImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.saveSolicitudCompra(json, usuario);
    }
    
    @POST
    @Path("/insert-categoria")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response insertCategoria(String json) {
        api = new ComprasImpl();
        usuario=(Usuario)requestContext.getProperty("user");
        return api.insertCategoria(json,usuario);
    } 
    
    @GET
    @Path("/get-categorias")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getCategorias() {
        api = new ComprasImpl();
        return api.getCategorias();
    }
    
    @POST
    @Path("/insert-proveedor")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response insertProveedor(String json) {
        api = new ComprasImpl();
        usuario=(Usuario)requestContext.getProperty("user");
        return api.insertProveedor(json,usuario);
    } 
    
    @GET
    @Path("/get-proveedores")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getProveedores() {
        api = new ComprasImpl();
        return api.getProveedores();
    }
    
    @POST
    @Path("/cambiar-estado-orden")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response cambiarEstadoOrden(String json) {
        api = new ComprasImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.cambiarEstadoOrden(usuario, json);
    }
    
    @POST
    @Path("/get-requisiciones")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response getRequisiciones(String json) {
        api = new ComprasImpl();
        usuario=(Usuario)requestContext.getProperty("user");
        return api.getRequisiciones(json, usuario);
    }
    
    @POST
    @Path("/insert-requisicion")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response insertRequisicion(String json) {
        api = new ComprasImpl();
        usuario=(Usuario)requestContext.getProperty("user");
        return api.insertRequisicion(json,usuario);
    } 
    
    @GET
    @Path("/get-requisicion-detalle/{option}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getRequisicionDetalle(@PathParam("option") String  codigoRequisicion) {
        api = new ComprasImpl();
        return api.getRequisicionDetalle(codigoRequisicion);
    }
    
    @POST
    @Path("/get-cotizaciones")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response getCotizaciones(String json) {
        api = new ComprasImpl();
        usuario=(Usuario)requestContext.getProperty("user");
        return api.getCotizaciones(json, usuario);
    }
    
    @GET
    @Path("/get-cotizacion-detalle/{option}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getCotizacionDetalle(@PathParam("option") String  codigoRequisicion) {
        api = new ComprasImpl();
        return api.getCotizacionDetalle(codigoRequisicion);
    }
    
    @POST
    @Path("/get-cotizaciones-all")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response getCotizacionesAll(String json) {
        api = new ComprasImpl();
        usuario=(Usuario)requestContext.getProperty("user");
        return api.getCotizacionesAll(json, usuario);
    }
    
    @GET
    @Path("/get-cotizacion-detalle-aprobadas/{option}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getCotizacionDetalleAprobadas(@PathParam("option") String  codigoCotizacion) {
        api = new ComprasImpl();
        return api.getCotizacionDetalleAprobadas(codigoCotizacion);
    }
    
    @POST
    @Path("/get-ordenes-compra")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response getOrdenesCompra(String json) {
        api = new ComprasImpl();
        usuario=(Usuario)requestContext.getProperty("user");
        return api.getOrdenesCompra(json, usuario);
    }
    
    @GET
    @Path("/get-ordenes-detalle/{option}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getOrdenesCompraDetalle(@PathParam("option") String  codigoCotizacion) {
        api = new ComprasImpl();
        return api.getOrdenesCompraDetalle(codigoCotizacion);
    }
    
    @GET
    @Path("/get-ordenes-detalle-pendiente/{option}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getOrdenesCompraDetallePendiente(@PathParam("option") String  codigoCotizacion) {
        api = new ComprasImpl();
        return api.getOrdenesCompraDetallePendiente(codigoCotizacion);
    }
    
    @GET
    @Path("/get-stock-disponible")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response getStockDisponible(@PathParam("option") String  codigoCotizacion) {
        api = new ComprasImpl();
        usuario=(Usuario)requestContext.getProperty("user");
        return api.getStockDisponible(usuario);
    }
    
    @GET
    @Path("/get-remisiones/{option}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getRemisiones(@PathParam("option") String  codigoOrdenCompra) {
        api = new ComprasImpl();
        return api.getRemisiones(codigoOrdenCompra);
    }
}
