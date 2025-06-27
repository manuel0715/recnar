/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package implement;

import com.aspose.cells.Cell;
import com.aspose.cells.Cells;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.aspose.cells.Worksheets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import javax.ws.rs.core.Response;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.crypto.spec.SecretKeySpec;
import util.Usuario;
import util.Util;
import interfaz.ApiInterfaz;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import util.ConnectionDataBase;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.tomcat.util.codec.binary.Base64;

/**
 *
 * @author usuario
 */
public class ApiImpl extends ConnectionDataBase implements ApiInterfaz {

    public static final String SECRET_KEY = "oeRaYY7Wo24sDqKSX3IM9ASGmdGPmkTd9jo1QTy4b7P9Ze5_9hKolVX8xNrQDcNRfVEdTZNOMAuOyqEGhXEbdJI-ZQ19k_o9MI0y3eZN2lp9YELjoNUEw55FfXMiINEdt1XR85ViBORIpRLSOkT6kSpzs2x-jbLDiz9iFVzkESd8ES1YKxMgPCAGAA7VfZeQUm4n-mOmnWMaVX30zMARGFU4L3oPBctYKkl4dYfqYWqRNfrgPCAJVi5DGFjywgxx0ASEiJHtV72paI3fDR2XwlSkyhhmY-ICjCRmsJN4fX1pdoL8a18-aQrvyu4j0Os6dVPYIoPvvY0SAZtWYKHfM15g7A3HD4cVREf9cUsprCRK93w";
    public static final String URL_SERVER = "3.132.57.72:8085";
    public static final String UPLOAD_FILE_SERVER = "D:\\Naranjos 2.0\\apache-tomcat-8.5.69\\webapps\\ROOT\\Documentos_Naranjos\\";
    public static final String UPLOAD_PHOTO_MANTENIMIENTO = "D:\\Naranjos 2.0\\apache-tomcat-8.5.69\\webapps\\ROOT\\mantenimiento\\";

    @Override
    public Response loginApi(String json) {
        Response response = null;
        JsonObject respObject = null;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "";

        query = "select u.estado, usuario,nombres||' '||apellidos as nombre, u.identificacion, coalesce(eps.descripcion, 'N/A') as eps, \n"
                + "coalesce(a.descripcion, 'N/A') as arl, coalesce(eps.codigo, 'N/A') as code_eps, coalesce(a.codigo, 'N/A') as code_arl, cg.jefe_directo, \n"
                + "(select e2.primer_nombre || ' ' || e2.primer_apellido as nombre \n"
                + "from rrhh.empleados e2 \n"
                + "where e2.numero_documento  = e.jefe_directo \n"
                + "limit 1) as jefe_nombre, \n"
                + "(select e3.numero_documento as documente_jefe_directo \n"
                + "from rrhh.empleados e3 \n"
                + "where e3.numero_documento = e.jefe_directo \n"
                + "limit 1) as jefe_identificacion \n"
                + "from usuarios u \n"
                + "left join rrhh.empleados e on e.numero_documento::int  = u.identificacion \n" 
                + "left join configuracion.cargos  cg on cg.codigo = e.cargo \n" 
                + "left join configuracion.eps eps on eps.codigo = e.eps \n"
                + "left join configuracion.arls a on a.codigo = e.arl \n"
                + "where u.usuario =? and u.contrasena =md5(?)";
                

        JsonObject parameter = Util.readJsonObjectParameter(json);
        if (parameter != null) {
            try {
                con = Util.conectarBD();
                if (con != null) {
                    ps = con.prepareStatement(query);
                    ps.setString(1, parameter.get("usuario").getAsString());
                    ps.setString(2, parameter.get("contrasena").getAsString());
                    rs = ps.executeQuery();
                    if (rs.next()) {
                        if (rs.getString("estado").equals("I")) {
                            respObject = Util.writeResponseError("Iniciar sesión", "Usuario se encuentra inactivo, por favor comuniquese con el administrador del sistema", "/login");
                            response = Util.responseApi(Util.getJsonString(respObject), Response.Status.UNAUTHORIZED);
                            return response;
                        }
                        String token = getTokenJWT(rs.getString("usuario"), rs.getString("usuario"), rs.getString("nombre"), Long.parseLong("2592000000"));

                        respObject = Util.jsonResponseObject("token", token);
                        respObject.addProperty("nombre", rs.getString("nombre"));
                        respObject.addProperty("usuario", rs.getString("usuario"));
                        respObject.addProperty("identificacion", rs.getString("identificacion"));
                        respObject.addProperty("eps", rs.getString("eps"));
                        respObject.addProperty("arl", rs.getString("arl"));
                        respObject.addProperty("code_eps", rs.getString("code_eps"));
                        respObject.addProperty("code_arl", rs.getString("code_arl"));
                        respObject.addProperty("jefe_nombre", rs.getString("jefe_nombre"));
                        respObject.addProperty("jefe_identificacion", rs.getString("jefe_identificacion")); 
                        respObject.addProperty("jefe_directo", rs.getString("jefe_directo")); 
                        respObject.add("menu_opciones", getMenuOpciones(rs.getString("usuario"), parameter.get("plataforma").getAsString()));
                        respObject.add("menu_opciones_nuevo", getMenuOpcionesNuevo(rs.getString("usuario"), parameter.get("plataforma").getAsString()));
                        respObject.add("menu_opciones_v2", getMenuOpcionesV2(rs.getString("usuario"), parameter.get("plataforma").getAsString()));

                        respObject = Util.writreResponseJsonObject(respObject);
                        response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
                    } else {
                        respObject = Util.writeResponseError("Iniciar sesión", "Usuario y/o contraseña incorrectos, por favor verificar", "/login");
                        response = Util.responseApi(Util.getJsonString(respObject), Response.Status.UNAUTHORIZED);
                    }
                }
            } catch (NumberFormatException | SQLException ex) {
                respObject = Util.writeResponseError("Login", ex.toString(), "/login");
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.INTERNAL_SERVER_ERROR);
                Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    Util.desconectarBd(con, rs, ps, null, this.getClass());
                } catch (SQLException ex) {
                    Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }
        return response;

    }

