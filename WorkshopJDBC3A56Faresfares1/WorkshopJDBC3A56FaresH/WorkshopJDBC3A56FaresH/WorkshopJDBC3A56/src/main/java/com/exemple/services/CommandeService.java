package com.exemple.services;

import com.exemple.entities.Commande;
import com.exemple.utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommandeService implements IService <Commande> {

    private Connection connection;

    public CommandeService() {
        connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void ajouter(Commande commande) throws SQLException {
        String query = "INSERT INTO commande (date_commande, montant_total, user_id) VALUES (?, ?, ?)";
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setDate(1, Date.valueOf(commande.getDateCommande()));
            ps.setDouble(2, commande.getMontantTotal());
            ps.setInt(3, commande.getUserId());
            ps.executeUpdate();
            System.out.println("Commande ajoutée !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void modifier(Commande commande) throws SQLException {
        String query = "UPDATE commande SET date_commande = ?, montant_total = ?, user_id = ? WHERE id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setDate(1, Date.valueOf(commande.getDateCommande()));
            ps.setDouble(2, commande.getMontantTotal());
            ps.setInt(3, commande.getUserId());
            ps.setInt(4, commande.getId());
            ps.executeUpdate();
            System.out.println("Commande modifiée !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void supprimer(int id) throws SQLException {
            String query = "DELETE FROM commande WHERE id = ?";
            try {
                PreparedStatement ps = connection.prepareStatement(query);
                ps.setInt(1, id);
                ps.executeUpdate();
                System.out.println("Commande supprimée !");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    @Override
    public List<Commande> afficher() throws SQLException {
        List<Commande> commandes = new ArrayList<>();
        String query = "SELECT * FROM commande";

        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                Commande c = new Commande();
                c.setId(rs.getInt("id"));
                c.setDateCommande(rs.getDate("date_commande").toLocalDate());
                c.setMontantTotal(rs.getDouble("montant_total"));
                c.setUserId(rs.getInt("user_id"));
                commandes.add(c);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return commandes;
    }
}
