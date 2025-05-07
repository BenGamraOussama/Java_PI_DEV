package tn.esprit.pidev.gestion_produit.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import tn.esprit.pidev.gestion_produit.entities.Produit;
import tn.esprit.pidev.gestion_produit.services.ProduitServices;
import tn.esprit.pidev.gestion_produit.services.RatingDAO;
import tn.esprit.pidev.gestion_produit.services.TranslationService;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class FrontController implements Initializable {

    @FXML private FlowPane productsContainer;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> priceFilterCombo;
    @FXML private Label cartCountLabel;
    @FXML private Button cartButton;
    @FXML private Button prevPageButton;
    @FXML private Button nextPageButton;
    @FXML private Label pageInfoLabel;

    private RatingDAO ratingService;
    private ProduitServices produitService;
    private TranslationService translationService;
    private final List<Produit> panier = new ArrayList<>();
    private int currentUserRating = 0;
    private Produit selectedProductForRating;

    // Pagination variables
    private int currentPage = 1;
    private final int ITEMS_PER_PAGE = 6;
    private int totalPages = 1;
    private List<Produit> allProduits = new ArrayList<>();
    private List<Produit> filteredProduits = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            produitService = new ProduitServices();
            translationService = TranslationService.getInstance();
            ratingService = new RatingDAO();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (cartButton != null) {
            cartButton.setOnAction(e -> ouvrirPanier());
        }

        priceFilterCombo.getItems().addAll(
                "Tous les prix",
                "Moins de 50 TND",
                "50 à 100 TND",
                "Plus de 100 TND"
        );
        priceFilterCombo.setValue("Tous les prix");
        priceFilterCombo.setOnAction(e -> filtrerProduits());

        // Initialize pagination
        currentPage = 1;
        prevPageButton.setDisable(true); // Initially disabled as we start at page 1

        try {
            chargerProduits();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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
        productsContainer.getChildren().clear();

        List<Produit> currentPageItems;
        if (filteredProduits.isEmpty()) {
            // If no filter is applied, use all products
            int startIndex = (currentPage - 1) * ITEMS_PER_PAGE;
            int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, allProduits.size());
            currentPageItems = allProduits.subList(startIndex, endIndex);
        } else {
            // If filter is applied, use filtered products
            int startIndex = (currentPage - 1) * ITEMS_PER_PAGE;
            int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, filteredProduits.size());
            currentPageItems = filteredProduits.subList(startIndex, endIndex);
        }

        for (Produit produit : currentPageItems) {
            Node productNode = createProductNode(produit);
            productsContainer.getChildren().add(productNode);
        }
    }

    private void chargerProduits() throws SQLException {
        // Get all products from database
        allProduits = produitService.getAllProduits();
        filteredProduits.clear();

        if (allProduits.isEmpty()) {
            productsContainer.getChildren().clear();
            productsContainer.getChildren().add(new Label("Aucun produit disponible"));
            return;
        }

        // Reset to first page when loading products
        currentPage = 1;

        // Calculate total pages
        totalPages = (int) Math.ceil((double) allProduits.size() / ITEMS_PER_PAGE);
        if (totalPages == 0) totalPages = 1;

        // Update pagination controls
        updatePaginationControls();

        // Display first page
        displayCurrentPage();
    }

    private VBox creerCarteProduit(Produit produit) {
        VBox card = new VBox();
        card.setSpacing(10);
        card.setStyle("-fx-padding: 10; -fx-border-color: #ccc; -fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: #ffffff;");
        card.setPrefWidth(180);

        ImageView imageView = new ImageView();
        Image image = produit.getFxImage();
        imageView.setImage(image);
        imageView.setFitWidth(120);
        imageView.setFitHeight(120);
        imageView.setPreserveRatio(true);

        Label nameLabel = new Label(produit.getNom());
        nameLabel.setStyle("-fx-font-weight: bold;");

        Label descriptionLabel = new Label(produit.getDescription());
        descriptionLabel.setWrapText(true);
        descriptionLabel.setMaxWidth(160);

        Label priceLabel = new Label(String.format("%.2f TND", produit.getPrix()));
        priceLabel.setStyle("-fx-text-fill: #e53935; -fx-font-weight: bold;");

        Label stockLabel = new Label();
        if (produit.estDisponible()) {
            stockLabel.setText("En stock: " + produit.getQuantite());
            stockLabel.setStyle("-fx-text-fill: green;");
        } else {
            stockLabel.setText("Rupture de stock");
            stockLabel.setStyle("-fx-text-fill: red;");
        }
// Bouton de traduction
        HBox translationBox = new HBox(5);
        translationBox.setAlignment(javafx.geometry.Pos.CENTER);

        ComboBox<String> languageCombo = new ComboBox<>();
        languageCombo.getItems().addAll(
                "Français", "English", "العربية", "Español", "Deutsch"
        );
        languageCombo.setValue("Français");

        Button translateButton = new Button("Traduire");
        translateButton.setOnAction(e -> {
            String selectedLanguage = languageCombo.getValue();
            String targetLang = getLanguageCode(selectedLanguage);
            String translatedText = translationService.translateText(produit.getDescription(), targetLang);
            descriptionLabel.setText(translatedText);
        });

        translationBox.getChildren().addAll(languageCombo, translateButton);
        translationBox.getChildren().addAll(languageCombo, translateButton);

        Button addButton = new Button("Ajouter au panier");
        addButton.setDisable(!produit.estDisponible());
        addButton.setOnAction(e -> ajouterAuPanier(produit));
        addButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");

        card.getChildren().addAll(imageView, nameLabel, descriptionLabel, priceLabel, stockLabel, translationBox, addButton);
        return card;
    }

    private String getLanguageCode(String language) {
        return switch (language) {
            case "English" -> TranslationService.ENGLISH;
            case "Français" -> TranslationService.FRENCH;
            case "العربية" -> TranslationService.ARABIC;
            case "Español" -> TranslationService.SPANISH;
            case "Deutsch" -> TranslationService.GERMAN;
            default -> TranslationService.FRENCH;
        };
    }

    private void ajouterAuPanier(Produit produit) {
        try {
            if (panier.stream().anyMatch(p -> p.getId() == produit.getId())) {
                showAlert(Alert.AlertType.INFORMATION, "Panier", "Ce produit est déjà dans le panier.");
                return;
            }

            panier.add(produit);
            produitService.ajouterProduitAuPanier(produit, 1);
            updateCartCount();

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Produit ajouté au panier avec succès !");
        } catch (SQLException e) {
            e.printStackTrace();
            afficherErreur("Erreur lors de l'ajout au panier");
        }
    }

    private void updateCartCount() {
        if (cartCountLabel != null) {
            cartCountLabel.setText(String.valueOf(panier.size()));
        }
    }

    @FXML
    public void handleSearch(ActionEvent event) {
        filtrerProduits();
    }

    private void filtrerProduits() {
        String searchText = searchField.getText().toLowerCase();
        String selectedRange = priceFilterCombo.getValue();

        // Filter products based on search text and price range
        filteredProduits.clear();

        for (Produit produit : allProduits) {
            boolean matchNom = produit.getNom().toLowerCase().contains(searchText);
            boolean matchPrix = switch (selectedRange) {
                case "Moins de 50 TND" -> produit.getPrix() < 50;
                case "50 à 100 TND" -> produit.getPrix() >= 50 && produit.getPrix() <= 100;
                case "Plus de 100 TND" -> produit.getPrix() > 100;
                default -> true;
            };

            if (matchNom && matchPrix) {
                filteredProduits.add(produit);
            }
        }

        // Reset to first page when filtering
        currentPage = 1;

        // Calculate total pages based on filtered products
        totalPages = (int) Math.ceil((double) filteredProduits.size() / ITEMS_PER_PAGE);
        if (totalPages == 0) totalPages = 1;

        // Update pagination controls
        updatePaginationControls();

        // Display first page of filtered products
        displayCurrentPage();
    }

    private void afficherErreur(String message) {
        showAlert(Alert.AlertType.ERROR, "Erreur", message);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void addToCart(Produit selectedProduct) {
        ajouterAuPanier(selectedProduct);
    }

    private void ouvrirPanier() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/panier_view.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();

            PanierController panierController = loader.getController();
            panierController.setCurrentUserId(1); // ID utilisateur par défaut
            panierController.setPanierItems(panier);

            stage.setTitle("Panier");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture du panier: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void handleStarClick(MouseEvent mouseEvent) {
        if (mouseEvent.getSource() instanceof Label) {
            Label clickedStar = (Label) mouseEvent.getSource();
            HBox starsContainer = (HBox) clickedStar.getParent();
            VBox productBox = (VBox) starsContainer.getParent();

            // Stocker le produit sélectionné pour le rating
            selectedProductForRating = (Produit) productBox.getUserData();

            // Trouver l'index de l'étoile cliquée
            currentUserRating = starsContainer.getChildren().indexOf(clickedStar) + 1;

            // Mettre à jour l'affichage des étoiles
            for (int i = 0; i < starsContainer.getChildren().size(); i++) {
                Label star = (Label) starsContainer.getChildren().get(i);
                star.setText(i < currentUserRating ? "★" : "☆");
                star.setStyle(i < currentUserRating ? "-fx-text-fill: gold;" : "-fx-text-fill: #ccc;");
            }
        }
    }

    @FXML
    public void submitRating(ActionEvent actionEvent) {
        try {
            if (selectedProductForRating != null && currentUserRating > 0) {
                ratingService.addRating(selectedProductForRating.getId(), 1, currentUserRating);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Merci pour votre évaluation !");

                // Store current page before refreshing
                int previousPage = currentPage;

                // Réinitialiser après soumission
                currentUserRating = 0;
                selectedProductForRating = null;

                // Recharger les produits pour mettre à jour les ratings
                chargerProduits();

                // Restore previous page if possible
                if (previousPage <= totalPages) {
                    currentPage = previousPage;
                    displayCurrentPage();
                    updatePaginationControls();
                }
            } else {
                showAlert(Alert.AlertType.WARNING, "Avertissement", "Veuillez sélectionner un produit et une évaluation.");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'enregistrer l'évaluation.");
            e.printStackTrace();
        }
    }

    private Node createProductNode(Produit produit) {
        VBox productBox = new VBox(10);
        productBox.setAlignment(Pos.CENTER);
        productBox.setPadding(new Insets(15));
        productBox.setStyle("-fx-background-color: white; -fx-border-color: #eee; -fx-border-radius: 5;");
        productBox.setUserData(produit); // Stocker le produit associé

        ImageView imageView = new ImageView(produit.getFxImage());
        imageView.setFitWidth(120);
        imageView.setFitHeight(120);
        imageView.setPreserveRatio(true);

        Label nameLabel = new Label(produit.getNom());
        nameLabel.setStyle("-fx-font-weight: bold;");

        Label priceLabel = new Label(String.format("%.2f TND", produit.getPrix()));
        priceLabel.setStyle("-fx-text-fill: #e53935; -fx-font-weight: bold;");

        // Section rating
        Label ratingTitle = new Label("Évaluation:");
        HBox ratingBox = new HBox(2);
        ratingBox.setAlignment(Pos.CENTER);

        double avgRating = ratingService.getAverageRatingForProduct(produit.getId());

        for (int i = 1; i <= 5; i++) {
            Label star = new Label(i <= avgRating ? "★" : "☆");
            star.setStyle(i <= avgRating ? "-fx-text-fill: gold;" : "-fx-text-fill: #ccc;");
            star.setOnMouseClicked(this::handleStarClick);
            ratingBox.getChildren().add(star);
        }

        Button addToCartBtn = new Button("Ajouter au panier");
        addToCartBtn.setOnAction(e -> ajouterAuPanier(produit));
        addToCartBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");

        productBox.getChildren().addAll(imageView, nameLabel, priceLabel, ratingTitle, ratingBox, addToCartBtn);
        return productBox;
    }
}
