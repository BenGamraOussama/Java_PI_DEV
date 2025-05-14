package tn.esprit.pidev.gestion_ressource.Controllers;

import javafx.scene.Node;
import tn.esprit.pidev.gestion_ressource.Entities.Article;
import tn.esprit.pidev.gestion_ressource.Entities.Category;
import tn.esprit.pidev.gestion_ressource.Services.ArticleService;
import tn.esprit.pidev.gestion_ressource.Services.CategoryService;
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/pidev/FXML/updateArticle.fxml"));
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
        if (article == null) {
            showAlert("Erreur", "Aucun article sélectionné.", Alert.AlertType.WARNING);
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Suppression de l'article");
        alert.setHeaderText("Confirmation de suppression");
        alert.setContentText("Voulez-vous vraiment supprimer l'article : '" + article.getTitle() + "' ?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                ArticleService articleService = new ArticleService();
                articleService.supprimer(article.getId());

                showAlert("Succès", "Article supprimé avec succès.", Alert.AlertType.INFORMATION);
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Erreur", "Impossible de supprimer l'article : " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private void openArticleDetail() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/pidev/FXML/articleDetail.fxml"));
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