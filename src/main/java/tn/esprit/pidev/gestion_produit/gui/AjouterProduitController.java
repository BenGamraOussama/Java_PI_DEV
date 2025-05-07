package tn.esprit.pidev.gestion_produit.gui;
import javafx.event.ActionEvent;
import javafx.scene.Node;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import tn.esprit.pidev.gestion_produit.entities.Produit;
import tn.esprit.pidev.gestion_produit.entities.Produit_categorie;
import tn.esprit.pidev.gestion_produit.services.ProduitCategorieServices;
import tn.esprit.pidev.gestion_produit.services.ProduitServices;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

public class AjouterProduitController {

    @FXML
    private ComboBox<Produit_categorie> categorieCombo;
    @FXML
    private TextField nomField;
    @FXML
    private TextArea descField;
    @FXML
    private CheckBox dispoCheck;
    @FXML
    private TextField quantiteField;
    @FXML
    private TextField prixField;
    @FXML
    private TextField productImageField;
    @FXML
    private Button parcourirBtn;
    @FXML
    private Button ajouterBtn;
    @FXML
    private Button annulerBtn;

    private ProduitCategorieServices categorieService = new ProduitCategorieServices(tn.esprit.pidev.Database.Database.getConnection());
    private ProduitServices produitService;

    public AjouterProduitController() {
        try {
            produitService = new ProduitServices();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            // Optionnel : afficher une alerte ou désactiver le bouton d'ajout
        }
    }

    @FXML
    public void initialize() {
        try {
            List<Produit_categorie> categories = categorieService.afficher();
            categories.removeIf(c -> c == null);
            categorieCombo.getItems().setAll(categories);
        } catch (Exception e) {
            showAlert("Erreur", "Impossible de charger les catégories : " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void handleBrowseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            productImageField.setText(file.getAbsolutePath());
        }
    }

    @FXML
    public void handleAjouterProduit() {
        Produit_categorie selectedCat = categorieCombo.getSelectionModel().getSelectedItem();
        if (selectedCat == null) {
            showAlert("Erreur", "Veuillez sélectionner une catégorie.", Alert.AlertType.ERROR);
            return;
        }
        String nom = nomField.getText().trim();
        String desc = descField.getText().trim();
        boolean dispo = dispoCheck.isSelected();
        int quantite = Integer.parseInt(quantiteField.getText().trim());
        float prix = Float.parseFloat(prixField.getText().trim());
        String imagePath = productImageField.getText().trim();
        byte[] imageBytes = null;
        try {
            if (!imagePath.isEmpty()) {
                imageBytes = Files.readAllBytes(new File(imagePath).toPath());
            }
        } catch (Exception e) {
            showAlert("Erreur", "Impossible de lire l'image : " + e.getMessage(), Alert.AlertType.ERROR);
            return;
        }

        Produit produit = new Produit(0, selectedCat, nom, desc, dispo, imageBytes, quantite, prix);
        try {
            produitService.addProduit(produit);
            showAlert("Succès", "Produit ajouté avec succès.", Alert.AlertType.INFORMATION);
            // Optionnel : fermer la fenêtre ou vider les champs
        } catch (Exception e) {
            showAlert("Erreur", "Impossible d'ajouter le produit : " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML
    private void handleAnnuler(ActionEvent event) {
        // Exemple : Fermer la fenêtre actuelle
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

}