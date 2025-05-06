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
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import tn.esprit.pidev.Model.User;
import tn.esprit.pidev.Service.QRCodeGenerator;
import tn.esprit.pidev.Service.TwoFactorAuth;
import tn.esprit.pidev.Service.UserDAO;

import java.io.IOException;

/**
 * Controller for the two-factor authentication setup screen.
 * This screen allows users to enable 2FA for their account.
 */
public class TwoFactorSetupController {

    @FXML
    private ImageView qrCodeImageView;

    @FXML
    private Label secretKeyLabel;

    @FXML
    private TextField verificationCodeField;

    @FXML
    private Label messageLabel;

    @FXML
    private Button verifyButton;

    @FXML
    private Button cancelButton;

    private User user;
    private String secretKey;
    private UserDAO userDAO = new UserDAO();

    /**
     * Initialize the controller with the current user.
     * This method must be called after loading the FXML.
     *
     * @param user The current user
     */
    public void initData(User user) {
        this.user = user;

        // Generate a new secret key
        this.secretKey = TwoFactorAuth.generateSecretKey();

        // Display the secret key in a formatted way for easier reading and manual entry
        secretKeyLabel.setText(TwoFactorAuth.formatSecretKey(secretKey, 4));

        // Generate the QR code URL with a more meaningful issuer name
        String qrCodeUrl = TwoFactorAuth.getQRCodeURL("HopeNest", user.getEmail(), secretKey);

        // Generate and display the QR code
        qrCodeImageView.setImage(QRCodeGenerator.generateQRCode(qrCodeUrl, 200));

        // Log the QR code URL for debugging
        System.out.println("QR Code URL: " + qrCodeUrl);
    }

    /**
     * Handle the verify button click.
     * Verifies the 2FA code and enables 2FA if successful.
     *
     * @param event The action event
     */
    @FXML
    private void handleVerify(ActionEvent event) {
        String code = verificationCodeField.getText().trim();

        if (code.isEmpty()) {
            messageLabel.setText("Please enter the verification code");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        if (TwoFactorAuth.validateCode(secretKey, code)) {
            // Enregistrement du secret en base APRES validation correcte
            boolean success = userDAO.saveTwoFactorSecret(user.getId(), secretKey);
            if (success) {
                user.setTwoFactorSecret(secretKey);
                user.setTwoFactorEnabled(true);
                messageLabel.setText("Two-factor authentication enabled successfully!");
                messageLabel.setStyle("-fx-text-fill: green;");
                verifyButton.setDisable(true);
            } else {
                messageLabel.setText("Erreur lors de l'enregistrement du secret 2FA");
                messageLabel.setStyle("-fx-text-fill: red;");
            }
        } else {
            messageLabel.setText("Invalid verification code");
            messageLabel.setStyle("-fx-text-fill: red;");
        }
    }

    /**
     * Handle the cancel button click.
     * Returns to the profile screen.
     *
     * @param event The action event
     */
    @FXML
    private void handleCancel(ActionEvent event) {
        try {
            // Set the static connected user before navigating back
            User.connecte = user;

            Parent root = FXMLLoader.load(getClass().getResource("Dashboard.fxml"));
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            messageLabel.setText("Error loading profile page");
            messageLabel.setStyle("-fx-text-fill: red;");
            e.printStackTrace();
        }
    }
    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            User.connecte = null; // Réinitialisation de l'utilisateur connecté

            // Chargement de la vue de login
            Parent root = FXMLLoader.load(getClass().getResource("/tn/esprit/pidev/Login.fxml"));
            Scene scene = new Scene(root);

            // Obtention de la fenêtre actuelle
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Changement de scène
            stage.setScene(scene);
            stage.setTitle("HopeNest / Login");
            stage.show();
        } catch (IOException e) {
            System.err.println("Erreur lors de la déconnexion: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
