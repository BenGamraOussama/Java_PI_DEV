package Controllers;

import Entities.Article;
import Entities.Category;
import Services.ArticleService;
import Services.CategoryService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Files;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;

public class UpdateArticleController {

    @FXML
    private Button cancel;

    @FXML
    private ComboBox<Category> comboCategory;

    @FXML
    private TextArea content;

    @FXML
    private DatePicker datePicker;

    @FXML
    private Label errorLabel;

    @FXML
    private TextField imagePathField;

    @FXML
    private Button save;

    @FXML
    private TextField title;
    @FXML
    private Button uploadBtn;
    private final CategoryService categoryService = new CategoryService();
    private final ArticleService articleService = new ArticleService();

    private Article article;

    @FXML
    void handleCancel(ActionEvent event) {
        // Close the update window
        ((Stage) cancel.getScene().getWindow()).close();
    }

    @FXML
    void handleSave(ActionEvent event) {
        boolean valid = true;
        errorLabel.setText("");

        String articleTitle = title.getText().trim();
        String articleContent = content.getText().trim();
        LocalDate publishDate = datePicker.getValue();
        String imagePath = imagePathField.getText().trim();
        Category selectedCategory = comboCategory.getValue();

        // Validate input
        if (articleTitle.isEmpty()) {
            valid = false;
            errorLabel.setText("Le titre est requis.");
        } else if (articleContent.isEmpty()) {
            valid = false;
            errorLabel.setText("Le contenu est requis.");
        } else if (publishDate == null) {
            valid = false;
            errorLabel.setText("La date est requise.");
        } else if (imagePath.isEmpty()) {
            valid = false;
            errorLabel.setText("Le chemin de l'image est requis.");
        } else if (selectedCategory == null) {
            valid = false;
            errorLabel.setText("La catégorie est requise.");
        }

        if (!valid) {
            return;
        }

        // Set updated values
        article.setTitle(articleTitle);
        article.setContent(articleContent);
        article.setPublishedAt(Date.valueOf(publishDate).toLocalDate());
        article.setMediaPath(imagePath);
        article.setCategoryId(selectedCategory.getId());

        try {
            articleService.modifier(article);

            // ✅ Show success alert
            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Succès");
            successAlert.setHeaderText(null);
            successAlert.setContentText("Article mis à jour avec succès !");
            successAlert.showAndWait();

            // ✅ Reload the article list
            Stage stage = (Stage) save.getScene().getWindow();
            stage.close();


        } catch (SQLException e) {
            errorLabel.setText("Erreur lors de la mise à jour de l'article !");
            e.printStackTrace();
        } catch (Exception e) {
            errorLabel.setText("Erreur lors du chargement de la liste !");
            e.printStackTrace();
        }
    }


    public void setArticle(Article article) {
        this.article = article;
        title.setText(article.getTitle());
        content.setText(article.getContent());
        imagePathField.setText(article.getMediaPath());

        datePicker.setValue(article.getPublishedAt());

        try {
            // Populate the category dropdown
            comboCategory.getItems().addAll(categoryService.afficher());
            comboCategory.setValue(categoryService.getById(article.getCategoryId()));

        } catch (SQLException e) {
            e.printStackTrace();
        }
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
                File destFolder = new File("src/main/resources/uploads/");
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
}
