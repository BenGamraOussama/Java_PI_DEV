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

        // Check if user is banned
        if (userDAO.isUserBanned(email)) {
            messageLabel.setText("Votre compte a été bloqué. Veuillez contacter l'administrateur.");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        User authenticatedUser = userDAO.authenticateUser(email, password);

        if (authenticatedUser != null) {
            messageLabel.setText("Login successful!");
            messageLabel.setStyle("-fx-text-fill: green;");

            // Check if 2FA is enabled for this user
            if (authenticatedUser.isTwoFactorEnabled()) {
                // Redirect to 2FA verification screen
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("TwoFactorVerification.fxml"));
                    Parent root = loader.load();

                    TwoFactorVerificationController controller = loader.getController();
                    controller.initData(authenticatedUser);

                    Scene scene = new Scene(root);
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

                    stage.setScene(scene);
                    stage.show();

                } catch (IOException e) {
                    messageLabel.setText("Error loading 2FA verification page");
                    messageLabel.setStyle("-fx-text-fill: red;");
                    e.printStackTrace();
                }
            } else {
                // Navigate to home page based on role
                try {
                    String fxmlFile;
                    // Get the first role if available, otherwise use an empty string
                    String role = (authenticatedUser.getRole() != null && authenticatedUser.getRole().length > 0) ? authenticatedUser.getRole()[0] : "";

                    System.out.println("Original role: " + role);

                    // Remove any JSON formatting if present
                    role = role.replace("[", "").replace("]", "").replace("\"", "");

                    System.out.println("Processed role: " + role);

                    if (role.isEmpty()) {
                        System.out.println("Warning: User has no role assigned, using default dashboard");
                    }

                    switch (role) {
                        case "ROLE_PSYCHIATRE":
                            fxmlFile = "Dashboard.fxml";
                            break;
                        case "ROLE_FOURNISSEUR":
                            fxmlFile = "Dashboard.fxml";
                            break;
                        case "ROLE_ADMIN":
                            fxmlFile = "Dashboard.fxml";
                            break;
                        case "ROLE_PATIENT":
                            fxmlFile = "ClientHome.fxml";
                            break;
                        default:
                            // Rôle non reconnu, rediriger vers une page par défaut ou afficher une erreur
                            fxmlFile = "DefaultDashboard.fxml";
                            break;
                    }

                    // Set the static connected user
                    User.connecte = authenticatedUser;

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
