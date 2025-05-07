package tn.esprit.pidev.gestion_ressource.Controllers;

import tn.esprit.pidev.gestion_ressource.Entities.Category;
import tn.esprit.pidev.gestion_ressource.Services.CategoryService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Files;

public class updateCategoryController {

    @FXML
    private Button cancel;

    @FXML
    private TextField categoryNameField;

    @FXML
    private DatePicker datePicker;

    @FXML
    private Label errorLabel;

    @FXML
    private TextField imagePathField;

    @FXML
    private TextArea messageReponse;

    @FXML
    private Button save;

    @FXML
    private Button uploadBtn;

    private Category currentCategory;

    public void setCategoryData(Category category) {
        this.currentCategory = category;
        categoryNameField.setText(category.getName());
        messageReponse.setText(category.getDescription());
        imagePathField.setText(category.getImage());
        datePicker.setValue(category.getCreatedAt());
    }


    @FXML
    void handleCancel(ActionEvent event) {
        Stage stage = (Stage) cancel.getScene().getWindow();
        stage.close();
    }


    @FXML
    void handleImageUpload(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File selectedFile = fileChooser.showOpenDialog(uploadBtn.getScene().getWindow());

        if (selectedFile != null) {
            try {
                // Destination folder inside resources
                File destFolder = new File("src/main/resources/tn/esprit/pidev/uploads/");
                if (!destFolder.exists()) {
                    destFolder.mkdirs();
                }

                // Create a unique filename
                String newFileName = System.currentTimeMillis() + "_" + selectedFile.getName();
                File destFile = new File(destFolder, newFileName);

                // Copy the file
                Files.copy(selectedFile.toPath(), destFile.toPath());

                // Update the text field with the filename only (not the full path)
                imagePathField.setText(newFileName);

                errorLabel.setVisible(false);

            } catch (Exception e) {
                errorLabel.setText("Erreur lors de l'importation de l'image.");
                errorLabel.setVisible(true);
                e.printStackTrace();
            }
        }
    }

    @FXML
    void handleSave(ActionEvent event) {
        try {
            String name = categoryNameField.getText();
            String desc = messageReponse.getText();
            String image = imagePathField.getText();
            java.sql.Date date = java.sql.Date.valueOf(datePicker.getValue());

            if (name.isEmpty() || desc.isEmpty() || image.isEmpty()) {
                errorLabel.setText("Tous les champs sont obligatoires !");
                errorLabel.setVisible(true);
                return;
            }

            currentCategory.setName(name);
            currentCategory.setDescription(desc);
            currentCategory.setImage(image);
            currentCategory.setCreatedAt(date.toLocalDate());

            CategoryService service = new CategoryService();
            service.modifier(currentCategory);

            // Show success alert
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Mise à jour réussie");
            alert.setHeaderText(null);
            alert.setContentText("La catégorie a été mise à jour avec succès !");
            alert.showAndWait();

            // Close the window
            Stage stage = (Stage) save.getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            e.printStackTrace();
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Erreur");
            errorAlert.setHeaderText("Erreur lors de la mise à jour");
            errorAlert.setContentText(e.getMessage());
            errorAlert.showAndWait();
        }
    }



}
