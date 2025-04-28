package tn.esprit.pidev;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import tn.esprit.pidev.Model.User;
import tn.esprit.pidev.Service.UserDAO;

public class ProfileController {

    @FXML
    private Label idLabel, roleLabel, messageLabel;
    @FXML
    private TextField firstNameField, lastNameField, emailField, phoneField;
    @FXML
    private Button handleSave, handleCancel;

    private UserDAO userDAO;

    @FXML
    public void initialize() {
        userDAO = new UserDAO();

        // Supposons que l'ID de l'utilisateur connecté est 1 (à récupérer dynamiquement)
        int currentUserId = User.connecte.getId();

        // Récupérer les données de l'utilisateur depuis la base de données
        User user = userDAO.getUserById(currentUserId);

        if (User.connecte != null) {
            // Remplir les champs avec les données utilisateur
            idLabel.setText(String.valueOf(user.getId()));
            firstNameField.setText(user.getFirstName());
            lastNameField.setText(user.getLastName());
            emailField.setText(user.getEmail());
            phoneField.setText(user.getPhoneNumber());
            roleLabel.setText(String.join(", ", user.getRole()));

            // Désactiver les boutons initialement
            handleSave.setDisable(true);
            handleCancel.setDisable(true);
        } else {
            System.err.println("Utilisateur non trouvé.");
        }
    }

    @FXML
    private void handleEdit() {
        // Activer la modification
        firstNameField.setEditable(true);
        lastNameField.setEditable(true);
        emailField.setEditable(true);
        phoneField.setEditable(true);

        // Activer les boutons
        handleSave.setDisable(false);
        handleCancel.setDisable(false);
    }

    @FXML
    private void handleSave() {
        // Sauvegarder les modifications (mise à jour dans la base de données ici)
        messageLabel.setText("Modification enregistrée.");

        // Désactiver la modification
        firstNameField.setEditable(false);
        lastNameField.setEditable(false);
        emailField.setEditable(false);
        phoneField.setEditable(false);

        // Désactiver les boutons
        handleSave.setDisable(true);
        handleCancel.setDisable(true);
    }

    @FXML
    private void handleCancel() {
        // Réinitialiser les champs aux valeurs d'origine
        int currentUserId = User.connecte.getId(); // Supposons que l'ID est toujours 1
        User user = userDAO.getUserById(currentUserId);

        if (User.connecte != null) {
            firstNameField.setText(user.getFirstName());
            lastNameField.setText(user.getLastName());
            emailField.setText(user.getEmail());
            phoneField.setText(user.getPhoneNumber());
        }

        // Désactiver la modification
        firstNameField.setEditable(false);
        lastNameField.setEditable(false);
        emailField.setEditable(false);
        phoneField.setEditable(false);

        // Désactiver les boutons
        handleSave.setDisable(true);
        handleCancel.setDisable(true);
    }
}