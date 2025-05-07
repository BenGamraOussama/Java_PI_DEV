package tn.esprit.pidev.gestion_activite.services;

import tn.esprit.pidev.gestion_activite.entities.Activite;
import tn.esprit.pidev.gestion_activite.entities.Exercice;
import tn.esprit.pidev.gestion_activite.entities.Patient;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class SMSService {
    // Your Twilio credentials
    private static final String ACCOUNT_SID = "ACce91a8a591a0c431c46543e8afc43c5c";
    private static final String AUTH_TOKEN = "ee0d9e720fe8e03490cfb37bf5204b25";
    private static final String TWILIO_PHONE_NUMBER = "+17602798381";

    public SMSService() {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }

    public void sendNewActivitySMS(Patient patient, Activite activite) {
        if (patient.getPhone() == null || patient.getPhone().isEmpty()) {
            return; // Skip if no phone number
        }

        String formattedPhone = formatPhoneNumber(patient.getPhone());
        if (formattedPhone == null) {
            System.err.println("Invalid phone number format for patient: " + patient.getName());
            return;
        }

        String message = String.format(
                "Bonjour %s,\n\nUne nouvelle activité vous a été assignée :\n%s\n\nDescription : %s\nType : %s\n\nConnectez-vous à votre compte pour commencer cette activité.\n\nCordialement,\nL'équipe de suivi",
                patient.getName(),
                activite.getTitre(),
                activite.getDescription(),
                activite.getType()
        );

        sendSMS(formattedPhone, message);
    }

    public void sendNewExerciseSMS(Patient patient, Exercice exercice) {
        if (patient.getPhone() == null || patient.getPhone().isEmpty()) {
            return; // Skip if no phone number
        }

        String formattedPhone = formatPhoneNumber(patient.getPhone());
        if (formattedPhone == null) {
            System.err.println("Invalid phone number format for patient: " + patient.getName());
            return;
        }

        String message = String.format(
                "Bonjour %s,\n\nUn nouvel exercice vous a été assigné :\n%s\n\nQuestion : %s\n\nConnectez-vous à votre compte pour répondre à cet exercice.\n\nCordialement,\nL'équipe de suivi",
                patient.getName(),
                exercice.getActivite().getTitre(),
                exercice.getQuestion()
        );

        sendSMS(formattedPhone, message);
    }

    private String formatPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return null;
        }

        // Remove all non-digit characters except the + prefix
        String digitsOnly = phoneNumber.replaceAll("[^0-9+]", "");

        // If the number already starts with +, validate the rest
        if (digitsOnly.startsWith("+")) {
            // Remove the + for digit count check
            String numberWithoutPlus = digitsOnly.substring(1);
            // Check if the number has at least 10 digits (minimum for a valid international number)
            if (numberWithoutPlus.length() >= 10 && numberWithoutPlus.matches("\\d+")) {
                return digitsOnly;
            }
            return null;
        }

        // Handle numbers without + prefix
        if (digitsOnly.startsWith("216")) {
            // Tunisian number, add + prefix
            return "+" + digitsOnly;
        } else if (digitsOnly.startsWith("0")) {
            // Local Tunisian number, add +216 prefix
            String numberWithoutZero = digitsOnly.substring(1);
            if (numberWithoutZero.length() >= 8) { // Minimum length for a valid Tunisian number
                return "+216" + numberWithoutZero;
            }
        }

        return null;
    }

    private void sendSMS(String toPhoneNumber, String message) {
        try {
            Message.creator(
                    new PhoneNumber(toPhoneNumber),
                    new PhoneNumber(TWILIO_PHONE_NUMBER),
                    message
            ).create();
        } catch (Exception e) {
            System.err.println("Failed to send SMS: " + e.getMessage());
        }
    }
}