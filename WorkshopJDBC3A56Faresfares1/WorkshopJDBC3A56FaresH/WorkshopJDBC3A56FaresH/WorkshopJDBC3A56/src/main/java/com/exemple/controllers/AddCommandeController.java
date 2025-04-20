package com.exemple.controllers;

import com.exemple.entities.Commande;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import com.exemple.services.CommandeService;

import java.time.LocalDate;
import java.util.List;

public class AddCommandeController {

    @FXML
    private TextField montantField;

    @FXML
    private TextField userIdField;

    @FXML
    private TableView<Commande> commandeTable;

    @FXML
    private TableColumn<Commande, Integer> idCol;

    @FXML
    private TableColumn<Commande, Double> montantCol;

    @FXML
    private TableColumn<Commande, Integer> userIdCol;

    private final CommandeService commandeService = new CommandeService();
    private final ObservableList<Commande> commandes = FXCollections.observableArrayList();

    private Commande selectedCommande = null;

    @FXML
    public void initialize() {
        idCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleIntegerProperty(cell.getValue().getId()).asObject());
        montantCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleDoubleProperty(cell.getValue().getMontantTotal()).asObject());
        userIdCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleIntegerProperty(cell.getValue().getUserId()).asObject());

        refreshTable();

        commandeTable.setOnMouseClicked(this::handleRowSelection);
    }

    @FXML
    private void handleAddCommande() {
        try {
            if (montantField.getText().isEmpty() || userIdField.getText().isEmpty()) {
                System.out.println("Veuillez remplir tous les champs !");
                return;
            }

            double montant = Double.parseDouble(montantField.getText());
            int userId = Integer.parseInt(userIdField.getText());

            Commande c = new Commande();
            c.setMontantTotal(montant);
            c.setUserId(userId);
            c.setDateCommande(LocalDate.now());

            commandeService.ajouter(c);
            clearFields();
            refreshTable();
        } catch (NumberFormatException e) {
            System.out.println("Format invalide pour le montant ou l'ID utilisateur.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void handleUpdateCommande() {
        if (selectedCommande == null) {
            System.err.println("Aucune commande sélectionnée.");
            return;
        }

        try {
            selectedCommande.setMontantTotal(Double.parseDouble(montantField.getText()));
            selectedCommande.setUserId(Integer.parseInt(userIdField.getText()));
            selectedCommande.setDateCommande(LocalDate.now());

            commandeService.modifier(selectedCommande);
            clearFields();
            refreshTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteCommande() {
        if (selectedCommande != null) {
            try {
                commandeService.supprimer(selectedCommande.getId());
                clearFields();
                refreshTable();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void refreshTable() {
        try {
            List<Commande> list = commandeService.afficher();
            commandes.setAll(list);
            commandeTable.setItems(commandes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        montantField.clear();
        userIdField.clear();
        selectedCommande = null;
    }

    private void handleRowSelection(MouseEvent event) {
        selectedCommande = commandeTable.getSelectionModel().getSelectedItem();
        if (selectedCommande != null) {
            montantField.setText(String.valueOf(selectedCommande.getMontantTotal()));
            userIdField.setText(String.valueOf(selectedCommande.getUserId()));
        }
    }
}
