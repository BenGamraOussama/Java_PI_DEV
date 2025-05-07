package tn.esprit.pidev.gestion_rdv.dao;




import tn.esprit.pidev.gestion_rdv.enteties.Traitement;
import tn.esprit.pidev.Database.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TraitementDAO {
    private Connection connection;

    public TraitementDAO() {
        this.connection = Database.getConnection();
    }

    /**
     * Ajoute un nouveau traitement à la base de données
     * @param traitement Le traitement à ajouter
     * @param consultationId L'ID de la consultation associée
     */
    public void addTraitement(Traitement traitement, int consultationId) {
        String query = "INSERT INTO traitement(type, medicament, suivi, consultation_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, traitement.getType());
            stmt.setString(2, traitement.getMedicament());
            stmt.setString(3, traitement.getSuivi());
            stmt.setInt(4, consultationId);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating traitement failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    traitement.setId(generatedKeys.getInt(1));
                    traitement.setConsultationId(consultationId);
                } else {
                    throw new SQLException("Creating traitement failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Récupère tous les traitements de la base de données
     * @return Liste de tous les traitements
     */
    public List<Traitement> getAllTraitements() {
        List<Traitement> traitements = new ArrayList<>();
        String query = "SELECT * FROM traitement";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Traitement traitement = createTraitementFromResultSet(rs);
                traitements.add(traitement);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return traitements;
    }

    /**
     * Récupère un traitement par son ID
     * @param id L'ID du traitement
     * @return Le traitement correspondant ou null
     */
    public Traitement getTraitementById(int id) {
        String query = "SELECT * FROM traitement WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return createTraitementFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Récupère tous les traitements associés à une consultation
     * @param consultationId L'ID de la consultation
     * @return Liste des traitements associés
     */
    public List<Traitement> getTraitementsByConsultationId(int consultationId) {
        List<Traitement> traitements = new ArrayList<>();
        String query = "SELECT * FROM traitement WHERE consultation_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, consultationId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Traitement traitement = createTraitementFromResultSet(rs);
                traitements.add(traitement);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return traitements;
    }

    /**
     * Met à jour un traitement existant
     * @param traitement Le traitement avec les nouvelles données
     */
    public void updateTraitement(Traitement traitement) {
        String query = "UPDATE traitement SET type = ?, medicament = ?, suivi = ?, consultation_id = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, traitement.getType());
            stmt.setString(2, traitement.getMedicament());
            stmt.setString(3, traitement.getSuivi());
            stmt.setInt(4, traitement.getConsultationId());
            stmt.setInt(5, traitement.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Supprime un traitement par son ID
     * @param id L'ID du traitement à supprimer
     */
    public void deleteTraitement(int id) {
        String query = "DELETE FROM traitement WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Supprime tous les traitements associés à une consultation
     * @param consultationId L'ID de la consultation
     */
    public void deleteTraitementsByConsultationId(int consultationId) {
        String query = "DELETE FROM traitement WHERE consultation_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, consultationId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Méthode utilitaire pour créer un objet Traitement à partir d'un ResultSet
     * @param rs Le ResultSet contenant les données
     * @return Un objet Traitement
     * @throws SQLException En cas d'erreur SQL
     */
    private Traitement createTraitementFromResultSet(ResultSet rs) throws SQLException {
        Traitement traitement = new Traitement(
                rs.getString("type"),
                rs.getString("medicament"),
                rs.getString("suivi")
        );
        traitement.setId(rs.getInt("id"));
        traitement.setConsultationId(rs.getInt("consultation_id"));
        return traitement;
    }
}