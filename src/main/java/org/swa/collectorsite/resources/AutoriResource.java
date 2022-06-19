package org.swa.collectorsite.resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.swa.collectorsite.RESTWebApplicationException;
import org.swa.collectorsite.model.TipologiaAutore;

import java.sql.*;
import java.util.*;

@Path("autori")
public class AutoriResource {

    Class c = Class.forName("com.mysql.jdbc.Driver");
    Connection con = DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/collector_site?noAccessToProcedureBodies=true&serverTimezone=Europe/Rome", "collectorsite","Collectorsite0@");

    public AutoriResource() throws SQLException, ClassNotFoundException {
        // Auto-generated constructor stub
    }

    static Map<String,   Object> createAutore(ResultSet rs) {
        try {
            Map<String, Object> autore = new LinkedHashMap<>();
            autore.put("id",rs.getInt("id"));
            autore.put("nome",rs.getString("nome"));
            autore.put("cognome", rs.getString("cognome"));
            autore.put("nome_artistico", rs.getString("nome_artistico"));
            autore.put("tipologia_autore",rs.getString("tipologia_autore"));
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

    // Operazione 9
    @GET
    @Path("{id}")
    @Produces("application/json")
    public Response getAutore(@Context UriInfo uriinfo, @PathParam("id") int id) throws SQLException {
        try(PreparedStatement sAutore = con.prepareStatement("SELECT * FROM autore WHERE id = ?")){
            sAutore.setInt(1, id);
            try(ResultSet rs = sAutore.executeQuery()){
                if(rs.next()){
                    return Response.ok(createAutore(rs)).build();
                } else {
                    return Response.status(Response.Status.NOT_FOUND).build();
                }
            }
            catch (SQLException e) {
                throw new RESTWebApplicationException(e);
            }
        }
    }

    @GET
    @Path("{id}/dischi")
    @Produces("application/json")
    public Response getDischiByAutore(@PathParam("id") int id) throws SQLException {
        try(PreparedStatement sAutore = con.prepareStatement("SELECT * FROM disco JOIN autore_disco ON disco.id = autore_disco.id_disco WHERE autore_disco.id_autore = ?")){
            sAutore.setInt(1, id);
            try(ResultSet rs = sAutore.executeQuery()){
                List<Map<String, Object>> dischi = new ArrayList<>();
                while (rs.next()) {
                    dischi.add(DischiResource.createDisco(rs));
                }
                if (dischi.isEmpty()) {
                    return Response.status(Response.Status.NOT_FOUND).build();
                } else {
                    return Response.ok(dischi).build();
                }
            }
            catch (SQLException e) {
                throw new RESTWebApplicationException(e);
            }
        }
    }
}