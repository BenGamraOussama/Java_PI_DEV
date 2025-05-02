package org.example.pi__dev_.services;


import org.example.pi__dev_.dao.PatientDAO;
import org.example.pi__dev_.dao.PsychiatreDAO;
import org.example.pi__dev_.dao.RDVDAO;
import org.example.pi__dev_.enteties.Etat;
import org.example.pi__dev_.enteties.Patient;
import org.example.pi__dev_.enteties.RDV;
import org.example.pi__dev_.exceptions.PatientNotFoundException;
import org.example.pi__dev_.utils.Pidev;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RDVservice implements Iservice<RDV> {
    private final RDVDAO dao;
    private final PatientDAO patientDAO;
    private final PsychiatreDAO psychiatreDAO;
    private Connection con;

    public RDVservice(RDVDAO dao, PatientDAO patientDAO, PsychiatreDAO psychiatreDAO) {
        this.dao = dao;
        this.patientDAO = patientDAO;
        this.psychiatreDAO = psychiatreDAO;
        this.con = Pidev.getInstance().getCon();
    }

    @Override
    public List<RDV> readList() throws SQLException {
        String query = "SELECT * FROM `rdv`";
        List<RDV> rdvs = new ArrayList<>();

        try (Statement stm = con.createStatement();
             ResultSet rs = stm.executeQuery(query)) {

            while (rs.next()) {
                RDV r = new RDV(rs.getTime("heure"), rs.getDate("date"), rs.getString("priorite"));
                rdvs.add(r);
            }
        }
        return rdvs;
    }

    @Override
    public RDV add(RDV rdv) throws SQLException {
        if (con == null) {
            System.out.println("La connexion à la base de données est nulle. Impossible d'ajouter le RDV.");
            return rdv;
    }

        String query = "INSERT INTO `rdv`(`heure`, `date`, `priorite`) VALUES (?,?,?)";

        try {
            Patient patient = patientDAO.getPatientById(1);
            if (patient == null) throw new SQLException("Patient introuvable");

            rdv.setPriorite(String.valueOf(Etat.EN_ATTENTE));
            rdv.setPatient(patient);
            RDVDAO.addRDV(rdv);

            patient.ajouterRendezVous(rdv);
            return rdv;
        } catch (SQLException | PatientNotFoundException e) {
            throw new SQLException("Erreur base de données");
        }
    }



    @Override
    public void update(RDV rdv) {
        String query = "UPDATE `rdv` SET `heure` = ?, `date` = ? `priorite` = ? WHERE `id` = ?";
        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setTime(1, rdv.getHeure());
            pstmt.setDate(2, rdv.getDate());
            pstmt.setInt(3, rdv.getId());
            pstmt.setString(4, rdv.getPriorite());
            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("RDV updated successfully.");
            } else {
                System.out.println("No RDV found with the given ID.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}

