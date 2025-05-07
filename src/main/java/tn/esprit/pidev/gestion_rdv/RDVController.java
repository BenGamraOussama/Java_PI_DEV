package tn.esprit.pidev.gestion_rdv;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.*;
import javafx.stage.Stage;
import tn.esprit.pidev.gestion_rdv.dao.RDVDAO;
import tn.esprit.pidev.gestion_rdv.enteties.Etat;
import tn.esprit.pidev.gestion_rdv.enteties.RDV;
import tn.esprit.pidev.gestion_rdv.services.Calendarservice;
import tn.esprit.pidev.gestion_rdv.services.EmailService;
import tn.esprit.pidev.gestion_rdv.services.GoogleCalendarService;
import tn.esprit.pidev.gestion_rdv.services.RDVservice;

import java.awt.Desktop;
import javafx.event.ActionEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

public class RDVController {
    @FXML private TextField idField;
    @FXML private DatePicker datePicker;
    @FXML private TextField heureField;
    @FXML private TextField prioriteField;
    @FXML private FlowPane rdvCardsContainer;
    @FXML private Button btnAjouterCalendrier;


    private RDVDAO rdvDAO;
    private RDVservice rdvService;
    private EmailService emailService;
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    @FXML
    public void initialize() {
        rdvDAO = new RDVDAO();
        rdvService = new RDVservice(null); // The constructor will get the connection from Pidev
        emailService = new EmailService(); // Initialize the email service
        configureHeureField();
        loadRDVs();
    }

