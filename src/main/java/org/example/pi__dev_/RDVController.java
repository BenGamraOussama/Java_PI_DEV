package org.example.pi__dev_;







import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.*;
import org.example.pi__dev_.dao.RDVDAO;
import org.example.pi__dev_.enteties.RDV;

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

    private RDVDAO rdvDAO;
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    @FXML
    public void initialize() {
        rdvDAO = new RDVDAO();
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
        List<RDV> rdvs = rdvDAO.getAllRDVs();

        if (rdvs != null) {
            for (RDV rdv : rdvs) {
                rdvCardsContainer.getChildren().add(createRDVCard(rdv));
            }
        }
    }

    private VBox createRDVCard(RDV rdv) {
        VBox card = new VBox();
        card.getStyleClass().add("rdv-card");
        card.setSpacing(8);
        card.setPadding(new Insets(12));
        card.setMinWidth(280);

        Label prioriteLabel = new Label("Priorité: " + rdv.getPriorite());
        prioriteLabel.getStyleClass().add("card-title");

        Label dateLabel = new Label("Date: " + rdv.getDate().toString());
        dateLabel.getStyleClass().add("card-date");

        Label heureLabel = new Label("Heure: " + rdv.getHeure().toString());
        heureLabel.getStyleClass().add("card-detail");

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        Button editButton = new Button("Modifier");
        editButton.getStyleClass().addAll("button", "button-edit");
        editButton.setOnAction(e -> fillFormWithRDV(rdv));

        Button deleteButton = new Button("Supprimer");
        deleteButton.getStyleClass().addAll("button", "button-delete");
        deleteButton.setOnAction(e -> confirmAndDeleteRDV(rdv));

        buttonBox.getChildren().addAll(editButton, deleteButton);
        card.getChildren().addAll(prioriteLabel, dateLabel, heureLabel, buttonBox);

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
            rdvDAO.addRDV(rdv);
            loadRDVs();
            clearForm();
            showAlert("Succès", "RDV ajouté avec succès", Alert.AlertType.INFORMATION);
        } catch (DateTimeParseException e) {
            showAlert("Erreur", "Format d'heure invalide. Utilisez HH:mm", Alert.AlertType.ERROR);
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
            rdvDAO.updateRDV(rdv);
            loadRDVs();
            clearForm();
            showAlert("Succès", "RDV mis à jour avec succès", Alert.AlertType.INFORMATION);
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
                rdvDAO.deleteRDV(rdv.getId());
                loadRDVs();
                if (idField.getText().equals(String.valueOf(rdv.getId()))) {
                    clearForm();
                }
                showAlert("Succès", "RDV supprimé avec succès", Alert.AlertType.INFORMATION);
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
            RDV rdv = rdvDAO.getRDVById(id);
            if (rdv != null) {
                confirmAndDeleteRDV(rdv);
            }
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
}