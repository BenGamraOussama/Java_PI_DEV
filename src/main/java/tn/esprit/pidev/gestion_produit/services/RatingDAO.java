package tn.esprit.pidev.gestion_produit.services;


import tn.esprit.pidev.gestion_produit.entities.Rating;
import tn.esprit.pidev.Database.Database;

import java.sql.*;

public class RatingDAO {
    private Connection connection;

    public RatingDAO() throws SQLException {
        // Utiliser la connexion de MyDatabase pour une gestion centralisée
        this.connection = Database.getConnection();
    }

    public void save(Rating rating) throws SQLException {
        String sql = "INSERT INTO arating (produit_id, note) VALUES (?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, rating.getProduitId());
            stmt.setInt(2, rating.getNote());

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    rating.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    public void addRating(int id, int i, int currentUserRating) {
    }

    public double getAverageRatingForProduct(int id) {
        return 0;
    }
}
