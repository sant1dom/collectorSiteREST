package org.swa.security;

import java.util.UUID;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

@Path("auth")
public class AutenticazioneResource {

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response doLogin(@Context UriInfo uriinfo,
            //un altro modo per ricevere e iniettare i parametri con JAX-RS...
            @FormParam("username") String username,
            @FormParam("password") String password) {
        try {
            if (authenticate(username, password)) {
                /* per esempio */
                String authToken = issueToken(uriinfo, username);

                //return Response.ok(authToken).build();
                //return Response.ok().cookie(new NewCookie("token", authToken)).build();
                //return Response.ok().header(HttpHeaders.AUTHORIZATION, "Bearer " + authToken).build();
                //Restituiamolo in tutte le modalità, giusto per fare un esempio..
                return Response.ok(authToken)
                        .cookie(new NewCookie("token", authToken))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + authToken).build();
            } else {
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }

    @Logged
    @DELETE
    @Path("/logout")
    public Response doLogout(@Context HttpServletRequest request) {
        try {
            //estraiamo i dati inseriti dal nostro LoggedFilter...
            String token = (String) request.getAttribute("token");
            if (token != null) {
                revokeToken(token);
            }
            return Response.noContent().build();
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }

    private boolean authenticate(String username, String password) {
        /* autenticare! */
        return true;
    }

    private String issueToken(UriInfo context, String username) {
        /* registrare il token e associarlo all'utenza */
        String token = username + UUID.randomUUID().toString();
        /* per esempio */

//        JWT        
//        Key key = JWTHelpers.getInstance().getJwtKey();
//        String token = Jwts.builder()
//                .setSubject(username)
//                .setIssuer(context.getAbsolutePath().toString())
//                .setIssuedAt(new Date())
//                .setExpiration(Date.from(LocalDateTime.now().plusMinutes(15L).atZone(ZoneId.systemDefault()).toInstant()))
//                .signWith(key)
//                .compact();
        return token;
    }

    private void revokeToken(String token) {
        /* invalidate il token */
    }
}
