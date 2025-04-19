package org.example.pi__dev_.dao;



import org.example.pi__dev_.enteties.RDV;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RDVDAO {
    private Connection connection;

    public RDVDAO() {
        this.connection = DatabaseConnection.getConnection();
    }

    public void addRDV(RDV rdv) throws SQLException {
        String query = "INSERT INTO rdv(heure, date, priorite) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setTime(1, rdv.getHeure());
            stmt.setDate(2, new java.sql.Date(rdv.getDate().getTime()));
            stmt.setString(3, rdv.getPriorite());

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    rdv.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<RDV> getAllRDVs() {
        List<RDV> rdvs = new ArrayList<>();
        String query = "SELECT * FROM rdv";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                RDV rdv = new RDV(
                        rs.getTime("heure"),
                        rs.getDate("date"),
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
            stmt.setDate(2, new java.sql.Date(rdv.getDate().getTime()));
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
                        rs.getTime("heure"),
                        rs.getDate("date"),
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
