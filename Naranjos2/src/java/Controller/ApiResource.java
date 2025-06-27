/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import filter.Audited;
import implement.ApiImpl;
import javax.ws.rs.core.Context;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import interfaz.ApiInterfaz;
import java.io.InputStream;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import util.Usuario;

/**
 * REST Web Service
 *
 * @author mcamargo
 */
@Path("/api")
//@Logged
public class ApiResource {

    @Context
    private ContainerRequestContext requestContext;
    private ApiInterfaz api;
    private Usuario usuario = null;

    /**
     * Creates a new instance of ApiResource
     */
    public ApiResource() {
    }

    @POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response loginApi(String json) {
        api = new ApiImpl();
        return api.loginApi(json);
    }

    @POST
    @Path("/generar-orden-ingreso")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Audited
    public Response generarOrdenIngreso(@FormDataParam("archivo") InputStream input, @FormDataParam("json") String json) {
        api = new ApiImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.generarOrdenIngreso(json, input, usuario);
    }

    @POST
    @Path("/get-orden-ingreso")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response getOrdenIngreso(String json) {
        api = new ApiImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.getOrdenIngreso(json, usuario);
    }

    @POST
    @Path("/get-orden-ingreso-no-procesadas")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response getOrdenIngresoNoProcesadas(String json) {
        api = new ApiImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.getOrdenIngresoNoProcesadas(json, usuario);
    }

    @POST
    @Path("/get-desintegracion-documental-pendientes")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response getDesintegracionDocumentalPendientes(String json) {
        api = new ApiImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.getDesintegracionDocumentalPendientes(json, usuario);
    }

    @POST
    @Path("/get-orden-ingreso-dd")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response getOrdenIngresoDd(String json) {
        api = new ApiImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.getOrdenIngresoDd(json, usuario);
    }

    @POST
    @Path("/get-detalle-orden-ingreso")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response getDetalleOrdenIngreso(String json) {
        api = new ApiImpl();
        return api.getDetalleOrdenIngreso(json);
    }

    @GET
    @Path("/get-combo-municipios")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getComboMunicipios() {
        api = new ApiImpl();
        return api.getComboMunicipios();
    }

    @GET
    @Path("/get-combo-patios")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response getComboPatios() {
        api = new ApiImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.getComboPatios(usuario);
    }

    @GET
    @Path("/get-combo-proveedor")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getComboproveedor() {
        api = new ApiImpl();
        return api.getComboProveedor();
    }

    @GET
    @Path("/get-combo-autorizado")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getComboAutorizado() {
        api = new ApiImpl();
        return api.getComboAutorizado();
    }

    @GET
    @Path("/get-combo-autorizacion")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getComboAutorizacion() {
        api = new ApiImpl();
        return api.getComboAutorizacion();
    }

    @GET
    @Path("/get-combo-motivo")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getComboMotivo() {
        api = new ApiImpl();
        return api.getComboMotivo();
    }

    @GET
    @Path("/get-combo-responsable")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getComboResponsable() {
        api = new ApiImpl();
        return api.getComboResponsable();
    }

    @GET
    @Path("/get-combo-tipo-articulo")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getComboTipoArticulo() {
        api = new ApiImpl();
        return api.getComboTipoArticulo();
    }

    @GET
    @Path("/get-combo-unidad-medida")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getComboUnidadMedida() {
        api = new ApiImpl();
        return api.getComboUnidadMedida();
    }

    @GET
    @Path("/get-combo-clase-articulo")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getComboClaseArticulo() {
        api = new ApiImpl();
        return api.getComboClaseArticulo();
    }

    @GET
    @Path("/get-combo-categoria")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getComboCategoria() {
        api = new ApiImpl();
        return api.getComboCategoria();
    }

    @GET
    @Path("/get-combo-clasificacion")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getComboClasificacion() {
        api = new ApiImpl();
        return api.getComboClasificacion();
    }

    @POST
    @Path("/get-documentos-proveedor")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getDocumentosRequeridosProveedor(String json) {
        api = new ApiImpl();
        return api.getDocumentosRequeridosProveedor(json);
    }

    @POST
    @Path("/save-documentacion-inicial")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Audited
    public Response guardarDesintegracionDocumentalInicial(@FormDataParam("archivo") List<FormDataBodyPart> parts, @FormDataParam("json") String json) {
        api = new ApiImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.guardarDesintegracionDocumentalInicial(json, parts, usuario);
    }

