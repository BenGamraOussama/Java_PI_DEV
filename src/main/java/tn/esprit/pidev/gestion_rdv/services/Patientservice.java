package tn.esprit.pidev.gestion_rdv.services;

import tn.esprit.pidev.gestion_rdv.dao.DatabaseConnection;
import tn.esprit.pidev.gestion_rdv.enteties.Patient;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Patientservice {
    public static List<Patient> searchPatients(String searchText) throws SQLException {
        List<Patient> patients = new ArrayList<>();
        String query = "SELECT * FROM patient WHERE firstName LIKE ? OR lastName LIKE ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, "%" + searchText + "%");
            stmt.setString(2, "%" + searchText + "%");

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Patient patient = new Patient(
                        rs.getInt("id"),
                        rs.getString("dossier_medical"),
                        rs.getString("firstName"),
                        rs.getString("lastName"),
                        rs.getString("email")
                );
                patients.add(patient);
            }
        }
        return patients;
    }

    public static List<Patient> getAllPatients() throws SQLException {
        List<Patient> patients = new ArrayList<>();
        String query = "SELECT * FROM patient";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Patient patient = new Patient(
                        rs.getInt("id"),
                        rs.getString("dossier_medical"),
                        rs.getString("firstName"),
                        rs.getString("lastName"),
                        rs.getString("email")
                );
                patients.add(patient);
            }
        }
        return patients;
    }
}

