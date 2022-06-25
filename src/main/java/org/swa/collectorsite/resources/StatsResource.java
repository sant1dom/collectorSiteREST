package org.swa.collectorsite.resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.swa.collectorsite.RESTWebApplicationException;
import org.swa.collectorsite.security.Logged;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

@Path("/stats")
public class StatsResource {
    Class c = Class.forName("com.mysql.jdbc.Driver");
    Connection connection = DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/collector_site?noAccessToProcedureBodies=true&serverTimezone=Europe/Rome", "collectorsite","Collectorsite0@");
    PreparedStatement dischiPerGenere = connection.prepareStatement("SELECT COUNT(*) FROM disco WHERE genere = ? AND padre IS NULL");
    PreparedStatement dischiPerAnno = connection.prepareStatement("SELECT COUNT(*) FROM disco WHERE anno = ? AND padre IS NULL");
    PreparedStatement dischiPerAutore = connection.prepareStatement("SELECT COUNT(*) FROM disco join disco_autore da on disco.id = da.disco_id join autore a on a.id = da.autore_id WHERE a.nome_artistico = ? AND padre IS NULL");
    PreparedStatement dischiPerEtichetta = connection.prepareStatement("SELECT COUNT(*) FROM disco WHERE etichetta = ? AND padre IS NULL");
    PreparedStatement dischiTotali = connection.prepareStatement("SELECT COUNT(*) FROM disco WHERE padre IS NULL");
    PreparedStatement tracceTotali = connection.prepareStatement("SELECT COUNT(*) FROM traccia WHERE padre IS NULL");
    PreparedStatement autoriTotali = connection.prepareStatement("SELECT COUNT(*) FROM autore");
    PreparedStatement etichetteTotali = connection.prepareStatement("SELECT COUNT(DISTINCT etichetta) FROM disco WHERE padre IS NULL");
    PreparedStatement generiTotali = connection.prepareStatement("SELECT COUNT(DISTINCT genere) FROM disco WHERE padre IS NULL");
    PreparedStatement numeroCollezioniPubbliche = connection.prepareStatement("SELECT COUNT(*) FROM collezione WHERE privacy = 'PUBBLICO'");
    PreparedStatement numeroCollezioniPrivateUtente = connection.prepareStatement("SELECT COUNT(*) FROM collezione WHERE privacy = 'PRIVATO' AND utente_id = ?");
    PreparedStatement numeroCollezioniTotaliUtente = connection.prepareStatement("SELECT COUNT(*) FROM collezione WHERE utente_id = ?");

    public StatsResource() throws ClassNotFoundException, SQLException {
    }

    @GET
    @Produces("application/json")
    public Response getStats() {
        try {
            var rs = dischiTotali.executeQuery();
            rs.next();
            int numDischiTotali = rs.getInt(1);
            rs = tracceTotali.executeQuery();
            rs.next();
            int numTracceTotali = rs.getInt(1);
            rs = autoriTotali.executeQuery();
            rs.next();
            int numAutoriTotali = rs.getInt(1);
            rs = etichetteTotali.executeQuery();
            rs.next();
            int numEtichetteTotali = rs.getInt(1);
            rs = generiTotali.executeQuery();
            rs.next();
            int numGeneriTotali = rs.getInt(1);
            rs = numeroCollezioniPubbliche.executeQuery();
            rs.next();
            int numCollezioniPubbliche = rs.getInt(1);
            Map<String, Object> statistiche = new LinkedHashMap<>();
            statistiche.put("numero_dischi", numDischiTotali);
            statistiche.put("numero_tracce", numTracceTotali);
            statistiche.put("numero_autori", numAutoriTotali);
            statistiche.put("numero_etichette", numEtichetteTotali);
            statistiche.put("numero_generi", numGeneriTotali);
            statistiche.put("numero_collezioni_pubbliche", numCollezioniPubbliche);
            return Response.ok(statistiche).build();
        } catch (SQLException e) {
            throw new RESTWebApplicationException(e);
        }
    }

    @GET
    @Path("/dischi_per_genere/{genere}")
    @Produces("application/json")
    public Response getDischiPerGenere(@PathParam("genere") String genere) {
        return buildStats(genere, dischiPerGenere, "numero_dischi_per_genere");
    }

    @GET
    @Path("/dischi_per_anno/{anno}")
    @Produces("application/json")
    public Response getDischiPerAnno(@PathParam("anno") int anno) {
        return buildStats(anno, dischiPerAnno, "numero_dischi_per_anno");
    }

    @GET
    @Path("/dischi_per_autore/{autore}")
    @Produces("application/json")
    public Response getDischiPerAutore(@PathParam("autore") String autore) {
        return buildStats(autore, dischiPerAutore, "numero_dischi_per_autore");
    }

    @GET
    @Path("/dischi_per_etichetta/{etichetta}")
    @Produces("application/json")
    public Response getDischiPerEtichetta(@PathParam("etichetta") String etichetta) {
        return buildStats(etichetta, dischiPerEtichetta, "numero_dischi_per_etichetta");
    }


    @GET
    @Logged
    @Path("/numero_collezioni_private_utente")
    @Produces("application/json")
    public Response getNumeroCollezioniPrivateUtente(@Context SecurityContext securityContext) {
        return buildStats(securityContext.getUserPrincipal().getName(), numeroCollezioniPrivateUtente, "numero_collezioni_private_utente");
    }

    @GET
    @Logged
    @Path("/numero_collezioni_totali_utente")
    @Produces("application/json")
    public Response getNumeroCollezioniTotaliUtente(@Context SecurityContext securityContext) {
        return buildStats(securityContext.getUserPrincipal().getName(), numeroCollezioniTotaliUtente, "numero_collezioni_totali_utente");
    }

    private <T> Response buildStats(T param, PreparedStatement stmt, String key) {
        try {
            if(param != null) {
                switch (param.getClass().getName()) {
                    case "java.lang.String" -> stmt.setString(1, (String) param);
                    case "java.lang.Integer" -> stmt.setInt(1, (Integer) param);
                    default -> throw new IllegalArgumentException("Parametro non supportato");
                }
            }
            var rs = stmt.executeQuery();
            rs.next();
            int num = rs.getInt(1);
            Map<String, Object> statistiche = new LinkedHashMap<>();
            statistiche.put(key, num);
            return Response.ok(statistiche).build();
        } catch (SQLException e) {
            throw new RESTWebApplicationException(e);
        }
    }
}
