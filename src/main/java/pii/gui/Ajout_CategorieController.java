package pii.gui;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import pii.entities.Produit_categorie;
import pii.services.Produit_CategoriesServices;

public class Ajout_CategorieController {

    @FXML
    private TextField nomCategorie;

    // Service instancié une seule fois
    private final Produit_CategoriesServices categorieService = new Produit_CategoriesServices();

    @FXML
    private void ajouterCategorie() {
        String nom = nomCategorie.getText().trim();

        if (nom.isEmpty()) {
            showAlert("Erreur", "Le nom de la catégorie ne peut pas être vide");
            return;
        }

        try {
            // Optionnel : vérifier si la catégorie existe déjà
            if (categorieService.nomExiste(nom)) {
                showAlert("Erreur", "Cette catégorie existe déjà !");
                return;
            }

            Produit_categorie cat = new Produit_categorie(0, nom);
            categorieService.ajouter(cat);

            showAlert("Succès", "Catégorie ajoutée avec succès !");
            fermerFenetre();
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de l'ajout : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void annulerAction() {
        fermerFenetre();
    }

    private void fermerFenetre() {
        Stage stage = (Stage) nomCategorie.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
