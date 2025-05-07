package tn.esprit.pidev.gestion_commande.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ConfirmationCommandeController {
    @FXML
    private Label numeroCommandeLabel;
    @FXML
    private Label dateCommandeLabel;
    @FXML
    private Label totalCommandeLabel;

    private int commandeId;
    private double total;

    public void setCommandeDetails(int commandeId, double total) {
        this.commandeId = commandeId;
        this.total = total;
        updateLabels();
    }

    private void updateLabels() {
        numeroCommandeLabel.setText("Numéro de commande : #" + commandeId);
        dateCommandeLabel.setText("Date : " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        totalCommandeLabel.setText("Total : " + String.format("%.2f €", total));
    }

    @FXML
    private void retourProduits() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/pidev/FXML/produit/ProduitView.fxml"));
            VBox produitView = loader.load();
            
            // Remplacer le contenu de la scène
            AnchorPane root = (AnchorPane) numeroCommandeLabel.getScene().getRoot();
            root.getChildren().set(1, produitView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void voirCommandes() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/pidev/FXML/commande/CommandeView.fxml"));
            VBox commandeView = loader.load();
            
            // Remplacer le contenu de la scène
            AnchorPane root = (AnchorPane) numeroCommandeLabel.getScene().getRoot();
            root.getChildren().set(1, commandeView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
} 