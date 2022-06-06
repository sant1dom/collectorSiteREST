package org.swa.collectorsite.resources;

import org.swa.collectorsite.RESTWebApplicationException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

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
}
