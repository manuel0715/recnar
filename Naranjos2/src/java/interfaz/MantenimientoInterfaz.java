/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interfaz;

import java.util.List;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import util.ConnectionDataBase;
import util.Usuario;

/**
 *
 * @author Administrator
 */
public interface MantenimientoInterfaz{

    public Response getComboEstados();

    public Response getComboFrecuencia();

    public Response getComboTipoMantenimiento();

    public Response getComboNovedades();

    public Response getComboResponsables();

    public Response getComboActividades(String json, Usuario usuario);

    public Response getComboMaquinas(String json, Usuario usuario);

    public Response insertMantenimiento(String json, Usuario usuario);

    public Response getMantenimientos(String json, Usuario usuario);

    public Response getTipoEquipos(String json, Usuario usuario);

    public Response insertEquipos(String json, Usuario usuario);

    public Response updateEquipo(String json, Usuario usuario);

    public Response guardarFotoMantenimiento(String json, List<FormDataBodyPart> parts, Usuario usuario);

    public Response crearFormulario(String json, Usuario usuario);

    public Response guardarRespuestas(String json, Usuario usuario);

    public Response getFormularios();

    public Response getTiposCampo();

    public Response getFormulario(int id_formulario);

    public Response soliciarMantenimiento(String json, Usuario usuario);

    public Response getSolicitudesMantenimiento();

    public Response getCategorias();

    public Response getDocumentosAsignados(int id_tipo_equipo);

    public Response getDocumentos();

    public Response crearTipoEquipo(String json, Usuario usuario);

    public Response crearSubcategorias(String json, Usuario usuario);

    public Response getSubcategorias();

    public Response getverificacion(int subcategoriaId);

    public Response guardarInspeccion(String json,Usuario usuario);

    public Response getActividades(int subcategoriaId);

    public Response getPropietariosMaquinas();

    public Response getClientesAlquiler();

    public Response getRespuestos();

    public Response getMarcas();

    public Response insertRepuestosReferencia(String json, Usuario usuario);

    public Response getRepuestosReferencia(String codigoMaquina);

    public Response getEquipoMaquina(String codigoMaquina);

    public Response getMantenimientoAsignado(String json, Usuario usuario);

    public Response getActividadesMantenimientoAsignado(String codigoMantenimiento);

    public Response getImspeccionesRealizadas(String json, Usuario usuario);

    public Response insertHorometro(String json, Usuario usuario);

    public Response getRevisionPendiente(String codigoQr);

    public Response getMantenimientosMaquina(String codigoMaquina);
   
}
