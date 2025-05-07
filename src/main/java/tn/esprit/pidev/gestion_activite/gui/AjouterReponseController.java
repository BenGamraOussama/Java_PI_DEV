package tn.esprit.pidev.gestion_activite.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import tn.esprit.pidev.gestion_activite.entities.Exercice;
import tn.esprit.pidev.gestion_activite.entities.Reponse;
import tn.esprit.pidev.gestion_activite.entities.Patient;
import tn.esprit.pidev.gestion_activite.services.ReponseService;
import tn.esprit.pidev.gestion_activite.services.PatientService;

import java.util.Arrays;
import java.util.List;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class AjouterReponseController {
    private static final int DEFAULT_PATIENT_ID = 1; // Default patient ID for testing
    private static final int MAX_BAD_WORD_ATTEMPTS = 3;
    private static final int SUSPENSION_HOURS = 24;

    private static final List<String> BAD_WORDS = Arrays.asList(
            "badword1",
            "badword2",
            "badword3"
    );

    @FXML
    private Label exerciceLabel;

    @FXML
    private TextArea contenuField;

    private Exercice exercice;
    private PatientService patientService;

    public AjouterReponseController() {
        this.patientService = new PatientService();
    }

    public void setExercice(Exercice exercice) {
        this.exercice = exercice;
        if (exercice != null) {
            exerciceLabel.setText("Question : " + exercice.getQuestion());
        }
    }

    private boolean containsBadWords(String text) {
        String lowerText = text.toLowerCase();
        for (String badWord : BAD_WORDS) {
            if (lowerText.contains(badWord.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    @FXML
    private void ajouterReponse() {
        String contenu = contenuField.getText().trim();

        if (contenu.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Champ vide", "Veuillez saisir une réponse.");
            return;
        }

        try {
            // Get the current patient
            Patient patient = patientService.getById(DEFAULT_PATIENT_ID);

            // Check if patient is suspended
            if (patient.getSuspended_until() != null &&
                    LocalDateTime.now().isBefore(patient.getSuspended_until())) {
                long hoursLeft = ChronoUnit.HOURS.between(LocalDateTime.now(), patient.getSuspended_until());
                showAlert(Alert.AlertType.WARNING, "Compte suspendu",
                        "Votre compte est suspendu. Vous pourrez à nouveau soumettre des réponses dans " +
                                hoursLeft + " heures.");
                return;
            }

            // Check for bad words
            if (containsBadWords(contenu)) {
                // Increment bad word attempts
                int attempts = patient.getBad_word_attempts() + 1;
                patient.setBad_word_attempts(attempts);
                patientService.update(patient);

                if (attempts >= MAX_BAD_WORD_ATTEMPTS) {
                    // Suspend the patient
                    LocalDateTime suspensionEnd = LocalDateTime.now().plusHours(SUSPENSION_HOURS);
                    patient.setSuspended_until(suspensionEnd);
                    patient.setBad_word_attempts(0);
                    patientService.update(patient);

                    showAlert(Alert.AlertType.WARNING, "Compte suspendu",
                            "Votre compte a été suspendu pour " + SUSPENSION_HOURS +
                                    " heures en raison de tentatives répétées d'utiliser des mots inappropriés.");
                    return;
                }

                showAlert(Alert.AlertType.WARNING, "Contenu inapproprié",
                        "Votre réponse contient des mots inappropriés. Tentatives restantes: " +
                                (MAX_BAD_WORD_ATTEMPTS - attempts));
                return;
            }

            // Create and save the response
            Reponse reponse = new Reponse(0, exercice, contenu, DEFAULT_PATIENT_ID);
            ReponseService reponseService = new ReponseService();
            reponseService.ajouter(reponse);

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Réponse enregistrée avec succès !");

            contenuField.clear();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }
}