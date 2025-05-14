package tn.esprit.pidev.gestion_rdv.services;

import tn.esprit.pidev.gestion_rdv.enteties.Consultation;
import tn.esprit.pidev.gestion_rdv.dao.ConsultationDAO;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Service for creating and managing Zoom meetings for consultations.
 */
public class ZoomService {
    private static final String ZOOM_API_KEY = "mn5tDyAITWVTkZli9Qc8Q";
    private static final String ZOOM_API_SECRET = "nNtc65jzALUkRCSJAiV8QVw9xk20nKi8";
    private static final Logger logger = Logger.getLogger(ZoomService.class.getName());

    private final ConsultationDAO consultationDAO = new ConsultationDAO();

    /**
     * Creates a Zoom meeting via the Zoom API.
     * @param consultation The consultation to create a meeting for
     * @return The join URL for the meeting
     * @throws Exception If an error occurs during the API call
     */
    private String createViaAPI(Consultation consultation) throws Exception {
        ZoomApiClient client = new ZoomApiClient(ZOOM_API_KEY, ZOOM_API_SECRET);

        Meeting meeting = new Meeting();
        meeting.setTopic("Medical Consultation on " + consultation.getDate() + " at " + consultation.getHeure());
        meeting.setStartTime(formatZoomDateTime(consultation));
        meeting.setDuration(30); // 30 minutes
        meeting.setTimezone("Europe/Paris");
        meeting.setPassword(generatePassword());

        Meeting createdMeeting = client.createMeeting(meeting);
        return createdMeeting.getJoinUrl();
    }

    /**
     * Formats the consultation date and time for Zoom.
     * @param consultation The consultation
     * @return The formatted date and time
     */
    private String formatZoomDateTime(Consultation consultation) {
        if (consultation.getDate() == null || consultation.getHeure() == null) {
            throw new IllegalArgumentException("Date or time cannot be null");
        }
        return consultation.getDate().toString() + "T" + consultation.getHeure() + ":00";
    }

    /**
     * Generates a random password for the meeting.
     * @return The generated password
     */
    private String generatePassword() {
        // Générer un mot de passe aléatoire pour la réunion
        return UUID.randomUUID().toString().substring(0, 8);
    }

    /**
     * Sends an invitation to the patient for the Zoom meeting.
     * This is a simplified implementation that just logs the action.
     * @param consultation The consultation
     * @param meetingUrl The Zoom meeting URL
     */
    private void sendInvitation(Consultation consultation, String meetingUrl) {
        // In a real implementation, this would send an email or SMS to the patient
        // For now, we'll just log the action
        if (meetingUrl == null || meetingUrl.isEmpty()) {
            logger.log(Level.WARNING, "Cannot send invitation: meeting URL is empty");
            return;
        }

        logger.log(Level.INFO, "Sending Zoom invitation to patient {0} for consultation on {1} at {2}. URL: {3}",
                new Object[]{consultation.getDate(), consultation.getHeure(), meetingUrl});
    }

    /**
     * Creates a Zoom meeting for the consultation and updates the consultation with the meeting URL.
     * @param consultation The consultation to create a meeting for
     * @return The join URL for the meeting
     */
    public String createZoomMeeting(Consultation consultation) {
        try {
            String meetingUrl = createViaAPI(consultation);

            // Update the consultation with the Zoom link (ensure it's not null)
            String safeUrl = meetingUrl != null ? meetingUrl : "";
            consultation.setMeetLink(safeUrl);
            consultationDAO.updateConsultation(consultation);

            // Send invitation to patient
            sendInvitation(consultation, meetingUrl);

            return meetingUrl;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to create Zoom meeting", e);
            throw new RuntimeException("Échec de la création de la réunion Zoom", e);
        }
    }
}
