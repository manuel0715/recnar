/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import filter.Audited;
import implement.RrhhImpl;
import interfaz.RrhhInterfaz;
import javax.ws.rs.core.Context;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import util.Usuario;

/**
 * REST Web Service
 *
 * @author Administrator
 */
@Path("/rrhh")
public class RrhhResource {

     @Context
    private ContainerRequestContext requestContext;
    private RrhhInterfaz api;
    private Usuario usuario = null;

    /**
     * Creates a new instance of RrhhResource
     */
    public RrhhResource() {
    }

    @POST
    @Path("/registrar-biometria-facial")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response loginApi(String json) {
        api = new RrhhImpl();
        return api.registrarFacial(json);
    }
    
    @POST
    @Path("/crear-empleado")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response crearEmpleado(String json) {
        api = new RrhhImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.crearEmpleado(json, usuario);
    }
    
    @GET
    @Path("/empleados")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response cargarEmpleados() {
        api = new RrhhImpl();
        return api.cargarEmpleados();
    }
    
    @POST
    @Path("/get-combos")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response cargarCombos(String json) {
        api = new RrhhImpl();
        return api.cargarCombos(json);
    }
    
    @GET
    @Path("/get-ciudades/{option}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getCiudades(@PathParam("option") String idDepartamento) {
        api = new RrhhImpl();
        return api.getCiudades(idDepartamento);
    }
    
    @POST
    @Path("/crear-novedad")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response crearNovedad(String json) {
        api = new RrhhImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.crearNovedad(json, usuario);
    }
    
    @GET
    @Path("/get-novedad-empleado/{option}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getNovedadesEmpleado(@PathParam("option") String numeroDocumento) {
        api = new RrhhImpl();
        return api.getNovedadesEmpleado(numeroDocumento);
    }  
    
    @GET
    @Path("/get-novedad-aprobacion-jefe")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getNovedadAprobacionJefe(
    @QueryParam("numeroDocumento") String numeroDocumento,
    @QueryParam("fechaInicio") String fechaInit,
    @QueryParam("fechaFin") String fechaFin) {
        api = new RrhhImpl();
    return api.getNovedadAprobacionJefe(numeroDocumento, fechaInit, fechaFin);
}

    @GET
    @Path("/get-novedad-aprobacion")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getNovedadAprobacion(
    @QueryParam("numeroDocumento") String numeroDocumento,
    @QueryParam("fechaInicio") String fechaInit,
    @QueryParam("fechaFin") String fechaFin) { 
        api = new RrhhImpl();
    return api.getNovedadAprobacion(numeroDocumento, fechaInit, fechaFin);
    } 
    
    @GET
    @Path("/get-documentos")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getDocumentos() {
        api = new RrhhImpl();
        return api.getDocumentos();
    }   
    
    @POST
    @Path("/gestionar-novedad")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response gestionarNovedad(String json) {
        api = new RrhhImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.gestionarNovedad(json, usuario);
    }
    
    @GET
    @Path("/get-festivos/{option}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getFestivos(@PathParam("option") String ano) {
       api = new RrhhImpl();      
       return api.getFestivos(ano);
    }  
    
    @GET
    @Path("/get-jefe-directo")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getJefeDirecto() {
        api = new RrhhImpl();
        return api.getJefeDirecto();
    }  

}