package pii.services;

import pii.entities.Produit;
import pii.entities.Produit_categorie;
import pii.utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProduitServices {
    private Connection cnx = MyDatabase.getInstance().getConnection();
    private Produit_CategoriesServices categorieService = new Produit_CategoriesServices();

    // Ajouter un produit à la base de données
    public void ajouter(Produit produit) throws SQLException {
        if (produit.getCategorie() == null || produit.getCategorie().getId() == 0) {
            throw new SQLException("La catégorie du produit doit être spécifiée");
        }

        String req = "INSERT INTO produits(id_categorie, nom, description, disponible, image, quantite, average_rating) " +
                "VALUES(?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = cnx.prepareStatement(req, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, produit.getCategorie().getId());
            ps.setString(2, produit.getNom());
            ps.setString(3, produit.getDescription());
            ps.setBoolean(4, produit.isDisponible());
            ps.setString(5, produit.getImage());
            ps.setInt(6, produit.getQuantite());
            ps.setDouble(7, produit.getAverageRating());

            ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    produit.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Erreur lors de l'ajout du produit : " + e.getMessage());
        }
    }

    // Lire la liste des produits
    public List<Produit> readList() throws SQLException {
        List<Produit> produits = new ArrayList<>();
        String req = "SELECT p.*, pc.nom as categorie_nom FROM produits p " +
                "JOIN produit_categories pc ON p.id_categorie = pc.id";

        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(req)) {
            while (rs.next()) {
                Produit_categorie categorie = new Produit_categorie(
                        rs.getInt("id_categorie"),
                        rs.getString("categorie_nom")
                );

                Produit produit = new Produit(
                        rs.getInt("id"),
                        categorie,
                        rs.getString("nom"),
                        rs.getString("description"),
                        rs.getBoolean("disponible"),
                        rs.getString("image"),
                        rs.getInt("quantite"),
                        rs.getDouble("average_rating")
                );

                produits.add(produit);
            }
        }
        return produits;
    }

    // Mise à jour d'un produit
    public void updateProduit(Produit p) throws SQLException {

    }

    // Suppression d'un produit
    public void deleteProduit(int id) throws SQLException {
        String sql = "DELETE FROM produits WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // Trouver un produit par ID
    public Produit findById(int id) throws SQLException {
        String req = "SELECT p.*, pc.nom as categorie_nom FROM produits p " +
                "JOIN produit_categories pc ON p.id_categorie = pc.id WHERE p.id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Produit_categorie categorie = new Produit_categorie(
                            rs.getInt("id_categorie"),
                            rs.getString("categorie_nom")
                    );
                    return new Produit(
                            rs.getInt("id"),
                            categorie,
                            rs.getString("nom"),
                            rs.getString("description"),
                            rs.getBoolean("disponible"),
                            rs.getString("image"),
                            rs.getInt("quantite"),
                            rs.getDouble("average_rating")
                    );
                }
            }
        }
        return null;
    }

    // Afficher tous les produits avec jointure correcte
    public List<Produit> afficher() throws SQLException {
        List<Produit> produits = new ArrayList<>();
        String req = "SELECT p.*, pc.nom as categorie_nom FROM produits p " +
                "JOIN produit_categories pc ON p.id_categorie = pc.id";

        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(req)) {
            while (rs.next()) {
                Produit produit = new Produit(
                        rs.getInt("id"),
                        new Produit_categorie(rs.getInt("id_categorie"), rs.getString("categorie_nom")),
                        rs.getString("nom"),
                        rs.getString("description"),
                        rs.getBoolean("disponible"),
                        rs.getString("image"),
                        rs.getInt("quantite"),
                        rs.getDouble("average_rating")
                );
                produits.add(produit);
            }
        }
        return produits;
    }

    public void delete(int id) {
    }

    public void supprimer(int id) {
    }
}
