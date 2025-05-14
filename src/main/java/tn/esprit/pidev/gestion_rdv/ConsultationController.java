package tn.esprit.pidev.gestion_rdv;





import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.util.StringConverter;
import tn.esprit.pidev.gestion_rdv.dao.ConsultationDAO;
import tn.esprit.pidev.gestion_rdv.enteties.Consultation;
import tn.esprit.pidev.gestion_rdv.enteties.Etat;
import tn.esprit.pidev.gestion_rdv.enteties.Patient;
import tn.esprit.pidev.gestion_rdv.services.Patientservice;
import tn.esprit.pidev.gestion_rdv.services.TodoistAPI;
import tn.esprit.pidev.gestion_rdv.services.ZoomService;


import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.awt.Desktop;
import java.net.URI;

public class ConsultationController {
    @FXML private ComboBox<Patient> patientCombo;
    @FXML private TextField idField;
    @FXML private DatePicker datePicker;
    @FXML private TextField heureField;
    @FXML private TextField prixField;
    @FXML private TextField modeConsultationField;
    @FXML private ComboBox<Etat> etatComboBox;
    @FXML private FlowPane consultationCardsContainer;
    @FXML private TableView<Consultation> consultationTable;
    @FXML private TableView<?> traitementTable;
    @FXML private Button zoomButton;


    private final ConsultationDAO consultationDAO = new ConsultationDAO();
    private ObservableList<Consultation> consultations;
    private final ObservableList<Etat> etats = FXCollections.observableArrayList(Etat.values());
    private final ZoomService zoomService = new ZoomService();
    private final TodoistAPI todoistAPI = new TodoistAPI();

    @FXML
    public void initialize() {
        configureHeureField();
        loadConsultations();
        setupZoomButton();
        loadPatients(); // Ensure this is called within the initialize method

        patientCombo.setCellFactory(lv -> new ListCell<Patient>() {
            @Override
            protected void updateItem(Patient patient, boolean empty) {
                super.updateItem(patient, empty);
                setText(empty || patient == null ? "" : patient.getFirstName() + " (" + patient.getId() + ")");
            }
        });

        patientCombo.setConverter(new StringConverter<Patient>() {
            @Override
            public String toString(Patient patient) {
                return patient != null ? patient.getFirstName() + " (" + patient.getId() + ")" : "";
            }

            @Override
            public Patient fromString(String string) {
                return null; // Not needed for our case
            }
        });
    }
    @FXML
    public void saveConsultation() {
        try {
            // 1. Create a Consultation object from form input
            Consultation consultation = getConsultationFromForm();
            if (consultation == null) {
                return;
            }

            // 2. Save the consultation to the database
            if (idField.getText().isEmpty()) {
                consultationDAO.addConsultation(consultation);
            } else {
                consultation.setId(Integer.parseInt(idField.getText()));
                consultationDAO.updateConsultation(consultation);
            }

            // 3. Send the task to Todoist
            boolean taskCreated = todoistAPI.createConsultationTask(consultation);
            if (taskCreated) {
                showAlert("Succès", "Consultation enregistrée et tâche Todoist créée", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Avertissement", "Consultation enregistrée mais échec de création de la tâche Todoist", Alert.AlertType.WARNING);
            }

            // 4. Reload consultations and clear form
            loadConsultations();
            clearForm();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'enregistrement: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    private void setupZoomButton() {
        zoomButton.setOnAction(e -> {
            Consultation selected = consultationTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                String zoomUrl = zoomService.createZoomMeeting(selected);
                showZoomLink(zoomUrl);
            } else {
                showAlert("Aucune sélection", "Veuillez sélectionner une consultation", Alert.AlertType.WARNING);
            }
        });
    }

    private void showZoomLink(String url) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Lien Zoom créé");
        alert.setHeaderText("Lien de la consultation Zoom");
        alert.setContentText(url);

        // Create a button to open the URL in the default browser
        Button openButton = new Button("Ouvrir dans le navigateur");
        openButton.setOnAction(e -> {
            try {
                Desktop.getDesktop().browse(new URI(url));
            } catch (Exception ex) {
                showAlert("Erreur", "Impossible d'ouvrir le navigateur: " + ex.getMessage(), Alert.AlertType.ERROR);
            }
        });

        Label zoomLabel = new Label("Le lien Zoom a été généré:");
        zoomLabel.getStyleClass().add("zoom-label");

        TextField urlField = new TextField(url);
        urlField.setEditable(false);
        urlField.setPrefWidth(350);
        urlField.getStyleClass().add("zoom-link-field");

        VBox content = new VBox(10, 
            zoomLabel, 
            urlField,
            openButton
        );
        content.setPadding(new Insets(10));
        alert.getDialogPane().setContent(content);

        alert.showAndWait();
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

        // Add Zoom link if available
        VBox zoomBox = new VBox(5);
        if (consultation.getMeetLink() != null && !consultation.getMeetLink().isEmpty()) {
            Label zoomLabel = new Label("🔗 Lien Zoom:");
            zoomLabel.getStyleClass().add("zoom-label");

            TextField zoomLinkField = new TextField(consultation.getMeetLink());
            zoomLinkField.setEditable(false);
            zoomLinkField.setPrefWidth(250);
            zoomLinkField.getStyleClass().add("zoom-link-field");

            Button openZoomButton = new Button("Ouvrir");
            openZoomButton.getStyleClass().addAll("button", "button-zoom");
            openZoomButton.setOnAction(e -> {
                try {
                    String zoomLink = consultation.getMeetLink();
                    if (zoomLink != null && !zoomLink.isEmpty()) {
                        Desktop.getDesktop().browse(new URI(zoomLink));
                    } else {
                        showAlert("Erreur", "Lien Zoom non disponible", Alert.AlertType.WARNING);
                    }
                } catch (Exception ex) {
                    showAlert("Erreur", "Impossible d'ouvrir le navigateur: " + ex.getMessage(), Alert.AlertType.ERROR);
                }
            });

            HBox zoomLinkBox = new HBox(5, zoomLinkField, openZoomButton);
            zoomBox.getChildren().addAll(zoomLabel, zoomLinkBox);
        } else {
            Button createZoomButton = new Button("Créer lien Zoom");
            createZoomButton.getStyleClass().addAll("button", "button-zoom");
            createZoomButton.setOnAction(e -> {
                String zoomUrl = zoomService.createZoomMeeting(consultation);
                showZoomLink(zoomUrl);
                loadConsultations(); // Reload to show the updated link
            });
            zoomBox.getChildren().add(createZoomButton);
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
                prixLabel, modeLabel, etatLabel, zoomBox, new Separator(), buttonBox);

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
        patientCombo.setValue(consultation.getPatient());
    }

