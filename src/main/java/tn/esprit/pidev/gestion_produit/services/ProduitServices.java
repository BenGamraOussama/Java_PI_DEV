package tn.esprit.pidev.gestion_produit.services;

import tn.esprit.pidev.gestion_produit.entities.Produit;
import tn.esprit.pidev.gestion_produit.entities.Produit_categorie;
import tn.esprit.pidev.Database.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProduitServices {
    private final Connection connection;

    public ProduitServices() throws SQLException {
        this.connection = Database.getConnection();
        if (this.connection == null) {
            throw new SQLException("La connexion à la base de données a échoué");
        }
    }

    public List<Produit> getAllProduits() {
        List<Produit> produits = new ArrayList<>();
        String query = "SELECT p.*, pc.nom as categorie_nom FROM produit p " +
                "LEFT JOIN produit_categories pc ON p.categorie_id = pc.id"; // Correction du nom de colonne

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Produit produit = new Produit(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getDouble("prix"),
                        rs.getInt("quantite"),
                        rs.getString("description"),
                        rs.getBoolean("disponible"),
                        rs.getBytes("image"), // Utilise la colonne image
                        new Produit_categorie(
                                rs.getInt("categorie_id"), // Correction du nom de colonne
                                rs.getString("categorie_nom")
                        )
                );
                produits.add(produit);
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL: " + e.getMessage());
            // Fallback sans catégories
            return getProduitsSansCategorie();
        }
        return produits;
    }

    private List<Produit> getProduitsSansCategorie() {
        List<Produit> produits = new ArrayList<>();
        String query = "SELECT * FROM produit";

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Produit produit = new Produit(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getDouble("prix"),
                        rs.getInt("quantite"),
                        rs.getString("description"),
                        rs.getBoolean("disponible"),
                        rs.getBytes("image"),
                        null // Aucune catégorie
                );
                produits.add(produit);
            }
        } catch (SQLException e) {
            System.err.println("Erreur de secours: " + e.getMessage());
        }
        return produits;
    }

    public boolean addProduit(Produit produit) {
        String query = "INSERT INTO produit (nom, prix, quantite, description, disponible, image, categorie_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            setProduitParameters(stmt, produit);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        produit.setId(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout du produit: " + e.getMessage());
        }
        return false;
    }

    public boolean updateProduit(Produit produit) {
        String query = "UPDATE produit SET nom = ?, prix = ?, quantite = ?, description = ?, " +
                "disponible = ?, image = ?, categorie_id = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            setProduitParameters(stmt, produit);
            stmt.setInt(8, produit.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du produit: " + e.getMessage());
        }
        return false;
    }

    public boolean deleteProduit(int id) {
        String query = "DELETE FROM produit WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du produit: " + e.getMessage());
        }
        return false;
    }

    // Méthodes utilitaires
    private Produit mapResultSetToProduit(ResultSet rs) throws SQLException {
        Produit_categorie categorie = new Produit_categorie(
                rs.getInt("categorie_id"), // Correction du nom de colonne
                rs.getString("categorie_nom")
        );

        return new Produit(
                rs.getInt("id"),
                rs.getString("nom"),
                rs.getDouble("prix"),
                rs.getInt("quantite"),
                rs.getString("description"),
                rs.getBoolean("disponible"),
                rs.getBytes("image"), // Utilise la colonne image
                categorie
        );
    }

    private void setProduitParameters(PreparedStatement stmt, Produit produit) throws SQLException {
        stmt.setString(1, produit.getNom());
        stmt.setDouble(2, produit.getPrix());
        stmt.setInt(3, produit.getQuantite());
        stmt.setString(4, produit.getDescription());
        stmt.setBoolean(5, produit.isDisponible());

        if (produit.getImage() != null && produit.getImage().length > 0) {
            stmt.setBytes(6, produit.getImage());
        } else {
            stmt.setNull(6, Types.BLOB);
        }

        stmt.setInt(7, produit.getCategorie().getId()); // id_categorie
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la fermeture de la connexion: " + e.getMessage());
        }
    }

    public boolean supprimerProduit(int id) {
        return false;
    }

    public List<Produit> readList() {
        return getAllProduits();
    }

    public void ajouter(Produit p) {
        String sql = "INSERT INTO produit (nom, prix, description) VALUES (?, ?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, p.getNom());
            stmt.setDouble(2, p.getPrix());
            stmt.setString(3, p.getDescription());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace(); // Ou meilleure gestion d'erreur
        }
    }


    public Produit findById(int id) {
        return null;
    }

    public List<Produit_categorie> getCategories() {
        return List.of();
    }

    public void ajouterCategorie(Produit_categorie selectedCategory) {
    }

    public void supprimerCategorie(Produit_categorie categorie) throws SQLException {
        String query = "DELETE FROM categorie WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, categorie.getId());  // Assure-toi que `getId()` renvoie l'ID de la catégorie
            stmt.executeUpdate();
        }
    }
    public void ajouterProduitAuPanier(Produit produit, int quantite) throws SQLException {
        // Utiliser l'ID de l'utilisateur connecté
        int utilisateurId = tn.esprit.pidev.Model.User.connecte != null ?
                            tn.esprit.pidev.Model.User.connecte.getId() :
                            1; // Fallback à 1 si aucun utilisateur n'est connecté

        // Vérifie si le produit est déjà dans le panier de cet utilisateur
        String checkQuery = "SELECT COUNT(*) FROM panier WHERE produit_id = ? AND utilisateur_id = ?";
        try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
            checkStmt.setInt(1, produit.getId());
            checkStmt.setInt(2, utilisateurId);
            try (ResultSet rs = checkStmt.executeQuery()) {
                rs.next();
                if (rs.getInt(1) > 0) {
                    System.out.println("❗ Ce produit est déjà dans le panier de l'utilisateur.");
                    return;
                }
            }
        }

        // Ajout du produit dans le panier
        String insertQuery = "INSERT INTO panier (produit_id, quantite, utilisateur_id) VALUES (?, ?, ?)";
        try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
            insertStmt.setInt(1, produit.getId());
            insertStmt.setInt(2, quantite);
            insertStmt.setInt(3, utilisateurId);
            insertStmt.executeUpdate();
            System.out.println("✅ Produit ajouté au panier avec succès !");
        }
    }
    public void modifierProduit(Produit produit) throws SQLException {
        String query = "UPDATE produit SET nom = ?, description = ?, quantite = ?, disponible = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, produit.getNom());
            stmt.setString(2, produit.getDescription());
            stmt.setInt(3, produit.getQuantite());
            stmt.setBoolean(4, produit.isDisponible());
            stmt.setInt(5, produit.getId());
            stmt.executeUpdate();
        }
    }



}
