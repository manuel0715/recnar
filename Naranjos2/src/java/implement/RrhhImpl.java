/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package implement;

import com.google.gson.JsonObject;
import interfaz.RrhhInterfaz;
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
public class RrhhImpl implements RrhhInterfaz {

    public static final String SECRET_KEY = "oeRaYY7Wo24sDqKSX3IM9ASGmdGPmkTd9jo1QTy4b7P9Ze5_9hKolVX8xNrQDcNRfVEdTZNOMAuOyqEGhXEbdJI-ZQ19k_o9MI0y3eZN2lp9YELjoNUEw55FfXMiINEdt1XR85ViBORIpRLSOkT6kSpzs2x-jbLDiz9iFVzkESd8ES1YKxMgPCAGAA7VfZeQUm4n-mOmnWMaVX30zMARGFU4L3oPBctYKkl4dYfqYWqRNfrgPCAJVi5DGFjywgxx0ASEiJHtV72paI3fDR2XwlSkyhhmY-ICjCRmsJN4fX1pdoL8a18-aQrvyu4j0Os6dVPYIoPvvY0SAZtWYKHfM15g7A3HD4cVREf9cUsprCRK93w";
    public static final String URL_SERVER = "3.132.57.72:8085";

    @Override
    public Response cargarEmpleados() {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        query = "select e.*, a.descripcion as nombre_arl, e2.descripcion as nombre_eps,\n"
                + "b.descripcion as nombre_banco, cc.descripcion as nombre_caja_compensacion,\n"
                + "c.descripcion as nombre_cargo, ec.descripcion as nombre_estado_civil,\n"
                + "fc.descripcion as nombre_fondo_cesantia, fp.descripcion as nombre_fondo_pension,\n"
                + "g.descripcion as nombre_genero, ne.descripcion as nombre_nivel_estudio,\n"
                + "p.descripcion as nombre_profesion, rl.descripcion as nombre_riesgo_laboral,\n"
                + "tc.descripcion as nombre_tipo_contrato, tc2.descripcion as nombre_tipo_cuenta,\n"
                + "td.descripcion as nombre_tipo_documento, tv.descripcion as nombre_tipo_vivienda\n"
                + "from rrhh.empleados e\n"
                + "inner join configuracion.arls a on a.codigo = e.arl\n"
                + "inner join configuracion.eps e2 on e2.codigo = e.eps \n"
                + "inner join configuracion.bancos b on b.codigo = e.banco \n"
                + "inner join configuracion.cajas_compensacion cc on cc.codigo = e.caja_compensacion\n"
                + "inner join configuracion.cargos c on c.codigo = e.cargo\n"
                + "inner join configuracion.estados_civiles ec on ec.codigo = e.estado_civil \n"
                + "inner join configuracion.fondos_cesantias fc on fc.codigo = e.fondo_cesantia \n"
                + "inner join configuracion.fondos_pensiones fp on fp.codigo = e.fondo_pension\n"
                + "inner join configuracion.generos g on g.codigo = e.genero\n"
                + "inner join configuracion.niveles_estudio ne on ne.codigo = e.nivel_estudio \n"
                + "inner join configuracion.profesiones p on p.codigo = e.profesion\n"
                + "inner join configuracion.riesgos_laborales rl on rl.codigo = e.riesgo\n"
                + "inner join configuracion.tipos_contratos tc on tc.codigo = e.tipo_contrato \n"
                + "inner join configuracion.tipos_cuentas tc2 on tc2.codigo = e.tipo_cuenta \n"
                + "inner join configuracion.tipos_documentos td on td.codigo = e.tipo_documento \n"
                + "inner join configuracion.tipos_vivienda tv on tv.codigo = e.tipo_vivienda;";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                rs = ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar Empleados", ex.toString(), "/empleados");
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
    public Response registrarFacial(String json) {
        JsonObject parameter = Util.readJsonObjectParameter(json);
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "insert into rrhh.empleados (nombre, facial) values (?, ?)";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, parameter.get("nombre").getAsString());
                ps.setString(2, parameter.get("landmarks").getAsString());
                if (ps.executeUpdate() > 0) {
                    respObject = Util.writreResponseString("OK");
                    response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            respObject = Util.writeResponseError("Insertar documentos db", ex.toString(), "/save-bitacora");
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
    public Response crearEmpleado(String json, Usuario usuario) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "SELECT rrhh.insertar_empleado(?::json, ?::varchar) as resp";
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
            respObject = Util.writeResponseError("Guardar empleado", ex.toString(), "/crear-empleado");
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
    public Response cargarCombos(String json) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "";
        JsonObject parameter = Util.readJsonObjectParameter(json);

        query = "select * from configuracion." + parameter.get("table").getAsString() + " order by descripcion asc;";

        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                //ps.setString(1, parameter.get("table").getAsString());
                rs = ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar combos", ex.toString(), "/get-combos");
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
    public Response getCiudades(String idDepartamento) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        query = "select * from configuracion.ciudades c where departamento = ? order by descripcion asc";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, idDepartamento);
                ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar ciudades", ex.toString(), "/get-ciudades");
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
    public Response crearNovedad(String json, Usuario usuario) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "SELECT rrhh.sp_rrhh_insertar_novedad(?::json, ?::varchar) as resp";
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
            respObject = Util.writeResponseError("Guardar novedad", ex.toString(), "/crear-novedad");
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
    public Response getNovedadesEmpleado(String numeroDocumento) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "    SELECT n.id_novedad, \n"
                + "          CASE WHEN n.estado = '' THEN 'Activo' ELSE 'Anulado' END estado,\n"
                + "	       n.codigo_novedad,\n"
                + "	       tn.descripcion AS descripcion_novedad,\n"
                + "	       n.codigo_enfermedad,\n"
                + "	       ce.descripcion AS descripcion_enfermedad,\n"
                + "	       n.codigo_eps,\n"
                + "	       e2.descripcion AS descripcion_eps,\n"
                + "	       n.codigo_arl,\n"
                + "	       a.descripcion AS descripcion_arl,\n"
                + "	       n.codigo_clase_licencia,\n"
                + "	       cl.descripcion AS descripcion_clase_licencia,\n"
                + "	       n.codigo_clase_permiso,\n"
                + "	       cp.descripcion AS descripcion_clase_permiso,\n"
                + "	       n.numero_documento,\n"
                + "	       n.nombre_completo,\n"
                + "	       n.fecha_inicial::Date,\n"
                + "	       n.fecha_final::date,\n"
                + "	       n.total_dias,\n"
                + "	       n.hora_inicial,\n"
                + "	       n.hora_final,\n"
                + "	       n.total_horas,\n"
                + "	       n.descripcion_novedad AS descripcion,\n"
                + "            n.remunerado, \n"
                + "	       CASE WHEN n.aprobado = 1 THEN 'Pendiente' \n"
                + "                 WHEN n.aprobado IN (2,3) THEN 'En proceso' \n"
                + "                 WHEN n.aprobado = 4 THEN 'Aprobado' \n"
                + "                 WHEN n.aprobado = 5 THEN 'Rechazada' \n"
                + "            ELSE 'Anulado' END AS aprobado, n.aprobado as aprobado_id, \n"
                + "	       n.fecha_creacion AS fecha_solicitud, n.jefe_aprobacion as jefe_directo\n"
                + "      FROM rrhh.novedades n \n"
                + "INNER JOIN rrhh.empleados e ON e.numero_documento = n.numero_documento    \n"
                + "INNER JOIN configuracion.tipos_novedades tn ON tn.codigo = n.codigo_novedad \n"
                + " LEFT JOIN configuracion.clases_enfermedades ce ON ce.codigo = n.codigo_enfermedad\n"
                + " LEFT JOIN configuracion.eps e2 ON e2.codigo = n.codigo_eps \n"
                + " LEFT JOIN configuracion.arls a ON a.codigo = n.codigo_arl\n"
                + " LEFT JOIN configuracion.clases_licencias cl ON cl.codigo = n.codigo_clase_licencia\n"
                + " LEFT JOIN configuracion.clases_permisos cp ON cp.codigo = n.codigo_clase_permiso\n"
                + "     WHERE n.numero_documento = ?::varchar \n"
                + "  ORDER BY id_novedad DESC;";

        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, numeroDocumento);
                ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar novedades empleados", ex.toString(), "/get-novedad-empleado");
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

