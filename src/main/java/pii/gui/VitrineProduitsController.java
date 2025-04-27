package pii.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;
import javafx.scene.image.ImageView;
import pii.entities.Produit;
import pii.services.PanierServices;
import pii.services.ProduitServices;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class VitrineProduitsController {

    @FXML
    private VBox produitsContainer;  // Conteneur pour les produits
    @FXML
    private ImageView logoImage; // Image pour le logo

    private final ProduitServices produitService = new ProduitServices();
    private final PanierServices panierServices = new PanierServices(); // Service pour gérer le panier

    private int utilisateurId = 1; // ID de l'utilisateur (modifiez cela selon votre logique)

    @FXML
    public void initialize() {
        try {
            List<Produit> produits = produitService.getAll();  // Récupérer tous les produits

            // Vider le conteneur avant d'ajouter les produits
            produitsContainer.getChildren().clear();

            for (Produit produit : produits) {
                // Créer une carte produit et l'ajouter au conteneur
                Node produitCard = createProduitCard(produit);
                produitsContainer.getChildren().add(produitCard);
            }
        } catch (SQLException | IOException e) {
            handleError("Erreur de chargement des produits", e);
        }
    }

    private Node createProduitCard(Produit produit) throws IOException {
        // Charger un fichier FXML pour la carte produit
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ProduitCard.fxml"));
        Node produitCard = loader.load();

        // Récupérer le contrôleur de la carte produit et lui passer le produit
        ProduitCardController controller = loader.getController();
        controller.setProduit(produit);
        controller.setPanierServices(panierServices);
        controller.setUtilisateurId(utilisateurId);

        return produitCard;
    }

    private void handleError(String message, Throwable e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message + ": " + e.getMessage());
        alert.showAndWait();
    }

    public void handleOpenPanier() {
        try {
            // Charger la vue du panier
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Panier.fxml"));
            Node panierView = loader.load();

            // Récupérer le contrôleur du panier et passer les produits
            PanierController panierController = loader.getController();
            panierController.setPanierServices(panierServices);
            panierController.setUtilisateurId(utilisateurId);

            // Afficher la vue du panier
            produitsContainer.getChildren().clear();
            produitsContainer.getChildren().add(panierView);
        } catch (IOException e) {
            handleError("Erreur lors de l'ouverture du panier", e);
        }
    }
}
