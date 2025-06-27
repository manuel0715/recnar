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
public interface ComprasInterfaz {

    public Response insertProducto(String json, Usuario usuario);

    public Response getProductos(int id_categoria);

    public Response saveSolicitudCompra(String json, Usuario usuario);

    public Response insertCategoria(String json, Usuario usuario);

    public Response getCategorias();

    public Response insertProveedor(String json, Usuario usuario);

    public Response getProveedores();

    public Response cambiarEstadoOrden(Usuario usuario, String json);

    public Response getRequisiciones(String json, Usuario usuario);

    public Response insertRequisicion(String json, Usuario usuario);

    public Response getRequisicionDetalle(String codigoRequisicion);

    public Response getCotizaciones(String json, Usuario usuario);

    public Response getCotizacionDetalle(String codigoRequisicion);

    public Response getCotizacionesAll(String json, Usuario usuario);

    public Response getCotizacionDetalleAprobadas(String codigoCotizacion);

    public Response getOrdenesCompra(String json, Usuario usuario);

    public Response getOrdenesCompraDetalle(String codigoCotizacion);

    public Response getOrdenesCompraDetallePendiente(String codigoCotizacion);

    public Response getStockDisponible(Usuario usuario);

    public Response getRemisiones(String codigoOrdenCompra);
    
}
