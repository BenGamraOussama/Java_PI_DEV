package com.exemple.controllers;

import com.exemple.entities.Commande;
import com.exemple.entities.Produit;
import com.exemple.services.CommandeService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.Map;

public class PanierController {
    @FXML
    private VBox panierContainer;
    @FXML
    private Label totalLabel;
    @FXML
    private TextField nomClientField;
    @FXML
    private TextField prenomClientField;
    @FXML
    private Button validerButton;

    private Map<Produit, Integer> produits;
    private ProduitController parentController;
    private CommandeService commandeService;

    public PanierController() {
        commandeService = new CommandeService();
    }

    public void setProduits(Map<Produit, Integer> produits) {
        this.produits = produits;
        updateUI();
    }

    public void setParentController(ProduitController parentController) {
        this.parentController = parentController;
    }

    private void updateUI() {
        panierContainer.getChildren().clear();
        double total = 0;

        for (Map.Entry<Produit, Integer> entry : produits.entrySet()) {
            Produit produit = entry.getKey();
            int quantite = entry.getValue();
            total += produit.getPrix() * quantite;

            Label produitLabel = new Label(produit.getNom() + " x" + quantite + " - " + 
                                         String.format("%.2f €", produit.getPrix() * quantite));
            panierContainer.getChildren().add(produitLabel);
        }

        totalLabel.setText(String.format("Total: %.2f €", total));
    }

    @FXML
    private void validerCommande() {
        if (nomClientField.getText().isEmpty() || prenomClientField.getText().isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs");
            return;
        }

        Commande commande = new Commande(
            LocalDate.now(),
            1, // ID utilisateur par défaut
            nomClientField.getText(),
            prenomClientField.getText()
        );

        // Ajouter les produits à la commande
        produits.forEach(commande::ajouterProduit);

        try {
            commandeService.ajouter(commande);
            showAlert("Succès", "Commande validée avec succès");
            parentController.clearPanier();
            // Retourner à la vue des produits
            parentController.showProduits();
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de la validation de la commande");
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 