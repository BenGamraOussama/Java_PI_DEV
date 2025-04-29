package tn.esprit.pidev;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import tn.esprit.pidev.Model.User;
import tn.esprit.pidev.Service.UserDAO;

import java.net.URL;
import java.util.Arrays;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class UserManagementController implements Initializable {

    @FXML private TextField idField;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private ComboBox<String> genderCombo, roleCombo;
    @FXML private TextField phoneField, emailField;
    @FXML private FlowPane usersCardsContainer;
    @FXML private Label messageLabel;

    private ObservableList<User> usersList = FXCollections.observableArrayList();
    private UserDAO userDAO = new UserDAO();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize ComboBoxes
        genderCombo.setItems(FXCollections.observableArrayList("Homme", "Femme"));
        roleCombo.setItems(FXCollections.observableArrayList("admin", "psychiatre", "fournisseur", "patient"));

        // Initial users load
        loadUsers();
    }

    private String formatRoles(String[] roles) {
        if (roles == null || roles.length == 0) {
            return "Aucun rôle";
        }

        return Arrays.stream(roles)
                .filter(Objects::nonNull)
                .map(role -> role.replace("[\"ROLE_", "").replace("\"]", ""))
                .map(role -> role.substring(0, 1).toUpperCase() + role.substring(1).toLowerCase())
                .collect(Collectors.joining(", "));
    }

    public void loadUsers() {
        usersCardsContainer.getChildren().clear();
        usersList.clear();
        usersList.addAll(userDAO.getAllUsers());
        for (User user : usersList) {
            usersCardsContainer.getChildren().add(createUserCard(user));
        }
    }

    private VBox createUserCard(User user) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-border-color: #e0e0e0; -fx-border-radius: 5; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 0);");
        card.setPrefWidth(250);

        // User information
        Label nameLabel = new Label(user.getFirstName() + " " + user.getLastName());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        Label emailLabel = new Label("Email: " + user.getEmail());
        Label roleLabel = new Label("Rôle: " + formatRoles(user.getRole()));
        Label phoneLabel = new Label("Tél: " + user.getPhoneNumber());

        // Action buttons
        HBox buttonsBox = new HBox(5);
        Button editBtn = new Button("Modifier");
        editBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        editBtn.setOnAction(e -> fillFormWithUser(user));

        Button deleteBtn = new Button("Supprimer");
        deleteBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        deleteBtn.setOnAction(e -> deleteUser(user));

        buttonsBox.getChildren().addAll(editBtn, deleteBtn);
        card.getChildren().addAll(nameLabel, emailLabel, roleLabel, phoneLabel, buttonsBox);
        return card;
    }

    private void fillFormWithUser(User user) {
        idField.setText(String.valueOf(user.getId()));
        firstNameField.setText(user.getFirstName());
        lastNameField.setText(user.getLastName());
        phoneField.setText(user.getPhoneNumber());
        emailField.setText(user.getEmail());

        // Set gender if available


        // Set role in combo box
        if (user.getRole() != null && user.getRole().length > 0) {
            String role = user.getRole()[0];
            String roleName = role.replace("[\"ROLE_", "").replace("\"]", "").toLowerCase();
            roleCombo.setValue(roleName);
        }
    }

    private void deleteUser(User user) {
        if (userDAO.deleteUser(user.getId())) {
            showMessage("Utilisateur supprimé avec succès", "green");
            loadUsers();
        } else {
            showMessage("Erreur lors de la suppression", "red");
        }
    }

    @FXML
    private void handleAdd(ActionEvent event) {
        if (validateForm()) {
            User user = createUserFromForm();
            String generatedPassword = generateRandomPassword();
            user.setPassword(generatedPassword);

            if (userDAO.addUser(user)) {
                EmailService.sendPasswordByEmail(user.getEmail(), generatedPassword);
                showMessage("Utilisateur ajouté. Mot de passe envoyé par email.", "green");
                loadUsers();
                clearForm();
            } else {
                showMessage("Erreur lors de l'ajout de l'utilisateur", "red");
            }
        }
    }

    @FXML
    private void handleUpdate(ActionEvent event) {
        if (validateForm() && !idField.getText().isEmpty()) {
            User user = createUserFromForm();
            user.setId(Integer.parseInt(idField.getText()));

            if (userDAO.updateUser(user)) {
                showMessage("Utilisateur modifié avec succès", "green");
                loadUsers();
            } else {
                showMessage("Erreur lors de la modification", "red");
            }
        }
    }

    @FXML
    private void handleDelete(ActionEvent event) {
        if (!idField.getText().isEmpty()) {
            int id = Integer.parseInt(idField.getText());
            if (userDAO.deleteUser(id)) {
                showMessage("Utilisateur supprimé avec succès", "green");
                loadUsers();
                clearForm();
            } else {
                showMessage("Erreur lors de la suppression", "red");
            }
        }
    }

    @FXML
    private void handleClear(ActionEvent event) {
        clearForm();
    }

    private User createUserFromForm() {
        User user = new User();
        user.setFirstName(firstNameField.getText());
        user.setLastName(lastNameField.getText());
        user.setPhoneNumber(phoneField.getText());
        user.setEmail(emailField.getText());

        // Set gender if selected

        // Set role if selected
        if (roleCombo.getValue() != null) {
            String selectedRole = roleCombo.getValue();
            String formattedRole = "[\"ROLE_" + selectedRole.toUpperCase() + "\"]";
            user.setRole(new String[]{formattedRole});
        }

        return user;
    }

    private void clearForm() {
        idField.clear();
        firstNameField.clear();
        lastNameField.clear();
        genderCombo.getSelectionModel().clearSelection();
        phoneField.clear();
        roleCombo.getSelectionModel().clearSelection();
        emailField.clear();
    }

    private boolean validateForm() {
        if (firstNameField.getText().isEmpty() || lastNameField.getText().isEmpty() ||
                emailField.getText().isEmpty() || roleCombo.getValue() == null) {
            showMessage("Veuillez remplir tous les champs obligatoires", "red");
            return false;
        }

        // Validate email format
        if (!emailField.getText().matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            showMessage("Format d'email invalide", "red");
            return false;
        }

        return true;
    }

    private String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            int index = (int) (Math.random() * chars.length());
            sb.append(chars.charAt(index));
        }
        return sb.toString();
    }

    private void showMessage(String message, String color) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: " + color + ";");
    }

    @FXML
    private void handleRefresh(ActionEvent actionEvent) {
        loadUsers();
    }
}