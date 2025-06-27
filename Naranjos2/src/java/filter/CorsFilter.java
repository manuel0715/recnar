/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.ext.Provider;

/**
 *
 * @author usuario
 */
@Provider
@PreMatching

public class CorsFilter implements ContainerResponseFilter{

    @Override
    @LoggerAspect
   public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        responseContext.getHeaders().putSingle("Access-Control-Allow-Origin", "*");
        responseContext.getHeaders().putSingle("Access-Control-Allow-Credentials", "true");
        responseContext.getHeaders().putSingle("Access-Control-Allow-Methods", "*");
        responseContext.getHeaders().putSingle("Access-Control-Allow-Headers", "*");
        List<String> reqHead = requestContext.getHeaders().get("Access-Control-Request-Headers");
        if (null != reqHead) {
            responseContext.getHeaders().put("Access-Control-Allow-Headers", new ArrayList<Object>(reqHead));
        }
    }
    
}
