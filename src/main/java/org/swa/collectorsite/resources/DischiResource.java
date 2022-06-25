package org.swa.collectorsite.resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.swa.collectorsite.RESTWebApplicationException;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Path("dischi")
public class DischiResource {
    Class c = Class.forName("com.mysql.jdbc.Driver");
    Connection con = DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/collector_site?noAccessToProcedureBodies=true&serverTimezone=Europe/Rome", "collectorsite","Collectorsite0@");

    public DischiResource() throws SQLException, ClassNotFoundException {
        // Auto-generated constructor stub
    }

    static Map<String, Object> createDisco(ResultSet rs) {
        try {
            Map<String, Object> disco = new LinkedHashMap<>();
            disco.put("id",rs.getInt("id"));
            disco.put("titolo",rs.getString("titolo"));
            disco.put("anno", rs.getInt("anno"));
            disco.put("etichetta", rs.getString("etichetta"));
            disco.put("barcode", rs.getString("barcode"));
            disco.put("genere", rs.getString("genere"));
            disco.put("stato_conservazione",rs.getString("stato_conservazione"));
            disco.put("formato",rs.getString("formato"));
            disco.put("data_inserimento",rs.getDate("data_inserimento"));
            return disco;
        } catch (SQLException ex) {
            throw new RESTWebApplicationException(ex);
        }
    }

    @GET
    @Produces("application/json")
    public Response getDischi(@Context UriInfo uriinfo) throws SQLException {
        PreparedStatement sDischi = con.prepareStatement("SELECT disco.id FROM disco JOIN collezione_disco cd on disco.id = cd.disco_id JOIN collezione c on cd.collezione_id = c.id WHERE c.privacy = 'PUBBLICA'");
        List<String> dischi = new ArrayList<>();
        try (ResultSet rs = sDischi.executeQuery()) {
            while (rs.next()) {
                dischi.add(uriinfo.getBaseUriBuilder()
                        .path(DischiResource.class)
                        .path(DischiResource.class, "getDisco")
                        .build(rs.getInt("id")).toString());
            }
        } catch (SQLException e) {
            throw new RESTWebApplicationException(e);
        }
        return Response.ok(dischi).build();
    }

    @GET
    @Path("{id}")
    @Produces("application/json")
    public Response getDisco(@PathParam("id") int id) throws SQLException {
        try(PreparedStatement sDischi = con.prepareStatement("SELECT * FROM disco WHERE id = ?")){
            sDischi.setInt(1, id);
            try (ResultSet rs = sDischi.executeQuery()) {
                if (rs.next()) {
                    return Response.ok(createDisco(rs)).build();
                } else {
                    return Response.status(Response.Status.NOT_FOUND).build();
                }
            } catch (SQLException e) {
                throw new RESTWebApplicationException(e);
            }
        }
    }
}
