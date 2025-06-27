/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import filter.Audited;
import implement.MantenimientoImpl;
import interfaz.MantenimientoInterfaz;
import java.util.List;
import javax.ws.rs.core.Context;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import util.Usuario;

/**
 * REST Web Service
 *
 * @author Administrator
 */
@Path("/mantenimiento")
public class MantenimientoResource {

    @Context
    private ContainerRequestContext requestContext;
    private MantenimientoInterfaz api;
    private Usuario usuario = null;

    /**
     * Creates a new instance of MantenimientoResource
     */
    public MantenimientoResource() {
    }

    @GET
    @Path("/get-combo-estados")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getComboEstados() {
        api = new MantenimientoImpl();
        return api.getComboEstados();
    }

    @GET
    @Path("/get-combo-frecuencia")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getComboFrecuencia() {
        api = new MantenimientoImpl();
        return api.getComboFrecuencia();
    }

    @GET
    @Path("/get-combo-tipo-mantenimiento")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getComboTipoMantenimiento() {
        api = new MantenimientoImpl();
        return api.getComboTipoMantenimiento();
    }

    @GET
    @Path("/get-combo-novedades")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getComboNovedades() {
        api = new MantenimientoImpl();
        return api.getComboNovedades();
    }

    @GET
    @Path("/get-combo-responsables")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getComboResponsables() {
        api = new MantenimientoImpl();
        return api.getComboResponsables();
    }

    @POST
    @Path("/get-combo-actividades")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response getComboActividades(String json) {
        api = new MantenimientoImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.getComboActividades(json, usuario);
    }

    @POST
    @Path("/get-combo-maquinas")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response getComboMaquinas(String json) {
        api = new MantenimientoImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.getComboMaquinas(json, usuario);
    }

    @POST
    @Path("/get-tipo-equipos")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response getTipoEquipos(String json) {
        api = new MantenimientoImpl();
        return api.getTipoEquipos(json, usuario);
    }

    @POST
    @Path("/insert-equipos")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response insertEquipos(String json) {
        api = new MantenimientoImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.insertEquipos(json, usuario);
    }

    @POST
    @Path("/update-equipos")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response updateEquipo(String json) {
        api = new MantenimientoImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.updateEquipo(json, usuario);
    }

    @POST
    @Path("/get-mantenimientos")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response getMantenimientos(String json) {
        api = new MantenimientoImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.getMantenimientos(json, usuario);
    }

    @GET
    @Path("/get-solicitudes-mantenimiento")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getSolicitudesMantenimiento() {
        api = new MantenimientoImpl();
        return api.getSolicitudesMantenimiento();
    }

    @POST
    @Path("/solicitar-mantenimiento")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response soliciarMantenimiento(String json) {
        api = new MantenimientoImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.soliciarMantenimiento(json, usuario);
    }

    @POST
    @Path("/insert-mantenimiento")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response insertMantenimiento(String json) {
        api = new MantenimientoImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.insertMantenimiento(json, usuario);
    }

    @POST
    @Path("/save-file-mantenimento")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Audited
    public Response guardarFotoMantenimiento(@FormDataParam("archivo") List<FormDataBodyPart> parts, @FormDataParam("json") String json) {
        api = new MantenimientoImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.guardarFotoMantenimiento(json, parts, usuario);
    }

    @POST
    @Path("/crear-formulario")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response crearFormulario(String json) {
        api = new MantenimientoImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.crearFormulario(json, usuario);
    }

    @POST
    @Path("/guardar-respuestas")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response guardarRespuestas(String json) {
        api = new MantenimientoImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.guardarRespuestas(json, usuario);
    }

    @GET
    @Path("/get-formularios")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getFormularios() {
        api = new MantenimientoImpl();
        return api.getFormularios();
    }

    @GET
    @Path("/get-tipos-campo")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getTiposCampo() {
        api = new MantenimientoImpl();
        return api.getTiposCampo();
    }

    @GET
    @Path("/get-formulario/{option}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getFormulario(@PathParam("option") int id_formulario) {
        api = new MantenimientoImpl();
        return api.getFormulario(id_formulario);
    }

    @GET
    @Path("/get-categorias")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getCategorias() {
        api = new MantenimientoImpl();
        return api.getCategorias();
    }

