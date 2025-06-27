/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package implement;

import com.google.gson.JsonObject;
import interfaz.PuntoVerdeInterfaz;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Response;
import util.Usuario;
import util.Util;

/**
 *
 * @author Administrator
 */
public class PuntoVerdeImpl implements PuntoVerdeInterfaz{

    @Override
    public Response getActividadesAsignadas(Usuario usuario) {
       Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "select aa.id as id_actividad, car.fecha_actividad,\n" +
                "car.descripcion_actividad,\n" +
                "pr.nombre_punto,\n" +
                "pr.latitud,\n" +
                "pr.longitud,\n" +
                "aa.usuario_responsable,\n" +
                "u.nombres ||' ' ||u.apellidos as nombre_usuario_responsable,\n" +
                "aa.estado_actividad,\n" +
                "aa.fecha_inicio_actividad,\n" +
                "aa.fecha_finalizacion_actividad,\n" +
                "aa.fecha_creacion\n" +
                "from punto_verde.asignaciones_actividad  aa \n" +
                "inner join punto_verde.cronograma_actividades_recoleccion car on car.id =aa.cronograma_id \n" +
                "inner join punto_verde.puntos_recogida pr on pr.id =car.punto_recogida_id \n" +
                "inner join usuarios u on u.usuario =aa.usuario_responsable \n" +
                "where usuario_responsable =? and aa.estado_actividad!='FINALIZADO' and aa.estado='' ";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, usuario.getId_usuario());
                System.out.println(ps.toString());
                ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            respObject = Util.writeResponseError("Cargar actividades asignadas", ex.toString(), "/get-actividades-asignadas");
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
    public Response actualizarEstadoActividad(String json, Usuario usuario) {
        Response response = null;
        JsonObject respObject = null;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "SELECT  punto_verde.gestionar_actividad(?::json, ?::varchar) as resp";
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
            respObject = Util.writeResponseError("gestion de actividad", ex.toString(), "/actualizar-estado-actividad");
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
    public Response guardarTrazabilidadActividad(String json, Usuario usuario) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        JsonObject parameter = Util.readJsonObjectParameter(json);

        query = "INSERT INTO punto_verde.trazabilidad_actividad\n" +
                "(asignaciones_actividad_id, observacion_actividad, latitud, longitud, usuario_creador, fecha_creacion)\n" +
                "VALUES(?,?,?,?,?,now());";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setInt(2, parameter.get("id_actividad").getAsInt());
                ps.setString(1, parameter.get("observacion").getAsString());
                ps.setString(3, parameter.get("latitud").getAsString());
                ps.setInt(4, parameter.get("longitud").getAsInt());
                ps.setString(10, usuario.getId_usuario());
                if (ps.executeUpdate() > 0) {
                    respObject = Util.writreResponseString("OK");
                    response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            respObject = Util.writeResponseError("Guardar trazabilidad", ex.toString(), "/guardar-trazabilidad");
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
    public Response getActividades(String json, Usuario usuario) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "select aa.estado,aa.id as id_actividad, car.fecha_actividad,\n" +
                "car.descripcion_actividad,\n" +
                "pr.nombre_punto,\n" +
                "pr.latitud,\n" +
                "pr.longitud,\n" +
                "u.nombres ||' ' ||u.apellidos as usuario_responsable,\n" +
                "aa.estado_actividad,\n" +
                "aa.fecha_inicio_actividad,\n" +
                "aa.fecha_finalizacion_actividad,\n" +
                "aa.fecha_creacion\n" +
                "from punto_verde.asignaciones_actividad  aa \n" +
                "inner join punto_verde.cronograma_actividades_recoleccion car on car.id =aa.cronograma_id \n" +
                "inner join punto_verde.puntos_recogida pr on pr.id =car.punto_recogida_id \n" +
                "inner join usuarios u on u.usuario =aa.usuario_responsable ";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, usuario.getId_usuario());
                System.out.println(">>>"+ps.toString());
                ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            respObject = Util.writeResponseError("Cargar actividades", ex.toString(), "/get-actividades");
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
    public Response getTrazabilidad(int idActividad) {
         Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "select observacion_actividad, latitud, longitud, usuario_creador, fecha_creacion\n" +
                "FROM punto_verde.trazabilidad_actividad\n" +
                "WHERE asignaciones_actividad_id=?";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setInt(1, idActividad);
                ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            respObject = Util.writeResponseError("Cargar trazabilidad", ex.toString(), "/get-trazabilidad");
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
    public Response getActividad(int idActividad) {
          Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query =  "select aa.id as id_actividad, car.fecha_actividad,\n" +
                "car.descripcion_actividad,\n" +
                "pr.nombre_punto,\n" +
                "pr.latitud,\n" +
                "pr.longitud,\n" +
                "u.nombres ||' ' ||u.apellidos as usuario_responsable,\n" +
                "aa.estado_actividad,\n" +
                "aa.fecha_inicio_actividad,\n" +
                "aa.fecha_finalizacion_actividad,\n" +
                "aa.fecha_creacion\n" +
                "from punto_verde.asignaciones_actividad  aa \n" +
                "inner join punto_verde.cronograma_actividades_recoleccion car on car.id =aa.cronograma_id \n" +
                "inner join punto_verde.puntos_recogida pr on pr.id =car.punto_recogida_id \n" +
                "inner join usuarios u on u.usuario =aa.usuario_responsable \n" +
                "where aa.id =?";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setInt(1, idActividad);
                ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            respObject = Util.writeResponseError("Cargar trazabilidad", ex.toString(), "/get-trazabilidad");
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
    public Response gestionarCronograma(String json, Usuario usuario) {
       Response response = null;
        JsonObject respObject = null;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "SELECT  punto_verde.gestionar_cronograma(?::json, ?::varchar) as resp";
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
            respObject = Util.writeResponseError("gestion de cronograma", ex.toString(), "/gestionar cronograma");
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
    public Response getCronogramas(String json, Usuario usuario) {
       Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "select \n" +
                "    car.fecha_actividad,\n" +
                "    car.descripcion_actividad,\n" +
                "    pr.nombre_punto,pr.id as id_punto_recogida,\n" +
                "    pr.latitud,\n" +
                "    pr.longitud,\n" +
                "    car.fecha_creacion,\n" +
                "    car.usuario_creador,\n" +
                "    car.id as id_cronograma,\n" +
                "    aa.id as id_actividad,\n" +
                "    aa.estado,\n" +
                "    coalesce (u.nombres ||' ' ||u.apellidos,'SIN ASIGNAR') as usuario,\n" +
                "    coalesce (u.usuario,'SIN ASIGNAR') as usuario_responsable,\n" +
                "    coalesce(aa.estado_actividad,'SIN ASIGNAR') as estado_actividad,\n" +
                "    coalesce (to_char(aa.fecha_inicio_actividad,'yyyy-MM-dd hh:mm:ss') ,'SIN ASIGNAR') as fecha_inicio_actividad,\n" +
                "    coalesce (to_char(aa.fecha_finalizacion_actividad,'yyyy-MM-dd hh:mm:ss') ,'SIN ASIGNAR') as fecha_finalizacion_actividad,\n" +
                "    coalesce (to_char(aa.fecha_creacion,'yyyy-MM-dd hh:mm:ss') ,'SIN ASIGNAR') as fecha_asignacion\n" +
                "    from punto_verde.cronograma_actividades_recoleccion car \n" +
                "    inner join punto_verde.puntos_recogida pr on pr.id =car.punto_recogida_id\n" +
                "    left join punto_verde.asignaciones_actividad aa on car.id =aa.cronograma_id \n" +
                "    left join usuarios u on u.usuario =aa.usuario_responsable \n" +
                "    where aa.estado_actividad !='FINALIZADO' OR aa.estado_actividad IS NULL;";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                // ps.setString(1, usuario.getId_usuario());
                ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            respObject = Util.writeResponseError("Cargar cronogramas", ex.toString(), "/get-cronogramas");
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
    public Response getCronograma(int idCronograma) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query =  "select \n" +
                "	car.fecha_actividad,\n" +
                "    car.descripcion_actividad,\n" +
                "    pr.nombre_punto,pr.id as id_punto_recogida,\n" +
                "    pr.latitud,\n" +
                "    pr.longitud,\n" +
                "    car.fecha_creacion,\n" +
                "    car.usuario_creador,\n" +
                "    car.id as id_cronograma,\n" +
                "    coalesce (u.nombres ||' ' ||u.apellidos,'SIN ASIGNAR') as usuario_responsable,\n" +
                "    coalesce (u.usuario,'SIN ASIGNAR') as usuario_responsable,\n" +
                "    coalesce(aa.estado_actividad,'SIN ASIGNAR') as estado_actividad,\n" +
                "	coalesce (to_char(aa.fecha_inicio_actividad,'yyyy-MM-dd hh:mm:ss') ,'SIN ASIGNAR') as fecha_inicio_actividad,\n" +
                "	coalesce (to_char(aa.fecha_finalizacion_actividad,'yyyy-MM-dd hh:mm:ss') ,'SIN ASIGNAR') as fecha_finalizacion_actividad,\n" +
                "	coalesce (to_char(aa.fecha_creacion,'yyyy-MM-dd hh:mm:ss') ,'SIN ASIGNAR') as fecha_asignacion\n" +
                "    from punto_verde.cronograma_actividades_recoleccion car \n" +
                "    inner join punto_verde.puntos_recogida pr on pr.id =car.punto_recogida_id\n" +
                "    left join punto_verde.asignaciones_actividad aa on car.id =aa.cronograma_id \n" +
                "    left join usuarios u on u.usuario =aa.usuario_responsable \n" +
                "    where car.id =?; ";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setInt(1, idCronograma);
                ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            respObject = Util.writeResponseError("Cargar cronograma", ex.toString(), "/get-cronograma");
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
    public Response getPuntosRecogida() {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query =  "select * from punto_verde.puntos_recogida order by nombre_punto asc;";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            respObject = Util.writeResponseError("Cargar trazabilidad", ex.toString(), "/get-trazabilidad");
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
    public Response gestionarAsignaciones(String json, Usuario usuario) {
       Response response = null;
        JsonObject respObject = null;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "SELECT  punto_verde.gestionar_asignaciones(?::json, ?::varchar) as resp";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, json);
                ps.setString(2, usuario.getId_usuario());
                rs = ps.executeQuery();
                System.out.println(ps.toString());
                if (rs.next()) {
                    respObject = Util.writreResponseString(rs.getString("resp"));
                    response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
                }
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("gestion de cronograma", ex.toString(), "/gestionar cronograma");
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
    
}
