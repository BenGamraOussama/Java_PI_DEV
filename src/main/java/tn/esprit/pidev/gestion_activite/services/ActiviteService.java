package tn.esprit.pidev.gestion_activite.services;

import tn.esprit.pidev.gestion_activite.entities.Activite;
import tn.esprit.pidev.gestion_activite.entities.Patient;
import tn.esprit.pidev.Database.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ActiviteService {
    private Connection cnx = Database.getConnection();
    private PatientService patientService;

    private PatientService getPatientService() {
        if (patientService == null) {
            patientService = new PatientService();
        }
        return patientService;
    }

    public void ajouter(Activite a) {
        String sql = "INSERT INTO activite (titre, description, status, type) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = cnx.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, a.getTitre());
            ps.setString(2, a.getDescription());
            ps.setString(3, a.getStatus());
            ps.setString(4, a.getType());
            ps.executeUpdate();

            // Get the generated ID
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    a.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Activite> getAll() {
        List<Activite> list = new ArrayList<>();
        String sql = "SELECT a.*, pa.patient_id FROM activite a " +
                "LEFT JOIN patient_activite pa ON a.id = pa.activite_id";
        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Patient patient = null;
                if (rs.getObject("patient_id") != null) {
                    patient = getPatientService().findById(rs.getInt("patient_id"));
                }
                list.add(new Activite(
                        rs.getInt("id"),
                        rs.getString("titre"),
                        rs.getString("description"),
                        rs.getString("status"),
                        rs.getString("type"),
                        patient
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void supprimer(int id) {
        String sql = "DELETE FROM activite WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("Activité supprimée !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void modifier(Activite a) {
        String sql = "UPDATE activite SET titre=?, description=?, status=?, type=? WHERE id=?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, a.getTitre());
            ps.setString(2, a.getDescription());
            ps.setString(3, a.getStatus());
            ps.setString(4, a.getType());
            ps.setInt(5, a.getId());
            ps.executeUpdate();
            System.out.println("Activité modifiée !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int lastInsertedId() throws SQLException {
        String query = "SELECT MAX(id) FROM activite";
        try (PreparedStatement pst = Database.getConnection().prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return -1;
    }

    public Activite findById(int id) {
        String query = "SELECT a.*, pa.patient_id FROM activite a " +
                "LEFT JOIN patient_activite pa ON a.id = pa.activite_id " +
                "WHERE a.id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Patient patient = null;
                if (rs.getObject("patient_id") != null) {
                    patient = getPatientService().findById(rs.getInt("patient_id"));
                }
                return new Activite(
                        rs.getInt("id"),
                        rs.getString("titre"),
                        rs.getString("description"),
                        rs.getString("status"),
                        rs.getString("type"),
                        patient
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void update(Activite activite) throws Exception {
        String sql = "UPDATE activite SET titre = ?, description = ?, type = ?, status = ? WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, activite.getTitre());
            stmt.setString(2, activite.getDescription());
            stmt.setString(3, activite.getType());
            stmt.setString(4, activite.getStatus());
            stmt.setInt(5, activite.getId());

            stmt.executeUpdate();
        }
    }
}
