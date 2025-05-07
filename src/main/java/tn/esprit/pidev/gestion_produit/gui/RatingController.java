package tn.esprit.pidev.gestion_produit.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import tn.esprit.pidev.gestion_produit.entities.Produit;
import tn.esprit.pidev.gestion_produit.services.RatingDAO;
import tn.esprit.pidev.gestion_produit.entities.Rating;

import java.io.InputStream;
import java.sql.SQLException;

public class RatingController {
    @FXML private HBox starsContainer;
    private Rating rating = new Rating();
    private int produitId;
    private Image emptyStar;
    private Image filledStar;

    @FXML
    public void initialize() {
        // Chargement des images
        emptyStar = new Image(getClass().getResourceAsStream("/image/logo.png"));

        // Après (avec vérification)
        InputStream emptyStream = getClass().getResourceAsStream("/image/logo.png");
        if (emptyStream == null) {
            throw new RuntimeException("Image empty_star.png non trouvée!");
        }
        emptyStar = new Image(emptyStream);

        InputStream filledStream = getClass().getResourceAsStream("/image/logo.png");
        if (filledStream == null) {
            throw new RuntimeException("Image filled_star.png non trouvée!");
        }
        filledStar = new Image(filledStream);

        // Création des 5 étoiles
        for (int i = 1; i <= 5; i++) {
            ImageView star = new ImageView(emptyStar);
            star.setUserData(i); // Stocke la valeur de l'étoile

            int finalI1 = i;
            star.setOnMouseEntered(e -> highlightStars(finalI1));
            star.setOnMouseExited(e -> resetStars());
            int finalI = i;
            star.setOnMouseClicked(e -> {
                rating.setNote(finalI);
                highlightStars(finalI);
            });

            starsContainer.getChildren().add(star);
        }
    }

    private void highlightStars(int upTo) {
        starsContainer.getChildren().forEach(node -> {
            ImageView star = (ImageView) node;
            star.setImage((int) star.getUserData() <= upTo ? filledStar : emptyStar);
        });
    }

    private void resetStars() {
        if (rating.getNote() == 0) {
            starsContainer.getChildren().forEach(node -> ((ImageView) node).setImage(emptyStar));
        } else {
            highlightStars(rating.getNote());
        }
    }

    @FXML
    private void submitRating() {
        try {
            rating.setProduitId(produitId);
            new RatingDAO().save(rating);
            showAlert("Succès", "Note enregistrée !");
        } catch (SQLException e) {
            showAlert("Erreur", "Échec de l'enregistrement: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Setter pour le produit
    public void setProduitId(int produitId) {
        this.produitId = produitId;
    }
}