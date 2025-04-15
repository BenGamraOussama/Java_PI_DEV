package pii.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import pii.entities.Produit_categorie;
import pii.services.Produit_CategoriesServices;

public class Ajout_CategorieController {

    @FXML
    private Button btnAjouter;

    @FXML
    private TextField nomCategorie;

    private final Produit_CategoriesServices categorieService = new Produit_CategoriesServices();

    @FXML
    private void ajouterCategorie() {
        String nom = nomCategorie.getText().trim();

        if (nom.isEmpty()) {
            showAlert("Erreur", "Le nom de la catégorie ne peut pas être vide");
            return;
        }

        try {
            // Vérifie si la catégorie existe déjà
            if (categorieService.nomExiste(nom)) {
                showAlert("Erreur", "Cette catégorie existe déjà !");
                return;
            }

            // Ajoute la catégorie
            Produit_categorie cat = new Produit_categorie(0, nom);
            categorieService.ajouter(cat);

            showAlert("Succès", "Catégorie ajoutée avec succès !");

            // Ferme la fenêtre actuelle
            fermerFenetre();

            // Ouvre la fenêtre ListeCategories.fxml
            ouvrirListeCategories();

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

    private void ouvrirListeCategories() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListeCategories.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Liste des Catégories");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            showAlert("Erreur", "Impossible d'ouvrir la liste des catégories : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void handleBrowseImage(ActionEvent actionEvent) {
    }
}
