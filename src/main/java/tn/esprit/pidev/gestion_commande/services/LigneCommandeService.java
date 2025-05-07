package tn.esprit.pidev.gestion_commande.services;

import tn.esprit.pidev.gestion_commande.entities.LigneCommande;
import tn.esprit.pidev.gestion_commande.entities.Produit;
import tn.esprit.pidev.Database.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LigneCommandeService implements IService<LigneCommande> {

    private final Connection connection;

    public LigneCommandeService() {
        connection = Database.getConnection();
    }

    @Override
    public void ajouter(LigneCommande ligne) throws SQLException {
        // Vérifier si la commande existe
        if (!commandeExists(ligne.getCommandeId())) {
            throw new SQLException("Impossible d'ajouter la ligne de commande : La commande avec l'ID " + ligne.getCommandeId() + " n'existe pas dans la base de données.");
        }

        // Vérifier si le produit existe
        if (!produitExists(ligne.getProduit())) {
            throw new SQLException("Impossible d'ajouter la ligne de commande : Le produit avec l'ID " + ligne.getProduit() + " n'existe pas dans la base de données.");
        }

        // Vérifier si la quantité est positive
        if (ligne.getQuantite() <= 0) {
            throw new SQLException("Impossible d'ajouter la ligne de commande : La quantité doit être positive.");
        }

        // Vérifier si le prix est positif
        if (ligne.getPrix() <= 0) {
            throw new SQLException("Impossible d'ajouter la ligne de commande : Le prix doit être positif.");
        }

        // Vérifier le stock disponible
        ProduitService produitService = new ProduitService();
        Produit produit = produitService.getProduitById(ligne.getProduit());
        if (produit == null || produit.getStock() < ligne.getQuantite()) {
            throw new SQLException("Impossible d'ajouter la ligne de commande : Stock insuffisant pour le produit " + produit.getNom());
        }

        // Démarrer une transaction
        connection.setAutoCommit(false);
        try {
            // Ajouter la ligne de commande
            String query = "INSERT INTO ligne_commande (produit_id, quantite, prix_unitaire, commande_id) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, ligne.getProduit());
                ps.setInt(2, ligne.getQuantite());
                ps.setDouble(3, ligne.getPrix());
                ps.setInt(4, ligne.getCommandeId());
                ps.executeUpdate();

                // Récupérer l'ID généré
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        ligne.setId(rs.getInt(1));
                    }
                }
            }

            // Mettre à jour le stock
            produitService.mettreAJourStock(ligne.getProduit(), ligne.getQuantite());

            // Valider la transaction
            connection.commit();
        } catch (SQLException e) {
            // En cas d'erreur, annuler la transaction
            connection.rollback();
            throw new SQLException("Erreur lors de l'ajout de la ligne de commande : " + e.getMessage());
        } finally {
            // Restaurer l'auto-commit
            connection.setAutoCommit(true);
        }
    }

    @Override
    public void modifier(LigneCommande ligne) throws SQLException {
        // Vérifier si la ligne de commande existe
        if (!ligneCommandeExists(ligne.getId())) {
            throw new SQLException("La ligne de commande avec l'ID " + ligne.getId() + " n'existe pas");
        }

        // Vérifier si la commande existe
        if (!commandeExists(ligne.getCommandeId())) {
            throw new SQLException("La commande avec l'ID " + ligne.getCommandeId() + " n'existe pas");
        }

        // Vérifier si le produit existe
        if (!produitExists(ligne.getProduit())) {
            throw new SQLException("Le produit avec l'ID " + ligne.getProduit() + " n'existe pas");
        }

        String query = "UPDATE ligne_commande SET produit_id = ?, quantite = ?, prix_unitaire = ?, commande_id = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, ligne.getProduit());
            ps.setInt(2, ligne.getQuantite());
            ps.setDouble(3, ligne.getPrix());
            ps.setInt(4, ligne.getCommandeId());
            ps.setInt(5, ligne.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void supprimer(int id) throws SQLException {
        // Vérifier si la ligne de commande existe
        if (!ligneCommandeExists(id)) {
            throw new SQLException("La ligne de commande avec l'ID " + id + " n'existe pas");
        }

        String query = "DELETE FROM ligne_commande WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public List<LigneCommande> afficher() throws SQLException {
        List<LigneCommande> lignes = new ArrayList<>();
        String query = "SELECT * FROM ligne_commande ORDER BY id DESC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                LigneCommande ligne = new LigneCommande();
                ligne.setId(rs.getInt("id"));
                ligne.setProduit(rs.getInt("produit_id"));
                ligne.setQuantite(rs.getInt("quantite"));
                ligne.setPrix(rs.getDouble("prix_unitaire"));
                ligne.setCommandeId(rs.getInt("commande_id"));
                lignes.add(ligne);
            }
        }

        return lignes;
    }

    private boolean commandeExists(int commandeId) throws SQLException {
        String query = "SELECT COUNT(*) FROM commande WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, commandeId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    private boolean produitExists(int produitId) throws SQLException {
        String query = "SELECT COUNT(*) FROM produit WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, produitId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    private boolean ligneCommandeExists(int id) throws SQLException {
        String query = "SELECT COUNT(*) FROM ligne_commande WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }
}