    @GET
    @Path("/get-documentacion-inicial/{option}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response getDocumentacionInicialCargada(@PathParam("option") String cod_inspeccion) {
        api = new ApiImpl();
        return api.getDocumentacionInicialCargada(cod_inspeccion);
    }

    @PUT
    @Path("/finalizar-desintegracion-documetal/{option}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response finalizarDesintegracionDocumental(@PathParam("option") String cod_inspeccion) {
        api = new ApiImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.finalizarDesintegracionDocumental(cod_inspeccion, usuario);
    }

    @POST
    @Path("/get-orden-ingreso-procesadas")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response getOrdenIngresoProcesadas(String json) {
        api = new ApiImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.getOrdenIngresoProcesadas(json, usuario);
    }

    @POST
    @Path("/get-detalle-orden-ingreso-procesadas")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response getDetalleOrdenIngresoProcesadas(String json) {
        api = new ApiImpl();
        return api.getDetalleOrdenIngresoProcesadas(json);
    }

    @POST
    @Path("/get-componentes-clase-articulo")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getComponentesClaseArticulo(String json) {
        api = new ApiImpl();
        return api.getComponentesClaseArticulo(json);
    }
    
    @GET
    @Path("/get-componentes-all")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getComponentesAll() {
        api = new ApiImpl();
        return api.getComponentesAll();
    }

    @POST
    @Path("/save-archivo-ingreso-remoto")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Audited
    public Response guardarArchivoIngresoRemoto(@FormDataParam("archivo") List<FormDataBodyPart> parts, @FormDataParam("json") String json) {
        api = new ApiImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.guardarArchivoIngresoRemoto(json, parts, usuario);
    }

    @POST
    @Path("/save-orden-ingreso-remoto")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response guardarOrdenDetalleIngresoRemoto(String json) {
        api = new ApiImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.guardarOrdenDetalleIngresoRemoto(json, usuario);
    }

    @PUT
    @Path("/finalizar-item-ingreso-remoto/{option}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response finalizarItemIngresoRemoto(@PathParam("option") int id_detalle_orden_ingreso) {
        api = new ApiImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.finalizarItemIngresoRemoto(id_detalle_orden_ingreso, usuario);
    }

    @PUT
    @Path("/finalizar-ingreso-remoto/{option}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response finalizarIngresoRemoto(@PathParam("option") String cod_ingreso_remoto) {
        api = new ApiImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.finalizarIngresoRemoto(cod_ingreso_remoto, usuario);
    }

    @POST
    @Path("/get-ingreso-remoto-sin-finalizar")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response getOrdenIngresoRemotoNoFinalizadas(String json) {
        api = new ApiImpl();
        return api.getOrdenIngresoRemotoNoFinalizadas(json);
    }

    @POST
    @Path("/get-ingreso-remoto-finalizadas")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response getOrdenIngresoRemotoFinalizadas(String json) {
        api = new ApiImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.getOrdenIngresoRemotoFinalizadas(json, usuario);
    }

    @GET
    @Path("/get-detalle-ingreso-remoto/{option}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response getDetalleIngresoRemoto(@PathParam("option") String cod_ingreso_remoto) {
        api = new ApiImpl();
        return api.getDetalleIngresoRemoto(cod_ingreso_remoto);
    }

    @GET
    @Path("/get-detalle-ingreso-remoto-sin-remision/{option}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response getDetalleIngresoRemotoSinRemision(@PathParam("option") String cod_ingreso_remoto) {
        api = new ApiImpl();
        return api.getDetalleIngresoRemotoSinRemision(cod_ingreso_remoto);
    }

    @GET
    @Path("/get-checks-detalle-ingreso-remoto/{option}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response getChecksDetalleIngresoRemoto(@PathParam("option") int id_detalle_ingreso_remoto) {
        api = new ApiImpl();
        return api.getChecksDetalleIngresoRemoto(id_detalle_ingreso_remoto);
    }

    @POST
    @Path("/save-traslado")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response guardarTraslado(String json) {
        api = new ApiImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.guardarTraslado(json, usuario);
    }

    @GET
    @Path("/get-traslados")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response getTraslados() {
        api = new ApiImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.getTraslados(usuario);
    }

    @GET
    @Path("/get-traslados-detalle/{option}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response getTrasladosDetalle(@PathParam("option") String codigoTraslado) {
        api = new ApiImpl();
        return api.getTrasladosDetalle(codigoTraslado);
    }

