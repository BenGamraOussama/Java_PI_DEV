package tn.esprit.pidev.gestion_rdv;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import tn.esprit.pidev.gestion_rdv.dao.RDVDAO;
import tn.esprit.pidev.gestion_rdv.enteties.RDV;

import java.net.URL;
import java.sql.SQLException;
import java.sql.Time;
import java.util.Date;
import java.util.ResourceBundle;

public class AppointmentController implements Initializable {

    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> heureCombo;
    @FXML private ComboBox<String> psychiatreCombo;
    @FXML private ComboBox<String> typeCombo;
    @FXML private TextArea motifArea;
    @FXML private CheckBox reminderCheckbox;
    @FXML private ListView<RDV> appointmentsListView;

    private RDVDAO rdvDAO;
    private ObservableList<RDV> appointmentsList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        rdvDAO = new RDVDAO();

        // Initialize time slots
        heureCombo.getItems().addAll(
                "08:00", "09:00", "10:00", "11:00",
                "14:00", "15:00", "16:00", "17:00"
        );

        // Initialize psychiatrists (you would load these from your database)
        psychiatreCombo.getItems().addAll(
                "Dr. Smith", "Dr. Johnson", "Dr. Williams"
        );

        // Load existing appointments
        loadAppointments();
    }

    @FXML
    private void confirmAppointment() {
        if (validateInput()) {
            try {
                RDV newRDV = createRDVFromForm();
                rdvDAO.addRDV(newRDV);
                showAlert("Rendez-vous confirmé avec succès!", Alert.AlertType.INFORMATION);
                loadAppointments();
                clearForm();
            } catch (SQLException e) {
                showAlert("Erreur lors de l'ajout du rendez-vous: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private RDV createRDVFromForm() {
        RDV rdv = new RDV();
        rdv.setDate(new Date(datePicker.getValue().toEpochDay()));
        rdv.setHeure(Time.valueOf(heureCombo.getValue() + ":00"));
        rdv.setPriorite(typeCombo.getValue());
        // Add other fields as needed
        return rdv;
    }

    private void loadAppointments() {
        appointmentsList.clear();
        appointmentsList.addAll(rdvDAO.getAllRDVs());
        appointmentsListView.setItems(appointmentsList);
        appointmentsListView.setCellFactory(lv -> new ListCell<RDV>() {
            @Override
            protected void updateItem(RDV rdv, boolean empty) {
                super.updateItem(rdv, empty);
                if (empty || rdv == null) {
                    setText(null);
                } else {
                    setText(String.format("%s à %s - %s",
                            rdv.getDate(),
                            rdv.getHeure(),
                            rdv.getPriorite()));
                }
            }
        });
    }

    private boolean validateInput() {
        if (datePicker.getValue() == null) {
            showAlert("Veuillez sélectionner une date", Alert.AlertType.WARNING);
            return false;
        }
        if (heureCombo.getValue() == null || heureCombo.getValue().isEmpty()) {
            showAlert("Veuillez sélectionner une heure", Alert.AlertType.WARNING);
            return false;
        }
        if (psychiatreCombo.getValue() == null || psychiatreCombo.getValue().isEmpty()) {
            showAlert("Veuillez sélectionner un psychiatre", Alert.AlertType.WARNING);
            return false;
        }
        if (typeCombo.getValue() == null || typeCombo.getValue().isEmpty()) {
            showAlert("Veuillez sélectionner un type de consultation", Alert.AlertType.WARNING);
            return false;
        }
        return true;
    }

    private void clearForm() {
        datePicker.setValue(null);
        heureCombo.setValue(null);
        psychiatreCombo.setValue(null);
        typeCombo.setValue(null);
        motifArea.clear();
        reminderCheckbox.setSelected(false);
    }

    private void showAlert(String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle("Notification");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}