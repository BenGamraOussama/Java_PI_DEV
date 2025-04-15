package pii.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import pii.entities.Produit_categorie;
import pii.services.Produit_CategoriesServices;

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

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));

        chargerCategories();
    }

    private void chargerCategories() {
        try {
            Produit_CategoriesServices service = new Produit_CategoriesServices();
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
                // Charger la vue de modification
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierController.fxml"));
                Parent root = loader.load();

                // Passer la catégorie sélectionnée au contrôleur de modification
                ModifierCategorieController controller = loader.getController();
                controller.setCategorie(selectedCategorie); // Passer la catégorie sélectionnée

                // Afficher la nouvelle scène
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Modifier une catégorie");
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Impossible de charger modifier.fxml");
            }
        } else {
            System.out.println("Aucune catégorie sélectionnée");
        }
    }
}
