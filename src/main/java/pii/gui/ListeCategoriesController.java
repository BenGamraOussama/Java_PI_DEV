package pii.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import pii.entities.Produit_categorie;
import pii.services.Produit_CategoriesService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ListeCategoriesController {

    @FXML
    private TableView<Produit_categorie> tableCategories;

    @FXML
    private TableColumn<Produit_categorie, Integer> colId;

    @FXML
    private TableColumn<Produit_categorie, String> colNom;

    private final ObservableList<Produit_categorie> categories = FXCollections.observableArrayList();

    // Instance du service
    private final Produit_CategoriesService service = new Produit_CategoriesService();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        chargerCategories();
    }

    private void chargerCategories() {
        try {
            List<Produit_categorie> categoriesBD = service.afficher();
            categories.setAll(categoriesBD);
            tableCategories.setItems(categories);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleModifierCategorie(ActionEvent event) {
        Produit_categorie selectedCategorie = tableCategories.getSelectionModel().getSelectedItem();
        if (selectedCategorie != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierController.fxml"));
                Parent root = loader.load();

                ModifierCategorieController controller = loader.getController();
                controller.setCategorie(selectedCategorie);

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Modifier une catégorie");
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Impossible de charger ModifierController.fxml");
            }
        } else {
            System.out.println("Aucune catégorie sélectionnée");
        }
    }

    @FXML
    private void ajouterCategorie(ActionEvent event) {
        try {
            // Cette méthode ouvre une nouvelle fenêtre pour ajouter une catégorie
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterCategories.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Ajouter une catégorie");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void supprimerCategorie(ActionEvent event) {
        Produit_categorie categorie = tableCategories.getSelectionModel().getSelectedItem();

        if (categorie != null) {
            try {
                service.supprimer(categorie.getId()); // appel de la méthode non statique
                tableCategories.getItems().remove(categorie);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Suppression");
                alert.setHeaderText(null);
                alert.setContentText("Catégorie supprimée avec succès !");
                alert.showAndWait();
            } catch (SQLException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText(null);
                alert.setContentText("Erreur lors de la suppression.");
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Attention");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner une catégorie à supprimer !");
            alert.showAndWait();
        }
    }

    @FXML
    private void annuler(ActionEvent event) {
        try {
            // Navigue vers l'interface AjouterCategories.fxml dans la même fenêtre
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterCategories.fxml"));
            Parent root = loader.load();

            // Récupère le stage actuel à partir de l'événement
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter une catégorie");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