    private Consultation getConsultationFromForm() {
        try {
            // Validation des champs
            if (datePicker.getValue() == null) {
                showAlert("Erreur", "Veuillez sélectionner une date", Alert.AlertType.ERROR);
                return null;
            }

            if (heureField.getText().isEmpty()) {
                showAlert("Erreur", "Veuillez entrer une heure", Alert.AlertType.ERROR);
                return null;
            }

            Etat selectedEtat = etatComboBox.getValue();
            if (selectedEtat == null) {
                showAlert("Erreur", "Veuillez sélectionner un état", Alert.AlertType.ERROR);
                return null;
            }

            Patient selectedPatient = patientCombo.getValue();
            if (selectedPatient == null) {
                showAlert("Erreur", "Veuillez sélectionner un patient", Alert.AlertType.ERROR);
                return null;
            }

            if (prixField.getText().isEmpty()) {
                showAlert("Erreur", "Veuillez entrer un prix", Alert.AlertType.ERROR);
                return null;
            }

            if (modeConsultationField.getText().isEmpty()) {
                showAlert("Erreur", "Veuillez entrer un mode de consultation", Alert.AlertType.ERROR);
                return null;
            }

            // Création de la consultation
            return new Consultation(
                    Date.valueOf(datePicker.getValue()),
                    Time.valueOf(heureField.getText()),
                    Double.parseDouble(prixField.getText()),
                    modeConsultationField.getText(),
                    selectedEtat,  // Etat sélectionné
                    selectedPatient,
                    "" // Lien de rencontre vide par défaut
            );
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Format numérique invalide", Alert.AlertType.ERROR);
            return null;
        } catch (IllegalArgumentException e) {
            showAlert("Erreur", "Format de date/heure invalide", Alert.AlertType.ERROR);
            return null;
        } catch (Exception e) {
            showAlert("Erreur", "Erreur inattendue: " + e.getMessage(), Alert.AlertType.ERROR);
            return null;
        }
    }    @FXML
    private void handleNewConsultation() {
        clearForm();
    }

    @FXML
    private void handleAddConsultation() {
        // Clear the ID field to ensure we're adding a new consultation
        idField.clear();
        saveConsultation();
    }

    @FXML
    private void handleUpdateConsultation() {
        if (idField.getText().isEmpty()) {
            showAlert("Avertissement", "Aucune consultation sélectionnée", Alert.AlertType.WARNING);
            return;
        }
        // The ID field is already set, so we can just call saveConsultation
        saveConsultation();
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
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleCreateTodoistTask() {
        Consultation selected = consultationTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            System.out.println("Aucune consultation sélectionnée !");
            return;
        }

        try {
            TodoistAPI todoistAPI = new TodoistAPI();
            boolean success = todoistAPI.createConsultationTask(selected);
            if (success) {
                System.out.println("Tâche Todoist créée avec succès !");
            } else {
                System.out.println("Échec de la création de la tâche Todoist.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void loadPatients() {
        try {
            List<Patient> patients = Patientservice.getAllPatients();
            patientCombo.setItems(FXCollections.observableArrayList(patients));
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de charger la liste des patients", Alert.AlertType.ERROR);
        }
    }

    private void setupPatientSearch() {
        patientCombo.getEditor().textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.isEmpty()) {
                patientCombo.getItems().setAll(searchPatients(newVal));
            } else {
                loadPatients();
            }
        });
    }

    private List<Patient> searchPatients(String searchText) {
        try {
            return Patientservice.searchPatients(searchText);
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors de la recherche: " + e.getMessage(), Alert.AlertType.ERROR);
            return new ArrayList<>();
        }
    }
    private void setupEtatComboBox() {
        // Convertir les valeurs de l'énumération en liste observable
        ObservableList<Etat> etatList = FXCollections.observableArrayList(Etat.values());
        etatComboBox.setItems(etatList);

        // Configurer l'affichage dans la liste déroulante
        etatComboBox.setCellFactory(lv -> new ListCell<Etat>() {
            @Override
            protected void updateItem(Etat etat, boolean empty) {
                super.updateItem(etat, empty);
                setText(empty || etat == null ? "" : etat.getDisplayName());
            }
        });

        // Configurer la conversion entre l'affichage et la valeur
        etatComboBox.setConverter(new StringConverter<Etat>() {
            @Override
            public String toString(Etat etat) {
                return etat == null ? "" : etat.getDisplayName();
            }

            @Override
            public Etat fromString(String string) {
                return Etat.fromDisplayName(string);
            }
        });

        // Définir la valeur par défaut
        etatComboBox.getSelectionModel().select(Etat.EN_ATTENTE);
    }

}
