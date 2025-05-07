package tn.esprit.pidev.gestion_activite.services;

import tn.esprit.pidev.gestion_activite.entities.User;
import tn.esprit.pidev.Database.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserService {
    private Connection connection;

    public UserService() {
        connection = Database.getConnection();
    }

    public User findById(int id) {
        String sql = "SELECT * FROM user WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new User(rs.getInt("id"), rs.getString("email"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}