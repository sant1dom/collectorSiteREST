package org.swa.collectorsite.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import java.sql.*;
import java.util.UUID;
import java.util.logging.Logger;

@Path("auth")
public class AutenticazioneResource {

    Class c = Class.forName("com.mysql.jdbc.Driver");
    Connection con = DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/collector_site?noAccessToProcedureBodies=true&serverTimezone=Europe/Rome", "collectorsite", "Collectorsite0@");

    public AutenticazioneResource() throws ClassNotFoundException, SQLException {
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response doLogin(@Context UriInfo uriinfo,
                            //un altro modo per ricevere e iniettare i parametri con JAX-RS...
                            @FormParam("username") String username,
                            @FormParam("password") String password) {
        try {
            int utente_id = authenticate(username, password);
            if (utente_id > 0) {

                String authToken = issueToken(uriinfo, username);
                try (PreparedStatement stmt = con.prepareStatement("UPDATE utente_rest SET token = ? WHERE id = ?")) {
                    stmt.setString(1, authToken);
                    stmt.setInt(2, utente_id);
                    stmt.executeUpdate();
                }
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

    private int authenticate(String username, String password) {
        try (PreparedStatement stmt = con.prepareStatement("SELECT id FROM utente WHERE username = ? AND password = ?")) {
            stmt.setString(1, username);
            stmt.setString(2, Encryption.encryptPassword(password));
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            return 0;
        }
    }

    private String issueToken(UriInfo context, String username) {
        /* registrare il token e associarlo all'utenza */

        return UUID.randomUUID().toString();
    }

    private void revokeToken(String token) {
        try (PreparedStatement stmt = con.prepareStatement("UPDATE utente_rest SET token = NULL WHERE token = ?")) {
            stmt.setString(1, token);
            stmt.executeUpdate();
        } catch (SQLException e) {
            Logger.getLogger(AutenticazioneResource.class.getName()).severe(e.getMessage());
        }
    }
}

