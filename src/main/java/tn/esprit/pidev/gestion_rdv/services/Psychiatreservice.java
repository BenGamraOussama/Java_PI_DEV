package tn.esprit.pidev.gestion_rdv.services;

import tn.esprit.pidev.Database.Database;
import tn.esprit.pidev.gestion_rdv.enteties.Psychiatre;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Psychiatreservice {
        public List<Psychiatre> getAllPsychiatres() throws SQLException {
            List<Psychiatre> psychiatres = new ArrayList<>();
            String query = "SELECT * FROM `psychiatre`";

            try (Connection conn = Database.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {

                while (rs.next()) {
                    Psychiatre psychiatre = new Psychiatre();
                    psychiatre.setId(rs.getInt("id"));
                    psychiatre.setFirstName(rs.getString("nom"));
                    psychiatre.setLastName(rs.getString("prenom"));
                    psychiatres.add(psychiatre);
                }
            }
            return psychiatres;
        }
    }