    @POST
    @Path("/save-ingreso-local")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response guardarIngresoLocal(String json) {
        api = new ApiImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.guardarIngresoLocal(json, usuario);
    }

    @GET
    @Path("/get-detalle-remision-sin-procesa/{option}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response getDetalleRemisionSinProcesar(@PathParam("option") String cod_ingreso_remoto) {
        api = new ApiImpl();
        return api.getDetalleTrasladoSinProcesar(cod_ingreso_remoto);
    }

    @POST
    @Path("/update-orden-ingreso")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response updateOrdenIngreso(String json) {
        api = new ApiImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.updateOrdenIngreso(json, usuario);
    }

    @POST
    @Path("/update-detalle-orden-ingreso")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response updateDetalleOrdenIngreso(String json) {
        api = new ApiImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.updateDetalleOrdenIngreso(json, usuario);
    }

    @POST
    @Path("/eliminar-orden-ingreso")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response deleteOrdenIngreso(String json) {
        api = new ApiImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.deleteOrdenIngreso(json, usuario);
    }

    @POST
    @Path("/delete-detalle-orden-ingreso/{option}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response deleteDetalleOrdenIngreso(@PathParam("option") int idDetalle) {
        api = new ApiImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.deleteDetalleOrdenIngreso(idDetalle, usuario);
    }

    @POST
    @Path("/crear-cronograma")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response crearCronograma(String json) {
        api = new ApiImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.crearCronograma(json, usuario);
    }

    @GET
    @Path("/get-combo-actividad-cronograma")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getComboCronograma() {
        api = new ApiImpl();
        return api.getComboCronograma();
    }

    @POST
    @Path("/update-cronograma")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response updateCronograma(String json) {
        api = new ApiImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.updateCronograma(json, usuario);
    }

    @POST
    @Path("/update-cronograma-detalle")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response updateCronogramaDetalle(String json) {
        api = new ApiImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.updateCronogramaDetalle(json, usuario);
    }

    @POST
    @Path("/insert-cronograma-detalle")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response insertCronogramaDetalle(String json) {
        api = new ApiImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.insertCronogramaDetalle(json, usuario);
    }

    @GET
    @Path("/get-cronograma")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response getCronograma() {
        api = new ApiImpl();
        return api.getCronograma();
    }

    @GET
    @Path("/get-cronograma-detalle/{option}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response getCronogramaDetalle(@PathParam("option") int idCronogramaDetalle) {
        api = new ApiImpl();
        return api.getCronogramaDetalle(idCronogramaDetalle);
    }

    @POST
    @Path("/get-documentos-ingreso-local")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getDocumentosIngresoLocal(String json) {
        api = new ApiImpl();
        return api.getDocumentosIngresoLocal(json);
    }

    @GET
    @Path("/get-cronograma-proveedor/{option}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response getCronogramaProveedor(@PathParam("option") String nit_proveedor) {
        api = new ApiImpl();
        return api.getCronogramaProveedor(nit_proveedor);
    }

    @GET
    @Path("/get-dependencia-cronograma/{option}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response getDependenciaCronograma(@PathParam("option") int idCronograma) {
        api = new ApiImpl();
        return api.getDependenciaCronograma(idCronograma);
    }

    @GET
    @Path("/get-ingreso-local-sin-finalizar")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response getOrdenIngresoLocalNoFinalizadas() {
        api = new ApiImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.getOrdenIngresoLocalNoFinalizadas(usuario);
    }

    @GET
    @Path("/get-detalle-ingreso-local/{option}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response getDetalleIngresoLocal(@PathParam("option") String cod_ingreso_local) {
        api = new ApiImpl();
        return api.getDetalleIngresoLocal(cod_ingreso_local);
    }

    @GET
    @Path("/get-checks-detalle-ingreso-local/{option}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response getChecksDetalleIngresoLocal(@PathParam("option") int id_detalle_ingreso_local) {
        api = new ApiImpl();
        return api.getChecksDetalleIngresoLocal(id_detalle_ingreso_local);
    }

    @POST
    @Path("/save-archivo-ingreso-local")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Audited
    public Response guardarArchivoIngresoLocal(@FormDataParam("archivo") List<FormDataBodyPart> parts, @FormDataParam("json") String json) {
        api = new ApiImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.guardarArchivoIngresoLocal(json, parts, usuario);
    }

