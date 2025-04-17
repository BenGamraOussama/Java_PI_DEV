package pii.gui;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import pii.entities.Produit;
import pii.entities.Produit_categorie;
import pii.services.ProduitServices;
import pii.services.Produit_CategoriesService;
import java.sql.SQLException;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;

public class AjouterProduitController {

    @FXML private TextField nomField;
    @FXML private TextArea descField;
    @FXML private TextField quantiteField;
    @FXML private TextField prixField;
    @FXML private ComboBox<Produit_categorie> categorieCombo;
    @FXML private TextField productImageField;

    private ProduitServices produitService = new ProduitServices();
    private Produit_CategoriesService categorieService = new Produit_CategoriesService();

    @FXML
    public void initialize() {
        try {
            // Charger toutes les catégories dans la ComboBox
            categorieCombo.getItems().addAll(categorieService.afficher());
        } catch (SQLException e) {
            showError("Erreur de chargement", "Échec du chargement des catégories", e);
        }
    }

    @FXML
    private void handleAnnuler(ActionEvent event) {
        // Réinitialiser tous les champs
        nomField.clear();
        descField.clear();
        quantiteField.clear();
        prixField.clear();
        productImageField.clear();
        categorieCombo.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleAjouterProduit(ActionEvent event) {
        try {
            // Vérifier si tous les champs sont remplis
            String nom = nomField.getText();
            String description = descField.getText();
            String quantiteStr = quantiteField.getText();
            String prixStr = prixField.getText();

            if (nom.isEmpty() || description.isEmpty() || quantiteStr.isEmpty() || prixStr.isEmpty()) {
                showError("Erreur", "Tous les champs doivent être remplis", null);
                return;
            }

            // Convertir la quantité et le prix en valeurs appropriées
            int quantite = Integer.parseInt(quantiteStr);
            double prix = Double.parseDouble(prixStr);

            // Vérifier si la quantité et le prix sont valides
            if (quantite < 0 || prix < 0) {
                showError("Erreur", "La quantité et le prix doivent être positifs", null);
                return;
            }

            // Vérifier la catégorie sélectionnée
            Produit_categorie categorie = categorieCombo.getValue();
            if (categorie == null) {
                showError("Erreur", "Veuillez sélectionner une catégorie", null);
                return;
            }

            // Créer un produit
            Produit produit = new Produit(0, categorie, nom, description, true, productImageField.getText(), quantite, prix);

            // Ajouter le produit à la base de données
            produitService.ajouter(produit);

            // Afficher un message de confirmation
            showConfirmation("Produit ajouté", "Le produit a été ajouté avec succès.");

            // Rediriger vers la liste des produits
            ouvrirListeProduits();

        } catch (SQLException e) {
            showError("Erreur SQL", "Échec de l'ajout du produit dans la base de données", e);
        } catch (NumberFormatException e) {
            showError("Erreur de format", "Veuillez entrer des valeurs valides pour la quantité et le prix", e);
        } catch (Exception e) {
            showError("Erreur inattendue", "Une erreur est survenue", e);
        }
    }

    // Méthode pour ouvrir la fenêtre de liste des produits
    private void ouvrirListeProduits() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListeProduits.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Liste des Produits");
            stage.setScene(new Scene(root));
            stage.show();

            // Fermer la fenêtre actuelle si nécessaire
            Stage currentStage = (Stage) nomField.getScene().getWindow();
            currentStage.close();
        } catch (Exception e) {
            showError("Erreur", "Impossible d'ouvrir la liste des produits : " + e.getMessage(), null);
            e.printStackTrace();
        }
    }

    // Gestion de l'image - méthode pour ouvrir la boîte de dialogue de sélection de fichier
    @FXML
    public void handleBrowseImage(ActionEvent event) {
        // Ouvrir une boîte de dialogue pour choisir un fichier image
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png", "*.gif"));
        Stage stage = (Stage) productImageField.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);

        // Si un fichier est sélectionné, mettre à jour le champ de texte avec le chemin du fichier
        if (file != null) {
            productImageField.setText(file.getAbsolutePath());
        }
    }

    private void showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String title, String message, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(message);
        alert.setContentText(e != null ? e.getMessage() : message);
        alert.showAndWait();
    }
}
