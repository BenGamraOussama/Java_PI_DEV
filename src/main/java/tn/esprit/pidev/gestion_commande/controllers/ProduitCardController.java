package tn.esprit.pidev.gestion_commande.controllers;

import tn.esprit.pidev.gestion_commande.entities.Produit;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class ProduitCardController {
    @FXML
    private VBox produitCard;
    @FXML
    private ImageView produitImage;
    @FXML
    private Label nomLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private Label prixLabel;
    @FXML
    private Label stockLabel;
    @FXML
    private Spinner<Integer> quantiteSpinner;

    private Produit produit;
    private ProduitController parentController;

    public void setProduit(Produit produit) {
        this.produit = produit;
        updateUI();
    }

    public void setParentController(ProduitController parentController) {
        this.parentController = parentController;
    }

    private void updateUI() {
        nomLabel.setText(produit.getNom());
        descriptionLabel.setText(produit.getDescription());
        prixLabel.setText(String.format("%.2f €", produit.getPrix()));
        stockLabel.setText("Stock: " + produit.getStock());
        
        // Charger l'image si elle existe
        if (produit.getImage() != null && !produit.getImage().isEmpty()) {
            try {
                Image image = new Image(getClass().getResourceAsStream("/img/" + produit.getImage()));
                produitImage.setImage(image);
            } catch (Exception e) {
                // Utiliser une image par défaut si l'image n'est pas trouvée
                produitImage.setImage(new Image(getClass().getResourceAsStream("/img/produits.png")));
            }
        }
    }

    @FXML
    private void ajouterAuPanier() {
        int quantite = quantiteSpinner.getValue();
        if (parentController != null) {
            parentController.ajouterAuPanier(produit, quantite);
        }
    }
} 