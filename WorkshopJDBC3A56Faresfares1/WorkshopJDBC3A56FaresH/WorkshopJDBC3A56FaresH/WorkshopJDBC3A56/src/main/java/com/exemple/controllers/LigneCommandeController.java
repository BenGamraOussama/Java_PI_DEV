package com.exemple.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;

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
    private TableView<?> tableLignes;
    @FXML
    private TableColumn<?, ?> idCol;
    @FXML
    private TableColumn<?, ?> produitCol;
    @FXML
    private TableColumn<?, ?> quantiteCol;
    @FXML
    private TableColumn<?, ?> prixCol;
    @FXML
    private TableColumn<?, ?> totalCol;
    @FXML
    private TableColumn<?, ?> commandeIdCol;

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
                Integer.parseInt(produitField.getText());
            } catch (NumberFormatException e) {
                errorMessage.append("- 'Produit ID' doit être un entier.\n");
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
                    errorMessage.append("- 'Quantité' doit être positive.\n");
                    quantiteField.setStyle("-fx-border-color: red;");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                errorMessage.append("- 'Quantité' doit être un entier valide.\n");
                quantiteField.setStyle("-fx-border-color: red;");
                isValid = false;
            }
        }

        // Prix Unitaire
        if (prixField.getText().isEmpty()) {
            errorMessage.append("- Le champ 'Prix Unitaire' est requis.\n");
            prixField.setStyle("-fx-border-color: red;");
            isValid = false;
        } else {
            try {
                double prix = Double.parseDouble(prixField.getText());
                if (prix <= 0) {
                    errorMessage.append("- 'Prix Unitaire' doit être positif.\n");
                    prixField.setStyle("-fx-border-color: red;");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                errorMessage.append("- 'Prix Unitaire' doit être un nombre valide.\n");
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
                Integer.parseInt(commandeIdField.getText());
            } catch (NumberFormatException e) {
                errorMessage.append("- 'Commande ID' doit être un entier.\n");
                commandeIdField.setStyle("-fx-border-color: red;");
                isValid = false;
            }
        }

        // Show error if not valid
        if (!isValid) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de saisie");
            alert.setHeaderText("Corrigez les erreurs suivantes:");
            alert.setContentText(errorMessage.toString());
            alert.showAndWait();
        }

        return isValid;
    }

    @FXML
    private void handleAjouter(ActionEvent event) {
        if (validateFields()) {
            System.out.println("Ligne de commande ajoutée !");
            // ➔ Ajouter logique ajout ici
        }
    }

    @FXML
    private void handleModifier(ActionEvent event) {
        if (validateFields()) {
            System.out.println("Ligne de commande modifiée !");
            // ➔ Ajouter logique modification ici
        }
    }

    @FXML
    private void handleSupprimer(ActionEvent event) {
        System.out.println("Ligne de commande supprimée !");
        // ➔ Ajouter logique suppression ici
    }
}
