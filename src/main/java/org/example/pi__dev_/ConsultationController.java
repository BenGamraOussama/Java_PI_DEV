package org.example.pi__dev_;






import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import org.example.pi__dev_.dao.ConsultationDAO;
import org.example.pi__dev_.enteties.Consultation;
import org.example.pi__dev_.enteties.Etat;


import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

public class ConsultationController {
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

    private final ConsultationDAO consultationDAO = new ConsultationDAO();
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
                    }
                });
    }

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
        }
    }

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
        Label patientLabel = new Label("👤 Patient ID: " + consultation.getPatientId());

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
                prixLabel, modeLabel, etatLabel, patientLabel, buttonBox);

        return card;
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
        patientIdField.setText(String.valueOf(consultation.getPatientId()));
    }

    private Consultation getConsultationFromForm() {
        try {
            // Validate and parse patient ID
            int patientId = Integer.parseInt(patientIdField.getText());
            if (patientId <= 0) {
                showAlert("Erreur", "ID patient doit être positif", Alert.AlertType.ERROR);
                return null;
            }

            return new Consultation(
                    Date.valueOf(datePicker.getValue()),
                    Time.valueOf(heureField.getText()),
                    Double.parseDouble(prixField.getText()),
                    modeConsultationField.getText(),
                    etatComboBox.getValue(),
                    patientId // Passing as int
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
            consultationDAO.addConsultation(getConsultationFromForm());
            loadConsultations();
            clearForm();
            showAlert("Succès", "Consultation ajoutée avec succès", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de l'ajout: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleUpdateConsultation() {
        if (idField.getText().isEmpty()) {
            showAlert("Avertissement", "Aucune consultation sélectionnée", Alert.AlertType.WARNING);
            return;
        }
        try {
            consultationDAO.updateConsultation(getConsultationFromForm());
            loadConsultations();
            clearForm();
            showAlert("Succès", "Consultation mise à jour avec succès", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de la mise à jour: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleDeleteConsultation() {
        if (!idField.getText().isEmpty()) {
            Consultation consultation = consultationDAO.getConsultationById(
                    Integer.parseInt(idField.getText()));
            if (consultation != null) {
                confirmAndDeleteConsultation(consultation);
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
        patientIdField.clear();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}