    @PUT
    @Path("/finalizar-item-ingreso-local/{option}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response finalizarItemIngresoLocal(@PathParam("option") int id_detalle_orden_ingreso) {
        api = new ApiImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.finalizarItemIngresoLocal(id_detalle_orden_ingreso, usuario);
    }

    @GET
    @Path("/get-ingreso-local-finalizadas")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response getOrdenIngresoLocalFinalizadas() {
        api = new ApiImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.getOrdenIngresoLocalFinalizadas(usuario);
    }

    @POST
    @Path("/save-desintegracion-paso1")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Audited
    public Response saveDesintegracionPaso1(@FormDataParam("archivo") List<FormDataBodyPart> parts, @FormDataParam("json") String json) {
        api = new ApiImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.saveDesintegracionPaso1(json, parts, usuario);
    }

    @GET
    @Path("/get-paso-desintegracion/{option}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response getPasoDesintegracion(@PathParam("option") String paso) {
        api = new ApiImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.getPasoDesintegracion(paso, usuario);
    }

    @POST
    @Path("/save-desintegracion-paso2")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response saveDesintegracionPaso2(String json) {
        api = new ApiImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.saveDesintegracionPaso2(json, usuario);
    }

    @POST
    @Path("/save-desintegracion-paso3")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Audited
    public Response saveDesintegracionPaso3(@FormDataParam("archivo") List<FormDataBodyPart> parts, @FormDataParam("json") String json) {
        api = new ApiImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.saveDesintegracionPaso3(json, parts, usuario);
    }

    @POST
    @Path("/upload-file-desintegracion")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Audited
    public Response saveFileDesintegracion(@FormDataParam("archivo") List<FormDataBodyPart> parts, @FormDataParam("json") String json) {
        api = new ApiImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.saveFileDesintegracion(json, parts, usuario);
    }

    @GET
    @Path("/get-archivos-desintegracion/{option}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response getDocumentosDesintegracion(@PathParam("option") int id_detalle_ingreso) {
        api = new ApiImpl();
        return api.getDocumentosDesintegracion(id_detalle_ingreso);
    }

    //REPORTES
    @GET
    @Path("/reporte-orden-ingreso")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response getReporteOrdenIngreso() {
        api = new ApiImpl();
        return api.getReporteOrdenIngreso();
    }

    @GET
    @Path("/reporte-detalle-orden-ingreso/{option}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response reporteDetalleOrdenIngreso(@PathParam("option") String codOrdenIngreso) {
        api = new ApiImpl();
        return api.reporteDetalleOrdenIngreso(codOrdenIngreso);
    }

    @GET
    @Path("/reporte-ingreso-remoto")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response reporteOrdenIngresoRemoto() {
        api = new ApiImpl();
        return api.reporteOrdenIngresoRemoto();
    }

    @POST
    @Path("/get-estado-item")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getEstadoItem(String json) {
        api = new ApiImpl();
        return api.getEstadoItem(json);
    }

    @GET
    @Path("/get-usuarios")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getUsuarios() {
        api = new ApiImpl();
        return api.getUsuarios();
    }

    @POST
    @Path("/save-usuario")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response saveUsuario(String json) {
        api = new ApiImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.adminUsuario(json, usuario, "INSERT");
    }

    @POST
    @Path("/update-usuario")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response updateUsuario(String json) {
        api = new ApiImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.adminUsuario(json, usuario, "UPDATE");
    }

    @GET
    @Path("/get-proveedores")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getProveedores() {
        api = new ApiImpl();
        return api.getProveedores();
    }

    @POST
    @Path("/save-proveedor")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response saveProveedor(String json) {
        api = new ApiImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.adminProveedor(json, usuario, "INSERT");
    }

    @POST
    @Path("/update-proveedor")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response updateProveedor(String json) {
        api = new ApiImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.adminProveedor(json, usuario, "UPDATE");
    }

    @GET
    @Path("/get-combo-menu-opciones")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getComboMenuOpciones() {
        api = new ApiImpl();
        return api.getComboMenuOpciones();
    }

    @GET
    @Path("/get-inventario-items")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getInventarioItems() {
        api = new ApiImpl();
        return api.getInventarioItems();
    }

    @GET
    @Path("/get-inventario-items-patios")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getInventarioItemsPatios() {
        api = new ApiImpl();
        return api.getInventarioItemsPatios();
    }