    private void configureHeureField() {
        heureField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("^\\d{0,2}:?\\d{0,2}$")) {
                heureField.setText(oldValue);
            }
        });
    }

    private void loadRDVs() {
        rdvCardsContainer.getChildren().clear();
        try {
            List<RDV> rdvs = rdvService.readList();

            if (rdvs != null) {
                for (RDV rdv : rdvs) {
                    rdvCardsContainer.getChildren().add(createRDVCard(rdv));
                }
            }
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors du chargement des RDVs: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private VBox createRDVCard(RDV rdv) {
        VBox card = new VBox();
        card.getStyleClass().add("rdv-card");
        card.setSpacing(8);
        card.setPadding(new Insets(12));
        card.setMinWidth(280);

        // Header with priority and Google Calendar indicator
        HBox headerBox = new HBox();
        headerBox.setSpacing(10);
        headerBox.setAlignment(Pos.CENTER_LEFT);

        Label prioriteLabel = new Label("Priorité: " + rdv.getPriorite());
        prioriteLabel.getStyleClass().add("card-title");
        prioriteLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(prioriteLabel, Priority.ALWAYS);

        // Google Calendar indicator
        Label calendarLabel = new Label("Google Calendar");
        calendarLabel.getStyleClass().add("calendar-indicator");

        headerBox.getChildren().addAll(prioriteLabel, calendarLabel);

        Label dateLabel = new Label("Date: " + rdv.getDate().toString());
        dateLabel.getStyleClass().add("card-date");

        Label heureLabel = new Label("Heure: " + rdv.getHeure().toString());
        heureLabel.getStyleClass().add("card-detail");

        // Add status label
        Label statusLabel = new Label("État: " + (rdv.getEtat() != null ? rdv.getEtat().name() : "EN_ATTENTE"));
        statusLabel.getStyleClass().add("card-detail");

        // Style the status label based on the state
        if (rdv.getEtat() == Etat.VALIDEE) {
            statusLabel.setStyle("-fx-text-fill: green;");
        } else if (rdv.getEtat() == Etat.ANNULEE) {
            statusLabel.setStyle("-fx-text-fill: red;");
        } else {
            statusLabel.setStyle("-fx-text-fill: orange;");
        }

        // Standard buttons (edit, delete)
        HBox standardButtonBox = new HBox(10);
        standardButtonBox.setAlignment(Pos.CENTER_RIGHT);

        Button editButton = new Button("Modifier");
        editButton.getStyleClass().addAll("button", "button-edit");
        editButton.setOnAction(e -> fillFormWithRDV(rdv));

        Button deleteButton = new Button("Supprimer");
        deleteButton.getStyleClass().addAll("button", "button-delete");
        deleteButton.setOnAction(e -> confirmAndDeleteRDV(rdv));

        standardButtonBox.getChildren().addAll(editButton, deleteButton);

        // Accept/Refuse buttons
        HBox acceptRefuseButtonBox = new HBox(10);
        acceptRefuseButtonBox.setAlignment(Pos.CENTER_RIGHT);

        Button acceptButton = new Button("Accepter");
        acceptButton.getStyleClass().addAll("button", "button-accept");
        acceptButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        acceptButton.setOnAction(e -> handleAcceptRDV(rdv));

        Button refuseButton = new Button("Refuser");
        refuseButton.getStyleClass().addAll("button", "button-refuse");
        refuseButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white;");
        refuseButton.setOnAction(e -> handleRefuseRDV(rdv));

        // Only show accept/refuse buttons if the appointment is pending
        if (rdv.getEtat() == null || rdv.getEtat() == Etat.EN_ATTENTE) {
            acceptRefuseButtonBox.getChildren().addAll(acceptButton, refuseButton);
        }

        card.getChildren().addAll(headerBox, dateLabel, heureLabel, statusLabel, standardButtonBox);

        // Only add the accept/refuse button box if it has children
        if (!acceptRefuseButtonBox.getChildren().isEmpty()) {
            card.getChildren().add(acceptRefuseButtonBox);
        }

        return card;
    }

    private void fillFormWithRDV(RDV rdv) {
        idField.setText(String.valueOf(rdv.getId()));
        datePicker.setValue(rdv.getDate().toLocalDate());
        heureField.setText(rdv.getHeure().toString());
        prioriteField.setText(rdv.getPriorite());
    }

    private RDV getRDVFromForm() throws DateTimeParseException {
        int id = idField.getText().isEmpty() ? 0 : Integer.parseInt(idField.getText());
        LocalDate date = datePicker.getValue();
        LocalTime heure = LocalTime.parse(heureField.getText(), timeFormatter);
        String priorite = prioriteField.getText();

        RDV rdv = new RDV(Time.valueOf(heure), new java.sql.Date(date.toEpochDay()), priorite);
        rdv.setId(id);
        return rdv;
    }

    @FXML
    private void handleNewRDV() {
        clearForm();
    }

    @FXML
    private void handleAddRDV() {
        try {
            RDV rdv = getRDVFromForm();
            rdvService.add(rdv);
            loadRDVs();
            clearForm();
            showAlert("Succès", "RDV ajouté avec succès et synchronisé avec Google Calendar", Alert.AlertType.INFORMATION);
        } catch (DateTimeParseException e) {
            showAlert("Erreur", "Format d'heure invalide. Utilisez HH:mm", Alert.AlertType.ERROR);
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur de base de données lors de l'ajout: " + e.getMessage(), Alert.AlertType.ERROR);
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de l'ajout: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleUpdateRDV() {
        if (idField.getText().isEmpty()) {
            showAlert("Avertissement", "Aucun RDV sélectionné", Alert.AlertType.WARNING);
            return;
        }

        try {
            RDV rdv = getRDVFromForm();
            rdvService.update(rdv);
            loadRDVs();
            clearForm();
            showAlert("Succès", "RDV mis à jour avec succès et synchronisé avec Google Calendar", Alert.AlertType.INFORMATION);
        } catch (DateTimeParseException e) {
            showAlert("Erreur", "Format d'heure invalide", Alert.AlertType.ERROR);
        } catch (Exception e) {
            showAlert("Erreur", "Échec de la mise à jour: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void confirmAndDeleteRDV(RDV rdv) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer le RDV");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer ce RDV ?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                rdvService.delete(rdv.getId());
                loadRDVs();
                if (idField.getText().equals(String.valueOf(rdv.getId()))) {
                    clearForm();
                }
                showAlert("Succès", "RDV supprimé avec succès et retiré de Google Calendar", Alert.AlertType.INFORMATION);
            } catch (SQLException e) {
                showAlert("Erreur", "Erreur de base de données lors de la suppression: " + e.getMessage(), Alert.AlertType.ERROR);
            } catch (IOException e) {
                showAlert("Erreur", "Erreur lors de la suppression dans Google Calendar: " + e.getMessage(), Alert.AlertType.ERROR);
            } catch (Exception e) {
                showAlert("Erreur", "Échec de la suppression: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void handleDeleteRDV() {
        if (idField.getText().isEmpty()) {
            showAlert("Avertissement", "Aucun RDV sélectionné", Alert.AlertType.WARNING);
            return;
        }

        try {
            int id = Integer.parseInt(idField.getText());
            RDV rdv = rdvService.getRDVById(id);
            if (rdv != null) {
                confirmAndDeleteRDV(rdv);
            } else {
                showAlert("Erreur", "RDV non trouvé", Alert.AlertType.ERROR);
            }
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur de base de données: " + e.getMessage(), Alert.AlertType.ERROR);
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de la suppression: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void clearForm() {
        idField.clear();
        datePicker.setValue(null);
        heureField.clear();
        prioriteField.clear();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void onAjouterRDV(ActionEvent event) {
        try {
            Calendarservice.createRDV(); // Call your Nylas calendar logic
            showAlert("Succès", "Le RDV a été ajouté à votre calendrier Nylas.", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            showAlert("Erreur", "Échec de l'ajout du RDV : " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }


    /**
     * Opens the calendar view in a new window
     */
    @FXML
    private void handleViewCalendar() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Calendar.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Calendrier des Rendez-vous");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir le calendrier: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    /**
     * Opens Google Calendar in the default web browser
     */
    @FXML
    private void handleOpenGoogleCalendar() {
        try {
            // Initialize Google Calendar service to ensure we're authenticated
            GoogleCalendarService.getCalendarService();

            // Open Google Calendar in the default browser
            Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
            if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
                desktop.browse(new URI("https://calendar.google.com/"));
                showAlert("Succès", "Google Calendar ouvert dans votre navigateur", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Erreur", "Impossible d'ouvrir le navigateur", Alert.AlertType.ERROR);
            }
        } catch (IOException e) {
            // Check if the error is related to placeholder credentials
            if (e.getMessage() != null && (e.getMessage().contains("YOUR_CLIENT_ID") || e.getMessage().contains("YOUR_CLIENT_SECRET"))) {
                showAlert("Configuration requise", 
                          "Les identifiants Google Calendar ne sont pas configurés. Veuillez mettre à jour le fichier de secrets client avec vos identifiants Google API.", 
                          Alert.AlertType.WARNING);
            } else {
                showAlert("Erreur", "Impossible d'ouvrir Google Calendar: " + e.getMessage(), Alert.AlertType.ERROR);
            }
            e.printStackTrace();
        } catch (URISyntaxException e) {
            showAlert("Erreur", "URL de Google Calendar invalide: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    /**
     * Handles accepting an appointment
     * 
     * @param rdv The appointment to accept
     */
    private void handleAcceptRDV(RDV rdv) {
        try {
            // Confirm with the user
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText("Accepter le rendez-vous");
            alert.setContentText("Êtes-vous sûr de vouloir accepter ce rendez-vous ?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // Accept the appointment
                RDV updatedRDV = rdvService.acceptRDV(rdv.getId());

                if (updatedRDV != null) {
                    // Send email notification
                    boolean emailSent = emailService.sendAppointmentAcceptedEmail(updatedRDV);

                    // Reload the RDVs to update the UI
                    loadRDVs();

                    // Show success message
                    String message = "Rendez-vous accepté avec succès.";
                    if (emailSent) {
                        message += " Notification envoyée au patient.";
                    }
                    showAlert("Succès", message, Alert.AlertType.INFORMATION);
                } else {
                    showAlert("Erreur", "Échec de l'acceptation du rendez-vous.", Alert.AlertType.ERROR);
                }
            }
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur de base de données: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        } catch (Exception e) {
            showAlert("Erreur", "Une erreur est survenue: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    /**
     * Handles refusing an appointment
     * 
     * @param rdv The appointment to refuse
     */
    private void handleRefuseRDV(RDV rdv) {
        try {
            // Confirm with the user
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText("Refuser le rendez-vous");
            alert.setContentText("Êtes-vous sûr de vouloir refuser ce rendez-vous ?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // Refuse the appointment
                RDV updatedRDV = rdvService.refuseRDV(rdv.getId());

                if (updatedRDV != null) {
                    // Send email notification
                    boolean emailSent = emailService.sendAppointmentRefusedEmail(updatedRDV);

                    // Reload the RDVs to update the UI
                    loadRDVs();

                    // Show success message
                    String message = "Rendez-vous refusé avec succès.";
                    if (emailSent) {
                        message += " Notification envoyée au patient.";
                    }
                    showAlert("Succès", message, Alert.AlertType.INFORMATION);
                } else {
                    showAlert("Erreur", "Échec du refus du rendez-vous.", Alert.AlertType.ERROR);
                }
            }
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur de base de données: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        } catch (Exception e) {
            showAlert("Erreur", "Une erreur est survenue: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }




    @FXML
    private void handlePatientRDV() {
        try {
            // Load the patient RDV FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("PatientrdvView.fxml"));
            Parent root = loader.load();

            // Get the current stage
            Stage stage = (Stage) idField.getScene().getWindow();

            // Set the new scene
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Show error message
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de navigation");
            alert.setHeaderText("Impossible d'ouvrir la page Patient RDV");
            alert.setContentText("Une erreur s'est produite lors du chargement de la page.");
            alert.showAndWait();
        }
    }
}
