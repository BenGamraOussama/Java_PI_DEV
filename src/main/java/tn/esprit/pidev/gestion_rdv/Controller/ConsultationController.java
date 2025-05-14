package tn.esprit.pidev.gestion_rdv.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import tn.esprit.pidev.gestion_rdv.Model.Consultation;
import tn.esprit.pidev.gestion_rdv.services.ConsultationService;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ResourceBundle;

public class ConsultationController implements Initializable {

    // Form Fields
    @FXML private DatePicker dateField;
    @FXML private TextField heureField;
    @FXML private TextField prixField;
    @FXML private ComboBox<String> modeField;
    @FXML private ComboBox<String> etatField;
    @FXML private TextField patientIdField;
    @FXML private TextField meetLinkField;

    // Buttons
    @FXML private Button addButton;
    @FXML private Button clearButton;

    // Search and Filter
    @FXML private TextField searchField;
    @FXML private ComboBox<String> filterComboBox;

    // Card Container
    @FXML private VBox consultationCardsContainer;

    private ConsultationService consultationService;
    private ObservableList<Consultation> consultationList = FXCollections.observableArrayList();
    private Consultation selectedConsultation;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        consultationService = new ConsultationService();

        // Initialize combo boxes
        modeField.getItems().addAll("En ligne", "En personne", "Téléphone");
        etatField.getItems().addAll("Planifié", "Terminé", "Annulé", "Reporté");
        filterComboBox.getItems().addAll("Tous", "Planifié", "Terminé", "Annulé", "Reporté");
        filterComboBox.setValue("Tous");

        // Load data
        loadConsultations();

        // Set button actions
        addButton.setOnAction(event -> handleAddConsultation());
        clearButton.setOnAction(event -> handleClearFields());

