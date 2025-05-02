package com.exemple.services;

import com.exemple.entities.Produit;
import com.exemple.utils.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProduitService {
    private Connection connection;

    public ProduitService() {
        try {
            this.connection = DatabaseConnection.getConnection();
            if (this.connection == null) {
                throw new SQLException("La connexion à la base de données a échoué");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'initialisation du service : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<Produit> getAllProduits() {
        List<Produit> produits = new ArrayList<>();
        if (connection == null) {
            System.err.println("La connexion à la base de données n'est pas établie");
            return produits;
        }

        String query = "SELECT * FROM produit";
        
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            
            while (resultSet.next()) {
                Produit produit = new Produit();
                produit.setId(resultSet.getInt("id"));
                produit.setNom(resultSet.getString("nom"));
                produit.setDescription(resultSet.getString("description"));
                produit.setPrix(resultSet.getDouble("prix"));
                produit.setStock(resultSet.getInt("stock"));
                produit.setImage(resultSet.getString("image"));
                produit.setDisponible(resultSet.getBoolean("disponible"));
                produits.add(produit);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des produits : " + e.getMessage());
            e.printStackTrace();
        }
        
        return produits;
    }

    public List<Produit> afficher() {
        List<Produit> produits = getAllProduits();
        System.out.println("Liste des produits :");
        System.out.println("----------------------------------------");
        for (Produit produit : produits) {
            System.out.println("ID: " + produit.getId());
            System.out.println("Nom: " + produit.getNom());
            System.out.println("Description: " + produit.getDescription());
            System.out.println("Prix: " + produit.getPrix());
            System.out.println("Stock: " + produit.getStock());
            System.out.println("Image: " + produit.getImage());
            System.out.println("Disponible: " + produit.isDisponible());
            System.out.println("----------------------------------------");
        }
        return produits;
    }

    public Produit getProduitById(int id) {
        String query = "SELECT * FROM produit WHERE id = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                Produit produit = new Produit();
                produit.setId(resultSet.getInt("id"));
                produit.setNom(resultSet.getString("nom"));
                produit.setDescription(resultSet.getString("description"));
                produit.setPrix(resultSet.getDouble("prix"));
                produit.setStock(resultSet.getInt("stock"));
                produit.setImage(resultSet.getString("image"));
                produit.setDisponible(resultSet.getBoolean("disponible"));
                return produit;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }

    public void ajouter(Produit produit) {
        String query = "INSERT INTO produit (nom, description, prix, stock, image, disponible) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, produit.getNom());
            statement.setString(2, produit.getDescription());
            statement.setDouble(3, produit.getPrix());
            statement.setInt(4, produit.getStock());
            statement.setString(5, produit.getImage());
            statement.setBoolean(6, produit.isDisponible());
            
            statement.executeUpdate();
            
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    produit.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void modifier(Produit produit) {
        String query = "UPDATE produit SET nom = ?, description = ?, prix = ?, stock = ?, image = ?, disponible = ? WHERE id = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, produit.getNom());
            statement.setString(2, produit.getDescription());
            statement.setDouble(3, produit.getPrix());
            statement.setInt(4, produit.getStock());
            statement.setString(5, produit.getImage());
            statement.setBoolean(6, produit.isDisponible());
            statement.setInt(7, produit.getId());
            
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void supprimer(int id) {
        String query = "DELETE FROM produit WHERE id = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void mettreAJourStock(int id, int quantite) {
        String query = "UPDATE produit SET stock = stock - ? WHERE id = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, quantite);
            statement.setInt(2, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
