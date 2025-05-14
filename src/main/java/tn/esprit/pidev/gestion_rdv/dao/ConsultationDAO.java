package tn.esprit.pidev.gestion_rdv.dao;



import tn.esprit.pidev.gestion_rdv.enteties.Consultation;
import tn.esprit.pidev.gestion_rdv.enteties.Etat;
import tn.esprit.pidev.gestion_rdv.enteties.Patient;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConsultationDAO {
    private Connection connection;

    public ConsultationDAO() {
        this.connection = DatabaseConnection.getConnection();
    }

    public void addConsultation(Consultation consultation) {
        String query = "INSERT INTO consultation(date, heure, prix, modeconsultation, etat, patient_id, meet_link) " +
                "VALUES (?, ?, ?, ?, ?, ?,?)";
        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setDate(1, consultation.getDate());
            stmt.setTime(2, consultation.getHeure());
            stmt.setDouble(3, consultation.getPrix());
            stmt.setString(4, consultation.getModeconsultation());
            stmt.setString(5, consultation.getEtatenum().name());
            stmt.setInt(6, consultation.getPatient().getId());
            stmt.setString(7, consultation.getMeetLink());

            stmt.executeUpdate();
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
                            patient,
                            rs.getString("meet_link") != null ? rs.getString("meet_link") : "");

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
        String query =  "UPDATE consultation SET "  + "date = ?, " + "heure = ?, " + "prix = ?, " + "modeconsultation = ?, " + "etat = ?, " +"patient_id = ?, " + "meetLink = ? " + "WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            // Get patient ID from the Patient object
            
            stmt.setDate(1, consultation.getDate());
            stmt.setTime(2, consultation.getHeure());
            stmt.setDouble(3, consultation.getPrix());
            stmt.setString(4, consultation.getModeconsultation());
            stmt.setString(5, consultation.getEtatenum().name());
            stmt.setInt(6, consultation.getPatient().getId());
            // Handle null zoomLink by using empty string
            String zoomLink = consultation.getMeetLink();
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
        String query = "SELECT c.*, p.dossier_medical, p.firstName, p.lastName, p.email " +
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
                        patient,
                        rs.getString("zoom_link") != null ? rs.getString("zoom_link") : ""
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
    private Consultation mapResultSetToConsultation(ResultSet rs) throws SQLException {
        Consultation consultation = new Consultation();
        consultation.setId(rs.getInt("id"));
        consultation.setDate(rs.getDate("date"));
        consultation.setHeure(rs.getTime("heure"));
        consultation.setPrix(rs.getDouble("prix"));
        consultation.setModeconsultation(rs.getString("modeconsultation"));

        // Gestion de l'état
        try {
            String etatStr = rs.getString("etat");
            consultation.setEtatenum(etatStr != null ? Etat.valueOf(etatStr) : Etat.EN_ATTENTE);
        } catch (IllegalArgumentException e) {
            consultation.setEtatenum(Etat.EN_ATTENTE);
        }

        // Gestion du patient
        int patientId = rs.getInt("patient_id");
        if (!rs.wasNull()) {
            Patient patient = new Patient();
            patient.setId(patientId);
            patient.setDossier_medical(rs.getString("dossier_medical"));
            patient.setFirstName(rs.getString("firstName"));
            patient.setLastName(rs.getString("lastName"));
            patient.setEmail(rs.getString("email"));
            consultation.setPatient(patient);
        }

        consultation.setMeetLink(rs.getString("meet_link"));

        return consultation;
    }

}
