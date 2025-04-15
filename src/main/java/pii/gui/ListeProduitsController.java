package pii.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import pii.entities.Produit;
import pii.services.ProduitServices;

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
        // Initialisation des colonnes
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colQuantite.setCellValueFactory(new PropertyValueFactory<>("quantite"));
        colDisponible.setCellValueFactory(new PropertyValueFactory<>("disponible"));
        produits.addAll(
                new Produit("Produit A", "Description A", true, 10, 4.5),
                new Produit("Produit B", "Description B", false, 5, 3.8)
        );
        tableProduits.setItems(produits);

        // Ajouter les boutons
        ajouterBoutonsModifier();
        ajouterBoutonsSupprimer();

        // Charger les produits à partir de la base de données
        rafraichirListeProduits();
    }

    private void ajouterBoutonsModifier() {
        Callback<TableColumn<Produit, Void>, TableCell<Produit, Void>> cellFactory = param -> new TableCell<>() {
            private final Button btn = new Button("Modifier");

            {
                btn.setOnAction(event -> {
                    Produit produit = getTableView().getItems().get(getIndex());
                    System.out.println("Modifier : " + produit.getNom());
                    // Implémenter ici la logique de modification (par exemple, ouvrir un formulaire de modification)
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
                    System.out.println("Supprimer : " + produit.getNom());
                    // Supprimer de la liste observable (il faudrait aussi supprimer de la base de données si nécessaire)
                    produits.remove(produit);
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

    @FXML
    private void rafraichirListeProduits() {
        try {
            ProduitServices produitService = new ProduitServices();
            List<Produit> produitsBD = produitService.afficher();

            produits.setAll(produitsBD); // Met à jour la liste observable
            tableProduits.setItems(produits); // Applique la liste observable à la TableView

        } catch (SQLException e) {
            afficherErreur("Erreur", "Erreur lors du chargement des produits", e);
        }
    }

    private void afficherErreur(String titre, String message, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(message);
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }
}
