package tn.esprit.pidev.gestion_rdv.services;

import tn.esprit.pidev.gestion_rdv.Model.Consultation;
import tn.esprit.pidev.Database.Database;

import java.sql.*;

import java.util.ArrayList;
import java.util.List;

public class ConsultationService {
    private Connection connection;

    public ConsultationService() {
        this.connection = Database.getConnection();
    }

    // Create
    public void addConsultation(Consultation consultation) throws SQLException {
        String query = "INSERT INTO consultation (date, heure, prix, modeconsultation, etaenum, patient_id, meet_link) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setDate(1, Date.valueOf(consultation.getDate()));
            statement.setTime(2, Time.valueOf(consultation.getHeure()));
            statement.setDouble(3, consultation.getPrix());
            statement.setString(4, consultation.getModeConsultation());
            statement.setString(5, consultation.getEtatEnum());
            statement.setInt(6, consultation.getPatientId());
            statement.setString(7, consultation.getMeetLink());

            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    consultation.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    // Read
    public List<Consultation> getAllConsultations() throws SQLException {
        List<Consultation> consultations = new ArrayList<>();
        String query = "SELECT * FROM consultation";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                Consultation consultation = new Consultation(
                        resultSet.getInt("id"),
                        resultSet.getDate("date").toLocalDate(),
                        resultSet.getTime("heure").toLocalTime(),
                        resultSet.getDouble("prix"),
                        resultSet.getString("modeconsultation"),
                        resultSet.getString("etaenum"),
                        resultSet.getInt("patient_id"),
                        resultSet.getString("meet_link")
                );
                consultations.add(consultation);
            }
        }
        return consultations;
    }

    // Update
    public void updateConsultation(Consultation consultation) throws SQLException {
        String query = "UPDATE consultation SET date = ?, heure = ?, prix = ?, modeconsultation = ?, etaenum = ?, patient_id = ?, meet_link = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setDate(1, Date.valueOf(consultation.getDate()));
            statement.setTime(2, Time.valueOf(consultation.getHeure()));
            statement.setDouble(3, consultation.getPrix());
            statement.setString(4, consultation.getModeConsultation());
            statement.setString(5, consultation.getEtatEnum());
            statement.setInt(6, consultation.getPatientId());
            statement.setString(7, consultation.getMeetLink());
            statement.setInt(8, consultation.getId());

            statement.executeUpdate();
        }
    }

    // Delete
    public void deleteConsultation(int id) throws SQLException {
        String query = "DELETE FROM consultation WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }

    // Get by ID
    public Consultation getConsultationById(int id) throws SQLException {
        String query = "SELECT * FROM consultation WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new Consultation(
                            resultSet.getInt("id"),
                            resultSet.getDate("date").toLocalDate(),
                            resultSet.getTime("heure").toLocalTime(),
                            resultSet.getDouble("prix"),
                            resultSet.getString("modeconsultation"),
                            resultSet.getString("etaenum"),
                            resultSet.getInt("patient_id"),
                            resultSet.getString("meet_link")
                    );
                }
            }
        }
        return null;
    }
}
