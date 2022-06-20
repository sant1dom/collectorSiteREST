package org.swa.collectorsite.resources;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.swa.collectorsite.RESTWebApplicationException;
import org.swa.collectorsite.model.Disco;

import java.net.URI;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;

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
            collezione.put("data_creazione", new SimpleDateFormat("dd/MM/yyyy").format(rs.getDate("data_creazione")));
            return collezione;
        } catch (SQLException ex) {
            throw new RESTWebApplicationException(ex);
        }
    }

    // Operazione 2
    @GET
    @Path("{username}/all")
    @Produces("application/json")
    public Response getCollezioniUtente(@PathParam("username") String username){
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
    @Produces("application/json")
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
    @Produces("application/json")
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
    @Produces("application/json")
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
    @Produces("application/json")
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

    //Operazione 7
    @GET
    @Produces("application/json")
    @Path("dischi")
    public Response ricercaDiscoCollezioniPubbliche(@QueryParam("titolo") String titolo,
                                                    @QueryParam("anno") int anno,
                                                    @QueryParam("genere") String genere,
                                                    @QueryParam("formato") String formato,
                                                    @QueryParam("autore") String autore){
        StringBuilder query;
        if(autore != null){
            query = new StringBuilder("SELECT * FROM disco JOIN collezione_disco cd on disco.id = cd.disco_id JOIN disco_autore ad on disco.id = ad.disco_id JOIN autore a on ad.autore_id = a.id WHERE cd.collezione_id IN (SELECT id FROM collezione WHERE privacy = 'PUBBLICO') AND titolo LIKE ? AND genere LIKE ? AND formato LIKE ? AND a.nome_artistico LIKE ?");
        } else {
            query = new StringBuilder("SELECT * FROM disco JOIN collezione_disco cd on disco.id = cd.disco_id WHERE cd.collezione_id IN (SELECT id FROM collezione WHERE privacy = 'PUBBLICO') AND titolo LIKE ? AND genere LIKE ? AND formato LIKE ?");
        }
        if(anno != 0){
            query.append(" AND anno = ?");
        }
        try(PreparedStatement stmt = con.prepareStatement(query.toString())){
            stmt.setString(1, "%"+((titolo!=null)?titolo:"")+"%");
            stmt.setString(2, "%"+((genere!=null)?genere:"")+"%");
            stmt.setString(3, "%"+((formato!=null)?formato:"")+"%");
            if(autore != null){
                stmt.setString(4, "%"+autore+"%");
                if(anno != 0){
                    stmt.setInt(5, anno);
                }
            } else {
                if(anno != 0){
                    stmt.setInt(4, anno);
                }
            }
            try(ResultSet rs = stmt.executeQuery()){
                List<Map<String, Object>> dischi = new ArrayList<>();
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

    @GET
    @Produces("application/json")
    @Path("{username}/condivise/dischi")
    public Response ricercaDischiCondivisi(@PathParam("username") String username,
                                       @QueryParam("titolo") String titolo,
                                       @QueryParam("anno") int anno,
                                       @QueryParam("genere") String genere,
                                       @QueryParam("formato") String formato,
                                       @QueryParam("autore") String autore){
        StringBuilder query;
        if(autore != null){
            query = new StringBuilder("SELECT * FROM disco JOIN collezione_disco cd on disco.id = cd.disco_id JOIN disco_autore ad on disco.id = ad.disco_id JOIN autore a on ad.autore_id = a.id JOIN collezione_condivisa_con cc on cd.collezione_id = cc.collezione_id WHERE cc.utente_id = (SELECT id FROM utente WHERE username=?) AND titolo LIKE ? AND genere LIKE ? AND formato LIKE ? AND a.nome_artistico LIKE ?");
        } else {
            query = new StringBuilder("SELECT * FROM disco JOIN collezione_disco cd on disco.id = cd.disco_id JOIN disco_autore ad on disco.id = ad.disco_id JOIN collezione_condivisa_con cc on cd.collezione_id = cc.collezione_id WHERE cc.utente_id = (SELECT id FROM utente WHERE username=?) AND titolo LIKE ? AND genere LIKE ? AND formato LIKE ?");
        }
        if(anno != 0){
            query.append(" AND anno = ?");
        }
        try(PreparedStatement stmt = con.prepareStatement(query.toString())) {
            stmt.setString(1, username);
            stmt.setString(2, "%" + ((titolo != null) ? titolo : "") + "%");
            stmt.setString(3, "%" + ((genere != null) ? genere : "") + "%");
            stmt.setString(4, "%" + ((formato != null) ? formato : "") + "%");
            if (autore != null) {
                stmt.setString(5, "%" + autore + "%");
                if (anno != 0) {
                    stmt.setInt(6, anno);
                }
            } else {
                if (anno != 0) {
                    stmt.setInt(5, anno);
                }
            }
            try (ResultSet rs = stmt.executeQuery()) {
                List<Map<String, Object>> dischi = new ArrayList<>();
                while (rs.next()) {
                    dischi.add(DischiResource.createDisco(rs));
                }
                if (dischi.isEmpty()) {
                    return Response.status(Response.Status.NOT_FOUND).build();
                } else {
                    return Response.ok(dischi).build();
                }
            } catch (SQLException ex) {
                throw new RESTWebApplicationException(ex);
            }
        } catch (SQLException ex) {
            throw new RESTWebApplicationException(ex);
        }
    }

    @GET
    @Produces("application/json")
    @Path("{username}/private/dischi")
    public Response ricercaDischiPrivati(@PathParam("username") String username,
                                        @QueryParam("titolo") String titolo,
                                        @QueryParam("anno") int anno,
                                        @QueryParam("genere") String genere,
                                        @QueryParam("formato") String formato,
                                        @QueryParam("autore") String autore){
        StringBuilder query;
        if(autore != null){
            query = new StringBuilder("SELECT * FROM disco JOIN collezione_disco cd on disco.id = cd.disco_id JOIN collezione collezione on cd.collezione_id = collezione.id JOIN disco_autore ad on disco.id = ad.disco_id JOIN autore a on ad.autore_id = a.id WHERE collezione.utente_id = (SELECT id FROM utente WHERE username=?) AND disco.titolo LIKE ? AND genere LIKE ? AND formato LIKE ? AND a.nome_artistico LIKE ? AND collezione.privacy = 'PRIVATO'");
        } else {
            query = new StringBuilder("SELECT * FROM disco JOIN collezione_disco cd on disco.id = cd.disco_id JOIN collezione collezione on cd.collezione_id = collezione.id WHERE collezione.utente_id = (SELECT id FROM utente WHERE username=?) AND disco.titolo LIKE ? AND genere LIKE ? AND formato LIKE ? AND collezione.privacy = 'PRIVATO'");
        }
        if(anno != 0){
            query.append(" AND anno = ?");
        }
        try(PreparedStatement stmt = con.prepareStatement(query.toString())) {
            stmt.setString(1, username);
            stmt.setString(2, "%" + ((titolo != null) ? titolo : "") + "%");
            stmt.setString(3, "%" + ((genere != null) ? genere : "") + "%");
            stmt.setString(4, "%" + ((formato != null) ? formato : "") + "%");
            if (autore != null) {
                stmt.setString(5, "%" + autore + "%");
                if (anno != 0) {
                    stmt.setInt(6, anno);
                }
            } else {
                if (anno != 0) {
                    stmt.setInt(5, anno);
                }
            }
            try (ResultSet rs = stmt.executeQuery()) {
                List<Map<String, Object>> dischi = new ArrayList<>();
                while (rs.next()) {
                    dischi.add(DischiResource.createDisco(rs));
                }
                if (dischi.isEmpty()) {
                    return Response.status(Response.Status.NOT_FOUND).build();
                } else {
                    return Response.ok(dischi).build();
                }
            } catch (SQLException ex) {
                throw new RESTWebApplicationException(ex);
            }
        } catch (SQLException ex) {
            throw new RESTWebApplicationException(ex);
        }
    }

    @Produces("application/json")
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
