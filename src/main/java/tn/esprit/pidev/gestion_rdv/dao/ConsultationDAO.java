package tn.esprit.pidev.gestion_rdv.dao;



import tn.esprit.pidev.gestion_rdv.enteties.Consultation;
import tn.esprit.pidev.gestion_rdv.enteties.Etat;
import tn.esprit.pidev.gestion_rdv.enteties.Patient;
import tn.esprit.pidev.Database.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConsultationDAO {
    private Connection connection;

    public ConsultationDAO() {
        this.connection = Database.getConnection();
    }

    public void addConsultation(Consultation consultation) {
        String query = "INSERT INTO `consultation`(`patient_id`, `date`, `heure`, `prix`, `modeconsultation`, `etat`, `zoom_link`)" +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            // Get patient ID from the Patient object
            int patientId = consultation.getPatient_id() != null ? consultation.getPatient_id().getId() : 0;
            stmt.setInt(1, patientId);
            stmt.setDate(2, consultation.getDate());
            stmt.setTime(3, consultation.getHeure());
            stmt.setDouble(4, consultation.getPrix());
            stmt.setString(5, consultation.getModeconsultation());
            stmt.setString(6, consultation.getEtatenum().name());
            // Handle null zoomLink by using empty string
            String zoomLink = consultation.getZoomLink();
            stmt.setString(7, zoomLink != null ? zoomLink : "");
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
        String query = "SELECT * FROM consultation";

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

                    // Safely handle etat value
                    Etat etat;
                    try {
                        String etatStr = rs.getString("etat");
                        if (etatStr == null || etatStr.trim().isEmpty()) {
                            etat = Etat.EN_ATTENTE;
                        } else {
                            try {
                                etat = Etat.valueOf(etatStr.trim());
                            } catch (IllegalArgumentException e) {
                                System.err.println("Invalid etat value for consultation ID " + rs.getInt("id") + ": " + etatStr);
                                etat = Etat.EN_ATTENTE; // Default to EN_ATTENTE if invalid
                            }
                        }
                    } catch (SQLException e) {
                        System.err.println("Error retrieving etat for consultation ID " + rs.getInt("id") + ": " + e.getMessage());
                        etat = Etat.EN_ATTENTE; // Default to EN_ATTENTE if there's an SQL error
                    }

                    // Create Patient object if patient_id exists
                    Patient patient = null;
                    try {
                        int patientId = rs.getInt("p_id");
                        if (!rs.wasNull()) {
                            patient = new Patient(
                                patientId,
                                rs.getString("dossier_medical"),
                                rs.getString("firstName"),
                                rs.getString("lastName"),
                                rs.getString("email")
                            );
                        }
                    } catch (SQLException e) {
                        System.err.println("Error retrieving patient data for consultation ID " + rs.getInt("id") + ": " + e.getMessage());
                    }

                    Consultation consultation = new Consultation(
                            sqlDate,
                            time,
                            rs.getDouble("prix"), // Assuming prix can't be null
                            rs.getString("modeconsultation"), // Handle null if needed
                            etat,
                            rs.getString("zoom_link") != null ? rs.getString("zoom_link") : "",
                            patient
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
        String query =  "UPDATE consultation SET " + "patient_id = ?, " + "date = ?, " + "heure = ?, " + "prix = ?, " + "modeconsultation = ?, " + "etat = ?, " + "zoom_link = ? " + "WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            // Get patient ID from the Patient object
            int patientId = consultation.getPatient_id() != null ? consultation.getPatient_id().getId() : 0;
            stmt.setInt(1, patientId);
            stmt.setDate(2, consultation.getDate());
            stmt.setTime(3, consultation.getHeure());
            stmt.setDouble(4, consultation.getPrix());
            stmt.setString(5, consultation.getModeconsultation());
            stmt.setString(6, consultation.getEtatenum().name());
            // Handle null zoomLink by using empty string
            String zoomLink = consultation.getZoomLink();
            stmt.setString(7, zoomLink != null ? zoomLink : "");
            stmt.setInt(8, consultation.getId());

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
        String query = "SELECT c.*, p.id as p_id, p.dossier_medical, p.firstName, p.lastName, p.email " +
                       "FROM consultation c " +
                       "LEFT JOIN patient p ON c.patient_id = p.id " +
                       "WHERE c.id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Safely handle etat value
                Etat etat;
                try {
                    String etatStr = rs.getString("etat");
                    if (etatStr == null || etatStr.trim().isEmpty()) {
                        etat = Etat.EN_ATTENTE;
                    } else {
                        try {
                            etat = Etat.valueOf(etatStr.trim());
                        } catch (IllegalArgumentException e) {
                            System.err.println("Invalid etat value for consultation ID " + rs.getInt("id") + ": " + etatStr);
                            etat = Etat.EN_ATTENTE; // Default to EN_ATTENTE if invalid
                        }
                    }
                } catch (SQLException e) {
                    System.err.println("Error retrieving etat for consultation ID " + rs.getInt("id") + ": " + e.getMessage());
                    etat = Etat.EN_ATTENTE; // Default to EN_ATTENTE if there's an SQL error
                }

                // Create Patient object if patient_id exists
                Patient patient = null;
                try {
                    int patientId = rs.getInt("p_id");
                    if (!rs.wasNull()) {
                        patient = new Patient(
                            patientId,
                            rs.getString("dossier_medical"),
                            rs.getString("firstName"),
                            rs.getString("lastName"),
                            rs.getString("email")
                        );
                    }
                } catch (SQLException e) {
                    System.err.println("Error retrieving patient data for consultation ID " + rs.getInt("id") + ": " + e.getMessage());
                }

                Consultation consultation = new Consultation(
                        rs.getDate("date"),
                        rs.getTime("heure"),
                        rs.getDouble("prix"),
                        rs.getString("modeconsultation"),
                        etat,
                        rs.getString("zoom_link") != null ? rs.getString("zoom_link") : "",
                        patient
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
