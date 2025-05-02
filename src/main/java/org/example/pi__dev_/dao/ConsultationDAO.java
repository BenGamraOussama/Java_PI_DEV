package org.example.pi__dev_.dao;


import org.example.pi__dev_.enteties.Consultation;
import org.example.pi__dev_.enteties.Etat;
import org.example.pi__dev_.exceptions.AppointmentConflictException;
import org.example.pi__dev_.exceptions.ConsultationNotFoundException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConsultationDAO {
    private Connection connection;

    public ConsultationDAO() {
        this.connection = DatabaseConnection.getConnection();
    }

    public int addConsultation(Consultation consultation) throws SQLException, AppointmentConflictException {
        if (hasAppointmentConflict(consultation.getDate(), consultation.getHeure())) {
            throw new AppointmentConflictException("Appointment conflict detected");
        }

        String query = "INSERT INTO consultation(date, heure, prix, modeconsultation, etat, patient_id, psychiatre_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setDate(1, consultation.getDate());
            stmt.setTime(2, consultation.getHeure());
            stmt.setInt(3, consultation.getPatient().getId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating consultation failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating consultation failed, no ID obtained.");
                }
            }
        }
    }

    public List<Consultation> getAllConsultations() throws SQLException {
        List<Consultation> consultations = new ArrayList<>();
        String query = "SELECT * FROM consultation";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Consultation consultation = new Consultation(
                        rs.getDate("date"),
                        rs.getTime("heure"),
                        rs.getDouble("prix"),
                        rs.getString("modeconsultation"),
                        Etat.valueOf(rs.getString("etatenum"))
                );
                consultation.setId(rs.getInt("id"));
                consultations.add(consultation);
            }
        }
        return consultations;
    }

    public Consultation updateConsultation(Consultation consultation) throws SQLException, ConsultationNotFoundException {
        String query = "UPDATE consultation SET date = ?, heure = ?, prix = ?, modeconsultation = ?, etat = ?, " +
                "patient_id = ?, psychiatre_id = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setDate(1, consultation.getDate());
            stmt.setTime(2, consultation.getHeure());
            stmt.setDouble(3, consultation.getPrix());
            stmt.setString(4, consultation.getModeconsultation());
            stmt.setString(5, consultation.getEtatenum().name());
            stmt.setInt(6, consultation.getPatient().getId());
            stmt.setInt(7, consultation.getPsychiatre() != null ? consultation.getPsychiatre().getId() : null);
            stmt.setInt(8, consultation.getId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new ConsultationNotFoundException(consultation.getId());
            }
        }
        return consultation;
    }

    public void deleteConsultation(int id) throws SQLException, ConsultationNotFoundException {
        String query = "DELETE FROM consultation WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new ConsultationNotFoundException(id);
            }
        }
    }

    public Consultation getConsultationById(int id) throws SQLException, ConsultationNotFoundException {
        String query = "SELECT * FROM consultation WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Consultation consultation = new Consultation(
                            rs.getDate("date"),
                            rs.getTime("heure"),
                            rs.getDouble("prix"),
                            rs.getString("modeconsultation"),
                            Etat.valueOf(rs.getString("etat"))
                    );
                    consultation.setId(rs.getInt("id"));
                    return consultation;
                }
            }
        }
        throw new ConsultationNotFoundException(id);
    }

    public boolean hasAppointmentConflict(Date date, Time time) throws SQLException, AppointmentConflictException {
        String query = "SELECT COUNT(*) FROM consultation WHERE date = ? AND heure = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setDate(1, date);
            stmt.setTime(2, time);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    throw new AppointmentConflictException("Appointment conflict detected");
                }
            }
        }
        return false;
    }

    public boolean hasAppointmentConflict(Date date, Time time, int excludeId) throws SQLException, AppointmentConflictException {
        String query = "SELECT COUNT(*) FROM consultation WHERE date = ? AND heure = ? AND id != ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setDate(1, date);
            stmt.setTime(2, time);
            stmt.setInt(3, excludeId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    throw new AppointmentConflictException("Appointment conflict detected");
                }
            }
        }
        return false;
    }

    public List<Consultation> getConsultationsByPatientId(int patientId) throws SQLException {
        List<Consultation> consultations = new ArrayList<>();
        String query = "SELECT * FROM consultation WHERE patient_id = ? ORDER BY date DESC, heure DESC";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, patientId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Consultation consultation = new Consultation(
                            rs.getDate("date"),
                            rs.getTime("heure"),
                            rs.getDouble("prix"),
                            rs.getString("modeconsultation"),
                            Etat.valueOf(rs.getString("etat"))
                    );
                    consultation.setId(rs.getInt("id"));
                    consultations.add(consultation);
                }
            }
        }
        return consultations;
    }

}