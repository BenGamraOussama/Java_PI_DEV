package com.exemple.controllers;

import com.exemple.entities.Commande;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class CommandeCardController implements Initializable {
    @FXML private Label nomUtilisateur;
    @FXML private Label montantLabel;
    @FXML private Label dateLabel;

    private Commande commande;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Vérification de l'initialisation des labels
        System.out.println("CommandeCardController initialized");
        if (nomUtilisateur == null || montantLabel == null || dateLabel == null) {
            System.err.println("ERROR: One or more labels is null during initialization!");
        }
    }

    public void setCommandeData(Commande commande) {
        this.commande = commande;

        // Vérifier que les labels ne sont pas null
        if (nomUtilisateur == null || montantLabel == null || dateLabel == null) {
            System.err.println("ERROR: One or more labels is null in setCommandeData!");
            return;
        }

        // Afficher les données dans les logs pour débogage
        System.out.println("Setting commande data for ID: " + commande.getId());
        System.out.println("Montant: " + commande.getMontantTotal());
        System.out.println("Date: " + commande.getDateCommande());
        System.out.println("UserID: " + commande.getUserId());

        try {
            // Définir les textes des labels avec des couleurs distinctes pour le débogage
            nomUtilisateur.setText("Utilisateur #" + commande.getUserId());
            nomUtilisateur.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: black;");

            montantLabel.setText(String.format("%.2f €", commande.getMontantTotal()));
            montantLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");

            if (commande.getDateCommande() != null) {
                dateLabel.setText(commande.getDateCommande().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            } else {
                dateLabel.setText("Date inconnue");
            }
            dateLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");

            // Forcer une mise à jour de l'affichage
            nomUtilisateur.setVisible(true);
            montantLabel.setVisible(true);
            dateLabel.setVisible(true);

            // Redimensionner les HBox parents si nécessaire
            ensureHBoxVisibility(nomUtilisateur);
            ensureHBoxVisibility(montantLabel);
            ensureHBoxVisibility(dateLabel);

        } catch (Exception e) {
            System.err.println("Exception when setting label texts: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void ensureHBoxVisibility(Label label) {
        if (label.getParent() instanceof HBox) {
            HBox parentBox = (HBox) label.getParent();
            parentBox.setMinWidth(130);
            parentBox.setPrefWidth(100);
        }
    }

    @FXML
    public void handleCardClicked() {
        if (commande != null) {
            System.out.println("Commande clicked: " + commande.getId());
            // Code pour naviguer vers les détails de la commande
        }
    }
}