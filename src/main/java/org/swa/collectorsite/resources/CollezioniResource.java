package org.swa.collectorsite.resources;

import jakarta.ws.rs.*;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.*;
import org.swa.collectorsite.RESTWebApplicationException;
import org.swa.collectorsite.security.Logged;

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
            "jdbc:mysql://localhost:3306/collector_site?noAccessToProcedureBodies=true&serverTimezone=Europe/Rome", "collectorsite", "Collectorsite0@");

    public CollezioniResource() throws SQLException, ClassNotFoundException {
        // Auto-generated constructor stub
    }

    private Response collezioniUriList(@Context UriInfo uriInfo, String username, List<String> collezioni, PreparedStatement stmt) throws SQLException {
        stmt.setString(1, username);
        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                collezioni.add(uriInfo.getBaseUriBuilder()
                        .path(CollezioniResource.class)
                        .path(CollezioniResource.class, "getCollezione")
                        .build(rs.getInt("id")).toString());
            }
            if (collezioni.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND).build();
            } else {
                return Response.ok(collezioni).build();
            }
        }
    }

    private int getIdUtente(ContainerRequestContext requestContext) {
        String token = null;
        int id_utente = 0;
        String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring("Bearer".length()).trim();
        } else if (requestContext.getCookies().containsKey("token")) {
            token = requestContext.getCookies().get("token").getValue();
        } else if (requestContext.getUriInfo().getQueryParameters().containsKey("token")) {
            token = requestContext.getUriInfo().getQueryParameters().getFirst("token");
        }
        if (token != null && !token.isEmpty()) {
            try (PreparedStatement stmt = con.prepareStatement("SELECT id FROM utente_rest WHERE token = ?")) {
                stmt.setString(1, token);
                ResultSet rs = stmt.executeQuery();
                rs.next();
                id_utente = rs.getInt("id");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return id_utente;
    }

    static Map<String, Object> createCollezione(ResultSet rs) {
        try {
            Map<String, Object> collezione = new LinkedHashMap<>();
            collezione.put("id", rs.getInt("id"));
            collezione.put("titolo", rs.getString("titolo"));
            collezione.put("privacy", rs.getString("privacy"));
            collezione.put("data_creazione", new SimpleDateFormat("dd/MM/yyyy").format(rs.getDate("data_creazione")));
            return collezione;
        } catch (SQLException ex) {
            throw new RESTWebApplicationException(ex);
        }
    }

    private Response dischiUriList(@Context UriInfo uriinfo, ResultSet rs) throws SQLException {
        Set<String> dischi = new HashSet<>();

        while (rs.next()) {
            dischi.add(uriinfo.getBaseUriBuilder().path(DischiResource.class).path(Integer.toString(rs.getInt("id"))).build().toString());
        }
        if (dischi.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            return Response.ok(dischi).build();
        }
    }

    // Operazione 4
    @GET
    @Path("{id_collezione}")
    @Produces("application/json")
    public Response getCollezione(@Context UriInfo uriinfo, @PathParam("id_collezione") int id_collezione, @Context ContainerRequestContext requestContext) {
        int id_utente = getIdUtente(requestContext);

        try (PreparedStatement stmt = con.prepareStatement("SELECT * FROM collezione WHERE id = ?")) {
            stmt.setInt(1, id_collezione);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> collezione = createCollezione(rs);
                    switch (rs.getString("privacy")) {
                        case "PRIVATO":
                            if (id_utente == rs.getInt("utente_id")) {
                                URI uri = uriinfo.getBaseUriBuilder()
                                        .path(CollezioniResource.class)
                                        .path(CollezioniResource.class, "getDischiCollezione")
                                        .build(id_collezione);
                                collezione.put("dischi", uri.toString());
                            } else {
                                return Response.status(Response.Status.UNAUTHORIZED).build();
                            }
                            break;
                        case "CONDIVISO":
                            if (id_utente > 0) {
                                try (PreparedStatement stmt1 = con.prepareStatement("SELECT * FROM collezione JOIN collezione_condivisa_con ccc on collezione.id = ccc.collezione_id WHERE ccc.utente_id = ? AND collezione.id = ?")) {
                                    stmt1.setInt(1, id_utente);
                                    stmt1.setInt(2, id_collezione);
                                    try (ResultSet rs1 = stmt1.executeQuery()) {
                                        if (rs1.next()) {
                                            URI uri = uriinfo.getBaseUriBuilder()
                                                    .path(CollezioniResource.class)
                                                    .path(CollezioniResource.class, "getDischiCollezione")
                                                    .build(id_collezione);
                                            collezione.put("dischi", uri.toString());
                                        } else {
                                            return Response.status(Response.Status.UNAUTHORIZED).build();
                                        }
                                    }

                                } catch (SQLException ex) {
                                    throw new RESTWebApplicationException(ex);
                                }
                            } else {
                                return Response.status(Response.Status.UNAUTHORIZED).build();
                            }
                            break;
                        default:
                            URI uri = uriinfo.getBaseUriBuilder()
                                    .path(CollezioniResource.class)
                                    .path(CollezioniResource.class, "getDischiCollezione")
                                    .build(id_collezione);
                            collezione.put("dischi", uri.toString());
                    }

                    return Response.ok(collezione).build();
                } else {
                    return Response.status(Response.Status.NOT_FOUND).build();
                }
            }
        } catch (SQLException ex) {
            throw new RESTWebApplicationException(ex);
        }
    }

    @GET
    @Path("{id_collezione}/dischi")
    @Produces("application/json")
    public Response getDischiCollezione(@Context UriInfo uriInfo, @PathParam("id_collezione") int id_collezione, @Context ContainerRequestContext requestContext) {
        int id_utente = getIdUtente(requestContext);

        try (PreparedStatement stmt = con.prepareStatement("SELECT * FROM collezione WHERE id = ? ORDER BY id")) {
            stmt.setInt(1, id_collezione);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    switch (rs.getString("privacy")) {
                        case "PRIVATO":
                            if (id_utente == rs.getInt("utente_id")) {
                                try (PreparedStatement stmt1 = con.prepareStatement("SELECT disco.id FROM disco JOIN collezione_disco cd on disco.id = cd.disco_id WHERE cd.collezione_id = ? ORDER BY disco.id")) {
                                    stmt1.setInt(1, id_collezione);
                                    try (ResultSet rs1 = stmt1.executeQuery()) {
                                        return dischiUriList(uriInfo, rs1);
                                    }
                                }
                            } else {
                                return Response.status(Response.Status.UNAUTHORIZED).build();
                            }
                        case "CONDIVISO":
                            if (id_utente > 0) {
                                try (PreparedStatement stmt1 = con.prepareStatement("SELECT * FROM collezione JOIN collezione_condivisa_con ccc on collezione.id = ccc.collezione_id WHERE ccc.utente_id = ? AND collezione.id = ? ORDER BY collezione.id")) {
                                    stmt1.setInt(1, id_utente);
                                    stmt1.setInt(2, id_collezione);
                                    try (ResultSet rs1 = stmt1.executeQuery()) {
                                        if (rs1.next()) {
                                            try (PreparedStatement stmt2 = con.prepareStatement("SELECT disco.id FROM disco JOIN collezione_disco cd on disco.id = cd.disco_id WHERE cd.collezione_id = ? ORDER BY disco.id")) {
                                                stmt2.setInt(1, id_collezione);
                                                try (ResultSet rs2 = stmt2.executeQuery()) {
                                                    return dischiUriList(uriInfo, rs2);
                                                }
                                            }
                                        } else {
                                            return Response.status(Response.Status.UNAUTHORIZED).build();
                                        }
                                    }
                                } catch (SQLException ex) {
                                    throw new RESTWebApplicationException(ex);
                                }
                            } else {
                                return Response.status(Response.Status.UNAUTHORIZED).build();
                            }
                        default:
                            try (PreparedStatement stmt2 = con.prepareStatement("SELECT disco.id FROM disco JOIN collezione_disco cd on disco.id = cd.disco_id WHERE cd.collezione_id = ? ORDER BY disco.id")) {
                                stmt2.setInt(1, id_collezione);
                                try (ResultSet rs2 = stmt2.executeQuery()) {
                                    return dischiUriList(uriInfo, rs2);
                                }
                            }
                    }
                } else {
                    return Response.status(Response.Status.NOT_FOUND).build();
                }
            }
        } catch (SQLException ex) {
            throw new RESTWebApplicationException(ex);
        }
    }

    //Operazione 6
    @POST
    @Logged
    @Consumes("application/json")
    @Produces("application/json")
    @Path("{id_collezione}/dischi")
    public Response aggiungiDisco(@Context UriInfo uriinfo,
                                  @PathParam("id_collezione") int id_collezione,
                                  Map<String, Object> disco,
                                  @Context SecurityContext securityContext) {
        String query = "INSERT INTO disco (titolo, anno, barcode, etichetta, data_inserimento,genere, formato, stato_conservazione, utente_id, padre) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String add_autori = "INSERT INTO disco_autore (disco_id, autore_id) VALUES (?, ?)";
        try (PreparedStatement stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, (String) disco.get("titolo"));
            stmt.setInt(2, (int) disco.get("anno"));
            stmt.setString(3, (String) disco.get("barcode"));
            stmt.setString(4, (String) disco.get("etichetta"));
            stmt.setDate(5, Date.valueOf(LocalDate.now()));
            stmt.setString(6, (String) disco.get("genere"));
            stmt.setString(7, (String) disco.get("formato"));
            stmt.setString(8, (String) disco.get("stato_conservazione"));
            stmt.setInt(9, Integer.parseInt(securityContext.getUserPrincipal().getName()));
            if (disco.get("padre") != null) {
                stmt.setInt(10, (int) disco.get("padre"));
            } else {
                stmt.setNull(10, Types.INTEGER);
            }
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                rs.next();
                int id_disco = rs.getInt(1);
                query = "INSERT INTO collezione_disco (collezione_id, disco_id) VALUES (?, ?)";
                try (PreparedStatement stmt2 = con.prepareStatement(query)) {
                    stmt2.setInt(1, id_collezione);
                    stmt2.setInt(2, id_disco);
                    stmt2.executeUpdate();
                }

                try (PreparedStatement stmt3 = con.prepareStatement(add_autori)) {
                    if (disco.get("autori") != null) {
                        List<Integer> autori_id = (List<Integer>) disco.get("autori");
                        for (Integer id : autori_id) {
                            stmt3.setInt(1, id_disco);
                            stmt3.setInt(2, id);
                            stmt3.executeUpdate();
                        }
                    }
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
    public Response getDiscoCollezione(@PathParam("id_collezione") int id_collezione,
                                       @PathParam("id_disco") int id_disco,
                                       @Context ContainerRequestContext requestContext,
                                       @Context UriInfo uriInfo) {
        int id_utente = getIdUtente(requestContext);
        try (PreparedStatement stmt = con.prepareStatement("SELECT * FROM collezione WHERE id = ?")) {
            stmt.setInt(1, id_collezione);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> collezione = createCollezione(rs);
                    switch (rs.getString("privacy")) {
                        case "PRIVATO":
                            if (id_utente == rs.getInt("utente_id")) {
                                try (PreparedStatement stmt1 = con.prepareStatement("SELECT * FROM disco JOIN collezione_disco cd on disco.id = cd.disco_id WHERE cd.collezione_id = ? AND cd.disco_id = ?")) {
                                    stmt1.setInt(1, id_collezione);
                                    stmt1.setInt(2, id_disco);
                                    try (ResultSet rs1 = stmt1.executeQuery()) {
                                        if (rs1.next()) {
                                            var disco = DischiResource.createDisco(rs1);
                                            disco.put("padre", uriInfo.getBaseUriBuilder()
                                                    .path(DischiResource.class)
                                                    .path(DischiResource.class, "getDisco")
                                                    .build(rs.getInt("padre")).toString());

                                            var autori = new ArrayList<>();
                                            try(PreparedStatement sAutori = con.prepareStatement("SELECT * FROM disco_autore WHERE disco_id = ?")){
                                                sAutori.setInt(1, id_disco);
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
                                    }
                                }
                            } else {
                                return Response.status(Response.Status.UNAUTHORIZED).build();
                            }
                        case "CONDIVISO":
                            if (id_utente > 0) {
                                try (PreparedStatement stmt1 = con.prepareStatement("SELECT * FROM collezione JOIN collezione_condivisa_con ccc on collezione.id = ccc.collezione_id WHERE ccc.utente_id = ? AND collezione.id = ?")) {
                                    stmt1.setInt(1, id_utente);
                                    stmt1.setInt(2, id_collezione);
                                    try (ResultSet rs1 = stmt1.executeQuery()) {
                                        if (rs1.next()) {
                                            try (PreparedStatement stmt2 = con.prepareStatement("SELECT * FROM disco JOIN collezione_disco cd on disco.id = cd.disco_id WHERE cd.collezione_id = ? AND cd.disco_id = ?")) {
                                                stmt2.setInt(1, id_collezione);
                                                stmt2.setInt(2, id_disco);
                                                try (ResultSet rs2 = stmt2.executeQuery()) {
                                                    if (rs2.next()) {
                                                        var disco = DischiResource.createDisco(rs2);
                                                        disco.put("padre", uriInfo.getBaseUriBuilder()
                                                                .path(DischiResource.class)
                                                                .path(DischiResource.class, "getDisco")
                                                                .build(rs.getInt("padre")).toString());
                                                        var autori = new ArrayList<>();
                                                        try(PreparedStatement sAutori = con.prepareStatement("SELECT * FROM disco_autore WHERE disco_id = ?")){
                                                            sAutori.setInt(1, id_disco);
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
                                                }
                                            }
                                        } else {
                                            return Response.status(Response.Status.UNAUTHORIZED).build();
                                        }
                                    }
                                } catch (SQLException ex) {
                                    throw new RESTWebApplicationException(ex);
                                }
                            } else {
                                return Response.status(Response.Status.UNAUTHORIZED).build();
                            }
                        default:
                            try (PreparedStatement stmt3 = con.prepareStatement("SELECT * FROM disco JOIN collezione_disco cd on disco.id = cd.disco_id WHERE cd.collezione_id = ? AND cd.disco_id = ?")) {
                                stmt3.setInt(1, id_collezione);
                                stmt3.setInt(2, id_disco);
                                try (ResultSet rs3 = stmt3.executeQuery()) {
                                    if (rs3.next()) {
                                        var disco = DischiResource.createDisco(rs3);
                                        disco.put("padre", uriInfo.getBaseUriBuilder()
                                                .path(DischiResource.class)
                                                .path(DischiResource.class, "getDisco")
                                                .build(rs.getInt("padre")).toString());
                                        var autori = new ArrayList<>();
                                        try(PreparedStatement sAutori = con.prepareStatement("SELECT * FROM disco_autore WHERE disco_id = ?")){
                                            sAutori.setInt(1, id_disco);
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
                                }
                            }
                    }
                } else {
                    return Response.status(Response.Status.NOT_FOUND).build();
                }
            }
        } catch (SQLException ex) {
            throw new RESTWebApplicationException(ex);
        }
    }

    // Operazione 10
    @PUT
    @Logged
    @Consumes("application/json")
    @Produces("application/json")
    @Path("{id_collezione}/dischi/{id_disco}")
    public Response modificaDisco(@PathParam("id_collezione") int id_collezione,
                                  @PathParam("id_disco") int id_disco, Map<String, Object> disco,
                                  @Context SecurityContext securityContext) {
        String query = "UPDATE disco SET titolo = ?, anno = ?, barcode = ?, etichetta = ?, genere = ?, formato = ?, stato_conservazione = ?, utente_id = ?, padre = ? WHERE id = ?";
        String delete_relazione = "DELETE FROM disco_autore WHERE disco_id = ?";
        String add_autori = "INSERT INTO disco_autore (disco_id, autore_id) VALUES (?, ?)";

        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, (String) disco.get("titolo"));
            stmt.setInt(2, (int) disco.get("anno"));
            stmt.setString(3, (String) disco.get("barcode"));
            stmt.setString(4, (String) disco.get("etichetta"));
            stmt.setString(5, (String) disco.get("genere"));
            stmt.setString(6, (String) disco.get("formato"));
            stmt.setString(7, (String) disco.get("stato_conservazione"));
            stmt.setInt(8, Integer.parseInt(securityContext.getUserPrincipal().getName()));
            if (disco.get("padre") != null) {
                stmt.setInt(9, (int) disco.get("padre"));
            } else {
                stmt.setNull(9, Types.INTEGER);
            }
            stmt.setInt(10, id_disco);
            stmt.executeUpdate();

            try (PreparedStatement stmt2 = con.prepareStatement(delete_relazione)) {
                stmt2.setInt(1, id_disco);
                stmt2.executeUpdate();

                try (PreparedStatement stmt3 = con.prepareStatement(add_autori)) {
                    if (disco.get("autori") != null) {
                        List<Integer> autori_id = (List<Integer>) disco.get("autori");
                        for (Integer id : autori_id) {
                            stmt3.setInt(1, id_disco);
                            stmt3.setInt(2, id);
                            stmt3.executeUpdate();
                        }
                    }
                }
            }


            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (SQLException ex) {
            throw new RESTWebApplicationException(ex);
        }
    }

    // Operazione 2
    @GET
    @Logged
    @Path("all")
    @Produces("application/json")
    public Response getCollezioniUtente(@Context UriInfo uriInfo, @Context SecurityContext securityContext) {
        List<String> collezioni = new ArrayList<>();

        try (PreparedStatement stmt = con.prepareStatement("SELECT id FROM collezione WHERE utente_id = ? ORDER BY id")) {
            return collezioniUriList(uriInfo, securityContext.getUserPrincipal().getName(), collezioni, stmt);
        } catch (SQLException ex) {
            throw new RESTWebApplicationException(ex);
        }
    }

    // Operazione 3
    @GET
    @Logged
    @Path("condivise")
    @Produces("application/json")
    public Response getCollezioniCondiviseUtente(@Context UriInfo uriInfo, @Context SecurityContext securityContext) {
        List<String> collezioni = new ArrayList<>();
        try (PreparedStatement stmt = con.prepareStatement("SELECT * FROM collezione JOIN collezione_condivisa_con ccc on collezione.id = ccc.collezione_id WHERE ccc.utente_id = ? ORDER BY id")) {
            return collezioniUriList(uriInfo, securityContext.getUserPrincipal().getName(), collezioni, stmt);
        } catch (SQLException ex) {
            throw new RESTWebApplicationException(ex);
        }
    }

    @GET
    @Logged
    @Produces("application/json")
    @Path("condivise/dischi")
    public Response ricercaDischiCondivisi(@QueryParam("titolo") String titolo,
                                           @QueryParam("anno") int anno,
                                           @QueryParam("genere") String genere,
                                           @QueryParam("formato") String formato,
                                           @QueryParam("autore") String autore,
                                           @Context UriInfo uriinfo,
                                           @Context SecurityContext securityContext) {
        StringBuilder query;
        if (autore != null) {
            query = new StringBuilder("SELECT disco.id FROM disco JOIN collezione_disco cd on disco.id = cd.disco_id JOIN disco_autore ad on disco.id = ad.disco_id JOIN autore a on ad.autore_id = a.id JOIN collezione_condivisa_con cc on cd.collezione_id = cc.collezione_id WHERE cc.utente_id = ? AND titolo LIKE ? AND genere LIKE ? AND formato LIKE ? AND a.nome_artistico LIKE ?");
        } else {
            query = new StringBuilder("SELECT disco.id FROM disco JOIN collezione_disco cd on disco.id = cd.disco_id JOIN disco_autore ad on disco.id = ad.disco_id JOIN collezione_condivisa_con cc on cd.collezione_id = cc.collezione_id WHERE cc.utente_id = ? AND titolo LIKE ? AND genere LIKE ? AND formato LIKE ?");
        }
        if (anno != 0) {
            query.append(" AND anno = ?");
        }
        try (PreparedStatement stmt = con.prepareStatement(query.toString())) {
            stmt.setString(1, securityContext.getUserPrincipal().getName());
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
    @Logged
    @Produces("application/json")
    @Path("private/dischi")
    public Response ricercaDischiPrivati(@QueryParam("titolo") String titolo,
                                         @QueryParam("anno") int anno,
                                         @QueryParam("genere") String genere,
                                         @QueryParam("formato") String formato,
                                         @QueryParam("autore") String autore,
                                         @Context UriInfo uriinfo,
                                         @Context SecurityContext securityContext) {
        StringBuilder query;
        if (autore != null) {
            query = new StringBuilder("SELECT * FROM disco JOIN collezione_disco cd on disco.id = cd.disco_id JOIN collezione collezione on cd.collezione_id = collezione.id JOIN disco_autore ad on disco.id = ad.disco_id JOIN autore a on ad.autore_id = a.id WHERE collezione.utente_id = ? AND disco.titolo LIKE ? AND genere LIKE ? AND formato LIKE ? AND a.nome_artistico LIKE ? AND collezione.privacy = 'PRIVATO' ORDER BY disco.id");
        } else {
            query = new StringBuilder("SELECT * FROM disco JOIN collezione_disco cd on disco.id = cd.disco_id JOIN collezione collezione on cd.collezione_id = collezione.id WHERE collezione.utente_id = ? AND disco.titolo LIKE ? AND genere LIKE ? AND formato LIKE ? AND collezione.privacy = 'PRIVATO' ORDER BY disco.id");
        }
        if (anno != 0) {
            query.append(" AND anno = ?");
        }
        try (PreparedStatement stmt = con.prepareStatement(query.toString())) {
            stmt.setString(1, securityContext.getUserPrincipal().getName());
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
        if (autore != null) {
            query = new StringBuilder("SELECT * FROM disco JOIN collezione_disco cd on disco.id = cd.disco_id JOIN disco_autore ad on disco.id = ad.disco_id JOIN autore a on ad.autore_id = a.id WHERE cd.collezione_id IN (SELECT id FROM collezione WHERE privacy = 'PUBBLICO') AND titolo LIKE ? AND genere LIKE ? AND formato LIKE ? AND a.nome_artistico LIKE ? ORDER BY disco.id");
        } else {
            query = new StringBuilder("SELECT * FROM disco JOIN collezione_disco cd on disco.id = cd.disco_id WHERE cd.collezione_id IN (SELECT id FROM collezione WHERE privacy = 'PUBBLICO') AND titolo LIKE ? AND genere LIKE ? AND formato LIKE ? ORDER BY disco.id");
        }
        if (anno != 0) {
            query.append(" AND anno = ?");
        }
        try (PreparedStatement stmt = con.prepareStatement(query.toString())) {
            stmt.setString(1, "%" + ((titolo != null) ? titolo : "") + "%");
            stmt.setString(2, "%" + ((genere != null) ? genere : "") + "%");
            stmt.setString(3, "%" + ((formato != null) ? formato : "") + "%");
            if (autore != null) {
                stmt.setString(4, "%" + autore + "%");
                if (anno != 0) {
                    stmt.setInt(5, anno);
                }
            } else {
                if (anno != 0) {
                    stmt.setInt(4, anno);
                }
            }
            try (ResultSet rs = stmt.executeQuery()) {
                return dischiUriList(uriinfo, rs);
            }
        } catch (SQLException ex) {
            throw new RESTWebApplicationException(ex);
        }
    }
}
