package pii.services;

import pii.entities.Produit_categorie; // Import de la classe d'entité correcte
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Produit_CategoriesService {

    private Connection connection;

    // Constructeur pour initialiser la connexion à la base de données
    public Produit_CategoriesService() {
        try {
            this.connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/pi", "root", "");
        } catch (SQLException e) {
            e.printStackTrace();  // Assurez-vous que la connexion s'établit correctement
        }
    }

    // Ajouter une catégorie
    public void ajouter(Produit_categorie categorie) throws SQLException {
        String query = "INSERT INTO produit_categories (nom) VALUES (?)"; // Nom de la table mis à jour
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
        String query = "SELECT * FROM produit_categories"; // Nom de la table mis à jour
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                categories.add(new Produit_categorie(rs.getInt("id"), rs.getString("nom")));
            }
        }
        return categories;
    }

    // Trouver une catégorie par ID
    public Produit_categorie trouverParId(int id) throws SQLException {
        String query = "SELECT * FROM produit_categories WHERE id = ?"; // Nom de la table mis à jour
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
        String query = "UPDATE produit_categories SET nom = ? WHERE id = ?"; // Nom de la table mis à jour
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
        String query = "DELETE FROM produit_categories WHERE id = ?"; // Nom de la table mis à jour
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
}
