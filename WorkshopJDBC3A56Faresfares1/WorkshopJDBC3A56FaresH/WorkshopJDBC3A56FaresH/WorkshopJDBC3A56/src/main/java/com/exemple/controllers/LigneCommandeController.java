package com.exemple.controllers;

import com.exemple.entities.LigneCommande;
import com.exemple.services.LigneCommandeService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LigneCommandeController {

    @FXML
    private TextField produitField;
    @FXML
    private TextField quantiteField;
    @FXML
    private TextField prixField;
    @FXML
    private TextField commandeIdField;

    @FXML
    private TableView<LigneCommande> tableLignes;
    @FXML
    private TableColumn<LigneCommande, Integer> idCol;
    @FXML
    private TableColumn<LigneCommande, Integer> produitCol;
    @FXML
    private TableColumn<LigneCommande, Integer> quantiteCol;
    @FXML
    private TableColumn<LigneCommande, Double> prixCol;
    @FXML
    private TableColumn<LigneCommande, Double> totalCol;
    @FXML
    private TableColumn<LigneCommande, Integer> commandeIdCol;

    private LigneCommandeService ligneCommandeService;
    private ObservableList<LigneCommande> lignesList;
    private static final Logger LOGGER = Logger.getLogger(LigneCommandeController.class.getName());

    @FXML
    public void initialize() {
        ligneCommandeService = new LigneCommandeService();
        lignesList = FXCollections.observableArrayList();
        setupTableColumns();
        loadAllLignes();
        setupTableSelection();
    }

    private void setupTableColumns() {
        idCol.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        produitCol.setCellValueFactory(cellData -> cellData.getValue().produitProperty().asObject());
        quantiteCol.setCellValueFactory(cellData -> cellData.getValue().quantiteProperty().asObject());
        prixCol.setCellValueFactory(cellData -> cellData.getValue().prixProperty().asObject());
        totalCol.setCellValueFactory(cellData -> cellData.getValue().totalProperty().asObject());
        commandeIdCol.setCellValueFactory(cellData -> cellData.getValue().commandeIdProperty().asObject());
    }

    private void setupTableSelection() {
        tableLignes.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                produitField.setText(String.valueOf(newSelection.getProduit()));
                quantiteField.setText(String.valueOf(newSelection.getQuantite()));
                prixField.setText(String.valueOf(newSelection.getPrix()));
                commandeIdField.setText(String.valueOf(newSelection.getCommandeId()));
            }
        });
    }

    private void loadAllLignes() {
        try {
            lignesList.clear();
            lignesList.addAll(ligneCommandeService.afficher());
            tableLignes.setItems(lignesList);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error loading lignes de commande", e);
            showErrorAlert("Erreur", "Impossible de charger les lignes de commande");
        }
    }

    private boolean validateFields() {
        boolean isValid = true;
        StringBuilder errorMessage = new StringBuilder();

        // Reset styles
        produitField.setStyle("");
        quantiteField.setStyle("");
        prixField.setStyle("");
        commandeIdField.setStyle("");

        // Produit ID
        if (produitField.getText().isEmpty()) {
            errorMessage.append("- Le champ 'Produit ID' est requis.\n");
            produitField.setStyle("-fx-border-color: red;");
            isValid = false;
        } else {
            try {
                int produitId = Integer.parseInt(produitField.getText());
                if (produitId <= 0) {
                    errorMessage.append("- Le 'Produit ID' doit être positif.\n");
                    produitField.setStyle("-fx-border-color: red;");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                errorMessage.append("- Le 'Produit ID' doit être un entier valide.\n");
                produitField.setStyle("-fx-border-color: red;");
                isValid = false;
            }
        }

        // Quantité
        if (quantiteField.getText().isEmpty()) {
            errorMessage.append("- Le champ 'Quantité' est requis.\n");
            quantiteField.setStyle("-fx-border-color: red;");
            isValid = false;
        } else {
            try {
                int quantite = Integer.parseInt(quantiteField.getText());
                if (quantite <= 0) {
                    errorMessage.append("- La 'Quantité' doit être positive.\n");
                    quantiteField.setStyle("-fx-border-color: red;");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                errorMessage.append("- La 'Quantité' doit être un entier valide.\n");
                quantiteField.setStyle("-fx-border-color: red;");
                isValid = false;
            }
        }

        // Prix
        if (prixField.getText().isEmpty()) {
            errorMessage.append("- Le champ 'Prix' est requis.\n");
            prixField.setStyle("-fx-border-color: red;");
            isValid = false;
        } else {
            try {
                double prix = Double.parseDouble(prixField.getText());
                if (prix <= 0) {
                    errorMessage.append("- Le 'Prix' doit être positif.\n");
                    prixField.setStyle("-fx-border-color: red;");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                errorMessage.append("- Le 'Prix' doit être un nombre valide.\n");
                prixField.setStyle("-fx-border-color: red;");
                isValid = false;
            }
        }

        // Commande ID
        if (commandeIdField.getText().isEmpty()) {
            errorMessage.append("- Le champ 'Commande ID' est requis.\n");
            commandeIdField.setStyle("-fx-border-color: red;");
            isValid = false;
        } else {
            try {
                int commandeId = Integer.parseInt(commandeIdField.getText());
                if (commandeId <= 0) {
                    errorMessage.append("- Le 'Commande ID' doit être positif.\n");
                    commandeIdField.setStyle("-fx-border-color: red;");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                errorMessage.append("- Le 'Commande ID' doit être un entier valide.\n");
                commandeIdField.setStyle("-fx-border-color: red;");
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
        alert.setHeaderText("Erreur lors de l'opération");
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void handleAjouter(ActionEvent event) {
        if (validateFields()) {
            try {
                LigneCommande ligne = new LigneCommande();
                ligne.setProduit(Integer.parseInt(produitField.getText()));
                ligne.setQuantite(Integer.parseInt(quantiteField.getText()));
                ligne.setPrix(Double.parseDouble(prixField.getText()));
                ligne.setCommandeId(Integer.parseInt(commandeIdField.getText()));
                
                ligneCommandeService.ajouter(ligne);
                loadAllLignes();
                clearFields();
                
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Succès");
                successAlert.setHeaderText(null);
                successAlert.setContentText("La ligne de commande a été ajoutée avec succès !");
                successAlert.showAndWait();
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error adding ligne de commande", e);
                showErrorAlert("Erreur d'ajout", e.getMessage());
            } catch (NumberFormatException e) {
                showErrorAlert("Erreur de format", "Veuillez vérifier que tous les champs contiennent des valeurs numériques valides.");
            }
        }
    }

    @FXML
    private void handleModifier(ActionEvent event) {
        if (validateFields()) {
            try {
                LigneCommande selectedLigne = tableLignes.getSelectionModel().getSelectedItem();
                if (selectedLigne != null) {
                    selectedLigne.setProduit(Integer.parseInt(produitField.getText()));
                    selectedLigne.setQuantite(Integer.parseInt(quantiteField.getText()));
                    selectedLigne.setPrix(Double.parseDouble(prixField.getText()));
                    selectedLigne.setCommandeId(Integer.parseInt(commandeIdField.getText()));
                    
                    ligneCommandeService.modifier(selectedLigne);
                    loadAllLignes();
                    clearFields();
                } else {
                    showErrorAlert("Erreur", "Veuillez sélectionner une ligne de commande à modifier");
                }
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error updating ligne de commande", e);
                showErrorAlert("Erreur", "Impossible de modifier la ligne de commande");
            }
        }
    }

    @FXML
    private void handleSupprimer(ActionEvent event) {
        LigneCommande selectedLigne = tableLignes.getSelectionModel().getSelectedItem();
        if (selectedLigne != null) {
            try {
                ligneCommandeService.supprimer(selectedLigne.getId());
                loadAllLignes();
                clearFields();
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error deleting ligne de commande", e);
                showErrorAlert("Erreur", "Impossible de supprimer la ligne de commande");
            }
        } else {
            showErrorAlert("Erreur", "Veuillez sélectionner une ligne de commande à supprimer");
        }
    }

    private void clearFields() {
        produitField.clear();
        quantiteField.clear();
        prixField.clear();
        commandeIdField.clear();
        tableLignes.getSelectionModel().clearSelection();
    }
}
