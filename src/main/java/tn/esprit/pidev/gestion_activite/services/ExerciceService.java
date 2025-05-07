package tn.esprit.pidev.gestion_activite.services;

import tn.esprit.pidev.gestion_activite.entities.Exercice;
import tn.esprit.pidev.gestion_activite.entities.Activite;
import tn.esprit.pidev.Database.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExerciceService {
    private Connection connection;

    public ExerciceService() {
        try {
            connection = Database.getConnection();
            // Set auto-commit to true to ensure each operation is committed
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void ajouter(Exercice ex) throws SQLException {
        connection = Database.getConnection();
        String req = "INSERT INTO exercice (activite_id, question) VALUES (?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(req, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, ex.getActivite().getId());
            ps.setString(2, ex.getQuestion());
            ps.executeUpdate();

            // Get the generated ID
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    ex.setId(rs.getInt(1));
                }
            }
        }
    }

    public List<Exercice> afficher() throws SQLException {
        connection = Database.getConnection();
        String req = "SELECT e.*, a.titre as activite_titre, a.description as activite_description, " +
                "a.status as activite_status, a.type as activite_type " +
                "FROM exercice e " +
                "JOIN activite a ON e.activite_id = a.id";
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(req)) {
            List<Exercice> list = new ArrayList<>();
            while (rs.next()) {
                Activite activite = new Activite(
                        rs.getInt("activite_id"),
                        rs.getString("activite_titre"),
                        rs.getString("activite_description"),
                        rs.getString("activite_status"),
                        rs.getString("activite_type")
                );

                Exercice ex = new Exercice(
                        rs.getInt("id"),
                        activite,
                        rs.getString("question")
                );
                list.add(ex);
            }
            return list;
        }
    }

    public void modifier(Exercice ex) throws SQLException {
        connection = Database.getConnection();
        String req = "UPDATE exercice SET question = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setString(1, ex.getQuestion());
            ps.setInt(2, ex.getId());
            ps.executeUpdate();
        }
    }

    public void supprimer(int id) throws SQLException {
        connection = Database.getConnection();
        String req = "DELETE FROM exercice WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public Exercice findByActiviteId(int activiteId) throws SQLException {
        connection = Database.getConnection();
        String req = "SELECT e.*, a.titre as activite_titre, a.description as activite_description, " +
                "a.status as activite_status, a.type as activite_type " +
                "FROM exercice e " +
                "JOIN activite a ON e.activite_id = a.id " +
                "WHERE e.activite_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setInt(1, activiteId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Activite activite = new Activite(
                        rs.getInt("activite_id"),
                        rs.getString("activite_titre"),
                        rs.getString("activite_description"),
                        rs.getString("activite_status"),
                        rs.getString("activite_type")
                );
                return new Exercice(
                        rs.getInt("id"),
                        activite,
                        rs.getString("question")
                );
            }
            return null;
        }
    }

    public List<Exercice> getAll() throws SQLException {
        return afficher();
    }

    public boolean hasAnswer(int exerciceId) throws SQLException {
        connection = Database.getConnection();
        String query = "SELECT COUNT(*) FROM reponse WHERE exercice_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, exerciceId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        }
    }
}