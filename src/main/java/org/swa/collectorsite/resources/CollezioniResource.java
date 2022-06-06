package org.swa.collectorsite.resources;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.swa.collectorsite.RESTWebApplicationException;

import java.net.URI;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Path("collezioni")
public class CollezioniResource {
    Class c = Class.forName("com.mysql.jdbc.Driver");
    Connection con = DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/collector_site?noAccessToProcedureBodies=true&serverTimezone=Europe/Rome", "collectorsite","Collectorsite0@");

    public CollezioniResource() throws SQLException, ClassNotFoundException {
        // Auto-generated constructor stub
    }

    static Map<String, Object> createCollezione(ResultSet rs) {
        try {
            Map<String, Object> collezione = new LinkedHashMap<>();
            collezione.put("id",rs.getInt("id"));
            collezione.put("titolo",rs.getString("titolo"));
            collezione.put("privacy", rs.getString("privacy"));
            collezione.put("data_creazione", rs.getDate("data_creazione"));
            return collezione;
        } catch (SQLException ex) {
            throw new RESTWebApplicationException(ex);
        }
    }

    // Operazione 2
    @GET
    @Path("{username}")
    public Response getCollezioniPrivateUtente(@PathParam("username") String username){
        List<Map<String, Object>> collezioni = new ArrayList<>();
        try(PreparedStatement stmt = con.prepareStatement("SELECT * FROM collezione WHERE utente_id = (SELECT id FROM utente WHERE username = ?)")){
            return createCollezioni(username, collezioni, stmt);
        }
        catch (SQLException ex) {
            throw new RESTWebApplicationException(ex);
        }
    }

    // Operazione 3
    @GET
    @Path("{username}/condivise")
    public Response getCollezioniCondiviseUtente(@PathParam("username") String username){
        List<Map<String, Object>> collezioni = new ArrayList<>();
        try(PreparedStatement stmt = con.prepareStatement("SELECT * FROM collezione JOIN collezione_condivisa_con ccc on collezione.id = ccc.collezione_id WHERE ccc.utente_id = (SELECT id FROM utente WHERE username = ?)")){
            return createCollezioni(username, collezioni, stmt);
        }
        catch (SQLException ex) {
            throw new RESTWebApplicationException(ex);
        }
    }
    // Operazione 4
    @GET
    @Path("{id_collezione}")
    public Response getCollezione(@Context UriInfo uriinfo,@PathParam("id_collezione") int id_collezione){
        try(PreparedStatement stmt = con.prepareStatement("SELECT * FROM collezione WHERE id = ?")){
            stmt.setInt(1, id_collezione);
            try(ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    Map<String, Object> collezione = createCollezione(rs);
                    URI uri = uriinfo.getBaseUriBuilder().path("collezioni/"+id_collezione+"/dischi").build();
                    collezione.put("dischi", uri.toString());
                    return Response.ok(collezione).build();
                }
                else{
                    return Response.status(Response.Status.NOT_FOUND).build();
                }
            }
        }
        catch (SQLException ex) {
            throw new RESTWebApplicationException(ex);
        }
    }

    @GET
    @Path("{id_collezione}/dischi")
    public Response getDischiCollezione(@PathParam("id_collezione") int id_collezione){
        List<Map<String, Object>> dischi = new ArrayList<>();
        try(PreparedStatement stmt = con.prepareStatement("SELECT * FROM disco JOIN collezione_disco cd on disco.id = cd.disco_id WHERE cd.collezione_id = ?")){
            stmt.setInt(1, id_collezione);
            try(ResultSet rs = stmt.executeQuery()){
                while(rs.next()){
                    dischi.add(DischiResource.createDisco(rs));
                }
                if (dischi.isEmpty()){
                    return Response.status(Response.Status.NOT_FOUND).build();
                }
                else{
                    return Response.ok(dischi).build();
                }
            }
        }
        catch (SQLException ex) {
            throw new RESTWebApplicationException(ex);
        }
    }

    // Operazione 5
    @GET
    @Path("{id_collezione}/dischi/{id_disco}")
    public Response getDiscoCollezione(@PathParam("id_collezione") int id_collezione, @PathParam("id_disco") int id_disco){
        try(PreparedStatement stmt = con.prepareStatement("SELECT * FROM disco JOIN collezione_disco cd on disco.id = cd.disco_id WHERE cd.collezione_id = ? AND cd.disco_id = ?")){
            stmt.setInt(1, id_collezione);
            stmt.setInt(2, id_disco);
            try(ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    return Response.ok(DischiResource.createDisco(rs)).build();
                }
                else{
                    return Response.status(Response.Status.NOT_FOUND).build();
                }
            }
        }
        catch (SQLException ex) {
            throw new RESTWebApplicationException(ex);
        }
    }


    private Response createCollezioni(String username, List<Map<String, Object>> collezioni, PreparedStatement stmt) throws SQLException {
        stmt.setString(1, username);
        try(ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                collezioni.add(createCollezione(rs));
            }
        }
        if (collezioni.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            return Response.ok(collezioni).build();
        }
    }
}
