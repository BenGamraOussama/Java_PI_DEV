package com.exemple.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;

public class AddCommandeController {

    @FXML
    private TextField montantField;

    @FXML
    private TextField userIdField;

    @FXML
    private TableView<?> commandeTable;

    @FXML
    private TableColumn<?, ?> idCol;

    @FXML
    private TableColumn<?, ?> montantCol;

    @FXML
    private TableColumn<?, ?> userIdCol;

    // Méthode de contrôle de saisie
    private boolean validateFields() {
        boolean isValid = true;
        StringBuilder errorMessage = new StringBuilder();

        // Reset styles
        montantField.setStyle("");
        userIdField.setStyle("");

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

        // Si y a erreurs, afficher une alerte
        if (!isValid) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de saisie");
            alert.setHeaderText("Veuillez corriger les erreurs suivantes:");
            alert.setContentText(errorMessage.toString());
            alert.showAndWait();
        }

        return isValid;
    }

    // Bouton Ajouter
    @FXML
    private void handleAddCommande(ActionEvent event) {
        if (validateFields()) {
            System.out.println("Commande ajoutée avec succès !");
            // ➔ Ici tu ajoutes ta commande à la base ou à ta TableView
        }
    }

    // Bouton Modifier
    @FXML
    private void handleUpdateCommande(ActionEvent event) {
        if (validateFields()) {
            System.out.println("Commande modifiée avec succès !");
            // ➔ Ici tu modifies ta commande
        }
    }

    // Bouton Supprimer
    @FXML
    private void handleDeleteCommande(ActionEvent event) {
        System.out.println("Commande supprimée !");
        // ➔ Ici tu supprimes ta commande
    }
}
