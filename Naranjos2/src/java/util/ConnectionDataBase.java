
/*
 * ConnectionDataBase.java
 * Esta clase es la clase Padre de todos los DAO's que usan el sistema de consultas 
 * 
 * @autor : usuario 
 * 
 */
package util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;
import java.util.logging.Level;
import javax.naming.*;
import java.util.logging.Logger;

/**
 * Clase Padre de todos los DAO's que usan el sistema de lectura de sentencias
 *
 * @author
 *
 */
public class ConnectionDataBase {

    public ConnectionDataBase() {
    }

    private Logger logger;
    
    private static String XML_PATH;


    /**
     * El objeto que contiene la fuente de datos
     */
    private DataSource dataSource;

    /**
     * Se encarga de controlar el acceso al archivo XML especificado
     */

    /**
     * La ruta del directorio donde se encuentran los archivos XML para realizar
     * las consultas SQL.
     */
    private String url;
    /**
     * El nombre del archivo XML donde se encuentran guardadas las consultas SQL
     */
    private String xmlFile;
    /*
     * Bandera para determinar cuando es necesario validar el archivo XML y no
     * caer en validaciones repetitivas e innecesarias
     */
    private boolean esNecesarioValidarXML = false;

    /**
     * Permite crear una instancia de la clase MainDAO
     *
     * @return 
     * @throws java.sql.SQLException
     */
    
    public synchronized Connection connectionJNDI() throws SQLException {
        try {
        //    System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.naming.java.javaURLContextFactory");
            Context contextoInicial = new InitialContext(); 
            dataSource = (DataSource) contextoInicial.lookup("java:/comp/env/jdbc/interdiseno");
            Connection con = (Connection) dataSource.getConnection();
            if ((con == null) || con.isClosed()) {
                throw new SQLException("No hay conexion a la base datos: Finexus");
            }
            return con;
        } catch (NamingException e) {
            System.out.println(e.getCause());
        } catch (SQLException ex) {
            this.connectionJNDI();
        }
        return null;
    }


    /**
     * Permite liberar la conexi�n asociada al nombre de la consulta dada
     *
     * @param con El objeto que contiene la conexion a la base de datos.
     * @param rs
     * @param ps
     * @param c
     */
    public synchronized void desconectarPool(Connection con, ResultSet rs, PreparedStatement ps, Class c) {
        try {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (con != null) {
                this.desconectar(con);
            }
        } catch (SQLException e) {
            logger.log(Level.INFO, "{0}: {1}", new Object[]{c.getName(), e.getMessage()});
        }
    }
    
    public synchronized void desconectar(Connection con) throws SQLException {

        if (con != null && !(con.isClosed())) {
            con.close();
        }
    }


    public  Connection getConnectionJdbc(){
        Connection conn=null;
        try {
            Class.forName("org.postgresql.Driver");
            String dbURL = "jdbc:postgresql://localhost:5432/interdisenio";
            String user = "postgres";
            String pass = "test";
            conn = DriverManager.getConnection(dbURL, user, pass);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ConnectionDataBase.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(ConnectionDataBase.class.getName()).log(Level.SEVERE, null, ex);
        }        
        return conn;    
    }
    
    public static Connection conectarBD() throws SQLException {
        Connection cn = null;
        String url = "jdbc:postgresql://localhost:5432/interdisenio";
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
    
       /**
     * Retorna un String completo con la cadena del error
     * @param e
     * @return
     */
    public  String printStackTrace(Exception e) {
        StringWriter sWriter = new StringWriter();
        PrintWriter pWriter = new PrintWriter(sWriter);
        e.printStackTrace(pWriter);
        return sWriter.toString();
    }
       /**
     * Retorna un String completo con la cadena del error
     * @param e
     * @return
     */
    public  String printStackTrace(SQLException e) {
        StringWriter sWriter = new StringWriter();
        PrintWriter pWriter = new PrintWriter(sWriter);
        e.printStackTrace(pWriter);
        return sWriter.toString();
    }

}
