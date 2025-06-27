/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import aspect.BaseAspect;
import aspect.LoggingAspect;
import com.fasterxml.jackson.databind.ObjectMapper;
import filter.LoggerAspect;
import java.util.Set;
import javax.ws.rs.core.Application;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

/**
 *
 * @author mcamargo
 */
@javax.ws.rs.ApplicationPath("webresources")
@LoggerAspect
public class ApplicationConfig extends Application {
    
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
         addRestResourceClasses(resources);
        resources.add(MultiPartFeature.class);
        return resources;
    }

    /**
     * Do not modify addRestResourceClasses() method.
     * It is automatically populated with
     * all resources defined in the project.
     * If required, comment out calling this method in getClasses().
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(Controller.ApiResource.class);
        resources.add(Controller.ComprasResource.class);
        resources.add(Controller.MantenimientoResource.class);
        resources.add(Controller.PuntoVerdeResource.class);
        resources.add(Controller.RrhhResource.class);
        resources.add(Logger.RequestLoggingFilter.class);
        resources.add(Logger.ResponseLoggingFilter.class);
        resources.add(aspect.LoggingAspect.class);
        resources.add(filter.CorsFilter.class);
        resources.add(filter.JwtAuthenticationFilter.class);
    }
    
}
