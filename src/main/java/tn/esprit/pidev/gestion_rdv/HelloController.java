package tn.esprit.pidev.gestion_rdv;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HelloController implements Initializable {
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    private Label welcomeText;







    @FXML
    private Button helloButton; // Ajouté pour référence



    @FXML
    protected void navigateToMain() {
        try {
            // Méthode plus robuste pour obtenir la scène actuelle
            Stage stage = (Stage) (helloButton != null ? helloButton.getScene().getWindow()
                    : welcomeText != null ? welcomeText.getScene().getWindow()
                    : null);

            if (stage == null) {
                throw new RuntimeException("Impossible de trouver la fenêtre actuelle");
            }

            // Charger Main.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Main.fxml"));
            Parent root = loader.load();

            // Configurer la nouvelle scène
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Erreur de navigation", "Impossible de charger Main.fxml");
        } catch (RuntimeException e) {
            e.printStackTrace();
            showErrorAlert("Erreur", e.getMessage());
        }
    }


    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


}