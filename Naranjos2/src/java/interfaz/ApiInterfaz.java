/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interfaz;

import java.io.InputStream;
import java.util.List;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import util.Usuario;

/**
 *
 * @author usuario
 */
public interface ApiInterfaz {

    /**
     *
     * @param json
     * @return
     */
    public Response loginApi(String json);

    public Response generarOrdenIngreso(String json, InputStream input, Usuario usuario);

    public Response getOrdenIngreso(String json,Usuario usuario);

    public Response getDetalleOrdenIngreso(String json);

    public Response getComboMunicipios();

    public Response getComboProveedor();

    public Response getComboAutorizado();

    public Response getComboAutorizacion();

    public Response getComboMotivo();

    public Response getComboResponsable();

    public Response getDocumentosRequeridosProveedor(String json);

    public Response guardarDesintegracionDocumentalInicial(String json,List<FormDataBodyPart> parts, Usuario usuario);

    public Response getDocumentacionInicialCargada(String cod_inspeccion);

    public Response finalizarDesintegracionDocumental(String cod_inspeccion, Usuario usuario);

    public Response getOrdenIngresoProcesadas(String json, Usuario usuario);

    public Response getDetalleOrdenIngresoProcesadas(String json);

    public Response getComponentesClaseArticulo(String json);

    public Response guardarArchivoIngresoRemoto(String json, List<FormDataBodyPart> parts, Usuario usuario);

    public Response finalizarIngresoRemoto(String cod_ingreso_remoto, Usuario usuario);

    public Response getOrdenIngresoRemotoNoFinalizadas(String json);

    public Response getDetalleIngresoRemoto(String cod_ingreso_remoto);

    public Response getOrdenIngresoRemotoFinalizadas(String json, Usuario usuario);

    public Response getComboPatios(Usuario usuario);

    public Response guardarTraslado(String json, Usuario usuario);

    public Response finalizarItemIngresoRemoto(int id_detalle_orden_ingreso, Usuario usuario);

    public Response getTraslados(Usuario usuario);

    public Response guardarOrdenDetalleIngresoRemoto(String json, Usuario usuario);

    public Response getChecksDetalleIngresoRemoto(int id_detalle_ingreso_remoto);

    public Response getDetalleIngresoRemotoSinRemision(String cod_ingreso_remoto);

    public Response guardarIngresoLocal(String json, Usuario usuario);

    public Response getDetalleTrasladoSinProcesar(String cod_ingreso_remoto);

    public Response updateOrdenIngreso(String json,  Usuario usuario);

    public Response updateDetalleOrdenIngreso(String json, Usuario usuario);

    public Response deleteOrdenIngreso(String json, Usuario usuario);

    public Response deleteDetalleOrdenIngreso(int idDetalle, Usuario usuario);

    public Response getComboTipoArticulo();

    public Response getComboUnidadMedida();

    public Response getComboClaseArticulo();

    public Response getComboCategoria();

    public Response getComboClasificacion();

    public Response crearCronograma(String json, Usuario usuario);

    public Response getComboCronograma();

    public Response updateCronograma(String json, Usuario usuario);
     
    public Response updateCronogramaDetalle(String json, Usuario usuario);   

    public Response getCronograma();

    public Response getCronogramaDetalle(int idCronogramaDetalle);

    public Response getTrasladosDetalle(String codigoTraslado);

    public Response getDocumentosIngresoLocal(String json);

    public Response getCronogramaProveedor(String nit_proveedor);

    public Response insertCronogramaDetalle(String json, Usuario usuario);

    public Response getDependenciaCronograma(int idCronograma);

    public Response getOrdenIngresoLocalNoFinalizadas(Usuario usuario);

    public Response getDetalleIngresoLocal(String cod_ingreso_local);

    public Response getChecksDetalleIngresoLocal(int id_detalle_ingreso_local);

    public Response guardarArchivoIngresoLocal(String json, List<FormDataBodyPart> parts, Usuario usuario);

    public Response getOrdenIngresoLocalFinalizadas(Usuario usuario);

    public Response finalizarItemIngresoLocal(int id_detalle_orden_ingreso, Usuario usuario);

    public Response saveDesintegracionPaso1(String json,List<FormDataBodyPart> parts , Usuario usuario);

    public Response getPasoDesintegracion(String paso, Usuario usuario);

    public Response saveDesintegracionPaso2(String json,  Usuario usuario);

    public Response saveDesintegracionPaso3(String json, List<FormDataBodyPart> parts, Usuario usuario);

    public Response saveFileDesintegracion(String json, List<FormDataBodyPart> parts, Usuario usuario);
    
    public Response getDocumentosDesintegracion(int id_detalle_orden_ingreso);

    public Response getReporteOrdenIngreso();

    public Response reporteDetalleOrdenIngreso(String codOrdenIngreso);

    public Response reporteOrdenIngresoRemoto();

    public Response getEstadoItem(String json);

    public Response getUsuarios();

    public Response adminUsuario(String json, Usuario usuario, String opcion);

    public Response getProveedores();

    public Response adminProveedor(String json, Usuario usuario, String opcion);

    public Response getComboMenuOpciones();

    public Response getOrdenIngresoNoProcesadas(String json, Usuario usuario);

    public Response getInventarioItems();

