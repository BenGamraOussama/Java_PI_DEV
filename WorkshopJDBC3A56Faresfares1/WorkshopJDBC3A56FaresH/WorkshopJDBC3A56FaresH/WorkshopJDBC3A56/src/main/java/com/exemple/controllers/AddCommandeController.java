package com.exemple.controllers;

import com.exemple.entities.Commande;
import com.exemple.services.CommandeService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AddCommandeController {

    @FXML
    private DatePicker dateField;

    @FXML
    private TextField montantField;

    @FXML
    private TextField userIdField;

    @FXML
    private TableView<Commande> commandeTable;

    @FXML
    private TableColumn<Commande, Integer> idCol;

    @FXML
    private TableColumn<Commande, LocalDate> dateCol;

    @FXML
    private TableColumn<Commande, Double> montantCol;

    @FXML
    private TableColumn<Commande, Integer> userIdCol;

    @FXML
    private TextField searchIdField;

    @FXML
    private TextField searchMontantField;

    @FXML
    private TextField searchUserField;

    private CommandeService commandeService;
    private static final Logger LOGGER = Logger.getLogger(AddCommandeController.class.getName());
    private ObservableList<Commande> commandesList;

    @FXML
    public void initialize() {
        commandeService = new CommandeService();
        commandesList = FXCollections.observableArrayList();
        setupTableColumns();
        loadAllCommandes();
        setupTableSelection();
    }

    private void setupTableColumns() {
        idCol.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        dateCol.setCellValueFactory(cellData -> cellData.getValue().dateCommandeProperty());
        montantCol.setCellValueFactory(cellData -> cellData.getValue().montantTotalProperty().asObject());
        userIdCol.setCellValueFactory(cellData -> cellData.getValue().userIdProperty().asObject());
    }

    private void setupTableSelection() {
        commandeTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                dateField.setValue(newSelection.getDateCommande());
                montantField.setText(String.valueOf(newSelection.getMontantTotal()));
                userIdField.setText(String.valueOf(newSelection.getUserId()));
            }
        });
    }

    private void loadAllCommandes() {
        try {
            commandesList.clear();
            commandesList.addAll(commandeService.afficher());
            commandeTable.setItems(commandesList);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error loading commandes", e);
            showErrorAlert("Erreur", "Impossible de charger les commandes");
        }
    }

    private boolean validateFields() {
        boolean isValid = true;
        StringBuilder errorMessage = new StringBuilder();

        // Reset styles
        dateField.setStyle("");
        montantField.setStyle("");
        userIdField.setStyle("");

        // Validation du champ Date
        if (dateField.getValue() == null) {
            errorMessage.append("- Le champ 'Date' est requis.\n");
            dateField.setStyle("-fx-border-color: red;");
            isValid = false;
        }

        // Validation du champ Montant
        if (montantField.getText().isEmpty()) {
            errorMessage.append("- Le champ 'Montant' est requis.\n");
            montantField.setStyle("-fx-border-color: red;");
            isValid = false;
        } else {
            try {
                double montant = Double.parseDouble(montantField.getText());
                if (montant <= 0) {
                    errorMessage.append("- Le 'Montant' doit être positif.\n");
                    montantField.setStyle("-fx-border-color: red;");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                errorMessage.append("- Le 'Montant' doit être un nombre valide.\n");
                montantField.setStyle("-fx-border-color: red;");
                isValid = false;
            }
        }

        // Validation du champ User ID
        if (userIdField.getText().isEmpty()) {
            errorMessage.append("- Le champ 'User ID' est requis.\n");
            userIdField.setStyle("-fx-border-color: red;");
            isValid = false;
        } else {
            try {
                int userId = Integer.parseInt(userIdField.getText());
                if (userId <= 0) {
                    errorMessage.append("- Le 'User ID' doit être positif.\n");
                    userIdField.setStyle("-fx-border-color: red;");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                errorMessage.append("- Le 'User ID' doit être un entier valide.\n");
                userIdField.setStyle("-fx-border-color: red;");
                isValid = false;
            }
        }

        if (!isValid) {
            showErrorAlert("Erreur de saisie", errorMessage.toString());
        }

        return isValid;
    }

    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void handleAddCommande(ActionEvent event) {
        if (validateFields()) {
            try {
                Commande commande = new Commande();
                commande.setDateCommande(dateField.getValue());
                commande.setMontantTotal(Double.parseDouble(montantField.getText()));
                commande.setUserId(Integer.parseInt(userIdField.getText()));
                commandeService.ajouter(commande);
                
                // Recharger toutes les commandes
                loadAllCommandes();
                clearFields();
                
                // Afficher un message de succès
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Succès");
                successAlert.setHeaderText(null);
                successAlert.setContentText("La commande a été ajoutée avec succès !");
                successAlert.showAndWait();
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error adding commande", e);
                showErrorAlert("Erreur", "Impossible d'ajouter la commande");
            }
        }
    }

    @FXML
    private void handleUpdateCommande(ActionEvent event) {
        if (validateFields()) {
            try {
                Commande selectedCommande = commandeTable.getSelectionModel().getSelectedItem();
                if (selectedCommande != null) {
                    selectedCommande.setDateCommande(dateField.getValue());
                    selectedCommande.setMontantTotal(Double.parseDouble(montantField.getText()));
                    selectedCommande.setUserId(Integer.parseInt(userIdField.getText()));
                    commandeService.modifier(selectedCommande);
                    loadAllCommandes();
                    clearFields();
                } else {
                    showErrorAlert("Erreur", "Veuillez sélectionner une commande à modifier");
                }
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error updating commande", e);
                showErrorAlert("Erreur", "Impossible de modifier la commande");
            }
        }
    }

    @FXML
    private void handleDeleteCommande(ActionEvent event) {
        Commande selectedCommande = commandeTable.getSelectionModel().getSelectedItem();
        if (selectedCommande != null) {
            try {
                commandeService.supprimer(selectedCommande.getId());
                loadAllCommandes();
                clearFields();
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error deleting commande", e);
                showErrorAlert("Erreur", "Impossible de supprimer la commande");
            }
        } else {
            showErrorAlert("Erreur", "Veuillez sélectionner une commande à supprimer");
        }
    }

    @FXML
    private void handleSearch(ActionEvent event) {
        try {
            String id = searchIdField.getText().trim();
            String montant = searchMontantField.getText().trim();
            String user = searchUserField.getText().trim();

            // Si tous les champs sont vides, afficher toutes les commandes
            if (id.isEmpty() && montant.isEmpty() && user.isEmpty()) {
                loadAllCommandes();
                return;
            }

            // Effectuer la recherche
            ObservableList<Commande> results = FXCollections.observableArrayList(
                commandeService.rechercher(id, montant, user)
            );
            
            // Mettre à jour le tableau avec les résultats
            commandeTable.setItems(results);
            
            // Afficher un message si aucun résultat n'est trouvé
            if (results.isEmpty()) {
                Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
                infoAlert.setTitle("Recherche");
                infoAlert.setHeaderText(null);
                infoAlert.setContentText("Aucune commande trouvée avec les critères spécifiés.");
                infoAlert.showAndWait();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error searching commandes", e);
            showErrorAlert("Erreur", "Impossible d'effectuer la recherche");
        }
    }

    @FXML
    private void handleReset(ActionEvent event) {
        clearFields();
        loadAllCommandes();
    }

    private void clearFields() {
        dateField.setValue(null);
        montantField.clear();
        userIdField.clear();
        searchIdField.clear();
        searchMontantField.clear();
        searchUserField.clear();
        commandeTable.getSelectionModel().clearSelection();
    }
}
