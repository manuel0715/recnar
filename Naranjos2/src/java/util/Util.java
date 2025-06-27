/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;
import javax.ws.rs.core.Response;
import javax.xml.bind.DatatypeConverter;
/**
 *
 * @author usuario
 */
public class Util {
    
    public static final String SECRET_KEY = "oeRaYY7Wo24sDqKSX3IM9ASGmdGPmkTd9jo1QTy4b7P9Ze5_9hKolVX8xNrQDcNRfVEdTZNOMAuOyqEGhXEbdJI-ZQ19k_o9MI0y3eZN2lp9YELjoNUEw55FfXMiINEdt1XR85ViBORIpRLSOkT6kSpzs2x-jbLDiz9iFVzkESd8ES1YKxMgPCAGAA7VfZeQUm4n-mOmnWMaVX30zMARGFU4L3oPBctYKkl4dYfqYWqRNfrgPCAJVi5DGFjywgxx0ASEiJHtV72paI3fDR2XwlSkyhhmY-ICjCRmsJN4fX1pdoL8a18-aQrvyu4j0Os6dVPYIoPvvY0SAZtWYKHfM15g7A3HD4cVREf9cUsprCRK93w";
    
    
    public static JsonObject jsonResponseObject(String param,String response){
        JsonObject jsonObject = new JsonObject();        
        jsonObject.addProperty(param, response);      
        return jsonObject;   
    }
    
