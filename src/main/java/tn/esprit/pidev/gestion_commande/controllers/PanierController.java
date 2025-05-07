package tn.esprit.pidev.gestion_commande.controllers;

import tn.esprit.pidev.gestion_commande.entities.Commande;
import tn.esprit.pidev.gestion_commande.entities.Produit;
import tn.esprit.pidev.gestion_commande.services.CommandeService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
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
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez remplir tous les champs");
            return;
        }

        try {
            // Calculate total amount
            double total = 0;
            for (Map.Entry<Produit, Integer> entry : produits.entrySet()) {
                Produit produit = entry.getKey();
                int quantite = entry.getValue();
                total += produit.getPrix() * quantite;
            }

            // Show payment window
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/pidev/FXML/panier/PaymentView.fxml"));
            Scene scene = new Scene(loader.load());
            
            Stage paymentStage = new Stage();
            paymentStage.initModality(Modality.APPLICATION_MODAL);
            paymentStage.setTitle("Paiement");
            paymentStage.setScene(scene);
            
            PaymentController paymentController = loader.getController();
            paymentController.setStage(paymentStage);
            paymentController.setAmount(total);
            
            paymentStage.showAndWait();
            
            // Create order after successful payment
            Commande commande = new Commande(
                LocalDate.now(),
                1, // ID utilisateur par défaut
                nomClientField.getText(),
                prenomClientField.getText()
            );

            // Add products to order
            produits.forEach(commande::ajouterProduit);
            
            try {
                commandeService.ajouter(commande);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Commande validée avec succès");
                parentController.clearPanier();
                parentController.showProduits();
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", 
                         "Une erreur est survenue lors de l'enregistrement de la commande: " + e.getMessage());
                e.printStackTrace();
            }
            
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", 
                     "Une erreur est survenue lors du traitement de la commande: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}