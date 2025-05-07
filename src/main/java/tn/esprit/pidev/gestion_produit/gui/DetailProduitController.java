package tn.esprit.pidev.gestion_produit.gui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import tn.esprit.pidev.gestion_produit.entities.Produit;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class DetailProduitController {

    @FXML private Button addToCartButton;
    @FXML private ImageView productImage;
    @FXML private Label nameLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label priceLabel;
    @FXML private Label quantityLabel;
    @FXML private Spinner<Integer> quantitySpinner;

    private Produit produit;
    private FrontController frontController;

    public void setProduit(Produit p) {
        if (p == null) {
            throw new IllegalArgumentException("Le produit ne peut pas être null");
        }

        this.produit = p;

        // Affichage de l'image du produit
        try {
            productImage.setImage(p.getFxImage());
        } catch (Exception e) {
            setDefaultImage();
        }

        // Mettre à jour les informations du produit
        nameLabel.setText(p.getNom());
        descriptionLabel.setText(p.getDescription());
        priceLabel.setText(String.format("Prix : %.2f TND", p.getPrix()));
        quantityLabel.setText("En stock : " + p.getQuantite());

        // Initialiser le Spinner
        SpinnerValueFactory<Integer> factory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, p.getQuantite(), 1);
        quantitySpinner.setValueFactory(factory);
    }

    private void setDefaultImage() {
        try {
            productImage.setImage(new Image(getClass().getResourceAsStream("/image/logo.png")));
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'image par défaut");
        }
    }

    public void setFrontController(FrontController frontController) {
        if (frontController == null) {
            throw new IllegalArgumentException("Le frontController ne peut pas être null");
        }
        this.frontController = frontController;
    }

    @FXML
    public void handleAddToCart() {
        if (produit == null) {
            showAlert("Erreur", "Aucun produit sélectionné");
            return;
        }

        int selectedQuantity = quantitySpinner.getValue();

        if (selectedQuantity > produit.getQuantite()) {
            showAlert("Erreur", "La quantité demandée dépasse le stock disponible");
            return;
        }

        Produit selectedProduct = new Produit();
        selectedProduct.setId(produit.getId());
        selectedProduct.setNom(produit.getNom());
        selectedProduct.setPrix(produit.getPrix());
        selectedProduct.setQuantite(selectedQuantity);

        if (frontController != null) {
            frontController.addToCart(selectedProduct);
            addToCartButton.getScene().getWindow().hide();
        } else {
            showAlert("Erreur", "Impossible d'ajouter au panier: contrôleur manquant");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}