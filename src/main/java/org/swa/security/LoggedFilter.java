package org.swa.security;

import java.io.IOException;
import java.security.Principal;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.Provider;

@Provider
@Logged
@Priority(Priorities.AUTHENTICATION)
public class LoggedFilter implements ContainerRequestFilter {

    @Context
    UriInfo uriInfo;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {        
        String token = null;
        final String path = requestContext.getUriInfo().getAbsolutePath().toString();
        
        //come esempio, proviamo a cercare il token in vari punti, in ordine di priorità
        //in un'applicazione reale, potremmo scegliere una sola modalità
        String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring("Bearer".length()).trim();
        } else if (requestContext.getCookies().containsKey("token")) {
            token = requestContext.getCookies().get("token").getValue();
        } else if (requestContext.getUriInfo().getQueryParameters().containsKey("token")) {
            token = requestContext.getUriInfo().getQueryParameters().getFirst("token");
        }
        if (token != null && !token.isEmpty()) {
            try {
                //validiamo il token
                final String user = validateToken(token);
                if (user != null) {
                    //inseriamo nel contesto i risultati dell'autenticazione
                    //per farli usare dai nostri metodi restful
                    //iniettando @Context ContainerRequestContext
                    requestContext.setProperty("token", token);
                    requestContext.setProperty("user", user);
                    //OPPURE
                    // https://dzone.com/articles/custom-security-context-injax-rs
                    //mettiamo i dati anche nel securitycontext standard di JAXRS...
                    //che può essere iniettato con @Context SecurityContext nei metodi
                    //final SecurityContext originalSecurityContext = requestContext.getSecurityContext();
                    requestContext.setSecurityContext(new SecurityContext() {
                        @Override
                        public Principal getUserPrincipal() {
                            return new Principal() {
                                @Override
                                public String getName() {
                                    return user;
                                }
                            };
                        }
                        @Override
                        public boolean isUserInRole(String role) {
                            //qui andrebbe verificato se l'utente ha il ruolo richiesto
                            return true;
                        }
                        @Override
                        public boolean isSecure() {                            
                            return path.startsWith("https");
                        }
                        @Override
                        public String getAuthenticationScheme() {
                            return "Token-Based-Auth-Scheme";
                        }
                    });

                } else {
                    //se non va bene... 
                    requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
                }
            } catch (Exception e) {
                requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
            }
        } else {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
        }
    }

    private String validateToken(String token) {
//      //JWT                
//      Key key = AppGlobals.getInstance().getJwtKey();
//      Jws<Claims> jwsc = Jwts.parser().setSigningKey(key).parseClaimsJws(token);
//      return jwsc.getBody().getSubject();
        return "pippo"; //andrebbe derivato dal token!
    }

}
