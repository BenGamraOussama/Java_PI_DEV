package com.exemple.controllers;

import com.exemple.entities.Commande;
import com.exemple.entities.LigneCommande;
import com.exemple.entities.Produit;
import com.exemple.services.LigneCommandeService;
import com.exemple.services.ProduitService;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;

public class CommandCardController {

    @FXML
    private Label dateLabel;

    @FXML
    private Label montantLabel;

    @FXML
    private Label userLabel;

    @FXML
    private Button detailButton;

    @FXML
    private Button incrementButton;

    @FXML
    private Button decrementButton;
    private int produitId = 1;  // Exemple : produit fixe pour l'instant

    @FXML
    private TextField quantityField;
    private final ProduitService produitService = new ProduitService();


    private final LigneCommandeService ligneCommandeService = new LigneCommandeService();

    private int quantite = 1;

    public void setCommandeData(Commande commande) {
        dateLabel.setText("Date: " + commande.getDateCommande());
        montantLabel.setText("Montant: " + commande.getMontantTotal() + " €");
        userLabel.setText("Utilisateur ID: " + commande.getUserId());

        quantityField.setText(String.valueOf(quantite));

        // 🔹 Listener pour n'accepter que des chiffres
        quantityField.textProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                quantityField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        // 🔹 Bouton +
        incrementButton.setOnAction(e -> {
            quantite = getQuantiteFromField();
            quantite++;
            quantityField.setText(String.valueOf(quantite));
        });

        // 🔹 Bouton -
        decrementButton.setOnAction(e -> {
            quantite = getQuantiteFromField();
            if (quantite > 1) {
                quantite--;
                quantityField.setText(String.valueOf(quantite));
            }
        });

        // 🔹 Bouton Détails
        detailButton.setOnAction(event -> {
            try {
                quantite = getQuantiteFromField();

                // 🔹 Récupérer le produit complet
                Produit produit = produitService.getProduitById(produitId);

                LigneCommande ligne = new LigneCommande();
                ligne.setProduit(produit.getId());
                ligne.setQuantite(quantite);
                ligne.setPrix(produit.getPrix());   // Prix dynamique
                ligne.setCommandeId(commande.getId());

                ligneCommandeService.ajouter(ligne);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Succès");
                alert.setHeaderText(null);
                alert.setContentText("Ligne de commande pour '" + produit.getNom() + "' ajoutée avec " + quantite + " unité(s) !");
                alert.showAndWait();

                quantite = 1;
                quantityField.setText(String.valueOf(quantite));

            } catch (SQLException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText(null);
                alert.setContentText("Erreur lors de l'ajout de la ligne de commande.");
                alert.showAndWait();
            }
        });
    }
        /**
         * Récupère la quantité depuis le TextField de manière sécurisée
         */
        private int getQuantiteFromField () {
            try {
                int qte = Integer.parseInt(quantityField.getText());
                return qte > 0 ? qte : 1;
            } catch (NumberFormatException e) {
                return 1;
            }
        }


    }