    @GET
    @Path("/get-combo-materiales-recuperados")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getComboMaterialesRecuperados() {
        api = new ApiImpl();
        return api.getComboMaterialesRecuperados();
    }

    @POST
    @Path("/save-inventario-matriales-recuperados")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response saveInventarioMaterialesRecuperados(String json) {
        api = new ApiImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.saveInventarioMaterialesRecuperados(json, usuario);
    }

    @POST
    @Path("/save-despacho")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response guardarDespacho(String json) {
        api = new ApiImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.guardarDespacho(json,  usuario);
    }

    @GET
    @Path("/get-item-files/{option}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getItemFiles(@PathParam("option") int idDetalleOrdenIngreso) {
        api = new ApiImpl();
        return api.getItemFiles(idDetalleOrdenIngreso);
    }

    @POST
    @Path("/save-bitacora")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Audited
    public Response guardarBitacora(@FormDataParam("archivo") List<FormDataBodyPart> parts, @FormDataParam("json") String json) {
        api = new ApiImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.guardarBitacora(json, parts, usuario);
    }

    @GET
    @Path("/get-bitacora/{option}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getBitacora(@PathParam("option") String codigo_orden_ingreso) {
        api = new ApiImpl();
        return api.getBitacora(codigo_orden_ingreso);
    }

    @GET
    @Path("/get-bitacora-file/{option}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getBitacoraFile(@PathParam("option") int idBitacora) {
        api = new ApiImpl();
        return api.getBitacoraFile(idBitacora);
    }

    @POST
    @Path("/save-caja")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response guardarCaja(String json) {
        api = new ApiImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.guardarCaja(json, usuario);
    }

    @POST
    @Path("/save-gastos-caja")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response guardarGastosCaja(String json) {
        api = new ApiImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.guardarGastosCaja(json, usuario);
    }

    @POST
    @Path("/get-gastos-caja")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response getGastosCaja(String json) {
        api = new ApiImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.getGastosCaja(usuario,json);
    }

    @GET
    @Path("/get-gastos-caja-admin")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response getGastosCajaAdmin() {
        api = new ApiImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.getGastosCajaAdmin(usuario);
    }

    @GET
    @Path("/get-ingreso-remoto-pendientes")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getIngresoRemotoPendientes() {
        api = new ApiImpl();
        return api.getIngresoRemotoPendientes();
    }

    @GET
    @Path("/get-ingreso-remoto-proceso")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getIngresoRemotoProceso() {
        api = new ApiImpl();
        return api.getIngresoRemotoProceso();
    }

    @GET
    @Path("/get-ingreso-local-pendientes")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getIngresoLocalPendientes() {
        api = new ApiImpl();
        return api.getIngresoLocalPendientes();
    }

    @GET
    @Path("/get-traslados-pendientes")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getTrasladosPendientes() {
        api = new ApiImpl();
        return api.getTrasladosPendientes();
    }

    @POST
    @Path("/save-clase-articulo")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response guardarClaseArticulo(String json) {
        api = new ApiImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.guardarClaseArticulo(json, usuario);
    }

    @POST
    @Path("/save-componentes")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response guardarComponente(String json) {
        api = new ApiImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.guardarComponente(json, usuario);
    }

    @POST
    @Path("/save-rel-clase-componetes")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response guardarRelClaseComponentes(String json) {
        api = new ApiImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.guardarRelClaseComponentes(json, usuario);
    }

    @GET
    @Path("/get-componentes/{option}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getComponentes(@PathParam("option") int idClase) {
        api = new ApiImpl();
        return api.getComponentes(idClase);
    }

    @GET
    @Path("/get-componentes-asignados/{option}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getComponentesAsignados(@PathParam("option") int idClase) {
        api = new ApiImpl();
        return api.getComponentesAsignados(idClase);
    }

    @GET
    @Path("/get-archivos-zip/{option}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getZipArchivos(@PathParam("option") int idOrden) {
        api = new ApiImpl();
        return api.getZipArchivos(idOrden);
    }

    @POST
    @Path("/get-ingresos-patios")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getIngresosPatios(String json) {
        api = new ApiImpl();
        return api.getIngresosPatios(json);
    }
    
    @GET
    @Path("/get-total-ingresos")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getTipoEquipos() {
        api = new ApiImpl();
        return api.getTotalIngresos();
    }

