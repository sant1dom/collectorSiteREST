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
        PreparedStatement sDischi = con.prepareStatement("SELECT disco.id FROM disco JOIN collezione_disco cd on disco.id = cd.disco_id JOIN collezione c on cd.collezione_id = c.id WHERE c.privacy = 'PUBBLICA'ORDER BY disco.id");
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
    public Response getDisco(@PathParam("id") int id, @Context UriInfo uriInfo) throws SQLException {
        try(PreparedStatement sDischi = con.prepareStatement("SELECT * FROM disco WHERE id = ?")){
            sDischi.setInt(1, id);
            try (ResultSet rs = sDischi.executeQuery()) {
                if (rs.next()) {
                    var disco = createDisco(rs);
                    disco.put("padre", uriInfo.getBaseUriBuilder()
                            .path(DischiResource.class)
                            .path(DischiResource.class, "getDisco")
                            .build(rs.getInt("padre")).toString());
                    var autori = new ArrayList<>();
                    try(PreparedStatement sAutori = con.prepareStatement("SELECT * FROM disco_autore WHERE disco_id = ?")){
                        sAutori.setInt(1, id);
                        try (ResultSet rsAutori = sAutori.executeQuery()) {
                            while (rsAutori.next()) {
                                autori.add(uriInfo.getBaseUriBuilder()
                                        .path(AutoriResource.class)
                                        .path(AutoriResource.class, "getAutore")
                                        .build(rsAutori.getInt("autore_id")).toString());
                            }
                        }
                    }
                    disco.put("autori", autori);
                    return Response.ok(disco).build();
                } else {
                    return Response.status(Response.Status.NOT_FOUND).build();
                }
            } catch (SQLException e) {
                throw new RESTWebApplicationException(e);
            }
        }
    }

    @GET
    @Path("{id}/autori")
    @Produces("application/json")
    public Response getAutori(@PathParam("id") int id, @Context UriInfo uriInfo) throws SQLException {
        try(PreparedStatement sAutori = con.prepareStatement("SELECT * FROM disco_autore WHERE disco_id = ?")){
            sAutori.setInt(1, id);
            try (ResultSet rsAutori = sAutori.executeQuery()) {
                List<String> autori = new ArrayList<>();
                while (rsAutori.next()) {
                    autori.add(uriInfo.getBaseUriBuilder()
                            .path(AutoriResource.class)
                            .path(AutoriResource.class, "getAutore")
                            .build(rsAutori.getInt("autore_id")).toString());
                }
                return Response.ok(autori).build();
            } catch (SQLException e) {
                throw new RESTWebApplicationException(e);
            }
        }
    }
}
