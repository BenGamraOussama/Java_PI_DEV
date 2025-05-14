package tn.esprit.pidev.gestion_produit.services;

import tn.esprit.pidev.gestion_produit.entities.Produit_categorie;
import tn.esprit.pidev.Database.Database;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProduitCategorieServices {

    private Connection connection;

    // Constructeur pour initialiser la connexion à la base de données
    public ProduitCategorieServices(Connection connection) {
        // Utiliser la connexion fournie si elle n'est pas null, sinon obtenir une nouvelle connexion
        if (connection != null) {
            this.connection = connection;
        } else {
            this.connection = Database.getConnection();
        }
    }

    // Ajouter une catégorie
    public void ajouter(Produit_categorie categorie) throws SQLException {
        String query = "INSERT INTO produit_categories (nom) VALUES (?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, categorie.getNom());
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Échec de l'ajout de la catégorie.");
            }
        }
    }

    // Lire toutes les catégories
    public List<Produit_categorie> afficher() throws SQLException {
        List<Produit_categorie> categories = new ArrayList<>();
        String query = "SELECT * FROM produit_categories"; // Assurez-vous que le nom de la table est correct

        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                categories.add(new Produit_categorie(rs.getInt("id"), rs.getString("nom")));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des catégories : " + e.getMessage());
            throw e;
        }
        return categories;
    }


    // Méthode alias utilisée par le contrôleur
    public List<Produit_categorie> getAllCategories() throws SQLException {
        return afficher();
    }

    // Trouver une catégorie par nom
    public Produit_categorie getCategorieByNom(String nom) throws SQLException {
        String query = "SELECT * FROM produit_categories WHERE nom = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, nom);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Produit_categorie(rs.getInt("id"), rs.getString("nom"));
                }
            }
        }
        return null;
    }

    // Trouver une catégorie par ID
    public Produit_categorie trouverParId(int id) throws SQLException {
        String query = "SELECT * FROM produit_categories WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Produit_categorie(rs.getInt("id"), rs.getString("nom"));
                }
            }
        }
        return null;
    }

    // Mettre à jour une catégorie
    public void update(Produit_categorie categorie) throws SQLException {
        String query = "UPDATE produit_categories SET nom = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, categorie.getNom());
            stmt.setInt(2, categorie.getId());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Échec de la mise à jour de la catégorie.");
            }
        }
    }

    // Supprimer une catégorie
    public void supprimer(int id) throws SQLException {
        String query = "DELETE FROM produit_categories WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Échec de la suppression de la catégorie.");
            }
        }
    }

    // Fermer la connexion (si nécessaire)
    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    public void modifier(Produit_categorie selected) {

    }
}
