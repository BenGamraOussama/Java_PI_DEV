package pii.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import pii.entities.Produit;
import pii.services.PanierServices;

public class PanierController {

    @FXML
    private TableView<Produit> panierTable;
    @FXML
    private TableColumn<Produit, String> nomColumn;
    @FXML
    private TableColumn<Produit, Double> prixColumn;
    @FXML
    private TableColumn<Produit, Integer> quantiteColumn;
    @FXML
    private TableColumn<Produit, Void> deleteColumn;

    private PanierServices panierServices;
    private ObservableList<Produit> produitsPanier;

    public PanierController() {
        panierServices = new PanierServices();  // Assurez-vous de l'initialiser correctement avec vos services
        produitsPanier = FXCollections.observableArrayList();
    }

    @FXML
    private void initialize() {
        // Initialisation des colonnes du panier
        nomColumn.setCellValueFactory(cellData -> cellData.getValue().nomProperty());
        prixColumn.setCellValueFactory(cellData -> cellData.getValue().prixProperty().asObject());
        quantiteColumn.setCellValueFactory(cellData -> cellData.getValue().quantiteProperty().asObject());

        // Initialiser la colonne de suppression
        deleteColumn.setCellFactory(new Callback<TableColumn<Produit, Void>, TableCell<Produit, Void>>() {
            @Override
            public TableCell<Produit, Void> call(TableColumn<Produit, Void> param) {
                return new TableCell<Produit, Void>() {
                    private final Button deleteButton = new Button("Supprimer");

                    {
                        // Action sur le bouton "Supprimer"
                        deleteButton.setOnAction(event -> {
                            Produit produit = getTableRow() != null ? getTableRow().getItem() : null;
                            if (produit != null) {
                                supprimerProduit(produit);
                            } else {
                                showErrorMessage("Produit non valide.");
                            }
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(deleteButton);
                        }
                    }
                };
            }
        });

        // Charger les produits dans le panier
        chargerPanier();
    }

    private void chargerPanier() {
        // Charger les produits du panier à partir des services (par exemple, depuis la base de données)
        produitsPanier.addAll(panierServices.getProduitsDansPanier());
        panierTable.setItems(produitsPanier);
    }

    private void supprimerProduit(Produit produit) {
        if (produit != null) {
            // Supprimer le produit du panier via le service
            panierServices.supprimerProduitDuPanier(produit);

            // Mettre à jour la vue du panier après la suppression
            produitsPanier.remove(produit);
            showSuccessMessage("Produit supprimé du panier");
        } else {
            showErrorMessage("Aucun produit sélectionné à supprimer.");
        }
    }

    private void showSuccessMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Attention");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleViderPanier() {
        panierServices.viderPanier();
        produitsPanier.clear();
        showSuccessMessage("Panier vidé avec succès");
    }

    public void setUtilisateurId(int utilisateurId) {
    }

    public void setPanierServices(PanierServices panierServices) {
    }
}
