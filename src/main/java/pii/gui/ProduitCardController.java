package pii.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import pii.entities.Produit;
import pii.services.PanierServices;

public class ProduitCardController {

    @FXML
    private Label nomLabel;
    @FXML
    private Label prixLabel;
    @FXML
    private Label quantiteLabel;
    @FXML
    private Label disponibiliteLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private ImageView productImage;

    private Produit produit;
    private PanierServices panierServices;
    private int utilisateurId;

    // Setter pour le produit
    public void setProduit(Produit produit) {
        this.produit = produit;
        displayProduitInfo();
    }

    // Setter pour le panier
    public void setPanierServices(PanierServices panierServices) {
        this.panierServices = panierServices;
    }

    // Setter pour l'ID utilisateur
    public void setUtilisateurId(int utilisateurId) {
        this.utilisateurId = utilisateurId;
    }

    // Méthode pour afficher les informations du produit dans l'UI
    private void displayProduitInfo() {
        if (produit != null) {
            // Mise à jour des labels
            nomLabel.setText(produit.getNom());
            prixLabel.setText(String.format("%.2f €", produit.getPrix()));
            quantiteLabel.setText("Quantité : " + produit.getQuantite());
            disponibiliteLabel.setText(produit.isEnStock() ? "En stock" : "Rupture de stock");
            descriptionLabel.setText(produit.getDescription());

            // Afficher l'image du produit si elle existe
            if (produit.getImage() != null && !produit.getImage().isEmpty()) {
                productImage.setImage(new javafx.scene.image.Image("file:" + produit.getImage()));
            }
        }
    }

    // Méthode pour ajouter le produit au panier
    @FXML
    public void handleAjouterAuPanier(ActionEvent event) {
        if (produit != null) {
            if (produit.isEnStock()) {
                panierServices.ajouterProduitAuPanier(utilisateurId, produit); // Ajouter le produit au panier
                showSuccessMessage("Produit ajouté au panier !");
            } else {
                showErrorMessage("Le produit est en rupture de stock.");
            }
        }
    }

    // Affichage d'un message de succès
    private void showSuccessMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Affichage d'un message d'erreur
    private void showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Attention");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
