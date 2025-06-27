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
public interface PuntoVerdeInterfaz {

    public Response getActividadesAsignadas(Usuario usuario);

    public Response actualizarEstadoActividad(String json, Usuario usuario);

    public Response guardarTrazabilidadActividad(String json, Usuario usuario);

    public Response getActividades(String json, Usuario usuario);

    public Response getTrazabilidad(int idActividad);

    public Response getActividad(int idActividad);

    public Response gestionarCronograma(String json, Usuario usuario);

    public Response getCronogramas(String json, Usuario usuario);

    public Response getCronograma(int idCronograma);

    public Response getPuntosRecogida();

    public Response gestionarAsignaciones(String json, Usuario usuario);
    
}
