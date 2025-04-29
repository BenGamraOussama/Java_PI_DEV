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
    private PasswordField currentPasswordField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Button changePasswordButton;

    @FXML
    private Button uploadPhotoButton;

    @FXML
    private Button goBackButton;

    @FXML
    private Label errorLabel;

    @FXML
    private Label successLabel;

    private UserDAO userDAO;
    private String tempImagePath;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        userDAO = new UserDAO();
        loadUserProfile();



        // Clear any status messages
        errorLabel.setText("");
        successLabel.setText("");
    }

    private void loadUserProfile() {
        if (User.connecte != null) {
            // Load user information into fields
            firstNameField.setText(User.connecte.getFirstName());
            lastNameField.setText(User.connecte.getLastName());
            emailField.setText(User.connecte.getEmail());
            phoneNumberField.setText(User.connecte.getPhoneNumber());
            addressField.setText(User.connecte.getAddress());

            // Set specialite field if user is a doctor


            // Make email field non-editable (should not be changed)
            emailField.setEditable(false);

        }
    }

    @FXML
    private void handleUpdateProfile(ActionEvent event) {
        try {
            // Validate input fields
            if (firstNameField.getText().isEmpty() || lastNameField.getText().isEmpty() ||
                    phoneNumberField.getText().isEmpty() || addressField.getText().isEmpty()) {
                showError("All fields are required");
                return;
            }

            // Update user object with new values
            User updatedUser = new User();
            updatedUser.setId(User.connecte.getId());
            updatedUser.setFirstName(firstNameField.getText());
            updatedUser.setLastName(lastNameField.getText());
            updatedUser.setAddress(addressField.getText());
            updatedUser.setPhoneNumber(phoneNumberField.getText());



            // Update profile in database
            boolean success = userDAO.updateProfile(updatedUser);

            if (success) {

                // Update the current user object with new values
                User.connecte.setFirstName(updatedUser.getFirstName());
                User.connecte.setLastName(updatedUser.getLastName());
                User.connecte.setAddress(updatedUser.getAddress());
                User.connecte.setPhoneNumber(updatedUser.getPhoneNumber());
                User.connecte.setSpecialite(updatedUser.getSpecialite());

                showSuccess("Profile updated successfully");
            } else {
                showError("Failed to update profile");
            }
        } catch (Exception e) {
            showError("Error updating profile: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleChangePassword(ActionEvent event) {
        try {
            // Validate password fields
            if (currentPasswordField.getText().isEmpty() ||
                    newPasswordField.getText().isEmpty() ||
                    confirmPasswordField.getText().isEmpty()) {
                showError("All password fields are required");
                return;
            }

            // Check if new password and confirm password match
            if (!newPasswordField.getText().equals(confirmPasswordField.getText())) {
                showError("New password and confirm password do not match");
                return;
            }

            // Validate current password (You'd need to add this method to UserDAO)
            User currentUser = userDAO.authenticateUser(User.connecte.getEmail(), currentPasswordField.getText());
            if (currentUser == null) {
                showError("Current password is incorrect");
                return;
            }

            // Update password in database
            boolean success = userDAO.updatePassword(User.connecte.getEmail(), newPasswordField.getText());

            if (success) {
                showSuccess("Password changed successfully");
                // Clear password fields
                currentPasswordField.clear();
                newPasswordField.clear();
                confirmPasswordField.clear();
            } else {
                showError("Failed to change password");
            }
        } catch (Exception e) {
            showError("Error changing password: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        successLabel.setText("");
    }

    private void showSuccess(String message) {
        successLabel.setText(message);
        errorLabel.setText("");
    }
}