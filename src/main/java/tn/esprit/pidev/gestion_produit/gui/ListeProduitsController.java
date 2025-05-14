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
import java.util.stream.Collectors;

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

    @FXML
    private Button prevPageButton;

    @FXML
    private Button nextPageButton;

    @FXML
    private Label pageInfoLabel;

    private final ObservableList<Produit> produits = FXCollections.observableArrayList();
    private ProduitServices produitService;

    // Pagination variables
    private int currentPage = 1;
    private final int ITEMS_PER_PAGE = 5;
    private int totalPages = 1;
    private List<Produit> allProduits;

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

        // Initialize pagination
        currentPage = 1;
        prevPageButton.setDisable(true); // Initially disabled as we start at page 1

        rafraichirListeProduits();
    }

    /**
     * Handles the previous page button click
     */
    @FXML
    private void handlePreviousPage() {
        if (currentPage > 1) {
            currentPage--;
            displayCurrentPage();
            updatePaginationControls();
        }
    }

    /**
     * Handles the next page button click
     */
    @FXML
    private void handleNextPage() {
        if (currentPage < totalPages) {
            currentPage++;
            displayCurrentPage();
            updatePaginationControls();
        }
    }

    /**
     * Updates the pagination controls (button states and page info)
     */
    private void updatePaginationControls() {
        // Update page info label
        pageInfoLabel.setText("Page " + currentPage + " / " + totalPages);

        // Enable/disable navigation buttons
        prevPageButton.setDisable(currentPage <= 1);
        nextPageButton.setDisable(currentPage >= totalPages);
    }

    /**
     * Displays the current page of products
     */
    private void displayCurrentPage() {
        int startIndex = (currentPage - 1) * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, allProduits.size());

        List<Produit> currentPageItems = allProduits.subList(startIndex, endIndex);
        produits.setAll(currentPageItems);
        tableProduits.setItems(produits);
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

                // Remove from both lists
                produits.remove(produit);
                allProduits.remove(produit);

                // Recalculate pagination
                totalPages = (int) Math.ceil((double) allProduits.size() / ITEMS_PER_PAGE);
                if (totalPages == 0) totalPages = 1;

                // Adjust current page if needed
                if (currentPage > totalPages) {
                    currentPage = totalPages;
                }

                // Update UI
                updatePaginationControls();
                displayCurrentPage();

                afficherConfirmation("Succès", "Le produit a été supprimé avec succès.");
            }
        });
    }

    @FXML
    private void rafraichirListeProduits() {
        // Get all products from database
        allProduits = produitService.readList();

        // Reset to first page when refreshing
        currentPage = 1;

        // Calculate total pages
        totalPages = (int) Math.ceil((double) allProduits.size() / ITEMS_PER_PAGE);
        if (totalPages == 0) totalPages = 1;

        // Update pagination controls
        updatePaginationControls();

        // Display first page
        displayCurrentPage();

        if (allProduits.isEmpty()) {
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

            // After adding a product, refresh and go to the last page to see the new product
            rafraichirListeProduits(); // Rafraîchir après la fermeture

            // Navigate to the last page to show the newly added product
            if (totalPages > 0) {
                currentPage = totalPages;
                displayCurrentPage();
                updatePaginationControls();
            }
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

            // Store current page before refreshing
            int previousPage = currentPage;

            rafraichirListeProduits(); // Rafraîchir après la fermeture

            // Try to restore previous page if possible
            if (previousPage <= totalPages) {
                currentPage = previousPage;
                displayCurrentPage();
                updatePaginationControls();
            }
        } catch (IOException e) {
            afficherErreur("Erreur", "Impossible d'ouvrir la fenêtre de modification", e);
        }
    }
}
