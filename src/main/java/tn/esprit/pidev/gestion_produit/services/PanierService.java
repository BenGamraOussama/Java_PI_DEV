package tn.esprit.pidev.gestion_produit.services;

import tn.esprit.pidev.gestion_produit.gui.PanierController.PanierItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PanierService {
    private final Connection connection;

    public PanierService(Connection connection) {
        this.connection = connection;
    }

    public List<PanierItem> getPanierItems(int userId) throws SQLException {
        List<PanierItem> items = new ArrayList<>();
        String query = "SELECT p.id, p.nom, p.prix, pi.quantite FROM panier pi " +
                "JOIN produit p ON pi.produit_id = p.id WHERE pi.utilisateur_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String nom = rs.getString("nom");
                double prix = rs.getDouble("prix");
                int quantite = rs.getInt("quantite");
                items.add(new PanierItem(id, nom, prix, quantite));
            }
        }
        return items;
    }

    public void modifierQuantite(int produitId, int quantite) throws SQLException {
        String query = "UPDATE panier SET quantite = ? WHERE produit_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, quantite);
            stmt.setInt(2, produitId);
            stmt.executeUpdate();
        }
    }

    public void supprimerProduit(int produitId) throws SQLException {
        String query = "DELETE FROM panier WHERE produit_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, produitId);
            stmt.executeUpdate();
        }
    }

    public void passerCommande(int userId, List<PanierItem> items) throws SQLException {
        String query = "INSERT INTO commande (user_id, produit_id, quantite) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            for (PanierItem item : items) {
                stmt.setInt(1, userId);
                stmt.setInt(2, item.getId());
                stmt.setInt(3, item.getQuantite());
                stmt.executeUpdate();
            }
        }
    }
}
