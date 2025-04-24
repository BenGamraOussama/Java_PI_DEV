package com.exemple.services;

import com.exemple.entities.Produit;
import com.exemple.utils.MyDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProduitService {

    private final Connection connection;

    public ProduitService() {
        connection = MyDatabase.getInstance().getConnection();
    }

    public Produit getProduitById(int produitId) throws SQLException {
        String query = "SELECT * FROM produit WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, produitId);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            Produit produit = new Produit();
            produit.setId(rs.getInt("id"));
            produit.setCategorieId(rs.getInt("categorie_id"));
            produit.setNom(rs.getString("nom"));
            produit.setDescription(rs.getString("description"));
            produit.setPrix(rs.getDouble("prix"));
            produit.setDisponible(rs.getInt("disponible"));
            produit.setImage(rs.getString("image"));
            return produit;
        } else {
            throw new SQLException("Produit avec ID " + produitId + " non trouvé !");
        }
    }
}
