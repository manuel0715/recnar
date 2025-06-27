/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import filter.Audited;
import implement.PuntoVerdeImpl;
import interfaz.PuntoVerdeInterfaz;
import javax.ws.rs.core.Context;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import util.Usuario;

/**
 * REST Web Service
 *
 * @author Administrator
 */
@Path("/punto-verde")
public class PuntoVerdeResource {

    @Context
    private ContainerRequestContext requestContext;
    private PuntoVerdeInterfaz api;
    private Usuario usuario = null;

    /**
     * Creates a new instance of PuntoVerdeResource
     */
    public PuntoVerdeResource() {
    }

    @GET
    @Path("/get-actividades-asignadas")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response getActividadesAsignadas() {
        api = new PuntoVerdeImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.getActividadesAsignadas(usuario);
    }

    @POST
    @Path("/actualizar-estado-actividad")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response actualizarEstadoActividad(String json) {
        api = new PuntoVerdeImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.actualizarEstadoActividad(json, usuario);
    }

    @POST
    @Path("/guardar-trazabilidad")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response guardarTrazabilidadActividad(String json) {
        api = new PuntoVerdeImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.guardarTrazabilidadActividad(json, usuario);
    }

    @POST
    @Path("/get-actividades")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response getActividades(String json) {
        api = new PuntoVerdeImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.getActividades(json, usuario);
    }

    @GET
    @Path("/get-trazabilidad/{option}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response getTrazabilidad(@PathParam("option") int idActividad) {
        api = new PuntoVerdeImpl();
        return api.getTrazabilidad(idActividad);
    }

    @GET
    @Path("/get-actividad/{option}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response getActividad(@PathParam("option") int idActividad) {
        api = new PuntoVerdeImpl();
        return api.getActividad(idActividad);
    }

    @POST
    @Path("/gestionar-cronograma")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response gestionarCronograma(String json) {
        api = new PuntoVerdeImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.gestionarCronograma(json, usuario);
    }

    @POST
    @Path("/get-cronogramas")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response getCronogramas(String json) {
        api = new PuntoVerdeImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.getCronogramas(json, usuario);
    }

    @GET
    @Path("/get-cronograma/{option}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response getCronograma(@PathParam("option") int idCronograma) {
        api = new PuntoVerdeImpl();
        return api.getCronograma(idCronograma);
    }

    @GET
    @Path("/get-puntos-recogida")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response getPuntosRecogida() {
        api = new PuntoVerdeImpl();
        return api.getPuntosRecogida();
    }

    @POST
    @Path("/gestionar-asignaciones")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response gestionarAsignaciones(String json) {
        api = new PuntoVerdeImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.gestionarAsignaciones(json, usuario);
    }
}
