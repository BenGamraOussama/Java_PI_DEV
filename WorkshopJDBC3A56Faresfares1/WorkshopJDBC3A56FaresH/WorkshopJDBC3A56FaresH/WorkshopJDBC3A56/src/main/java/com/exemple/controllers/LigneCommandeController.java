package com.exemple.controllers;

import com.exemple.entities.LigneCommande;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import com.exemple.services.LigneCommandeService;

import java.sql.SQLException;

public class LigneCommandeController {

    @FXML private TextField produitField;
    @FXML private TextField quantiteField;
    @FXML private TextField prixField;
    @FXML private TextField commandeIdField;

    @FXML private TableView<LigneCommande> tableLignes;
    @FXML private TableColumn<LigneCommande, Integer> idCol;
    @FXML private TableColumn<LigneCommande, Integer> produitCol;
    @FXML private TableColumn<LigneCommande, Integer> quantiteCol;
    @FXML private TableColumn<LigneCommande, Double> prixCol;
    @FXML private TableColumn<LigneCommande, Double> totalCol;
    @FXML private TableColumn<LigneCommande, Integer> commandeIdCol;

    private final LigneCommandeService service = new LigneCommandeService();
    private final ObservableList<LigneCommande> lignes = FXCollections.observableArrayList();
    private LigneCommande selected = null;

    @FXML
    public void initialize() {
        idCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleIntegerProperty(cell.getValue().getId()).asObject());
        produitCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleIntegerProperty(cell.getValue().getProduit()).asObject());
        quantiteCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleIntegerProperty(cell.getValue().getQuantite()).asObject());
        prixCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleDoubleProperty(cell.getValue().getPrix()).asObject());
        totalCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleDoubleProperty(cell.getValue().getTotal()).asObject());
        commandeIdCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleIntegerProperty(cell.getValue().getCommandeId()).asObject());

        refreshTable();
        tableLignes.setOnMouseClicked(this::handleRowSelection);
    }

    @FXML
    private void handleAjouter() {
        try {
            LigneCommande ligne = new LigneCommande();
            ligne.setProduit(Integer.parseInt(produitField.getText()));
            ligne.setQuantite(Integer.parseInt(quantiteField.getText()));
            ligne.setPrix(Double.parseDouble(prixField.getText()));
            ligne.setCommandeId(Integer.parseInt(commandeIdField.getText()));

            service.ajouter(ligne);
            clearFields();
            refreshTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleModifier() {
        if (selected == null) return;
        try {
            selected.setProduit(Integer.parseInt(produitField.getText()));
            selected.setQuantite(Integer.parseInt(quantiteField.getText()));
            selected.setPrix(Double.parseDouble(prixField.getText()));
            selected.setCommandeId(Integer.parseInt(commandeIdField.getText()));

            service.modifier(selected);
            clearFields();
            refreshTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSupprimer() {
        if (selected == null) return;
        try {
            service.supprimer(selected.getId());
            clearFields();
            refreshTable();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void handleRowSelection(MouseEvent event) {
        selected = tableLignes.getSelectionModel().getSelectedItem();
        if (selected != null) {
            produitField.setText(String.valueOf(selected.getProduit()));
            quantiteField.setText(String.valueOf(selected.getQuantite()));
            prixField.setText(String.valueOf(selected.getPrix()));
            commandeIdField.setText(String.valueOf(selected.getCommandeId()));
        }
    }

    private void refreshTable() {
        try {
            lignes.setAll(service.afficher());
            tableLignes.setItems(lignes);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        produitField.clear();
        quantiteField.clear();
        prixField.clear();
        commandeIdField.clear();
        selected = null;
    }
}