        query = "select * from rrhh.documentos";

        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar documentos", ex.toString(), "/get-documentos");
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
    public Response gestionarNovedad(String json, Usuario usuario) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "SELECT rrhh.sp_rrhh_gestionar_novedad(?::json, ?::varchar) as resp";
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
            respObject = Util.writeResponseError("Gestionar novedad", ex.toString(), "/gestionar-novedad");
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
   public Response getFestivos(String ano) {
       Response response = null;
       JsonObject respObject;
       Connection con = null;
       PreparedStatement ps = null;
       ResultSet rs = null;
       String query;
       
       query = "SELECT rrhh.fn_festivos(?::int) as resp";
       
       try {
           con = Util.conectarBD();
           if (con != null) {
               ps = con.prepareStatement(query);
               ps.setString(1, ano);
               rs = ps.executeQuery();
               if (rs.next()) {
                   respObject = Util.writreResponseString(rs.getString("resp"));
                   response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
               }
           }
       } catch (SQLException ex) {
           respObject = Util.writeResponseError("Festivos", ex.toString(), "/get-festivos");
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
    public Response getJefeDirecto() {
       Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "select e.primer_nombre||' '||e.primer_apellido as nombre, c.descripcion, e.numero_documento as codigo \n" +
                "from rrhh.empleados e\n" +
                "inner join configuracion.cargos c on c.codigo = e.cargo \n" +
                "where c.jefe_directo = true;";

        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar jefes directos", ex.toString(), "/get-jefe-directo");
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
    public Response getNovedadAprobacionJefe(String numeroDocumento, String fechaInit, String fechaFin) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "    SELECT n.id_novedad, \n"
                + "          CASE WHEN n.estado = '' THEN 'Activo' ELSE 'Anulado' END estado,\n"
                + "	       n.codigo_novedad,\n"
                + "	       tn.descripcion AS descripcion_novedad,\n"
                + "	       n.codigo_enfermedad,\n"
                + "	       ce.descripcion AS descripcion_enfermedad,\n"
                + "	       n.codigo_eps,\n"
                + "	       e2.descripcion AS descripcion_eps,\n"
                + "	       n.codigo_arl,\n"
                + "	       a.descripcion AS descripcion_arl,\n"
                + "	       n.codigo_clase_licencia,\n"
                + "	       cl.descripcion AS descripcion_clase_licencia,\n"
                + "	       n.codigo_clase_permiso,\n"
                + "	       cp.descripcion AS descripcion_clase_permiso,\n"
                + "	       n.numero_documento,\n"
                + "	       n.nombre_completo,\n"
                + "	       n.fecha_inicial::Date,\n"
                + "	       n.fecha_final::date,\n"
                + "	       n.total_dias,\n"
                + "	       n.hora_inicial,\n"
                + "	       n.hora_final,\n"
                + "	       n.total_horas,\n"
                + "	       n.descripcion_novedad AS descripcion,\n"
                + "            n.remunerado, \n"
                + "	       CASE WHEN n.aprobado = 1 THEN 'Pendiente' \n"
                + "                 WHEN n.aprobado IN (2,3) THEN 'En proceso' \n"
                + "                 WHEN n.aprobado = 4 THEN 'Aprobado' \n"
                + "                 WHEN n.aprobado = 5 THEN 'Rechazada' \n"
                + "                 ELSE 'Anulado' END AS aprobado, n.aprobado as aprobado_id, \n"
                + "	       n.fecha_creacion AS fecha_solicitud, n.jefe_aprobacion as jefe_directo\n"
                + "      FROM rrhh.novedades n \n"
                + "INNER JOIN rrhh.empleados e ON e.numero_documento = n.numero_documento    \n"
                + "INNER JOIN configuracion.tipos_novedades tn ON tn.codigo = n.codigo_novedad \n"
                + " LEFT JOIN configuracion.clases_enfermedades ce ON ce.codigo = n.codigo_enfermedad\n"
                + " LEFT JOIN configuracion.eps e2 ON e2.codigo = n.codigo_eps \n"
                + " LEFT JOIN configuracion.arls a ON a.codigo = n.codigo_arl\n"
                + " LEFT JOIN configuracion.clases_licencias cl ON cl.codigo = n.codigo_clase_licencia\n"
                + " LEFT JOIN configuracion.clases_permisos cp ON cp.codigo = n.codigo_clase_permiso\n"
                + "     WHERE n.jefe_aprobacion = ?::varchar \n"
                + "           and n.fecha_creacion BETWEEN ?::date AND ?::date \n"
                + "  ORDER BY id_novedad DESC;";

        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, numeroDocumento);
                ps.setString(2, fechaInit);
                ps.setString(3, fechaFin);
                ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar novedades para aprobar", ex.toString(), "/get-novedad-aprobacion-jefe");
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
    public Response getNovedadAprobacion(String numeroDocumento, String fechaInit, String fechaFin) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "    SELECT n.id_novedad, \n"
                + "          CASE WHEN n.estado = '' THEN 'Activo' ELSE 'Anulado' END estado,\n"
                + "	       n.codigo_novedad,\n"
                + "	       tn.descripcion AS descripcion_novedad,\n"
                + "	       n.codigo_enfermedad,\n"
                + "	       ce.descripcion AS descripcion_enfermedad,\n"
                + "	       n.codigo_eps,\n"
                + "	       e2.descripcion AS descripcion_eps,\n"
                + "	       n.codigo_arl,\n"
                + "	       a.descripcion AS descripcion_arl,\n"
                + "	       n.codigo_clase_licencia,\n"
                + "	       cl.descripcion AS descripcion_clase_licencia,\n"
                + "	       n.codigo_clase_permiso,\n"
                + "	       cp.descripcion AS descripcion_clase_permiso,\n"
                + "	       n.numero_documento,\n"
                + "	       n.nombre_completo,\n"
                + "	       n.fecha_inicial::Date,\n"
                + "	       n.fecha_final::date,\n"
                + "	       n.total_dias,\n"
                + "	       n.hora_inicial,\n"
                + "	       n.hora_final,\n"
                + "	       n.total_horas,\n"
                + "	       n.descripcion_novedad AS descripcion,\n"
                + "            n.remunerado, \n"
                + "	       CASE WHEN n.aprobado = 1 THEN 'Pendiente' \n"
                + "                 WHEN n.aprobado IN (2,3) THEN 'En proceso' \n"
                + "                 WHEN n.aprobado = 4 THEN 'Aprobado' \n"
                + "                 WHEN n.aprobado = 5 THEN 'Rechazada' \n"
                + "                 ELSE 'Anulado' END AS aprobado, n.aprobado as aprobado_id, \n"
                + "	       n.fecha_creacion AS fecha_solicitud, n.jefe_aprobacion as jefe_directo\n"
                + "      FROM rrhh.novedades n \n"
                + "INNER JOIN rrhh.empleados e ON e.numero_documento = n.numero_documento    \n"
                + "INNER JOIN configuracion.tipos_novedades tn ON tn.codigo = n.codigo_novedad \n"
                + " LEFT JOIN configuracion.clases_enfermedades ce ON ce.codigo = n.codigo_enfermedad\n"
                + " LEFT JOIN configuracion.eps e2 ON e2.codigo = n.codigo_eps \n"
                + " LEFT JOIN configuracion.arls a ON a.codigo = n.codigo_arl\n"
                + " LEFT JOIN configuracion.clases_licencias cl ON cl.codigo = n.codigo_clase_licencia\n"
                + " LEFT JOIN configuracion.clases_permisos cp ON cp.codigo = n.codigo_clase_permiso\n"
                + " WHERE (EXISTS(select 1 from rrhh.empleados e3 \n" 
                +"                inner join configuracion.cargos c on c.codigo = e3.cargo \n" 
                +"                where e3.numero_documento = ?::varchar  and c.codigo = '01' \n" 
                +"                      and n.aprobado not in (1,2) \n" 
                +"        )OR EXISTS(select 1 from rrhh.empleados e3 \n" 
                +"                   inner join configuracion.cargos c on c.codigo = e3.cargo \n" 
                +"                   where e3.numero_documento = ?::varchar  and c.codigo = '06' \n" 
                +"                   and n.aprobado != 1 \n" 
                +"                      )) \n"
                +"       AND n.fecha_creacion BETWEEN ?::date AND ?::date \n"
                +"  ORDER BY id_novedad DESC;";

        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, numeroDocumento);
                ps.setString(2, numeroDocumento);
                ps.setString(3, fechaInit);
                ps.setString(4, fechaFin);
                ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar novedades para aprobar", ex.toString(), "/get-novedad-aprobacion");
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
