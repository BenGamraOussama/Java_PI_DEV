package pii.services;

import pii.entities.Produit;
import pii.entities.Produit_categorie;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProduitServices {

    private Connection connection;

    public ProduitServices() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/pi", "root", "");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Échec de la connexion à la base de données", e);
        }
    }
    // Méthode pour obtenir tous les produits
    public List<Produit> getAll() throws SQLException {
        return readList();  // Appel à la méthode readList pour récupérer tous les produits
    }

    // Ajouter un produit
    public void ajouter(Produit produit) throws SQLException {
        String query = "INSERT INTO produits (nom, description, quantite, prix, id_categorie, image, disponible) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, produit.getNom());
            statement.setString(2, produit.getDescription());
            statement.setInt(3, produit.getQuantite());
            statement.setFloat(4, (float) produit.getPrix());
            statement.setInt(5, produit.getCategorie().getId()); // Utilisation de `id_categorie`
            statement.setString(6, produit.getImage());
            statement.setBoolean(7, produit.isDisponible());
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Échec de l'ajout du produit.");
            }
        }
    }

    // Lire tous les produits
    public List<Produit> readList() throws SQLException {
        List<Produit> produits = new ArrayList<>();
        String query = "SELECT * FROM produits";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Produit produit = new Produit(
                        rs.getInt("id"),
                        new Produit_categorie(rs.getInt("id_categorie"), ""), // Utilisation de `id_categorie`
                        rs.getString("nom"),
                        rs.getString("description"),
                        rs.getBoolean("disponible"),
                        rs.getString("image"),
                        rs.getInt("quantite"),
                        rs.getFloat("prix")
                );
                produits.add(produit);
            }
        }
        return produits;
    }

    // Trouver un produit par ID
    public Produit findById(int id) throws SQLException {
        String query = "SELECT * FROM produits WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Produit(
                            rs.getInt("id"),
                            new Produit_categorie(rs.getInt("id_categorie"), ""), // Utilisation de `id_categorie`
                            rs.getString("nom"),
                            rs.getString("description"),
                            rs.getBoolean("disponible"),
                            rs.getString("image"),
                            rs.getInt("quantite"),
                            rs.getFloat("prix")
                    );
                }
            }
        }
        return null;
    }

    // Modifier un produit
    public void updateProduit(Produit produit) throws SQLException {
        String query = "UPDATE produits SET nom = ?, description = ?, quantite = ?, prix = ?, image = ?, disponible = ?, id_categorie = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, produit.getNom());
            stmt.setString(2, produit.getDescription());
            stmt.setInt(3, produit.getQuantite());
            stmt.setFloat(4, (float) produit.getPrix());
            stmt.setString(5, produit.getImage());
            stmt.setBoolean(6, produit.isDisponible());
            stmt.setInt(7, produit.getCategorie().getId()); // Utilisation de `id_categorie`
            stmt.setInt(8, produit.getId());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Échec de la mise à jour du produit.");
            }
        }
    }

    // Supprimer un produit
    public void supprimerProduit(int id) throws SQLException {
        String query = "DELETE FROM produits WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Échec de la suppression du produit.");
            }
        }
    }

    // Méthode pour fermer la connexion proprement
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
