/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package filter;

import com.google.gson.JsonObject;
import implement.ApiImpl;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.ext.Provider;
import util.Usuario;
import util.Util;

/**
 *
 * @author mcamargo
 */
@Provider
@Audited
public class JwtAuthenticationFilter implements ContainerRequestFilter {

    private static final String AUTH_HEADER = "Authentication";

    @Override
    public void filter(ContainerRequestContext requestContext) {
        String jwt = requestContext.getHeaderString(AUTH_HEADER);
        
        boolean sw = true;
        ResponseBuilder unauthorizedResponse = Response.status(Response.Status.UNAUTHORIZED)
                .header("Content-Type", "application/json");
        String message = "";

        try {
            if (jwt == null) {
                message = "Authentication header not found";
            } else if (!Util.validateAuthentication(jwt)) {
                message = "Invalid API token";
            } else {
                ApiImpl impl = new ApiImpl();
                Usuario user = impl.validarUserTokenJwT(impl.decodeJWT(jwt));
                if (user==null){
                sw=true;
                 message = "Expired API token!";
                }else{
                requestContext.setProperty("user", user);
                sw = false;
                }
            }
        } catch (ExpiredJwtException ex) {
            message = "Expired API token";
        } catch (UnsupportedJwtException e) {
            message = "Unsupported API token";
        } catch (MalformedJwtException e) {
            message = "Malformed API token";
        } catch (ArrayIndexOutOfBoundsException e) {
            message = "Invalid API token";
        }

        if (sw) {
            JsonObject jsonResponse = new JsonObject();
            jsonResponse.addProperty("msg", message);
            
            jsonResponse=Util.writeResponseError("Authentication", message, "/Authentication-Filter");
            requestContext.abortWith(
                    unauthorizedResponse.entity(Util.getJsonString(jsonResponse))
                            .build());
        }

    }
}
