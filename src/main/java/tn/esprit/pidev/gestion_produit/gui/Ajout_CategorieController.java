package tn.esprit.pidev.gestion_produit.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.esprit.pidev.gestion_produit.entities.Produit_categorie;
import tn.esprit.pidev.gestion_produit.services.ProduitCategorieServices;

public class Ajout_CategorieController {

    @FXML
    private TextField nomCategorie;

    @FXML
    private Label alertLabel;

    private final ProduitCategorieServices categorieService = new ProduitCategorieServices(tn.esprit.pidev.Database.Database.getConnection());

    // Méthode pour ajouter une catégorie
    @FXML
    public void ajouterCategorie() {
        String categorie = nomCategorie.getText().trim();
        if (categorie.isEmpty()) {
            alertLabel.setText("Le nom de la catégorie ne peut pas être vide.");
        } else {
            try {
                Produit_categorie cat = new Produit_categorie(0, categorie);
                categorieService.ajouter(cat);
                alertLabel.setText("Catégorie ajoutée avec succès !");

                // Fermer la fenêtre actuelle
                Stage stage = (Stage) nomCategorie.getScene().getWindow();
                stage.close();

                // Ouvrir la liste des catégories
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/pidev/gestion_produit/ListeCategories.fxml"));
                Parent root = loader.load();
                Stage newStage = new Stage();
                newStage.setTitle("Liste des Catégories");
                newStage.setScene(new Scene(root));
                newStage.show();

            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText(null);
                alert.setContentText("Impossible d'ajouter la catégorie : " + e.getMessage());
                alert.showAndWait();
            }
        }
    }

    // Méthode pour annuler l'action
    @FXML
    public void annulerAction() {
        nomCategorie.clear();
        alertLabel.setText("");
    }
}