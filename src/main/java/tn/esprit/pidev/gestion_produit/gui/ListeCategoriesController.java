package tn.esprit.pidev.gestion_produit.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import tn.esprit.pidev.gestion_produit.entities.Produit_categorie;
import tn.esprit.pidev.gestion_produit.services.ProduitCategorieServices;

import java.sql.SQLException;
import java.util.List;

public class ListeCategoriesController {

    @FXML
    private TableView<Produit_categorie> tableCategories;
    @FXML
    private TableColumn<Produit_categorie, Integer> colId;
    @FXML
    private TableColumn<Produit_categorie, String> colNom;
    @FXML
    private Button ajouterBtn;
    @FXML
    private Button supprimerBtn;

    private ProduitCategorieServices categorieService;
    private ObservableList<Produit_categorie> categories = FXCollections.observableArrayList();

    public ListeCategoriesController() {
        this.categorieService = new ProduitCategorieServices(tn.esprit.pidev.Database.Database.getConnection());
    }

    @FXML
    private void initialize() {
        // Initialisation des colonnes
        colId.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        colNom.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNom()));

        // Charger les catégories depuis la base
        try {
            List<Produit_categorie> list = categorieService.afficher();
            list.removeIf(c -> c == null); // Supprime les éléments null
            categories.setAll(list);
            tableCategories.setItems(categories);
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de charger les catégories : " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void ajouterCategorie() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Ajouter une catégorie");
        dialog.setHeaderText(null);
        dialog.setContentText("Nom de la nouvelle catégorie :");
        dialog.showAndWait().ifPresent(nom -> {
            try {
                Produit_categorie cat = new Produit_categorie(0, nom);
                categorieService.ajouter(cat);
                categories.add(cat);
                showAlert("Succès", "Catégorie ajoutée.", Alert.AlertType.INFORMATION);
            } catch (SQLException e) {
                showAlert("Erreur", "Impossible d'ajouter la catégorie : " + e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }

    @FXML
    private void supprimerCategorie() {
        Produit_categorie selected = tableCategories.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                categorieService.supprimer(selected.getId());
                categories.remove(selected);
                showAlert("Succès", "Catégorie supprimée.", Alert.AlertType.INFORMATION);
            } catch (SQLException e) {
                showAlert("Erreur", "Impossible de supprimer la catégorie : " + e.getMessage(), Alert.AlertType.ERROR);
            }
        } else {
            showAlert("Erreur", "Veuillez sélectionner une catégorie à supprimer.", Alert.AlertType.ERROR);
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
    public void handleModifierCategorie(ActionEvent actionEvent) {
        Produit_categorie selected = tableCategories.getSelectionModel().getSelectedItem();
        if (selected != null) {
            TextInputDialog dialog = new TextInputDialog(selected.getNom());
            dialog.setTitle("Modifier une catégorie");
            dialog.setHeaderText(null);
            dialog.setContentText("Nouveau nom de la catégorie :");

            dialog.showAndWait().ifPresent(nouveauNom -> {
                if (nouveauNom != null && !nouveauNom.trim().isEmpty()) {
                    selected.setNom(nouveauNom);
                    categorieService.modifier(selected); // Assurez-vous que cette méthode met à jour la catégorie dans la base de données
                    tableCategories.refresh(); // Rafraîchit le TableView pour afficher le nouveau nom
                    showAlert("Succès", "Catégorie modifiée.", Alert.AlertType.INFORMATION);
                } else {
                    showAlert("Erreur", "Le nom de la catégorie ne peut pas être vide.", Alert.AlertType.ERROR);
                }
            });
        } else {
            showAlert("Erreur", "Veuillez sélectionner une catégorie à modifier.", Alert.AlertType.ERROR);
        }
    }

}