        // Set up search functionality
        searchField.textProperty().addListener((observable, oldValue, newValue) -> filterConsultations());
        filterComboBox.valueProperty().addListener((observable, oldValue, newValue) -> filterConsultations());
    }

    private void loadConsultations() {
        try {
            consultationList.clear();
            consultationList.addAll(consultationService.getAllConsultations());
            refreshConsultationCards();
        } catch (SQLException e) {
            showAlert("Error loading consultations: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void refreshConsultationCards() {
        consultationCardsContainer.getChildren().clear();

        for (Consultation consultation : consultationList) {
            VBox card = createConsultationCard(consultation);
            consultationCardsContainer.getChildren().add(card);
        }
    }

    private VBox createConsultationCard(Consultation consultation) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-padding: 15; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 0);");

        // Header with status and date
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        Circle statusDot = new Circle(5);
        statusDot.setFill(getStatusColor(consultation.getEtatEnum()));

        Text dateText = new Text(consultation.getDate() + " | " + consultation.getHeure());
        dateText.setStyle("-fx-font-weight: bold;");

        header.getChildren().addAll(statusDot, dateText);

        // Consultation details
        Text modeText = new Text("Mode: " + consultation.getModeConsultation());
        Text statusText = new Text("Status: " + consultation.getEtatEnum());
        Text patientText = new Text("Patient ID: " + consultation.getPatientId());
        Text priceText = new Text("Price: $" + consultation.getPrix());

        // Action buttons
        HBox buttons = new HBox(10);
        Button editButton = new Button("Edit");
        editButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        editButton.setOnAction(e -> selectConsultationForEdit(consultation));

        Button deleteButton = new Button("Delete");
        deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        deleteButton.setOnAction(e -> handleDeleteConsultation(consultation));

        buttons.getChildren().addAll(editButton, deleteButton);

        card.getChildren().addAll(header, modeText, statusText, patientText, priceText, buttons);
        return card;
    }

    private Color getStatusColor(String status) {
        return switch (status) {
            case "Terminé" -> Color.GREEN;
            case "Annulé" -> Color.RED;
            case "Reporté" -> Color.ORANGE;
            default -> Color.BLUE; // For "Planifié"
        };
    }

    private void selectConsultationForEdit(Consultation consultation) {
        selectedConsultation = consultation;
        showConsultationDetails(consultation);
    }

    private void showConsultationDetails(Consultation consultation) {
        if (consultation != null) {
            dateField.setValue(consultation.getDate());
            heureField.setText(consultation.getHeure().toString());
            prixField.setText(String.valueOf(consultation.getPrix()));
            modeField.setValue(consultation.getModeConsultation());
            etatField.setValue(consultation.getEtatEnum());
            patientIdField.setText(String.valueOf(consultation.getPatientId()));
            meetLinkField.setText(consultation.getMeetLink());
        }
    }

    @FXML
    private void handleAddConsultation() {
        if (validateInput()) {
            Consultation consultation = new Consultation(
                    dateField.getValue(),
                    LocalTime.parse(heureField.getText()),
                    Double.parseDouble(prixField.getText()),
                    modeField.getValue(),
                    etatField.getValue(),
                    Integer.parseInt(patientIdField.getText()),
                    meetLinkField.getText()
            );

            try {
                if (selectedConsultation == null) {
                    consultationService.addConsultation(consultation);
                    showAlert("Consultation added successfully!", Alert.AlertType.INFORMATION);
                } else {
                    consultation.setId(selectedConsultation.getId());
                    consultationService.updateConsultation(consultation);
                    showAlert("Consultation updated successfully!", Alert.AlertType.INFORMATION);
                    selectedConsultation = null;
                }
                loadConsultations();
                clearFields();
            } catch (SQLException e) {
                showAlert("Error: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private void handleDeleteConsultation(Consultation consultation) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Delete Consultation");
        confirmation.setContentText("Are you sure you want to delete this consultation?");

        if (confirmation.showAndWait().get() == ButtonType.OK) {
            try {
                consultationService.deleteConsultation(consultation.getId());
                loadConsultations();
                if (selectedConsultation != null && selectedConsultation.getId() == consultation.getId()) {
                    clearFields();
                    selectedConsultation = null;
                }
                showAlert("Consultation deleted successfully!", Alert.AlertType.INFORMATION);
            } catch (SQLException e) {
                showAlert("Error deleting: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private void filterConsultations() {
        String searchText = searchField.getText().toLowerCase();
        String filterStatus = filterComboBox.getValue();

        consultationCardsContainer.getChildren().clear();

        for (Consultation consultation : consultationList) {
            boolean matchesSearch = consultation.toString().toLowerCase().contains(searchText);
            boolean matchesFilter = filterStatus.equals("Tous") || consultation.getEtatEnum().equals(filterStatus);

            if (matchesSearch && matchesFilter) {
                VBox card = createConsultationCard(consultation);
                consultationCardsContainer.getChildren().add(card);
            }
        }
    }

    @FXML
    private void handleClearFields() {
        clearFields();
    }

    private void clearFields() {
        dateField.setValue(null);
        heureField.clear();
        prixField.clear();
        modeField.setValue(null);
        etatField.setValue(null);
        patientIdField.clear();
        meetLinkField.clear();
        selectedConsultation = null;
    }

    private boolean validateInput() {
        StringBuilder errorMessage = new StringBuilder();

        if (dateField.getValue() == null) {
            errorMessage.append("Date is required!\n");
        }

        if (heureField.getText() == null || heureField.getText().isEmpty()) {
            errorMessage.append("Time is required!\n");
        } else {
            try {
                LocalTime.parse(heureField.getText());
            } catch (Exception e) {
                errorMessage.append("Invalid time format! Use HH:MM:SS\n");
            }
        }

        if (prixField.getText() == null || prixField.getText().isEmpty()) {
            errorMessage.append("Price is required!\n");
        } else {
            try {
                Double.parseDouble(prixField.getText());
            } catch (NumberFormatException e) {
                errorMessage.append("Price must be a number!\n");
            }
        }

        if (modeField.getValue() == null || modeField.getValue().isEmpty()) {
            errorMessage.append("Consultation mode is required!\n");
        }

        if (etatField.getValue() == null || etatField.getValue().isEmpty()) {
            errorMessage.append("Status is required!\n");
        }

        if (patientIdField.getText() == null || patientIdField.getText().isEmpty()) {
            errorMessage.append("Patient ID is required!\n");
        } else {
            try {
                Integer.parseInt(patientIdField.getText());
            } catch (NumberFormatException e) {
                errorMessage.append("Patient ID must be a number!\n");
            }
        }

        if (errorMessage.length() > 0) {
            showAlert(errorMessage.toString(), Alert.AlertType.ERROR);
            return false;
        }

        return true;
    }

    private void showAlert(String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle("Notification");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}