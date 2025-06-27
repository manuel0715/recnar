/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package implement;

import com.google.gson.JsonObject;
import static implement.ApiImpl.UPLOAD_FILE_SERVER;
import interfaz.MantenimientoInterfaz;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import util.ConnectionDataBase;
import util.Usuario;
import util.Util;

/**
 *
 * @author Administrator
 */
public class MantenimientoImpl extends ConnectionDataBase implements MantenimientoInterfaz {

    public static final String SECRET_KEY = "oeRaYY7Wo24sDqKSX3IM9ASGmdGPmkTd9jo1QTy4b7P9Ze5_9hKolVX8xNrQDcNRfVEdTZNOMAuOyqEGhXEbdJI-ZQ19k_o9MI0y3eZN2lp9YELjoNUEw55FfXMiINEdt1XR85ViBORIpRLSOkT6kSpzs2x-jbLDiz9iFVzkESd8ES1YKxMgPCAGAA7VfZeQUm4n-mOmnWMaVX30zMARGFU4L3oPBctYKkl4dYfqYWqRNfrgPCAJVi5DGFjywgxx0ASEiJHtV72paI3fDR2XwlSkyhhmY-ICjCRmsJN4fX1pdoL8a18-aQrvyu4j0Os6dVPYIoPvvY0SAZtWYKHfM15g7A3HD4cVREf9cUsprCRK93w";
    public static final String URL_SERVER = "3.132.57.72:8085";
    public static final String UPLOAD_FILE_SERVER = "D:\\Naranjos 2.0\\apache-tomcat-8.5.69\\webapps\\ROOT\\Documentos_Naranjos\\";
    public static final String UPLOAD_PHOTO_MANTENIMIENTO = "D:\\Naranjos 2.0\\apache-tomcat-8.5.69\\webapps\\ROOT\\mantenimiento\\";

