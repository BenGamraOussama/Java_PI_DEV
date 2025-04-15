package pii.gui;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import pii.entities.Produit_categorie;
import pii.services.Produit_CategoriesServices;

import java.sql.SQLException;

public class ModifierCategorieController {

    @FXML
    private TextField nomCategorieField;

    private Produit_categorie categorie;

    // Méthode pour recevoir la catégorie à modifier
    public void setCategorie(Produit_categorie categorie) {
        this.categorie = categorie;
        nomCategorieField.setText(categorie.getNom()); // Pré-remplir le champ
    }

    @FXML
    private void handleEnregistrer(ActionEvent event) {
        try {
            // Mettre à jour la catégorie avec le nom fourni
            categorie.setNom(nomCategorieField.getText());

            // Utilisation du service pour modifier la catégorie dans la base de données
            Produit_CategoriesServices service = new Produit_CategoriesServices();
            service.modifier(categorie); // Met à jour la catégorie en base de données

            // Fermer la fenêtre après la modification
            Stage stage = (Stage) nomCategorieField.getScene().getWindow();
            stage.close(); // Fermer la fenêtre

        } catch (SQLException e) {
            e.printStackTrace(); // Affiche l'exception dans la console
            // Tu peux ajouter un message d'erreur ou une alerte ici
        }
    }


    @FXML
    private void handleAnnuler(ActionEvent event) {
        // Fermer la fenêtre sans enregistrer
        Stage stage = (Stage) nomCategorieField.getScene().getWindow();
        stage.close();
    }
}