    @POST
    @Path("/get-ingresos-proveedores")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getIngresosProveedores(String json) {
        api = new ApiImpl();
        return api.getIngresosProveedores(json);
    }

    @POST
    @Path("/get-ingresos-fechas")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getIngresosFechas(String json) {
        api = new ApiImpl();
        return api.getIngresosFechas(json);
    }

    @GET
    @Path("/get-documents-pending/{option}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getDocumentsPending(@PathParam("option") String codigOrden) {
        api = new ApiImpl();
        return api.getDocumentsPending(codigOrden);
    }

    @GET
    @Path("/get-inventario-recnar")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response getInventarioRecnar() {
        api = new ApiImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.getInventarioRecnar(usuario);
    }

    @POST
    @Path("/eliminar-item-cronograma/{option}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteItemCronograma(@PathParam("option") String item) {
        api = new ApiImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.deleteItemCronograma(item, usuario);
    }

    @POST
    @Path("/get-dependencias-proveedor/{option}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getDependenciasProveedor(@PathParam("option") String item) {
        api = new ApiImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.getDependenciasProveedor(item, usuario);
    }

    @POST
    @Path("/insert-dependencias-proveedor")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response insertDependenciasProveedor(String json) {
        api = new ApiImpl();
        return api.insertDependenciasProveedor(json);
    }

    @POST
    @Path("/delete-dependencias-proveedor/{option}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteDependenciasProveedor(@PathParam("option") String item) {
        api = new ApiImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.deleteDependenciasProveedor(item, usuario);
    }

    @POST
    @Path("/get-propietarios")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getPropietarios() {
        api = new ApiImpl();
        return api.getPropietarios();
    }

    @POST
    @Path("/insert-propietarios")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response insertPropietarios(String json) {
        api = new ApiImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.insertPropietarios(json, usuario);
    }

    @POST
    @Path("/get-novedad-detalle-cronograma/{option}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getNovedadDetalleCronograma(@PathParam("option") Integer item) {
        api = new ApiImpl();
        return api.getNovedadDetalleCronograma(item);
    }

    @POST
    @Path("/get-total-automotores-cronograma/{option}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getTotalAutomotoresCronograma(@PathParam("option") Integer item) {
        api = new ApiImpl();
        return api.getTotalAutomotoresCronograma(item);
    }

    @POST
    @Path("/insert-novedad-detalle-cronograma")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response insertNovedadDetalleCronograma(String json) {
        api = new ApiImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.insertNovedadDetalleCronograma(json, usuario);
    }

    @GET
    @Path("/get-status-orden/{option}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getEstadoOrden(@PathParam("option") int idCronograma) {
        api = new ApiImpl();
        return api.getEstadoOrden(idCronograma);
    }

    @POST
    @Path("/save-solicitud-despacho")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response saveSolicitudDespacho(String json) {
        api = new ApiImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.saveSolicitudDespacho(json, usuario);
    }

    @GET
    @Path("/get-solicitudes-despacho/{option}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response getSolicitudesDespacho(@PathParam("option") String aprobado) {
        api = new ApiImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.getSolicitudesDespacho(usuario, aprobado);
    }

    @GET
    @Path("/get-solicitudes-despacho-detalle/{option}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getSolicitudesDespachoDetalle(@PathParam("option") String codigoSolicitud) {
        api = new ApiImpl();
        return api.getSolicitudesDespachoDetalle(codigoSolicitud);
    }
    
    @GET
    @Path("/get-solicitud-despacho/{option}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getSolicitudeDespacho(@PathParam("option") String codigoSolicitud) {
        api = new ApiImpl();
        return api.getSolicitudeDespacho(codigoSolicitud);
    }

    @POST
    @Path("/aprobar-solicitud-despacho")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response aprobarSolicitudDespacho(String json) {
        api = new ApiImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.aprobarSolicitudDespacho(json, usuario);
    }
    
    @POST
    @Path("/get-reporte-solicitudes-despacho")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response getReporteSolicitudesDespacho(String json) {
        api = new ApiImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.getReporteSolicitudesDespacho(usuario, json);
    }

    @POST
    @Path("/generar-certificacion-final")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
   // @Audited
    public Response generarCertificacionFinal(String json) {
        api = new ApiImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.generarCertificacionFinal(usuario, json, false);
    }
    
