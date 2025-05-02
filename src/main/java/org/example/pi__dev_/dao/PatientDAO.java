package org.example.pi__dev_.dao;


import org.example.pi__dev_.enteties.Patient;
import org.example.pi__dev_.exceptions.PatientNotFoundException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.example.pi__dev_.dao.DatabaseConnection.connection;

public class PatientDAO {

    // In PatientDAO.java
    public Patient getPatientById(int id) throws SQLException, PatientNotFoundException {
        String query = "SELECT * FROM patient WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Patient(
                            rs.getInt("id"),
                            rs.getString("firstName"),
                            rs.getString("lastName"),
                            rs.getString("dossierMedical")
                    );
                }
            }
        }
        throw new PatientNotFoundException(id);
    }



}