    @GET
    @Path("/get-documentos-asignados/{option}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getDocumentosAsignados(@PathParam("option") int id_tipo_equipo) {
        api = new MantenimientoImpl();
        return api.getDocumentosAsignados(id_tipo_equipo);
    }

    @GET
    @Path("/get-documentos")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getDocumentos() {
        api = new MantenimientoImpl();
        return api.getDocumentos();
    }

    @POST
    @Path("/crear-tipo-equipo")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response crearTipoEquipo(String json) {
        api = new MantenimientoImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.crearTipoEquipo(json, usuario);
    }

    @POST
    @Path("/crear-subcategorias")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response crearSubcategorias(String json) {
        api = new MantenimientoImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.crearSubcategorias(json, usuario);
    }

    @GET
    @Path("/get-subcategorias")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getSubcategorias() {
        api = new MantenimientoImpl();
        return api.getSubcategorias();
    }

    @GET
    @Path("/get-verificacion/{option}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getverificacion(@PathParam("option") int subcategoriaId) {
        api = new MantenimientoImpl();
        return api.getverificacion(subcategoriaId);
    }

    @POST
    @Path("/guardar-inspeccion")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response guardarInspeccion(String json) {
        api = new MantenimientoImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.guardarInspeccion(json, usuario);
    }

    @GET
    @Path("/get-actividades/{option}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getActividades(@PathParam("option") int subcategoriaId) {
        api = new MantenimientoImpl();
        return api.getActividades(subcategoriaId);
    }

    @GET
    @Path("/get-propietarios-maquinas")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getPropietariosMaquinas() {
        api = new MantenimientoImpl();
        return api.getPropietariosMaquinas();
    }

    @GET
    @Path("/get-clientes-alquiler")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getClientesAlquiler() {
        api = new MantenimientoImpl();
        return api.getClientesAlquiler();
    }

    @GET
    @Path("/get-repuestos")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getRespuestos() {
        api = new MantenimientoImpl();
        return api.getRespuestos();
    }

    @GET
    @Path("/get-marcas")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getMarcas() {
        api = new MantenimientoImpl();
        return api.getMarcas();
    }

    @POST
    @Path("/insert-repuesto-referencia")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response insertRepuestosReferencia(String json) {
        api = new MantenimientoImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.insertRepuestosReferencia(json, usuario);
    }

    @GET
    @Path("/get-repuestos-referencia/{option}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getRepuestosReferencia(@PathParam("option") String codigoMaquina) {
        api = new MantenimientoImpl();
        return api.getRepuestosReferencia(codigoMaquina);
    }

    @GET
    @Path("/get-equipo-maquina/{option}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getEquipoMaquina(@PathParam("option") String codigoMaquina) {
        api = new MantenimientoImpl();
        return api.getEquipoMaquina(codigoMaquina);
    }

    @POST
    @Path("/get-mantenimiento-asignado")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response getMantenimientoAsignado(String json) {
        api = new MantenimientoImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.getMantenimientoAsignado(json, usuario);
    }

    @GET
    @Path("/get-actividades-mantenimiento-asignado/{option}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getActividadesMantenimientoAsignado(@PathParam("option") String codigoMantenimiento) {
        api = new MantenimientoImpl();
        return api.getActividadesMantenimientoAsignado(codigoMantenimiento);
    }

    @POST
    @Path("/get-inspecciones-realizadas")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
  //  @Audited
    public Response getImspeccionesRealizadas(String json) {
        api = new MantenimientoImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.getImspeccionesRealizadas(json, usuario);
    }
    
    @POST
    @Path("/insert-horometro")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response insertHorometro(String json) {
        api = new MantenimientoImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.insertHorometro(json, usuario);
    }
    
    @GET
    @Path("/get-revision-pendiente/{option}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getRevisionPendiente(@PathParam("option") String codigoQr) {
        api = new MantenimientoImpl();
        return api.getRevisionPendiente(codigoQr);
    }
    
    @GET
    @Path("/get-mantenimientos-maquina/{option}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getMantenimientosMaquina(@PathParam("option") String codigoMaquina) {
        api = new MantenimientoImpl();
        return api.getMantenimientosMaquina(codigoMaquina);
    }
}