    @POST
    @Path("/firmar-certificacion-final")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
   // @Audited
    public Response firmarCertificacionFinal(String json) {
        api = new ApiImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.generarCertificacionFinal(usuario, json, true);
    }
    
    @POST
    @Path("/save-certificacion-final")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response saveCertificacionFinal(String json) {
        api = new ApiImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.saveCertificacionFinal(json, usuario);
    }
    
    @POST
    @Path("/get-consulta-certificados")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response getConsultaCertificados(String json) {
        api = new ApiImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.getConsultaCertificados(json);
    }
    
    @POST
    @Path("/aprobar-certificado")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response aprobarCertificados(String json) {
        api = new ApiImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.aprobarCertificados(json, usuario);
    }
    
    @POST
    @Path("/anular-certificado")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response anularCertificados(String json) {
        api = new ApiImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.anularCertificados(json, usuario);
    }
    
    @POST
    @Path("/get-grupos-proveedor/{option}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getGruposProveedor(@PathParam("option") String item) {
        api = new ApiImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.getGruposProveedor(item, usuario);
    }

    @POST
    @Path("/insert-grupos-proveedor")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response insertGruposProveedor(String json) {
        api = new ApiImpl();
        return api.insertGruposProveedor(json);
    }

    @POST
    @Path("/delete-grupos-proveedor/{option}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteGruposProveedor(@PathParam("option") String item) {
        api = new ApiImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.deleteGruposProveedor(item, usuario);
    }
    
    @POST
    @Path("/get-centros-acopios-proveedor/{option}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getCentroAcopiosProveedor(@PathParam("option") String item) {
        api = new ApiImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.getCentroAcopiosProveedor(item, usuario);
    }

    @POST
    @Path("/insert-centros-acopios-proveedor")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response insertCentroAcopiosProveedor(String json) {
        api = new ApiImpl();
        return api.insertCentroAcopiosProveedor(json);
    }

    @POST
    @Path("/delete-centros-acopios-proveedor/{option}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteCentroAcopiosProveedor(@PathParam("option") String item) {
        api = new ApiImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.deleteCentroAcopiosProveedor(item, usuario);
    }
    
    @GET
    @Path("/get-documentos")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getDocumentos() {
        api = new ApiImpl();
        return api.getDocumentos();
    }
    
    @GET
    @Path("/get-combo-cuentas-cajas")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getComboCuentas() {
        api = new ApiImpl();
        return api.getComboCuentasCajas();
    }
    
    @GET
    @Path("/get-tipificacion/{option}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getTipificacion(@PathParam("option") String item) {
        api = new ApiImpl();
        return api.getTipificacion(item);
    }
    
    @GET
    @Path("/get-patios")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getPatios() {
        api = new ApiImpl();
        return api.getPatios();
    }
    
    @POST
    @Path("/get-retanqueos")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getReporteRetanqueos(String json) {
        api = new ApiImpl();
        return api.getReporteRetanqueos(json);
    }
    
    @GET
    @Path("/get-gastos-caja-pendiente/{option}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getGastosCajasPendientes(@PathParam("option") String aprobado) {
        api = new ApiImpl();
        return api.getGastosCajasPendientes(aprobado);
    }
    
    @POST
    @Path("/get-ordenes-cargue")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response getOrdenesCargue(String json) {
        api = new ApiImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.getOrdenesCargue(json, usuario);
    }
    
    @POST
    @Path("/save-remision")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response guardarRemision(String json) {
        api = new ApiImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.guardarRemision(json,  usuario);
    }
    
    @GET
    @Path("/get-tabla-gen/{option}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getTablaGen(@PathParam("option") String code) {
        api = new ApiImpl();
        return api.getTablaGen(code);
    }
    
    @POST
    @Path("/aprobar-caja")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response aprobarCaja(String json) {
        api = new ApiImpl();
         usuario = (Usuario) requestContext.getProperty("user");
        return api.aprobarCaja(json,usuario);
    }
    
    @GET
    @Path("/get-configuracion-cajas")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response getConfiguracionCajas() {
        api = new ApiImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.getConfiguracionCajas(usuario);
    }
    
    
    @POST
    @Path("/get-reporte-gastos-caja")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getReporteGastosCaja(String json) {
        api = new ApiImpl();
        return api.getReporteGastosCaja(json);
    }
    
    @GET
    @Path("/get-reporte-gastos-retanqueo/{option}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getReporteGastosRetanqueo(@PathParam("option") int idCaja) {
        api = new ApiImpl();
        return api.getReporteGastosRetanqueo(idCaja);
    }
    
