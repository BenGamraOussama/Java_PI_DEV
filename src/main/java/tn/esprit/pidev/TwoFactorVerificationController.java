package tn.esprit.pidev;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.esprit.pidev.Model.User;
import tn.esprit.pidev.Service.UserDAO;

import java.io.IOException;

/**
 * Controller for the two-factor authentication verification screen.
 * This screen is shown after a successful password authentication if 2FA is enabled.
 */
public class TwoFactorVerificationController {

    @FXML
    private TextField codeField;

    @FXML
    private Label messageLabel;

    @FXML
    private Button verifyButton;

    @FXML
    private Button cancelButton;

    private User user;
    private UserDAO userDAO = new UserDAO();

    /**
     * Initialize the controller with the authenticated user.
     * This method must be called after loading the FXML.
     *
     * @param user The authenticated user
     */
    public void initData(User user) {
        this.user = user;
    }

    /**
     * Handle the verify button click.
     * Verifies the 2FA code and redirects to the appropriate dashboard if successful.
     *
     * @param event The action event
     */
    @FXML
    private void handleVerify(ActionEvent event) {
        String code = codeField.getText().trim();

        if (code.isEmpty()) {
            messageLabel.setText("Please enter the verification code");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        if (userDAO.verifyTwoFactorCode(user.getId(), code)) {
            messageLabel.setText("Verification successful!");
            messageLabel.setStyle("-fx-text-fill: green;");

            // Set the static connected user
            User.connecte = user;

            // Navigate to home page based on role
            try {
                String fxmlFile;
                // Get the first role if available, otherwise use an empty string
                String role = (user.getRole() != null && user.getRole().length > 0) ? user.getRole()[0] : "";

                System.out.println("Original role: " + role);

                // Remove any JSON formatting if present
                role = role.replace("[", "").replace("]", "").replace("\"", "");

                System.out.println("Processed role: " + role);

                if (role.isEmpty()) {
                    System.out.println("Warning: User has no role assigned, using default dashboard");
                }

                switch (role) {
                    case "ROLE_PSYCHIATRE":
                        fxmlFile = "PsychiatreDashboard.fxml";
                        break;
                    case "ROLE_FOURNISSEUR":
                        fxmlFile = "FournisseurDashboard.fxml";
                        break;
                    case "ROLE_ADMIN":
                        fxmlFile = "Dashboard.fxml";
                        break;
                    case "ROLE_PATIENT":
                        fxmlFile = "ClientHome.fxml";
                        break;
                    default:
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
            messageLabel.setText("Invalid verification code");
            messageLabel.setStyle("-fx-text-fill: red;");
        }
    }

    /**
     * Handle the cancel button click.
     * Returns to the login screen.
     *
     * @param event The action event
     */
    @FXML
    private void handleCancel(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            messageLabel.setText("Error loading login page");
            messageLabel.setStyle("-fx-text-fill: red;");
            e.printStackTrace();
        }
    }
}
