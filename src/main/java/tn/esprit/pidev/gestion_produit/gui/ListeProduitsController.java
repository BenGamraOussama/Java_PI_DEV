package tn.esprit.pidev.gestion_produit.gui;

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
import tn.esprit.pidev.gestion_produit.entities.Produit;
import tn.esprit.pidev.gestion_produit.services.ProduitServices;
import tn.esprit.pidev.Database.Database;

import java.io.IOException;
import java.sql.Connection;
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
    private ProduitServices produitService;

    @FXML
    public void initialize() throws SQLException {
        // Initialisation de la connexion et du service
        Connection connection = Database.getConnection();
        produitService = new ProduitServices();

        // Configuration des colonnes
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colQuantite.setCellValueFactory(new PropertyValueFactory<>("quantite"));
        colDisponible.setCellValueFactory(new PropertyValueFactory<>("disponible"));

        ajouterBoutonsModifier();
        ajouterBoutonsSupprimer();

        rafraichirListeProduits();
    }

    private void ajouterBoutonsModifier() {
        Callback<TableColumn<Produit, Void>, TableCell<Produit, Void>> cellFactory = param -> new TableCell<>() {
            private final Button btn = new Button("Modifier");
            {
                btn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
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
                btn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                btn.setOnAction(event -> {
                    Produit produit = getTableView().getItems().get(getIndex());
                    showConfirmationBeforeDelete(produit);
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

    private void showConfirmationBeforeDelete(Produit produit) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Êtes-vous sûr de vouloir supprimer le produit : " + produit.getNom() + " ?");
        alert.setContentText("Cette action est irréversible.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                produitService.supprimerProduit(produit.getId());
                produits.remove(produit);
                afficherConfirmation("Succès", "Le produit a été supprimé avec succès.");
            }
        });
    }

    @FXML
    private void rafraichirListeProduits() {
        List<Produit> produitsBD = produitService.readList();
        produits.setAll(produitsBD);
        tableProduits.setItems(produits);

        if (produitsBD.isEmpty()) {
            afficherConfirmation("Information", "Aucun produit trouvé dans la base de données.");
        }
    }

    @FXML
    private void ajouterProduits() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/pidev/gestion_produit/AjouterProduit.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter un produit");
            stage.showAndWait(); // Attendre la fermeture de la fenêtre

            rafraichirListeProduits(); // Rafraîchir après la fermeture
        } catch (IOException e) {
            afficherErreur("Erreur", "Impossible d'ouvrir la fenêtre d'ajout", e);
        }
    }

    private void afficherErreur(String titre, String message, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(message);
        alert.setContentText(e.getMessage());
        alert.showAndWait();
        e.printStackTrace();
    }

    private void afficherConfirmation(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showModifierProduitWindow(Produit produit) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/pidev/gestion_produit/ModifierProduit.fxml"));
            Parent root = loader.load();

            ModifierProduitController controller = loader.getController();
            controller.setProduit(produit);
            controller.setListeProduitsController(this);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Modifier le produit");
            stage.showAndWait(); // Utilisation de showAndWait pour attendre la fermeture

            rafraichirListeProduits(); // Rafraîchir après la fermeture
        } catch (IOException e) {
            afficherErreur("Erreur", "Impossible d'ouvrir la fenêtre de modification", e);
        }
    }
}
