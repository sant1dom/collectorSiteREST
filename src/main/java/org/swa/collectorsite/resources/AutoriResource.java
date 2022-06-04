package org.swa.collectorsite.resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.swa.collectorsite.RESTWebApplicationException;
import org.swa.collectorsite.model.TipologiaAutore;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("autori")
public class AutoriResource {

    Class c = Class.forName("com.mysql.jdbc.Driver");
    Connection con = DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/collector_site?noAccessToProcedureBodies=true&serverTimezone=Europe/Rome", "collectorsite","Collectorsite0@");

    public AutoriResource() throws SQLException, ClassNotFoundException {
    }

    private Map<String, Object> createAutore(ResultSet rs) {
        try {
            Map<String, Object> autore = new HashMap<>();
            autore.put("id",rs.getInt("id"));
            autore.put("nome",rs.getString("nome"));
            autore.put("cognome", rs.getString("cognome"));
            autore.put("nome_artistico", rs.getString("nome_artistico"));
            autore.put("tipologia_autore",TipologiaAutore.valueOf(rs.getString("tipologia_autore")));
            return autore;
        } catch (SQLException ex) {
            throw new RESTWebApplicationException(ex);
        }
    }
    // Operazione 8
    @GET
    @Produces("application/json")
    public Response getAutori(@Context UriInfo uriinfo) throws SQLException {
        PreparedStatement sAutori = con.prepareStatement("SELECT * FROM autore");
        List<Map<String, Object>> autori = new ArrayList<>();
        try (ResultSet rs = sAutori.executeQuery()) {
            while (rs.next()) {
                autori.add(createAutore(rs));
            }
        } catch (SQLException e) {
            throw new RESTWebApplicationException(e);
        }
        return Response.ok(autori).build();
    }
}
