package org.example.pi__dev_.services;

import org.example.pi__dev_.dao.PatientDAO;
import org.example.pi__dev_.dao.ConsultationDAO;
import org.example.pi__dev_.enteties.Patient;
import org.example.pi__dev_.enteties.Consultation;
import org.example.pi__dev_.enteties.RDV;
import org.example.pi__dev_.exceptions.AppointmentConflictException;
import org.example.pi__dev_.exceptions.ConsultationNotFoundException;
import org.example.pi__dev_.exceptions.PatientNotFoundException;

import java.sql.SQLException;
import java.util.List;

public class PatientRdvService {
    private final PatientDAO patientDao;
    private final ConsultationDAO consultationDao;

    public PatientRdvService() {
        this.patientDao = new PatientDAO();
        this.consultationDao = new ConsultationDAO();
    }

    // Get all appointments
    public RDV getAllRDVs() throws SQLException {
        return (RDV) consultationDao.getAllConsultations();
    }

    // Get appointments for a specific patient
    public List<Consultation> getRDVsByPatient(int patientId) throws SQLException, PatientNotFoundException {
        // Verify patient exists
        Patient patient = patientDao.getPatientById(patientId);
        if (patient == null) {
            throw new PatientNotFoundException(patientId);
        }
        return consultationDao.getConsultationsByPatientId(patientId);
    }

    // Book a new appointment
    public boolean bookRDV(RDV rdv) throws SQLException, AppointmentConflictException {
        int rowsAffected = consultationDao.addConsultation(convertToConsultation(rdv));
        return rowsAffected > 0;
    }

    private Consultation convertToConsultation(RDV rdv) {
        Consultation consultation = new Consultation();
        consultation.setDate(rdv.getDate());
        consultation.setHeure(rdv.getHeure());
        // Set other fields as needed
        return consultation;
    }

    // Cancel an appointment
    public boolean cancelRDV(int rdvId) throws SQLException, ConsultationNotFoundException {
        Consultation rdv = consultationDao.getConsultationById(rdvId);
        if (rdv == null) {
            throw new ConsultationNotFoundException(rdvId);
        }
        consultationDao.deleteConsultation(rdvId);
        return false;
    }

    // Update an appointment
    public Consultation updateRDV(Consultation rdv) throws SQLException, ConsultationNotFoundException {
        Consultation existing = consultationDao.getConsultationById(rdv.getId());
        if (existing == null) {
            throw new ConsultationNotFoundException(rdv.getId());
        }
        return consultationDao.updateConsultation(rdv);
    }
}
