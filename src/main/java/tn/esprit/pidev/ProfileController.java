package tn.esprit.pidev;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tn.esprit.pidev.Model.User;
import tn.esprit.pidev.Service.UserDAO;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.UUID;

public class ProfileController implements Initializable {

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField phoneNumberField;

    @FXML
    private TextField addressField;

    @FXML
    private TextField specialiteField;

    @FXML
    private Label specialiteLabel;

    @FXML
    private Label memberSinceLabel;

    @FXML
    private PasswordField currentPasswordField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private ProgressBar passwordStrengthBar;

    @FXML
    private Label passwordStrengthLabel;

    @FXML
    private Button changePasswordButton;

    @FXML
    private Button changePhotoButton;

    @FXML
    private Button goBackButton;

    @FXML
    private ImageView profileImageView;

    @FXML
    private Circle profileImageCircle;

    @FXML
    private Label errorLabel;

    @FXML
    private Label successLabel;

    @FXML
    private Button twoFactorButton;

    private UserDAO userDAO;
    private String tempImagePath;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        userDAO = new UserDAO();
        // Set up password strength evaluation
        newPasswordField.textProperty().addListener((observable, oldValue, newValue) -> {
            evaluatePasswordStrength(newValue);
        });

        // Clear any status messages
        errorLabel.setText("");
        successLabel.setText("");

        // Set member since date

        memberSinceLabel.setText("Member since: May 2025");