    @GET
    @Path("/get-detalle-gasto/{option}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getDetalleGasto(@PathParam("option") int idGasto) {
        api = new ApiImpl();
        return api.getDetalleGasto(idGasto);
    }
    
    @POST
    @Path("/save-configuracion-caja")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response guardarConfiguracionCaja(String json) {
        api = new ApiImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.configuracionCaja(json,  usuario,"INSERT");
    }
    
    @POST
    @Path("/update-configuracion-caja")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response updateConfiguracionCaja(String json) {
        api = new ApiImpl();
        usuario = (Usuario) requestContext.getProperty("user");
         return api.configuracionCaja(json,  usuario,"UPDATE");
    }
    
    @GET
    @Path("/get-all-configuracion-cajas")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response getAllConfiguracionCajas() {
        api = new ApiImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.getAllConfiguracionCajas(usuario);
    }
    
    @POST
    @Path("/import-gastos-caja")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response importGastosCaja(String json) {
        api = new ApiImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.importGastosCaja(json, usuario);
    }
    
    @POST
    @Path("/eliminar-item-detalle-despacho")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteItemDetalleDespacho(String json) {
        api = new ApiImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.deleteItemDetalleDespacho(json, usuario);
    }
    
    @POST
    @Path("/crear-cliente")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response crearCliente(String json) {
        api = new ApiImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.crearCliente(json,  usuario);
    }
    
    @GET
    @Path("/get-combo-clientes/{option}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getClientes(@PathParam("option") String tipo) {
        api = new ApiImpl();
        return api.getClientes(tipo);
    }
    
    @POST
    @Path("/get-ordenes-ingreso-all")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getOrdenesIngresoAll(String json) {
        api = new ApiImpl();
        return api.getOrdenesIngresoAll(json);
    }
    
    @POST
    @Path("/certificacion-final-masiva")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response certificacionFinalMasiva(String json) {
        api = new ApiImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.certificacionFinalMasiva(json, usuario);
    }
    
    @GET
    @Path("/get-tipificacion-all")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getTipificacionAll() {
        api = new ApiImpl();
        return api.getTipificacionAll();
    }
    
    @POST
    @Path("/save-tipificacion")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response guardarInventarioTipificacion(String json) {
        api = new ApiImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.guardarInventarioTipificacion(json, usuario);
    }
    
    @GET
    @Path("/get-materiales")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getMateriales() {
        api = new ApiImpl();
        return api.getMateriales();
    }
    
    @POST
    @Path("/get-informe-certificados")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response getInformeCertificados(String json) {
        api = new ApiImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.getInformeCertificados(json);
    }
    
    @GET
    @Path("/get-propietarios-informe")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getPropietariosInforme() {
        api = new ApiImpl();
        return api.getPropietariosInforme();
    }
    
    @POST
    @Path("/save-fletes")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response saveFletes(String json) {
        api = new ApiImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.saveFletes(json, usuario);
    }
    
    @POST
    @Path("/get-fletes")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response getFletes(String json) {
        api = new ApiImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.getFletes(usuario, json);
    }
    
    @GET
    @Path("/get-fletes-detalle/{option}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getFletesDetalle(@PathParam("option") String codigoSolicitud) {
        api = new ApiImpl();
        return api.getFletesDetalle(codigoSolicitud);
    }
    
    @GET
    @Path("/get-fletes-orden")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getFletesOrden() {
        api = new ApiImpl();
        return api.getFletesOrden();
    }
    
    @GET
    @Path("/get-fletes-orden-detalle/{option}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getFletesOrdenDetalle(@PathParam("option") String codigoSolicitud) {
        api = new ApiImpl();
        return api.getFletesOrdenDetalle(codigoSolicitud);
    }
    
    @GET
    @Path("/get-orden-detalle/{option}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getOrdenDetalle(@PathParam("option") String codigoOrden) {
        api = new ApiImpl();
        return api.getOrdenDetalle(codigoOrden);
    }
   
    
    @GET
    @Path("/get-certificacion-final/{option}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Audited
    public Response getCerificacionFinal(@PathParam("option") int tipo) {
        api = new ApiImpl();
        usuario = (Usuario) requestContext.getProperty("user");
        return api.getCertificacionFinal(tipo, usuario);
    }
}
