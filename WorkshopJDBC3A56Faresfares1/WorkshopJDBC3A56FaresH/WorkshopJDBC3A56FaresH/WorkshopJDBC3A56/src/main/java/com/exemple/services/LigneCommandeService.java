package com.exemple.services;

import com.exemple.entities.LigneCommande;
import com.exemple.utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LigneCommandeService  implements IService <LigneCommande>{


    private final Connection connection;

    public LigneCommandeService() {
        connection = MyDatabase.getInstance().getConnection();
    }

    public void ajouter(LigneCommande ligne) throws SQLException {
        String query = "INSERT INTO ligne_commande (produit_id, quantite, prix_unitaire, commande_id) VALUES (?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, ligne.getProduit());
        ps.setInt(2, ligne.getQuantite());
        ps.setDouble(3, ligne.getPrix());
        ps.setInt(4, ligne.getCommandeId());
        ps.executeUpdate();
    }

    public void modifier(LigneCommande ligne) throws SQLException {
        String query = "UPDATE ligne_commande SET produit_id = ?, quantite = ?, prix_unitaire = ?, commande_id = ? WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, ligne.getProduit());
        ps.setInt(2, ligne.getQuantite());
        ps.setDouble(3, ligne.getPrix());
        ps.setInt(4, ligne.getCommandeId());
        ps.setInt(5, ligne.getId());
        ps.executeUpdate();
    }

    public void supprimer(int id) throws SQLException {
        String query = "DELETE FROM ligne_commande WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, id);
        ps.executeUpdate();
    }

    public List<LigneCommande> afficher() throws SQLException {
        List<LigneCommande> lignes = new ArrayList<>();
        String query = "SELECT * FROM ligne_commande";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        while (rs.next()) {
            LigneCommande ligne = new LigneCommande();
            ligne.setId(rs.getInt("id"));
            ligne.setProduit(rs.getInt("produit_id"));
            ligne.setQuantite(rs.getInt("quantite"));
            ligne.setPrix(rs.getDouble("prix_unitaire"));
            ligne.setCommandeId(rs.getInt("commande_id"));
            lignes.add(ligne);
        }

        return lignes;
    }
}
