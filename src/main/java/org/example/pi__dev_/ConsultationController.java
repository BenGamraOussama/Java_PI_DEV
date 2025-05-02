package org.example.pi__dev_;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
import java.time.LocalDate;
import java.util.Optional;

public class ConsultationController {
    // Existing fields
    @FXML private TextField idField;
    @FXML private DatePicker datePicker;
    @FXML private TextField heureField;
    @FXML private TextField prixField;
    @FXML private TextField modeConsultationField;
    @FXML private ComboBox<Etat> etatComboBox;
    @FXML private TextField patientIdField;
    @FXML private FlowPane consultationCardsContainer;
    @FXML private TableView<Consultation> consultationTable;
    @FXML private TableView<?> traitementTable;

    // New fields for names
    @FXML private Label patientNameLabel;
    @FXML private Label psychiatristNameLabel;

    private final ConsultationDAO consultationDAO = new ConsultationDAO();
    private final PatientDAO patientDAO = new PatientDAO();
    private final PsychiatreDAO psychiatristDAO = new PsychiatreDAO();
    private ObservableList<Consultation> consultations;
    private final ObservableList<Etat> etats = FXCollections.observableArrayList(Etat.values());

    @FXML
    public void initialize() {
        etatComboBox.setItems(etats);
        configureHeureField();
        loadConsultations();

        consultationTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        fillFormWithConsultation(newSelection);
                        updateNamesLabels(newSelection);
                    }
                });
    }

    // New method to update name labels
    private void updateNamesLabels(Consultation consultation) {
        try {
            Patient patient = patientDAO.getPatientById(consultation.getPatient().getId());
            if (patient != null) {
                patientNameLabel.setText(patient.getFirstName() + " " + patient.getLastName());
            } else {
                patientNameLabel.setText("Patient inconnu");
            }

            if (consultation.getPsychiatre() != null) {
                Psychiatre psychiatrist = psychiatristDAO.findById(consultation.getPsychiatre().getId());
                if (psychiatrist != null) {
                    psychiatristNameLabel.setText(psychiatrist.getFirstName() + " " + psychiatrist.getLastName());
                } else {
                    psychiatristNameLabel.setText("Psychiatre non assigné");
                }
            } else {
                psychiatristNameLabel.setText("Psychiatre non assigné");
            }
        } catch (Exception e) {
            System.err.println("Error loading names: " + e.getMessage());
            patientNameLabel.setText("Erreur chargement");
            psychiatristNameLabel.setText("Erreur chargement");
        }
    }

    // Updated createConsultationCard to include names
    private VBox createConsultationCard(Consultation consultation) {
        VBox card = new VBox();
        card.getStyleClass().add("consultation-card");
        card.setSpacing(8);
        card.setPadding(new Insets(12));
        card.setMinWidth(300);

        Label titleLabel = new Label("Consultation #" + consultation.getId());
        titleLabel.getStyleClass().add("card-title");

        LocalDate localDate = consultation.getDate().toLocalDate();
        Label dateLabel = new Label("📅 " + localDate.toString());
        Label heureLabel = new Label("🕒 " + consultation.getHeure().toString());
        Label prixLabel = new Label("💵 " + consultation.getPrix() + " €");
        Label modeLabel = new Label("💻 " + consultation.getModeconsultation());
        Label etatLabel = new Label("🏷 " + consultation.getEtatenum().toString());

        // New labels for names
        Label patientLabel = new Label();
        Label psychiatreLabel = new Label();

        try {
            Patient patient = patientDAO.getPatientById(consultation.getPatient().getId());
            patientLabel.setText("👤 Patient: " + patient.getFirstName() + " " + patient.getLastName());

            if (consultation.getPsychiatre() != null) {
                Psychiatre psychiatre = psychiatristDAO.findById(consultation.getPsychiatre().getId());
                psychiatreLabel.setText("👨‍⚕️ Psychiatre: " + psychiatre.getFirstName() + " " + psychiatre.getLastName());
            } else {
                psychiatreLabel.setText("👨‍⚕️ Psychiatre: Non assigné");
            }
        } catch (Exception e) {
            patientLabel.setText("👤 Patient: Erreur chargement");
            psychiatreLabel.setText("👨‍⚕️ Psychiatre: Erreur chargement");
        }

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        Button editButton = new Button("Modifier");
        editButton.getStyleClass().addAll("button", "button-edit");
        editButton.setOnAction(e -> {
            fillFormWithConsultation(consultation);
            consultationTable.getSelectionModel().select(consultation);
        });

        Button deleteButton = new Button("Supprimer");
        deleteButton.getStyleClass().addAll("button", "button-delete");
        deleteButton.setOnAction(e -> confirmAndDeleteConsultation(consultation));

        buttonBox.getChildren().addAll(editButton, deleteButton);
        card.getChildren().addAll(titleLabel, new Separator(), dateLabel, heureLabel,
                prixLabel, modeLabel, etatLabel, patientLabel, psychiatreLabel, buttonBox);

        return card;
    }

    // Rest of the existing methods remain exactly the same
    private void configureHeureField() {
        heureField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("^\\d{0,2}:?\\d{0,2}$")) {
                heureField.setText(oldValue);
            }
        });
    }

    private void loadConsultations() {
        try {
            consultations = FXCollections.observableArrayList(consultationDAO.getAllConsultations());
            consultationTable.setItems(consultations);

            if (consultationCardsContainer != null) {
                consultationCardsContainer.getChildren().clear();
                for (Consultation consultation : consultations) {
                    try {
                        consultationCardsContainer.getChildren().add(createConsultationCard(consultation));
                    } catch (Exception e) {
                        System.err.println("Error creating card for consultation " + consultation.getId() + ": " + e.getMessage());
                    }
                }
            }
        } catch (RuntimeException e) {
            System.err.println("Failed to load consultations: " + e.getMessage());
            showAlert("Erreur", "Impossible de charger les consultations", Alert.AlertType.ERROR);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void confirmAndDeleteConsultation(Consultation consultation) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText(null);
        alert.setContentText("Êtes-vous sûr de vouloir supprimer cette consultation?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                consultationDAO.deleteConsultation(consultation.getId());
                loadConsultations();
                if (idField.getText().equals(String.valueOf(consultation.getId()))) {
                    clearForm();
                }
                showAlert("Succès", "Consultation supprimée avec succès", Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                showAlert("Erreur", "Échec de la suppression: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private void fillFormWithConsultation(Consultation consultation) {
        idField.setText(String.valueOf(consultation.getId()));
        datePicker.setValue(consultation.getDate().toLocalDate());
        heureField.setText(consultation.getHeure().toString());
        prixField.setText(String.valueOf(consultation.getPrix()));
        modeConsultationField.setText(consultation.getModeconsultation());
        etatComboBox.setValue(consultation.getEtatenum());
    }

    private Consultation getConsultationFromForm() {
        try {
            return new Consultation(
                    Date.valueOf(datePicker.getValue()),
                    Time.valueOf(heureField.getText()),
                    Double.parseDouble(prixField.getText()),
                    modeConsultationField.getText(),
                    etatComboBox.getValue()
            );
        } catch (NumberFormatException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void handleNewConsultation() {
        clearForm();
    }

    @FXML
    private void handleAddConsultation() {
        try {
            Consultation newConsultation = getConsultationFromForm();

            // This will throw PatientNotFoundException if patient not found
            Patient patient = patientDAO.getPatientById(newConsultation.getPatient().getId());

            if (newConsultation.getPsychiatre() != null) {
                // This will throw PsychiatretNotFoundException if psychiatrist not found
                Psychiatre psychiatrist = psychiatristDAO.findById(newConsultation.getPsychiatre().getId());
            }

            // This will throw AppointmentConflictException if conflict exists
            if (consultationDAO.hasAppointmentConflict(newConsultation.getDate(), newConsultation.getHeure())) {
                throw new AppointmentConflictException("Time slot already booked");
            }

            loadConsultations();
            clearForm();
            showAlert("Success", "Consultation added successfully", Alert.AlertType.INFORMATION);

        } catch (AppointmentConflictException e) {
            showAlert("Conflict", e.getMessage(), Alert.AlertType.WARNING);
        } catch (PatientNotFoundException e) {
            showAlert("Error", "Patient not found: " + e.getMessage(), Alert.AlertType.ERROR);
        } catch (PsychiatretNotFoundException e) {
            showAlert("Error", "Psychiatrist not found: " + e.getMessage(), Alert.AlertType.ERROR);
        } catch (SQLException e) {
            showAlert("Database Error", e.getMessage(), Alert.AlertType.ERROR);
        } catch (Exception e) {
            showAlert("Error", "Unexpected error: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleUpdateConsultation() {
        if (idField.getText().isEmpty()) {
            showAlert("Warning", "No consultation selected", Alert.AlertType.WARNING);
            return;
        }

        try {
            Consultation updatedConsultation = getConsultationFromForm();

            // This will throw ConsultationNotFoundException if not found
            consultationDAO.getConsultationById(updatedConsultation.getId());

            // This will throw PatientNotFoundException if not found
            patientDAO.getPatientById(updatedConsultation.getPatient().getId());

            if (updatedConsultation.getPsychiatre() != null) {
                // This will throw PsychiatretNotFoundException if not found
                psychiatristDAO.findById(updatedConsultation.getPsychiatre().getId());
            }

            // Check for appointment conflicts
            if (consultationDAO.hasAppointmentConflict(
                    updatedConsultation.getDate(),
                    updatedConsultation.getHeure(),
                    updatedConsultation.getId())) {
                throw new AppointmentConflictException("Time slot conflict");
            }

            consultationDAO.updateConsultation(updatedConsultation);
            loadConsultations();
            clearForm();
            showAlert("Success", "Consultation updated", Alert.AlertType.INFORMATION);

        } catch (ConsultationNotFoundException e) {
            showAlert("Not Found", "Consultation not found", Alert.AlertType.ERROR);
        } catch (PatientNotFoundException e) {
            showAlert("Error", "Patient not found", Alert.AlertType.ERROR);
        } catch (PsychiatretNotFoundException e) {
            showAlert("Error", "Psychiatrist not found", Alert.AlertType.ERROR);
        } catch (AppointmentConflictException e) {
            showAlert("Conflict", e.getMessage(), Alert.AlertType.WARNING);
        } catch (SQLException e) {
            showAlert("Database Error", e.getMessage(), Alert.AlertType.ERROR);
        } catch (Exception e) {
            showAlert("Error", "Update failed: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleDeleteConsultation() {
        if (!idField.getText().isEmpty()) {
            try {
                Consultation consultation = consultationDAO.getConsultationById(
                        Integer.parseInt(idField.getText()));
                confirmAndDeleteConsultation(consultation);
            } catch (ConsultationNotFoundException e) {
                showAlert("Consultation introuvable", e.getMessage(), Alert.AlertType.ERROR);
            } catch (Exception e) {
                showAlert("Erreur", "Échec de la suppression: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private void clearForm() {
        idField.clear();
        datePicker.setValue(null);
        heureField.clear();
        prixField.clear();
        modeConsultationField.clear();
        etatComboBox.setValue(null);
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}