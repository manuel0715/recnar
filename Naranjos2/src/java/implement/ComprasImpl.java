/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package implement;

import com.google.gson.JsonObject;
import interfaz.ComprasInterfaz;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Response;
import util.ConnectionDataBase;
import util.Usuario;
import util.Util;

/**
 *
 * @author Administrator
 */
public class ComprasImpl extends ConnectionDataBase implements ComprasInterfaz {

    public static final String SECRET_KEY = "oeRaYY7Wo24sDqKSX3IM9ASGmdGPmkTd9jo1QTy4b7P9Ze5_9hKolVX8xNrQDcNRfVEdTZNOMAuOyqEGhXEbdJI-ZQ19k_o9MI0y3eZN2lp9YELjoNUEw55FfXMiINEdt1XR85ViBORIpRLSOkT6kSpzs2x-jbLDiz9iFVzkESd8ES1YKxMgPCAGAA7VfZeQUm4n-mOmnWMaVX30zMARGFU4L3oPBctYKkl4dYfqYWqRNfrgPCAJVi5DGFjywgxx0ASEiJHtV72paI3fDR2XwlSkyhhmY-ICjCRmsJN4fX1pdoL8a18-aQrvyu4j0Os6dVPYIoPvvY0SAZtWYKHfM15g7A3HD4cVREf9cUsprCRK93w";
    public static final String URL_SERVER = "3.132.57.72:8085";
    public static final String UPLOAD_FILE_SERVER = "D:\\Naranjos 2.0\\apache-tomcat-8.5.69\\webapps\\ROOT\\Documentos_Naranjos\\";
    public static final String UPLOAD_PHOTO_MANTENIMIENTO = "D:\\Naranjos 2.0\\apache-tomcat-8.5.69\\webapps\\ROOT\\mantenimiento\\";

