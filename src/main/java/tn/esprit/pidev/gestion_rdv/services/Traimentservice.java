package tn.esprit.pidev.gestion_rdv.services;


import tn.esprit.pidev.gestion_rdv.enteties.Traitement;
import tn.esprit.pidev.Database.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class Traimentservice implements Iservice<Traitement> {

    private Connection con;

    public Traimentservice() {
        con = Database.getConnection();
    }

    @Override
    public List<Traitement> readList() throws SQLException {
        String query = "SELECT * FROM `traitement`";
        List<Traitement> traitements = new ArrayList<>();

        try (Statement stm = con.createStatement();
             ResultSet rs = stm.executeQuery(query)) {

            while (rs.next()) {
                Traitement t = new Traitement(
                        rs.getString("type"),
                        rs.getString("medicament"),
                        rs.getString("suivi")
                );
                traitements.add(t);
            }
        }
        return traitements;
    }

    @Override
    public void add(Traitement traitement) throws SQLException {
        String query = "INSERT INTO `traitement`(`type`, `medicament`, `suivi`) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setString(1, traitement.getType());
            pstmt.setString(2, traitement.getMedicament());
            pstmt.setString(3, traitement.getSuivi());
            pstmt.executeUpdate();
            System.out.println("Traitement ajouté avec succès.");
        }
    }
    @Override
    public void update(Traitement traitement) throws SQLException {
        String query = "UPDATE `traitement` SET `type` = ?, `medicament` = ?, `suivi` = ? WHERE `id` = ?";

        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setString(1, traitement.getType());
            pstmt.setString(2, traitement.getMedicament());
            pstmt.setString(3, traitement.getSuivi());
            pstmt.setInt(4, traitement.getId());

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Traitement mis à jour avec succès.");
            } else {
                System.out.println("Aucun traitement trouvé avec cet ID.");
            }
        }
    }


    }
