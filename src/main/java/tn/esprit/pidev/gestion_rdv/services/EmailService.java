package tn.esprit.pidev.gestion_rdv.services;

import tn.esprit.pidev.gestion_rdv.enteties.RDV;
import tn.esprit.pidev.gestion_rdv.enteties.Patient;
import tn.esprit.pidev.gestion_rdv.enteties.Etat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Service for simulating email notifications to patients about their appointments
 * This is a simplified version that logs the email content to the console
 * instead of actually sending emails
 */
public class EmailService {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Simulates sending an email to a patient about their appointment being accepted
     * 
     * @param rdv The appointment that was accepted
     * @return true if the email simulation was successful, false otherwise
     */
    public boolean sendAppointmentAcceptedEmail(RDV rdv) {
        if (rdv == null || rdv.getPatient() == null || rdv.getPatient().getEmail() == null) {
            System.err.println("Cannot send email: RDV or patient information is missing");
            return false;
        }

        Patient patient = rdv.getPatient();
        String subject = "Rendez-vous Confirmé";
        String content = String.format(
            "Bonjour %s %s,\n\n" +
            "Nous sommes heureux de vous informer que votre rendez-vous a été confirmé.\n\n" +
            "Détails du rendez-vous:\n" +
            "Date: %s\n" +
            "Heure: %s\n\n" +
            "Nous vous attendons à la date et l'heure indiquées.\n\n" +
            "Cordialement,\n" +
            "L'équipe médicale",
            patient.getFirstName(), patient.getLastName(),
            rdv.getDate().toString(), rdv.getHeure().toString()
        );

        return logEmail(patient.getEmail(), subject, content);
    }

    /**
     * Simulates sending an email to a patient about their appointment being refused
     * 
     * @param rdv The appointment that was refused
     * @return true if the email simulation was successful, false otherwise
     */
    public boolean sendAppointmentRefusedEmail(RDV rdv) {
        if (rdv == null || rdv.getPatient() == null || rdv.getPatient().getEmail() == null) {
            System.err.println("Cannot send email: RDV or patient information is missing");
            return false;
        }

        Patient patient = rdv.getPatient();
        String subject = "Rendez-vous Annulé";
        String content = String.format(
            "Bonjour %s %s,\n\n" +
            "Nous regrettons de vous informer que votre rendez-vous prévu pour le %s à %s a été annulé.\n\n" +
            "Veuillez nous contacter pour planifier un nouveau rendez-vous.\n\n" +
            "Cordialement,\n" +
            "L'équipe médicale",
            patient.getFirstName(), patient.getLastName(),
            rdv.getDate().toString(), rdv.getHeure().toString()
        );

        return logEmail(patient.getEmail(), subject, content);
    }

    /**
     * Logs the email content to the console instead of actually sending an email
     * 
     * @param recipientEmail The email address of the recipient
     * @param subject The subject of the email
     * @param content The content of the email
     * @return true always, as this is just a simulation
     */
    private boolean logEmail(String recipientEmail, String subject, String content) {
        System.out.println("\n==================================================");
        System.out.println("SIMULATION D'ENVOI D'EMAIL - " + LocalDateTime.now().format(formatter));
        System.out.println("==================================================");
        System.out.println("À: " + recipientEmail);
        System.out.println("Sujet: " + subject);
        System.out.println("--------------------------------------------------");
        System.out.println(content);
        System.out.println("==================================================\n");

        return true;
    }
}
