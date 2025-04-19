package org.example.pi__dev_.dao;








import org.example.pi__dev_.enteties.Consultation;
import org.example.pi__dev_.enteties.Etat;
import org.example.pi__dev_.enteties.Patient;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConsultationDAO {
    private Connection connection;

    public ConsultationDAO() {
        this.connection = DatabaseConnection.getConnection();
    }

    public void addConsultation(Consultation consultation) {
        String query = "INSERT INTO consultations(date, heure, prix, modeconsultation, etatenum, p) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setDate(1, new java.sql.Date(consultation.getDate().getTime()));
            stmt.setTime(2, consultation.getHeure());
            stmt.setDouble(3, consultation.getPrix());
            stmt.setString(4, consultation.getModeconsultation());
            stmt.setString(5, consultation.getEtatenum().name());
            stmt.setInt(6, consultation.getPatientId());
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    consultation.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Consultation> getAllConsultations() {
        List<Consultation> consultations = new ArrayList<>();
        String query = "SELECT c.*, p.dossier_medical FROM consultation c " +
                "LEFT JOIN patient p ON c.patient_id = p.id";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                try {
                    // Safely handle possible null dates
                    Date sqlDate = rs.getDate("date");
                    if (sqlDate == null) {
                        System.err.println("Warning: Null date found for consultation ID " + rs.getInt("id"));
                        continue; // Skip this record or handle differently
                    }

                    // Safely handle time
                    Time time = rs.getTime("heure");
                    if (time == null) {
                        System.err.println("Warning: Null time found for consultation ID " + rs.getInt("id"));
                        continue;
                    }

                    // Get patient ID (can't be null due to foreign key constraint)
                    int patientId = rs.getInt("patient_id");
                    if (rs.wasNull()) { // Check if SQL NULL was converted to 0
                        System.err.println("Warning: Null patient_id found for consultation ID " + rs.getInt("id"));
                        continue;
                    }

                    Consultation consultation = new Consultation(
                            sqlDate,
                            time,
                            rs.getDouble("prix"), // Assuming prix can't be null
                            rs.getString("modeconsultation"), // Handle null if needed
                            Etat.valueOf(rs.getString("etat")), // This can throw IllegalArgumentException
                            patientId
                    );

                    consultation.setId(rs.getInt("id"));
                    consultations.add(consultation);

                } catch (IllegalArgumentException e) {
                    System.err.println("Error processing consultation record: " + e.getMessage());
                    // Continue to next record instead of failing completely
                    continue;
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error fetching consultations: " + e.getMessage());
            e.printStackTrace();
            // Consider throwing a custom exception
        }
        return consultations;
    }
    public void updateConsultation(Consultation consultation) {
        String query = "UPDATE consultation SET date = ?, heure = ?, prix = ?, modeconsultation = ?, etat = ?, patient_id = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setDate(1, new java.sql.Date(consultation.getDate().getTime()));
            stmt.setTime(2, consultation.getHeure());
            stmt.setDouble(3, consultation.getPrix());
            stmt.setString(4, consultation.getModeconsultation());
            stmt.setString(5, consultation.getEtatenum().name());
            stmt.setInt(6, consultation.getPatientId());
            stmt.setInt(7, consultation.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteConsultation(int id) {
        // First delete related traitements (cascade)
        TraitementDAO traitementDAO = new TraitementDAO();
        traitementDAO.deleteTraitementsByConsultationId(id);

        // Then delete the consultation
        String query = "DELETE FROM consultation WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Consultation getConsultationById(int id) {
        String query = "SELECT c.*, p.dossier_medical FROM consultation c " +
                "LEFT JOIN patient p ON c.patient_id = p.id " +
                "WHERE c.id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Create Patient object with both id and medical record
                Patient patient = new Patient(
                        rs.getInt("patient_id"),
                        rs.getString("dossier_medical")
                );

                Consultation consultation = new Consultation(
                        rs.getDate("date"),
                        rs.getTime("heure"),
                        rs.getDouble("prix"),
                        rs.getString("modeconsultation"),
                        Etat.valueOf(rs.getString("etat")),
                        patient.getId()
                );

                consultation.setId(rs.getInt("id"));
                return consultation;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Consider proper error handling/logging
        }
        return null;
    }


}