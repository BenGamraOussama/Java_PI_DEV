package org.example.pi__dev_;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import java.io.IOException;

public class MainController {

    @FXML
    private TabPane mainTabPane;

    // Initialisation si nécessaire
    public void initialize() {
        // Configuration supplémentaire peut être ajoutée ici
    }

    @FXML
    private BorderPane mainPane; // Maintenant correctement injecté grâce au fx:id

    @FXML
    private void showConsultationView() {
        loadView("/org/example/pi__dev_/ConsultationView.fxml");
    }

    @FXML
    private void showRendezVousView() {
        loadView("/org/example/pi__dev_/RendezVousView.fxml");
    }

    @FXML
    private void showTraitementView() {
        loadView("/org/example/pi__dev_/TraitementView.fxml");
    }

    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            mainPane.setCenter(view);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la vue: " + fxmlPath);
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}