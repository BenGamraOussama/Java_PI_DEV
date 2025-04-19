package org.example.pi__dev_.services;


import org.example.pi__dev_.enteties.RDV;
import org.example.pi__dev_.utils.Pidev;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RDVservice implements Iservice<RDV> {

    private Connection con;

    public RDVservice() {
        this.con = Pidev.getInstance().getCon();
    }

    @Override
    public List<RDV> readList() throws SQLException {
        String query = "SELECT * FROM `rdv`";
        List<RDV> rdvs = new ArrayList<>();

        try (Statement stm = con.createStatement();
             ResultSet rs = stm.executeQuery(query)) {

            while (rs.next()) {
                RDV r = new RDV(rs.getTime("heure"), rs.getDate("date"), rs.getString("priorite"));
                rdvs.add(r);
            }
        }
        return rdvs;
    }

    public void add(RDV rdv) throws SQLException {
        if (con == null) {
            System.out.println("La connexion à la base de données est nulle. Impossible d'ajouter le RDV.");
            return;  // Ou gérer l'erreur comme tu veux
        }

        String query = "INSERT INTO `rdv`(`heure`, `date`, `priorite`) VALUES (?,?,?)";

        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setTime(1, rdv.getHeure());
            pstmt.setDate(2, rdv.getDate());
            pstmt.setString(3, rdv.getPriorite());
            pstmt.executeUpdate();
            System.out.println("RDV ajouté avec succès !");
        }
    }


    @Override
    public void update(RDV rdv) {
        String query = "UPDATE `rdv` SET `heure` = ?, `date` = ? `priorite` = ? WHERE `id` = ?";
        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setTime(1, rdv.getHeure());
            pstmt.setDate(2, rdv.getDate());
            pstmt.setInt(3, rdv.getId());
            pstmt.setString(4, rdv.getPriorite());
            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("RDV updated successfully.");
            } else {
                System.out.println("No RDV found with the given ID.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}

