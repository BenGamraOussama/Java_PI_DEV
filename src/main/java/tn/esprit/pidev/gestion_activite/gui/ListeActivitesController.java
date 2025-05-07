package tn.esprit.pidev.gestion_activite.gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.esprit.pidev.gestion_activite.entities.Activite;
import tn.esprit.pidev.gestion_activite.entities.Exercice;
import tn.esprit.pidev.gestion_activite.services.ActiviteService;
import tn.esprit.pidev.gestion_activite.services.ExerciceService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ListeActivitesController {

    @FXML
    private TableView<Activite> activiteTable;
    @FXML
    private TableColumn<Activite, String> titreColumn;
    @FXML
    private TableColumn<Activite, String> descriptionColumn;
    @FXML
    private TableColumn<Activite, String> statusColumn;
    @FXML
    private TableColumn<Activite, String> typeColumn;
    @FXML
    private TableColumn<Activite, String> questionColumn;
    @FXML
    private ComboBox<String> typeFilter;
    @FXML
    private ComboBox<String> statusFilter;
    @FXML
    private TextField searchField;

    private final ActiviteService activiteService = new ActiviteService();
    private final ExerciceService exerciceService = new ExerciceService();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private final ObservableList<Activite> activiteList = FXCollections.observableArrayList();
    private final FilteredList<Activite> filteredData = new FilteredList<>(activiteList, b -> true);

    @FXML
    public void initialize() {
        // Initialize table columns
        titreColumn.setCellValueFactory(cell -> cell.getValue().titreProperty());
        descriptionColumn.setCellValueFactory(cell -> cell.getValue().descriptionProperty());
        statusColumn.setCellValueFactory(cell -> cell.getValue().statusProperty());
        typeColumn.setCellValueFactory(cell -> cell.getValue().typeProperty());

        questionColumn.setCellValueFactory(cell -> {
            Activite a = cell.getValue();
            if ("exercice".equalsIgnoreCase(a.getType())) {
                try {
                    Exercice ex = exerciceService.findByActiviteId(a.getId());
                    return new SimpleStringProperty(ex != null ? ex.getQuestion() : "Question non disponible");
                } catch (SQLException e) {
                    return new SimpleStringProperty("Erreur");
                }
            }
            return new SimpleStringProperty("");
        });

        // Initialize filters
        initializeFilters();

        // Set up the filtered list
        SortedList<Activite> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(activiteTable.comparatorProperty());
        activiteTable.setItems(sortedData);

        // Set up search field listener
        setupSearchListener();

        // Load initial data
        loadActivites();
    }

    private void setupSearchListener() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            executorService.submit(() -> {
                updateFilter();
            });
        });
    }

    private void initializeFilters() {
        // Initialize type filter
        Set<String> types = new HashSet<>();
        types.add("Tous");
        types.add("mentale");
        types.add("relaxation");
        types.add("sociale");
        types.add("exercice");
        typeFilter.setItems(FXCollections.observableArrayList(types));
        typeFilter.getSelectionModel().selectFirst();

        // Initialize status filter
        Set<String> statuses = new HashSet<>();
        statuses.add("Tous");
        statuses.add("pas commencé");
        statuses.add("en cours");
        statuses.add("complété");
        statusFilter.setItems(FXCollections.observableArrayList(statuses));
        statusFilter.getSelectionModel().selectFirst();

        // Add listeners to filters
        typeFilter.valueProperty().addListener((obs, oldVal, newVal) -> updateFilter());
        statusFilter.valueProperty().addListener((obs, oldVal, newVal) -> updateFilter());
    }

    private void updateFilter() {
        String searchText = searchField.getText().toLowerCase();
        String selectedType = typeFilter.getValue();
        String selectedStatus = statusFilter.getValue();

        filteredData.setPredicate(activite -> {
            // Search filter
            boolean matchesSearch = searchText.isEmpty() ||
                    activite.getTitre().toLowerCase().contains(searchText) ||
                    activite.getDescription().toLowerCase().contains(searchText);

            // Type filter
            boolean typeMatch = selectedType.equals("Tous") ||
                    activite.getType().equalsIgnoreCase(selectedType);

            // Status filter
            boolean statusMatch = selectedStatus.equals("Tous") ||
                    activite.getStatus().equalsIgnoreCase(selectedStatus);

            return matchesSearch && typeMatch && statusMatch;
        });
    }

    @FXML
    private void resetFilters() {
        typeFilter.getSelectionModel().selectFirst();
        statusFilter.getSelectionModel().selectFirst();
        searchField.clear();
    }

    public void loadActivites() {
        activiteList.setAll(activiteService.getAll());
    }

    @FXML
    private void supprimerActivite() {
        Activite selected = activiteTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Veuillez sélectionner une activité.");
            return;
        }
        activiteService.supprimer(selected.getId());
        loadActivites();
        showAlert(Alert.AlertType.INFORMATION, "Activité supprimée.");
    }

    @FXML
    private void modifierActivite() {
        Activite selected = activiteTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Veuillez sélectionner une activité.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/pidev/ModifierActivite.fxml"));
            Parent root = loader.load();

            ModifierActiviteController controller = loader.getController();
            controller.setActivite(selected);

            Stage stage = new Stage();
            stage.setTitle("Modifier Activité");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            loadActivites();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur lors de l'ouverture du formulaire de modification: " + e.getMessage());
        }
    }

    @FXML
    private void openAjouterActivite() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/pidev/AjouterActivite.fxml"));
            Parent root = loader.load();

            AjouterActiviteController controller = loader.getController();
            controller.setListeActivitesController(this);

            Stage stage = new Stage();
            stage.setTitle("Ajouter une Activité");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur lors de l'ouverture du formulaire d'ajout: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setContentText(message);
        alert.show();
    }
}
