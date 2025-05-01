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
import java.util.List;
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
    @FXML private ComboBox<String> filterRoleCombo;
    @FXML private TextField searchField;
    @FXML private Button prevPageButton;
    @FXML private Button nextPageButton;
    @FXML private Label pageInfoLabel;

    // Pagination variables
    private int currentPage = 1;
    private final int ITEMS_PER_PAGE = 5;
    private int totalPages = 1;
    private List<User> filteredUsers;

    private ObservableList<User> usersList = FXCollections.observableArrayList();
    private UserDAO userDAO = new UserDAO();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize ComboBoxes
        genderCombo.setItems(FXCollections.observableArrayList("Homme", "Femme"));
        roleCombo.setItems(FXCollections.observableArrayList("admin", "psychiatre", "fournisseur", "patient"));
        filterRoleCombo.setItems(FXCollections.observableArrayList("Tous", "admin", "psychiatre", "fournisseur", "patient"));
        filterRoleCombo.setValue("Tous");

        // Initialize pagination
        currentPage = 1;
        prevPageButton.setDisable(true); // Initially disabled as we start at page 1

        // Add listeners for search and filter
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            currentPage = 1; // Reset to first page when search changes
            filterUsers();
        });

        filterRoleCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            currentPage = 1; // Reset to first page when filter changes
            filterUsers();
        });

        loadUsers();
    }
    public void loadUsers() {
        usersCardsContainer.getChildren().clear();
        usersList.clear();
        usersList.addAll(userDAO.getAllUsers());

        // Reset pagination to first page
        currentPage = 1;

        filterUsers();
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

    // ... (autres méthodes restent les mêmes jusqu'à filterUsers)

    private void filterUsers() {
        usersCardsContainer.getChildren().clear();

        String searchTerm = searchField.getText().toLowerCase();
        String selectedRole = filterRoleCombo.getValue();

        // Filter users based on search term and role
        filteredUsers = usersList.stream()
                .filter(user -> matchesSearch(user, searchTerm))
                .filter(user -> matchesRole(user, selectedRole))
                .collect(Collectors.toList());

        // Calculate total pages
        totalPages = (int) Math.ceil((double) filteredUsers.size() / ITEMS_PER_PAGE);
        if (totalPages == 0) totalPages = 1;

        // Ensure current page is valid
        if (currentPage > totalPages) {
            currentPage = totalPages;
        }

        // Update pagination controls
        updatePaginationControls();

        // Display current page
        displayCurrentPage();
    }

    /**
     * Displays the current page of user cards
     */
    private void displayCurrentPage() {
        usersCardsContainer.getChildren().clear();

        int startIndex = (currentPage - 1) * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, filteredUsers.size());

        for (int i = startIndex; i < endIndex; i++) {
            usersCardsContainer.getChildren().add(createUserCard(filteredUsers.get(i)));
        }
    }

    /**
     * Updates the pagination controls (button states and page info)
     */
    private void updatePaginationControls() {
        // Update page info label
        pageInfoLabel.setText("Page " + currentPage + " / " + totalPages);

        // Enable/disable navigation buttons
        prevPageButton.setDisable(currentPage <= 1);
        nextPageButton.setDisable(currentPage >= totalPages);
    }

    /**
     * Handles the previous page button click
     */
    @FXML
    private void handlePreviousPage(ActionEvent event) {
        if (currentPage > 1) {
            currentPage--;
            displayCurrentPage();
            updatePaginationControls();
        }
    }

    /**
     * Handles the next page button click
     */
    @FXML
    private void handleNextPage(ActionEvent event) {
        if (currentPage < totalPages) {
            currentPage++;
            displayCurrentPage();
            updatePaginationControls();
        }
    }

    private boolean matchesSearch(User user, String searchTerm) {
        if (searchTerm == null || searchTerm.isEmpty()) return true;

        String searchLower = searchTerm.toLowerCase();
        return (user.getFirstName() != null && user.getFirstName().toLowerCase().contains(searchLower)) ||
                (user.getLastName() != null && user.getLastName().toLowerCase().contains(searchLower)) ||
                (user.getEmail() != null && user.getEmail().toLowerCase().contains(searchLower)) ||
                (user.getPhoneNumber() != null && user.getPhoneNumber().toLowerCase().contains(searchLower)) ||
                String.valueOf(user.getId()).contains(searchTerm);
    }

    private boolean matchesRole(User user, String selectedRole) {
        if (selectedRole == null || "Tous".equals(selectedRole)) return true;

        if (user.getRole() == null) return false;

        // Vérifie si l'un des rôles de l'utilisateur correspond au rôle sélectionné
        return Arrays.stream(user.getRole())
                .anyMatch(role -> role != null &&
                        role.replace("[\"ROLE_", "").replace("\"]", "").equalsIgnoreCase(selectedRole));
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

        // Status label for banned users
        Label statusLabel = new Label(user.isBanned() ? "Statut: Banni" : "Statut: Actif");
        statusLabel.setStyle(user.isBanned() ? 
                "-fx-text-fill: #f44336; -fx-font-weight: bold;" : 
                "-fx-text-fill: #4CAF50; -fx-font-weight: bold;");

        // Action buttons
        HBox buttonsBox = new HBox(5);
        Button editBtn = new Button("Modifier");
        editBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        editBtn.setOnAction(e -> fillFormWithUser(user));

        Button deleteBtn = new Button("Supprimer");
        deleteBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        deleteBtn.setOnAction(e -> deleteUser(user));

        // Ban/Unban button
        Button banBtn = new Button(user.isBanned() ? "Débloquer" : "Bloquer");
        banBtn.setStyle(user.isBanned() ? 
                "-fx-background-color: #2196F3; -fx-text-fill: white;" : 
                "-fx-background-color: #FF9800; -fx-text-fill: white;");
        banBtn.setOnAction(e -> toggleBanStatus(user));

        buttonsBox.getChildren().addAll(editBtn, deleteBtn, banBtn);
        card.getChildren().addAll(nameLabel, emailLabel, roleLabel, phoneLabel, statusLabel, buttonsBox);
        return card;
    }

    /**
     * Toggle the ban status of a user
     * @param user The user to toggle ban status for
     */
    private void toggleBanStatus(User user) {
        boolean success;
        if (user.isBanned()) {
            // Unban the user
            success = userDAO.unbanUser(user.getId());
            if (success) {
                user.setBanned(false);
                showMessage("Utilisateur débloqué avec succès", "green");
            } else {
                showMessage("Erreur lors du déblocage de l'utilisateur", "red");
            }
        } else {
            // Ban the user
            success = userDAO.banUser(user.getId());
            if (success) {
                user.setBanned(true);
                showMessage("Utilisateur bloqué avec succès", "green");
            } else {
                showMessage("Erreur lors du blocage de l'utilisateur", "red");
            }
        }

        if (success) {
            // Refresh the user card
            refreshUserCard(user);
        }
    }
    private void refreshUserCard(User user) {
        // Find the user in the main list and update it
        int index = usersList.indexOf(user);
        if (index >= 0) {
            // Update the user in the main list
            User updatedUser = userDAO.getUserById(user.getId());
            usersList.set(index, updatedUser);

            // Also update in filtered list if present
            int filteredIndex = filteredUsers.indexOf(user);
            if (filteredIndex >= 0) {
                filteredUsers.set(filteredIndex, updatedUser);
            }

            // Refresh the current page display
            displayCurrentPage();
        }
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

            // Save current page
            int savedPage = currentPage;

            // Reload users
            usersList.clear();
            usersList.addAll(userDAO.getAllUsers());

            // Apply filters
            filterUsers();

            // Try to restore the previous page if it's valid
            if (savedPage <= totalPages) {
                currentPage = savedPage;
                displayCurrentPage();
                updatePaginationControls();
            }
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

            // Valider et formater le numéro de téléphone
            String formattedPhoneNumber = formatPhoneNumber(user.getPhoneNumber());
            if (formattedPhoneNumber == null) {
                showMessage("Numéro de téléphone invalide. Format attendu: +216XXXXXXXX ou 2XXXXXXXX", "red");
                return;
            }

            if (userDAO.addUser(user)) {
                TwilioSmsSender.sendPasswordBySms(formattedPhoneNumber, generatedPassword);
                showMessage("Utilisateur ajouté. Mot de passe envoyé par SMS.", "green");

                // After adding a user, we typically want to show the first page
                // to see the newly added user, so we reset to page 1
                currentPage = 1;
                usersList.clear();
                usersList.addAll(userDAO.getAllUsers());
                filterUsers();

                clearForm();
            } else {
                showMessage("Erreur lors de l'ajout de l'utilisateur", "red");
            }
        }
    }

    public static String formatPhoneNumber(String phoneNumber) {
        if (phoneNumber == null) return null;

        // Supprimer tous les caractères non numériques
        String digitsOnly = phoneNumber.replaceAll("[^0-9]", "");

        // Si le numéro commence par 2 (pour la Tunisie) et a 8 chiffres, ajouter +216
        if (digitsOnly.startsWith("2") && digitsOnly.length() == 8) {
            return "+216" + digitsOnly;
        }
        // Si le numéro commence par 216 et a 10 ou 11 chiffres, ajouter +
        else if (digitsOnly.startsWith("216") && (digitsOnly.length() == 10 || digitsOnly.length() == 11)) {
            return "+" + digitsOnly;
        }
        // Si le numéro est déjà au format international (+216), le garder tel quel
        else if (phoneNumber.startsWith("+216") && phoneNumber.length() == 12) {
            return phoneNumber;
        }

        return null; // Format non reconnu
    }

    @FXML
    private void handleUpdate(ActionEvent event) {
        if (validateForm() && !idField.getText().isEmpty()) {
            User user = createUserFromForm();
            user.setId(Integer.parseInt(idField.getText()));

            if (userDAO.updateUser(user)) {
                showMessage("Utilisateur modifié avec succès", "green");

                // Save current page
                int savedPage = currentPage;

                // Reload users
                usersList.clear();
                usersList.addAll(userDAO.getAllUsers());

                // Apply filters
                filterUsers();

                // Try to restore the previous page if it's valid
                if (savedPage <= totalPages) {
                    currentPage = savedPage;
                    displayCurrentPage();
                    updatePaginationControls();
                }
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

                // Save current page
                int savedPage = currentPage;

                // Reload users
                usersList.clear();
                usersList.addAll(userDAO.getAllUsers());

                // Apply filters
                filterUsers();

                // Try to restore the previous page if it's valid
                if (savedPage <= totalPages) {
                    currentPage = savedPage;
                    displayCurrentPage();
                    updatePaginationControls();
                }

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
        // Save current page
        int savedPage = currentPage;

        // Reload users
        usersCardsContainer.getChildren().clear();
        usersList.clear();
        usersList.addAll(userDAO.getAllUsers());

        // Apply filters but keep the same page if possible
        filterUsers();

        // Try to restore the previous page if it's valid
        if (savedPage <= totalPages) {
            currentPage = savedPage;
            displayCurrentPage();
            updatePaginationControls();
        }
    }
}
