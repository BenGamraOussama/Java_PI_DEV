package org.example.pi__dev_.dao;



import org.example.pi__dev_.enteties.RDV;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RDVDAO {
    private static Connection connection;

    public RDVDAO() {
        this.connection = DatabaseConnection.getConnection();
    }

    public static void addRDV(RDV rdv) throws SQLException {
        if (rdv == null) {
            throw new IllegalArgumentException("RDV object cannot be null");
        }

        String query = "INSERT INTO rdv(heure, date, priorite) VALUES (?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            // Handle Time conversion
            Time sqlTime = rdv.getHeure() != null ? rdv.getHeure() : null;
            stmt.setTime(1, sqlTime);

            // Handle Date conversion - properly convert from java.util.Date to java.sql.Date
            stmt.setDate(2,rdv.getDate());
            stmt.setString(3, rdv.getPriorite());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating RDV failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    rdv.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating RDV failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            // Re-throw the exception with additional context
            throw new SQLException("Failed to add RDV: " + e.getMessage(), e);
        }
    }

    public List<RDV> getAllRDVs() {
        List<RDV> rdvs = new ArrayList<>();
        String query = "SELECT * FROM rdv";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                RDV rdv = new RDV(
                        rs.getDate("date"),
                        rs.getTime("heure"),
                        rs.getString("priorite")
                );
                rdv.setId(rs.getInt("id"));
                rdvs.add(rdv);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rdvs;
    }

    public void updateRDV(RDV rdv) throws SQLException {
        String query = "UPDATE rdv SET heure = ?, date = ?, priorite = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setTime(1, rdv.getHeure());
            stmt.setDate(2, rdv.getDate());
            stmt.setString(3, rdv.getPriorite());
            stmt.setInt(4, rdv.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteRDV(int id) throws SQLException {
        String query = "DELETE FROM rdv WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public RDV getRDVById(int id) {
        String query = "SELECT * FROM rdv WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                RDV rdv = new RDV(
                        rs.getDate("date"),
                        rs.getTime("heure"),
                        rs.getString("priorite")
                );
                rdv.setId(rs.getInt("id"));
                return rdv;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
