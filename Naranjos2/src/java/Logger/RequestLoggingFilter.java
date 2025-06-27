/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Logger;

/**
 *
 * @author Administrator
 */
/*import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;*/
import java.io.IOException;
import java.util.Date;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;

@Logged
@Provider
public class RequestLoggingFilter implements ContainerRequestFilter {
   // private static final Logger LOG = LogManager.getLogger(RequestLoggingFilter.class);
    
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
       
        System.out.println("==Request message==");
        System.out.println("Date " + new Date());
        System.out.println("request " + IOUtils.toString(requestContext.getEntityStream(), Charsets.UTF_8));
        System.out.println("method " + requestContext.getMethod());;
    }
}