package org.swa.collectorsite.resources;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.swa.collectorsite.RESTWebApplicationException;

import java.net.URI;
import java.sql.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

@Path("collezioni")
public class CollezioniResource {
    Class c = Class.forName("com.mysql.jdbc.Driver");
    Connection con = DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/collector_site?noAccessToProcedureBodies=true&serverTimezone=Europe/Rome", "collectorsite","Collectorsite0@");

    public CollezioniResource() throws SQLException, ClassNotFoundException {
        // Auto-generated constructor stub
    }

    private Response collezioniUriList(@Context UriInfo uriInfo, String username, List<String> collezioni, PreparedStatement stmt) throws SQLException {
        stmt.setString(1, username);
        try(ResultSet rs = stmt.executeQuery()){
            while(rs.next()){
                collezioni.add(uriInfo.getBaseUriBuilder()
                        .path(CollezioniResource.class)
                        .path(CollezioniResource.class, "getCollezione")
                        .build(rs.getInt("id")).toString());
            }
            if (collezioni.isEmpty()){
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            else{
                return Response.ok(collezioni).build();
            }
        }
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
                    URI uri = uriinfo.getBaseUriBuilder()
                                        .path(CollezioniResource.class)
                                        .path(CollezioniResource.class, "getDischiCollezione")
                                        .build(id_collezione);
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
    public Response getDischiCollezione(@Context UriInfo uriInfo, @PathParam("id_collezione") int id_collezione){
        try(PreparedStatement stmt = con.prepareStatement("SELECT disco.id FROM disco JOIN collezione_disco cd on disco.id = cd.disco_id WHERE cd.collezione_id = ?")){
            stmt.setInt(1, id_collezione);
            try(ResultSet rs = stmt.executeQuery()){
                return dischiUriList(uriInfo, rs);
            }
        }
        catch (SQLException ex) {
            throw new RESTWebApplicationException(ex);
        }
    }

    //Operazione 6
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    @Path("{id_collezione}/dischi")
    public Response aggiungiDisco(@Context UriInfo uriinfo,@PathParam("id_collezione") int id_collezione, Map<String, Object> disco) {
        String query = "INSERT INTO disco (titolo, anno, barcode, etichetta, data_inserimento,genere, formato, stato_conservazione, utente_id, padre) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try(PreparedStatement stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, (String) disco.get("titolo"));
            stmt.setInt(2, (int) disco.get("anno"));
            stmt.setString(3, (String) disco.get("barcode"));
            stmt.setString(4, (String) disco.get("etichetta"));
            stmt.setDate(5, Date.valueOf(LocalDate.now()));
            stmt.setString(6, (String) disco.get("genere"));
            stmt.setString(7, (String) disco.get("formato"));
            stmt.setString(8, (String) disco.get("stato_conservazione"));
            stmt.setInt(9, (int) disco.get("utente_id"));
            if (disco.get("padre") != null) {
                stmt.setInt(10, (int) disco.get("padre"));
            } else {
                stmt.setNull(10, Types.INTEGER);
            }
            stmt.executeUpdate();
            try(ResultSet rs = stmt.getGeneratedKeys()) {
                rs.next();
                int id_disco = rs.getInt(1);
                query = "INSERT INTO collezione_disco (collezione_id, disco_id) VALUES (?, ?)";
                try(PreparedStatement stmt2 = con.prepareStatement(query)) {
                    stmt2.setInt(1, id_collezione);
                    stmt2.setInt(2, id_disco);
                    stmt2.executeUpdate();
                }
                URI uri = uriinfo.getBaseUriBuilder().path("collezioni/" + id_collezione + "/dischi/" + id_disco).build();
                return Response.created(uri).build();
            }
        } catch (SQLException ex) {
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

    // Operazione 10
    @PUT
    @Consumes("application/json")
    @Produces("application/json")
    @Path("{id_collezione}/dischi/{id_disco}")
    public Response modificaDisco(@PathParam("id_collezione") int id_collezione, @PathParam("id_disco") int id_disco, Map<String, Object> disco) {
        String query = "UPDATE disco SET titolo = ?, anno = ?, barcode = ?, etichetta = ?, genere = ?, formato = ?, stato_conservazione = ?, utente_id = ?, padre = ? WHERE id = ?";
        try(PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, (String) disco.get("titolo"));
            stmt.setInt(2, (int) disco.get("anno"));
            stmt.setString(3, (String) disco.get("barcode"));
            stmt.setString(4, (String) disco.get("etichetta"));
            stmt.setString(5, (String) disco.get("genere"));
            stmt.setString(6, (String) disco.get("formato"));
            stmt.setString(7, (String) disco.get("stato_conservazione"));
            stmt.setInt(8, (int) disco.get("utente_id"));
            if (disco.get("padre") != null) {
                stmt.setInt(9, (int) disco.get("padre"));
            } else {
                stmt.setNull(9, Types.INTEGER);
            }
            stmt.setInt(10, id_disco);
            stmt.executeUpdate();
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (SQLException ex) {
            throw new RESTWebApplicationException(ex);
        }
    }


    // Operazione 2
    @GET
    @Path("{username}/all")
    @Produces("application/json")
    public Response getCollezioniUtente(@Context UriInfo uriInfo,@PathParam("username") String username){
        List<String> collezioni = new ArrayList<>();
        try(PreparedStatement stmt = con.prepareStatement("SELECT id FROM collezione WHERE utente_id = (SELECT id FROM utente WHERE username = ?)")){
            return collezioniUriList(uriInfo, username, collezioni, stmt);
        }
        catch (SQLException ex) {
            throw new RESTWebApplicationException(ex);
        }
    }

    // Operazione 3
    @GET
    @Path("{username}/condivise")
    @Produces("application/json")
    public Response getCollezioniCondiviseUtente(@Context UriInfo uriInfo,@PathParam("username") String username){
        List<String> collezioni = new ArrayList<>();
        try(PreparedStatement stmt = con.prepareStatement("SELECT * FROM collezione JOIN collezione_condivisa_con ccc on collezione.id = ccc.collezione_id WHERE ccc.utente_id = (SELECT id FROM utente WHERE username = ?)")){
            return collezioniUriList(uriInfo, username, collezioni, stmt);
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
                                       @QueryParam("autore") String autore,
                                       @Context UriInfo uriinfo) {
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
                return dischiUriList(uriinfo, rs);
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
                                        @QueryParam("autore") String autore,
                                        @Context UriInfo uriinfo) {
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
                return dischiUriList(uriinfo, rs);
            }
        } catch (SQLException ex) {
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
                                                    @QueryParam("autore") String autore,
                                                    @Context UriInfo uriinfo) {
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
                return dischiUriList(uriinfo, rs);
            }
        }
        catch (SQLException ex) {
            throw new RESTWebApplicationException(ex);
        }
    }

    private Response dischiUriList(@Context UriInfo uriinfo, ResultSet rs) throws SQLException {
        List<String> dischi = new ArrayList<>();
        while(rs.next()){
            dischi.add(uriinfo.getBaseUriBuilder().path(DischiResource.class).path(Integer.toString(rs.getInt("id"))).build().toString());
        }
        if (dischi.isEmpty()){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        else{
            return Response.ok(dischi).build();
        }
    }
}
