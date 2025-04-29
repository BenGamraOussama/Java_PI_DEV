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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.Optional;

public class ArticleCardController {

    CategoryService categoryService = new CategoryService(); // or inject it properly
    private final ArticleService articleService = new ArticleService();

    @FXML
    private Label categoryLabel;

    @FXML
    private Label content;

    @FXML
    private Button delete;

    @FXML
    private ImageView media;

    @FXML
    private Label publishedAt;

    @FXML
    private Label title;

    @FXML
    private Button update;

    @FXML
    private Label user;

    private  Article article;

    @FXML
    void handledelete(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText(null);
        alert.setContentText("Êtes-vous sûr de vouloir supprimer cet article ?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                articleService.supprimer(article.getId()); // Delete article from the database

                // Refresh the view or close the current stage (as article is deleted)
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/articleList.fxml"));
                Stage stage = (Stage) delete.getScene().getWindow();
                stage.setScene(new Scene(loader.load()));
                stage.show();

            } catch (Exception e) {
                e.printStackTrace();
                showError("Erreur lors de la suppression de l'article !");
            }
        }
    }

    @FXML
    void handleupdate(ActionEvent event) {
        try {
            // Load the UpdateArticleController and pass the article data
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/updateArticle.fxml"));
            Parent root = loader.load();
            // Pass the current article to the UpdateArticleController
            UpdateArticleController updateController = loader.getController();
            updateController.setArticle(article);

            Stage stage = new Stage();
            stage.setTitle("Modifier Article");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur lors du chargement du formulaire de mise à jour !");
        }
    }

    public void setData(Article article) {
        this.article = article; // Save article object for later use

        title.setText(article.getTitle());

        try {
            Category category = categoryService.getById(article.getCategoryId());
            if (category != null) {
                categoryLabel.setText(category.getName());
            } else {
                categoryLabel.setText("Unknown Category");
            }
        } catch (SQLException e) {
            categoryLabel.setText("Error loading category");
            e.printStackTrace();
        }

        content.setText(article.getContent());
        publishedAt.setText("Published: " + article.getPublishedAt().toString());

        Image img = new Image("file:" + article.getMediaPath(), true);
        media.setImage(img);
    }


    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


}