    public static JsonObject writreResponseString(String response) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("data", response);        
        return jsonObject;
    }
    
     public static JsonObject writreResponseJsonObject(JsonObject object) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("data", object);
        return jsonObject;
    }
     
     public static Response responseApi(String json,Response.Status status){
         return Response.status(status).entity(json).build();
    }
     
    public static String getJsonString(JsonObject jsonObject){
          return new Gson().toJson(jsonObject);
    }
    
     public static JsonObject readJsonObjectParameter(String json) {
        JsonObject jsonObject;
        try {
            JsonParser parser = new JsonParser();
            jsonObject = (JsonObject) parser.parse(json);

        } catch (JsonSyntaxException e) {
            jsonObject = null;
        } 
         return jsonObject;
    }
     
    public static JsonObject writeResponseError(String title, String detail, String source) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("title", title);
        if (isJson(detail)) {
              jsonObject.add("detail", getObjectJson(detail));
        } else {
            jsonObject.addProperty("detail", detail);
        }
        jsonObject.addProperty("source", source);
        return jsonObject;
    }
    
    private static boolean isJson(String json) {
        try {
            JsonParser parser = new JsonParser();
            parser.parse(json);
            return true;
        } catch (JsonSyntaxException e) {
            return false;
        }
    }
    
     public static JsonObject getObjectJson(String json) {
        JsonParser parser = new JsonParser();
        JsonObject o = parser.parse(json).getAsJsonObject();
        return o;
    }
     
     public static JsonArray getArrayJson(String json) {
        JsonParser parser = new JsonParser();
        JsonArray o = parser.parse(json).getAsJsonArray();
        return o;
    }
     
     public static Connection conectarBD() throws SQLException {
        Connection cn = null;
        String url = "jdbc:postgresql://localhost:5432/naranjos2";
        String user = "postgres";
        String password = "test";
        try {
            Class.forName("org.postgresql.Driver");
            cn = DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error de conexion" + e.getMessage());
        }
        return cn;
    }
     
     public static void desconectarBd(Connection con, ResultSet rs, PreparedStatement ps, Properties properties, Class c) throws SQLException {
        try {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (con != null && !(con.isClosed())) {
            con.close();
        }
        } catch (SQLException e) {
          
        }
    }
     
     public synchronized static JsonArray getQueryJsonArray(Connection con, PreparedStatement ps) throws SQLException {
        ResultSet rs=null;
        JsonArray lista = null;
        JsonObject jsonObject = null;
        if (con != null) {
            rs = ps.executeQuery();
            lista = new JsonArray();
            while (rs.next()) {
                jsonObject = new JsonObject();
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                     switch(rs.getMetaData().getColumnTypeName(i)){
                        case "numeric":
                            jsonObject.addProperty(rs.getMetaData().getColumnLabel(i), rs.getDouble(rs.getMetaData().getColumnLabel(i)));
                            break;
                        case "int4":
                            jsonObject.addProperty(rs.getMetaData().getColumnLabel(i), rs.getInt(rs.getMetaData().getColumnLabel(i)));
                            break;    
                        case "bool":
                            jsonObject.addProperty(rs.getMetaData().getColumnLabel(i), rs.getBoolean(rs.getMetaData().getColumnLabel(i)));
                            break;    
                        default:
                            jsonObject.addProperty(rs.getMetaData().getColumnLabel(i), rs.getString(rs.getMetaData().getColumnLabel(i)));
                    }
                   
                }
                lista.add(jsonObject);
            }
        }       
        if (rs != null)
          rs.close();
        
        return lista;
    }

     public static JsonObject writreResponseArrayObject(JsonArray arrayObject) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("data", arrayObject);
        return jsonObject;
    }
     
     
     
      public static JsonArray readJsonArrayParameter(String json) {
        JsonArray jsonArray = null;
        try {
            JsonParser parser = new JsonParser();
            jsonArray = (JsonArray) parser.parse(json);

        } catch (Exception e) {
            jsonArray = null;
        } finally {
            return jsonArray;
        }
    }
      
    public static JsonObject getQueryJsonObject(Connection con, PreparedStatement ps) throws SQLException {
        ResultSet rs=null;
        JsonObject jsonObject = null;
        if (con != null) {
            rs = ps.executeQuery(); 
            jsonObject = new JsonObject();
            if(rs.next()) {                
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    
                    //System.out.println("rs.getMetaData().getColumnLabel(i): "+rs.getMetaData().getColumnLabel(i)+" "+"rs.getMetaData().getColumnTypeName(i): "+rs.getMetaData().getColumnTypeName(i));
                    switch(rs.getMetaData().getColumnTypeName(i)){
                        case "numeric":
                            jsonObject.addProperty(rs.getMetaData().getColumnLabel(i), rs.getDouble(rs.getMetaData().getColumnLabel(i)));
                            break;
                        case "int4":
                            jsonObject.addProperty(rs.getMetaData().getColumnLabel(i), rs.getInt(rs.getMetaData().getColumnLabel(i)));
                            break;    
                        default:
                            jsonObject.addProperty(rs.getMetaData().getColumnLabel(i), rs.getString(rs.getMetaData().getColumnLabel(i)));
                    }
                    
                    
                }
            }
        }       
        if (rs != null)
          rs.close();
        
        return jsonObject;
    }

    public static boolean validateAuthentication(String jwt) throws ExpiredJwtException, MalformedJwtException,
            UnsupportedJwtException, IllegalArgumentException {
       
        String[] parts = jwt.split(Pattern.quote("."));
        String concatened = parts[0] + "." + parts[1];

        try {
            String signature = Jwts.parser()
                    .setSigningKey(DatatypeConverter.parseBase64Binary(SECRET_KEY))
                    .parseClaimsJws(jwt).getSignature();

            return signature.equals(parts[2]);
        } catch (SignatureException e) {
            return false;
        }
    }
    
  
    
    public static void desconectarBdUtil(Connection con, ResultSet rs, PreparedStatement ps, Properties properties, Class c) throws SQLException {
        try {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (con != null && !(con.isClosed())) {
            con.close();
        }
        } catch (SQLException e) {
          
        }
    }
    
    public static void sendEmail(String caja, double baseCaja,double saldoCaja){
        
        String host = "smtp.zoho.com"; // Cambia esto por tu servidor SMTP
        String from = "no-responder@recuperacionesnaranjo.com";
        String[] to = {"mjcamargo67@gmail.com",
                        "gerencia@recuperacionesnaranjo.com",
                        "auxiliarcontable@recuperacionesnaranjo.com",
                        "contabilidad@recuperacionesnaranjo.com"}; // Lista de destinatarios
        String subject = "Notificación Urgente: Retanqueo de Caja Requerido "+caja;
        String text = "Estimado usuario se debe realizar un retanqueo de caja para: "+caja+ "\n Saldo actual: "+saldoCaja+ "\nBase Caja: "+baseCaja;
        String pass = "1pvmQix*";

        // Crear y empezar el hilo para enviar el email
        EmailSender emailSender = new EmailSender(host, from, to, subject, text,pass);
        Thread thread = new Thread(emailSender);
        thread.start();
    }
    
    public static void sendNotificacionEmail(String codigo_orden_ingreso){
        
        String host = "smtp.zoho.com"; // Cambia esto por tu servidor SMTP
        String from = "no-responder@recuperacionesnaranjo.com";
        String[] to = { "gerencia@recuperacionesnaranjo.com",
                        "auxiliarcontable@recuperacionesnaranjo.com",
                        "auxiliar.proyectos@recuperacionesnaranjo.com",
                        "auditoriadesintegracion@recuperacionesnaranjo.com"};
        String subject = "Notificación ingreso local orden ingreso: "+codigo_orden_ingreso;
        String text = "Estimado usuario se debe realizar un retanqueo de caja para: ";
        String pass = "1pvmQix*";

        // Crear y empezar el hilo para enviar el email
        EmailSender emailSender = new EmailSender(host, from, to, subject, text,pass);
        Thread thread = new Thread(emailSender);
        thread.start();
    }
    
   
    public static void sendNotificacionEmail(String host, String from, String[] to, String subject, String text, String pass, Map<String, String> attachmentsBase64) {
    // Crear y empezar el hilo para enviar el email
    EmailNotifications emailSender = new EmailNotifications(host, from, to, subject, text, pass, attachmentsBase64);
    Thread thread = new Thread(emailSender);
    thread.start();
}
    

}
 
