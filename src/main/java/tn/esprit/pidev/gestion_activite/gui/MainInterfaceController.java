package tn.esprit.pidev.gestion_activite.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

import java.io.IOException;

public class MainInterfaceController {

    @FXML
    private StackPane contentArea;
    @FXML
    private Button btnActivites;
    @FXML
    private Button btnExercices;

    @FXML
    public void initialize() {
        // Load the default view (Activités)
        showActivites();
    }

    @FXML
    private void showActivites() {
        loadView("/ListeActivites.fxml");
        updateButtonStyles(btnActivites);
    }

    @FXML
    private void showExercices() {
        loadView("/liste_exercices.fxml");
        updateButtonStyles(btnExercices);
    }

    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            e.printStackTrace();
            // You should handle this error appropriately
        }
    }

    private void updateButtonStyles(Button activeButton) {
        // Reset all buttons to default style
        btnActivites.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        btnExercices.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

        // Set active button style
        activeButton.setStyle("-fx-background-color: #0d47a1; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

        // Update effects
        DropShadow activeEffect = new DropShadow(10, Color.rgb(13, 71, 161, 0.5));
        DropShadow inactiveEffect = new DropShadow(5, Color.rgb(13, 71, 161, 0.2));

        btnActivites.setEffect(btnActivites == activeButton ? activeEffect : inactiveEffect);
        btnExercices.setEffect(btnExercices == activeButton ? activeEffect : inactiveEffect);
    }
} 