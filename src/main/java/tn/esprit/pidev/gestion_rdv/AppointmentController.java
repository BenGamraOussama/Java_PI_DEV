package tn.esprit.pidev.gestion_rdv;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;

public class AppointmentController {
    @FXML private TextField idField;
    @FXML private DatePicker datePicker;
    @FXML private TextField heureField;
    @FXML private TextField prioriteField;
    @FXML private ComboBox<String> typeConsultationCombo;
    @FXML private ComboBox<String> medecinCombo;
    @FXML private TextArea motifArea;
    @FXML private CheckBox reminderCheckbox;

    @FXML
    public void initialize() {
        // Initialize form fields
        idField.setText(generateNewId());
        datePicker.setValue(LocalDate.now());

        // Set up combo boxes
        typeConsultationCombo.getSelectionModel().selectFirst();
        medecinCombo.getSelectionModel().selectFirst();
    }

    private String generateNewId() {
        // Generate a new appointment ID
        return "RDV-" + System.currentTimeMillis();
    }

    @FXML
    private void handleConfirmAppointment() {
        // Handle appointment confirmation
        String id = idField.getText();
        LocalDate date = datePicker.getValue();
        String heure = heureField.getText();
        String priorite = prioriteField.getText();

        // Validate and save the appointment
        if (validateAppointment(date, heure, priorite)) {
            saveAppointment(id, date, heure, priorite);
            showConfirmation();
        }
    }

    private boolean validateAppointment(LocalDate date, String time, String priority) {
        // Add validation logic here
        return true;
    }

    private void saveAppointment(String id, LocalDate date, String time, String priority) {
        // Add save logic here
    }

    private void showConfirmation() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Rendez-vous enregistré");
        alert.setContentText("Votre rendez-vous a été enregistré avec succès.");
        alert.showAndWait();
    }
}