    @Override
    public Response getComboEstados() {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        query = "SELECT id ,nombre \n"
                + "FROM mantenimiento.estados m  \n"
                + "ORDER BY nombre";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                rs = ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar Combo", ex.toString(), "/get-combo-estados");
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
    public Response getComboFrecuencia() {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        query = "SELECT id ,nombre \n"
                + "FROM mantenimiento.frecuencias m  \n"
                + "ORDER BY nombre";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                rs = ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar Combo", ex.toString(), "/get-combo-frecuencias");
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
    public Response getComboResponsables() {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        query = "SELECT id ,nombre,nit \n"
                + "FROM mantenimiento.responsables m  \n"
                + "ORDER BY nombre";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                rs = ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar Combo", ex.toString(), "/get-combo-responsables");
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
    public Response getComboTipoMantenimiento() {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        query = "SELECT id ,nombre \n"
                + "FROM mantenimiento.tipo_mantenimiento m  \n"
                + "ORDER BY nombre";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                rs = ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar Combo", ex.toString(), "/get-combo-tipo-mantenimiento");
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
    public Response getComboNovedades() {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        query = "SELECT id ,nombre \n"
                + "FROM mantenimiento.novedades m  \n"
                + "ORDER BY nombre";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                rs = ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar Combo", ex.toString(), "/get-combo-novedades");
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
    public Response getComboActividades(String json, Usuario usuario) {
        String query;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Response response = null;
        JsonObject respObject;

        JsonObject parameter = Util.readJsonObjectParameter(json);

        query = "select * from mantenimiento.actividades a where tipo_id = ? and id_subcategoria = ?;";
        try {
            con = Util.conectarBD();
            if (con != null) {
                // query = query.replaceAll("#PATIO", filtroPatio).replaceAll("#TIPOEQUIPO", filtroTipoEquipo);
                ps = con.prepareStatement(query);
                ps.setInt(1, parameter.get("tipo_id").getAsInt());
                ps.setInt(2, parameter.get("id_subcategoria").getAsInt());
                ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }

        } catch (SQLException ex) {
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            respObject = Util.writeResponseError("Cargar Combo", ex.toString(), "/get-combo-actividades");
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
    public Response getComboMaquinas(String json, Usuario usuario) {
        String query;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Response response = null;
        JsonObject respObject;

        JsonObject parameter = Util.readJsonObjectParameter(json);

        query = "select p.nombre_patio, te.nombre_tipo, s.nombre_subcategoria, m.*, pr.nombre_propietario\n"
                + "from mantenimiento.maquinas m\n"
                + "inner join public.patios p on p.id = m.id_patio\n"
                + "inner join mantenimiento.tipo_equipo te on te.id = m.id_tipo_equipo\n"
                + "inner join mantenimiento.subcategorias s on s.id = m.id_subcategoria\n"
                + "inner join mantenimiento.propietario pr on pr.identificacion_propietario = m.identificacion_propietario\n"
                + "where case when ? = 0 THEN m.id_patio is not null else id_patio = ? END\n"
                + "and case when ? = 0 THEN m.id_subcategoria is not null else m.id_subcategoria = ? END\n"
                + "and case when ? = 0 THEN m.id_tipo_equipo is not null else m.id_tipo_equipo = ? END";
        try {
            con = Util.conectarBD();
            if (con != null) {
                // query = query.replaceAll("#PATIO", filtroPatio).replaceAll("#TIPOEQUIPO", filtroTipoEquipo);
                ps = con.prepareStatement(query);
                ps.setInt(1, parameter.get("id_patio").getAsInt());
                ps.setInt(2, parameter.get("id_patio").getAsInt());
                ps.setInt(3, parameter.get("id_subcategoria").getAsInt());
                ps.setInt(4, parameter.get("id_subcategoria").getAsInt());
                ps.setInt(5, parameter.get("id_tipo_equipo").getAsInt());
                ps.setInt(6, parameter.get("id_tipo_equipo").getAsInt());
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
    public Response insertEquipos(String json, Usuario usuario) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        JsonObject parameter = Util.readJsonObjectParameter(json);

        query = "SELECT mantenimiento.crear_equipo(?::json, ?::varchar) as resp;";
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
            respObject = Util.writeResponseError("Crear mantenimiento", ex.toString(), "/insert-mantenimiento");
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
    public Response updateEquipo(String json, Usuario usuario) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        JsonObject parameter = Util.readJsonObjectParameter(json);

        query = "UPDATE mantenimiento.maquinas\n"
                + "		SET codigo = upper(?)::varchar, \n"
                + "		nombre = UPPER(?::VARCHAR), \n"
                + "		marca = UPPER(?::VARCHAR), \n"
                + "		modelo = UPPER(?::VARCHAR), \n"
                + "		serie = UPPER(?::VARCHAR), \n"
                + "		fecha_ingreso = ?::date, \n"
                + "		aplica_garantia = ?::VARCHAR, \n"
                + "		fecha_garantia = ?::date, \n"
                + "		id_tipo_equipo = ?::int, \n"
                + "		id_patio = ?::int, \n"
                + "		estado = ?::VARCHAR, \n"
                + "		kilometraje_inicial = ?::VARCHAR \n"
                + "             WHERE id = ?::int \n";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, parameter.get("codigo").getAsString());
                ps.setString(2, parameter.get("nombre").getAsString());
                ps.setString(3, parameter.get("marca").getAsString());
                ps.setString(4, parameter.get("modelo").getAsString());
                ps.setString(5, parameter.get("serie").getAsString());
                ps.setString(6, parameter.get("fecha_ingreso").getAsString());
                ps.setString(7, parameter.get("aplica_garantia").getAsString());
                ps.setString(8, parameter.get("fecha_garantia").getAsString());
                ps.setInt(9, parameter.get("id_tipo_equipo").getAsInt());
                ps.setInt(10, parameter.get("id_patio").getAsInt());
                ps.setString(11, parameter.get("estado").getAsString());
                ps.setString(12, parameter.get("kilometraje_inicial").getAsString());
                ps.setInt(13, parameter.get("id").getAsInt());
                if (ps.executeUpdate() > 0) {
                    respObject = Util.writreResponseString("OK");
                    response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
                } else {
                    respObject = Util.writreResponseString("No se lograron actualizar los campos");
                    response = Util.responseApi(Util.getJsonString(respObject), Response.Status.INTERNAL_SERVER_ERROR);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            respObject = Util.writeResponseError("Actualizar equipo", ex.toString(), "/update-equipos");
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
    public Response getTipoEquipos(String json, Usuario usuario) {
        String filtroTipoEquipo = "";
        String query;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Response response = null;
        JsonObject respObject;

        JsonObject parameter = Util.readJsonObjectParameter(json);
        if (parameter != null) {

            if (!parameter.get("id_subcategoria").getAsString().equals("")) {
                filtroTipoEquipo = "where te.id_subcategoria = '" + parameter.get("id_subcategoria").getAsInt() + "'";
            }
        }

        query = "select * from mantenimiento.tipo_equipo te #TIPOEQUIPO order by te.nombre_tipo asc;";
        try {
            con = Util.conectarBD();
            if (con != null) {
                query = query.replaceAll("#TIPOEQUIPO", filtroTipoEquipo);
                ps = con.prepareStatement(query);
                ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }

        } catch (SQLException ex) {
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            respObject = Util.writeResponseError("Cargar tipos equipos", ex.toString(), "/get-tipo-equipos");
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
    public Response getSolicitudesMantenimiento() {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        String json = "{}";
        query = "select json_build_object('data', json_agg(row_to_json(d)::json)) as json\n"
                + "from (\n"
                + "	select sm.*, m.nombre, p.nombre_patio, (\n"
                + "		select array_to_json(array_agg(row_to_json(d))) from (\n"
                + "			select p.nombre as repuesto, dsm.id_producto, dsm.cantidad from mantenimiento.detalle_solicitud_mantenimiento dsm \n"
                + "			inner join compras.productos p on p.id = dsm.id_producto\n"
                + "			where dsm.codigo_solicitud = sm.codigo_solicitud\n"
                + "		) d\n"
                + "	) as solicitudes from mantenimiento.solicitud_mantenimiento sm \n"
                + "	inner join mantenimiento.maquinas m on m.id = sm.id_maquina\n"
                + "	inner join public.patios p on p.id = sm.id_patio\n"
                + "	order by sm.fecha_creacion\n"
                + ") d";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
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
    public Response soliciarMantenimiento(String json, Usuario usuario) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        JsonObject parameter = Util.readJsonObjectParameter(json);

        query = "SELECT mantenimiento.solicitar_mantenimiento(?::json, ?::varchar) as resp;";
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
            respObject = Util.writeResponseError("Crear mantenimiento", ex.toString(), "/insert-mantenimiento");
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
    public Response insertMantenimiento(String json, Usuario usuario) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "SELECT mantenimiento.crear_mantenimiento(?::json, ?::varchar) as resp;";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, json);
                ps.setString(2, usuario.getId_usuario());
                System.out.println(ps.toString());
                rs = ps.executeQuery();
                if (rs.next()) {
                    respObject = Util.readJsonObjectParameter(rs.getString("resp"));
                    response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
                }
            }
        } catch (SQLException ex) {
            String fullErrorMessage = ex.getLocalizedMessage();
            String[] parts = fullErrorMessage.split("ERROR: ");
            String customErrorMessage = parts.length > 1 ? parts[1].split("Where:")[0].trim() : "Error desconocido";

            respObject = Util.writeResponseError("Guardar inspeccion", customErrorMessage, "/guardar-inspeccion");
            response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
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
    public Response getMantenimientos(String json, Usuario usuario) {
        String filtroActividad = "";
        String filtroMaquina = "";
        String filtroFrecuencia = "";
        String filtroNovedad = "";
        String filtroTipoMantenimiento = "";
        String filtroFechas = "";
        String query;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Response response = null;
        JsonObject respObject;

        JsonObject parameter = Util.readJsonObjectParameter(json);
        if (parameter != null) {
            if (!parameter.get("frecuencia_id").getAsString().equals("")) {
                filtroFrecuencia = "AND m.frecuencia_id = '" + parameter.get("frecuencia_id").getAsInt() + "'";
            }

            if (!parameter.get("tipo_id").getAsString().equals("")) {
                filtroTipoMantenimiento = "AND  m.tipo_id = '" + parameter.get("tipo_id").getAsInt() + "'";
            }

            if (!parameter.get("novedad_id").getAsString().equals("")) {
                filtroNovedad = "AND  m.novedad_id = '" + parameter.get("novedad_id").getAsInt() + "'";
            }

            if (!parameter.get("maquina_id").getAsString().equals("")) {
                filtroMaquina = "AND  m.maquina_id = '" + parameter.get("maquina_id").getAsInt() + "'";
            }

            if (!parameter.get("id_tipo_equipo").getAsString().equals("")) {
                filtroMaquina = "AND  ma.id_tipo_equipo = '" + parameter.get("id_tipo_equipo").getAsInt() + "'";
            }

            if (!parameter.get("fecha_ini").getAsString().equals("")) {
                filtroFechas = "and date(m.fecha_creacion) between '" + parameter.get("fecha_ini").getAsString() + "'::date and '" + parameter.get("fecha_fin").getAsString() + "'::date";
            }
        }

        query = "select m.id,m.tipo_id,m.titulo,\n"
                + "f.nombre as frecuencia,m.frecuencia_id, \n"
                + "ma.PLACA as placa,ma.codigo, m.maquina_id,m.codigo_mantenimiento,\n"
                + "te.id as tipo_equipo,te.nombre_tipo,\n"
                + "m.observacion,\n"
                + "m.fecha_mantenimiento::date,\n"
                + "m.usuario_creador,\n"
                + "m.realizado, p.nombre_patio,m.id_patio\n"
                + "from mantenimiento.mantenimientos as m\n"
                + "inner join mantenimiento.frecuencias f on m.frecuencia_id = f.id\n"
                + "inner join mantenimiento.maquinas ma on m.maquina_id = ma.id\n"
                + "inner join mantenimiento.tipo_equipo te on te.id = ma.id_tipo_equipo\n"
                + "inner join public.patios p on p.id = ma.id_patio\n"
                + "WHERE case when ? = 'ALL' THEN realizado in ('S','N') else realizado = ? END #FECHAS #FRECUENCIA #MAQUINA";
        try {
            con = Util.conectarBD();
            if (con != null) {
                query = query.replaceAll("#FRECUENCIA", filtroFrecuencia).replaceAll("#MAQUINA", filtroMaquina).replaceAll("#FECHAS", filtroFechas);
                ps = con.prepareStatement(query);
                ps.setString(1, parameter.get("realizado").getAsString());
                ps.setString(2, parameter.get("realizado").getAsString());
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
    public Response guardarFotoMantenimiento(String json, List<FormDataBodyPart> parts, Usuario usuario) {
        JsonObject parameter = Util.readJsonObjectParameter(json);
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null, ps2 = null;
        ResultSet rs = null;
        String fileName = "";
        String path;
        String query = "INSERT INTO mantenimiento.archivos\n"
                + "(codigo, nombre_archivo, path_archivo, usuario_creador, fecha_creacion)\n"
                + "VALUES(?,?,?,?,now());";

        String query2 = "update mantenimiento.mantenimientos \n"
                + "set observacion = ?::varchar,\n"
                + "realizado = 'S',\n"
                + "promedio = ?::varchar,\n"
                + "kilometraje = ?::varchar,\n"
                + "voltaje = ?::varchar,\n"
                + "parte_electrica = ?::varchar,\n"
                + "fecha_realizado = now(),\n"
                + "usuario_ultima_modificacion = ?::varchar\n"
                + "where id = ?::int;";

        String query3 = "update mantenimiento.maquinas \n"
                + "set kilometraje_actual = ?::varchar"
                + "where id = ?::int;";
        if (parameter != null) {
            for (FormDataBodyPart fileInputStream : parts) {
                fileName = buildNameFile(fileInputStream, parameter.get("nombre_archivo").getAsString() + "_" + parameter.get("id_mantenimiento").getAsString() + "_" + System.currentTimeMillis());
                path = loadArchivoServer(parameter.get("id_mantenimiento").getAsString(), fileName, fileInputStream, usuario, UPLOAD_PHOTO_MANTENIMIENTO);

                try {
                    con = Util.conectarBD();
                    if (con != null) {
                        ps = con.prepareStatement(query);
                        ps.setString(1, parameter.get("id_mantenimiento").getAsString());
                        ps.setString(2, fileName);
                        ps.setString(3, path);
                        ps.setString(4, usuario.getId_usuario());
                        ps.executeUpdate();

                        ps2 = con.prepareStatement(query2);
                        ps2.setString(1, parameter.get("observacion").getAsString());
                        ps2.setString(2, parameter.get("promedio").getAsString());
                        ps2.setString(3, parameter.get("kilometraje").getAsString());
                        ps2.setString(4, parameter.get("voltaje").getAsString());
                        ps2.setString(5, parameter.get("parte_electrica").getAsString());
                        ps2.setString(6, usuario.getId_usuario());
                        ps2.setInt(7, parameter.get("id_mantenimiento").getAsInt());
                        ps2.executeUpdate();

                        ps2 = con.prepareStatement(query2);
                        ps2.setString(1, parameter.get("kilometraje").getAsString());
                        ps2.setInt(2, parameter.get("id_maquina").getAsInt());
                        ps2.executeUpdate();
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    try {
                        Util.desconectarBd(con, rs, ps, null, this.getClass());
                    } catch (SQLException ex) {
                        Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

            }

            respObject = Util.writreResponseString("OK");
            response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);

        }
        return response;
    }

    public String loadArchivoServer(String codigo, String nombreArchivo, FormDataBodyPart fileInputStream, Usuario usuario, String pathServer) {

        InputStream inputStream;
        OutputStream outputStream;
        String ruta;

        inputStream = fileInputStream.getEntityAs(InputStream.class);

        ruta = pathServer + codigo;

        File f = new File(ruta);
        if (!f.exists()) {
            f.mkdirs();
        }

        ruta = ruta + "\\" + nombreArchivo;

        try {
            outputStream = new FileOutputStream(new File(ruta));
            int read;
            byte[] bytes = new byte[1024];
            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
            outputStream.flush();
            inputStream.close();
            outputStream.close();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        return ruta;

    }

    public String buildNameFile(FormDataBodyPart fileInputStream, String nameFile) {
        String ext;
        String mimeType;
        mimeType = fileInputStream.getMediaType().toString();

        switch (mimeType) {
            case "image/png":
                ext = ".png";
                break;
            case "image/jpeg":
                ext = ".jpg";
                break;
            case "application/msword":
                ext = ".doc";
                break;
            case "application/vnd.openxmlformats-officedocument.wordprocessingml.document":
                ext = ".docx";
                break;
            case "application/vnd.ms-excel":
                ext = ".xls";
                break;
            case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet":
                ext = ".xlsx";
                break;
            case "video/mpeg":
                ext = ".mpeg";
                break;
            case "video/mp4":
                ext = ".mp4";
                break;
            case "application/pdf":
                ext = ".pdf";
                break;
            default:
                ext = ".file";
                break;
        }

        return ext;
    }

    @Override
    public Response crearFormulario(String json, Usuario usuario) {
        Response response = null;
        JsonObject respObject = null;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "SELECT mantenimiento.crear_formulario_reutilizable(?::json, ?::varchar) as resp";
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
            respObject = Util.writeResponseError("Crear formulario", ex.toString(), "/crear-formulario");
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
    public Response guardarRespuestas(String json, Usuario usuario) {
        Response response = null;
        JsonObject respObject = null;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "SELECT mantenimiento.guardar_respuestas(?::json, ?::varchar) as resp";
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
            respObject = Util.writeResponseError("Guardar respuestas", ex.toString(), "/guardar-respuestas");
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
    public Response getFormularios() {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        query = "SELECT * FROM mantenimiento.formularios order by id asc";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar formularios", ex.toString(), "/get-formularios");
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
    public Response getTiposCampo() {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        query = "select * from mantenimiento.tipos_campo order by nombre asc;";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar tipos campo", ex.toString(), "/get-tipos-campo");
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
    public Response getFormulario(int id_formulario) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        String json = "{}";

        query = "select json_build_object(\n"
                + "        'id', f.id,\n"
                + "        'nombre', f.nombre,\n"
                + "        'descripcion', f.descripcion,\n"
                + "        'camposFormulario', (\n"
                + "            SELECT json_agg(\n"
                + "                json_build_object(\n"
                + "                    'id', cf.id,\n"
                + "                    'nombre', cf.nombre,\n"
                + "                    'idTipoCampo', cf.id_tipo_campo,\n"
                + "                    'itemsCampos', (\n"
                + "                        SELECT json_agg(json_build_object( 'id', ic.id, 'nombre', ic.nombre))\n"
                + "                        FROM mantenimiento.items_campos ic\n"
                + "                        WHERE ic.id_formulario = ? and ic.id_campo_formulario = cf.id\n"
                + "                    )\n"
                + "                )\n"
                + "            )\n"
                + "            FROM mantenimiento.campos_formulario cf\n"
                + "            WHERE cf.id_formulario = f.id\n"
                + "        )\n"
                + "    )\n"
                + "FROM mantenimiento.formularios f where id = ?;";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setInt(1, id_formulario);
                ps.setInt(2, id_formulario);
                rs = ps.executeQuery();

                if (rs.next()) {
                    json = rs.getString("json_build_object");
                }
                respObject = Util.getObjectJson(json);
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
                //respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                //response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            respObject = Util.writeResponseError("Cargar detalle ingreso local", ex.toString(), "/get-detalle-ingreso-local");
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
        query = "SELECT id ,nombre_categoria \n"
                + "FROM mantenimiento.categorias c  \n"
                + "ORDER BY nombre_categoria";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                rs = ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar Combo", ex.toString(), "/get-combo-estados");
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
    public Response getDocumentosAsignados(int id_tipo_equipo) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "select d.id, d.nombre_documento, d.codigo \n"
                + "from mantenimiento.rel_tipo_equipo_documentos rted \n"
                + "inner join mantenimiento.documentos d on d.id = rted.id_documento \n"
                + "where id_subcategoria = ?;";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setInt(1, id_tipo_equipo);
                ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            respObject = Util.writeResponseError("Cargar componentes no asignados a una clase", ex.toString(), "/get-componentes-asignados");
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
    public Response getDocumentos() {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        query = "SELECT id ,nombre_documento \n"
                + "FROM mantenimiento.documentos  \n"
                + "ORDER BY nombre_documento";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                rs = ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar Combo", ex.toString(), "/get-combo-estados");
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
    public Response crearTipoEquipo(String json, Usuario usuario) {
        Response response = null;
        JsonObject respObject = null;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "SELECT mantenimiento.crear_tipo_equipo(?::json, ?::varchar) as resp";
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
            respObject = Util.writeResponseError("Crear tipo equipos", ex.toString(), "/crear-tipo-equipo");
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
    public Response crearSubcategorias(String json, Usuario usuario) {
        Response response = null;
        JsonObject respObject = null;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "SELECT mantenimiento.crear_subcategorias(?::json, ?::varchar) as resp";
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
            respObject = Util.writeResponseError("Crear subcategorias", ex.toString(), "/crear-subcategorias");
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
    public Response getSubcategorias() {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        query = "SELECT * FROM mantenimiento.subcategorias ORDER BY nombre_subcategoria asc";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar subcategorias", ex.toString(), "/get-subcategorias");
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
    public Response getverificacion(int subcategoriaId) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        String json = "{}";

        query = "SELECT json_agg(resultado)as json\n"
                + "FROM (\n"
                + "    SELECT json_build_object(\n"
                + "        'titulo', v.descripcion,\n"
                + "        'id_verificacion', v.id,\n"
                + "        'orden',v.orden, \n"
                + "        'checks', json_agg(json_build_object(\n"
                + "            'id_check', cv.id,\n"
                + "            'check', cv.descripcion\n"
                + "        ))\n"
                + "    ) AS resultado\n"
                + "    FROM mantenimiento.rel_check_verificaciones rel\n"
                + "    INNER JOIN mantenimiento.verificaciones v ON v.id = rel.verificacion_id \n"
                + "    INNER JOIN mantenimiento.check_verificaciones cv ON cv.id = rel.check_verificacion_id \n"
                + "    WHERE subcategoria_id = ?\n"
                + "    GROUP BY v.descripcion,v.orden,v.id \n"
                + "    order by v.orden \n"
                + ") AS subconsulta;";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setInt(1, subcategoriaId);
                rs = ps.executeQuery();
                if (rs.next()) {
                    json = rs.getString("json");
                }
                respObject = Util.writreResponseArrayObject(Util.getArrayJson(json));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            respObject = Util.writeResponseError("Cargar checks verificaciones", ex.toString(), "/get-verificacion");
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
    public Response guardarInspeccion(String json, Usuario usuario) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "SELECT mantenimiento.guardar_inspeccion_mantenimiento(?::json, ?::varchar) as resp";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, json);
                ps.setString(2, usuario.getId_usuario());
                System.out.println(ps.toString());
                rs = ps.executeQuery();
                if (rs.next()) {
                    respObject = Util.readJsonObjectParameter(rs.getString("resp"));
                    response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
                }
            }
        } catch (SQLException ex) {
            String fullErrorMessage = ex.getLocalizedMessage();
            String[] parts = fullErrorMessage.split("ERROR: ");
            String customErrorMessage = parts.length > 1 ? parts[1].split("Where:")[0].trim() : "Error desconocido";

            respObject = Util.writeResponseError("Guardar inspeccion", customErrorMessage, "/guardar-inspeccion");
            response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
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
    public Response getActividades(int subcategoriaId) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        query = "select a.nombre, a.codigo, a.tipo_id, a.id_subcategoria,tm.nombre as tipo_mantenimiento\n"
                + "from mantenimiento.actividades a \n"
                + "inner join mantenimiento.tipo_mantenimiento tm on tm.id =a.tipo_id \n"
                + "where id_subcategoria =?;";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setInt(1, subcategoriaId);
                ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar subcategorias", ex.toString(), "/get-subcategorias");
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
    public Response getPropietariosMaquinas() {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        query = "SELECT * \n"
                + "FROM mantenimiento.propietario p  \n"
                + "ORDER BY nombre_propietario";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                rs = ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar propietarios maquinas", ex.toString(), "/get-propietarios-maquinas");
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
    public Response getClientesAlquiler() {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        query = "SELECT * \n"
                + "FROM mantenimiento.clientes_alquiler p  \n"
                + "ORDER BY nombre";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                rs = ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar arrendatarios", ex.toString(), "/get-propietarios-maquinas");
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
    public Response getRespuestos() {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        query = "SELECT * \n"
                + "FROM mantenimiento.referencias_repuestos p  \n"
                + "ORDER BY nombre";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                rs = ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar arrendatarios", ex.toString(), "/get-propietarios-maquinas");
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
    public Response getMarcas() {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        query = "SELECT * \n"
                + "FROM mantenimiento.marcas p  \n"
                + "ORDER BY nombre";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                rs = ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar arrendatarios", ex.toString(), "/get-propietarios-maquinas");
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
    public Response insertRepuestosReferencia(String json, Usuario usuario) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        JsonObject parameter = Util.readJsonObjectParameter(json);

        query = "SELECT mantenimiento.crear_referencia(?::json, ?::varchar) as resp;";
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
            respObject = Util.writeResponseError("Crear referencia", ex.toString(), "/insert-repuesto-referencia");
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
    public Response getRepuestosReferencia(String codigoMaquina) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        query = "select rrrm.codigo, m.nombre as nombre_marca, rr.nombre as nombre_repuesto, rrrm.referencia, rrrm.id \n"
                + "from mantenimiento.rel_referencias_repuestos_maquinas rrrm\n"
                + "inner join mantenimiento.marcas m on m.id = rrrm.id_marca\n"
                + "inner join mantenimiento.referencias_repuestos rr on rr.id = rrrm.id_referencias_repuestos\n"
                + "where codigo = ?;";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, codigoMaquina);
                ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar subcategorias", ex.toString(), "/get-subcategorias");
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
    public Response getEquipoMaquina(String codigoMaquina) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        query = "select * from mantenimiento.maquinas \n"
                + "where codigo_qr = ?;";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, codigoMaquina);
                ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar maquina", ex.toString(), "/get-equipo-maquina");
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
    public Response getMantenimientoAsignado(String json, Usuario usuario) {
        String query;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Response response = null;
        JsonObject respObject;

        JsonObject parameter = Util.readJsonObjectParameter(json);

        query = "select *\n"
                + "from mantenimiento.mantenimientos m\n"
                + "inner join mantenimiento.maquinas m2 on m2.id = m.maquina_id and m2.codigo_qr = ?\n"
                + "where m.tipo_id = ?::numeric and realizado = 'N';";
        try {
            con = Util.conectarBD();
            if (con != null) {
                // query = query.replaceAll("#ACTIVIDAD", filtroActividad).replaceAll("#FRECUENCIA", filtroFrecuencia).replaceAll("#TIPOMANTENIMIENTO", filtroTipoMantenimiento).replaceAll("#NOVEDAD", filtroNovedad).replaceAll("#MAQUINA", filtroMaquina).replaceAll("#FECHAS", filtroFechas);
                ps = con.prepareStatement(query);
                ps.setString(1, parameter.get("codigoQr").getAsString());
                ps.setString(2, parameter.get("tipoId").getAsString());
                ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }

        } catch (SQLException ex) {
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            respObject = Util.writeResponseError("Cargar Mantenimientos asignados", ex.toString(), "/get-manteniminto-asignado");
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
    public Response getActividadesMantenimientoAsignado(String codigoMantenimiento) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        query = "select a.id, a.nombre as nombre_actividad\n"
                + "from mantenimiento.mantenimiento_actividades ma\n"
                + "inner join mantenimiento.actividades a on a.id = ma.id_actividad \n"
                + "where codigo_mantenimiento = ?;";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, codigoMantenimiento);
                ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar subcategorias", ex.toString(), "/get-subcategorias");
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
    public Response getImspeccionesRealizadas(String json, Usuario usuario) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        String resp = "[]";
        JsonObject parameter = Util.readJsonObjectParameter(json);

        query = "SELECT coalesce (json_agg(\n"
                + "                    json_build_object(        \n"
                + "                        'id_verificacion', ip.id,\n"
                + "                        'identificacion_operador', ip.identificacion_operador,\n"
                + "                        'fecha', ip.fecha,\n"
                + "                        'kilometraje', ip.kilometraje,\n"
                + "                        'paso_actual_detalle', ip.paso_actual_detalle,\n"
                + "                        'usuario_creador', ip.usuario_creador,\n"
                + "                        'fecha_creacion', ip.fecha_creacion,\n"
                + "                            'id_maquina', m.id,\n"
                + "                            'nombre_tipo', te.nombre_tipo,\n"
                + "                        	'nombre_subcategoria', s.nombre_subcategoria,\n"
                + "                        	'nombre_operador', ip.nombre_operador,\n"
                + "                            'codigo', m.codigo,\n"
                + "                            'modelo', m.modelo,\n"
                + "                            'serie', m.serie,\n"
                + "                            'estado', m.estado,\n"
                + "                            'fecha_ingreso', m.fecha_ingreso,\n"
                + "                            'fecha_garantia', m.fecha_garantia,\n"
                + "                            'marca', m.marca,\n"
                + "                            'id_tipo_equipo', m.id_tipo_equipo,\n"
                + "                            'id_patio', m.id_patio,\n"
                + "                            'kilometraje_inicial', m.kilometraje_inicial,\n"
                + "                            'kilometraje_actual', m.kilometraje_actual,\n"
                + "                            'peso', m.peso,\n"
                + "                            'cilindraje', m.cilindraje,\n"
                + "                            'chasis', m.chasis,\n"
                + "                            'servicio', m.servicio,\n"
                + "                            'linea', m.linea,\n"
                + "                            'tipo_aceite', m.tipo_aceite,\n"
                + "                            'filtro_aceite', m.filtro_aceite,\n"
                + "                            'filtro_combustible', m.filtro_combustible,\n"
                + "                            'filtro_aire', m.filtro_aire,\n"
                + "                            'llantas_delanteras', m.llantas_delanteras,\n"
                + "                            'llantas_traseras', m.llantas_traseras,\n"
                + "                            'correas', m.correas,\n"
                + "                            'fecha_ultima_modificacion', m.fecha_ultima_modificacion,\n"
                + "                            'usuario_ultima_modificacion', m.usuario_ultima_modificacion,\n"
                + "                            'id_subcategoria', m.id_subcategoria,\n"
                + "                            'carroceria', m.carroceria,\n"
                + "                            'capacidad_carga', m.capacidad_carga,\n"
                + "                            'combustible', m.combustible,\n"
                + "                            'cilindrada', m.cilindrada,\n"
                + "                            'ubicacion_volante', m.ubicacion_volante,\n"
                + "                            'numero_motor', m.numero_motor,\n"
                + "                            'caracteristicas', m.caracteristicas,\n"
                + "                            'color', m.color,\n"
                + "                            'clase_vehiculo', m.clase_vehiculo,\n"
                + "                            'vim', m.vim,\n"
                + "                            'identificacion_propietario', m.identificacion_propietario,\n"
                + "                            'placa', m.placa,\n"
                + "                        'detalle', (\n"
                + "                            SELECT json_agg(\n"
                + "         json_build_object(\n"
                + "             'formulario', verificacion,\n"
                + "             'formulario_id', formulario_id,\n"
                + "             'respuestas', respuestas\n"
                + "         )\n"
                + "       ) AS resultado\n"
                + "FROM (\n"
                + "  SELECT v.descripcion AS verificacion,\n"
                + "         v.id AS formulario_id,\n"
                + "         json_agg(\n"
                + "           json_build_object(\n"
                + "             'inspeccion_id', ipd.inspeccion_id,\n"
                + "             'nombre_check',cv.descripcion,\n"
                + "             'check_verificacion_id', ipd.check_verificacion_id,\n"
                + "             'calificacion', ipd.calificacion\n"
                + "           )\n"
                + "         ) AS respuestas\n"
                + "  FROM mantenimiento.inspecciones_preoperacionales_detalle ipd\n"
                + "  inner join mantenimiento.check_verificaciones cv on cv.id =ipd.check_verificacion_id\n"
                + "  INNER JOIN mantenimiento.rel_check_verificaciones rcv ON rcv.check_verificacion_id = ipd.check_verificacion_id \n"
                + "  INNER JOIN mantenimiento.verificaciones v ON v.id = rcv.verificacion_id  \n"
                + "  WHERE ipd.inspeccion_id = ip.id\n"
                + "  GROUP BY v.descripcion, v.id\n"
                + ") AS subquery\n"
                + "                        )\n"
                + "                    )\n"
                + "                ),'[]') AS json\n"
                + "                FROM mantenimiento.inspecciones_preoperacionales ip \n"
                + "                INNER JOIN mantenimiento.maquinas m ON m.id = ip.maquina_id \n"
                + "                INNER JOIN mantenimiento.tipo_equipo te ON te.id = m.id_tipo_equipo \n"
                + "                INNER JOIN mantenimiento.subcategorias s ON s.id = m.id_subcategoria \n"
                + "                WHERE ip.paso_actual_detalle != 16\n"
                + "                AND case when ? !='' then ip.maquina_id::varchar = ? else 1=1 end and ip.fecha_creacion::date between ?::date and ?::date\n"
                + "                ";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, parameter.get("maquina_id").getAsString());
                ps.setString(2, parameter.get("maquina_id").getAsString());
                ps.setString(3, parameter.get("fecha_ini").getAsString());
                ps.setString(4, parameter.get("fecha_fin").getAsString());
                System.out.println(ps.toString());
                rs = ps.executeQuery();
                if (rs.next()) {
                    resp = rs.getString("json");
                }
                respObject = Util.writreResponseArrayObject(Util.getArrayJson(resp));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            respObject = Util.writeResponseError("Cargar inspecciones", ex.toString(), "/get-inspecciones-realizadas");
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
    public Response insertHorometro(String json, Usuario usuario) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        System.out.println(json.toString());
        query = "SELECT mantenimiento.guardar_horometro(?::json, ?::varchar) as resp;";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, json);
                ps.setString(2, usuario.getId_usuario());
                System.out.println(ps.toString());
                rs = ps.executeQuery();
                if (rs.next()) {
                    respObject = Util.readJsonObjectParameter(rs.getString("resp"));
                    response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
                }
            }
        } catch (SQLException ex) {
            String fullErrorMessage = ex.getLocalizedMessage();
            String[] parts = fullErrorMessage.split("ERROR: ");
            String customErrorMessage = parts.length > 1 ? parts[1].split("Where:")[0].trim() : "Error desconocido";

            respObject = Util.writeResponseError("Guardar horometro", customErrorMessage, "/insert-horometro");
            response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
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
    public Response getRevisionPendiente(String codigoQr) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        query = "select rm.* \n"
                + "from mantenimiento.revision_mantenimiento rm \n"
                + "inner join mantenimiento.maquinas m on m.id = rm.maquina_id and m.codigo_qr = ?\n"
                + "where rm.estado = 'PM';";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, codigoQr);
                ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar subcategorias", ex.toString(), "/get-subcategorias");
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
    public Response getMantenimientosMaquina(String codigoMaquina) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        query = "select m.*, tm.nombre as tipo_mantenimiento, r.nombre as responsable, n.nombre as novedad, p.nombre_patio \n"
                + "from mantenimiento.mantenimientos m \n"
                + "inner join mantenimiento.tipo_mantenimiento tm on tm.id = m.tipo_id \n"
                + "inner join mantenimiento.responsables r on r.id = m.responsable_id \n"
                + "inner join mantenimiento.novedades n on n.id = m.novedad_id \n"
                + "inner join patios p on p.id = patio_id \n"
                + "where maquina_id = ?::int order by fecha_mantenimiento desc;";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, codigoMaquina);
                ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar subcategorias", ex.toString(), "/get-subcategorias");
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
