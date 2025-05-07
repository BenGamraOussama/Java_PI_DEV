package tn.esprit.pidev.gestion_rdv;





import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import tn.esprit.pidev.gestion_rdv.dao.TraitementDAO;
import tn.esprit.pidev.gestion_rdv.enteties.Traitement;


import java.util.ArrayList;
import java.util.List;

public class TraitementController {
    @FXML private TextField idField;
    @FXML private ComboBox<String> typeField;
    @FXML private TextField medicamentField;
    @FXML private TextField suiviField;
    @FXML private TextField consultationIdField;
    @FXML private FlowPane traitementCardsContainer;
    @FXML private TextField searchField;
    @FXML private Pagination pagination;

    private static final int ITEMS_PER_PAGE = 4;

    private List<Traitement> allTraitements;

    private TraitementDAO traitementDAO;
    private ObservableList<Traitement> traitements;
    private FilteredList<Traitement> filteredTraitements;


    @FXML
    public void initialize() {
        traitementDAO = new TraitementDAO();
        allTraitements = traitementDAO.getAllTraitements(); // doit être fait après avoir instancié le DAO
        loadTraitements();

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            applyFilter(newValue);
        });
        if (allTraitements == null) {
            allTraitements = new ArrayList<>(); // évite les NullPointerException si DAO retourne null
        }

        List<Traitement> fetched = traitementDAO.getAllTraitements();
        if (fetched != null) {
            allTraitements = fetched;
        }

        if (pagination != null) {
            setupPagination();
        } else {
            System.err.println("Pagination is null. Check your FXML fx:id binding.");
        }

        typeField.getItems().addAll(
                "Thérapie cognitivo-comportementale",
                "Thérapie psychodynamique",
                "Thérapie humaniste",
                "Thérapie systémique"
        );
    }

    private void setupPagination() {
        if (allTraitements == null) {
            allTraitements = new ArrayList<>();
        }

        int pageCount = (int) Math.ceil((double) allTraitements.size() / ITEMS_PER_PAGE);
        pagination.setPageCount(pageCount > 0 ? pageCount : 1);
        pagination.setCurrentPageIndex(0);
        pagination.setPageFactory(this::createPage);
    }
    private void initializeTypeComboBox() {
        typeField.setItems(FXCollections.observableArrayList(
                "Thérapie cognitivo-comportementale",
                "Thérapie psychanalytique",
                "Thérapie humaniste",
                "Thérapie systémique"
        ));
    }

    private VBox createPage(int pageIndex) {
        traitementCardsContainer.getChildren().clear();

        int start = pageIndex * ITEMS_PER_PAGE;
        int end = Math.min(start + ITEMS_PER_PAGE, allTraitements.size());

        for (int i = start; i < end; i++) {
            Traitement traitement = allTraitements.get(i);
            traitementCardsContainer.getChildren().add(createTraitementCard(traitement));
        }

        return new VBox(); // placeholder, non utilisé car traitementCardsContainer est déjà dans le FXML
    }

    private void reloadData() {
        allTraitements = traitementDAO.getAllTraitements();
        setupPagination();
    }


    private void applyFilter(String filterText) {
        if (filteredTraitements == null) return;

        filteredTraitements.setPredicate(traitement -> {
            if (filterText == null || filterText.isEmpty()) {
                return true;
            }
            String lowerFilter = filterText.toLowerCase();
            return traitement.getType().toLowerCase().contains(lowerFilter) ||
                    traitement.getMedicament().toLowerCase().contains(lowerFilter) ||
                    traitement.getSuivi().toLowerCase().contains(lowerFilter) ||
                    String.valueOf(traitement.getConsultationId()).contains(lowerFilter);
        });

        traitementCardsContainer.getChildren().clear();
        for (Traitement t : filteredTraitements) {
            traitementCardsContainer.getChildren().add(createTraitementCard(t));
        }
    }



    private void loadTraitements() {
        traitements = FXCollections.observableArrayList(traitementDAO.getAllTraitements());
        filteredTraitements = new FilteredList<>(traitements, p -> true);
        applyFilter(searchField.getText());  // Appliquer filtre courant
    }


    private VBox createTraitementCard(Traitement traitement) {
        // Création de la carte
        VBox card = new VBox();
        card.getStyleClass().add("traitement-card");
        card.setAlignment(Pos.TOP_LEFT);
        card.setSpacing(8);
        card.setPadding(new Insets(12));
        card.setMinWidth(280);
        card.setMaxWidth(280);

        // Style de la carte
        card.setStyle("-fx-background-color: #ffffff; " +
                "-fx-border-color: #e0e0e0; " +
                "-fx-border-width: 1px; " +
                "-fx-border-radius: 5px; " +
                "-fx-background-radius: 5px; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 1);");

        // Titre (Type)
        Label typeLabel = new Label(traitement.getType());
        typeLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        typeLabel.setStyle("-fx-text-fill: #2c3e50;");

        // Séparateur
        Separator separator = new Separator();
        separator.setPadding(new Insets(5, 0, 5, 0));

        // Détails
        Label medicamentLabel = new Label("💊 " + traitement.getMedicament());
        Label suiviLabel = new Label("📝 " + traitement.getSuivi());
        Label consultationLabel = new Label("🆔 Consultation: " + traitement.getConsultationId());

        // Style des labels
        for (Label label : new Label[]{medicamentLabel, suiviLabel, consultationLabel}) {
            label.setFont(Font.font("System", 12));
            label.setStyle("-fx-text-fill: #34495e;");
        }

        // Boutons d'action
        HBox buttonBox = new HBox(8);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        Button editButton = new Button("Modifier");
        editButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        editButton.setOnAction(e -> fillFormWithTraitement(traitement));

        Button deleteButton = new Button("Supprimer");
        deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        deleteButton.setOnAction(e -> handleDeleteFromCard(traitement));

        buttonBox.getChildren().addAll(editButton, deleteButton);

        // Assemblage de la carte
        card.getChildren().addAll(
                typeLabel,
                separator,
                medicamentLabel,
                suiviLabel,
                consultationLabel,
                buttonBox
        );

        // Effet de survol
        card.setOnMouseEntered(e -> {
            card.setStyle(card.getStyle() + "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 8, 0, 0, 2);");
        });

        card.setOnMouseExited(e -> {
            card.setStyle(card.getStyle().replace("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 8, 0, 0, 2);",
                    "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 1);"));
        });

        return card;
    }

    private void handleDeleteFromCard(Traitement traitement) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer le traitement");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer ce traitement?\n" +
                "Type: " + traitement.getType() + "\n" +
                "Médicament: " + traitement.getMedicament());

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                traitementDAO.deleteTraitement(traitement.getId());
                loadTraitements();
                clearForm();
            }
        });
    }

    private void fillFormWithTraitement(Traitement traitement) {
        idField.setText(String.valueOf(traitement.getId()));
        typeField.setValue(traitement.getType());
        medicamentField.setText(traitement.getMedicament());
        suiviField.setText(traitement.getSuivi());
        consultationIdField.setText(String.valueOf(traitement.getConsultationId()));
    }

    private Traitement getTraitementFromForm() {
        String type = typeField.getValue(); // Changement ici
        String medicament = medicamentField.getText();
        String suivi = suiviField.getText();
        int consultationId = Integer.parseInt(consultationIdField.getText());

        Traitement traitement = new Traitement(type, medicament, suivi);
        traitement.setConsultationId(consultationId);

        if (!idField.getText().isEmpty()) {
            traitement.setId(Integer.parseInt(idField.getText()));
        }

        return traitement;
    }

    @FXML
    private void handleNewTraitement() {
        clearForm();
    }

    @FXML
    private void handleAddTraitement() {
        try {
            Traitement traitement = getTraitementFromForm();
            traitementDAO.addTraitement(traitement, traitement.getConsultationId());
            loadTraitements();
            clearForm();
        } catch (Exception e) {
            showAlert("Erreur", "Veuillez remplir tous les champs correctement", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUpdateTraitement() {
        if (!idField.getText().isEmpty()) {
            try {
                Traitement updatedTraitement = getTraitementFromForm();
                traitementDAO.updateTraitement(updatedTraitement);
                loadTraitements();
                clearForm();
            } catch (Exception e) {
                showAlert("Erreur", "Veuillez remplir tous les champs correctement", Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        } else {
            showAlert("Aucune sélection", "Veuillez sélectionner un traitement à modifier", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void handleDeleteTraitement() {
        if (!idField.getText().isEmpty()) {
            int id = Integer.parseInt(idField.getText());
            handleDeleteFromCard(traitementDAO.getTraitementById(id));
        } else {
            showAlert("Aucune sélection", "Veuillez sélectionner un traitement à supprimer", Alert.AlertType.WARNING);
        }
    }

    private void clearForm() {
        idField.clear();
        typeField.setValue(null);
        medicamentField.clear();
        suiviField.clear();
        consultationIdField.clear();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}