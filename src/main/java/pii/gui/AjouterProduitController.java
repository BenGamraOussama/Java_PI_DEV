package pii.gui;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import pii.entities.Produit;
import pii.entities.Produit_categorie;
import pii.services.ProduitServices;
import pii.services.Produit_CategoriesServices;

import java.io.File;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class AjouterProduitController {

    // Champs FXML
    @FXML private TextField nomField;
    @FXML private TextArea descField;
    @FXML private TextField quantiteField;
    @FXML private TextField noteField;
    @FXML private CheckBox dispoCheck;
    @FXML private ComboBox<Produit_categorie> categorieCombo;
    @FXML private TextField productImageField;
    @FXML private Button ajouterButton;
    @FXML private Button annulerButton;

    // Services
    private final ProduitServices produitService = new ProduitServices();
    private final Produit_CategoriesServices categorieService = new Produit_CategoriesServices();

    @FXML
    public void initialize() {
        try {
            chargerCategories();
            verifierInjectionFXML();
        } catch (SQLException e) {
            afficherErreur("Erreur d'initialisation", "Erreur lors du chargement des données", e);
        }
    }

    private void chargerCategories() throws SQLException {
        List<Produit_categorie> categories = categorieService.afficher();
        categorieCombo.setItems(FXCollections.observableArrayList(categories));
        if (!categories.isEmpty()) {
            categorieCombo.getSelectionModel().selectFirst();
        }
    }

    private void verifierInjectionFXML() {
        Objects.requireNonNull(nomField, "nomField n'a pas été injecté");
        Objects.requireNonNull(descField, "descField n'a pas été injecté");
        Objects.requireNonNull(quantiteField, "quantiteField n'a pas été injecté");
        Objects.requireNonNull(noteField, "noteField n'a pas été injecté");
        Objects.requireNonNull(dispoCheck, "dispoCheck n'a pas été injecté");
        Objects.requireNonNull(categorieCombo, "categorieCombo n'a pas été injecté");
        Objects.requireNonNull(productImageField, "productImageField n'a pas été injecté");
        Objects.requireNonNull(ajouterButton, "ajouterButton n'a pas été injecté");
        Objects.requireNonNull(annulerButton, "annulerButton n'a pas été injecté");
    }

    @FXML
    private void handleAjouterProduit(ActionEvent event) {
        try {
            Produit produit = validerEtCreerProduit();
            produitService.ajouter(produit);
            afficherAlerte("Succès", "Produit ajouté avec succès", Alert.AlertType.INFORMATION);
            reinitialiserFormulaire();
        } catch (IllegalArgumentException e) {
            afficherAlerte("Erreur de validation", e.getMessage(), Alert.AlertType.WARNING);
        } catch (SQLException e) {
            afficherErreur("Erreur", "Erreur lors de l'ajout du produit", e);
        }
    }

    private Produit validerEtCreerProduit() {
        String nom = validerChampTexte(nomField, "Le nom du produit est requis");
        String desc = validerChampTexte(descField, "La description du produit est requise");
        int qte = validerEntierPositif(quantiteField, "La quantité doit être un entier positif");
        double note = validerNote();
        boolean dispo = dispoCheck.isSelected();
        Produit_categorie categorie = validerCategorie();
        String imagePath = validerCheminImage();

        return new Produit(0, categorie, nom, desc, dispo, imagePath, qte, note);
    }

    private String validerChampTexte(TextInputControl field, String messageErreur) {
        String texte = field.getText().trim();
        if (texte.isEmpty()) {
            throw new IllegalArgumentException(messageErreur);
        }
        return texte;
    }

    private int validerEntierPositif(TextField field, String messageErreur) {
        try {
            int valeur = Integer.parseInt(field.getText().trim());
            if (valeur <= 0) {
                throw new IllegalArgumentException(messageErreur);
            }
            return valeur;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(messageErreur);
        }
    }

    private double validerNote() {
        try {
            if (noteField.getText().trim().isEmpty()) {
                return 0.0;
            }
            double note = Double.parseDouble(noteField.getText().trim());
            if (note < 0 || note > 5) {
                throw new IllegalArgumentException("La note doit être entre 0 et 5");
            }
            return note;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("La note doit être un nombre valide");
        }
    }

    private Produit_categorie validerCategorie() {
        Produit_categorie categorie = categorieCombo.getValue();
        if (categorie == null) {
            throw new IllegalArgumentException("Veuillez sélectionner une catégorie");
        }
        return categorie;
    }

    private String validerCheminImage() {
        String chemin = productImageField.getText().trim();
        if (chemin.isEmpty()) {
            throw new IllegalArgumentException("Veuillez sélectionner une image pour le produit");
        }
        return chemin;
    }

    @FXML
    private void handleBrowseImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp"),
                new FileChooser.ExtensionFilter("Tous les fichiers", "*.*")
        );

        Stage stage = (Stage) productImageField.getScene().getWindow();
        File fichierSelectionne = fileChooser.showOpenDialog(stage);

        if (fichierSelectionne != null) {
            productImageField.setText(fichierSelectionne.getAbsolutePath());
        }
    }

    @FXML
    private void handleAnnuler(ActionEvent event) {
        reinitialiserFormulaire();
    }

    private void reinitialiserFormulaire() {
        nomField.clear();
        descField.clear();
        quantiteField.clear();
        noteField.clear();
        dispoCheck.setSelected(false);
        if (!categorieCombo.getItems().isEmpty()) {
            categorieCombo.getSelectionModel().selectFirst();
        }
        productImageField.clear();
    }

    private void afficherAlerte(String titre, String message, Alert.AlertType type) {
        Alert alerte = new Alert(type);
        alerte.setTitle(titre);
        alerte.setHeaderText(null);
        alerte.setContentText(message);
        alerte.showAndWait();
    }

    private void afficherErreur(String titre, String message, Exception e) {
        afficherAlerte(titre, message + "\nDétails : " + e.getMessage(), Alert.AlertType.ERROR);
    }
}