        loadUserProfile();
    }

    private void loadUserProfile() {
        if (User.connecte != null) {
            // Load user information into fields
            firstNameField.setText(User.connecte.getFirstName());
            lastNameField.setText(User.connecte.getLastName());
            emailField.setText(User.connecte.getEmail());
            phoneNumberField.setText(User.connecte.getPhoneNumber());
            addressField.setText(User.connecte.getAddress());

            // Set area of interest if available

            // Update 2FA button based on current status
            if (User.connecte.isTwoFactorEnabled()) {
                twoFactorButton.setText("Disable Two-Factor Authentication");
                twoFactorButton.setStyle("-fx-background-color: #f44336; -fx-background-radius: 22;");
            } else {
                twoFactorButton.setText("Set Up Two-Factor Authentication");
                twoFactorButton.setStyle("-fx-background-color: #4CAF50; -fx-background-radius: 22;");
            }

            // Show or hide specialite field based on user role
        }
    }

    @FXML
    private void handleUpdateProfile(ActionEvent event) {
        try {
            // Validate fields
            if (firstNameField.getText().trim().isEmpty() ||
                    lastNameField.getText().trim().isEmpty() ||
                    emailField.getText().trim().isEmpty()) {
                showError("First name, last name, and email are required fields.");
                return;
            }

            // Email validation
            if (!isValidEmail(emailField.getText().trim())) {
                showError("Please enter a valid email address.");
                return;
            }

            // Update user object
            User.connecte.setFirstName(firstNameField.getText().trim());
            User.connecte.setLastName(lastNameField.getText().trim());
            User.connecte.setEmail(emailField.getText().trim());
            User.connecte.setPhoneNumber(phoneNumberField.getText().trim());
            User.connecte.setAddress(addressField.getText().trim());
            User.connecte.setSpecialite(specialiteField.getText().trim());

            // Update user in database
            boolean updated = userDAO.updateUser(User.connecte);

            if (updated) {
                showSuccess("Profile updated successfully!");
            } else {
                showError("Failed to update profile. Please try again.");
            }
        } catch (Exception e) {
            showError("Error updating profile: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleChangePassword(ActionEvent event) {
        try {
            // Clear previous messages
            errorLabel.setText("");
            successLabel.setText("");

            // Validate inputs
            String currentPassword = currentPasswordField.getText();
            String newPassword = newPasswordField.getText();
            String confirmPassword = confirmPasswordField.getText();

            if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                showError("All password fields are required.");
                return;
            }

            // Verify current password
            User currentUser = userDAO.authenticateUser(User.connecte.getEmail(), currentPasswordField.getText());
            if (currentUser == null) {
                showError("Current password is incorrect");
                return;
            }

            // Check if new passwords match
            if (!newPassword.equals(confirmPassword)) {
                showError("New passwords do not match.");
                return;
            }

            // Check password strength
            int strength = evaluatePasswordStrength(newPassword);
            if (strength < 2) {
                showError("Password is too weak. Please choose a stronger password.");
                return;
            }

            // Update password
            boolean updated = userDAO.updatePassword(User.connecte.getEmail(), newPassword);

            if (updated) {
                showSuccess("Password changed successfully!");
                currentPasswordField.clear();
                newPasswordField.clear();
                confirmPasswordField.clear();
                passwordStrengthBar.setProgress(0);
                passwordStrengthLabel.setText("Password strength: Weak");
                passwordStrengthLabel.setStyle("-fx-text-fill: #e74c3c;");
            } else {
                showError("Failed to change password. Please try again.");
            }
        } catch (Exception e) {
            showError("Error changing password: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleChangePhoto(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Profile Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            try {
                // Create a temporary preview of the image
                Image image = new Image(selectedFile.toURI().toString());
                profileImageView.setImage(image);

                // Apply clip to make the image circular
                profileImageView.setClip(profileImageCircle);

                // Store the temporary image path
                tempImagePath = selectedFile.getAbsolutePath();

                // Show success message
                showSuccess("Photo selected. Click 'Update Profile' to save changes.");
            } catch (Exception e) {
                showError("Error loading image: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private String saveProfileImagePermanently(String tempPath) throws IOException {
        // Create directory if it doesn't exist
        Path uploadDir = Paths.get("src/main/resources/uploads/profiles");
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        // Generate unique filename
        String fileName = UUID.randomUUID().toString() + getFileExtension(tempPath);
        Path destination = uploadDir.resolve(fileName);

        // Copy file to destination
        Files.copy(Paths.get(tempPath), destination, StandardCopyOption.REPLACE_EXISTING);

        return destination.toString();
    }

    private String getFileExtension(String path) {
        int lastDotIndex = path.lastIndexOf(".");
        if (lastDotIndex > 0) {
            return path.substring(lastDotIndex);
        }
        return "";
    }

    private int evaluatePasswordStrength(String password) {
        int strength = 0;

        // Length check
        if (password.length() >= 8) strength++;

        // Contains uppercase letter
        if (password.matches(".*[A-Z].*")) strength++;

        // Contains lowercase letter
        if (password.matches(".*[a-z].*")) strength++;

        // Contains number
        if (password.matches(".*\\d.*")) strength++;

        // Contains special character
        if (password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) strength++;

        // Update progress bar and label
        double progress = strength / 5.0;
        passwordStrengthBar.setProgress(progress);

        if (strength <= 1) {
            passwordStrengthBar.setStyle("-fx-accent: #e74c3c;"); // Red
            passwordStrengthLabel.setText("Password strength: Weak");
            passwordStrengthLabel.setStyle("-fx-text-fill: #e74c3c;");
        } else if (strength <= 3) {
            passwordStrengthBar.setStyle("-fx-accent: #f39c12;"); // Orange
            passwordStrengthLabel.setText("Password strength: Moderate");
            passwordStrengthLabel.setStyle("-fx-text-fill: #f39c12;");
        } else {
            passwordStrengthBar.setStyle("-fx-accent: #2ecc71;"); // Green
            passwordStrengthLabel.setText("Password strength: Strong");
            passwordStrengthLabel.setStyle("-fx-text-fill: #2ecc71;");
        }

        return strength;
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }

    private void showError(String message) {
        errorLabel.setText(message);
        successLabel.setText("");

        // Clear error after 5 seconds
        new Thread(() -> {
            try {
                Thread.sleep(5000);
                javafx.application.Platform.runLater(() -> errorLabel.setText(""));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void showSuccess(String message) {
        successLabel.setText(message);
        errorLabel.setText("");

        // Clear success after 5 seconds
        new Thread(() -> {
            try {
                Thread.sleep(5000);
                javafx.application.Platform.runLater(() -> successLabel.setText(""));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
    @FXML
    private void handleTwoFactorSetup(ActionEvent event) {
        if (User.connecte.isTwoFactorEnabled()) {
            // Disable 2FA
            if (userDAO.disableTwoFactor(User.connecte.getId())) {
                User.connecte.setTwoFactorEnabled(false);
                User.connecte.setTwoFactorSecret(null);

                // Update button
                twoFactorButton.setText("Set Up Two-Factor Authentication");
                twoFactorButton.setStyle("-fx-background-color: #4CAF50; -fx-background-radius: 22;");

                showSuccess("Two-factor authentication has been disabled");
            } else {
                showError("Failed to disable two-factor authentication");
            }
        } else {
            // Enable 2FA - Navigate to setup page
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("TwoFactorSetup.fxml"));
                Parent root = loader.load();

                TwoFactorSetupController controller = loader.getController();
                controller.initData(User.connecte);

                Scene scene = new Scene(root);
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

                stage.setScene(scene);
                stage.show();

            } catch (IOException e) {
                showError("Error loading two-factor setup page: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleGoBack(ActionEvent event) {
        try {
            String fxmlPath;
            String title;
            // Determine the destination based on user role
            if (User.connecte.getRole().equals("[\"ROLE_PATIENT\"]")) {
                fxmlPath = "/tn/esprit/pidev/ClientHome.fxml";
                title = "HopeNest / Client Home";
            } else if (User.connecte.getRole().equals("[\"ROLE_ADMIN\"]")) {
                fxmlPath = "/tn/esprit/pidev/Dashboard.fxml";
                title = "HopeNest / Dashboard";
            } else {
                // Default case (optional)
                fxmlPath = "/tn/esprit/pidev/ClientHome.fxml";
                title = "HopeNest";
            }

            // Load the appropriate view
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Scene scene = new Scene(root);

            // Get the current stage
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Set the new scene
            stage.setScene(scene);
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            showError("Error navigating back: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
