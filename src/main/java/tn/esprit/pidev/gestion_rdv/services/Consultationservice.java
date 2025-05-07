package tn.esprit.pidev.gestion_rdv.services;

import tn.esprit.pidev.gestion_rdv.dao.ConsultationDAO;
import tn.esprit.pidev.gestion_rdv.enteties.Consultation;
import tn.esprit.pidev.gestion_rdv.enteties.Etat;
import tn.esprit.pidev.Database.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Consultationservice implements Iservice<Consultation> {
    private Connection connection;
    private ConsultationDAO consultationDAO;

    public Consultationservice() {
        this.connection = Database.getConnection();
        this.consultationDAO = new ConsultationDAO();
    }

    public Consultationservice(Connection connection) {
        this.connection = connection;
        this.consultationDAO = new ConsultationDAO();
    }

    @Override
    public List<Consultation> readList() throws SQLException {
        return consultationDAO.getAllConsultations();
    }

    @Override
    public void add(Consultation consultation) throws SQLException {
        consultationDAO.addConsultation(consultation);
    }

    @Override
    public void update(Consultation consultation) throws SQLException {
        consultationDAO.updateConsultation(consultation);
    }

    public void delete(int id) {
        consultationDAO.deleteConsultation(id);
    }

    public Consultation getById(int id) {
        return consultationDAO.getConsultationById(id);
    }
}
