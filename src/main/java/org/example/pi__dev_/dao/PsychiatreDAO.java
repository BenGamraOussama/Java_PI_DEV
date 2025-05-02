package org.example.pi__dev_.dao;

import org.example.pi__dev_.enteties.Psychiatre;
import org.example.pi__dev_.exceptions.PsychiatretNotFoundException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.example.pi__dev_.dao.DatabaseConnection.connection;

public class PsychiatreDAO {
    public Psychiatre findById(int id) throws SQLException, PsychiatretNotFoundException {
        String query = "SELECT * FROM psychiatre WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Psychiatre(
                            rs.getInt("id"),
                            rs.getString("firstName"),
                            rs.getString("lastName"),
                            rs.getString("specialite")
                    );
                } else {
                    // Explicitly throw the exception when psychiatre is not found
                    throw new PsychiatretNotFoundException(id);
                }
            }
        }
}}