    public Response getComboMaterialesRecuperados();

    public Response saveInventarioMaterialesRecuperados(String json, Usuario usuario);

    public Response getInventarioItemsPatios();

    public Response guardarDespacho(String json, Usuario usuario);

    public Response getItemFiles(int idDetalleOrdenIngreso);

    public Response guardarBitacora(String json, List<FormDataBodyPart> parts, Usuario usuario);

    public Response getBitacora(String codigo_orden_ingreso);

    public Response getBitacoraFile(int idBitacora);

    public Response getOrdenIngresoDd(String json, Usuario usuario);

    public Response guardarCaja(String json, Usuario usuario);

    public Response guardarGastosCaja(String json, Usuario usuario);

    public Response getGastosCaja(Usuario usuario,String json);

    public Response getIngresoRemotoPendientes();

    public Response getIngresoRemotoProceso();

    public Response getIngresoLocalPendientes();

    public Response getTrasladosPendientes();

    public Response getDesintegracionDocumentalPendientes(String json, Usuario usuario);

    public Response getGastosCajaAdmin(Usuario usuario);

    public Response guardarClaseArticulo(String json, Usuario usuario);

    public Response guardarComponente(String json, Usuario usuario);

    public Response guardarRelClaseComponentes(String json, Usuario usuario);

    public Response getComponentes(int idBitacora);

    public Response getComponentesAsignados(int idClase);

    public Response getZipArchivos(int idOrden);

    public Response getIngresosPatios(String json);

    public Response getDocumentsPending(String codigOrden);

    public Response getIngresosProveedores(String json);

    public Response getIngresosFechas(String json);

    public Response getInventarioRecnar(Usuario usuario);

    public Response deleteItemCronograma(String item, Usuario usuario);

    public Response getDependenciasProveedor(String item, Usuario usuario);

    public Response deleteDependenciasProveedor(String item, Usuario usuario);

    public Response insertDependenciasProveedor(String json);

    public Response getPropietarios();

    public Response getEstadoOrden(int idCronograma);

    public Response insertNovedadDetalleCronograma(String json, Usuario usuario);

    public Response getNovedadDetalleCronograma(Integer item);

    public Response getTotalAutomotoresCronograma(Integer item);   

    public Response saveSolicitudDespacho(String json, Usuario usuario);

    public Response getSolicitudesDespacho(Usuario usuario, String aprobado);

    public Response getSolicitudesDespachoDetalle(String codigoSolicitud);
    
    public Response getSolicitudeDespacho(String codigoSolicitud);

    public Response aprobarSolicitudDespacho(String json, Usuario usuario);

    public Response getReporteSolicitudesDespacho(Usuario usuario, String json);

    public Response generarCertificacionFinal(Usuario usuario, String json, boolean firma);

    public Response saveCertificacionFinal(String json, Usuario usuario);

    public Response getTotalIngresos();

    public Response getConsultaCertificados(String json);

    public Response aprobarCertificados(String json, Usuario usuario);

    public Response anularCertificados(String json, Usuario usuario);

    public Response getGruposProveedor(String item, Usuario usuario);

    public Response insertGruposProveedor(String json);

    public Response deleteGruposProveedor(String item, Usuario usuario);

    public Response getCentroAcopiosProveedor(String item, Usuario usuario);

    public Response insertCentroAcopiosProveedor(String json);

    public Response deleteCentroAcopiosProveedor(String item, Usuario usuario);

    public Response getDocumentos();

    public Response getComboCuentasCajas();

    public Response getTipificacion(String item);

    public Response getPatios();

    public Response getReporteRetanqueos(String json);

    public Response getGastosCajasPendientes(String aprobado);

    public Response getOrdenesCargue(String json, Usuario usuario);

    public Response guardarRemision(String json, Usuario usuario);

    public Response getTablaGen(String code);

    public Response aprobarCaja(String json,Usuario usuario);

    public Response getConfiguracionCajas(Usuario usuario);

    public Response getReporteGastosCaja(String json);

    public Response getReporteGastosRetanqueo(int idCaja);

    public Response getDetalleGasto(int idGasto);

    public Response configuracionCaja(String json, Usuario usuario, String action);

    public Response getAllConfiguracionCajas(Usuario usuario);

    public Response importGastosCaja(String json, Usuario usuario);

    public Response deleteItemDetalleDespacho(String json, Usuario usuario);

    public Response crearCliente(String json, Usuario usuario);

    public Response getClientes(String tipo);

    public Response getComponentesAll();

    public Response getOrdenesIngresoAll(String json);

    public Response certificacionFinalMasiva(String json, Usuario usuario);

    public Response getTipificacionAll();

    public Response guardarInventarioTipificacion(String json, Usuario usuario);

    public Response getMateriales();

    public Response getInformeCertificados(String json);

    public Response getPropietariosInforme();

    public Response insertPropietarios(String json, Usuario usuario);

    public Response saveFletes(String json, Usuario usuario);

    public Response getFletes(Usuario usuario, String json);

    public Response getFletesDetalle(String codigoSolicitud);

    public Response getFletesOrden();

    public Response getFletesOrdenDetalle(String codigoSolicitud);

    public Response getOrdenDetalle(String codigoOrden);

    public Response getCertificacionFinal(int tipo, Usuario usuario);

   }
