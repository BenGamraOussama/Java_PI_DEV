package pii.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import pii.entities.Produit;
import pii.services.ProduitServices;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ListeProduitsController {

    @FXML
    private TableView<Produit> tableProduits;

    @FXML
    private TableColumn<Produit, String> colNom;

    @FXML
    private TableColumn<Produit, String> colDescription;

    @FXML
    private TableColumn<Produit, Integer> colQuantite;

    @FXML
    private TableColumn<Produit, Boolean> colDisponible;

    @FXML
    private TableColumn<Produit, Void> colModifier;

    @FXML
    private TableColumn<Produit, Void> colSupprimer;

    private final ObservableList<Produit> produits = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Initialiser les colonnes de la TableView
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colQuantite.setCellValueFactory(new PropertyValueFactory<>("quantite"));
        colDisponible.setCellValueFactory(new PropertyValueFactory<>("disponible"));

        // Ajouter les colonnes avec boutons Modifier et Supprimer
        ajouterBoutonsModifier();
        ajouterBoutonsSupprimer();

        // Charger les produits depuis la base
        rafraichirListeProduits();
    }

    private void ajouterBoutonsModifier() {
        Callback<TableColumn<Produit, Void>, TableCell<Produit, Void>> cellFactory = param -> new TableCell<>() {
            private final Button btn = new Button("Modifier");

            {
                btn.setOnAction(event -> {
                    Produit produit = getTableView().getItems().get(getIndex());
                    showModifierProduitWindow(produit);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        };
        colModifier.setCellFactory(cellFactory);
    }

    private void ajouterBoutonsSupprimer() {
        Callback<TableColumn<Produit, Void>, TableCell<Produit, Void>> cellFactory = param -> new TableCell<>() {
            private final Button btn = new Button("Supprimer");

            {
                btn.setOnAction(event -> {
                    Produit produit = getTableView().getItems().get(getIndex());
                    supprimerProduit(produit);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        };
        colSupprimer.setCellFactory(cellFactory);
    }

    private void supprimerProduit(Produit produit) {
        try {
            ProduitServices service = new ProduitServices();
            service.deleteProduit(produit.getId()); // ⚠️ Assure-toi que cette méthode supprime bien de la BD
            produits.remove(produit); // supprime de la table
        } catch (SQLException e) {
            afficherErreur("Erreur", "Échec de la suppression", e);
        }
    }

    @FXML
    private void rafraichirListeProduits() {
        try {
            ProduitServices produitService = new ProduitServices();
            List<Produit> produitsBD = produitService.afficher();
            produits.setAll(produitsBD);
            tableProduits.setItems(produits);
        } catch (SQLException e) {
            afficherErreur("Erreur SQL", "Erreur lors du chargement des produits", e);
        }
    }

    private void afficherErreur(String titre, String message, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(message);
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }

    public void showModifierProduitWindow(Produit produit) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierProduit.fxml"));
            Parent root = loader.load();

            ModifierProduitController controller = loader.getController();
            controller.setProduit(produit); // Passe le produit au contrôleur de modification

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Modifier le produit");
            stage.setScene(scene);

            // Rafraîchir la liste quand la fenêtre est fermée
            stage.setOnHiding(event -> rafraichirListeProduits());

            stage.show();
        } catch (IOException e) {
            afficherErreur("Erreur FXML", "Impossible d’ouvrir la fenêtre de modification", e);
        }
    }
}
