package Controllers;

import Entities.Article;
import Entities.Category;
import Services.ArticleService;
import Services.CategoryService;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class AddArticleController implements Initializable {

    @FXML
    private Button cancel;

    @FXML
    private ComboBox<Category> comboCategory;
    @FXML
    private Button emojiButton;

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
    public static String emojis = "";

    Stage newStage = new Stage();

    public void emojiPopup() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/emojis.fxml"));
        Scene scene = new Scene(root);
        newStage.setScene(scene);
        newStage.show();

        newStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                String currentText = title.getText();
                title.setText(currentText +" "+ emojis);
                emojis = "";
            }
        });
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            List<Category> categories = categoryService.afficher();
            comboCategory.getItems().addAll(categories);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Display category name in ComboBox
        comboCategory.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Category category, boolean empty) {
                super.updateItem(category, empty);
                setText(empty || category == null ? "" : category.getName());
            }
        });
        comboCategory.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Category category, boolean empty) {
                super.updateItem(category, empty);
                setText(empty || category == null ? "" : category.getName());
            }
        });
    }

    @FXML
    void handleCancel(ActionEvent event) {
        title.clear();
        content.clear();
        imagePathField.clear();
        datePicker.setValue(null);
        comboCategory.getSelectionModel().clearSelection();
        errorLabel.setText("");

        // Reset styles
        resetFieldStyle(title);
        resetFieldStyle(content);
        resetFieldStyle(imagePathField);
        resetFieldStyle(datePicker);
        resetFieldStyle(comboCategory);
    }

    @FXML
    void handleMediaUpload(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Media File");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Supported Media", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.mp4", "*.mov", "*.avi", "*.pdf"),
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"),
                new FileChooser.ExtensionFilter("Video Files", "*.mp4", "*.mov", "*.avi"),
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
        );

        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            imagePathField.setText(selectedFile.getAbsolutePath());
        }
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

        // Validate title
        if (articleTitle.isEmpty()) {
            markFieldInvalid(title, "Le titre est requis");
            valid = false;
        } else {
            resetFieldStyle(title);
        }

        // Validate content
        if (articleContent.isEmpty()) {
            markFieldInvalid(content, "Le contenu est requis");
            valid = false;
        } else {
            resetFieldStyle(content);
        }

        // Validate date
        if (publishDate == null) {
            markFieldInvalid(datePicker, "La date est requise");
            valid = false;
        } else {
            resetFieldStyle(datePicker);
        }

        // Validate image
        if (imagePath.isEmpty()) {
            markFieldInvalid(imagePathField, "Le chemin de l'image est requis");
            valid = false;
        } else {
            resetFieldStyle(imagePathField);
        }

        // Validate category
        if (selectedCategory == null) {
            markFieldInvalid(comboCategory, "La catégorie est requise");
            valid = false;
        } else {
            resetFieldStyle(comboCategory);
        }

        if (!valid) return;

        Article article = new Article();
        article.setTitle(articleTitle);
        article.setContent(articleContent);
        article.setPublishedAt(Date.valueOf(publishDate).toLocalDate());
        article.setMediaPath(imagePath);
        article.setCategoryId(selectedCategory.getId());
        article.setUserId(1); // Static user ID

        try {
            articleService.ajouter(article);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("Article ajouté avec succès !");
            alert.showAndWait();

            handleCancel(null); // Clear form

            // Close stage
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();

        } catch (SQLException e) {
            e.printStackTrace();
            errorLabel.setText("Erreur lors de l'ajout !");
        }
    }

    // Helper method: style a field with red border and tooltip
    private void markFieldInvalid(Control control, String message) {
        control.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
        Tooltip tooltip = new Tooltip(message);
        Tooltip.install(control, tooltip);

        if (control instanceof TextField) {
            ((TextField) control).setPromptText(message + " *");
        } else if (control instanceof TextArea) {
            ((TextArea) control).setPromptText(message + " *");
        } else if (control instanceof ComboBox) {
            ((ComboBox<?>) control).setPromptText(message + " *");
        } else if (control instanceof DatePicker) {
            ((DatePicker) control).setPromptText(message + " *");
        }
    }


    // Helper method: reset style
    private void resetFieldStyle(Control control) {
        control.setStyle(null);
        Tooltip.uninstall(control, null);

        if (control instanceof TextField) {
            ((TextField) control).setPromptText("Titre"); // adjust per field
        } else if (control instanceof TextArea) {
            ((TextArea) control).setPromptText("Entrez la Description");
        } else if (control instanceof ComboBox) {
            ((ComboBox<?>) control).setPromptText("Choisir Categorie");
        } else if (control instanceof DatePicker) {
            ((DatePicker) control).setPromptText("Date Creation");
        }
    }

}
