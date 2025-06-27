/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Logger;



import java.io.IOException;
import java.util.Date;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;



@Logged
@Provider
public class ResponseLoggingFilter implements ContainerResponseFilter {
//    private static final Logger LOG = LogManager.getLogger(ResponseLoggingFilter.class);
    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        
        
        
        System.out.println("==response message==");
        System.out.println("Date: " + new Date());
        System.out.println("Response: " + responseContext.getEntity());
        System.out.println("Status: " + responseContext.getStatus());
        System.out.println("Media type: " + responseContext.getMediaType());
    }
}