    private JsonElement getMenuOpciones(String id_usuario, String plataforma) {
        Connection con = null;
        PreparedStatement ps = null;
        JsonArray opciones = null;
        String query = "";
        query = "SELECT opu.id,opu.nombre_opcion,opu.opcion,orden,icono\n"
                + "FROM usuarios u \n"
                + "INNER JOIN rel_usuario_perfil up ON up.usuario =u.usuario \n"
                + "INNER JOIN rel_opciones_perfiles op ON op.id_perfil=up.id_perfil \n"
                + "INNER JOIN opciones_perfil_usuario opu ON opu.id =op.id_opcion \n"
                + "WHERE u.usuario =? AND plataforma LIKE ? AND opu.estado=''\n"
                + "GROUP BY  opu.id,opu.nombre_opcion,opu.opcion ORDER BY orden";
//                + "UNION ALL \n"
//                + "SELECT opu.id,opu.nombre_opcion,opu.opcion,opu.id_padre,orden\n"
//                + "FROM opciones_perfil_usuario opu\n"
//                + "WHERE id_padre =0 AND opu.estado='' ORDER BY orden";

        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, id_usuario);
                ps.setString(2, "%" + plataforma + "%");
                opciones = Util.getQueryJsonArray(con, ps);
                return opciones;
            }
        } catch (SQLException ex) {
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            return opciones;
        } finally {
            try {
                Util.desconectarBd(con, null, ps, null, this.getClass());
            } catch (SQLException ex) {
                Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return opciones;
    }

    private JsonObject getMenuOpcionesNuevo(String id_usuario, String plataforma) {
        JsonObject respObject = null;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        String responseJson = "{}";

        query = "SELECT\n"
                + "    jsonb_build_object(\n"
                + "        'menu_opciones',\n"
                + "        jsonb_object_agg(\n"
                + "            nombre_menu,\n"
                + "            opciones_json\n"
                + "        )\n"
                + "    ) AS resultado\n"
                + "FROM (\n"
                + "    SELECT\n"
                + "        mp.nombre_menu,\n"
                + "        jsonb_agg(\n"
                + "            jsonb_build_object(\n"
                + "                'id', op.id,\n"
                + "                'nombre_opcion', op.nombre_opcion,\n"
                + "                'opcion', op.opcion,\n"
                + "                'orden', op.orden,\n"
                + "                'icono', op.icono\n"
                + "            )\n"
                + "        ) AS opciones_json\n"
                + "    FROM\n"
                + "        opciones_perfil_usuario op\n"
                + "    inner JOIN menu_padre mp ON op.id_padre = mp.id\n"
                + "    inner join rel_opciones_perfiles rop on rop.id_opcion =op.id \n"
                + "    inner join rel_usuario_perfil rup on rup.id_perfil =rop.id_perfil\n"
                + "    inner join usuarios u on u.usuario =rup.usuario \n"
                + "    where u.usuario =? and  op.plataforma ilike ?\n"
                + "    GROUP BY\n"
                + "        mp.nombre_menu   \n"
                + ") AS subquery;";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, id_usuario);
                ps.setString(2, "%" + plataforma + "%");
                rs = ps.executeQuery();

                if (rs.next()) {
                    responseJson = rs.getString("resultado");
                }
                respObject = Util.getObjectJson(responseJson);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar tipo equipos", ex.toString(), "/get-tipo-equipos");
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                Util.desconectarBd(con, rs, ps, null, this.getClass());
            } catch (SQLException ex) {
                Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return respObject;
    }

    private JsonObject getMenuOpcionesV2(String id_usuario, String plataforma) {
        JsonObject respObject = null;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        String responseJson = "{}";

        query = "SELECT json_agg(resultado)as resultado \n"
                + "				from (\n"
                + "						select jsonb_build_object(\n"
                + "	                    'menu_padre',mp.nombre_menu,\n"
                + "	                    'id',mp.id,\n"
                + "	                    'orden',mp.orden,\n"
                + "	                    'icono',mp.icono,\n"
                + "	                    'menu_opciones',json_agg(json_build_object(\n"
                + "	                    	'id', op.id,\n"
                + "		                    'nombre_opcion', op.nombre_opcion,\n"
                + "		                    'opcion', op.opcion,\n"
                + "		                    'orden', op.orden,\n"
                + "		                    'icono', op.icono\n"
                + "	                    ))	                    \n"
                + "      		) AS resultado\n"
                + "        FROM\n"
                + "            opciones_perfil_usuario op\n"
                + "        inner JOIN menu_padre mp ON op.id_padre = mp.id\n"
                + "        inner join rel_opciones_perfiles rop on rop.id_opcion =op.id \n"
                + "        inner join rel_usuario_perfil rup on rup.id_perfil =rop.id_perfil\n"
                + "        inner join usuarios u on u.usuario =rup.usuario \n"
                + "        where u.usuario =? and  op.plataforma ilike ? \n"
                + "        GROUP BY mp.nombre_menu,mp.id,mp.orden,mp.icono\n"
                + "\n"
                + ")AS subconsulta;";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, id_usuario);
                ps.setString(2, "%" + plataforma + "%");
                rs = ps.executeQuery();

                if (rs.next()) {
                    responseJson = rs.getString("resultado");
                }
                respObject = Util.writreResponseArrayObject(Util.getArrayJson(responseJson));
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar menu v2", ex.toString(), "/login");
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                Util.desconectarBd(con, rs, ps, null, this.getClass());
            } catch (SQLException ex) {
                Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return respObject;
    }

    private String getTokenJWT(String cod_token, String issuer, String subject, long ttlMillis) {
        String resp = "";

        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(SECRET_KEY);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        JwtBuilder builder = Jwts.builder().setId(cod_token)
                .setIssuedAt(now)
                .setSubject(subject)
                .setIssuer(issuer)
                .signWith(signingKey, signatureAlgorithm);

        //if it has been specified, let's add the expiration
        if (ttlMillis > 0) {
            long expMillis = nowMillis + ttlMillis;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }
        resp = builder.compact();

        return resp;
    }

    public Usuario validarUserTokenJwT(Claims claims) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "";

        query = "SELECT  nombres||' '||apellidos as nombre,identificacion , usuario\n"
                + "FROM public.usuarios\n"
                + "WHERE usuario =?";
        Usuario usuario = null;
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, claims.getIssuer());
                rs = ps.executeQuery();
                if (rs.next()) {
                    usuario = new Usuario();
                    usuario.setNombre(rs.getString("nombre"));
                    usuario.setIdentificacion(rs.getString("identificacion"));
                    usuario.setId_usuario(rs.getString("usuario"));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            usuario = null;
        } finally {
            try {
                Util.desconectarBd(con, rs, ps, null, this.getClass());
            } catch (SQLException ex) {
                Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return usuario;
    }

    public Claims decodeJWT(String jwt) {
        Claims claims = Jwts.parser()
                .setSigningKey(DatatypeConverter.parseBase64Binary(SECRET_KEY))
                .parseClaimsJws(jwt).getBody();
        return claims;
    }

    @Override
    public Response generarOrdenIngreso(String json, InputStream input, Usuario usuario) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps;
        ResultSet rs = null;
        String query;
        String query2;
        JsonObject parameter = Util.readJsonObjectParameter(json);
        if (parameter != null) {

            try {

                query = "INSERT INTO public.orden_ingreso\n"
                        + "(codigo_orden_ingreso, fecha, id_municipio, documento_tercero, fecha_resolucion, lote, \n"
                        + "nit_proveedor, nit_propietario, dependencia, autorizado, autorizacion, motivo, responsable1, responsable2, usuario_creador, fecha_creacion,id_cronograma,responsable_patio,valor_compra)\n"
                        + "VALUES(?::varchar,?::date,?::int,UPPER(?)::varchar,?::date,UPPER(?)::varchar,\n"
                        + "?::NUMERIC,?::NUMERIC,UPPER(?)::varchar,?::NUMERIC,?::int,?::int,?::varchar,?::varchar,?::varchar,now(),?::int,?::varchar,?::numeric)";

                query2 = "INSERT INTO public.orden_ingreso_detalle\n"
                        + "( tipo_codigo, cod_identificacion, item, id_articulo, descripcion_articulo, unidad_medida,\n"
                        + "cantidad, peso_kg, id_clase_articulos, placa, motor, chasis, serie, marca, carroceria, modelo, linea, capacidad,\n"
                        + "tipo_servicio, id_categoria,id_clasificacion,patio_id, fecha_creacion,color ,nit_propietario ,nombre_propietario ,configuracion_ejes ,peso_bruto,usuario_creador,codigo_orden_ingreso)\n"
                        + "VALUES(?::numeric,UPPER(?)::varchar,?::int,?::int,UPPER(?)::text,?::int,?::int,?::numeric,?::int,UPPER(?)::varchar,UPPER(?)::varchar,"
                        + "UPPER(?)::varchar,UPPER(?)::varchar,UPPER(?)::varchar,UPPER(?)::varchar,"
                        + "UPPER(?)::varchar,UPPER(?)::varchar,UPPER(?)::varchar,UPPER(?)::varchar,?::int,?::int,?::int,NOW(),UPPER(?)::varchar,?::varchar,UPPER(?)::varchar,?::varchar,?::varchar,UPPER(?)::varchar,UPPER(?)::varchar);";

                con = Util.conectarBD();
                if (con != null) {
                    String codigo_orden_ingreso = getSeries("ORDEN_INGRESO");
                    con.setAutoCommit(false);
                    ps = con.prepareStatement(query);

                    ps.setString(1, codigo_orden_ingreso);
                    ps.setString(2, parameter.get("fecha").getAsString());
                    ps.setInt(3, parameter.get("id_municipio").getAsInt());
                    ps.setString(4, parameter.get("documento_tercero").getAsString());
                    ps.setString(5, parameter.get("fecha_resolucion").getAsString());
                    ps.setString(6, parameter.get("lote").getAsString());
                    ps.setString(7, parameter.get("nit_proveedor").getAsString());
                    ps.setString(8, parameter.get("nit_propietario").getAsString());
                    ps.setString(9, parameter.get("dependencia").getAsString());
                    ps.setString(10, parameter.get("autorizado").getAsString());
                    ps.setInt(11, parameter.get("autorizacion").getAsInt());
                    ps.setInt(12, parameter.get("motivo").getAsInt());
                    ps.setString(13, parameter.get("responsable1").getAsString());
                    ps.setString(14, parameter.get("responsable2").getAsString());
                    ps.setString(15, usuario.getId_usuario());
                    ps.setInt(16, parameter.get("id_cronograma").getAsInt());
                    ps.setString(17, parameter.get("responsable_patio").getAsString());
                    ps.setString(18, parameter.get("valor_compra").getAsString());
                    ps.addBatch();
                    ps.executeBatch();

                    ps = con.prepareStatement(query2);

                    if (parameter.get("tipo_subida").getAsString().equals("P")) {
                        Workbook workbook = new Workbook();
                        workbook.open(input);
                        Worksheets sheets = workbook.getWorksheets();
                        Worksheet base = sheets.getSheet("Base");
                        Cells celdas = base.getCells();

                        for (int i = 1; i <= celdas.getMaxDataRow(); i++) {
                            for (int j = 0; j <= celdas.getMaxDataColumn(); j++) {
                                Cell celdaActual = celdas.getCell(i, j);
                                ps.setString(j + 1, celdaActual.getStringValue().trim());
                            }
                            ps.setString(28, usuario.getId_usuario());
                            ps.setString(29, codigo_orden_ingreso);
                            ps.addBatch();
                        }
                    } else if (parameter.get("tipo_subida").getAsString().equals("M")) {

                        JsonArray jsonArr = parameter.get("detalle").getAsJsonArray();
                        int i = 1;
                        for (JsonElement jsonElement : jsonArr) {
                            JsonObject detalle = jsonElement.getAsJsonObject();
                            i = 1;
                            ps.setString(i++, detalle.get("tipo_codigo").getAsString());
                            ps.setString(i++, detalle.get("cod_identificacion").getAsString());
                            ps.setString(i++, detalle.get("item").getAsString());
                            ps.setString(i++, detalle.get("id_articulo").getAsString());
                            ps.setString(i++, detalle.get("descripcion_articulo").getAsString());
                            ps.setString(i++, detalle.get("unidad_medida").getAsString());
                            ps.setString(i++, detalle.get("cantidad").getAsString());
                            ps.setString(i++, detalle.get("peso_kg").getAsString());
                            ps.setString(i++, detalle.get("id_clase_articulos").getAsString());
                            ps.setString(i++, detalle.get("placa").getAsString());
                            ps.setString(i++, detalle.get("motor").getAsString());
                            ps.setString(i++, detalle.get("chasis").getAsString());
                            ps.setString(i++, detalle.get("serie").getAsString());
                            ps.setString(i++, detalle.get("marca").getAsString());
                            ps.setString(i++, detalle.get("carroceria").getAsString());
                            ps.setString(i++, detalle.get("modelo").getAsString());
                            ps.setString(i++, detalle.get("linea").getAsString());
                            ps.setString(i++, detalle.get("capacidad").getAsString());
                            ps.setString(i++, detalle.get("tipo_servicio").getAsString());
                            ps.setString(i++, detalle.get("id_categoria").getAsString());
                            ps.setString(i++, detalle.get("id_clasificacion").getAsString());
                            ps.setInt(i++, detalle.get("id_patio").getAsInt());
                            ps.setString(i++, detalle.get("color").getAsString());
                            ps.setString(i++, detalle.get("nit_propietario").getAsString());
                            ps.setString(i++, detalle.get("nombre_propietario").getAsString());
                            ps.setString(i++, detalle.get("configuracion_ejes").getAsString());
                            ps.setString(i++, detalle.get("peso_bruto").getAsString());
                            ps.setString(i++, usuario.getId_usuario());
                            ps.setString(i++, codigo_orden_ingreso);

                            ps.addBatch();
                        }

                    }
                    ps.executeBatch();
                    con.commit();

                    respObject = Util.writreResponseString(codigo_orden_ingreso);
                    response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);

                } else {
                    respObject = Util.writeResponseError("Error de conexion", "conexion null", "/generar-orden-ingreso");
                    response = Util.responseApi(Util.getJsonString(respObject), Response.Status.BAD_REQUEST);
                }

            } catch (IOException ex) {
                try {
                    con.rollback();

                } catch (SQLException ex1) {
                    Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex1);

                }
                respObject = Util.writeResponseError("Error al leer datos", "Error al leer el archivo de excel por favor valide los campos digitados he intente nuevamente", "/generar-orden-ingreso");
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.INTERNAL_SERVER_ERROR);

                Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);

            } catch (SQLException ex) {
                try {
                    con.rollback();
                    respObject = Util.writeResponseError("Error al leer datos", "Error al leer el archivo de excel por favor valide los campos digitados he intente nuevamente", "/generar-orden-ingreso");
                    response = Util.responseApi(Util.getJsonString(respObject), Response.Status.INTERNAL_SERVER_ERROR);
                } catch (SQLException ex1) {
                    Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex1);
                }
                Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex.getNextException());
            }

        } else {
            respObject = Util.writeResponseError("No se encontro informacion", "No se encontraron parametros", "/generar-orden-ingreso");
            response = Util.responseApi(Util.getJsonString(respObject), Response.Status.BAD_REQUEST);
        }
        return response;
    }

    public String getSeries(String id_serie) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        query = "SELECT get_series(?) as resp;";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, id_serie);
                rs = ps.executeQuery();
                if (rs.next()) {
                    return rs.getString("resp");
                }
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

        return null;
    }

    @Override
    public Response getOrdenIngreso(String json, Usuario usuario) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        JsonObject parameter = Util.readJsonObjectParameter(json);

        query = "SELECT codigo_orden_ingreso, \n"
                + "fecha,\n"
                + "m.nombre_municipio,\n"
                + "m.id as id_municipio, \n"
                + "documento_tercero AS resolucion,\n"
                + "fecha_resolucion, \n"
                + "lote,\n"
                + "p.nit::varchar AS nit_proveedor,\n"
                + "p.razon_social AS proveedor,\n"
                + "dependencia, \n"
                + "a.nombre_completo AS autorizado ,\n"
                + "a.nit as nombre_autorizado,\n"
                + "a2.nombre_autorizacion AS autorizacion,\n"
                + "a2.id as id_autorizacion,\n"
                + "m2.nombre_motivo AS motivo,\n"
                + "m2.id as id_motivo,\n"
                + "u.nombres||' '||u.apellidos AS responsable1,\n"
                + "u.usuario as id_responsable1,\n"
                + "u2.nombres||' '||u2.apellidos AS responsable2,\n"
                + "u2.usuario  as id_responsable2,\n"
                + "oi.usuario_creador,\n"
                + "oi.fecha_creacion::timestamp(0),\n"
                + "oi.usuario_ultima_modificacion, \n"
                + "oi.fecha_ultima_modificacion::timestamp(0),\n"
                + "oi.id_cronograma,\n"
                + "coalesce(oi.responsable_patio,'') as responsable_documental,\n"
                + "valor_compra\n"
                + "FROM orden_ingreso oi \n"
                + "INNER JOIN municipio m ON m.id =oi.id_municipio \n"
                + "INNER JOIN proveedor p ON p.nit =oi.nit_proveedor \n"
                + "INNER JOIN autorizado a ON a.nit =oi.autorizado \n"
                + "INNER JOIN autorizacion a2 ON a2.id =oi.autorizacion\n"
                + "INNER JOIN motivo m2 ON m2.id =oi.motivo \n"
                + "INNER JOIN usuarios u ON u.usuario =oi.responsable1 \n"
                + "INNER JOIN usuarios u2 ON u2.usuario =oi.responsable2\n"
                + "WHERE oi.codigo_orden_ingreso=?  --AND (u2.usuario= ? OR u.usuario =?)\n"
                + "ORDER BY oi.fecha_creacion ASC";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, parameter.get("codigo_orden_ingreso").getAsString());
                ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            respObject = Util.writeResponseError("Cargar Ordenes de Ingreso", ex.toString(), "/get-orden-ingreso");
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
    public Response getDetalleOrdenIngreso(String json) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        JsonObject parameter = Util.readJsonObjectParameter(json);

        query = "SELECT  \n"
                + "		oi.nit_proveedor,\n"
                + "		oi.codigo_orden_ingreso,\n"
                + "		p.razon_social AS proveedor,\n"
                + "		oi.documento_tercero AS resolucion,	\n"
                + "		a.nombre_articulo,\n"
                + "		id.descripcion_articulo,\n"
                + "		id.cantidad,\n"
                + "		ca.nombre_clase_articulo, \n"
                + "		id.placa,\n"
                + "		id.motor,\n"
                + "		id.chasis,\n"
                + "		id.serie,\n"
                + "		id.marca,\n"
                + "		id.carroceria,\n"
                + "		id.linea,\n"
                + "		id.capacidad,\n"
                + "		id.peso_kg, \n"
                + "		c.categoria,\n"
                + "		c2.clasificacion,\n"
                + "		id.modelo,\n"
                + "		id.tipo_servicio,\n"
                + "		um.nombre_unidad_medida\n"
                + "FROM orden_ingreso oi\n"
                + "INNER JOIN orden_ingreso_detalle id ON id.codigo_orden_ingreso =oi.codigo_orden_ingreso \n"
                + "INNER JOIN proveedor p ON p.nit=oi.nit_proveedor \n"
                + "INNER JOIN articulos a ON a.id=id.id_articulo \n"
                + "INNER JOIN clase_articulos ca ON ca.id=id.id_clase_articulos\n"
                + "INNER JOIN categoria c ON c.id=id.id_categoria \n"
                + "INNER JOIN clasificacion c2 ON c2.id=id.id_clasificacion \n"
                + "INNER JOIN unidad_medida um ON um.id=id.unidad_medida \n"
                + "WHERE id.procesado='N' AND oi.codigo_orden_ingreso =?";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, parameter.get("codigo_orden_ingreso").getAsString());
                ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            respObject = Util.writeResponseError("Cargar detalle Ordenes de Ingreso", ex.toString(), "/get-detalle-orden-ingreso");
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
    public Response getComboMunicipios() {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        query = "SELECT id ,nombre_municipio \n"
                + "FROM municipio m  \n"
                + "ORDER BY nombre_municipio";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                rs = ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar Combo", ex.toString(), "/get-combo-municipios");
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
    public Response getComboProveedor() {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        query = "SELECT nit ,razon_social \n"
                + "FROM proveedor p   \n"
                + "WHERE estado ='A'   \n"
                + "ORDER BY razon_social ";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                rs = ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar Combo", ex.toString(), "/get-combo-proveedor");
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
    public Response getComboAutorizado() {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        query = "SELECT nit ,nombre_completo \n"
                + "FROM autorizado a \n"
                + "WHERE estado =''\n"
                + "ORDER BY nombre_completo ";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                rs = ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar Combo", ex.toString(), "/get-combo-autorizado");
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
    public Response getComboAutorizacion() {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        query = "SELECT id ,nombre_autorizacion \n"
                + "FROM autorizacion a    \n"
                + "WHERE estado =''\n"
                + "ORDER BY nombre_autorizacion ";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                rs = ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar Combo", ex.toString(), "/get-combo-autorizacion");
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
    public Response getComboMotivo() {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        query = "SELECT id ,nombre_motivo \n"
                + "FROM motivo m   \n"
                + "WHERE estado =''\n"
                + "ORDER BY nombre_motivo ";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                rs = ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar Combo", ex.toString(), "/get-combo-motivo");
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
    public Response getComboResponsable() {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        query = "SELECT usuario, nombres ||' '||apellidos AS nombre\n"
                + "FROM usuarios u  \n"
                + "WHERE estado ='A'\n"
                + "ORDER BY nombres ||' '||apellidos ";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                rs = ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar Combo", ex.toString(), "/get-combo-responsable");
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
    public Response getDocumentosRequeridosProveedor(String json) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        JsonObject parameter = Util.readJsonObjectParameter(json);

        query = "SELECT ddd.id,ddd.nombre_documento FROM documentos_desintegracion_documental ddd \n"
                + "INNER JOIN rel_proveedor_documento rpd ON rpd.id_documento =ddd.id \n"
                + "WHERE rpd.nit_proveedor =?::numeric";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, !parameter.get("nit_proveedor").getAsString().equals("") ? parameter.get("nit_proveedor").getAsString() : "0");
                ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            respObject = Util.writeResponseError("Cargar detalle Ordenes de Ingreso", ex.toString(), "/get-detalle-orden-ingreso");
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
    public Response guardarDesintegracionDocumentalInicial(String json, List<FormDataBodyPart> parts, Usuario usuario) {
        JsonObject parameter = Util.readJsonObjectParameter(json);
        Response response = null;
        JsonObject respObject;
        String ext;
        String mimeType;
        String nombreArchivo;
        InputStream inputStream;
        OutputStream outputStream;
        String ruta;

        if (parameter != null) {
            for (FormDataBodyPart fileInputStream : parts) {

                mimeType = fileInputStream.getMediaType().toString();
                inputStream = fileInputStream.getEntityAs(InputStream.class);

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
                        ext = "";
                        break;
                }
                ruta = UPLOAD_FILE_SERVER + parameter.get("codigo_orden").getAsString();
                nombreArchivo = insertDesintegracionDocumentalInicial(json, ext, usuario);

                if (parameter.get("tiene_archivo").getAsString().equals("S")) {

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
                        respObject = Util.writeResponseError("Guardar documentación", ex.toString(), "/save-documentacion_inicial");
                        response = Util.responseApi(Util.getJsonString(respObject), Response.Status.INTERNAL_SERVER_ERROR);
                    } catch (IOException ex) {
                        Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
                        respObject = Util.writeResponseError("Guardar documentación", ex.toString(), "/save-documentacion_inicial");
                        response = Util.responseApi(Util.getJsonString(respObject), Response.Status.INTERNAL_SERVER_ERROR);
                    }
                }
                respObject = Util.writreResponseString("OK");
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);

            }

        }
        return response;
    }

    public String insertDesintegracionDocumentalInicial(String json, String ext, Usuario usuario) {
        String response = null;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "SELECT procesar_desintegracion_documental_inicial(?::json, ?::varchar, ?::varchar,'SAVE'::varchar) as resp";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, json);
                ps.setString(2, usuario.getId_usuario());
                ps.setString(3, ext);
                rs = ps.executeQuery();
                if (rs.next()) {
                    response = rs.getString("resp");
                }
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

        return response;
    }

    @Override
    public Response getDocumentacionInicialCargada(String cod_inspeccion) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        String json = "{}";

        query = "select am.id_documento_desintegracion,\n"
                + "am.codigo_orden_ingreso,\n"
                + "ddd.nombre_documento,\n"
                + "am.nombre_archivo,\n"
                + "am.path_archivo,\n"
                + "am.tiene_archivo\n"
                + "from archivos_desintegracion_inicial am\n"
                + "LEFT JOIN documentos_desintegracion_documental ddd  ON ddd.id =am.id_documento_desintegracion \n"
                + "where am.codigo_orden_ingreso =?";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, cod_inspeccion);
                ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            respObject = Util.writeResponseError("Cargar documentos desintegracion inicial", ex.toString(), "/get-documentacion_inicial");
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
    public Response finalizarDesintegracionDocumental(String cod_inspeccion, Usuario usuario) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "UPDATE orden_ingreso SET procesado ='S', usuario_ultima_modificacion=?, fecha_ultima_modificacion=now() WHERE codigo_orden_ingreso =?;";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, usuario.getId_usuario());
                ps.setString(2, cod_inspeccion);
                if (ps.executeUpdate() > 0) {
                    respObject = Util.writreResponseString("OK");
                    response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            respObject = Util.writeResponseError("Actualizar estado proceso orden", ex.toString(), "/finalizar-desintegracion-documetal");
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
    public Response getOrdenIngresoProcesadas(String json, Usuario usuario) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "SELECT codigo_orden_ingreso, \n"
                + "fecha,\n"
                + "m.nombre_municipio,\n"
                + "documento_tercero AS resolucion,\n"
                + "fecha_resolucion, \n"
                + "lote,\n"
                + "p.nit::varchar AS nit_proveedor,\n"
                + "p.razon_social AS proveedor,\n"
                + "dependencia, a.nombre_completo AS autorizado ,\n"
                + "a2.nombre_autorizacion AS autorizacion,\n"
                + "m2.nombre_motivo AS motivo,\n"
                + "u.nombres||' '||u.apellidos AS responsable1,\n"
                + "u2.nombres||' '||u2.apellidos AS responsable2,\n"
                + "oi.usuario_creador,\n"
                + "oi.fecha_creacion::timestamp(0),\n"
                + "oi.usuario_ultima_modificacion, \n"
                + "oi.fecha_ultima_modificacion::timestamp(0),\n"
                + "oi.id_cronograma\n"
                + "FROM orden_ingreso oi \n"
                + "INNER JOIN municipio m ON m.id =oi.id_municipio \n"
                + "INNER JOIN proveedor p ON p.nit =oi.nit_proveedor \n"
                + "INNER JOIN autorizado a ON a.nit =oi.autorizado \n"
                + "INNER JOIN autorizacion a2 ON a2.id =oi.autorizacion\n"
                + "INNER JOIN motivo m2 ON m2.id =oi.motivo \n"
                + "INNER JOIN usuarios u ON u.usuario =oi.responsable1 \n"
                + "INNER JOIN usuarios u2 ON u2.usuario =oi.responsable2\n"
                + "WHERE oi.procesado ='S'  AND 'N' IN (SELECT procesado FROM orden_ingreso_detalle  WHERE codigo_orden_ingreso =oi.codigo_orden_ingreso AND estado!='A' and id_articulo !=4)\n"
                + "AND (oi.responsable1=? OR oi.responsable2=? OR oi.responsable_patio =? ) \n"
                + "ORDER BY oi.fecha_creacion DESC";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, usuario.getId_usuario());
                ps.setString(2, usuario.getId_usuario());
                ps.setString(3, usuario.getId_usuario());
                ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            respObject = Util.writeResponseError("Cargar Ordenes de Ingreso", ex.toString(), "/get-orden-ingreso");
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
    public Response getDetalleOrdenIngresoProcesadas(String json) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        JsonObject parameter = Util.readJsonObjectParameter(json);

        query = "SELECT id.id as id_detalle_orden_ingreso, \n"
                + "  		id.codigo_orden_ingreso,\n"
                + "		tc.razon_social AS tipo_codigo,\n"
                + "		tc.nit as id_tipo_codigo,\n"
                + "		cod_identificacion,\n"
                + "		item,\n"
                + "		a.nombre_articulo AS articulo,\n"
                + "		a.id as id_articulo, \n"
                + "		descripcion_articulo,\n"
                + "		um.nombre_unidad_medida AS unidad_medida,\n"
                + "		um.id as id_unidad_medida,\n"
                + "		id.cantidad, \n"
                + "		peso_kg, 		\n"
                + "		ca.nombre_clase_articulo AS clase_articulos,\n"
                + "		ca.id AS id_clase_articulos,\n"
                + "		placa, \n"
                + "		motor, \n"
                + "		chasis, \n"
                + "		serie,\n"
                + "		marca, \n"
                + "		carroceria,\n"
                + "		modelo,\n"
                + "		linea,\n"
                + "		capacidad,\n"
                + "		tipo_servicio,\n"
                + "		COALESCE(ird.codigo_ingreso_remoto,'-') AS codigo_ingreso_remoto,\n"
                + "		id.procesado,\n"
                + "		c.categoria,\n"
                + "		c.id as id_categoria,\n"
                + "		c2.clasificacion,\n"
                + "		c2.id  as id_clasificacion\n"
                + "FROM public.orden_ingreso_detalle id\n"
                + "INNER JOIN proveedor tc ON tc.nit =id.tipo_codigo \n"
                + "INNER JOIN articulos a ON a.id =id.id_articulo \n"
                + "INNER JOIN unidad_medida um ON um.id =id.unidad_medida \n"
                + "INNER JOIN clase_articulos ca ON ca.id =id.id_clase_articulos \n"
                + "LEFT JOIN ingreso_remoto_detalle ird ON ird.codigo_orden_ingreso =id.codigo_orden_ingreso AND ird.id_detalle_orden_ingreso=id.id \n"
                + "left join categoria c on c.id=id.id_categoria \n"
                + "left join clasificacion c2 on c2.id=id.id_clasificacion \n"
                + "WHERE id.procesado='N' and id.estado !='A' and id.id_articulo !=4\n"
                + "AND id.codigo_orden_ingreso =?";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, parameter.get("codigo_orden_ingreso").getAsString());
                ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            respObject = Util.writeResponseError("Cargar detalle Ordenes de Ingreso", ex.toString(), "/get-detalle-orden-ingreso");
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
    public Response getComponentesClaseArticulo(String json) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        JsonObject parameter = Util.readJsonObjectParameter(json);

        query = "SELECT cca.id,\n"
                + "cca.nombre_componente,\n"
                + "rcc.archivo_obligatorio \n"
                + "FROM componentes_clase_articulos cca\n"
                + "INNER JOIN rel_clase_componentes rcc ON rcc.id_componente_clase_articulo =cca.id \n"
                + "INNER JOIN clase_articulos ca ON ca.id =rcc.id_clase_articulo \n"
                + "WHERE ca.id =?::int";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, !parameter.get("id_clase_articulo").getAsString().equals("") ? parameter.get("id_clase_articulo").getAsString() : "0");
                ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            respObject = Util.writeResponseError("Cargar detalle Ordenes de Ingreso", ex.toString(), "/get-detalle-orden-ingreso");
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
    public Response guardarArchivoIngresoRemoto(String json, List<FormDataBodyPart> parts, Usuario usuario) {
        JsonObject parameter = Util.readJsonObjectParameter(json);
        Response response = null;
        JsonObject respObject;
        String ext;
        String mimeType;
        String nombreArchivo;
        InputStream inputStream;
        OutputStream outputStream;
        String ruta;

        if (parameter != null) {
            for (FormDataBodyPart fileInputStream : parts) {

                mimeType = fileInputStream.getMediaType().toString();
                inputStream = fileInputStream.getEntityAs(InputStream.class);

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
                        ext = "";
                        break;
                }

                String codigo_ingreso_remoto = parameter.get("codigo_ingreso_remoto").getAsString();

                ruta = UPLOAD_FILE_SERVER + codigo_ingreso_remoto;
                nombreArchivo = insertArchivoDetalleIngresoRemoto(json, ext, usuario, codigo_ingreso_remoto);

                if (!ext.equals("")) {

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
                        respObject = Util.writeResponseError("Guardar documentación", ex.toString(), "/save-documentacion_inicial");
                        response = Util.responseApi(Util.getJsonString(respObject), Response.Status.INTERNAL_SERVER_ERROR);
                    } catch (IOException ex) {
                        Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
                        respObject = Util.writeResponseError("Guardar documentación", ex.toString(), "/save-documentacion_inicial");
                        response = Util.responseApi(Util.getJsonString(respObject), Response.Status.INTERNAL_SERVER_ERROR);
                    }
                }
                respObject = Util.writreResponseString(codigo_ingreso_remoto);
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);

            }

        }
        return response;

    }

    public String insertArchivoDetalleIngresoRemoto(String json, String ext, Usuario usuario, String codigo_ingreso_remoto) {
        String response = null;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "SELECT cargar_archivos_detalle_ingreso_remoto(?::json, ?::varchar, ?::varchar) as resp";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, json);
                ps.setString(2, usuario.getId_usuario());
                ps.setString(3, ext);
                rs = ps.executeQuery();
                if (rs.next()) {
                    response = rs.getString("resp");
                }
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

        return response;
    }

    @Override
    public Response finalizarIngresoRemoto(String cod_ingreso_remoto, Usuario usuario) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "UPDATE public.ingreso_remoto SET procesado='S', usuario_ultima_modificacion=?,fecha_ultima_modificacion=now() WHERE codigo_ingreso_remoto=?;";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, usuario.getId_usuario());
                ps.setString(2, cod_ingreso_remoto);
                if (ps.executeUpdate() > 0) {
                    respObject = Util.writreResponseString("OK");
                    response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            respObject = Util.writeResponseError("Actualizar estado proceso orden", ex.toString(), "/finalizar-desintegracion-documetal");
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
    public Response getOrdenIngresoRemotoNoFinalizadas(String json) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "SELECT \n"
                + "               ir.codigo_ingreso_remoto,\n"
                + "               ir.fecha_creacion::timestamp(0) ,\n"
                + "               p.nit::varchar AS nit_proveedor,\n"
                + "               p.razon_social AS proveedor, oi.dependencia,\n"
                + "               ir.usuario_creador\n"
                + "               FROM ingreso_remoto ir \n"
                + "               INNER JOIN ingreso_remoto_detalle ird ON ird.codigo_ingreso_remoto =ir.codigo_ingreso_remoto and ird.procesado = 'N' \n"
                + "               INNER JOIN orden_ingreso_detalle oid2 ON oid2.id=ird.id_detalle_orden_ingreso and oid2.id_articulo !=4 \n"
                + "               INNER JOIN orden_ingreso oi ON oi.codigo_orden_ingreso=ird.codigo_orden_ingreso \n"
                + "               INNER JOIN proveedor p ON p.nit =oi.nit_proveedor \n"
                + "               GROUP BY 1,2,3,4,5,6 ORDER BY ir.codigo_ingreso_remoto DESC";
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
            respObject = Util.writeResponseError("Cargar Ordenes de Ingreso", ex.toString(), "/get-orden-ingreso");
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
    public Response getDetalleIngresoRemoto(String cod_ingreso_remoto) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        String json = "{}";

        query = "SELECT ird.id_detalle_orden_ingreso AS id_detalle_ingreso_remoto,\n"
                + "		ird.codigo_ingreso_remoto,\n"
                + "		ird.codigo_orden_ingreso,\n"
                + "		ird.cantidad,\n"
                + "		id.cod_identificacion,\n"
                + "		p.razon_social AS proveedor,\n"
                + "		a.nombre_articulo,\n"
                + "		id.descripcion_articulo,\n"
                + "		ca.nombre_clase_articulo,\n"
                + "		id.placa,\n"
                + "		ird.procesado,\n"
                + "		id.placa,\n"
                + "		id.placa,\n"
                + "		id.motor,\n"
                + "		id.chasis,\n"
                + "		id.serie,\n"
                + "		id.marca,\n"
                + "		id.carroceria,\n"
                + "		id.linea,\n"
                + "		id.capacidad,\n"
                + "		id.peso_kg, \n"
                + "		c.categoria, \n"
                + "		c2.clasificacion, \n"
                + "		id.modelo,\n"
                + "		id.tipo_servicio,\n"
                + "		um.nombre_unidad_medida,\n"
                + "		ird.fecha_creacion::timestamp(0) \n"
                + " FROM ingreso_remoto_detalle ird  \n"
                + " INNER JOIN orden_ingreso_detalle id ON id.id=ird.id_detalle_orden_ingreso\n"
                + " INNER JOIN orden_ingreso oi ON oi.codigo_orden_ingreso =id.codigo_orden_ingreso \n"
                + " INNER JOIN articulos a ON a.id =id.id_articulo \n"
                + " INNER JOIN clase_articulos ca ON ca.id =id.id_clase_articulos \n"
                + " INNER JOIN proveedor p ON p.nit =oi.nit_proveedor \n"
                + " INNER JOIN unidad_medida um ON um.id=id.unidad_medida \n"
                + " INNER JOIN categoria c ON c.id=id.id_categoria  \n"
                + " INNER JOIN clasificacion c2 ON c2.id=id.id_clasificacion  \n"
                + " WHERE ird.codigo_ingreso_remoto=? and id.id_articulo !=4 and id.estado != 'A'";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, cod_ingreso_remoto);
                ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            respObject = Util.writeResponseError("Cargar detalle ingreso remoto", ex.toString(), "/get-detalle-ingreso-remoto");
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
    public Response getOrdenIngresoRemotoFinalizadas(String json, Usuario usuario) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "SELECT \n"
                + "ir.codigo_ingreso_remoto,\n"
                + "ir.fecha_creacion::timestamp(0) ,\n"
                + "p.nit::varchar AS nit_proveedor,\n"
                + "p.razon_social AS proveedor,\n"
                + "oi.documento_tercero AS resolucion,\n"
                + "oi.dependencia, \n"
                + "ir.fecha_creacion::timestamp(0), \n"
                + "ir.usuario_creador\n"
                + "FROM ingreso_remoto ir \n"
                + "INNER JOIN ingreso_remoto_detalle ird ON ird.codigo_ingreso_remoto =ir.codigo_ingreso_remoto AND ird.traslado='N' and ird.procesado = 'S'\n"
                + "INNER JOIN orden_ingreso_detalle oid2 ON oid2.id=ird.id_detalle_orden_ingreso and oid2.id_articulo !=4 \n"
                + "INNER JOIN orden_ingreso oi ON oi.codigo_orden_ingreso=ird.codigo_orden_ingreso \n"
                + "INNER JOIN proveedor p ON p.nit =oi.nit_proveedor \n"
                + "AND (oi.responsable1=? OR oi.responsable2=? OR oi.responsable_patio =? )\n"
                + "GROUP BY 1,2,3,4,5,6,7,8 order by ir.codigo_ingreso_remoto desc";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, usuario.getId_usuario());
                ps.setString(2, usuario.getId_usuario());
                ps.setString(3, usuario.getId_usuario());
                ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            respObject = Util.writeResponseError("Cargar Ordenes de Ingreso", ex.toString(), "/get-orden-ingreso");
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
    public Response getComboPatios(Usuario usuario) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        query = "SELECT p.id,nombre_patio,base_caja,saldo_caja, base_caja - saldo_caja as valor_retanquear \n"
                + "FROM patios p \n"
                + "inner join rel_usuarios_patios up on up.id_patio=p.id  \n"
                + "WHERE estado ='' and up.usuario=? \n"
                + "ORDER BY nombre_patio";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, usuario.getId_usuario());
                rs = ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar Combo", ex.toString(), "/get-combo-municipios");
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
    public Response guardarTraslado(String json, Usuario usuario) {
        Response response = null;
        JsonObject respObject = null;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "SELECT guardar_traslado(?::json, ?::varchar) as resp";
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
            respObject = Util.writeResponseError("Guardar remision", ex.toString(), "/save-remision");
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
    public Response finalizarItemIngresoRemoto(int id_detalle_orden_ingreso, Usuario usuario) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "UPDATE ingreso_remoto_detalle SET procesado ='S',usuario_ultima_modificacion=?,fecha_ultima_modificacion =now() WHERE id_detalle_orden_ingreso =? ";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, usuario.getId_usuario());
                ps.setInt(2, id_detalle_orden_ingreso);
                if (ps.executeUpdate() > 0) {
                    respObject = Util.writreResponseString("OK");
                    response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            respObject = Util.writeResponseError("Actualizar estado proceso orden", ex.toString(), "/finalizar-item-ingreso-remoto");
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
    public Response getTraslados(Usuario usuario) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        query = "SELECT t.id,\n"
                + "	t.codigo_traslado,\n"
                + "	entidad, \n"
                + "	fecha,\n"
                + "	conductor,\n"
                + "	cedula_conductor,\n"
                + "	t.placa,\n"
                + "	t.empresa,\n"
                + "	origen, \n"
                + "	p.nombre_patio, \n"
                + "	id_patio_destino,\n"
                + "	descripcion,\n"
                + "	t.usuario_creador,\n"
                + "	t.fecha_creacion,\n"
                + "	oid2.cod_identificacion\n"
                + "FROM public.traslados t\n"
                + "inner join traslado_detalle td2 on td2.codigo_traslado =t.codigo_traslado \n"
                + "inner join orden_ingreso_detalle oid2 on oid2.id =td2.id_detalle_orden_ingreso \n"
                + "inner join patios p on p.id=oid2.patio_id\n"
                + "WHERE t.procesado='N' \n"
                + "AND 'N' IN (SELECT procesado FROM traslado_detalle td  WHERE codigo_traslado =t.codigo_traslado AND estado !='A')\n"
                + "AND oid2.patio_id IN (SELECT id_patio  FROM rel_usuarios_patios rup WHERE rup.usuario=?) \n"
                + "group by 1,2,3,4,5,6,7,8,9,10,11,12,13,14,15\n"
                + "union all \n"
                + "select id.id,\n"
                + "'ECR'||id.id as codigo_traslado,\n"
                + "p3.razon_social ,\n"
                + "'0100-01-01'::date,\n"
                + "'NO APLICA POR ECR',\n"
                + "'NO APLICA POR ECR',\n"
                + "id.cod_identificacion,\n"
                + "'NO APLICA POR ECR',\n"
                + "'NO APLICA POR ECR',\n"
                + "p2.nombre_patio,\n"
                + "p2.id  as id_patio_destino,\n"
                + "'NO APLICA POR ECR',\n"
                + "'NO APLICA POR ECR',\n"
                + "'0100-01-01'::date,\n"
                + "id.cod_identificacion\n"
                + "from orden_ingreso_detalle id\n"
                + "inner join orden_ingreso oi on oi.codigo_orden_ingreso =id.codigo_orden_ingreso \n"
                + "inner join proveedor p3 on p3.nit =oi.nit_proveedor \n"
                + "inner join patios p2 on p2.id = id.patio_id\n"
                + "where id.estado='' AND id_articulo =4 and id.id not in (select id_detalle_orden_ingreso  from ingreso_local_detalle where  id_detalle_orden_ingreso =id.id) \n"
                + "AND id.patio_id IN (SELECT id_patio  FROM rel_usuarios_patios rup WHERE rup.usuario=?) \n"
                + "group by 1,2,3,4,5,6,7,8,9,10,11,12,13,14,15";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, usuario.getId_usuario());
                ps.setString(2, usuario.getId_usuario());
                ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            respObject = Util.writeResponseError("Cargar traslados", ex.toString(), "/get-traslados");
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
    public Response guardarOrdenDetalleIngresoRemoto(String json, Usuario usuario) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "SELECT generar_ingreso_remoto(?::json, ?::varchar) as resp";
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
    public Response getChecksDetalleIngresoRemoto(int id_detalle_ingreso_remoto) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        String json = "{}";

        query = " SELECT aird.id AS id_documento, aird.codigo_ingreso_remoto,\n"
                + "		  		 aird.codigo_orden_ingreso,\n"
                + "		  		 aird.id_detalle_orden_ingreso as id_detalle_ingreso_remoto,\n"
                + "		  		 aird.id_componentes_clase_articulos, \n"
                + "		  		 cca.nombre_componente, \n"
                + "		  		 aird.nombre_archivo,\n"
                + "		  		 aird.path_archivo,\n"
                + "		  		 aird.tiene_archivo,\n"
                + "		  		 aird.cantidad,\n"
                + "		  		 aird.procesado,\n"
                + "		  		 rc.archivo_obligatorio, \n"
                + "		  		 rc.orden \n"
                + "		 FROM  archivos_ingreso_remoto_detalle aird \n"
                + "		 INNER JOIN orden_ingreso_detalle id ON id.id=aird.id_detalle_orden_ingreso \n"
                + "		 INNER JOIN rel_clase_componentes rc ON rc.id_componente_clase_articulo=aird.id_componentes_clase_articulos  AND rc.id_clase_articulo=id.id_clase_articulos \n"
                + "		 INNER JOIN componentes_clase_articulos cca ON cca.id= rc.id_componente_clase_articulo\n"
                + "		 WHERE id_detalle_orden_ingreso =? \n"
                + "		 GROUP BY 1,2,3,4,5,6,7,8,9,10,11,12,13 ORDER BY rc.orden ";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setInt(1, id_detalle_ingreso_remoto);
                ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            respObject = Util.writeResponseError("Cargar checks detalle ingreso remoto", ex.toString(), "/get-checks-detalle-ingreso-remoto");
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
    public Response getDetalleIngresoRemotoSinRemision(String cod_ingreso_remoto) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        String json = "{}";

        query = " SELECT ird.id_detalle_orden_ingreso AS id_detalle_ingreso_remoto,ird.codigo_ingreso_remoto,\n"
                + "		ird.codigo_orden_ingreso,\n"
                + "		ird.cantidad,\n"
                + "		id.cod_identificacion,\n"
                + "		p.razon_social AS proveedor,\n"
                + "		a.nombre_articulo,\n"
                + "		id.descripcion_articulo,\n"
                + "		ca.nombre_clase_articulo,\n"
                + "		id.placa,\n"
                + "		ird.procesado,\n"
                + "		id.placa,\n"
                + "		id.motor,\n"
                + "		id.chasis,\n"
                + "		id.serie,\n"
                + "		id.marca,\n"
                + "		id.carroceria,\n"
                + "		id.linea,\n"
                + "		id.capacidad,\n"
                + "		id.peso_kg, \n"
                + "		c.categoria,\n"
                + "		c2.clasificacion, \n"
                + "		id.modelo,\n"
                + "		id.tipo_servicio,\n"
                + "		um.nombre_unidad_medida,\n"
                + "		ird.fecha_creacion::timestamp(0) \n"
                + " FROM ingreso_remoto_detalle ird  \n"
                + " INNER JOIN orden_ingreso_detalle id ON id.id=ird.id_detalle_orden_ingreso and id.id_articulo !=4\n"
                + " INNER JOIN orden_ingreso oi ON oi.codigo_orden_ingreso =id.codigo_orden_ingreso \n"
                + " INNER JOIN articulos a ON a.id =id.id_articulo \n"
                + " INNER JOIN clase_articulos ca ON ca.id =a.id\n"
                + " INNER JOIN proveedor p ON p.nit =oi.nit_proveedor \n"
                + " INNER JOIN categoria c ON c.id=id.id_categoria \n"
                + "INNER JOIN clasificacion c2 ON c2.id=id.id_clasificacion \n"
                + "INNER JOIN unidad_medida um ON um.id=id.unidad_medida \n"
                + "WHERE ird.codigo_ingreso_remoto =? AND ird.traslado='N'  and ird.procesado = 'S'";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, cod_ingreso_remoto);
                ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            respObject = Util.writeResponseError("Cargar detalle ingreso remoto", ex.toString(), "/get-detalle-ingreso-remoto");
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
    public Response guardarIngresoLocal(String json, Usuario usuario) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "SELECT generar_ingreso_local(?::json, ?::varchar) as resp";
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
    public Response getDetalleTrasladoSinProcesar(String cod_ingreso_remoto) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "SELECT  rd.codigo_traslado,\n"
                + "		ir.codigo_ingreso_remoto, \n"
                + "		id.codigo_orden_ingreso,		\n"
                + "		tc.razon_social AS tipo_codigo,\n"
                + "		id.cod_identificacion,\n"
                + "		id.item,\n"
                + "		a.nombre_articulo AS articulo,\n"
                + "		id.descripcion_articulo,\n"
                + "		um.nombre_unidad_medida AS unidad_medida,\n"
                + "		id.cantidad, \n"
                + "		id.peso_kg, \n"
                + "		ca.id AS id_clase_articulos, \n"
                + "		ca.nombre_clase_articulo AS clase_articulos,\n"
                + "		id.placa, \n"
                + "		id.motor, \n"
                + "		id.chasis, \n"
                + "		id.serie,\n"
                + "		id.marca, \n"
                + "		id.carroceria,\n"
                + "		id.modelo,\n"
                + "		id.linea,\n"
                + "		id.capacidad,\n"
                + "		tipo_servicio\n"
                + "FROM  traslado_detalle rd \n"
                + "INNER JOIN ingreso_remoto ir ON ir.codigo_ingreso_remoto =rd.codigo_ingreso_remoto \n"
                + "INNER JOIN ingreso_remoto_detalle ird ON ird.codigo_ingreso_remoto =ir.codigo_ingreso_remoto \n"
                + "INNER JOIN orden_ingreso_detalle id ON id.codigo_orden_ingreso=ird.codigo_orden_ingreso AND id.id=ird.id_detalle_orden_ingreso \n"
                + "INNER JOIN orden_ingreso oi ON oi.codigo_orden_ingreso =id.codigo_orden_ingreso \n"
                + "INNER JOIN proveedor tc ON tc.nit =id.tipo_codigo \n"
                + "INNER JOIN articulos a ON a.id =id.id_articulo \n"
                + "INNER JOIN unidad_medida um ON um.id =id.unidad_medida \n"
                + "INNER JOIN clase_articulos ca ON ca.id =id.id_clase_articulos\n"
                + "WHERE rd.procesado ='N' AND rd.codigo_traslado =? \n"
                + "GROUP BY 1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, cod_ingreso_remoto);
                ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            respObject = Util.writeResponseError("Cargar detalle Ordenes de Ingreso", ex.toString(), "/get-detalle-orden-ingreso");
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
    public Response updateOrdenIngreso(String json, Usuario usuario) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        JsonObject parameter = Util.readJsonObjectParameter(json);

        query = "UPDATE public.orden_ingreso\n"
                + "               SET fecha=?::date, \n"
                + "		id_municipio=?::int, \n"
                + "		documento_tercero=upper(?)::varchar,\n"
                + "		fecha_resolucion=?::date, \n"
                + "		lote=?::varchar, \n"
                + "		nit_proveedor=?::numeric,\n"
                + "		nit_propietario=?::numeric,\n"
                + "		dependencia=?::varchar,\n"
                + "		autorizado=?::numeric,\n"
                + "		autorizacion=?::int,\n"
                + "		motivo=?::int,\n"
                + "		responsable1=?::varchar,\n"
                + "		responsable2=?::varchar,\n"
                + "		usuario_ultima_modificacion=?,\n"
                + "		fecha_ultima_modificacion=now(),\n"
                + "		id_cronograma=?::int,\n"
                + "		responsable_patio=?::varchar,\n"
                + "		valor_compra=?::numeric\n"
                + "	WHERE codigo_orden_ingreso=?;";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, parameter.get("fecha").getAsString());
                ps.setInt(2, parameter.get("id_municipio").getAsInt());
                ps.setString(3, parameter.get("documento_tercero").getAsString());
                ps.setString(4, parameter.get("fecha_resolucion").getAsString());
                ps.setString(5, parameter.get("lote").getAsString());
                ps.setString(6, parameter.get("nit_proveedor").getAsString());
                ps.setString(7, parameter.get("nit_propietario").getAsString());
                ps.setString(8, parameter.get("dependencia").getAsString());
                ps.setString(9, parameter.get("autorizado").getAsString());
                ps.setInt(10, parameter.get("autorizacion").getAsInt());
                ps.setInt(11, parameter.get("motivo").getAsInt());
                ps.setString(12, parameter.get("responsable1").getAsString());
                ps.setString(13, parameter.get("responsable2").getAsString());
                ps.setString(14, usuario.getId_usuario());
                ps.setInt(15, parameter.get("id_cronograma").getAsInt());
                ps.setString(16, parameter.get("responsable_patio").getAsString());
                ps.setString(17, parameter.get("valor_compra").getAsString());
                ps.setString(18, parameter.get("codigo_orden_ingreso").getAsString());
                if (ps.executeUpdate() > 0) {
                    respObject = Util.writreResponseString("OK");
                    response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            respObject = Util.writeResponseError("Actualizar orden", ex.toString(), "/update-orden-ingreso");
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
    public Response updateDetalleOrdenIngreso(String json, Usuario usuario) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        JsonObject parameter = Util.readJsonObjectParameter(json);

        query = "UPDATE public.orden_ingreso_detalle\n"
                + "		SET tipo_codigo=?::numeric, \n"
                + "		cod_identificacion=UPPER(?::VARCHAR),\n"
                + "		item=?::int,\n"
                + "		id_articulo=?::int,\n"
                + "		descripcion_articulo=UPPER(?::VARCHAR), \n"
                + "		unidad_medida=?::int,\n"
                + "		cantidad=?::int,\n"
                + "		peso_kg=?::numeric,\n"
                + "		id_clase_articulos=?::int,\n"
                + "		placa=UPPER(?::VARCHAR),\n"
                + "		motor=UPPER(?::VARCHAR),\n"
                + "		chasis=UPPER(?::VARCHAR), \n"
                + "		serie=UPPER(?::VARCHAR),\n"
                + "		marca=UPPER(?::VARCHAR),\n"
                + "		carroceria=UPPER(?::VARCHAR),\n"
                + "		modelo=UPPER(?::VARCHAR), \n"
                + "		linea=UPPER(?::VARCHAR), \n"
                + "		capacidad=UPPER(?::VARCHAR),\n"
                + "		tipo_servicio=UPPER(?::VARCHAR),\n"
                + "		id_categoria=?::int,\n"
                + "		id_clasificacion=?::int,\n"
                + "		usuario_ultima_modificacion=UPPER(?::VARCHAR), \n"
                + "		patio_id=?::int, \n"
                + "		fecha_ultima_modificacion=NOW()\n"
                + "WHERE id=?::int \n"
                + "and codigo_orden_ingreso=?::varchar;";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, parameter.get("tipo_codigo").getAsString());
                ps.setString(2, parameter.get("cod_identificacion").getAsString());
                ps.setInt(3, parameter.get("item").getAsInt());
                ps.setInt(4, parameter.get("id_articulo").getAsInt());
                ps.setString(5, parameter.get("descripcion_articulo").getAsString());
                ps.setInt(6, parameter.get("unidad_medida").getAsInt());
                ps.setInt(7, parameter.get("cantidad").getAsInt());
                ps.setString(8, parameter.get("peso_kg").getAsString());
                ps.setString(9, parameter.get("id_clase_articulos").getAsString());
                ps.setString(10, parameter.get("placa").getAsString());
                ps.setString(11, parameter.get("motor").getAsString());
                ps.setString(12, parameter.get("chasis").getAsString());
                ps.setString(13, parameter.get("serie").getAsString());
                ps.setString(14, parameter.get("marca").getAsString());
                ps.setString(15, parameter.get("carroceria").getAsString());
                ps.setString(16, parameter.get("modelo").getAsString());
                ps.setString(17, parameter.get("linea").getAsString());
                ps.setString(18, parameter.get("capacidad").getAsString());
                ps.setString(19, parameter.get("tipo_servicio").getAsString());
                ps.setString(20, parameter.get("id_categoria").getAsString());
                ps.setString(21, parameter.get("id_clasificacion").getAsString());
                ps.setString(22, usuario.getId_usuario());
                ps.setInt(23, parameter.get("id_patio").getAsInt());
                ps.setString(24, parameter.get("id_detalle_orden_ingreso").getAsString());
                ps.setString(25, parameter.get("codigo_orden_ingreso").getAsString());
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
            respObject = Util.writeResponseError("Actualizar orden", ex.toString(), "/update-detalle-orden-ingreso");
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
    public Response deleteOrdenIngreso(String json, Usuario usuario) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        JsonObject parameter = Util.readJsonObjectParameter(json);

        query = "update orden_ingreso set estado = 'A', motivo_anulacion = upper(?), fecha_anulacion = now(), usuario_anulacion = ? where codigo_orden_ingreso = ?;"
                + "update orden_ingreso_detalle set estado = 'A' where codigo_orden_ingreso = ?;";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, parameter.get("motivo_anulacion").getAsString());
                ps.setString(2, usuario.getId_usuario());
                ps.setString(3, parameter.get("codigo_orden_ingreso").getAsString());
                ps.setString(4, parameter.get("codigo_orden_ingreso").getAsString());
                if (ps.executeUpdate() > 0) {
                    respObject = Util.writreResponseString("OK");
                    response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            respObject = Util.writeResponseError("Actualizar orden", ex.toString(), "/delete-orden-ingreso");
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
    public Response deleteDetalleOrdenIngreso(int idDetalle, Usuario usuario) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "update orden_ingreso_detalle set estado ='A', usuario_ultima_modificacion=?, fecha_ultima_modificacion =NOW() where id=?::INT;";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, usuario.getId_usuario());
                ps.setInt(2, idDetalle);
                if (ps.executeUpdate() > 0) {
                    respObject = Util.writreResponseString("OK");
                    response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            respObject = Util.writeResponseError("Actualizar orden", ex.toString(), "/delete-orden-ingreso");
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
    public Response getComboTipoArticulo() {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        query = "select id,nombre_articulo from articulos order by nombre_articulo";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                rs = ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar Combo", ex.toString(), "/get-combo-tipo-articulo");
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
    public Response getComboUnidadMedida() {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        query = "select id,nombre_unidad_medida  from unidad_medida um order by nombre_unidad_medida";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                rs = ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar Combo", ex.toString(), "/get-combo-unidad-medida");
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
    public Response getComboClaseArticulo() {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        query = "select id, nombre_clase_articulo, id_articulo from clase_articulos order by nombre_clase_articulo";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                rs = ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar Combo", ex.toString(), "/get-combo-clase-articulo");
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
    public Response getComboCategoria() {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        query = "select id,categoria  from categoria c order by categoria";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                rs = ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar Combo", ex.toString(), "/get-combo-tipo-articulo");
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
    public Response getComboClasificacion() {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        query = "select id,clasificacion  from clasificacion c order by clasificacion";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                rs = ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar Combo", ex.toString(), "/get-combo-tipo-articulo");
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
    public Response crearCronograma(String json, Usuario usuario) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "SELECT crear_cronograma(?::json, ?::varchar) as resp";
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
            respObject = Util.writeResponseError("Crear Cronograma", ex.toString(), "/crear-cronograma");
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
    public Response getComboCronograma() {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        query = "select id,actividad from actividad_cronograma order by actividad";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                rs = ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar Combo", ex.toString(), "/get-combo-tipo-articulo");
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
    public Response updateCronograma(String json, Usuario usuario) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        JsonObject parameter = Util.readJsonObjectParameter(json);

        query = "UPDATE public.cronograma\n"
                + "SET descripcion=upper(?), nit_proveedor=?::numeric,usuario_ultima_modificacion=?, \n"
                + "fecha_ultima_modificacion=now(), propietario_id=?::numeric \n"
                + "WHERE id =?;";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, parameter.get("descripcion").getAsString());
                ps.setString(2, parameter.get("nit_proveedor").getAsString());
                ps.setString(3, usuario.getId_usuario());
                ps.setString(4, parameter.get("propietario_id").getAsString());
                ps.setInt(5, parameter.get("id_cronograma").getAsInt());
                if (ps.executeUpdate() > 0) {
                    respObject = Util.writreResponseString("OK");
                    response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            respObject = Util.writeResponseError("Actualizar cronograma", ex.toString(), "/update-cronograma");
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
    public Response updateCronogramaDetalle(String json, Usuario usuario) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        JsonObject parameter = Util.readJsonObjectParameter(json);

        query = "UPDATE public.cronograma_detalle\n"
                + "SET  resolucion=?, dependencia=?, id_actividad=?, fecha_inicio=?::date, fecha_fin=?::date, usuario_ultima_modificacion=?, fecha_ultima_modificacion=now(),\n"
                + "valor=?::numeric,componente=?,kilos=?::numeric,id_dependencia=?::numeric,id_grupo=?::numeric,id_centro_acopio=?::numeric \n"
                + "WHERE id =? and id_cronograma=?;";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, parameter.get("resolucion").getAsString());
                ps.setString(2, parameter.get("dependencia").getAsString());
                ps.setInt(3, parameter.get("id_actividad").getAsInt());
                ps.setString(4, parameter.get("fecha_inicio").getAsString());
                ps.setString(5, parameter.get("fecha_fin").getAsString());
                ps.setString(6, usuario.getId_usuario());
                ps.setString(7, parameter.get("valor").getAsString());
                ps.setString(8, parameter.get("componente").getAsString());
                ps.setString(9, parameter.get("kilos").getAsString());
                ps.setInt(10, parameter.get("id_dependencia").getAsInt());
                ps.setInt(11, parameter.get("id_grupo").getAsInt());
                ps.setInt(12, parameter.get("id_centro_acopio").getAsInt());
                ps.setInt(13, parameter.get("id_detalle_cronograma").getAsInt());
                ps.setInt(14, parameter.get("id_cronograma").getAsInt());
                if (ps.executeUpdate() > 0) {
                    respObject = Util.writreResponseString("OK");
                    response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            respObject = Util.writeResponseError("Actualizar detalle cronograma", ex.toString(), "/update-cronograma-detalle");
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
    public Response getCronograma() {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "SELECT c.id,\n"
                + "	descripcion,\n"
                + "	p.razon_social as proveedor,\n"
                + "	nit_proveedor,\n"
                + "	estado_cronograma,\n"
                + "	c.usuario_creador, \n"
                + "	c.fecha_creacion, \n"
                + "	c.usuario_ultima_modificacion, \n"
                + "	c.fecha_ultima_modificacion\n"
                + "FROM public.cronograma c\n"
                + "inner join proveedor p on p.nit =c.nit_proveedor order by c.fecha_creacion desc";
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
            respObject = Util.writeResponseError("Cargar detalle Ordenes de Ingreso", ex.toString(), "/get-detalle-orden-ingreso");
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
    public Response getCronogramaDetalle(int idCronogramaDetalle) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "SELECT cd.id as id_detalle_cronograma,\n"
                + "	resolucion,\n"
                + "	id_cronograma,\n"
                + "	dependencia,\n"
                + "	ac.actividad,\n"
                + "	id_actividad,\n"
                + "	fecha_inicio,\n"
                + "	fecha_fin,\n"
                + "	usuario_creador,\n"
                + "	fecha_creacion,\n"
                + "	usuario_ultima_modificacion,\n"
                + "	fecha_ultima_modificacion,\n"
                + "	valor,\n"
                + "	componente,\n"
                + "	kilos,id_dependencia,ac.orden, validar_estado_orden(id_cronograma, ac.proceso_interno, dependencia, ac.actividad), id_grupo, id_centro_acopio\n"
                + "FROM public.cronograma_detalle cd\n"
                + "inner join actividad_cronograma ac on ac.id =cd.id_actividad \n"
                + "where cd.id_cronograma=?  order by dependencia, ac.orden asc";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setInt(1, idCronogramaDetalle);
                ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            respObject = Util.writeResponseError("Cargar detalle Ordenes de Ingreso", ex.toString(), "/get-detalle-orden-ingreso");
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
    public Response getTrasladosDetalle(String codigoTraslado) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "SELECT  \n"
                + "    		td.id as id_traslado, \n"
                + "    		td.codigo_traslado,\n"
                + "    		td.codigo_ingreso_remoto, \n"
                + "    		td.id_detalle_orden_ingreso, \n"
                + "    		id.codigo_orden_ingreso,		\n"
                + "    		tc.razon_social AS tipo_codigo,\n"
                + "    		id.cod_identificacion,\n"
                + "    		id.item,\n"
                + "    		a.nombre_articulo AS articulo,\n"
                + "    		id.descripcion_articulo,\n"
                + "    		um.nombre_unidad_medida AS unidad_medida,\n"
                + "    		id.cantidad, \n"
                + "    		id.peso_kg, \n"
                + "    		ca.id AS id_clase_articulos, \n"
                + "    		ca.nombre_clase_articulo AS clase_articulos,\n"
                + "    		id.placa, \n"
                + "    		id.motor, \n"
                + "    		id.chasis, \n"
                + "    		id.serie,\n"
                + "    		id.marca, \n"
                + "    		id.carroceria,\n"
                + "    		id.modelo,\n"
                + "    		id.linea,\n"
                + "    		id.capacidad,\n"
                + "    		id.tipo_servicio,\n"
                + "    		c.categoria,\n"
                + "    		c2.clasificacion,oi.dependencia \n"
                + "    FROM traslado_detalle td\n"
                + "    INNER JOIN orden_ingreso_detalle id ON id.id=td.id_detalle_orden_ingreso\n"
                + "    INNER JOIN orden_ingreso oi ON oi.codigo_orden_ingreso =id.codigo_orden_ingreso \n"
                + "    INNER JOIN proveedor tc ON tc.nit =id.tipo_codigo \n"
                + "    INNER JOIN articulos a ON a.id =id.id_articulo \n"
                + "    INNER JOIN unidad_medida um ON um.id =id.unidad_medida \n"
                + "    INNER JOIN clase_articulos ca ON ca.id =id.id_clase_articulos\n"
                + "    INNER JOIN categoria c ON c.id =id.id_categoria \n"
                + "    INNER JOIN clasificacion c2  ON c2.id =id.id_clasificacion\n"
                + "    WHERE td.procesado ='N' and td.codigo_traslado=?\n"
                + "    GROUP BY 1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28\n"
                + "    union all                \n"
                + "    SELECT  \n"
                + "    		id.id as id_traslado, \n"
                + "    		'ECR'||id.id as codigo_traslado,\n"
                + "    		'NO APLICA POR ECR' as ingreso_remoto, \n"
                + "    		id.id as id_detalle_orden_ingreso, \n"
                + "    		id.codigo_orden_ingreso,		\n"
                + "    		tc.razon_social AS tipo_codigo,\n"
                + "    		id.cod_identificacion,\n"
                + "    		id.item,\n"
                + "    		a.nombre_articulo AS articulo,\n"
                + "    		id.descripcion_articulo,\n"
                + "    		um.nombre_unidad_medida AS unidad_medida,\n"
                + "    		id.cantidad, \n"
                + "    		id.peso_kg, \n"
                + "    		ca.id AS id_clase_articulos, \n"
                + "    		ca.nombre_clase_articulo AS clase_articulos,\n"
                + "    		id.placa, \n"
                + "    		id.motor, \n"
                + "    		id.chasis, \n"
                + "    		id.serie,\n"
                + "    		id.marca, \n"
                + "    		id.carroceria,\n"
                + "    		id.modelo,\n"
                + "    		id.linea,\n"
                + "    		id.capacidad,\n"
                + "    		id.tipo_servicio,\n"
                + "    		c.categoria,\n"
                + "    		c2.clasificacion,oi.dependencia \n"
                + "    FROM orden_ingreso_detalle id \n"
                + "    INNER JOIN orden_ingreso oi ON oi.codigo_orden_ingreso =id.codigo_orden_ingreso \n"
                + "    INNER JOIN proveedor tc ON tc.nit =id.tipo_codigo \n"
                + "    INNER JOIN articulos a ON a.id =id.id_articulo \n"
                + "    INNER JOIN unidad_medida um ON um.id =id.unidad_medida \n"
                + "    INNER JOIN clase_articulos ca ON ca.id =id.id_clase_articulos\n"
                + "    INNER JOIN categoria c ON c.id =id.id_categoria \n"
                + "    INNER JOIN clasificacion c2  ON c2.id =id.id_clasificacion\n"
                + "    WHERE  'ECR'||id.id =?\n"
                + "    GROUP BY 1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28;";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, codigoTraslado);
                ps.setString(2, codigoTraslado);
                ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            respObject = Util.writeResponseError("Cargar Ordenes de Ingreso", ex.toString(), "/get-orden-ingreso");
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
    public Response getDocumentosIngresoLocal(String json) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        JsonObject parameter = Util.readJsonObjectParameter(json);

        query = "SELECT \n"
                + "	 aird.id_componentes_clase_articulos, \n"
                + "	 cca.nombre_componente\n"
                + "	 FROM  archivos_ingreso_remoto_detalle aird \n"
                + "	 INNER JOIN orden_ingreso_detalle id ON id.id=aird.id_detalle_orden_ingreso \n"
                + "	 INNER JOIN rel_clase_componentes rc ON rc.id_componente_clase_articulo=aird.id_componentes_clase_articulos  AND rc.id_clase_articulo=id.id_clase_articulos \n"
                + "	 INNER JOIN componentes_clase_articulos cca ON cca.id= rc.id_componente_clase_articulo\n"
                + "	 WHERE id_detalle_orden_ingreso =? AND codigo_ingreso_remoto =?\n"
                + "	 GROUP BY 1,2";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setInt(1, parameter.get("id_detalle_orden_ingreso").getAsInt());
                ps.setString(2, parameter.get("codigo_ingreso_remoto").getAsString());
                ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            respObject = Util.writeResponseError("Cargar detalle Ordenes de Ingreso", ex.toString(), "/get-detalle-orden-ingreso");
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
    public Response getCronogramaProveedor(String nit_proveedor) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "SELECT c.id,\n"
                + "	descripcion,\n"
                + "	p.razon_social as proveedor,\n"
                + "	nit_proveedor,\n"
                + "	estado_cronograma,\n"
                + "	c.usuario_creador, \n"
                + "	c.fecha_creacion, \n"
                + "	c.usuario_ultima_modificacion, \n"
                + "	c.fecha_ultima_modificacion\n"
                + "FROM public.cronograma c\n"
                + "inner join proveedor p on p.nit =c.nit_proveedor\n"
                + "where nit_proveedor=?::numeric;";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, nit_proveedor);
                ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            respObject = Util.writeResponseError("Cargar cronograma", ex.toString(), "/get-cronograma-proveedor");
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
    public Response insertCronogramaDetalle(String json, Usuario usuario) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        JsonObject parameter = Util.readJsonObjectParameter(json);

        query = "INSERT INTO public.cronograma_detalle\n"
                + "(resolucion, id_cronograma, dependencia, id_actividad, fecha_inicio, fecha_fin, valor, componente, kilos, usuario_creador, fecha_creacion,id_dependencia,id_grupo, id_centro_acopio)\n"
                + "VALUES(UPPER(?)::varchar,?::int,UPPER(?)::varchar,?::int,?::date,?::date,?::numeric,?::varchar,?::numeric,?::varchar,now(),?::numeric,?::numeric,?::numeric);";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, parameter.get("resolucion").getAsString());
                ps.setInt(2, parameter.get("id_cronograma").getAsInt());
                ps.setString(3, parameter.get("dependencia").getAsString());
                ps.setInt(4, parameter.get("id_actividad").getAsInt());
                ps.setString(5, parameter.get("fecha_inicio").getAsString());
                ps.setString(6, parameter.get("fecha_fin").getAsString());
                ps.setString(7, parameter.get("valor").getAsString());
                ps.setString(8, parameter.get("componente").getAsString());
                ps.setString(9, parameter.get("kilos").getAsString());
                ps.setString(10, usuario.getId_usuario());
                ps.setInt(11, parameter.get("id_dependencia").getAsInt());
                ps.setInt(12, parameter.get("id_grupo").getAsInt());
                ps.setInt(13, parameter.get("id_centro_acopio").getAsInt());
                if (ps.executeUpdate() > 0) {
                    respObject = Util.writreResponseString("OK");
                    response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            respObject = Util.writeResponseError("Actualizar detalle cronograma", ex.toString(), "/update-cronograma-detalle");
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
    public Response getDependenciaCronograma(int idCronograma) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "SELECT dependencia AS id_dependencia,dependencia \n"
                + "FROM cronograma_detalle cd \n"
                + "WHERE id_cronograma=?::int GROUP BY dependencia ";
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
            respObject = Util.writeResponseError("Cargar dependencia", ex.toString(), "/get-dependencia-cronograma");
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
    public Response getOrdenIngresoLocalNoFinalizadas(Usuario usuario) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "SELECT \n"
                + "                il.codigo_ingreso_local,\n"
                + "                il.fecha_creacion::timestamp(0) ,\n"
                + "                p.nit::varchar AS nit_proveedor,\n"
                + "                p.razon_social AS proveedor,\n"
                + "                ild.usuario_creador,\n"
                + "                pa.nombre_patio, \n"
                + "                oi.dependencia \n"
                + "                FROM ingreso_local il  \n"
                + "                INNER JOIN ingreso_local_detalle ild ON ild.codigo_ingreso_local =il.codigo_ingreso_local                 \n"
                + "                INNER JOIN orden_ingreso_detalle oid2 ON oid2.id=ild.id_detalle_orden_ingreso \n"
                + "                INNER JOIN patios pa on pa.id=oid2.patio_id\n"
                + "                INNER JOIN orden_ingreso oi ON oi.codigo_orden_ingreso=oid2.codigo_orden_ingreso\n"
                + "                INNER JOIN proveedor p ON p.nit =oi.nit_proveedor \n"
                + "                WHERE ild.procesado ='N'\n"
                + "                AND oid2.patio_id  IN (SELECT id_patio  FROM rel_usuarios_patios rup WHERE rup.usuario=?)\n"
                + "                GROUP BY 1,2,3,4,5,6,7";
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
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            respObject = Util.writeResponseError("Cargar Ordenes de Ingreso local", ex.toString(), "/get-ingreso-local-sin-finalizar");
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
    public Response getDetalleIngresoLocal(String cod_ingreso_local) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "SELECT ild.id_detalle_orden_ingreso AS id_detalle_ingreso_local,\n"
                + "		ild.codigo_ingreso_local, \n"
                + "		ild.codigo_ingreso_remoto,\n"
                + "		oi.codigo_orden_ingreso,\n"
                + "		ild.cantidad,\n"
                + "		id.cod_identificacion AS resolucion,\n"
                + "		p.razon_social AS proveedor,\n"
                + "		a.nombre_articulo,\n"
                + "		id.descripcion_articulo,\n"
                + "		ca.nombre_clase_articulo,\n"
                + "		id.placa,\n"
                + "		id.descripcion_articulo,\n"
                + "		ild.procesado,\n"
                + "		ild.fecha_creacion::timestamp(0) \n"
                + " FROM ingreso_local_detalle ild  \n"
                + " INNER JOIN orden_ingreso_detalle id ON id.id=ild.id_detalle_orden_ingreso\n"
                + " INNER JOIN orden_ingreso oi ON oi.codigo_orden_ingreso =id.codigo_orden_ingreso \n"
                + " INNER JOIN articulos a ON a.id =id.id_articulo \n"
                + " INNER JOIN clase_articulos ca ON ca.id =id.id_clase_articulos \n"
                + " INNER JOIN proveedor p ON p.nit =oi.nit_proveedor \n"
                + " WHERE ild.codigo_ingreso_local =?\n"
                + " GROUP BY 1,2,3,4,5,6,7,8,9,10,11,12,13,14";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, cod_ingreso_local);
                ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
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
    public Response getChecksDetalleIngresoLocal(int id_detalle_ingreso_local) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "SELECT aild.id AS id_documento, \n"
                + "		  		 aild.codigo_ingreso_local,\n"
                + "		  		 aild.codigo_ingreso_remoto,\n"
                + "		  		 id.codigo_orden_ingreso,\n"
                + "		  		 aild.id_detalle_orden_ingreso as id_detalle_ingreso_local,\n"
                + "		  		 aild.id_componentes_clase_articulos, \n"
                + "		  		 cca.nombre_componente, \n"
                + "		  		 aild.nombre_archivo,\n"
                + "		  		 aild.path_archivo,\n"
                + "		  		 aild.tiene_archivo,\n"
                + "		  		 aild.cantidad,\n"
                + "		  		 aild.procesado,\n"
                + "		  		 'N'::varchar as archivo_obligatorio,\n"
                + "		  		 coalesce (aird.cantidad,0) AS cantidad_ingreso_remoto,\n"
                + "		  		 coalesce (aird.path_archivo,'') AS path_archivo_ingreso_remoto,\n"
                + "		  		 coalesce (aild.descripcion,'') as descripcion,\n"
                + "		  		 coalesce (aild.cumple,'S')as cumple \n"
                + "		 FROM  archivos_ingreso_local_detalle aild \n"
                + "		 INNER JOIN orden_ingreso_detalle id ON id.id=aild.id_detalle_orden_ingreso \n"
                + "		 INNER JOIN rel_clase_componentes rc ON rc.id_componente_clase_articulo=aild.id_componentes_clase_articulos  AND rc.id_clase_articulo=id.id_clase_articulos \n"
                + "		 INNER JOIN componentes_clase_articulos cca ON cca.id= rc.id_componente_clase_articulo\n"
                + "		 left JOIN archivos_ingreso_remoto_detalle aird ON aird.id_detalle_orden_ingreso =aild.id_detalle_orden_ingreso AND aird.id_componentes_clase_articulos =aild.id_componentes_clase_articulos \n"
                + "		 WHERE aild.id_detalle_orden_ingreso =?\n"
                + "		 GROUP BY 1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setInt(1, id_detalle_ingreso_local);
                ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            respObject = Util.writeResponseError("Cargar checks detalle ingreso local", ex.toString(), "/get-checks-detalle-ingreso-local");
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
    public Response guardarArchivoIngresoLocal(String json, List<FormDataBodyPart> parts, Usuario usuario) {
        JsonObject parameter = Util.readJsonObjectParameter(json);
        Response response = null;
        JsonObject respObject;
        String ext;
        String mimeType;
        String nombreArchivo;
        InputStream inputStream;
        OutputStream outputStream;
        String ruta;

        if (parameter != null) {
            for (FormDataBodyPart fileInputStream : parts) {

                mimeType = fileInputStream.getMediaType().toString();
                inputStream = fileInputStream.getEntityAs(InputStream.class);

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
                        ext = "";
                        break;
                }

                String codigo_ingreso_local = parameter.get("codigo_ingreso_local").getAsString();

                ruta = UPLOAD_FILE_SERVER + codigo_ingreso_local;
                nombreArchivo = insertArchivoDetalleIngresoLocal(json, ext, usuario);

                if (!ext.equals("")) {

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
                        respObject = Util.writeResponseError("Guardar documentación", ex.toString(), "/save-documentacion_inicial");
                        response = Util.responseApi(Util.getJsonString(respObject), Response.Status.INTERNAL_SERVER_ERROR);
                    } catch (IOException ex) {
                        Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
                        respObject = Util.writeResponseError("Guardar documentación", ex.toString(), "/save-documentacion_inicial");
                        response = Util.responseApi(Util.getJsonString(respObject), Response.Status.INTERNAL_SERVER_ERROR);
                    }
                }
                respObject = Util.writreResponseString(codigo_ingreso_local);
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);

            }

        }
        return response;
    }

    public String insertArchivoDetalleIngresoLocal(String json, String ext, Usuario usuario) {
        String response = null;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "SELECT cargar_archivos_detalle_ingreso_local(?::json, ?::varchar, ?::varchar) as resp";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, json);
                ps.setString(2, usuario.getId_usuario());
                ps.setString(3, ext);
                rs = ps.executeQuery();
                if (rs.next()) {
                    response = rs.getString("resp");
                }
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

        return response;
    }

    @Override
    public Response getOrdenIngresoLocalFinalizadas(Usuario usuario) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "SELECT\n"
                + "    	il.codigo_ingreso_local,\n"
                + "    	oi.codigo_orden_ingreso,\n"
                + "    	ild.id_detalle_orden_ingreso,\n"
                + "    	oi.nit_proveedor,\n"
                + "    	p.razon_social AS proveedor,oi.dependencia,\n"
                + "    	id.cod_identificacion AS resolucion,	\n"
                + "    	a.nombre_articulo,\n"
                + "    	id.descripcion_articulo,\n"
                + "    	ild.cantidad,\n"
                + "    	ca.nombre_clase_articulo, \n"
                + "    	id.placa,\n"
                + "    	id.motor,\n"
                + "    	id.chasis,\n"
                + "    	id.serie,\n"
                + "    	id.marca,\n"
                + "    	id.carroceria,\n"
                + "    	id.linea,\n"
                + "    	id.capacidad,\n"
                + "    	id.peso_kg, \n"
                + "    	c.categoria,\n"
                + "    	c2.clasificacion,\n"
                + "    	id.modelo,\n"
                + "    	id.tipo_servicio,\n"
                + "    	um.nombre_unidad_medida \n"
                + "    FROM ingreso_local il \n"
                + "    INNER JOIN ingreso_local_detalle ild ON ild.codigo_ingreso_local=il.codigo_ingreso_local\n"
                + "    INNER JOIN orden_ingreso_detalle id ON id.id=ild.id_detalle_orden_ingreso \n"
                + "    INNER JOIN orden_ingreso oi ON oi.codigo_orden_ingreso=id.codigo_orden_ingreso \n"
                + "    INNER JOIN proveedor p ON p.nit=oi.nit_proveedor \n"
                + "    INNER JOIN articulos a ON a.id=id.id_articulo \n"
                + "    INNER JOIN clase_articulos ca ON ca.id=id.id_clase_articulos\n"
                + "    INNER JOIN categoria c ON c.id=id.id_categoria \n"
                + "    INNER JOIN clasificacion c2 ON c2.id=id.id_clasificacion \n"
                + "    INNER JOIN unidad_medida um ON um.id=id.unidad_medida \n"
                + "    WHERE ild.procesado ='S' and ild.desintegracion='N' \n"
                + "    AND id.patio_id  IN (SELECT id_patio  FROM rel_usuarios_patios rup WHERE rup.usuario=?)\n"
                + "    ORDER BY codigo_ingreso_local desc";
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
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            respObject = Util.writeResponseError("Cargar Ordenes de Ingreso local finalizadas", ex.toString(), "/get-ingreso-local-finalizadas");
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
    public Response finalizarItemIngresoLocal(int id_detalle_orden_ingreso, Usuario usuario) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "UPDATE ingreso_local_detalle SET procesado ='S',usuario_ultima_modificacion=?,fecha_ultima_modificacion =now() WHERE id_detalle_orden_ingreso =? ";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, usuario.getId_usuario());
                ps.setInt(2, id_detalle_orden_ingreso);
                if (ps.executeUpdate() > 0) {
                    respObject = Util.writreResponseString("OK");
                    response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            respObject = Util.writeResponseError("Actualizar estado proceso orden", ex.toString(), "/finalizar-item-ingreso-remoto");
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
    public Response saveDesintegracionPaso1(String json, List<FormDataBodyPart> parts, Usuario usuario) {

        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        query = "select guardar_desintegracion(?::json,?::varchar) as codigo_desintegracion";
        try {
            con = Util.conectarBD();
            if (con != null) {
                con.setAutoCommit(false);
                ps = con.prepareStatement(query);
                ps.setString(1, json);
                ps.setString(2, usuario.getId_usuario());
                System.out.println("USUARIO >>> "+ usuario.getId_usuario());
                System.out.println("DESINTEGRACION >>> "+ ps.toString());
                rs = ps.executeQuery();
                if (rs.next()) {
                    if (loadArchivoServer(rs.getString("codigo_desintegracion"), " ARCHIVO PASO 1", parts)) {
                        respObject = Util.writreResponseString("OK");
                        response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
                        con.commit();
                    }

                }
            }
        } catch (SQLException ex) {
            try {
                con.rollback();
            } catch (SQLException ex1) {
                Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex1);
            }
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            respObject = Util.writeResponseError("Guardar desintegracion paso 1", ex.toString(), "/save-desintegracion-paso1");
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

    private boolean loadArchivoServer(String codigo, String texto, List<FormDataBodyPart> parts) {

        String ext;
        String mimeType;
        InputStream inputStream;
        OutputStream outputStream;
        String ruta;

        for (FormDataBodyPart fileInputStream : parts) {

            mimeType = fileInputStream.getMediaType().toString();
            inputStream = fileInputStream.getEntityAs(InputStream.class);

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
                    ext = "";
                    break;
            }

            ruta = UPLOAD_FILE_SERVER + codigo;

            File f = new File(ruta);
            if (!f.exists()) {
                f.mkdirs();
            }

            ruta = ruta + "\\" + texto + ext;
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

                return true;
            } catch (FileNotFoundException ex) {
                Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;

    }

    @Override
    public Response getPasoDesintegracion(String paso, Usuario usuario) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "SELECT  \n"
                + "		d.id_detalle_orden_ingreso AS id_detalle_desintegracion,\n"
                + "		d.codigo_desintegracion, \n"
                + "		d.codigo_ingreso_local, \n"
                + "oi.codigo_orden_ingreso, \n"
                + "		d.tb, \n"
                + "		d.peso,\n"
                + "		d.porcentaje_descuento,\n"
                + "		d.total, \n"
                + "		d.fecha_certificado_sijin, \n"
                + "		d.certificado_sijin,\n"
                + "		d.fecha_certificado,\n"
                + "		d.numero_certificado,\n"
                + "		oi.nit_proveedor::varchar,\n"
                + "		p.razon_social AS proveedor,\n"
                + "		id.cod_identificacion AS resolucion,\n"
                + "		a.nombre_articulo,\n"
                + "		id.descripcion_articulo,\n"
                + "		id.cantidad,\n"
                + "		a.id as id_clase_articulo, ca.nombre_clase_articulo, \n"
                + "		id.placa,\n"
                + "		id.motor,\n"
                + "		id.chasis,\n"
                + "		id.serie,\n"
                + "		id.marca,\n"
                + "		id.carroceria,\n"
                + "		id.linea,\n"
                + "		id.capacidad,\n"
                + "		id.peso_kg, \n"
                + "		id.peso_bruto, \n"
                + "		c.categoria,\n"
                + "		c2.clasificacion,\n"
                + "		id.modelo,\n"
                + "		id.color,\n"
                + "		id.motivo,\n"
                + "		id.configuracion_ejes,\n"
                + "		case when id.nit_propietario is null or id.nit_propietario = '' then oi.nit_proveedor::varchar else id.nit_propietario end as nit_propietario,\n"
                + "		case when id.nit_propietario is null or id.nit_propietario = '' then p.razon_social::varchar else id.nombre_propietario end as nombre_propietario,\n"
                + "		id.tipo_servicio,\n"
                + "		um.nombre_unidad_medida,\n"
                + "		d.procesado, \n"
                + "		d.paso_actual,\n"
                + "		d.usuario_creador,\n"
                + "		d.fecha_creacion,\n"
                + "		p.razon_social,\n"
                + "		id.codigo_certificado,\n"
                + "		oi.dependencia\n"
                + "FROM public.desintegracion d\n"
                + "INNER JOIN orden_ingreso_detalle id ON id.id=d.id_detalle_orden_ingreso and id.estado ='' \n"
                + "INNER JOIN orden_ingreso oi ON oi.codigo_orden_ingreso =id.codigo_orden_ingreso and oi.estado ='' \n"
                + "INNER JOIN proveedor p ON p.nit=oi.nit_proveedor \n"
                + "INNER JOIN articulos a ON a.id=id.id_articulo \n"
                + "INNER JOIN clase_articulos ca ON ca.id=id.id_clase_articulos\n"
                + "INNER JOIN categoria c ON c.id=id.id_categoria \n"
                + "INNER JOIN clasificacion c2 ON c2.id=id.id_clasificacion \n"
                + "INNER JOIN unidad_medida um ON um.id=id.unidad_medida \n"
                + "WHERE d.procesado ='N' AND paso_actual =?\n"
                + "AND id.patio_id IN (SELECT id_patio  FROM rel_usuarios_patios rup WHERE rup.usuario=?) order by codigo_desintegracion desc ";

        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, paso);
                ps.setString(2, usuario.getId_usuario());
                System.out.println(ps.toString());
                ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            respObject = Util.writeResponseError("Cargar Ordenes de Ingreso local finalizadas", ex.toString(), "/get-ingreso-local-finalizadas");
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
    public Response saveDesintegracionPaso2(String json, Usuario usuario) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "SELECT insertar_desintegracion_detalle(?::json, ?::varchar) as resp";
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
            respObject = Util.writeResponseError("Insertar Detalle Desintegracion paso 2", ex.toString(), "/save-desintegracion-paso2");
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
    public Response saveDesintegracionPaso3(String json, List<FormDataBodyPart> parts, Usuario usuario) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        JsonObject parameter = Util.readJsonObjectParameter(json);

        query = "UPDATE public.desintegracion\n"
                + "SET fecha_certificado=?::date, numero_certificado=?::varchar, usuario_ultima_modificacion=?,paso_actual='FINALIZADO',procesado='S', fecha_ultima_modificacion=now() \n"
                + "WHERE codigo_desintegracion=? AND  codigo_ingreso_local=? AND id_detalle_orden_ingreso=?";
        try {
            con = Util.conectarBD();
            if (con != null) {
                con.setAutoCommit(false);
                ps = con.prepareStatement(query);
                ps.setString(1, parameter.get("fecha_certificado").getAsString());
                ps.setString(2, parameter.get("numero_certificado").getAsString());
                ps.setString(3, usuario.getId_usuario());
                ps.setString(4, parameter.get("codigo_desintegracion").getAsString());
                ps.setString(5, parameter.get("codigo_ingreso_local").getAsString());
                ps.setInt(6, parameter.get("id_detalle_orden_ingreso").getAsInt());
                if (ps.executeUpdate() > 0) {
                    if (loadArchivoServer(parameter.get("codigo_desintegracion").getAsString(), " ARCHIVO PASO 4", parts)) {
                        respObject = Util.writreResponseString("OK");
                        response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
                        con.commit();
                    }

                }
            }
        } catch (SQLException ex) {
            try {
                con.rollback();
            } catch (SQLException ex1) {
                Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex1);
            }
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            respObject = Util.writeResponseError("Guardar desintegracion paso 4", ex.toString(), "/save-desintegracion-paso4");
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
    public Response saveFileDesintegracion(String json, List<FormDataBodyPart> parts, Usuario usuario) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        JsonObject parameter = Util.readJsonObjectParameter(json);

        String ext = null;
        String mimeType;
        InputStream inputStream;
        int cont = 0;

        for (FormDataBodyPart fileInputStream : parts) {
            cont++;
            mimeType = fileInputStream.getMediaType().toString();
            inputStream = fileInputStream.getEntityAs(InputStream.class);

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
                    ext = "";
                    break;
            }

            query = "INSERT INTO public.archivos_desintegracion\n"
                    + "(codigo_desintegracion, id_detalle_orden_ingreso, nombre_archivo, path_archivo, cantidad, usuario_creador, fecha_creacion)\n"
                    + "VALUES(?::varchar,?::int,?::varchar,?::varchar,1,?::varchar,now());";
            
            String nameArhivo = parameter.get("descripcion").getAsString() + "_" + parameter.get("id_detalle_orden_ingreso").getAsInt() + "_" + cont;
            try {
                con = Util.conectarBD();
                if (con != null) {
                    con.setAutoCommit(false);
                    ps = con.prepareStatement(query);
                    ps.setString(1, parameter.get("codigo_desintegracion").getAsString());
                    ps.setInt(2, parameter.get("id_detalle_orden_ingreso").getAsInt());
                    ps.setString(3, nameArhivo + ext);
                    ps.setString(4, parameter.get("codigo_desintegracion").getAsString() + "\\" + nameArhivo + ext);
                    ps.setString(5, usuario.getId_usuario());
                    if (ps.executeUpdate() > 0) {
                        OutputStream outputStream;
                        String ruta;

                        ruta = UPLOAD_FILE_SERVER + parameter.get("codigo_desintegracion").getAsString();

                        File f = new File(ruta);
                        if (!f.exists()) {
                            f.mkdirs();
                        }

                        ruta = ruta + "\\" + nameArhivo + ext;

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
                        con.commit();

                    }
                }
            } catch (SQLException ex) {
                try {
                    con.rollback();
                } catch (SQLException ex1) {
                    Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex1);
                }
                Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
                respObject = Util.writeResponseError("Guardar desintegracion paso 3", ex.toString(), "/save-desintegracion-paso3");
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.INTERNAL_SERVER_ERROR);
            } finally {
                try {

                    Util.desconectarBd(con, rs, ps, null, this.getClass());
                } catch (SQLException ex) {
                    Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }        
        
        query = "update desintegracion set paso_actual ='PASO 3',usuario_ultima_modificacion =?,fecha_ultima_modificacion =now()\n" +
                "where codigo_desintegracion =?";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1,usuario.getId_usuario());
                ps.setString(2,parameter.get("codigo_desintegracion").getAsString());
                if (ps.executeUpdate() > 0) {
                        respObject = Util.writreResponseString("OK");
                        response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
                    }
            }
        } catch (SQLException ex) {
             Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
                respObject = Util.writeResponseError("Guardar desintegracion fisica", ex.toString(), "/save-desintegracion-paso3");
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
    public Response getDocumentosDesintegracion(int id_detalle_ingreso_remoto) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        String json = "{}";

        query = "SELECT codigo_desintegracion,\n"
                + "id_detalle_orden_ingreso,\n"
                + "nombre_archivo,\n"
                + "path_archivo,\n"
                + "cantidad,\n"
                + "usuario_creador,\n"
                + "fecha_creacion\n"
                + "FROM public.archivos_desintegracion\n"
                + "WHERE id_detalle_orden_ingreso =?;";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setInt(1, id_detalle_ingreso_remoto);
                ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            respObject = Util.writeResponseError("Cargar checks detalle ingreso remoto", ex.toString(), "/get-checks-detalle-ingreso-remoto");
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
    public Response getReporteOrdenIngreso() {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "SELECT codigo_orden_ingreso, \n"
                + "fecha,\n"
                + "m.nombre_municipio,\n"
                + "m.id as id_municipio, \n"
                + "documento_tercero AS resolucion,\n"
                + "fecha_resolucion, \n"
                + "lote,\n"
                + "p.nit::varchar AS nit_proveedor,\n"
                + "p.razon_social AS proveedor,\n"
                + "dependencia, \n"
                + "a.nit::varchar AS autorizado ,\n"
                + "a.nombre_completo as nombre_autorizado,\n"
                + "a2.nombre_autorizacion AS autorizacion,\n"
                + "a2.id as id_autorizacion,\n"
                + "m2.nombre_motivo AS motivo,\n"
                + "m2.id as id_motivo,\n"
                + "u.nombres||' '||u.apellidos AS responsable1,\n"
                + "u.usuario as id_responsable1,\n"
                + "u2.nombres||' '||u2.apellidos AS responsable2,\n"
                + "u2.usuario  as id_responsable2,\n"
                + "oi.usuario_creador,\n"
                + "oi.fecha_creacion::timestamp(0),\n"
                + "oi.usuario_ultima_modificacion, \n"
                + "oi.fecha_ultima_modificacion::timestamp(0),\n"
                + "oi.id_cronograma,\n"
                + "coalesce(oi.responsable_patio, '') as responsable_documental,\n"
                + "oi.procesado,\n"
                + "oi.valor_compra\n"
                + "FROM orden_ingreso oi \n"
                + "INNER JOIN municipio m ON m.id =oi.id_municipio \n"
                + "INNER JOIN proveedor p ON p.nit =oi.nit_proveedor \n"
                + "INNER JOIN autorizado a ON a.nit =oi.autorizado \n"
                + "INNER JOIN autorizacion a2 ON a2.id =oi.autorizacion\n"
                + "INNER JOIN motivo m2 ON m2.id =oi.motivo \n"
                + "INNER JOIN usuarios u ON u.usuario =oi.responsable1 \n"
                + "INNER JOIN usuarios u2 ON u2.usuario =oi.responsable2\n"
                + "WHERE oi.estado != 'A' \n"
                + "ORDER BY fecha_creacion desc";
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
            respObject = Util.writeResponseError("Cargar Ordenes de Ingreso", ex.toString(), "/get-reporte-orden-ingreso");
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
    public Response reporteDetalleOrdenIngreso(String codOrdenIngreso) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "SELECT id.id as id_detalle_orden_ingreso, \n"
                + "  		id.codigo_orden_ingreso,\n"
                + "		tc.razon_social AS tipo_codigo,\n"
                + "		tc.nit as id_tipo_codigo,\n"
                + "		cod_identificacion,\n"
                + "		item,\n"
                + "		a.nombre_articulo AS articulo,\n"
                + "		a.id as id_articulo, \n"
                + "		descripcion_articulo,\n"
                + "		um.nombre_unidad_medida AS unidad_medida,\n"
                + "		um.id as id_unidad_medida,\n"
                + "		id.cantidad, \n"
                + "		peso_kg, 		\n"
                + "		ca.nombre_clase_articulo AS clase_articulos,\n"
                + "		ca.id AS id_clase_articulos,\n"
                + "		placa, \n"
                + "		motor, \n"
                + "		chasis, \n"
                + "		serie,\n"
                + "		marca, \n"
                + "		carroceria,\n"
                + "		modelo,\n"
                + "		linea,\n"
                + "		capacidad,\n"
                + "		tipo_servicio,\n"
                + "		COALESCE(ird.codigo_ingreso_remoto,'-') AS codigo_ingreso_remoto,\n"
                + "		id.procesado,\n"
                + "		c.categoria,\n"
                + "		c.id as id_categoria,\n"
                + "		c2.clasificacion,\n"
                + "		c2.id  as id_clasificacion, patio_id, configuracion_ejes, id.color, id.nit_propietario, peso_bruto\n"
                + "FROM public.orden_ingreso_detalle id\n"
                + "INNER JOIN proveedor tc ON tc.nit =id.tipo_codigo \n"
                + "INNER JOIN articulos a ON a.id =id.id_articulo \n"
                + "INNER JOIN unidad_medida um ON um.id =id.unidad_medida \n"
                + "INNER JOIN clase_articulos ca ON ca.id =id.id_clase_articulos \n"
                + "LEFT JOIN ingreso_remoto_detalle ird ON ird.codigo_orden_ingreso =id.codigo_orden_ingreso AND ird.id_detalle_orden_ingreso=id.id \n"
                + "left join categoria c on c.id=id.id_categoria \n"
                + "left join clasificacion c2 on c2.id=id.id_clasificacion \n"
                + "WHERE id.codigo_orden_ingreso =? AND id.estado = ''";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, codOrdenIngreso);
                ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            respObject = Util.writeResponseError("Cargar detalle Ordenes de Ingreso", ex.toString(), "/reporte-detalle-orden-ingreso");
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
    public Response reporteOrdenIngresoRemoto() {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "SELECT ird.id_detalle_orden_ingreso AS id_detalle_ingreso_remoto,\n"
                + "		ird.codigo_ingreso_remoto,\n"
                + "		ird.codigo_orden_ingreso,\n"
                + "		ird.cantidad,\n"
                + "		id.cod_identificacion,\n"
                + "		p.razon_social AS proveedor,\n"
                + "		a.nombre_articulo,\n"
                + "		id.descripcion_articulo,\n"
                + "		ca.nombre_clase_articulo,\n"
                + "		id.placa,\n"
                + "		ird.procesado,\n"
                + "		id.placa,\n"
                + "		id.placa,\n"
                + "		id.motor,\n"
                + "		id.chasis,\n"
                + "		id.serie,\n"
                + "		id.marca,\n"
                + "		id.carroceria,\n"
                + "		id.linea,\n"
                + "		id.capacidad,\n"
                + "		id.peso_kg, \n"
                + "		c.categoria, \n"
                + "		c2.clasificacion, \n"
                + "		id.modelo,\n"
                + "		id.tipo_servicio,\n"
                + "		um.nombre_unidad_medida,\n"
                + "		ird.fecha_creacion::timestamp(0) \n"
                + " FROM ingreso_remoto_detalle ird  \n"
                + " INNER JOIN orden_ingreso_detalle id ON id.id=ird.id_detalle_orden_ingreso\n"
                + " INNER JOIN orden_ingreso oi ON oi.codigo_orden_ingreso =id.codigo_orden_ingreso \n"
                + " INNER JOIN articulos a ON a.id =id.id_articulo \n"
                + " INNER JOIN clase_articulos ca ON ca.id =id.id_clase_articulos \n"
                + " INNER JOIN proveedor p ON p.nit =oi.nit_proveedor \n"
                + " INNER JOIN unidad_medida um ON um.id=id.unidad_medida \n"
                + " INNER JOIN categoria c ON c.id=id.id_categoria  \n"
                + " INNER JOIN clasificacion c2 ON c2.id=id.id_clasificacion";
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
            respObject = Util.writeResponseError("Cargar Ordenes de Ingreso", ex.toString(), "/get-orden-ingreso");
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
    public Response getEstadoItem(String json) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        JsonObject parameter = Util.readJsonObjectParameter(json);
        String filtroCodigoOrden = "";
        String filtroFechas = "";

        if (!parameter.get("codigo_orden_ingreso").getAsString().equals("")) {
            filtroCodigoOrden = "where id.codigo_orden_ingreso = '" + parameter.get("codigo_orden_ingreso").getAsString() + "'";
        }

        if (!parameter.get("fecha_ini").getAsString().equals("")) {
            filtroFechas = "where date(oi.fecha_creacion) between '" + parameter.get("fecha_ini").getAsString() + "'::date and '" + parameter.get("fecha_fin").getAsString() + "'::date";
        }

        query = "SELECT id.id as id_detalle_orden_ingreso, \n"
                + "  		id.codigo_orden_ingreso,\n"
                + "		tc.razon_social AS tipo_codigo,oi.dependencia,\n"
                + "		tc.nit as id_tipo_codigo,\n"
                + "		cod_identificacion,\n"
                + "		item,\n"
                + "		a.nombre_articulo AS articulo,\n"
                + "		a.id as id_articulo, \n"
                + "		descripcion_articulo,\n"
                + "		um.nombre_unidad_medida AS unidad_medida,\n"
                + "		um.id as id_unidad_medida,\n"
                + "		id.cantidad, \n"
                + "		peso_kg, 		\n"
                + "		ca.nombre_clase_articulo AS clase_articulos,\n"
                + "		ca.id AS id_clase_articulos,\n"
                + "		id.placa, \n"
                + "		motor, \n"
                + "		chasis, \n"
                + "		serie,\n"
                + "		marca, \n"
                + "		carroceria,\n"
                + "		modelo,\n"
                + "		linea,\n"
                + "		capacidad,\n"
                + "		tipo_servicio,\n"
                + "		c.categoria,\n"
                + "		c2.clasificacion,\n"
                + "		ird.procesado AS ingreso_remoto,\n"
                + "		ird.codigo_ingreso_remoto,\n"
                + "		ird.traslado,\n"
                + "		ird.codigo_traslado,\n"
                + "		p.nombre_patio AS patio_destino,\n"
                + "		ild.procesado AS ingreso_local, \n"
                + "		ild.codigo_ingreso_local,\n"
                + "		ild.desintegracion,\n"
                + "		d.codigo_desintegracion,\n"
                + "		id.fecha_creacion::timestamp(0),\n"
                + "		id.fecha_ultima_modificacion::timestamp(0),\n"
                + "		d.paso_actual AS paso_desintegracion, id.codigo_certificado, id.fecha_certificado, id.serial\n"
                + "FROM orden_ingreso_detalle id \n"
                + "left join orden_ingreso oi on oi.codigo_orden_ingreso = id.codigo_orden_ingreso \n"
                + "LEFT JOIN ingreso_remoto_detalle ird ON id.id=ird.id_detalle_orden_ingreso \n"
                + "LEFT JOIN traslados t ON t.codigo_traslado=ird.codigo_traslado  \n"
                + "LEFT JOIN patios p ON p.id=t.id_patio_destino \n"
                + "LEFT JOIN ingreso_local_detalle ild ON ild.id_detalle_orden_ingreso=id.id \n"
                + "LEFT JOIN desintegracion d ON d.id_detalle_orden_ingreso =id.id\n"
                + "LEFT JOIN proveedor tc ON tc.nit =id.tipo_codigo \n"
                + "LEFT JOIN articulos a ON a.id =id.id_articulo \n"
                + "LEFT JOIN unidad_medida um ON um.id =id.unidad_medida \n"
                + "LEFT JOIN clase_articulos ca ON ca.id =id.id_clase_articulos \n"
                + "left join categoria c on c.id=id.id_categoria \n"
                + "left join clasificacion c2 on c2.id=id.id_clasificacion \n"
                + "#FECHAS #ORDEN and oi.estado ='' and id.estado ='' ORDER BY id.id";
        try {
            con = Util.conectarBD();
            if (con != null) {
                query = query.replaceAll("#FECHAS", filtroFechas).replaceAll("#ORDEN", filtroCodigoOrden);
                ps = con.prepareStatement(query);
                ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            respObject = Util.writeResponseError("Cargar detalle Ordenes de Ingreso", ex.toString(), "/get-detalle-orden-ingreso");
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
    public Response getUsuarios() {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        String json = "{}";

        query = "select json_build_object('usuarios', json_agg(row_to_json(p)::json))as json\n"
                + "                from (\n"
                + "                SELECT  estado,\n"
                + "                		identificacion,\n"
                + "                		nombres,\n"
                + "                		apellidos,\n"
                + "                		email,\n"
                + "                		telefono,\n"
                + "                		usuario,\n"
                + "                		usuario_creador,\n"
                + "                		fecha_creacion,\n"
                + "                		usuario_ultima_modificacion,\n"
                + "                		fecha_ultima_modificacion,\n"
                + "                		(\n"
                + "                      select array_to_json(array_agg(row_to_json(d)))\n"
                + "                      from (\n"
                + "                        		SELECT  p.id,\n"
                + "                                               p.nombre_patio \n"
                + "                				FROM patios p \n"
                + "                				INNER JOIN rel_usuarios_patios rup ON rup.id_patio=p.id \n"
                + "                				WHERE rup.usuario= u.usuario\n"
                + "                      ) d\n"
                + "                    ) as patios,\n"
                + "                    (\n"
                + "                      select array_to_json(array_agg(row_to_json(r)))\n"
                + "                      from (\n"
                + "                        		SELECT  opu.id,\n"
                + "                						opu.nombre_opcion \n"
                + "                				FROM opciones_perfil_usuario opu \n"
                + "                				INNER JOIN rel_opciones_perfiles rop  ON opu.id =rop.id_opcion \n"
                + "                				INNER JOIN rel_usuario_perfil rup ON rup.id_perfil =rop.id_perfil \n"
                + "                				WHERE rup.usuario =u.usuario ORDER BY opu.nombre_opcion\n"
                + "                      ) r\n"
                + "                    ) as opciones,\n"
                + "                    (\n"
                + "                      select array_to_json(array_agg(row_to_json(r)))\n"
                + "                      from (\n"
                + "                        		select cc.id , cc.nombre_caja \n"
                + "				                from rel_usuario_cajas uc\n"
                + "				                inner join configuracion_caja cc on cc.id =uc.configuracion_caja_id\n"
                + "				                where uc.usuario=u.usuario\n"
                + "                      ) r\n"
                + "                    ) as cajas\n"
                + "                FROM public.usuarios u ORDER BY nombres\n"
                + "                )p";
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
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            respObject = Util.writeResponseError("Cargar Ordenes de Ingreso", ex.toString(), "/get-orden-ingreso");
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
    public Response adminUsuario(String json, Usuario usuario, String opcion) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "SELECT administracion_usuario(?::json, ?::varchar,?::VARCHAR) as resp";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, json);
                ps.setString(2, usuario.getId_usuario());
                ps.setString(3, opcion);
                rs = ps.executeQuery();
                if (rs.next()) {
                    respObject = Util.writreResponseString(rs.getString("resp"));
                    response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
                }
            }
        } catch (SQLException ex) {
             String fullErrorMessage = ex.getLocalizedMessage();
            String[] parts = fullErrorMessage.split("ERROR: ");
            String customErrorMessage = parts.length > 1 ? parts[1].split("Where:")[0].trim() : "Error desconocido";

            respObject = Util.writeResponseError("Guardar usuario", customErrorMessage, "/save-usuario");
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
    public Response getProveedores() {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        String json = "{}";
        query = "select json_build_object('data', json_agg(row_to_json(d)::json)) as json\n"
                + "from (\n"
                + "	select *, (\n"
                + "		select array_to_json(array_agg(row_to_json(d))) from (\n"
                + "			select ddd.id, ddd.nombre_documento from documentos_desintegracion_documental ddd \n"
                + "			inner join rel_proveedor_documento rpd on rpd.id_documento = ddd.id\n"
                + "			where rpd.nit_proveedor = p.nit\n"
                + "		) d\n"
                + "	) as documentos from proveedor p order by p.razon_social \n"
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
            respObject = Util.writeResponseError("Cargar Combo", ex.toString(), "/get-combo-proveedor");
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
    public Response adminProveedor(String json, Usuario usuario, String opcion) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "SELECT administracion_proveedor(?::json, ?::varchar,?::VARCHAR) as resp";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, json);
                ps.setString(2, usuario.getId_usuario());
                ps.setString(3, opcion);
                rs = ps.executeQuery();
                if (rs.next()) {
                    respObject = Util.writreResponseString(rs.getString("resp"));
                    response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
                }
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Admin proveedor", ex.toString(), "/save-proveedor");
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
    public Response getComboMenuOpciones() {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        query = "SELECT id, nombre_opcion \n"
                + "FROM opciones_perfil_usuario opu \n"
                + "WHERE estado =''\n"
                + "ORDER BY nombre_opcion ";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                rs = ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar Combo", ex.toString(), "/get-combo-proveedor");
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
    public Response getOrdenIngresoNoProcesadas(String json, Usuario usuario) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "SELECT codigo_orden_ingreso, \n"
                + "fecha,\n"
                + "m.nombre_municipio,\n"
                + "m.id as id_municipio, \n"
                + "documento_tercero AS resolucion,\n"
                + "fecha_resolucion, \n"
                + "lote,\n"
                + "p.nit::varchar AS nit_proveedor,\n"
                + "p.razon_social AS proveedor,\n"
                + "dependencia, \n"
                + "a.nombre_completo AS autorizado ,\n"
                + "a.nit as nombre_autorizado,\n"
                + "a2.nombre_autorizacion AS autorizacion,\n"
                + "a2.id as id_autorizacion,\n"
                + "m2.nombre_motivo AS motivo,\n"
                + "m2.id as id_motivo,\n"
                + "u.nombres||' '||u.apellidos AS responsable1,\n"
                + "u.usuario as id_responsable1,\n"
                + "u2.nombres||' '||u2.apellidos AS responsable2,\n"
                + "u2.usuario  as id_responsable2,\n"
                + "oi.usuario_creador,\n"
                + "oi.fecha_creacion::timestamp(0),\n"
                + "oi.usuario_ultima_modificacion, \n"
                + "oi.fecha_ultima_modificacion::timestamp(0),\n"
                + "oi.id_cronograma,\n"
                + "oi.responsable_patio \n"
                + "FROM orden_ingreso oi \n"
                + "INNER JOIN municipio m ON m.id =oi.id_municipio \n"
                + "INNER JOIN proveedor p ON p.nit =oi.nit_proveedor \n"
                + "INNER JOIN autorizado a ON a.nit =oi.autorizado \n"
                + "INNER JOIN autorizacion a2 ON a2.id =oi.autorizacion\n"
                + "INNER JOIN motivo m2 ON m2.id =oi.motivo \n"
                + "INNER JOIN usuarios u ON u.usuario =oi.responsable1 \n"
                + "INNER JOIN usuarios u2 ON u2.usuario =oi.responsable2\n"
                + "WHERE oi.procesado ='N'  AND (oi.responsable_patio=? or ? IN ('MGARCIA','ADMIN')) \n"
                + "ORDER BY oi.fecha_creacion DESC";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, usuario.getId_usuario());
                ps.setString(2, usuario.getId_usuario());
                ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            respObject = Util.writeResponseError("Cargar Ordenes de Ingreso", ex.toString(), "/get-orden-ingreso");
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
    public Response getInventarioItems() {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        String json = "{}";

        query = "select json_build_object('inventario', json_agg(row_to_json(p)::json))as json\n"
                + "from (\n"
                + "SELECT \n"
                + "cca.nombre_componente, \n"
                + "sum(dd.cantidad) as cantidad,\n"
                + "(select array_to_json(array_agg(row_to_json(d)))\n"
                + "      from (\n"
                + "      		select \n"
                + "			sum(de.cantidad) as cantidad ,\n"
                + "			p.nombre_patio \n"
                + "			from desintegracion_detalle as de\n"
                + "			INNER JOIN traslado_detalle td  ON td.id_detalle_orden_ingreso  =de.id_detalle_orden_ingreso \n"
                + "			INNER JOIN traslados t ON t.codigo_traslado =td.codigo_traslado \n"
                + "			INNER JOIN patios p ON p.id =t.id_patio_destino \n"
                + "			WHERE de.id_componentes_clase_articulos =dd.id_componentes_clase_articulos \n"
                + "			group by p.nombre_patio \n"
                + "			order by p.nombre_patio\n"
                + "      ) d\n"
                + "    ) as detalle_patios\n"
                + "from desintegracion_detalle as dd\n"
                + "inner join componentes_clase_articulos cca on cca.id=dd.id_componentes_clase_articulos\n"
                + "--WHERE dd.id_componentes_clase_articulos =13\n"
                + "group by cca.nombre_componente,dd.id_componentes_clase_articulos\n"
                + "order by cca.nombre_componente\n"
                + ")p";
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
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            respObject = Util.writeResponseError("Cargar Ordenes de Ingreso", ex.toString(), "/get-orden-ingreso");
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
    public Response getComboMaterialesRecuperados() {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        query = "select id,\n"
                + "nombre_material,total_inventario ,codigo_inventario,\n"
                + "unidad \n"
                + "from materiales_recuperados mr";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                rs = ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar Combo", ex.toString(), "/get-combo-materiales-recuperados");
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
    public Response saveInventarioMaterialesRecuperados(String json, Usuario usuario) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "SELECT guardar_inventario_material_recuperado(?::json, ?::varchar) as resp";
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
            respObject = Util.writeResponseError("Guardar Inventario", ex.toString(), "/save-inventario_materiales-recuperados");
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
    public Response getInventarioItemsPatios() {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        String json = "{}";

        query = "select json_build_object('inventario', json_agg(row_to_json(p)::json))as json\n"
                + "from (\n"
                + "SELECT nombre_patio,\n"
                + "(select array_to_json(array_agg(row_to_json(d)))\n"
                + "      from (\n"
                + "      		select \n"
                + "				sum(de.cantidad) as cantidad ,\n"
                + "				cca.nombre_componente \n"
                + "				from desintegracion_detalle as de\n"
                + "				INNER JOIN traslado_detalle td  ON td.id_detalle_orden_ingreso  =de.id_detalle_orden_ingreso \n"
                + "				INNER JOIN traslados t ON t.codigo_traslado =td.codigo_traslado \n"
                + "				INNER JOIN componentes_clase_articulos cca ON cca.id =de.id_componentes_clase_articulos \n"
                + "				WHERE t.id_patio_destino =pt.id\n"
                + "				group by cca.nombre_componente\n"
                + "				order by cca.nombre_componente\n"
                + "      ) d\n"
                + "    ) as detalle_patios\n"
                + "FROM patios pt \n"
                + ")p";
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
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            respObject = Util.writeResponseError("Cargar Inventario", ex.toString(), "/get-inventario-items-patios");
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
    public Response guardarDespacho(String json, Usuario usuario) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "SELECT guardar_despacho(?::json, ?::varchar) as resp";
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
            respObject = Util.writeResponseError("Guardar despacho", ex.toString(), "/save-despacho");
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
    public Response getItemFiles(int idDetalleOrdenIngreso) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        String json = "{}";

        query = "select json_build_object('files', json_agg(row_to_json(p)::json))as json\n"
                + "from (\n"
                + "SELECT \n"
                + "	oid2.id AS id_detalle_orden_ingreso,\n"
                + "	oid2.codigo_orden_ingreso,\n"
                + "	oid2.cod_identificacion,\n"
                + "	oid2.descripcion_articulo,\n"
                + "	oid2.placa,\n"
                + "	a.nombre_articulo,\n"
                + "	ca.nombre_clase_articulo,\n"
                + "		(\n"
                + "      select array_to_json(array_agg(row_to_json(a)))\n"
                + "      from (\n"
                + "        		SELECT codigo_desintegracion AS codigo,\n"
                + "					nombre_archivo,\n"
                + "					path_archivo\n"
                + "					FROM public.archivos_desintegracion\n"
                + "					WHERE id_detalle_orden_ingreso =oid2.id\n"
                + "					AND path_archivo!=''\n"
                + "      ) a\n"
                + "    ) as archivos_desintegracion,\n"
                + "    (\n"
                + "    select array_to_json(array_agg(row_to_json(b)))\n"
                + "      from (\n"
                + "        		SELECT codigo_orden_ingreso AS codigo,\n"
                + "					nombre_archivo,\n"
                + "					path_archivo\n"
                + "					FROM archivos_desintegracion_inicial adi \n"
                + "					WHERE codigo_orden_ingreso = oid2.codigo_orden_ingreso\n"
                + "					AND path_archivo!=''\n"
                + "      ) b\n"
                + "    ) as archivos_desintegracion_inicial,\n"
                + "    (\n"
                + "    select array_to_json(array_agg(row_to_json(b)))\n"
                + "      from (\n"
                + "        		SELECT codigo_ingreso_local AS codigo,\n"
                + "					nombre_archivo,\n"
                + "					path_archivo,\n"
                + "					cantidad FROM archivos_ingreso_local_detalle aild \n"
                + "					WHERE id_detalle_orden_ingreso  =oid2.id\n"
                + "					AND path_archivo!=''\n"
                + "      ) b\n"
                + "    ) as archivos_ingreso_local,\n"
                + "    (\n"
                + "    select array_to_json(array_agg(row_to_json(b)))\n"
                + "      from (\n"
                + "        		SELECT codigo_ingreso_remoto AS codigo,\n"
                + "					nombre_archivo,\n"
                + "					path_archivo,\n"
                + "					cantidad FROM archivos_ingreso_remoto_detalle aird \n"
                + "					WHERE id_detalle_orden_ingreso  =oid2.id\n"
                + "					AND path_archivo!=''\n"
                + "      ) b\n"
                + "    ) as archivos_ingreso_remoto,\n"
                + "    (\n"
                + "    select array_to_json(array_agg(row_to_json(b)))\n"
                + "      from (\n"
                + "        		select \n"
                + "				codigo_desintegracion as codigo,\n"
                + "				'ARCHIVO PASO 1.pdf' as nombre_archivo,\n"
                + "				codigo_desintegracion||'/ ARCHIVO PASO 1.pdf' as path_archivo\n"
                + "				from desintegracion\n"
                + "				where id_detalle_orden_ingreso=oid2.id\n"
                + "      ) b\n"
                + "    ) as archivo_tiquete_bascula,\n"
                + "    (\n"
                + "    select array_to_json(array_agg(row_to_json(b)))\n"
                + "      from (\n"
                + "        		select \n"
                + "				codigo_desintegracion as codigo,\n"
                + "				'ARCHIVO PASO 4.pdf' as nombre_archivo,\n"
                + "				codigo_desintegracion||'/ ARCHIVO PASO 4.pdf' as path_archivo\n"
                + "				from desintegracion\n"
                + "				where id_detalle_orden_ingreso=oid2.id\n"
                + "      ) b\n"
                + "    ) as archivo_certificacion_final\n"
                + "FROM orden_ingreso_detalle oid2 \n"
                + "INNER JOIN articulos a ON a.id =oid2.id_articulo \n"
                + "INNER JOIN clase_articulos ca ON ca.id =oid2.id_clase_articulos\n"
                + "WHERE oid2.id =?\n"
                + ")p";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setInt(1, idDetalleOrdenIngreso);
                rs = ps.executeQuery();

                if (rs.next()) {
                    json = rs.getString("json");
                }
                respObject = Util.getObjectJson(json);
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            respObject = Util.writeResponseError("Cargar docuemntos item", ex.toString(), "/get-item-files");
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
    public Response guardarBitacora(String json, List<FormDataBodyPart> parts, Usuario usuario) {
        JsonObject parameter = Util.readJsonObjectParameter(json);
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        String nombreArchivo = "Bitacora_" + parameter.get("codigo_orden_ingreso").getAsString() + "_" + System.currentTimeMillis();
        if (parameter != null) {
            for (FormDataBodyPart fileInputStream : parts) {
                loadArchivoServer(parameter.get("codigo_orden_ingreso").getAsString(), nombreArchivo, fileInputStream, usuario, UPLOAD_FILE_SERVER);
            }

            query = "INSERT INTO public.bitacora\n"
                    + "(codigo_orden_ingreso, concepto, valor, descripcion, usuario_creador, fecha_creacion)\n"
                    + "VALUES(?,?,?,?,?,now());";
            try {
                con = Util.conectarBD();
                if (con != null) {
                    ps = con.prepareStatement(query);
                    ps.setString(1, parameter.get("codigo_orden_ingreso").getAsString());
                    ps.setString(2, parameter.get("concepto").getAsString());
                    ps.setDouble(3, parameter.get("valor").getAsDouble());
                    ps.setString(4, parameter.get("descripcion").getAsString());
                    ps.setString(5, usuario.getId_usuario());
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
        return response;
    }

    public void loadArchivoServer(String codigo, String nombreArchivo, FormDataBodyPart fileInputStream, Usuario usuario, String pathServer) {

        String ext;
        String mimeType;
        InputStream inputStream;
        OutputStream outputStream;
        String ruta;

        mimeType = fileInputStream.getMediaType().toString();
        inputStream = fileInputStream.getEntityAs(InputStream.class);

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
                ext = "";
                break;
        }

        ruta = pathServer + codigo;

        File f = new File(ruta);
        if (!f.exists()) {
            f.mkdirs();
        }

        ruta = ruta + "\\" + nombreArchivo + ext;

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

            insertDocumentoBitacora(codigo, nombreArchivo, ext, usuario);

        } catch (FileNotFoundException ex) {
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void insertDocumentoBitacora(String codigo, String nombreArchivo, String ext, Usuario usuario) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        String path = codigo + "\\" + nombreArchivo + ext;

        query = "INSERT INTO public.archivos_bitacora\n"
                + "(codigo_orden_ingreso, nombre_archivo, path_archivo, usuario_creador, fecha_creacion)\n"
                + "VALUES(?,?,?,?,now());";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, codigo);
                ps.setString(2, nombreArchivo);
                ps.setString(3, path);
                ps.setString(4, usuario.getId_usuario());
                ps.executeUpdate();
            }
        } catch (SQLException ex) {
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            respObject = Util.writeResponseError("Insertar documentos db", ex.toString(), "/update-cronograma-detalle");
            response = Util.responseApi(Util.getJsonString(respObject), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            try {
                Util.desconectarBd(con, rs, ps, null, this.getClass());
            } catch (SQLException ex) {
                Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    @Override
    public Response getBitacora(String codigo_orden_ingreso) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        query = "SELECT id, codigo_orden_ingreso, concepto, valor, descripcion, usuario_creador, fecha_creacion,(select CASE WHEN count(id)>0 THEN 'S' ELSE 'N' END  FROM archivos_bitacora WHERE id=b.id) AS tiene_imagen\n"
                + "FROM public.bitacora b\n"
                + "WHERE codigo_orden_ingreso=?;";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, codigo_orden_ingreso);
                rs = ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar bitacora", ex.toString(), "/get-bitacora");
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
    public Response getBitacoraFile(int idBitacora) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "select id,codigo_orden_ingreso,nombre_archivo,path_archivo from archivos_bitacora WHERE id=?;";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setInt(1, idBitacora);
                ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            respObject = Util.writeResponseError("Cargar documentos bitacora", ex.toString(), "/get-bitacora-file");
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
    public Response getOrdenIngresoDd(String json, Usuario usuario) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "SELECT codigo_orden_ingreso, \n"
                + "fecha,\n"
                + "m.nombre_municipio,\n"
                + "m.id as id_municipio, \n"
                + "documento_tercero AS resolucion,\n"
                + "fecha_resolucion, \n"
                + "lote,\n"
                + "p.nit::varchar AS nit_proveedor,\n"
                + "p.razon_social AS proveedor,\n"
                + "dependencia, \n"
                + "a.nombre_completo AS autorizado ,\n"
                + "a.nit as nombre_autorizado,\n"
                + "a2.nombre_autorizacion AS autorizacion,\n"
                + "a2.id as id_autorizacion,\n"
                + "m2.nombre_motivo AS motivo,\n"
                + "m2.id as id_motivo,\n"
                + "u.nombres||' '||u.apellidos AS responsable1,\n"
                + "u.usuario as id_responsable1,\n"
                + "u2.nombres||' '||u2.apellidos AS responsable2,\n"
                + "u2.usuario  as id_responsable2,\n"
                + "oi.usuario_creador,\n"
                + "oi.fecha_creacion::timestamp(0),\n"
                + "oi.usuario_ultima_modificacion, \n"
                + "oi.fecha_ultima_modificacion::timestamp(0),\n"
                + "oi.id_cronograma,\n"
                + "oi.responsable_patio,valor_compra \n"
                + "FROM orden_ingreso oi \n"
                + "INNER JOIN municipio m ON m.id =oi.id_municipio \n"
                + "INNER JOIN proveedor p ON p.nit =oi.nit_proveedor \n"
                + "INNER JOIN autorizado a ON a.nit =oi.autorizado \n"
                + "INNER JOIN autorizacion a2 ON a2.id =oi.autorizacion\n"
                + "INNER JOIN motivo m2 ON m2.id =oi.motivo \n"
                + "INNER JOIN usuarios u ON u.usuario =oi.responsable1 \n"
                + "INNER JOIN usuarios u2 ON u2.usuario =oi.responsable2\n"
                + "WHERE oi.responsable_patio=? \n"
                + "ORDER BY oi.fecha_creacion DESC";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, usuario.getId_usuario());
//                ps.setString(2, usuario.getId_usuario());
                ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            respObject = Util.writeResponseError("Cargar Ordenes de Ingreso", ex.toString(), "/get-orden-ingreso");
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
    public Response guardarCaja(String json, Usuario usuario) {
        Response response = null;
        JsonObject respObject = null;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "SELECT public.retanquear_caja(?::JSON,?) as resp";
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
            respObject = Util.writeResponseError("Guardar caja", ex.toString(), "/save-caja");
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
    public Response guardarGastosCaja(String json, Usuario usuario) {
        Response response = null;
        JsonObject respObject = null;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "SELECT row_to_json(t) as json FROM ( SELECT base_caja, saldo_caja,enviar_email,upper(nombre_caja) as nombre_caja,id_gasto,status "
                + "FROM public.guardar_gastos_caja(?::json,?)"
                + "AS(base_caja NUMERIC,saldo_caja NUMERIC, enviar_email boolean, nombre_caja varchar, id_gasto int, status varchar) ) t;";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, json);
                ps.setString(2, usuario.getId_usuario());
                rs = ps.executeQuery();
                if (rs.next()) {

                    json = rs.getString("json");
                    JsonObject parameter = Util.readJsonObjectParameter(json);

                    if (parameter.get("enviar_email").getAsBoolean()) {
                        Util.sendEmail(parameter.get("nombre_caja").getAsString(), parameter.get("base_caja").getAsDouble(), parameter.get("saldo_caja").getAsDouble());
                    }
                }
                respObject = Util.getObjectJson(json);
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Guardar gastos caja", ex.getMessage(), "/save-gastos-caja");
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
    public Response getGastosCaja(Usuario usuario, String json) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        JsonObject parameter = Util.readJsonObjectParameter(json);

        query = "select upper(p.empresa) as empresa,\n"
                + "        p.tipo_documento,\n"
                + "        p.prefijo,\n"
                + "        p.consecutivo ,\n"
                + "        to_char(fecha::date,'dd/MM/yyyy') as fecha,\n"
                + "        p.tercero_interno ,\n"
                + "        p.tercero_externo ,\n"
                + "        upper(p.concepto) as concepto,\n"
                + "        '' as verificado,\n"
                + "		'' as anulado,\n"
                + "		'' as sucursal,\n"
                + "		'' as clasificación,\n"
                + "		'' as personalizado_1,\n"
                + "		'' as personalizado_2,\n"
                + "		'' as personalizado_3,\n"
                + "		'' as personalizado_4,\n"
                + "		'' as personalizado_5,\n"
                + "		'' as personalizado_6,\n"
                + "		'' as personalizado_7,\n"
                + "		'' as personalizado_8,\n"
                + "		'' as personalizado_9,\n"
                + "		'' as personalizado_10,\n"
                + "		'' as personalizado_11,\n"
                + "		'' as personalizado_12,\n"
                + "		'' as personalizado_13,\n"
                + "		'' as personalizado_14,\n"
                + "		'' as personalizado_15,\n"
                + "        cc.cuenta as cuenta_contable ,\n"
                + "        upper(gc.descripcion) as descripcion,\n"
                + "        gc.nit_tercero as tercero,        \n"
                + "        '' as cheque,\n"
                + "        gc.valor as debito,\n"
                + "        0 as credito,\n"
                + "        to_char(fecha::date,'dd/MM/yyyy') as fecha_vencimiento,\n"
                + "        p.centro_costo,'' as activo_fijo,\n"
                + "		'' as tipo_base,\n"
                + "		'' as porcentaje_retención,\n"
                + "		'' as baseretención,\n"
                + "		'' as pagoretención,\n"
                + "		'' as ivaalcosto,\n"
                + "		'' as sucursal,\n"
                + "		'' as importación,\n"
                + "		'' as caja_menor,\n"
                + "		'' as excluir_niif,\n"
                + "		'' as impoconsumocosto,\n"
                + "		'' as no_deducible,\n"
                + "		'' as codigo_centro_costos\n"
                + "        from gastos_caja gc \n"
                + "        inner join configuracion_caja p on p.id=gc.configuracion_caja_id\n"
                + "        left join cuentas_caja cc on cc.id =gc.id_cuenta \n"
                + "        where  \n"
                //+ "        fecha::DATE between ?::DATE and ?::DATE\n"
                + "        gc.configuracion_caja_id=? AND finalizado='S' and gc.caja_id = ? order by gc.id";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setInt(1, parameter.get("configuracion_caja_id").getAsInt());
                ps.setInt(2, parameter.get("caja_id").getAsInt());
                //ps.setInt(3, parameter.get("configuracion_caja_id").getAsInt());
                ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            respObject = Util.writeResponseError("Cargar gastos de caja", ex.toString(), "/get-gastos-caja");
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
    public Response getIngresoRemotoPendientes() {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "SELECT codigo_orden_ingreso, \n"
                + "fecha,\n"
                + "m.nombre_municipio,\n"
                + "documento_tercero AS resolucion,\n"
                + "fecha_resolucion, \n"
                + "lote,\n"
                + "p.nit::varchar AS nit_proveedor,\n"
                + "p.razon_social AS proveedor,\n"
                + "dependencia, a.nombre_completo AS autorizado ,\n"
                + "a2.nombre_autorizacion AS autorizacion,\n"
                + "m2.nombre_motivo AS motivo,\n"
                + "u.nombres||' '||u.apellidos AS responsable1,\n"
                + "u2.nombres||' '||u2.apellidos AS responsable2,\n"
                + "oi.usuario_creador,\n"
                + "oi.fecha_creacion::timestamp(0),\n"
                + "oi.usuario_ultima_modificacion\n"
                + "FROM orden_ingreso oi \n"
                + "INNER JOIN municipio m ON m.id =oi.id_municipio \n"
                + "INNER JOIN proveedor p ON p.nit =oi.nit_proveedor \n"
                + "INNER JOIN autorizado a ON a.nit =oi.autorizado \n"
                + "INNER JOIN autorizacion a2 ON a2.id =oi.autorizacion\n"
                + "INNER JOIN motivo m2 ON m2.id =oi.motivo \n"
                + "INNER JOIN usuarios u ON u.usuario =oi.responsable1 \n"
                + "INNER JOIN usuarios u2 ON u2.usuario =oi.responsable2\n"
                + "WHERE oi.procesado ='S'  AND 'N' IN (SELECT procesado FROM orden_ingreso_detalle  WHERE codigo_orden_ingreso =oi.codigo_orden_ingreso AND estado!='A')\n"
                + "ORDER BY oi.fecha_creacion DESC";
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
            respObject = Util.writeResponseError("Cargar ingreso remoto pendientes", ex.toString(), "/get-ingreso-remoto-pendientes");
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
    public Response getIngresoRemotoProceso() {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "SELECT \n"
                + "oi.codigo_orden_ingreso, \n"
                + "oi.fecha,\n"
                + "m.nombre_municipio,\n"
                + "documento_tercero AS resolucion,\n"
                + "fecha_resolucion, \n"
                + "lote,\n"
                + "p.razon_social AS proveedor,\n"
                + "dependencia,\n"
                + "u.nombres||' '||u.apellidos AS responsable1,\n"
                + "u2.nombres||' '||u2.apellidos AS responsable2,\n"
                + "oi.usuario_creador,\n"
                + "oi.fecha_creacion::timestamp(0),\n"
                + "id.id as id_detalle_orden_ingreso, \n"
                + "		cod_identificacion,\n"
                + "		item,\n"
                + "		a.nombre_articulo AS articulo, \n"
                + "		descripcion_articulo,\n"
                + "		id.cantidad, \n"
                + "		peso_kg, 		\n"
                + "		ca.nombre_clase_articulo AS clase_articulos,\n"
                + "		id.placa, \n"
                + "		motor, \n"
                + "		chasis, \n"
                + "		serie,\n"
                + "		marca, \n"
                + "		carroceria,\n"
                + "		modelo,\n"
                + "		linea,\n"
                + "		capacidad,\n"
                + "		tipo_servicio,\n"
                + "		COALESCE(ird.codigo_ingreso_remoto,'-') AS codigo_ingreso_remoto,\n"
                + "		id.procesado,\n"
                + "		c.categoria,\n"
                + "		c2.clasificacion\n"
                + "FROM ingreso_remoto ir \n"
                + "INNER JOIN ingreso_remoto_detalle ird ON ird.codigo_ingreso_remoto =ir.codigo_ingreso_remoto \n"
                + "INNER JOIN orden_ingreso_detalle id ON id.id=ird.id_detalle_orden_ingreso \n"
                + "INNER JOIN orden_ingreso oi ON oi.codigo_orden_ingreso=ird.codigo_orden_ingreso \n"
                + "INNER JOIN articulos a ON a.id =id.id_articulo \n"
                + "INNER JOIN clase_articulos ca ON ca.id =id.id_clase_articulos \n"
                + "INNER JOIN municipio m ON m.id =oi.id_municipio \n"
                + "INNER JOIN proveedor p ON p.nit =oi.nit_proveedor \n"
                + "INNER JOIN usuarios u ON u.usuario =oi.responsable1 \n"
                + "INNER JOIN usuarios u2 ON u2.usuario =oi.responsable2\n"
                + "INNER join categoria c on c.id=id.id_categoria \n"
                + "INNER join clasificacion c2 on c2.id=id.id_clasificacion \n"
                + "--INNER JOIN traslado_detalle td ON td.id_detalle_orden_ingreso =id.id \n"
                + "--INNER JOIN traslados t ON t.codigo_traslado =td.codigo_traslado \n"
                + "--INNER JOIN patios pa ON pa.id =t.id_patio_destino \n"
                + "WHERE ir.procesado ='N'";
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
            respObject = Util.writeResponseError("Cargar ingreso remoto en proceso", ex.toString(), "/get-ingreso-remoto-proceso");
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
    public Response getIngresoLocalPendientes() {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "SELECT t.id,\n"
                + "	codigo_traslado,\n"
                + "	entidad, \n"
                + "	fecha,\n"
                + "	conductor,\n"
                + "	cedula_conductor,\n"
                + "	placa,\n"
                + "	empresa,\n"
                + "	origen, \n"
                + "	p.nombre_patio, \n"
                + "	id_patio_destino,\n"
                + "	descripcion,\n"
                + "	t.usuario_creador,\n"
                + "	t.fecha_creacion \n"
                + "FROM public.traslados t\n"
                + "inner join patios p on p.id=t.id_patio_destino \n"
                + "WHERE t.procesado='N' \n"
                + "AND 'N' IN (SELECT procesado FROM traslado_detalle td  WHERE codigo_traslado =t.codigo_traslado AND estado !='A')";
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
            respObject = Util.writeResponseError("Cargar ingreso local pendientes", ex.toString(), "/get-ingreso-local-pendientes");
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
    public Response getTrasladosPendientes() {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "SELECT \n"
                + "oi.codigo_orden_ingreso, \n"
                + "oi.fecha,\n"
                + "m.nombre_municipio,\n"
                + "documento_tercero AS resolucion,\n"
                + "fecha_resolucion, \n"
                + "lote,\n"
                + "p.razon_social AS proveedor,\n"
                + "dependencia,\n"
                + "u.nombres||' '||u.apellidos AS responsable1,\n"
                + "u2.nombres||' '||u2.apellidos AS responsable2,\n"
                + "oi.usuario_creador,\n"
                + "oi.fecha_creacion::timestamp(0),\n"
                + "id.id as id_detalle_orden_ingreso, \n"
                + "		cod_identificacion,\n"
                + "		item,\n"
                + "		a.nombre_articulo AS articulo, \n"
                + "		descripcion_articulo,\n"
                + "		id.cantidad, \n"
                + "		peso_kg, 		\n"
                + "		ca.nombre_clase_articulo AS clase_articulos,\n"
                + "		id.placa, \n"
                + "		motor, \n"
                + "		chasis, \n"
                + "		serie,\n"
                + "		marca, \n"
                + "		carroceria,\n"
                + "		modelo,\n"
                + "		linea,\n"
                + "		capacidad,\n"
                + "		tipo_servicio,\n"
                + "		COALESCE(ird.codigo_ingreso_remoto,'-') AS codigo_ingreso_remoto,\n"
                + "		id.procesado,\n"
                + "		c.categoria,\n"
                + "		c2.clasificacion\n"
                + "FROM ingreso_remoto ir \n"
                + "INNER JOIN ingreso_remoto_detalle ird ON ird.codigo_ingreso_remoto =ir.codigo_ingreso_remoto \n"
                + "INNER JOIN orden_ingreso_detalle id ON id.id=ird.id_detalle_orden_ingreso \n"
                + "INNER JOIN orden_ingreso oi ON oi.codigo_orden_ingreso=ird.codigo_orden_ingreso \n"
                + "INNER JOIN articulos a ON a.id =id.id_articulo \n"
                + "INNER JOIN clase_articulos ca ON ca.id =id.id_clase_articulos \n"
                + "INNER JOIN municipio m ON m.id =oi.id_municipio \n"
                + "INNER JOIN proveedor p ON p.nit =oi.nit_proveedor \n"
                + "INNER JOIN usuarios u ON u.usuario =oi.responsable1 \n"
                + "INNER JOIN usuarios u2 ON u2.usuario =oi.responsable2\n"
                + "INNER join categoria c on c.id=id.id_categoria \n"
                + "INNER join clasificacion c2 on c2.id=id.id_clasificacion \n"
                + "--INNER JOIN traslado_detalle td ON td.id_detalle_orden_ingreso =id.id \n"
                + "--INNER JOIN traslados t ON t.codigo_traslado =td.codigo_traslado \n"
                + "--INNER JOIN patios pa ON pa.id =t.id_patio_destino \n"
                + "WHERE ir.procesado ='S'";
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
            respObject = Util.writeResponseError("Cargar traslados pendientes", ex.toString(), "/get-traslados-pendientes");
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
    public Response getDesintegracionDocumentalPendientes(String json, Usuario usuario) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "SELECT codigo_orden_ingreso, \n"
                + "fecha,\n"
                + "m.nombre_municipio,\n"
                + "m.id as id_municipio, \n"
                + "documento_tercero AS resolucion,\n"
                + "fecha_resolucion, \n"
                + "lote,\n"
                + "p.nit::varchar AS nit_proveedor,\n"
                + "p.razon_social AS proveedor,\n"
                + "dependencia, \n"
                + "a.nombre_completo AS autorizado ,\n"
                + "a.nit as nombre_autorizado,\n"
                + "a2.nombre_autorizacion AS autorizacion,\n"
                + "a2.id as id_autorizacion,\n"
                + "m2.nombre_motivo AS motivo,\n"
                + "m2.id as id_motivo,\n"
                + "u.nombres||' '||u.apellidos AS responsable1,\n"
                + "u.usuario as id_responsable1,\n"
                + "u2.nombres||' '||u2.apellidos AS responsable2,\n"
                + "u2.usuario  as id_responsable2,\n"
                + "oi.usuario_creador,\n"
                + "oi.fecha_creacion::timestamp(0),\n"
                + "oi.usuario_ultima_modificacion, \n"
                + "oi.fecha_ultima_modificacion::timestamp(0),\n"
                + "oi.id_cronograma,\n"
                + "oi.responsable_patio \n"
                + "FROM orden_ingreso oi \n"
                + "INNER JOIN municipio m ON m.id =oi.id_municipio \n"
                + "INNER JOIN proveedor p ON p.nit =oi.nit_proveedor \n"
                + "INNER JOIN autorizado a ON a.nit =oi.autorizado \n"
                + "INNER JOIN autorizacion a2 ON a2.id =oi.autorizacion\n"
                + "INNER JOIN motivo m2 ON m2.id =oi.motivo \n"
                + "INNER JOIN usuarios u ON u.usuario =oi.responsable1 \n"
                + "INNER JOIN usuarios u2 ON u2.usuario =oi.responsable2\n"
                + "WHERE  oi.responsable_patio=? and oi.estado='' \n"
                + "ORDER BY oi.fecha_creacion DESC";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, usuario.getId_usuario());
//                ps.setString(2, usuario.getId_usuario());
                ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            respObject = Util.writeResponseError("Cargar desintegracion documental pendientes", ex.toString(), "/get-desintegracion-documental-pendientes");
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
    public Response getGastosCajaAdmin(Usuario usuario) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "select gc.id,\n"
                + "gc.estado,\n"
                + "gc.fecha,\n"
                + "p.nombre_patio as patio,\n"
                + "gc.descripcion,\n"
                + "gc.valor,\n"
                + "gc.tercero,\n"
                + "gc.nit_tercero,\n"
                + "gc.factura,\n"
                + "gc.usuario_creador,\n"
                + "gc.fecha_creacion\n"
                + "from gastos_caja gc \n"
                + "inner join patios p on p.id=gc.patio\n"
                + "inner join rel_usuarios_patios up on up.id_patio=gc.patio";
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
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            respObject = Util.writeResponseError("Cargar gastos de caja", ex.toString(), "/get-gastos-caja");
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
    public Response guardarClaseArticulo(String json, Usuario usuario) {
        Response response = null;
        JsonObject respObject = null;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "SELECT public.crear_clase_articulo(?::JSON,?) as resp";
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
            respObject = Util.writeResponseError("Guardar clase articulo", ex.toString(), "/save-clase-articulo");
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
    public Response guardarComponente(String json, Usuario usuario) {
        Response response = null;
        JsonObject respObject = null;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "SELECT public.crear_componentes(?::JSON,?) as resp";
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
            respObject = Util.writeResponseError("Guardar componenres", ex.toString(), "/save-componentes");
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
    public Response guardarRelClaseComponentes(String json, Usuario usuario) {
        Response response = null;
        JsonObject respObject = null;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "SELECT public.crear_rel_claseart_componente(?::JSON,?) as resp";
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
            respObject = Util.writeResponseError("Guardar relacion de clase de articulo y componentes", ex.toString(), "/save-rel-clase-componetes");
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
    public Response getComponentes(int idClase) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "SELECT * FROM componentes_clase_articulos cca \n"
                + "WHERE id NOT IN (SELECT id_componente_clase_articulo  FROM rel_clase_componentes rcc WHERE id_clase_articulo = ?);";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setInt(1, idClase);
                ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            respObject = Util.writeResponseError("Cargar componentes no asignados a una clase", ex.toString(), "/get-componentes");
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
    public Response getComponentesAsignados(int idClase) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "select cca.id, cca.nombre_componente from \n"
                + "rel_clase_componentes as rcc \n"
                + "inner join componentes_clase_articulos cca on cca.id = rcc.id_componente_clase_articulo \n"
                + "where rcc.id_clase_articulo = ?";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setInt(1, idClase);
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
    public Response getZipArchivos(int idOrden) {
        HttpServletRequest request;
        HttpServletResponse response = null;
        // Ruta de la carpeta que deseas comprimir
        String carpetaPath = "D:\\web";

        // Nombre para el archivo ZIP resultante
        String archivoZipNombre = "archivo.zip";

        // Configura el tipo de contenido y los encabezados de respuesta
        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "attachment; filename=" + archivoZipNombre);

        // Crea un flujo de salida para el archivo ZIP
        try (ZipOutputStream zipOut = new ZipOutputStream(response.getOutputStream())) {
            File carpeta = new File(carpetaPath);
            comprimirCarpeta(carpeta, carpeta.getName(), zipOut);
        } catch (IOException ex) {
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    private void comprimirCarpeta(File carpeta, String nombreCarpeta, ZipOutputStream zipOut) throws IOException {
        File[] archivos = carpeta.listFiles();
        byte[] buffer = new byte[1024];

        for (File archivo : archivos) {
            if (archivo.isFile()) {
                FileInputStream fis = new FileInputStream(archivo);
                zipOut.putNextEntry(new ZipEntry(nombreCarpeta + File.separator + archivo.getName()));

                int length;
                while ((length = fis.read(buffer)) > 0) {
                    zipOut.write(buffer, 0, length);
                }

                fis.close();
            } else if (archivo.isDirectory()) {
                comprimirCarpeta(archivo, nombreCarpeta + File.separator + archivo.getName(), zipOut);
            }
        }
    }

    @Override
    public Response getIngresosPatios(String json) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        JsonObject parameter = Util.readJsonObjectParameter(json);
        String fecha_ini = parameter.get("fecha_ini").getAsString();
        String fecha_fin = parameter.get("fecha_fin").getAsString();

        query = "select p.nombre_patio, t.id_patio_destino,p.color, count(*)\n"
                + "from ingreso_local_detalle ild\n"
                + "inner join traslado_detalle td on td.id_detalle_orden_ingreso = ild.id_detalle_orden_ingreso\n"
                + "inner join traslados t on t.codigo_traslado = ild.codigo_traslado\n"
                + "inner join patios p on p.id = t.id_patio_destino \n"
                + "where ild.fecha_creacion::date between ?::date and ?::date\n"
                + "group by 1, 2, 3;";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, fecha_ini);
                ps.setString(2, fecha_fin);
                ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            respObject = Util.writeResponseError("Cargar ingresos a patios", ex.toString(), "/get-ingresos-proveedores");
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
    public Response getTotalIngresos() {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        query = "select count(*)\n"
                + "from ingreso_local_detalle ild\n"
                + "inner join traslado_detalle td on td.id_detalle_orden_ingreso = ild.id_detalle_orden_ingreso\n"
                + "inner join traslados t on t.codigo_traslado = ild.codigo_traslado";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                rs = ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar Total de ingresos", ex.toString(), "/get-total-ingresos");
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
    public Response getDocumentsPending(String codigOrden) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "select nombre_documento from (\n"
                + "				SELECT \n"
                + "				rpd.id_documento,\n"
                + "				ddd.nombre_documento,\n"
                + "				rpd.archivo_obligatorio,\n"
                + "				coalesce(adi.tiene_archivo,'N') as tiene_archivo\n"
                + "				FROM orden_ingreso oi \n"
                + "				INNER JOIN rel_proveedor_documento rpd ON rpd.nit_proveedor =oi.nit_proveedor \n"
                + "				inner join documentos_desintegracion_documental ddd on ddd.id=rpd.id_documento\n"
                + "				LEFT join archivos_desintegracion_inicial adi on adi.id_documento_desintegracion = rpd.id_documento\n"
                + "				and adi.codigo_orden_ingreso = oi.codigo_orden_ingreso\n"
                + "				WHERE oi.codigo_orden_ingreso =? AND rpd.archivo_obligatorio ='S'\n"
                + ")r where r.tiene_archivo='N'";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, codigOrden);
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
    public Response getIngresosProveedores(String json) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        JsonObject parameter = Util.readJsonObjectParameter(json);
        String fecha_ini = parameter.get("fecha_ini").getAsString();
        String fecha_fin = parameter.get("fecha_fin").getAsString();
        Integer id_patio = parameter.get("id_patio").getAsInt();

        query = "select p.nit, p.razon_social, p.color, count(*) \n"
                + "from ingreso_local_detalle ild\n"
                + "inner join orden_ingreso_detalle oid2 on oid2.id = ild.id_detalle_orden_ingreso \n"
                + "inner join traslados t on t.codigo_traslado = ild.codigo_traslado\n"
                + "inner join proveedor p on p.nit = oid2.tipo_codigo \n"
                + "where ild.fecha_creacion::date between ?::date and ?::date and t.id_patio_destino = ?\n"
                + "group by 1;";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, fecha_ini);
                ps.setString(2, fecha_fin);
                ps.setInt(3, id_patio);
                ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            respObject = Util.writeResponseError("Cargar detalle Ordenes de Ingreso", ex.toString(), "/get-detalle-orden-ingreso");
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
    public Response getIngresosFechas(String json) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        JsonObject parameter = Util.readJsonObjectParameter(json);
        String fecha_ini = parameter.get("fecha_ini").getAsString();
        String fecha_fin = parameter.get("fecha_fin").getAsString();
        String id_proveedor = parameter.get("id_proveedor").getAsString();
        String id_patio = parameter.get("id_patio").getAsString();

        query = "select * \n"
                + "from ingreso_local_detalle ild\n"
                + "inner join orden_ingreso_detalle oid2 on oid2.id = ild.id_detalle_orden_ingreso \n"
                + "inner join traslados t on t.codigo_traslado = ild.codigo_traslado\n"
                + "inner join proveedor p on p.nit = oid2.tipo_codigo \n"
                + "where ild.fecha_creacion::date between ?::date and ?::date and t.id_patio_destino = ?::numeric and p.nit = ?::numeric;";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, fecha_ini);
                ps.setString(2, fecha_fin);
                ps.setString(3, id_patio);
                ps.setString(4, id_proveedor);
                ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            respObject = Util.writeResponseError("Cargar detalle Ordenes de Ingreso", ex.toString(), "/get-detalle-orden-ingreso");
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
    public Response getInventarioRecnar(Usuario usuario) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        query = " select \n"
                + "                	cca.nombre_componente,\n"
                + "                	ir.unidad_medida,\n"
                + "                	ir.codigo_inventario,\n"
                + "                	ir.origen_inventario,\n"
                + "                	ir.cantidad_ingreso,\n"
                + "                	ir.cantidad_salida,\n"
                + "                	ir.total_inventario,\n"
                + "                	p.nombre_patio\n"
                + "                from public.inventario_recnar ir\n"
                + "                inner join componentes_clase_articulos cca on cca.codigo_inventario =substring(ir.codigo_inventario FROM '^[A-Z0-9]+') and ir.origen_inventario ='DE' and cca.inventario = true\n"
                + "                inner join patios p on p.id=ir.patio \n"
                + "                where ir.patio  IN (SELECT id_patio  FROM rel_usuarios_patios rup WHERE rup.usuario=?)\n"
                + "                union all\n"
                + "                select mr.nombre_material,\n"
                + "                ir.unidad_medida,\n"
                + "                	ir.codigo_inventario,\n"
                + "                	ir.origen_inventario,\n"
                + "                	ir.cantidad_ingreso,\n"
                + "                	ir.cantidad_salida,\n"
                + "                	ir.total_inventario ,\n"
                + "                	p.nombre_patio\n"
                + "                from public.inventario_recnar ir\n"
                + "                inner join materiales_recuperados mr on mr.codigo_inventario=substring(ir.codigo_inventario FROM '^[A-Z0-9]+') and ir.origen_inventario ='MA'\n"
                + "                inner join patios p on p.id=ir.patio \n"
                + "                 where ir.patio  IN (SELECT id_patio  FROM rel_usuarios_patios rup WHERE rup.usuario=?)";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, usuario.getId_usuario());
                ps.setString(2, usuario.getId_usuario());
                rs = ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar Combo", ex.toString(), "/get-combo-proveedor");
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
    public Response deleteItemCronograma(String item, Usuario usuario) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "delete from cronograma_detalle where id = ?::numeric;";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, item);
                if (ps.executeUpdate() > 0) {
                    respObject = Util.writreResponseString("OK");
                    response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            respObject = Util.writeResponseError("Actualizar orden", ex.toString(), "/delete-orden-ingreso");
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
    public Response getDependenciasProveedor(String item, Usuario usuario) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        query = "SELECT * FROM DEPENDENCIAS WHERE nit_proveedor = ?::numeric";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, item);
                ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar Dependencias", ex.toString(), "/get-dependencias-proveedor");
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
    public Response deleteDependenciasProveedor(String item, Usuario usuario) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "delete from dependencias where id = ?::numeric;";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, item);
                if (ps.executeUpdate() > 0) {
                    respObject = Util.writreResponseString("OK");
                    response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            respObject = Util.writeResponseError("Actualizar orden", ex.toString(), "/delete-orden-ingreso");
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
    public Response insertDependenciasProveedor(String json) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        JsonObject parameter = Util.readJsonObjectParameter(json);

        query = "INSERT INTO dependencias\n"
                + "(nombre, nit_proveedor)\n"
                + "VALUES(upper(?)::varchar, ?::numeric);";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, parameter.get("nombre").getAsString());
                ps.setString(2, parameter.get("nit_proveedor").getAsString());
                if (ps.executeUpdate() > 0) {
                    respObject = Util.writreResponseString("OK");
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
    public Response getPropietarios() {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        query = "SELECT * FROM propietario ORDER BY nombre asc";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar propietarios", ex.toString(), "/get-propietarios");
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
    public Response insertPropietarios(String json, Usuario usuario) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        JsonObject parameter = Util.readJsonObjectParameter(json);

        query = "SELECT public.crear_propietario(?::json, ?::varchar) as resp;";
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
    public Response getNovedadDetalleCronograma(Integer item) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        query = "select * from novedad_cronograma nc where id_detalle_cronograma = ?::int order by fecha_creacion desc";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setInt(1, item);
                ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar Dependencias", ex.toString(), "/get-dependencias-proveedor");
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
    public Response insertNovedadDetalleCronograma(String json, Usuario usuario) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        JsonObject parameter = Util.readJsonObjectParameter(json);

        query = "INSERT INTO novedad_cronograma\n"
                + "(descripcion, usuario_creador, id_detalle_cronograma, fecha_creacion)\n"
                + "VALUES(?::varchar,?::varchar,?::int, now());";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, parameter.get("descripcion").getAsString());
                ps.setString(2, usuario.getId_usuario());
                ps.setInt(3, parameter.get("id_detalle_cronograma").getAsInt());
                if (ps.executeUpdate() > 0) {
                    respObject = Util.writreResponseString("OK");
                    response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            respObject = Util.writeResponseError("Crear propietarios", ex.toString(), "/insert-propietarios");
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
    public Response getEstadoOrden(int idCronograma) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        String json = "{}";

        query = "select validar_estado_orden(?) as json";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setInt(1, idCronograma);
                rs = ps.executeQuery();

                if (rs.next()) {
                    json = rs.getString("json");
                }
                respObject = Util.getObjectJson(json);
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            respObject = Util.writeResponseError("Cargar estado Ordenes de Ingreso", ex.toString(), "/get-orden-ingreso");
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
    public Response getTotalAutomotoresCronograma(Integer item) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        query = "select count(*), ca.nombre_clase_articulo,id_clasificacion, oid2.id_clase_articulos \n"
                + "from cronograma c \n"
                + "inner join orden_ingreso oi on oi.id_cronograma = c.id \n"
                + "inner join orden_ingreso_detalle oid2 on oid2.codigo_orden_ingreso = oi.codigo_orden_ingreso \n"
                + "inner join clase_articulos ca on ca.id = oid2.id_clase_articulos \n"
                + "where c.id = ?::int \n"
                + "group by ca.nombre_clase_articulo,id_clasificacion, oid2.id_clase_articulos;";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setInt(1, item);
                ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar Dependencias", ex.toString(), "/get-dependencias-proveedor");
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
    public Response saveSolicitudDespacho(String json, Usuario usuario) {
        Response response = null;
        JsonObject respObject = null;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "SELECT guardar_solicitud_despacho(?::json, ?::varchar) as resp";
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
            respObject = Util.writeResponseError("Guardar solicitud despacho", ex.toString(), "/save-solicitud-despacho");
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
    public Response getSolicitudesDespacho(Usuario usuario, String aprobado) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "SELECT sd.codigo_solicitud,\n"
                + "            sd.descripcion,\n"
                + "            sd.aprobado,\n"
                + "            sd.usuario_creador,\n"
                + "            sd.usuario_aprobacion,\n"
                + "            sd.observacion,\n"
                + "            sd.fecha_creacion,\n"
                + "            sd.fecha_aprobacion,\n"
                + "            p.nombre_patio \n"
                + "            FROM public.solicitud_despacho sd\n"
                + "            inner join patios p on p.id =sd.patio\n"
                + "            WHERE case when ? = 'ALL' THEN aprobado in ('S','N','R','D') else aprobado=? END\n"
                + "            AND sd.patio IN (SELECT id_patio  FROM rel_usuarios_patios rup WHERE rup.usuario= ?::varchar )\n"
                + "            order by fecha_creacion desc";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, aprobado);
                ps.setString(2, aprobado);
                ps.setString(3, usuario.getId_usuario());
                rs = ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar solicitudes despacho", ex.toString(), "/get-solicitudes-despacho");
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
    public Response getSolicitudesDespachoDetalle(String codigoSolicitud) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        query = "select sdd.codigo_solicitud, \n"
                + "	cca.nombre_componente,\n"
                + "	ir.unidad_medida,\n"
                + "	ir.codigo_inventario,\n"
                + "	ir.total_inventario,\n"
                + "	p.nombre_patio, sdd.peso, sdd.observacion\n"
                + "from solicitud_despacho sd\n"
                + "inner join solicitud_despacho_detalle sdd on sdd.codigo_solicitud =sd.codigo_solicitud \n"
                + "inner join public.inventario_recnar ir on ir.codigo_inventario = sdd.codigo_inventario \n"
                + "inner join componentes_clase_articulos cca on cca.codigo_inventario =substring(ir.codigo_inventario FROM '^[A-Z0-9]+') and ir.origen_inventario ='DE'\n"
                + "inner join patios p on p.id=ir.patio \n"
                + "where sd.codigo_solicitud =? \n"
                + "union all\n"
                + "select sdd.codigo_solicitud, mr.nombre_material,\n"
                + "	ir.unidad_medida,\n"
                + "	ir.codigo_inventario,\n"
                + "	ir.total_inventario,\n"
                + "	p.nombre_patio, sdd.peso, sdd.observacion\n"
                + "from solicitud_despacho sd\n"
                + "inner join solicitud_despacho_detalle sdd on sdd.codigo_solicitud =sd.codigo_solicitud \n"
                + "inner join public.inventario_recnar ir on ir.codigo_inventario = sdd.codigo_inventario\n"
                + "inner join materiales_recuperados mr on mr.codigo_inventario=substring(ir.codigo_inventario FROM '^[A-Z0-9]+') and ir.origen_inventario ='MA'\n"
                + "inner join patios p on p.id=ir.patio \n"
                + "where sd.codigo_solicitud =? ";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, codigoSolicitud);
                ps.setString(2, codigoSolicitud);
                rs = ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar solicitudes despacho detalle", ex.toString(), "/get-solicitudes-despacho-detalle");
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
    public Response aprobarSolicitudDespacho(String json, Usuario usuario) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        JsonObject parameter = Util.readJsonObjectParameter(json);

        query = "UPDATE public.solicitud_despacho\n"
                + "SET aprobado=?, usuario_aprobacion=?, fecha_aprobacion=now(),observacion=?\n"
                + "WHERE codigo_solicitud =?;";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, parameter.get("aprobado").getAsString());
                ps.setString(2, usuario.getId_usuario());
                ps.setString(3, parameter.get("observacion").getAsString());
                ps.setString(4, parameter.get("codigo_solicitud").getAsString());
                if (ps.executeUpdate() > 0) {
                    respObject = Util.writreResponseString("OK");
                    response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            respObject = Util.writeResponseError("Aprobar solicitud despacho", ex.toString(), "/finalizar-desintegracion-documetal");
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
    public Response getReporteSolicitudesDespacho(Usuario usuario, String json) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        JsonObject parameter = Util.readJsonObjectParameter(json);

        query = "SELECT sd.*, case \n"
                + "when sd.aprobado = 'S' THEN 'APROBADA' \n"
                + "when sd.aprobado = 'N' THEN 'PENDIENTE' \n"
                + "when sd.aprobado = 'D' THEN 'DESPACHADA' \n"
                + "when sd.aprobado = 'R' THEN 'RECHAZADA' \n"
                + "end as estado_solicitud, case \n"
                + "when sd.aprobado = 'S' THEN 'aprobada' \n"
                + "when sd.aprobado = 'N' THEN 'pendiente' \n"
                + "when sd.aprobado = 'D' THEN 'despachada' \n"
                + "when sd.aprobado = 'R' THEN 'rechazada' \n"
                + "end as fondo, \n"
                + "            p.nombre_patio,po.nombre_patio as nombre_patio_origen,pd.nombre_patio as nombre_patio_destino \n"
                + "            FROM public.solicitud_despacho sd\n"
                + "            inner join patios p on p.id =sd.patio\n"
                + "            left join patios po on po.id =sd.patio_origen\n"
                + "            left join patios pd on pd.id =sd.patio_destino\n"
                + "            WHERE case when ? = 'ALL' THEN aprobado in ('S','N','R','D') else aprobado=? END\n"
                + "            AND sd.patio IN (SELECT id_patio  FROM rel_usuarios_patios rup WHERE rup.usuario= ?::varchar ) and date(sd.fecha_creacion) between ?::date and ?::date \n"
                + "            order by fecha_creacion";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, parameter.get("aprobado").getAsString());
                ps.setString(2, parameter.get("aprobado").getAsString());
                ps.setString(3, usuario.getId_usuario());
                ps.setString(4, parameter.get("fecha_ini").getAsString());
                ps.setString(5, parameter.get("fecha_fin").getAsString());
                rs = ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar solicitudes despacho", ex.toString(), "/get-solicitudes-despacho");
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
    public Response generarCertificacionFinal(Usuario usuario, String json, boolean firma) {
        Response response = null;
        JsonObject respObject;
        JsonObject parameter = Util.readJsonObjectParameter(json);

        byte[] pdfBytes = null;
        try {
            pdfBytes = generarPDFComoBytes(parameter, firma);
        } catch (BadElementException | IOException ex) {
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Convertir el contenido del PDF a Base64
        String pdfBase64 = Base64.encodeBase64String(pdfBytes);

        respObject = Util.writreResponseString(pdfBase64);
        response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);

        return response;
    }

    private byte[] generarPDFComoBytes(JsonObject parameter, boolean firma) throws BadElementException, IOException {
        String logo = "D:\\Naranjos 2.0\\apache-tomcat-8.5.69\\webapps\\ROOT\\resources\\Logo.png";
        String logo2 = "D:\\Naranjos 2.0\\apache-tomcat-8.5.69\\webapps\\ROOT\\resources\\LogoSolo.png";

        Font tahomaFontTit = FontFactory.getFont("Tahoma", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 9, Font.BOLD);
        Font fTituloCod = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD, BaseColor.RED);
        Font fNormal = new Font(Font.FontFamily.HELVETICA, 8);
        Font fSubtit = new Font(Font.FontFamily.HELVETICA, 7, Font.BOLD);
        Font fNormal2 = FontFactory.getFont("Tahoma", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 7);
        float interlineado = 10f;
        try {
            // Crear un documento

            Document documento = new Document(PageSize.LETTER, 30, 30, 25, 25);
            // Crear un flujo de bytes para escribir el PDF
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfWriter writer = PdfWriter.getInstance(documento, outputStream);

            // Abrir el documento para agregar contenido+
            documento.open();

            PdfContentByte contentByte = writer.getDirectContentUnder();
            // Crear una imagen desde el archivo
            Image image = Image.getInstance(logo2);

            // Escalar la imagen según sea necesario
            image.scaleToFit(600, 600);

            float pageWidth = PageSize.LETTER.getWidth();
            float pageHeight = PageSize.LETTER.getHeight();

            // Calcular las coordenadas para el centro de la página
            float x = (pageWidth - image.getScaledWidth()) / 2;
            float y = (pageHeight - image.getScaledHeight()) / 2;

            PdfGState gs = new PdfGState();
            gs.setFillOpacity(0.5f); // Establecer la opacidad (valores entre 0 y 1)

            // Aplicar la transparencia al contenido
            contentByte.setGState(gs);

            // Agregar la imagen al fondo del documento
            image.setAbsolutePosition(x, y);
            contentByte.addImage(image);
            contentByte.setGState(new PdfGState());

            PdfPTable table = new PdfPTable(3);
            table.setWidths(new int[]{5, 25, 10});

            table.setWidthPercentage(100);
            table.getDefaultCell().setVerticalAlignment(Element.ALIGN_CENTER);
            PdfPCell cellCabe = new PdfPCell();
            Image img1 = Image.getInstance(logo);
            img1.scaleAbsolute(10, 10);
            cellCabe.setImage(img1);
            cellCabe.setBorderColor(BaseColor.BLACK);
            table.addCell(cellCabe);

            cellCabe = new PdfPCell();
            Paragraph paragraph1 = new Paragraph("CERTIFICADO DE DESINTEGRACIÓN FÍSICA TOTAL DE VEHÍCULOS", tahomaFontTit);
            Paragraph paragraph2 = new Paragraph("SERVICIO PÚBLICO Y PARTICULAR DE TRANSPORTE TERRESTRE DE PASAJEROS", fNormal2);
            paragraph1.setAlignment(Paragraph.ALIGN_CENTER);
            paragraph2.setAlignment(Paragraph.ALIGN_CENTER);
            cellCabe.addElement(paragraph1);
            cellCabe.addElement(paragraph2);
            cellCabe.setBorderColor(BaseColor.BLACK);
            table.addCell(cellCabe);

            cellCabe = new PdfPCell();
            Paragraph cCodigo = new Paragraph("Código: F-CP-10 \n", fNormal2);
            Paragraph cVersion = new Paragraph("Versión: 2\n", fNormal2);
            Paragraph cFecha = new Paragraph("Fecha: 14-01-2020", fNormal2);
            cCodigo.setAlignment(Paragraph.ALIGN_CENTER);
            cVersion.setAlignment(Paragraph.ALIGN_CENTER);
            cFecha.setAlignment(Paragraph.ALIGN_CENTER);
            cellCabe.addElement(cCodigo);
            cellCabe.addElement(cVersion);
            cellCabe.addElement(cFecha);
            cellCabe.setBorderColor(BaseColor.BLACK);
            table.addCell(cellCabe);

            documento.add(table);

            documento.add(new Paragraph(" "));

            JsonObject detalle = getInfoDetalle(parameter.get("id_detalle_orden_ingreso").getAsInt());

            Paragraph paragraph = new Paragraph();
            paragraph.setAlignment(Paragraph.ALIGN_JUSTIFIED);
            paragraph.add(new Chunk("Dando cumplimiento al procedimiento establecido en la Resolución 646 del 18 de Marzo de 2014 del Ministerio de Transporte"
                    + ", Por la cual se autoriza a ", fNormal));

            paragraph.add(new Chunk("RECUPERACIONES NARANJO RECYCLING SAS", fSubtit));

            paragraph.add(new Chunk(" con Nit. ", fNormal));

            paragraph.add(new Chunk("806.011.019-0", fSubtit));

            paragraph.add(new Chunk(", para operar como entidad Desintegradora para expedir el Certificado de Desintegración Física Total de los vehículos de servicio público y particular de transporte terrestre de pasajeros.", fNormal));

            // Agregar el párrafo a la celda
            PdfPCell cellDescr = new PdfPCell();
            paragraph.setLeading(interlineado);
            cellDescr.addElement(paragraph);
            cellDescr.setVerticalAlignment(Element.ALIGN_CENTER);
            cellDescr.setBorderColor(BaseColor.BLACK);

            Paragraph paraDesc2 = new Paragraph("CERTIFICADO DE DESINTEGRACIÓN FÍSICA DE VEHÍCULOS", tahomaFontTit);
            paraDesc2.setAlignment(Paragraph.ALIGN_CENTER);
            PdfPCell cellDescr2 = new PdfPCell();
            cellDescr2.addElement(paraDesc2);
            cellDescr2.setVerticalAlignment(Element.ALIGN_CENTER);
            cellDescr2.setBorderColor(BaseColor.BLACK);

            Paragraph paraDesc3 = new Paragraph("Los representantes designados RECUPERACIONES NARANJO RECYCLING SAS, inspeccionaron el estado del vehículo, identificándolo con las siguientes especificaciones:", fNormal);
            paraDesc3.setAlignment(Paragraph.ALIGN_LEFT);
            PdfPCell cellDescr3 = new PdfPCell();
            cellDescr3.addElement(paraDesc3);
            cellDescr3.setVerticalAlignment(Element.ALIGN_CENTER);
            cellDescr3.setBorderColor(BaseColor.BLACK);

            PdfPTable tableDescr = new PdfPTable(1);
            tableDescr.setWidthPercentage(100);
            tableDescr.addCell(cellDescr);
            tableDescr.addCell(cellDescr2);
            tableDescr.addCell(cellDescr3);
            documento.add(tableDescr);

            PdfPTable tablecod = new PdfPTable(2);
            tablecod.setWidthPercentage(100);

            Paragraph sello = new Paragraph("Sello original:", fNormal);
            sello.setAlignment(Paragraph.ALIGN_LEFT);
            PdfPCell cellCod = new PdfPCell();
            cellCod.addElement(sello);
            cellCod.setVerticalAlignment(Element.ALIGN_CENTER);
            cellCod.setBorderColor(BaseColor.BLACK);
            tablecod.addCell(cellCod);

            Paragraph pCert = new Paragraph("CERTIFICADO No. " + detalle.get("codigo_certificado").getAsString() + "             ", fTituloCod);
            pCert.setAlignment(Paragraph.ALIGN_RIGHT);
            cellCod = new PdfPCell();
            cellCod.addElement(pCert);
            cellCod.setVerticalAlignment(Element.ALIGN_CENTER);
            cellCod.setBorderColor(BaseColor.BLACK);
            tablecod.addCell(cellCod);

            documento.add(tablecod);

            PdfPTable tableDetalle = new PdfPTable(2);
            tableDetalle.setWidthPercentage(100);

            PdfPCell cellDetalle = new PdfPCell(new Paragraph("Fecha del Certificado", fNormal));
            cellDetalle.setFixedHeight(10f);
            cellDetalle.setHorizontalAlignment(Element.ALIGN_LEFT);
            tableDetalle.addCell(cellDetalle);

            LocalDate fecha = LocalDate.parse(detalle.get("fecha_certificado").getAsString());

            // Formatear la fecha en el formato deseado: 'dd de MMMM de yyyy'
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy", new Locale("es"));
            String fechaFormateada = fecha.format(formatter);

            cellDetalle = new PdfPCell(new Paragraph(fechaFormateada.toUpperCase(), fSubtit));
            cellDetalle.setFixedHeight(20f);
            cellDetalle.setHorizontalAlignment(Element.ALIGN_CENTER);
            tableDetalle.addCell(cellDetalle);

            cellDetalle = new PdfPCell(new Paragraph("Departamento Desintegracion / Ciudad ", fNormal));
            cellDetalle.setFixedHeight(20f);
            cellDetalle.setHorizontalAlignment(Element.ALIGN_LEFT);
            tableDetalle.addCell(cellDetalle);

            cellDetalle = new PdfPCell(new Paragraph(detalle.get("municipio").getAsString(), fSubtit));
            cellDetalle.setFixedHeight(20f);
            cellDetalle.setHorizontalAlignment(Element.ALIGN_CENTER);
            tableDetalle.addCell(cellDetalle);

            cellDetalle = new PdfPCell(new Paragraph("Placa ", fNormal));
            cellDetalle.setFixedHeight(20f);
            cellDetalle.setHorizontalAlignment(Element.ALIGN_LEFT);
            tableDetalle.addCell(cellDetalle);

            cellDetalle = new PdfPCell(new Paragraph(detalle.get("placa").getAsString(), fSubtit));
            cellDetalle.setFixedHeight(20f);
            cellDetalle.setHorizontalAlignment(Element.ALIGN_CENTER);
            tableDetalle.addCell(cellDetalle);

            cellDetalle = new PdfPCell(new Paragraph("Nombre Completo del Propietario", fNormal));
            cellDetalle.setFixedHeight(20f);
            cellDetalle.setHorizontalAlignment(Element.ALIGN_LEFT);
            tableDetalle.addCell(cellDetalle);

            cellDetalle = new PdfPCell(new Paragraph(detalle.get("propietario").getAsString(), fSubtit));
            cellDetalle.setFixedHeight(20f);
            cellDetalle.setHorizontalAlignment(Element.ALIGN_CENTER);
            tableDetalle.addCell(cellDetalle);

            cellDetalle = new PdfPCell(new Paragraph("Documento del Propietario", fNormal));
            cellDetalle.setFixedHeight(20f);
            cellDetalle.setHorizontalAlignment(Element.ALIGN_LEFT);
            tableDetalle.addCell(cellDetalle);

            cellDetalle = new PdfPCell(new Paragraph(detalle.get("documento_propietario").getAsString(), fSubtit));
            cellDetalle.setFixedHeight(20f);
            cellDetalle.setHorizontalAlignment(Element.ALIGN_CENTER);
            tableDetalle.addCell(cellDetalle);

            cellDetalle = new PdfPCell(new Paragraph("Autorización / Resolución / Contrato / Dijin ", fNormal));
            cellDetalle.setFixedHeight(20f);
            cellDetalle.setHorizontalAlignment(Element.ALIGN_LEFT);
            tableDetalle.addCell(cellDetalle);

            cellDetalle = new PdfPCell(new Paragraph(detalle.get("resolucion").getAsString(), fSubtit));
            cellDetalle.setFixedHeight(20f);
            cellDetalle.setHorizontalAlignment(Element.ALIGN_CENTER);
            tableDetalle.addCell(cellDetalle);

            cellDetalle = new PdfPCell(new Paragraph("Motivo de la Desintegración", fNormal));
            cellDetalle.setFixedHeight(20f);
            cellDetalle.setHorizontalAlignment(Element.ALIGN_LEFT);
            tableDetalle.addCell(cellDetalle);

            cellDetalle = new PdfPCell(new Paragraph(detalle.get("motivo").getAsString(), fSubtit));
            cellDetalle.setFixedHeight(20f);
            cellDetalle.setHorizontalAlignment(Element.ALIGN_CENTER);
            tableDetalle.addCell(cellDetalle);

            cellDetalle = new PdfPCell(new Paragraph("Marca", fNormal));
            cellDetalle.setFixedHeight(20f);
            cellDetalle.setHorizontalAlignment(Element.ALIGN_LEFT);
            tableDetalle.addCell(cellDetalle);

            cellDetalle = new PdfPCell(new Paragraph(detalle.get("marca").getAsString(), fSubtit));
            cellDetalle.setFixedHeight(20f);
            cellDetalle.setHorizontalAlignment(Element.ALIGN_CENTER);
            tableDetalle.addCell(cellDetalle);

            cellDetalle = new PdfPCell(new Paragraph("Linea", fNormal));
            cellDetalle.setFixedHeight(20f);
            cellDetalle.setHorizontalAlignment(Element.ALIGN_LEFT);
            tableDetalle.addCell(cellDetalle);

            cellDetalle = new PdfPCell(new Paragraph(detalle.get("linea").getAsString(), fSubtit));
            cellDetalle.setFixedHeight(20f);
            cellDetalle.setHorizontalAlignment(Element.ALIGN_CENTER);
            tableDetalle.addCell(cellDetalle);

            cellDetalle = new PdfPCell(new Paragraph("Modelo ", fNormal));
            cellDetalle.setFixedHeight(20f);
            cellDetalle.setHorizontalAlignment(Element.ALIGN_LEFT);
            tableDetalle.addCell(cellDetalle);

            cellDetalle = new PdfPCell(new Paragraph(detalle.get("modelo").getAsString(), fSubtit));
            cellDetalle.setFixedHeight(20f);
            cellDetalle.setHorizontalAlignment(Element.ALIGN_CENTER);
            tableDetalle.addCell(cellDetalle);

            cellDetalle = new PdfPCell(new Paragraph("Clase de Vehículo", fNormal));
            cellDetalle.setFixedHeight(20f);
            cellDetalle.setHorizontalAlignment(Element.ALIGN_LEFT);
            tableDetalle.addCell(cellDetalle);

            cellDetalle = new PdfPCell(new Paragraph(detalle.get("nombre_clase_articulo").getAsString(), fSubtit));
            cellDetalle.setFixedHeight(20f);
            cellDetalle.setHorizontalAlignment(Element.ALIGN_CENTER);
            tableDetalle.addCell(cellDetalle);

            cellDetalle = new PdfPCell(new Paragraph("Tipo de Carrocería", fNormal));
            cellDetalle.setFixedHeight(20f);
            cellDetalle.setHorizontalAlignment(Element.ALIGN_LEFT);
            tableDetalle.addCell(cellDetalle);

            cellDetalle = new PdfPCell(new Paragraph(detalle.get("carroceria").getAsString(), fSubtit));
            cellDetalle.setFixedHeight(20f);
            cellDetalle.setHorizontalAlignment(Element.ALIGN_CENTER);
            tableDetalle.addCell(cellDetalle);

            cellDetalle = new PdfPCell(new Paragraph("Tipo de Servicio ", fNormal));
            cellDetalle.setFixedHeight(20f);
            cellDetalle.setHorizontalAlignment(Element.ALIGN_LEFT);
            tableDetalle.addCell(cellDetalle);

            cellDetalle = new PdfPCell(new Paragraph(detalle.get("tipo_servicio").getAsString(), fSubtit));
            cellDetalle.setFixedHeight(20f);
            cellDetalle.setHorizontalAlignment(Element.ALIGN_CENTER);
            tableDetalle.addCell(cellDetalle);

            cellDetalle = new PdfPCell(new Paragraph("Configuración N de Ejes", fNormal));
            cellDetalle.setFixedHeight(20f);
            cellDetalle.setHorizontalAlignment(Element.ALIGN_LEFT);
            tableDetalle.addCell(cellDetalle);

            cellDetalle = new PdfPCell(new Paragraph(detalle.get("configuracion_ejes").getAsString(), fSubtit));
            cellDetalle.setFixedHeight(20f);
            cellDetalle.setHorizontalAlignment(Element.ALIGN_CENTER);
            tableDetalle.addCell(cellDetalle);

            cellDetalle = new PdfPCell(new Paragraph("Peso Bruto Vehicular", fNormal));
            cellDetalle.setFixedHeight(20f);
            cellDetalle.setHorizontalAlignment(Element.ALIGN_LEFT);
            tableDetalle.addCell(cellDetalle);

            cellDetalle = new PdfPCell(new Paragraph(detalle.get("peso_bruto").getAsString(), fSubtit));
            cellDetalle.setFixedHeight(20f);
            cellDetalle.setHorizontalAlignment(Element.ALIGN_CENTER);
            tableDetalle.addCell(cellDetalle);

            cellDetalle = new PdfPCell(new Paragraph("Numero de Motor", fNormal));
            cellDetalle.setFixedHeight(20f);
            cellDetalle.setHorizontalAlignment(Element.ALIGN_LEFT);
            tableDetalle.addCell(cellDetalle);

            cellDetalle = new PdfPCell(new Paragraph(detalle.get("motor").getAsString(), fSubtit));
            cellDetalle.setFixedHeight(20f);
            cellDetalle.setHorizontalAlignment(Element.ALIGN_CENTER);
            tableDetalle.addCell(cellDetalle);
            cellDetalle = new PdfPCell(new Paragraph("Numero de Chasis / Serie", fNormal));
            cellDetalle.setFixedHeight(20f);
            cellDetalle.setHorizontalAlignment(Element.ALIGN_LEFT);
            tableDetalle.addCell(cellDetalle);

            cellDetalle = new PdfPCell(new Paragraph(detalle.get("chasis").getAsString(), fSubtit));
            cellDetalle.setFixedHeight(20f);
            cellDetalle.setHorizontalAlignment(Element.ALIGN_CENTER);
            tableDetalle.addCell(cellDetalle);

            cellDetalle = new PdfPCell(new Paragraph("Color", fNormal));
            cellDetalle.setFixedHeight(20f);
            cellDetalle.setHorizontalAlignment(Element.ALIGN_LEFT);
            tableDetalle.addCell(cellDetalle);

            cellDetalle = new PdfPCell(new Paragraph(detalle.get("color").getAsString(), fSubtit));
            cellDetalle.setFixedHeight(20f);
            cellDetalle.setHorizontalAlignment(Element.ALIGN_CENTER);
            tableDetalle.addCell(cellDetalle);

            cellDetalle = new PdfPCell(new Paragraph("Capacidad", fNormal));
            cellDetalle.setFixedHeight(20f);
            cellDetalle.setHorizontalAlignment(Element.ALIGN_LEFT);
            tableDetalle.addCell(cellDetalle);

            cellDetalle = new PdfPCell(new Paragraph(detalle.get("capacidad").getAsString(), fSubtit));
            cellDetalle.setFixedHeight(20f);
            cellDetalle.setHorizontalAlignment(Element.ALIGN_CENTER);
            tableDetalle.addCell(cellDetalle);

            documento.add(tableDetalle);

            documento.add(new Paragraph(" "));

            Paragraph paragraphFoot = new Paragraph();
            paragraphFoot.setAlignment(Paragraph.ALIGN_JUSTIFIED);
            paragraphFoot.add(new Chunk("1. RECUPERACIONES NARANJO RECYCLING SAS", fSubtit));
            paragraphFoot.add(new Chunk(", realizó el proceso de Desintegración Física al vehículo con las especificaciones arriba mencionadas. \n", fNormal));

            paragraphFoot.add(new Chunk("2. RECUPERACIONES NARANJO RECYCLING SAS", fSubtit));
            paragraphFoot.add(new Chunk(", certifica que todos los guarismos de identificación y placas del vehículo fueron objeto de destrucción por parte de la entidad desintegradora, así mismo garantizamos que los demás componentes que integran el automotor  quedaron en estado inhabilitado e inservible, evitando su comercialización. \n", fNormal));

            paragraphFoot.add(new Chunk("3. RECUPERACIONES NARANJO RECYCLING SAS", fSubtit));
            paragraphFoot.add(new Chunk(", no asume responsabilidad alguna por el uso indebido que se le dé a este documento. \n", fSubtit));

            paragraphFoot.add(new Chunk("4. ", fSubtit));
            paragraphFoot.add(new Chunk("Este documento es válido, únicamente en original y firmado por el representante de la compañía, sin enmendaduras y/o tachaduras.  \n", fNormal));

            paragraphFoot.add(new Chunk("5. ", fSubtit));
            paragraphFoot.add(new Chunk("Este trabajo fue realizado dentro de las condiciones generales de servicio de ", fNormal));
            paragraphFoot.add(new Chunk("RECUPERACIONES NARANJO RECYCLING SAS", fSubtit));
            paragraphFoot.add(new Chunk(" y certificado emitido sin prejuicio es para beneficio de quien corresponda.", fNormal));
            paragraphFoot.setLeading(interlineado);

            PdfPCell cellFoot = new PdfPCell();
            cellFoot.addElement(paragraphFoot);
            cellFoot.setVerticalAlignment(Element.ALIGN_CENTER);
            cellFoot.setBorderColor(BaseColor.BLACK);

            PdfPTable tableFoot = new PdfPTable(1);
            tableFoot.setWidthPercentage(100);
            tableFoot.addCell(cellFoot);

            documento.add(tableFoot);

            // Cerrar el documento
            documento.close();

            // Obtener los bytes del PDF
            return outputStream.toByteArray();

        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return null;
    }

    public JsonObject getInfoDetalle(int idDetalleOrden) {
        JsonObject respObject = null;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "select fecha_certificado,\n"
                + "		upper(m.departamento || ' - ' || m.nombre_municipio) as municipio\n"
                + "                    ,upper(placa) as placa\n"
                + "                    ,upper(oid2.nombre_propietario)as propietario\n"
                + "                    ,upper(oid2.nit_propietario) as documento_propietario\n"
                + "                    ,upper(oid2.resolucion) as resolucion \n"
                + "                    ,upper(marca) as marca\n"
                + "                    ,upper(linea) as linea\n"
                + "                    ,upper(modelo) as modelo \n"
                + "                    ,upper(ca.nombre_clase_articulo) as nombre_clase_articulo\n"
                + "                    ,upper(carroceria) as carroceria\n"
                + "                    ,upper(tipo_servicio) as tipo_servicio\n"
                + "                    ,upper(configuracion_ejes) as configuracion_ejes\n"
                + "                    ,peso_bruto\n"
                + "                    ,upper(motor) as motor\n"
                + "                    ,upper(chasis) as chasis\n"
                + "                    ,upper(oid2.color) as color\n"
                + "                    ,upper(serie) as serie\n"
                + "                    ,upper(capacidad) as capacidad\n"
                + "                    ,upper(oid2.motivo) as motivo\n"
                + "                	,upper(codigo_certificado) as codigo_certificado\n"
                + "                	,upper(certificado_generado) as certificado_generado \n"
                + "                	from orden_ingreso_detalle oid2 \n"
                + "                	inner join orden_ingreso oi on oi.codigo_orden_ingreso =oid2.codigo_orden_ingreso \n"
                + "                	inner join proveedor p on p.nit =oi.nit_proveedor \n"
                + "                	inner join  clase_articulos ca on ca.id =oid2.id_clase_articulos \n"
                + "                	inner join municipio m on m.id =oi.id_municipio \n"
                + "                	where oid2.id =?";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setInt(1, idDetalleOrden);
                rs = ps.executeQuery();
                respObject = Util.getQueryJsonObject(con, ps);
                return respObject;
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

        return respObject;
    }

    @Override
    public Response saveCertificacionFinal(String json, Usuario usuario) {
        Response response = null;
        JsonObject respObject = null;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "SELECT guardar_certificacion_final(?::json, ?::varchar) as resp";
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
            respObject = Util.writeResponseError("Guardar Certificacion final", ex.toString(), "/save-certificacion-final");
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
    public Response getConsultaCertificados(String json) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        JsonObject parameter = Util.readJsonObjectParameter(json);
        String filtroFechas = "";

        if (!parameter.get("fecha_ini").getAsString().equals("")) {
            filtroFechas = "and date(oide.fecha_certificado) between '" + parameter.get("fecha_ini").getAsString() + "'::date and '" + parameter.get("fecha_fin").getAsString() + "'::date";
        }

        query = "select oide.nit_propietario, \n"
                + "oide.nombre_propietario, \n"
                + "oi.codigo_orden_ingreso, \n"
                + "oide.codigo_certificado, \n"
                + "oide.usuario_genera_certificado, \n"
                + "oide.placa, \n"
                + "oide.descripcion_articulo,\n"
                + "oide.id,\n"
                + "oide.motor,\n"
                + "oide.chasis,\n"
                + "oide.color,\n"
                + "oide.resolucion,\n"
                + "oide.linea,\n"
                + "oide.marca,\n"
                + "oide.capacidad,\n"
                + "oide.serie,\n"
                + "oide.modelo,\n"
                + "oide.motivo,\n"
                + "oide.carroceria,\n"
                + "oide.tipo_servicio,\n"
                + "c.categoria,\n"
                + "a.nombre_articulo,\n"
                + "ca.nombre_clase_articulo,\n"
                + "c2.clasificacion\n"
                + "from orden_ingreso_detalle oide\n"
                + "inner join orden_ingreso oi on oi.codigo_orden_ingreso = oide.codigo_orden_ingreso \n"
                + "inner join proveedor p on p.nit = oi.nit_proveedor \n"
                + "INNER JOIN articulos a ON a.id = oide.id_articulo \n"
                + "INNER JOIN clase_articulos ca ON ca.id = oide.id_clase_articulos\n"
                + "INNER JOIN categoria c ON c.id = oide.id_categoria \n"
                + "INNER JOIN clasificacion c2 ON c2.id = oide.id_clasificacion\n"
                + "where oide.certificado_generado = ? and oide.aprobado = ? #FECHAS ORDER BY codigo_certificado;";
        try {
            con = Util.conectarBD();
            if (con != null) {
                query = query.replaceAll("#FECHAS", filtroFechas);
                ps = con.prepareStatement(query);
                ps.setString(1, parameter.get("generado").getAsString());
                ps.setString(2, parameter.get("aprobado").getAsString());
                System.out.println( ps.toString());
                rs = ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar solicitudes despacho", ex.toString(), "/get-solicitudes-despacho");
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
    public Response aprobarCertificados(String json, Usuario usuario) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        JsonObject parameter = Util.readJsonObjectParameter(json);

        query = "UPDATE orden_ingreso_detalle \n"
                + "SET aprobado = 'S', \n"
                + "usuario_aprueba_certificado = UPPER(?::VARCHAR) \n"
                + "WHERE id = ?::int \n";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, usuario.getId_usuario());
                ps.setInt(2, parameter.get("id").getAsInt());
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
            respObject = Util.writeResponseError("Actualizar producto", ex.toString(), "/update-producto");
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
    public Response anularCertificados(String json, Usuario usuario) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null, ps2 = null;
        ResultSet rs = null;
        String query;
        JsonObject parameter = Util.readJsonObjectParameter(json);

        query = "UPDATE orden_ingreso_detalle \n"
                + "SET aprobado = 'N', \n"
                + "certificado_generado = 'N', \n"
                + "usuario_aprueba_certificado = UPPER(?::VARCHAR) \n"
                + "WHERE id = ?::int \n";

        String query2 = "update desintegracion \n"
                + "set paso_actual = 'PASO 3', procesado = 'N'"
                + "where id_detalle_orden_ingreso = ?::int;";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, usuario.getId_usuario());
                ps.setInt(2, parameter.get("id").getAsInt());

                ps2 = con.prepareStatement(query2);
                ps2.setInt(1, parameter.get("id").getAsInt());
                ps2.executeUpdate();
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
            respObject = Util.writeResponseError("Actualizar producto", ex.toString(), "/update-producto");
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
    public Response getGruposProveedor(String item, Usuario usuario) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        query = "SELECT * FROM grupos WHERE nit_proveedor = ?::numeric";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, item);
                ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar Dependencias", ex.toString(), "/get-grupos-proveedor");
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
    public Response deleteGruposProveedor(String item, Usuario usuario) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "delete from grupos where id = ?::numeric;";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, item);
                if (ps.executeUpdate() > 0) {
                    respObject = Util.writreResponseString("OK");
                    response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            respObject = Util.writeResponseError("Actualizar orden", ex.toString(), "/delete-orden-ingreso");
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
    public Response insertGruposProveedor(String json) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        JsonObject parameter = Util.readJsonObjectParameter(json);

        query = "INSERT INTO grupos\n"
                + "(nombre, nit_proveedor)\n"
                + "VALUES(upper(?)::varchar, ?::numeric);";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, parameter.get("nombre").getAsString());
                ps.setString(2, parameter.get("nit_proveedor").getAsString());
                if (ps.executeUpdate() > 0) {
                    respObject = Util.writreResponseString("OK");
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
    public Response getCentroAcopiosProveedor(String item, Usuario usuario) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        query = "SELECT * FROM centros_acopios WHERE nit_proveedor = ?::numeric";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, item);
                ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar Dependencias", ex.toString(), "/get-grupos-proveedor");
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
    public Response deleteCentroAcopiosProveedor(String item, Usuario usuario) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "delete from centros_acopios where id = ?::numeric;";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, item);
                if (ps.executeUpdate() > 0) {
                    respObject = Util.writreResponseString("OK");
                    response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            respObject = Util.writeResponseError("Actualizar orden", ex.toString(), "/delete-orden-ingreso");
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
    public Response insertCentroAcopiosProveedor(String json) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        JsonObject parameter = Util.readJsonObjectParameter(json);

        query = "INSERT INTO centros_acopios\n"
                + "(nombre, nit_proveedor)\n"
                + "VALUES(upper(?)::varchar, ?::numeric);";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, parameter.get("nombre").getAsString());
                ps.setString(2, parameter.get("nit_proveedor").getAsString());
                if (ps.executeUpdate() > 0) {
                    respObject = Util.writreResponseString("OK");
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
    public Response getDocumentos() {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        query = "SELECT id ,nombre_documento \n"
                + "FROM documentos_desintegracion_documental  \n"
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
            respObject = Util.writeResponseError("Cargar Combo", ex.toString(), "/get-combo-municipios");
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
    public Response getComboCuentasCajas() {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        query = "select id,cuenta,descripcion \n"
                + "from public.cuentas_caja";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                rs = ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar Combo", ex.toString(), "/get-combo-municipios");
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
    public Response getTipificacion(String item) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        query = "select rit.codigo_inventario, rit.id_tipificacion, t.nombre_tipo\n"
                + "from rel_inventario_tipificacion rit\n"
                + "inner join tipificacion t on t.id = rit.id_tipificacion\n"
                + "where rit.codigo_inventario = substring(? FROM '^[A-Z0-9]+')";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, item);
                rs = ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar Combo", ex.toString(), "/get-tipificacion");
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
    public Response getPatios() {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        query = "select * from patios order by nombre_patio;";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                rs = ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar Combo", ex.toString(), "/get-patios");
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
    public Response getReporteRetanqueos(String json) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        JsonObject parameter = Util.readJsonObjectParameter(json);
        String filtroFechas = "";

        if (!parameter.get("fecha_ini").getAsString().equals("")) {
            filtroFechas = "and date(c.fecha_creacion) between '" + parameter.get("fecha_ini").getAsString() + "'::date and '" + parameter.get("fecha_fin").getAsString() + "'::date";
        }

        query = "select c.id,\n"
                + "	cc.nombre_caja ,\n"
                + "	c.descripcion,\n"
                + "	c.base_caja,\n"
                + "	c.saldo_anterior,\n"
                + "	c.valor as valor_retanqueado,\n"
                + "	c.nuevo_saldo as saldo_actual,\n"
                + "	c.fecha_creacion::timestamp(0),\n"
                + "	c.usuario_creador\n"
                + "from caja c\n"
                + "inner join configuracion_caja cc on cc.id = c.configuracion_caja_id  \n"
                + "where c.configuracion_caja_id = ? #FECHAS order by c.fecha_creacion desc";
        try {
            con = Util.conectarBD();
            if (con != null) {
                query = query.replaceAll("#FECHAS", filtroFechas);
                ps = con.prepareStatement(query);
                ps.setInt(1, parameter.get("configuracion_caja_id").getAsInt());
                rs = ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar retanqueos", ex.toString(), "/get-retanqueos");
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
    public Response getGastosCajasPendientes(String aprobado) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        query = "select gc.id, gc.fecha,\n"
                + "cc.nombre_caja,\n"
                + "descripcion,\n"
                + "gc.valor,\n"
                + "gc.usuario_creador,\n"
                + "gc.nit_tercero,\n"
                + "gc.tercero,\n"
                + "gc.descripcion,\n"
                + "gc.id_cuenta as tipo_gasto,\n"
                + "gc.configuracion_caja_id,\n"
                + "gc.aprobado \n"
                + "from gastos_caja gc  \n"
                + "inner join configuracion_caja cc on cc.id =gc.configuracion_caja_id \n"
                + "where gc.aprobado =? AND finalizado='N' order by gc.fecha desc";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, aprobado);
                rs = ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar Combo", ex.toString(), "/get-patios");
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
    public Response getOrdenesCargue(String json, Usuario usuario) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        String filtroPatio = "";
        String filtroFechas = "";
        String filtroCliente = "";

        JsonObject parameter = Util.readJsonObjectParameter(json);

        if (parameter != null) {
            if (!parameter.get("fecha_ini").getAsString().equals("")) {
                filtroFechas = "and date(sd.fecha_creacion) between '" + parameter.get("fecha_ini").getAsString() + "'::date and '" + parameter.get("fecha_fin").getAsString() + "'::date";
            }
            if (!parameter.get("patio").getAsString().equals("")) {
                filtroPatio = "and sd.patio = '" + parameter.get("patio").getAsString() + "'";
            }
            if (!parameter.get("cliente").getAsString().equals("")) {
                filtroCliente = "and sd.cliente_id = '" + parameter.get("cliente").getAsString() + "'";
            }
        }
        query = "select case when sd.aprobado = 'N' then 'PENDIENTE' \n"
                + "	when sd.aprobado = 'S' then 'APROBADO' \n"
                + "	when sd.aprobado = 'R' then 'RECHAZADO' \n"
                + "	when sd.aprobado = 'D' then 'CARGADO'\n"
                + "	when sd.aprobado = 'F' then 'FINALIZADO'\n"
                + "	when sd.aprobado = 'C' then 'CONCILIADO'"
                + "     when sd.aprobado = 'A' then 'ANULADO' end as estado, sd.aprobado, sd.descripcion,\n"
                + "sd.codigo_solicitud, sd.codigo_despacho, sd.codigo_remision,\n"
                + "sd.observacion, sd.cedula_conductor, sd.nombre_conductor,\n"
                + "sd.placa_trasportador, sd.peso_inicial, sd.peso_final,\n"
                + "sd.resolucion, sd.proveedor, sd.fecha_inicio_cargue,\n"
                + "sd.fecha_fin_cargue, p.nombre_patio, c.nombre, c.destino,\n"
                + "sd.peso_neto, sd.empresa_transportadora, sd.contrato, sd.propietario,\n"
                + "sd.peso_siderurgica, sd.anticipo, sd.pago, sd.siderurgica, sd.contenido\n"
                + "from solicitud_despacho sd\n"
                + "inner join patios p on p.id = sd.patio \n"
                + "inner join clientes c on c.id = sd.cliente_id\n"
                + "where case when ? = 'ALL' THEN aprobado in ('N','S','F','D','C','R','A') else aprobado = ? END AND sd.patio IN (SELECT id_patio  FROM rel_usuarios_patios rup WHERE rup.usuario= ?::varchar ) #FECHAS #PATIO #CLIENTE order by sd.codigo_solicitud desc;";
        try {
            con = Util.conectarBD();
            if (con != null) {
                query = query.replaceAll("#FECHAS", filtroFechas).replaceAll("#PATIO", filtroPatio).replaceAll("#CLIENTE", filtroCliente);
                ps = con.prepareStatement(query);
                ps.setString(1, parameter.get("aprobado").getAsString());
                ps.setString(2, parameter.get("aprobado").getAsString());
                ps.setString(3, usuario.getId_usuario());
                rs = ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar Combo", ex.toString(), "/get-patios");
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
    public Response guardarRemision(String json, Usuario usuario) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "SELECT crear_remision(?::json, ?::varchar) as resp";
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
            respObject = Util.writeResponseError("Guardar remision", ex.toString(), "/save-remision");
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
    public Response getTablaGen(String code) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        query = "select * from tabla_gen where tipo = ?;";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, code);
                rs = ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar Combo", ex.toString(), "/get-tabla-gen");
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
    public Response aprobarCaja(String json, Usuario usuario) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        JsonObject parameter = Util.readJsonObjectParameter(json);

        query = "UPDATE public.gastos_caja\n"
                + "SET  aprobado=?,\n"
                + "usuario_aprobacion=?,\n"
                + "fecha_aprobacion=now()\n"
                + "WHERE id=?;";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, parameter.get("estado").getAsString());
                ps.setString(2, usuario.getId_usuario());
                ps.setInt(3, parameter.get("id_gasto").getAsInt());
                if (ps.executeUpdate() > 0) {
                    respObject = Util.writreResponseString("OK");
                    response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            respObject = Util.writeResponseError("Aprobar caja", ex.toString(), "/aprobar-caja");
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
    public Response getConfiguracionCajas(Usuario usuario) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        query = "select id\n"
                + ",nombre_caja \n"
                + ",base_caja \n"
                + ",saldo_caja \n"
                + ",base_caja - saldo_caja as valor_retanquear \n"
                + ",prefijo\n"
                + ",centro_costo \n"
                + "from configuracion_caja \n"
                + "where id in (select configuracion_caja_id  from rel_usuario_cajas where usuario=?)\n"
                + "order by nombre_caja;";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, usuario.getId_usuario());
                rs = ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar Combo", ex.toString(), "/get-configuracion-cajas");
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
    public Response getReporteGastosCaja(String json) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        JsonObject parameter = Util.readJsonObjectParameter(json);
        query = "SELECT gc.id\n"
                + "fecha,\n"
                + "cc.id as configuracion_caja_id,\n"
                + "cc.nombre_caja,\n"
                + "gc.descripcion,\n"
                + "valor, \n"
                + "tercero,\n"
                + "nit_tercero,\n"
                + "factura,\n"
                + "gc.usuario_creador,\n"
                + "gc.fecha_creacion,\n"
                + "cc2.descripcion as tipo_gasto, \n"
                + "aprobado,\n"
                + "valor_final,\n"
                + "finalizado,\n"
                + "caja_id,\n"
                + "usuario_aprobacion,\n"
                + "fecha_aprobacion\n"
                + "FROM public.gastos_caja gc\n"
                + "inner join configuracion_caja cc on cc.id = gc.configuracion_caja_id  \n"
                + "inner join cuentas_caja cc2 on cc2.id =gc.id_cuenta \n"
                + "WHERE gc.fecha_creacion between ?::DATE and ?::DATE";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, parameter.get("fecha_ini").getAsString());
                ps.setString(2, parameter.get("fecha_fin").getAsString());
                rs = ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar reporte gastos", ex.toString(), "/get-reporte-gastos-caja");
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
    public Response getReporteGastosRetanqueo(int idCaja) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        query = "SELECT \n"
                + "fecha,\n"
                + "cc.id as configuracion_caja_id,\n"
                + "cc.nombre_caja,\n"
                + "gc.descripcion, gc.id,\n"
                + "valor, \n"
                + "tercero,\n"
                + "nit_tercero,\n"
                + "factura,\n"
                + "gc.usuario_creador,\n"
                + "gc.fecha_creacion,\n"
                + "cc2.descripcion as tipo_gasto, \n"
                + "aprobado,\n"
                + "valor_final,\n"
                + "finalizado,\n"
                + "caja_id,\n"
                + "usuario_aprobacion,\n"
                + "fecha_aprobacion,\n"
                + "importado\n"
                + "FROM public.gastos_caja gc\n"
                + "inner join configuracion_caja cc on cc.id = gc.configuracion_caja_id  \n"
                + "inner join cuentas_caja cc2 on cc2.id =gc.id_cuenta \n"
                + "WHERE caja_id =?;";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setInt(1, idCaja);
                rs = ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar Combo", ex.toString(), "/get-tabla-gen");
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
    public Response getDetalleGasto(int idGasto) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        query = "SELECT gc.id\n"
                + "fecha,\n"
                + "cc.id as configuracion_caja_id,\n"
                + "cc.nombre_caja,\n"
                + "gc.descripcion,\n"
                + "valor, \n"
                + "tercero,\n"
                + "nit_tercero,\n"
                + "factura,\n"
                + "gc.usuario_creador,\n"
                + "gc.fecha_creacion,\n"
                + "cc2.descripcion as tipo_gasto, \n"
                + "aprobado,\n"
                + "valor_final,\n"
                + "finalizado,\n"
                + "caja_id,\n"
                + "usuario_aprobacion,\n"
                + "fecha_aprobacion\n"
                + "FROM public.gastos_caja gc\n"
                + "inner join configuracion_caja cc on cc.id = gc.configuracion_caja_id  \n"
                + "inner join cuentas_caja cc2 on cc2.id =gc.id_cuenta \n"
                + "WHERE gc.id=?";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setInt(1, idGasto);
                rs = ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar reporte gastos", ex.toString(), "/get-reporte-gastos-caja");
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
    public Response configuracionCaja(String json, Usuario usuario, String action) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "SELECT admin_configuracion_cajas(?::json, ?::varchar, ?::varchar) as resp";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, json);
                ps.setString(2, action);
                ps.setString(3, usuario.getId_usuario());
                rs = ps.executeQuery();
                if (rs.next()) {
                    respObject = Util.writreResponseString(rs.getString("resp"));
                    response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
                }
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Admin cajas", ex.toString(), "/admin-cajas");
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
    public Response getAllConfiguracionCajas(Usuario usuario) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        query = "select id\n"
                + ",estado \n"
                + ",nombre_caja \n"
                + ",base_caja \n"
                + ",saldo_caja \n"
                + ",base_caja - saldo_caja as valor_retanquear \n"
                + ",prefijo\n"
                + ",centro_costo \n"
                + "from configuracion_caja \n"
                + "order by nombre_caja;";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, usuario.getId_usuario());
                rs = ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar Combo", ex.toString(), "/get-configuracion-cajas");
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
    public Response importGastosCaja(String json, Usuario usuario) {
        Response response = null;
        JsonObject respObject = null;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "SELECT row_to_json(t) as json FROM ( SELECT base_caja, saldo_caja,enviar_email,upper(nombre_caja) as nombre_caja,id_gasto,status "
                + "FROM public.importar_gastos_caja(?::json,?)"
                + "AS(base_caja NUMERIC,saldo_caja NUMERIC, enviar_email boolean, nombre_caja varchar, id_gasto int, status varchar) ) t;";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, json);
                ps.setString(2, usuario.getId_usuario());
                rs = ps.executeQuery();

                if (rs.next()) {

                    json = rs.getString("json");
                    JsonObject parameter = Util.readJsonObjectParameter(json);

                    if (parameter.get("enviar_email").getAsBoolean()) {
                        Util.sendEmail(parameter.get("nombre_caja").getAsString(), parameter.get("base_caja").getAsDouble(), parameter.get("saldo_caja").getAsDouble());
                    }
                }
                respObject = Util.getObjectJson(json);
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);

            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Guardar gastos caja", ex.getMessage(), "/save-gastos-caja");
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
    public Response deleteItemDetalleDespacho(String json, Usuario usuario) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        JsonObject parameter = Util.readJsonObjectParameter(json);

        query = "delete from solicitud_despacho_detalle where codigo_solicitud = ? and codigo_inventario = ?;";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, parameter.get("solicitud").getAsString());
                ps.setString(2, parameter.get("material").getAsString());
                if (ps.executeUpdate() > 0) {
                    respObject = Util.writreResponseString("OK");
                    response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            respObject = Util.writeResponseError("Eliminar material de solicitud", ex.toString(), "/eliminar-item-detalle-despacho");
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
    public Response crearCliente(String json, Usuario usuario) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "SELECT crear_cliente(?::json, ?::varchar) as resp";
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
            respObject = Util.writeResponseError("Guardar cliente", ex.toString(), "/crear-cliente");
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
    public Response getClientes(String tipo) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        query = "select * from clientes where tipo = ?;";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, tipo);
                rs = ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar reporte gastos", ex.toString(), "/get-reporte-gastos-caja");
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
    public Response getComponentesAll() {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        query = "select * from componentes_clase_articulos cca order by nombre_componente asc;";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                rs = ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar reporte gastos", ex.toString(), "/get-reporte-gastos-caja");
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
    public Response getOrdenesIngresoAll(String json) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        JsonObject parameter = Util.readJsonObjectParameter(json);

        query = "SELECT codigo_orden_ingreso, \n"
                + "                fecha,\n"
                + "                m.nombre_municipio,\n"
                + "                m.id as id_municipio, \n"
                + "                documento_tercero AS resolucion,\n"
                + "                fecha_resolucion, \n"
                + "                lote,\n"
                + "                p.nit::varchar AS nit_proveedor,\n"
                + "                p.razon_social AS proveedor,\n"
                + "                dependencia, \n"
                + "                a.nit::varchar AS autorizado ,\n"
                + "                a.nombre_completo as nombre_autorizado,\n"
                + "                a2.nombre_autorizacion AS autorizacion,\n"
                + "                a2.id as id_autorizacion,\n"
                + "                m2.nombre_motivo AS motivo,\n"
                + "                m2.id as id_motivo,\n"
                + "                u.nombres||' '||u.apellidos AS responsable1,\n"
                + "                u.usuario as id_responsable1,\n"
                + "                u2.nombres||' '||u2.apellidos AS responsable2,\n"
                + "                u2.usuario  as id_responsable2,\n"
                + "                oi.usuario_creador,\n"
                + "                oi.fecha_creacion::timestamp(0),\n"
                + "                oi.usuario_ultima_modificacion, \n"
                + "                oi.fecha_ultima_modificacion::timestamp(0),\n"
                + "                oi.id_cronograma,\n"
                + "                coalesce(oi.responsable_patio, '') as responsable_documental,\n"
                + "                oi.procesado,\n"
                + "                oi.valor_compra,\n"
                + "                validar_estado_orden_reporte(oi.codigo_orden_ingreso) as estado\n"
                + "                FROM orden_ingreso oi \n"
                + "                INNER JOIN municipio m ON m.id =oi.id_municipio \n"
                + "                INNER JOIN proveedor p ON p.nit =oi.nit_proveedor \n"
                + "                INNER JOIN autorizado a ON a.nit =oi.autorizado \n"
                + "                INNER JOIN autorizacion a2 ON a2.id =oi.autorizacion\n"
                + "                INNER JOIN motivo m2 ON m2.id =oi.motivo \n"
                + "                INNER JOIN usuarios u ON u.usuario =oi.responsable1 \n"
                + "                INNER JOIN usuarios u2 ON u2.usuario =oi.responsable2\n"
                + "                where date(oi.fecha_creacion) between ?::date and ?::date and oi.id_municipio=? \n"
                + "                and case when ? != '' then validar_estado_orden_reporte(oi.codigo_orden_ingreso)  in ('EN PROCESO','PENDIENTE') \n"
                + "                  when ? = 'PENDIENTE' then  validar_estado_orden_reporte(oi.codigo_orden_ingreso) in ('EN PROCESO','PENDIENTE') ELSE 1=1 END  \n"
                + "                and case when ? != '' then nit_proveedor::VARCHAR=? else 1=1 end \n"
                + "                ORDER BY fecha_creacion desc;";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, parameter.get("fecha_ini").getAsString());
                ps.setString(2, parameter.get("fecha_fin").getAsString());
                ps.setInt(3, parameter.get("id_municipio").getAsInt());
                ps.setString(4, parameter.get("estado").getAsString());
                ps.setString(5, parameter.get("estado").getAsString());
                // ps.setString(6, parameter.get("estado").getAsString());
                ps.setString(6, parameter.get("nit_proveedor").getAsString());
                ps.setString(7, parameter.get("nit_proveedor").getAsString());
                rs = ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar Combo", ex.toString(), "/get-patios");
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
    public Response certificacionFinalMasiva(String json, Usuario usuario) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "SELECT fecha_certificado,\n"
                + "placa,\n"
                + "resolucion,\n"
                + "carroceria,\n"
                + "tipo_servicio,\n"
                + "marca,\n"
                + "linea,\n"
                + "modelo,\n"
                + "color,\n"
                + "motor,\n"
                + "chasis,\n"
                + "serie,\n"
                + "capacidad,\n"
                + "motivo,\n"
                + "configuracion_ejes,\n"
                + "nit_propietario,\n"
                + "nombre_propietario,\n"
                + "codigo_certificado,\n"
                + "peso_bruto,\n"
                + "serial,\n"
                + "autorizacion_secretaria,\n"
                + "codigo_orden_ingreso,\n"
                + "codigo_identificacion,\n"
                + "id_detalle_orden_ingreso,\n"
                + "encontrado,\n"
                + "campo_encontrado,\n"
                + "fecha_creacion,\n"
                + "codigo_lote "
                + "from generara_certificacion_final_masivo(?::json, ?::varchar) as (fecha_certificado VARCHAR,\n"
                + "placa VARCHAR,\n"
                + "resolucion VARCHAR,\n"
                + "carroceria VARCHAR,\n"
                + "tipo_servicio VARCHAR,\n"
                + "marca VARCHAR,\n"
                + "linea VARCHAR,\n"
                + "modelo VARCHAR,\n"
                + "color VARCHAR,\n"
                + "motor VARCHAR,\n"
                + "chasis VARCHAR,\n"
                + "serie VARCHAR,\n"
                + "capacidad VARCHAR,\n"
                + "motivo VARCHAR,\n"
                + "configuracion_ejes VARCHAR,\n"
                + "nit_propietario VARCHAR,\n"
                + "nombre_propietario VARCHAR,\n"
                + "codigo_certificado VARCHAR,\n"
                + "peso_bruto VARCHAR,\n"
                + "serial VARCHAR,\n"
                + "autorizacion_secretaria VARCHAR,\n"
                + "codigo_orden_ingreso VARCHAR,\n"
                + "codigo_identificacion VARCHAR,\n"
                + "id_detalle_orden_ingreso INT,\n"
                + "encontrado BOOLEAN,\n"
                + "campo_encontrado VARCHAR,\n"
                + "fecha_creacion TIMESTAMP,\n"
                + "codigo_lote VARCHAR)";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, json);
                ps.setString(2, usuario.getId_usuario());
                rs = ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Certificacion final masiva", ex.toString(), "/certificacion-final-masiva");
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
    public Response getSolicitudeDespacho(String codigoSolicitud) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "SELECT sd.codigo_solicitud,\n"
                + "            sd.descripcion,\n"
                + "            sd.aprobado,\n"
                + "            sd.usuario_creador,\n"
                + "            sd.usuario_aprobacion,\n"
                + "            sd.observacion,\n"
                + "            sd.fecha_creacion,\n"
                + "            sd.fecha_aprobacion,\n"
                + "            sd.estado_cargue,\n"
                + "            sd.peso_inicial\n"
                + "            FROM public.solicitud_despacho sd\n"
                + "            WHERE codigo_solicitud = ?"
                + "            order by fecha_creacion desc";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, codigoSolicitud);
                rs = ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar solicitudes despacho", ex.toString(), "/get-solicitudes-despacho");
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
    public Response getTipificacionAll() {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        query = "select id::numeric as id_tipificacion, nombre_tipo from tipificacion t order by nombre_tipo asc;";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                rs = ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar reporte gastos", ex.toString(), "/get-reporte-gastos-caja");
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
    public Response guardarInventarioTipificacion(String json, Usuario usuario) {
        Response response = null;
        JsonObject respObject = null;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "SELECT public.crear_tipificacion(?::JSON,?) as resp";
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
            respObject = Util.writeResponseError("Guardar clase articulo", ex.toString(), "/save-clase-articulo");
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
    public Response getMateriales() {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        query = "select * from (\n"
                + "select nombre_componente as nombre_material, codigo_inventario from componentes_clase_articulos cca \n"
                + "union\n"
                + "select nombre_material as nombre_material, codigo_inventario from materiales_recuperados mr \n"
                + ") materiales order by nombre_material asc;";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                rs = ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar reporte gastos", ex.toString(), "/get-reporte-gastos-caja");
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
    public Response getInformeCertificados(String json) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        JsonObject parameter = Util.readJsonObjectParameter(json);
        String filtroFechas = "";

        if (!parameter.get("fecha_ini").getAsString().equals("")) {
            filtroFechas = "and date(oid2.fecha_certificado) between '" + parameter.get("fecha_ini").getAsString() + "'::date and '" + parameter.get("fecha_fin").getAsString() + "'::date";
        }

        query = "select codigo_certificado \n"
                + "from orden_ingreso_detalle oid2 where oid2.estado != 'A' and oid2.nit_propietario::varchar = ? #FECHAS ORDER BY codigo_certificado;";
        try {
            con = Util.conectarBD();
            if (con != null) {
                query = query.replaceAll("#FECHAS", filtroFechas);
                ps = con.prepareStatement(query);
                ps.setString(1, parameter.get("nit_proveedor").getAsString());
                rs = ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar solicitudes despacho", ex.toString(), "/get-solicitudes-despacho");
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
    public Response getPropietariosInforme() {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        query = "select distinct(nit_propietario), nombre_propietario \n"
                + "from orden_ingreso_detalle oid2 \n"
                + "where aprobado = 'S' and nit_propietario is not null order by nombre_propietario;";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                rs = ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar reporte gastos", ex.toString(), "/get-reporte-gastos-caja");
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
    public Response saveFletes(String json, Usuario usuario) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "SELECT crear_flete(?::json, ?::varchar) as resp";
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
            respObject = Util.writeResponseError("Guardar flete", ex.toString(), "/save-fletes");
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
    public Response getFletes(Usuario usuario, String json) {
        String filtroFechas = "";
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        JsonObject parameter = Util.readJsonObjectParameter(json);

        if (!parameter.get("fecha_ini").getAsString().equals("")) {
            filtroFechas = "and date(m.fecha_creacion) between '" + parameter.get("fecha_ini").getAsString() + "'::date and '" + parameter.get("fecha_fin").getAsString() + "'::date";
        }

        query = "SELECT f.*, case \n"
                + "when f.estado = 'A' THEN 'APROBADO' \n"
                + "when f.estado = 'N' THEN 'PENDIENTE' \n"
                + "when f.estado = 'F' THEN 'FINALIZADO' \n"
                + "when f.estado = 'R' THEN 'RECHAZADO' \n"
                + "end as estado_flete FROM FLETES f \n"
                + "WHERE case when ? = 'ALL' THEN estado in ('A','N','R','F') else estado = ? END\n"
                + "#FECHAS order by fecha_creacion;";
        try {
            con = Util.conectarBD();
            if (con != null) {
                query = query.replaceAll("#FECHAS", filtroFechas);
                ps = con.prepareStatement(query);
                ps.setString(1, parameter.get("estado").getAsString());
                ps.setString(2, parameter.get("estado").getAsString());
                rs = ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar fletes", ex.toString(), "/get-fletes");
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
    public Response getFletesDetalle(String codigoSolicitud) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        query = "select * from fletes_detalle where codigo_flete = ?";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, codigoSolicitud);
                rs = ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar fletes detalle", ex.toString(), "/get-fletes-detalle");
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
    public Response getFletesOrden() {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        query = "select * from orden_fletes where estado = 'P';";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                rs = ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar fletes orden", ex.toString(), "/get-fletes-orden");
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
    public Response getFletesOrdenDetalle(String codigoSolicitud) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        String json = "{}";
        query = "select json_build_object('data', json_agg(row_to_json(d)::json)) as json\n"
                + "from (\n"
                + "	select f.*, (\n"
                + "		select array_to_json(array_agg(row_to_json(d))) from (\n"
                + "			select fd.* from fletes_detalle fd\n"
                + "			where fd.codigo_flete = f.codigo_flete\n"
                + "		) d\n"
                + "	) as detalle from fletes f WHERE f.codigo_orden_ingreso=? \n"
                + "	order by f.valor_total\n"
                + ") d";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, codigoSolicitud);
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
    public Response getOrdenDetalle(String codigoOrden) {
        Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        query = "select ca.nombre_clase_articulo AS descripcion_articulo, count(*)\n"
                + "from orden_ingreso_detalle id\n"
                + "INNER JOIN clase_articulos ca ON ca.id= id.id_clase_articulos \n"
                + "where codigo_orden_ingreso = ? \n"
                + "group by nombre_clase_articulo;";
        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setString(1, codigoOrden);
                rs = ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            respObject = Util.writeResponseError("Cargar fletes orden detalle", ex.toString(), "/get-fletes-orden-detalle");
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
    public Response getCertificacionFinal(int tipo, Usuario usuario) {
         Response response = null;
        JsonObject respObject;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;

        query = "SELECT  \n"
                + "		d.id_detalle_orden_ingreso AS id_detalle_desintegracion,\n"
                + "		d.codigo_desintegracion, \n"
                + "		d.codigo_ingreso_local, \n"
                + "oi.codigo_orden_ingreso, \n"
                + "		d.tb, \n"
                + "		d.peso,\n"
                + "		d.porcentaje_descuento,\n"
                + "		d.total, \n"
                + "		d.fecha_certificado_sijin, \n"
                + "		d.certificado_sijin,\n"
                + "		d.fecha_certificado,\n"
                + "		d.numero_certificado,\n"
                + "		oi.nit_proveedor::varchar,\n"
                + "		p.razon_social AS proveedor,\n"
                + "		id.cod_identificacion AS resolucion,\n"
                + "		a.nombre_articulo,\n"
                + "		id.descripcion_articulo,\n"
                + "		id.cantidad,\n"
                + "		ca.id as id_clase_articulo, ca.nombre_clase_articulo, \n"
                + "		id.placa,\n"
                + "		id.motor,\n"
                + "		id.chasis,\n"
                + "		id.serie,\n"
                + "		id.marca,\n"
                + "		id.carroceria,\n"
                + "		id.linea,\n"
                + "		id.capacidad,\n"
                + "		id.peso_kg, \n"
                + "		id.peso_bruto, \n"
                + "		c.categoria,\n"
                + "		c2.clasificacion,\n"
                + "		id.modelo,\n"
                + "		id.color,\n"
                + "		id.motivo,\n"
                + "		id.configuracion_ejes,\n"
                + "		case when id.nit_propietario is null or id.nit_propietario = '' then oi.nit_proveedor::varchar else id.nit_propietario end as nit_propietario,\n"
                + "		case when id.nit_propietario is null or id.nit_propietario = '' then p.razon_social::varchar else id.nombre_propietario end as nombre_propietario,\n"
                + "		id.tipo_servicio,\n"
                + "		um.nombre_unidad_medida,\n"
                + "		d.procesado, \n"
                + "		d.paso_actual,\n"
                + "		d.usuario_creador,\n"
                + "		d.fecha_creacion,\n"
                + "		p.razon_social,\n"
                + "		id.codigo_certificado,\n"
                + "		oi.dependencia\n"
                + "FROM public.desintegracion d\n"
                + "INNER JOIN orden_ingreso_detalle id ON id.id=d.id_detalle_orden_ingreso and id.estado ='' \n"
                + "INNER JOIN orden_ingreso oi ON oi.codigo_orden_ingreso =id.codigo_orden_ingreso and oi.estado ='' \n"
                + "INNER JOIN proveedor p ON p.nit=oi.nit_proveedor \n"
                + "INNER JOIN articulos a ON a.id=id.id_articulo \n"
                + "INNER JOIN clase_articulos ca ON ca.id=id.id_clase_articulos\n"
                + "INNER JOIN categoria c ON c.id=id.id_categoria \n"
                + "INNER JOIN clasificacion c2 ON c2.id=id.id_clasificacion \n"
                + "INNER JOIN unidad_medida um ON um.id=id.unidad_medida \n"
                + "WHERE d.procesado ='N' AND paso_actual ='PASO 3' and id.id_categoria=? \n"
                + "AND id.patio_id IN (SELECT id_patio  FROM rel_usuarios_patios rup WHERE rup.usuario=?) order by codigo_desintegracion desc ";

        try {
            con = Util.conectarBD();
            if (con != null) {
                ps = con.prepareStatement(query);
                ps.setInt(1, tipo);
                ps.setString(2, usuario.getId_usuario());
                ps.executeQuery();
                respObject = Util.writreResponseArrayObject(Util.getQueryJsonArray(con, ps));
                response = Util.responseApi(Util.getJsonString(respObject), Response.Status.OK);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            respObject = Util.writeResponseError("Cargar Ordenes de Ingreso local finalizadas", ex.toString(), "/get-ingreso-local-finalizadas");
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
