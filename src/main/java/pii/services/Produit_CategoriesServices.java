package pii.services;

import pii.entities.Produit_categorie;
import pii.utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Produit_CategoriesServices {

    // Connexion à la base de données
    private Connection cnx = MyDatabase.getInstance().getConnection();

    // CREATE - Ajouter une nouvelle catégorie
    public void ajouter(Produit_categorie categorie) throws SQLException {
        String req = "INSERT INTO produit_categories(nom) VALUES(?)";
        try (PreparedStatement ps = cnx.prepareStatement(req, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, categorie.getNom());
            ps.executeUpdate();

            // Récupérer l'ID généré après l'insertion
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    categorie.setId(generatedKeys.getInt(1)); // Définir l'ID généré
                }
            }
        }
    }

    // READ ALL - Afficher toutes les catégories
    public List<Produit_categorie> afficher() throws SQLException {
        List<Produit_categorie> list = new ArrayList<>();
        String req = "SELECT * FROM produit_categories";
        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(req)) {
            while (rs.next()) {
                list.add(new Produit_categorie(
                        rs.getInt("id"),
                        rs.getString("nom")
                ));
            }
        }
        return list;
    }

    // READ BY ID - Trouver une catégorie par son ID
    public Produit_categorie trouverParId(int id) throws SQLException {
        String req = "SELECT * FROM produit_categories WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Produit_categorie(
                            rs.getInt("id"),
                            rs.getString("nom")
                    );
                }
            }
        }
        return null; // Retourner null si aucune catégorie n'est trouvée
    }

    // UPDATE - Mettre à jour une catégorie existante
    public void modifier(Produit_categorie categorie) throws SQLException {
        String sql = "UPDATE produit_categories SET nom = ? WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, categorie.getNom()); // Mettre à jour le nom de la catégorie
            ps.setInt(2, categorie.getId()); // Utiliser l'ID de la catégorie
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                System.out.println("Aucune catégorie modifiée, vérifie l'ID.");
            }
        }
    }

    // DELETE - Supprimer une catégorie par son ID
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM produit_categories WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, id);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                System.out.println("Aucune catégorie supprimée, vérifie l'ID.");
            }
        }
    }

    // Vérifier si une catégorie avec un nom donné existe déjà
    public boolean nomExiste(String nom) {
        String sql = "SELECT COUNT(*) FROM produit_categories WHERE nom = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, nom);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // Retourne vrai si une catégorie avec ce nom existe
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Retourne faux si aucune catégorie avec ce nom n'existe
    }
    public void update(Produit_categorie categorie) throws SQLException {
        String sql = "UPDATE produit_categories SET nom = ? WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, categorie.getNom());
            ps.setInt(2, categorie.getId());
            ps.executeUpdate();
        }
    }

    public Produit_categorie getCategorieById(int id) throws SQLException {
        String req = "SELECT * FROM produit_categories WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Produit_categorie(
                            rs.getInt("id"),
                            rs.getString("nom")
                    );
                }
            }
        }
        return null;
    }

}
