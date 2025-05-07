package tn.esprit.pidev.gestion_activite.gui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.esprit.pidev.gestion_activite.entities.Activite;
import tn.esprit.pidev.gestion_activite.entities.Exercice;
import tn.esprit.pidev.gestion_activite.services.ActiviteService;
import tn.esprit.pidev.gestion_activite.services.ExerciceService;

import java.sql.SQLException;

public class ModifierActiviteController {

    @FXML
    private TextField titreField;

    @FXML
    private TextArea descriptionField;

    @FXML
    private ComboBox<String> statusComboBox;

    @FXML
    private ComboBox<String> typeComboBox;

    @FXML
    private TextField questionField;

    @FXML
    private Label questionLabel;

    private Activite activite;
    private Exercice exercice;
    private final ActiviteService activiteService = new ActiviteService();
    private final ExerciceService exerciceService = new ExerciceService();

    public void setActivite(Activite a) {
        this.activite = a;
        titreField.setText(a.getTitre());
        descriptionField.setText(a.getDescription());
        statusComboBox.setValue(a.getStatus());
        typeComboBox.setValue(a.getType());

        if ("exercise".equalsIgnoreCase(a.getType())) {
            try {
                exercice = exerciceService.findByActiviteId(a.getId());
                if (exercice != null) {
                    questionField.setText(exercice.getQuestion());
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de récupération de l'exercice.");
            }
        }
    }

    @FXML
    private void modifierActivite() {
        // Get all field values
        String titre = titreField.getText().trim();
        String description = descriptionField.getText().trim();
        String status = statusComboBox.getValue();
        String type = typeComboBox.getValue();
        String question = questionField.getText().trim();

        // Validate each field
        StringBuilder errorMessage = new StringBuilder();

        if (titre.isEmpty()) {
            errorMessage.append("• Le titre est obligatoire\n");
        }

        if (description.isEmpty()) {
            errorMessage.append("• La description est obligatoire\n");
        }

        if (status == null) {
            errorMessage.append("• Le statut est obligatoire\n");
        }

        if (type == null) {
            errorMessage.append("• Le type est obligatoire\n");
        }

        if ("exercice".equalsIgnoreCase(type) && question.isEmpty()) {
            errorMessage.append("• Une question est obligatoire pour les exercices\n");
        }

        // If there are any validation errors, show them and return
        if (errorMessage.length() > 0) {
            showAlert(Alert.AlertType.ERROR, "Erreur de validation", "Veuillez corriger les erreurs suivantes :\n\n" + errorMessage.toString());
            return;
        }

        try {
            // Update the activity
            activite.setTitre(titre);
            activite.setDescription(description);
            activite.setStatus(status);
            activite.setType(type);
            activiteService.modifier(activite);

            // If it's an exercise, update the question
            if ("exercice".equalsIgnoreCase(type)) {
                if (exercice == null) {
                    exercice = new Exercice(0, activite, question);
                    exerciceService.ajouter(exercice);
                } else {
                    exercice.setQuestion(question);
                    exerciceService.modifier(exercice);
                }
            }

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Activité modifiée avec succès!");

            // Close the window
            Stage stage = (Stage) titreField.getScene().getWindow();
            stage.close();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la modification: " + e.getMessage());
            e.printStackTrace();
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
