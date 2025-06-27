/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interfaz;

import javax.ws.rs.core.Response;
import util.Usuario;

/**
 *
 * @author Administrator
 */
public interface RrhhInterfaz {

    public Response cargarEmpleados();

    public Response registrarFacial(String json);

    public Response crearEmpleado(String json, Usuario usuario);

    public Response cargarCombos(String json);

    public Response getCiudades(String idDepartamento);

    public Response crearNovedad(String json, Usuario usuario);

    public Response getNovedadesEmpleado(String numeroDocumento);

    public Response getDocumentos();

    public Response gestionarNovedad(String json, Usuario usuario);

    public Response getFestivos(String ano);

    public Response getJefeDirecto();

    public Response getNovedadAprobacionJefe(String numeroDocumento, String fechaInit, String fechaFin);

    public Response getNovedadAprobacion(String numeroDocumento, String fechaInit, String fechaFin);;

    
}