    @Override
    public Response getProductos(int id_categoria) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        query = "select p.*, c.nombre as nombre_categoria\n"
                + "from compras.productos p\n"
                + "inner join compras.categorias c on c.id = p.id_categoria \n"
                + "where case when ? = 0 then p.id_categoria is not null else p.id_categoria = ? end\n"
                + "ORDER BY p.nombre;";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setInt(1, id_categoria);
                ps.setInt(2, id_categoria);
                ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar tipo equipos", ex.toString(), "/get-tipo-equipos");
            response = Util.responseApi(Util.getJsonString(respObject), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            try {
                Util.desconectarBd(con, rs, ps, null, this.getClass());
            } catch (SQLException ex) {
                Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return response;
    }

    @Override
    public Response insertProducto(String json, Usuario usuario) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        JsonObject parameter = Util.readJsonObjectParameter(json);

        query = "SELECT compras.crear_producto(?::json, ?::varchar) as resp;";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, json);
                ps.setString(2, usuario.getId_usuario());
                rs = ps.executeQuery();
                if (rs.next()) {
                    respObject = Util.writreResponseString(rs.getString("resp"));
                    response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            respObject = Util.writeResponseError("Crear producto", ex.toString(), "/insert-producto");
            response = Util.responseApi(Util.getJsonString(respObject), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            try {
                Util.desconectarBd(con, rs, ps, null, this.getClass());
            } catch (SQLException ex) {
                Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return response;
    }

    @Override
    public Response saveSolicitudCompra(String json, Usuario usuario) {
        Response response = null;
        JsonObject respObject = null;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "SELECT compras.guardar_solicitud_compras(?::json, ?::varchar) as resp";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, json);
                ps.setString(2, usuario.getId_usuario());
                rs = ps.executeQuery();
                if (rs.next()) {
                    respObject = Util.writreResponseString(rs.getString("resp"));
                    response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
                }
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Guardar solicitud compra", ex.toString(), "/save-solicitud-compra");
            response = Util.responseApi(Util.getJsonString(respObject), Response.Status.INTERNAL_SERVER_ERROR);
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                Util.desconectarBd(con, rs, ps, null, this.getClass());
            } catch (SQLException ex) {
                Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return response;
    }

    @Override
    public Response insertCategoria(String json, Usuario usuario) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        JsonObject parameter = Util.readJsonObjectParameter(json);

        query = "SELECT compras.crear_categoria(?::json, ?::varchar) as resp;";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, json);
                ps.setString(2, usuario.getId_usuario());
                rs = ps.executeQuery();
                if (rs.next()) {
                    respObject = Util.writreResponseString(rs.getString("resp"));
                    response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            respObject = Util.writeResponseError("Crear categoria", ex.toString(), "/insert-categoria");
            response = Util.responseApi(Util.getJsonString(respObject), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            try {
                Util.desconectarBd(con, rs, ps, null, this.getClass());
            } catch (SQLException ex) {
                Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return response;
    }

    @Override
    public Response getCategorias() {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        query = "SELECT * FROM compras.categorias ORDER BY nombre asc";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar tipo equipos", ex.toString(), "/get-tipo-equipos");
            response = Util.responseApi(Util.getJsonString(respObject), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            try {
                Util.desconectarBd(con, rs, ps, null, this.getClass());
            } catch (SQLException ex) {
                Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return response;
    }

    @Override
    public Response insertProveedor(String json, Usuario usuario) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        JsonObject parameter = Util.readJsonObjectParameter(json);

        query = "SELECT compras.crear_proveedor(?::json, ?::varchar) as resp;";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, json);
                ps.setString(2, usuario.getId_usuario());
                rs = ps.executeQuery();
                if (rs.next()) {
                    respObject = Util.writreResponseString(rs.getString("resp"));
                    response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            respObject = Util.writeResponseError("Crear categoria", ex.toString(), "/insert-categoria");
            response = Util.responseApi(Util.getJsonString(respObject), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            try {
                Util.desconectarBd(con, rs, ps, null, this.getClass());
            } catch (SQLException ex) {
                Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return response;
    }

    @Override
    public Response getProveedores() {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        query = "SELECT p.* FROM compras.proveedores p\n"
                + " ORDER BY p.nombre asc";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar proveedores", ex.toString(), "/get-proveedores");
            response = Util.responseApi(Util.getJsonString(respObject), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            try {
                Util.desconectarBd(con, rs, ps, null, this.getClass());
            } catch (SQLException ex) {
                Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return response;
    }

    @Override
    public Response cambiarEstadoOrden(Usuario usuario, String json) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        JsonObject parameter = Util.readJsonObjectParameter(json);

        query = "SELECT compras.cambiar_estado_orden(?::json, ?::varchar) as resp;";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, json);
                ps.setString(2, usuario.getId_usuario());
                rs = ps.executeQuery();
                if (rs.next()) {
                    respObject = Util.writreResponseString(rs.getString("resp"));
                    response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            respObject = Util.writeResponseError("Crear categoria", ex.toString(), "/insert-categoria");
            response = Util.responseApi(Util.getJsonString(respObject), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            try {
                Util.desconectarBd(con, rs, ps, null, this.getClass());
            } catch (SQLException ex) {
                Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return response;
    }

    @Override
    public Response getRequisiciones(String json, Usuario usuario) {
        String query;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Response response = null;
        JsonObject respObject;

        JsonObject parameter = Util.readJsonObjectParameter(json);

        query = "select c.nombre as nombre_tipo_requisicion, * \n"
                + "from compras.requisiciones r\n"
                + "inner join compras.categorias c on r.id_tipo_requisicion = c.id\n"
                + "inner join patios p on p.id = r.patio_id\n"
                + "where case when ? = 'ALL' THEN r.estado in ('P', 'A', 'F') else r.estado = ? end\n"
                + "AND r.patio_id IN (SELECT id_patio  FROM rel_usuarios_patios rup WHERE rup.usuario = ?);";
        try {
            con = Util.conectarBD();
            if (con != null) {
                // query = query.replaceAll("#PATIO", filtroPatio).replaceAll("#TIPOEQUIPO", filtroTipoEquipo);
                ps = con.prepareStatement(query);
                ps.setString(1, parameter.get("estado").getAsString());
                ps.setString(2, parameter.get("estado").getAsString());
                ps.setString(3, usuario.getId_usuario());
                ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }

        } catch (SQLException ex) {
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            respObject = Util.writeResponseError("Cargar Mantenimientos", ex.toString(), "/get-mantenimintos");
            response = Util.responseApi(Util.getJsonString(respObject), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            try {
                Util.desconectarBd(con, rs, ps, null, this.getClass());
            } catch (SQLException ex) {
                Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return response;
    }

    @Override
    public Response insertRequisicion(String json, Usuario usuario) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        JsonObject parameter = Util.readJsonObjectParameter(json);

        query = "SELECT compras.crear_requisicion(?::json, ?::varchar) as resp;";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, json);
                ps.setString(2, usuario.getId_usuario());
                rs = ps.executeQuery();
                if (rs.next()) {
                    respObject = Util.readJsonObjectParameter(rs.getString("resp"));
                    response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            respObject = Util.writeResponseError("Crear categoria", ex.toString(), "/insert-categoria");
            response = Util.responseApi(Util.getJsonString(respObject), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            try {
                Util.desconectarBd(con, rs, ps, null, this.getClass());
            } catch (SQLException ex) {
                Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return response;
    }

    @Override
    public Response getRequisicionDetalle(String codigoRequisicion) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        query = "select rq.codigo_requisicion, rq.cantidad, p.codigo_producto, p.nombre as nombre_producto from compras.requisicion_detalle rq\n"
                + "inner join compras.productos p on p.codigo_producto = rq.codigo_producto \n"
                + "where rq.codigo_requisicion = ? order by rq.id asc;";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, codigoRequisicion);
                ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar requisicion detalle", ex.toString(), "/get-requisicion-detalle");
            response = Util.responseApi(Util.getJsonString(respObject), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            try {
                Util.desconectarBd(con, rs, ps, null, this.getClass());
            } catch (SQLException ex) {
                Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return response;
    }

    @Override
    public Response getCotizaciones(String json, Usuario usuario) {
        String query;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Response response = null;
        JsonObject respObject;

        JsonObject parameter = Util.readJsonObjectParameter(json);

        query = "select * \n"
                + "from compras.cotizaciones \n"
                + "where case when ? = 'ALL' THEN estado in ('P', 'A', 'F') else estado = ? END;";
        try {
            con = Util.conectarBD();
            if (con != null) {
                // query = query.replaceAll("#PATIO", filtroPatio).replaceAll("#TIPOEQUIPO", filtroTipoEquipo);
                ps = con.prepareStatement(query);
                ps.setString(1, parameter.get("estado").getAsString());
                ps.setString(2, parameter.get("estado").getAsString());
                ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }

        } catch (SQLException ex) {
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            respObject = Util.writeResponseError("Cargar Mantenimientos", ex.toString(), "/get-mantenimintos");
            response = Util.responseApi(Util.getJsonString(respObject), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            try {
                Util.desconectarBd(con, rs, ps, null, this.getClass());
            } catch (SQLException ex) {
                Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return response;
    }

    @Override
    public Response getCotizacionDetalle(String codigoRequisicion) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        String json = "{}";
        query = "select json_build_object('data', json_agg(row_to_json(d)::json)) as json\n"
                + "                from (\n"
                + "                	select pc.*, p.nombre as nombre_proveedor, pc.aplica_descuento, pc.valor_descuento, (\n"
                + "                		select array_to_json(array_agg(row_to_json(d))) from (\n"
                + "                			select dc.*, dc.precio_unitatio * dc.cantidad as valor_producto, pr.nombre as nombre_producto \n"
                + "                			from compras.detalle_cotizacion dc \n"
                + "                         	inner join compras.productos pr on pr.codigo_producto = dc.codigo_producto \n"
                + "                			where dc.codigo_cotizacion = pc.codigo_cotizacion order by dc.id asc\n"
                + "                		) d\n"
                + "                	) as detalle from compras.proveedores_cotizacion pc \n"
                + "                	inner join compras.proveedores p on p.nit = pc.nit_proveedor\n"
                + "                	where pc.codigo_requisicion = ? \n"
                + "                	order by pc.valor_total\n"
                + "                ) d;";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, codigoRequisicion);
                rs = ps.executeQuery();

                if (rs.next()) {
                    json = rs.getString("json");
                }
                respObject = Util.getObjectJson(json);
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar tipo equipos", ex.toString(), "/get-tipo-equipos");
            response = Util.responseApi(Util.getJsonString(respObject), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            try {
                Util.desconectarBd(con, rs, ps, null, this.getClass());
            } catch (SQLException ex) {
                Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return response;
    }

    @Override
    public Response getCotizacionesAll(String json, Usuario usuario) {
        String query;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Response response = null;
        JsonObject respObject;

        JsonObject parameter = Util.readJsonObjectParameter(json);

        query = "select r.codigo_requisicion , pc.*, r.descripcion, \n"
                + "p.nombre as nombre_proveedor, p.direccion, p.telefono, p.*\n"
                + "from compras.requisiciones r\n"
                + "inner join compras.proveedores_cotizacion pc on r.codigo_requisicion = pc.codigo_requisicion\n"
                + "inner join compras.proveedores p on p.nit = pc.nit_proveedor and pc.estado = ?"
                + "AND r.patio_id IN (SELECT id_patio  FROM rel_usuarios_patios rup WHERE rup.usuario= ?);";
        try {
            con = Util.conectarBD();
            if (con != null) {
                // query = query.replaceAll("#PATIO", filtroPatio).replaceAll("#TIPOEQUIPO", filtroTipoEquipo);
                ps = con.prepareStatement(query);
                ps.setString(1, parameter.get("estado").getAsString());
                ps.setString(2, usuario.getId_usuario());
                ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }

        } catch (SQLException ex) {
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            respObject = Util.writeResponseError("Cargar Mantenimientos", ex.toString(), "/get-mantenimintos");
            response = Util.responseApi(Util.getJsonString(respObject), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            try {
                Util.desconectarBd(con, rs, ps, null, this.getClass());
            } catch (SQLException ex) {
                Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return response;
    }

    @Override
    public Response getCotizacionDetalleAprobadas(String codigoCotizacion) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        query = "select dc.*, p.*\n"
                + "from compras.detalle_cotizacion dc\n"
                + "inner join compras.productos p on p.codigo_producto = dc.codigo_producto\n"
                + "where dc.codigo_cotizacion  = ? order by dc.id asc;";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, codigoCotizacion);
                ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar requisicion detalle", ex.toString(), "/get-requisicion-detalle");
            response = Util.responseApi(Util.getJsonString(respObject), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            try {
                Util.desconectarBd(con, rs, ps, null, this.getClass());
            } catch (SQLException ex) {
                Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return response;
    }

    @Override
    public Response getOrdenesCompra(String json, Usuario usuario) {
        String query;
        String filtroFechas = "";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Response response = null;
        JsonObject respObject;

        JsonObject parameter = Util.readJsonObjectParameter(json);
        if (parameter != null) {
            if (!parameter.get("fecha_ini").getAsString().equals("")) {
                filtroFechas = "and date(oc.fecha_creacion) between '" + parameter.get("fecha_ini").getAsString() + "'::date and '" + parameter.get("fecha_fin").getAsString() + "'::date";
            }
        }

        query = "select oc.*, p.nombre as nombre_proveedor, p.direccion, p.telefono, pt.nombre_patio,\n"
                + "case when oc.forma_pago = 'CT' then 'CREDITO' else 'CONTADO' end as nombre_forma_pago,\n"
                + "case when oc.estado = 'P' then 'PENDIENTE' else 'FINALIZADA' end as nombre_estado\n"
                + "from compras.ordenes_compra oc\n"
                + "inner join compras.proveedores p on p.nit = oc.nit_proveedor \n"
                + "inner join patios pt on pt.id = oc.patio_id \n"
                + "where case when ? = 'ALL' then oc.estado in ('P', 'I') else oc.estado = ? end and\n"
                + "oc.patio_id IN (SELECT id_patio  FROM rel_usuarios_patios rup WHERE rup.usuario= ?) #FECHAS;";
        try {
            con = Util.conectarBD();
            if (con != null) {
                query = query.replaceAll("#FECHAS", filtroFechas);
                ps = con.prepareStatement(query);
                ps.setString(1, parameter.get("estado").getAsString());
                ps.setString(2, parameter.get("estado").getAsString());
                ps.setString(3, usuario.getId_usuario());
                ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }

        } catch (SQLException ex) {
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            respObject = Util.writeResponseError("Cargar ordenes de compra", ex.toString(), "/get-mantenimintos");
            response = Util.responseApi(Util.getJsonString(respObject), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            try {
                Util.desconectarBd(con, rs, ps, null, this.getClass());
            } catch (SQLException ex) {
                Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return response;
    }

    @Override
    public Response getOrdenesCompraDetalle(String codigoCotizacion) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        query = "select doc.cantidad, p.*\n"
                + "from compras.detalle_ordenes_compra doc\n"
                + "inner join compras.productos p on p.codigo_producto = doc.codigo_producto\n"
                + "where doc.codigo_orden_compra = ? order by doc.id asc;";

        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, codigoCotizacion);
                ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar requisicion detalle", ex.toString(), "/get-requisicion-detalle");
            response = Util.responseApi(Util.getJsonString(respObject), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            try {
                Util.desconectarBd(con, rs, ps, null, this.getClass());
            } catch (SQLException ex) {
                Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return response;
    }
    
    public Response getOrdenesCompraDetallePendiente(String codigoCotizacion) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        query = "select doc.cantidad_pendiente as cantidad, p.*\n"
                + "from compras.detalle_ordenes_compra doc\n"
                + "inner join compras.productos p on p.codigo_producto = doc.codigo_producto\n"
                + "where doc.codigo_orden_compra = ? and doc.cantidad_pendiente != 0 order by doc.id asc;";

        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, codigoCotizacion);
                ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar requisicion detalle", ex.toString(), "/get-requisicion-detalle");
            response = Util.responseApi(Util.getJsonString(respObject), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            try {
                Util.desconectarBd(con, rs, ps, null, this.getClass());
            } catch (SQLException ex) {
                Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return response;
    }

    @Override
    public Response getStockDisponible(Usuario usuario) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        query = "select s.codigo_producto, s.stock_actual, p.nombre\n"
                + "from inventario.stock s\n"
                + "inner join compras.productos p on p.codigo_producto = s.codigo_producto \n"
                + "where s.patio_id IN (SELECT id_patio FROM rel_usuarios_patios rup WHERE rup.usuario = ?);";

        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, usuario.getId_usuario());
                ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar stock", ex.toString(), "/get-stock-dispoonible");
            response = Util.responseApi(Util.getJsonString(respObject), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            try {
                Util.desconectarBd(con, rs, ps, null, this.getClass());
            } catch (SQLException ex) {
                Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return response;
    }

    @Override
    public Response getRemisiones(String codigoOrdenCompra) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        String json = "{}";
        query = "select json_build_object('data', json_agg(row_to_json(d)::json)) as json\n"
                + "from (\n"
                + "	select rc.id, rc.fecha_creacion, rc.codigo_remision, rc.usuario_creador, p.nombre_patio, (\n"
                + "		select array_to_json(array_agg(row_to_json(d))) from (\n"
                + "			select i.*, p.nombre\n"
                + "			from inventario.ingresos i\n"
                + "			inner join compras.productos p on p.codigo_producto = i.codigo_producto\n"
                + "			where i.codigo_remision = rc.codigo_remision\n"
                + "		) d\n"
                + "	) as ingresos from compras.remision_compras rc \n"
                + "	inner join patios p on p.id = rc.patio_id \n"
                + "	where codigo_orden_compra = ?\n"
                + "	order by rc.fecha_creacion\n"
                + ") d";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, codigoOrdenCompra);
                rs = ps.executeQuery();

                if (rs.next()) {
                    json = rs.getString("json");
                }
                respObject = Util.getObjectJson(json);
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar remisiones", ex.toString(), "/get-remisiones");
            response = Util.responseApi(Util.getJsonString(respObject), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            try {
                Util.desconectarBd(con, rs, ps, null, this.getClass());
            } catch (SQLException ex) {
                Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return response;
    }
}
