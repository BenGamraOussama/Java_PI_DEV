package tn.esprit.pidev;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import tn.esprit.pidev.Model.User;
import tn.esprit.pidev.Service.UserDAO;

import java.io.IOException;
import java.util.Arrays;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    @FXML
    private ImageView myImageView;

    @FXML
    private ImageView BgImageView;

    private UserDAO userDAO = new UserDAO();



    @FXML
    private void handleLogin(ActionEvent event) {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        // Basic validation
        if (email.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please fill in all fields");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        User authenticatedUser = userDAO.authenticateUser(email, password);

        if (authenticatedUser != null) {
            messageLabel.setText("Login successful!");
            messageLabel.setStyle("-fx-text-fill: green;");

            // Navigate to home page based on role
            try {
                String fxmlFile;
                User user = userDAO.authenticateUser(email, password);
                String role = String.join(", ", user.getRole()); // Convertir en minuscules pour éviter les problèmes de casse

                switch (role) {
                    case "psychiatre":
                        fxmlFile = "PsychiatreDashboard.fxml";
                        break;
                    case "fournisseur":
                        fxmlFile = "FournisseurDashboard.fxml";
                        break;
                    case "admin":
                        fxmlFile = "Dashboard.fxml"; // ou "AdminDashboard.fxml" selon votre convention
                        break;
                    case "patient":
                        fxmlFile = "ClientHome.fxml"; // ou "AdminDashboard.fxml" selon votre convention
                        break;
                    default:
                        // Rôle non reconnu, rediriger vers une page par défaut ou afficher une erreur
                        fxmlFile = "DefaultDashboard.fxml";
                        break;
                }

                Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
                Scene scene = new Scene(root);
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

                stage.setScene(scene);
                stage.show();

            } catch (IOException e) {
                messageLabel.setText("Error loading home page");
                messageLabel.setStyle("-fx-text-fill: red;");
                e.printStackTrace();
            }
        } else {
            messageLabel.setText("Invalid email or password");
            messageLabel.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    private void goToSignup(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("signup.fxml"));
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            messageLabel.setText("Error loading signup page");
            messageLabel.setStyle("-fx-text-fill: red;");
            e.printStackTrace();
        }
    }

    @FXML
    private void forgotPassword(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("ForgotPassword.fxml"));
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            messageLabel.setText("Error loading forgot password page");
            messageLabel.setStyle("-fx-text-fill: red;");
            e.printStackTrace();
        }
    }
}