package org.example.pi__dev_.services;


import com.google.protobuf.ServiceException;
import org.example.pi__dev_.dao.ConsultationDAO;
import org.example.pi__dev_.dao.PatientDAO;
import org.example.pi__dev_.dao.PsychiatreDAO;
import org.example.pi__dev_.enteties.Consultation;
import org.example.pi__dev_.enteties.Etat;
import org.example.pi__dev_.enteties.Patient;
import org.example.pi__dev_.enteties.Psychiatre;
import org.example.pi__dev_.exceptions.AppointmentConflictException;
import org.example.pi__dev_.exceptions.ConsultationNotFoundException;
import org.example.pi__dev_.exceptions.PatientNotFoundException;
import org.example.pi__dev_.exceptions.PsychiatretNotFoundException;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;

public class Consultationservice {
    private final ConsultationDAO consultationDao;
    private final PatientDAO patientDao;
    private final PsychiatreDAO psychiatreDAO;

    public Consultationservice() {
        this.consultationDao = new ConsultationDAO();
        this.patientDao = new PatientDAO();
        this.psychiatreDAO = new PsychiatreDAO();
    }

    public Consultation bookAppointment(int patientId, Date date, Time heure, Double prix, String modeconsultation, Etat etat)
            throws PatientNotFoundException, AppointmentConflictException, ServiceException {
        try {
            Patient patient = patientDao.getPatientById(patientId);
            if (patient == null) {
                throw new PatientNotFoundException(patientId);
            }

            // This will now throw AppointmentConflictException if there's a conflict
            consultationDao.hasAppointmentConflict(date, heure);

            Consultation consultation = new Consultation(date, heure, prix, modeconsultation, etat);
            int id = consultationDao.addConsultation(consultation);
            consultation.setId(id);

            return consultation;
        } catch (SQLException e) {
            throw new ServiceException("Erreur lors de la prise de rendez-vous", e);
        }
    }

    public Consultation confirmAppointment(int consultationId, int psychiatristId)
            throws ConsultationNotFoundException, PsychiatretNotFoundException, ServiceException {
        try {
            Consultation consultation = consultationDao.getConsultationById(consultationId);
            if (consultation == null) {
                throw new ConsultationNotFoundException(consultationId);
            }

            Psychiatre psychiatrist = psychiatreDAO.findById(psychiatristId);
            if (psychiatrist == null) {
                throw new PsychiatretNotFoundException(psychiatristId);
            }

            consultation.setEtatenum(Etat.VALIDEE);
            consultation.setPsychiatre(psychiatrist);

            consultationDao.updateConsultation(consultation);
            return consultation;
        } catch (SQLException e) {
            throw new ServiceException("Erreur lors de la confirmation du rendez-vous", e);
        }
    }

    public Consultation cancelAppointment(int consultationId)
            throws ConsultationNotFoundException, ServiceException {
        try {
            Consultation consultation = consultationDao.getConsultationById(consultationId);
            if (consultation == null) {
                throw new ConsultationNotFoundException(consultationId);
            }

            consultation.setEtatenum(Etat.ANNULEE);
            consultationDao.updateConsultation(consultation);
            return consultation;
        } catch (SQLException e) {
            throw new ServiceException("Erreur lors de l'annulation du rendez-vous", e);
        }
    }
}