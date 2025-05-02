package Controllers;

import Entities.Article;
import Entities.Category;
import Services.ArticleService;
import Services.CategoryService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

public class ArticleCardController {

    private final CategoryService categoryService = new CategoryService();
    private Article article;

    @FXML private ImageView media;
    @FXML private Label title;
    @FXML private Label categoryLabel;
    @FXML private Label content;
    @FXML private Label publishedAt;
    @FXML private Button open;
    @FXML private Button update;
    @FXML private Button delete;

    @FXML
    void handleOpen(ActionEvent event) {
        openArticleDetail();
    }

    @FXML
    void handleUpdate(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/updateArticle.fxml"));
            Parent root = loader.load();

            UpdateArticleController controller = loader.getController();
            controller.setArticle(article);

            Stage stage = new Stage();
            stage.setTitle("Update Article: " + article.getTitle());
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert("Error", "Failed to open update form", Alert.AlertType.ERROR);
        }
    }

    @FXML
    void handleDelete(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Article");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to delete '" + article.getTitle() + "'?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                new ArticleService().supprimer(article.getId());
                // Close the card after deletion
                ((Stage) delete.getScene().getWindow()).close();
            } catch (SQLException e) {
                showAlert("Error", "Failed to delete article", Alert.AlertType.ERROR);
            }
        }
    }

    private void openArticleDetail() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/articleDetail.fxml"));
            Parent root = loader.load();

            ArticleDetailController controller = loader.getController();
            controller.setArticle(article);

            Stage stage = new Stage();
            stage.setTitle(article.getTitle() + " - Details");
            stage.setScene(new Scene(root));

            // Set minimum size
            stage.setMinWidth(600);
            stage.setMinHeight(500);

            stage.show();
        } catch (IOException e) {
            showAlert("Error", "Failed to open article details", Alert.AlertType.ERROR);
        }
    }

    public void setData(Article article) {
        this.article = article;
        title.setText(article.getTitle());
        content.setText(article.getContent());
        publishedAt.setText("Published: " + article.getPublishedAt().toString());

        try {
            Image image = new Image("file:" + article.getMediaPath());
            media.setImage(image);
        } catch (Exception e) {
            media.setImage(new Image(getClass().getResourceAsStream("/images/default-article.png")));
        }

        try {
            Category category = categoryService.getById(article.getCategoryId());
            categoryLabel.setText(category != null ? category.getName() : "Uncategorized");
        } catch (SQLException e) {
            categoryLabel.setText("Category Error");
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}