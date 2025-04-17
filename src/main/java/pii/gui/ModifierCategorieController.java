package pii.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import pii.entities.Produit_categorie;
import pii.services.Produit_CategoriesService;

import java.io.IOException;
import java.sql.SQLException;

public class ModifierCategorieController {

    @FXML
    private TextField nomCategorieField;

    private Produit_categorie categorie;

    // Méthode pour recevoir la catégorie à modifier et pré-remplir le champ
    public void setCategorie(Produit_categorie categorie) {
        this.categorie = categorie;
        nomCategorieField.setText(categorie.getNom());
    }

    @FXML
    private void handleEnregistrer(ActionEvent event) {
        try {
            // Mettre à jour la catégorie avec le nom fourni
            categorie.setNom(nomCategorieField.getText());

            // Utilisation du service pour modifier la catégorie dans la base de données
            Produit_CategoriesService service = new Produit_CategoriesService();
            service.update(categorie); // Met à jour la catégorie en base de données

            // Navigation vers l'interface Liste des Catégories après modification
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListeCategories.fxml"));
            Parent root = loader.load();

            // Récupère le stage courant à partir de l'événement et change la scène
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Liste des Catégories");
            stage.show();
        } catch (SQLException e) {
            e.printStackTrace();
            // Ici, vous pouvez ajouter une alerte ou un message d'erreur en cas d'exception SQL
        } catch (IOException e) {
            e.printStackTrace();
            // Gérer l'exception d'entrée/sortie lors du chargement du FXML
        }
    }

    @FXML
    private void handleAnnuler(ActionEvent event) {
        try {
            // Navigation vers l'interface Liste des Catégories sans enregistrer de modifications
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListeCategories.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Liste des Catégories");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
