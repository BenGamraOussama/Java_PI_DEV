package tn.esprit.pidev.gestion_ressource.Controllers;

import tn.esprit.pidev.gestion_ressource.Entities.Category;
import tn.esprit.pidev.gestion_ressource.Services.CategoryService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class AddCategoryController {

    @FXML
    private Button cancel;

    @FXML
    private Label errorLabel;

    @FXML
    private TextArea messageReponse;

    @FXML
    private TextField categoryNameField;

    @FXML
    private TextField imagePathField;
    @FXML
    private Label errorMessage2;
    @FXML
    private DatePicker datePicker;

    @FXML
    private Button save;

    private File selectedImageFile;

    private final CategoryService categoryService = new CategoryService();

    @FXML
    void handleCancel(ActionEvent event) {
        // Optional: Close window or clear fields
        categoryNameField.clear();
        messageReponse.clear();
        imagePathField.clear();
        datePicker.setValue(null);
        errorLabel.setVisible(false);
    }

    @FXML
    void handleSave(ActionEvent event) {
        String name = categoryNameField.getText();
        String description = messageReponse.getText();
        LocalDate date = datePicker.getValue();

        if (name.isEmpty() || description.isEmpty() || date == null || selectedImageFile == null) {
            errorLabel.setText("Tous les champs sont obligatoires.");
            errorLabel.setVisible(true);
            return;
        }

        try {
            // Copy image to resources/uploads
            String uploadsDir = "src/main/resources/tn/esprit/pidev/uploads/";
            String destPath = uploadsDir + selectedImageFile.getName();
            Path targetPath = Paths.get(destPath);

            if (!Files.exists(targetPath)) {
                Files.copy(selectedImageFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            }

            // Save category
            Category newCat = new Category();
            newCat.setName(name);
            newCat.setDescription(description);
            newCat.setImage(selectedImageFile.getName());
            newCat.setCreatedAt(Date.valueOf(date).toLocalDate());
            if(!descriptionValide(description)){
                errorMessage2.setText(" la description contient des mots inapropriés");
            }else {
                categoryService.ajouter(newCat);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Categorie ajoutée avec succès!");
                Stage stage = (Stage) save.getScene().getWindow();
                stage.close();

                errorLabel.setVisible(false);
                handleCancel(null);
            }// Reset fields

        } catch (IOException | SQLException e) {
            e.printStackTrace();
            errorLabel.setText("Erreur lors de l'enregistrement.");
            errorLabel.setVisible(true);
        }
    }
    public static boolean descriptionValide(String description) {
        List<String> motsInterdits = null;
        try {
            motsInterdits = Files.readAllLines(Paths.get("src/main/java/utils/motsinap.txt"));
        } catch (IOException e) {
            System.out.println("Erreur lors de la lecture du fichier de mots inappropriés");
            System.out.println(e.getMessage());
        }
        if (motsInterdits == null) {
            return true;
        }

        // Convertir le titre en minuscules pour une comparaison insensible à la casse
        String descriptionMiniscule = description.toLowerCase();

        // Vérifier si le titre contient un mot interdit
        for (String mot : motsInterdits) {
            if (descriptionMiniscule.contains(mot)) {
                return false;
            }
        }

        return true;
    }


    @FXML
    void handleImageUpload(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );

        Stage stage = (Stage) save.getScene().getWindow(); // any control from scene will work
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            selectedImageFile = file;
            imagePathField.setText(file.getName());
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
