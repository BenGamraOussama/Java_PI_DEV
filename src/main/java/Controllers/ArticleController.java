package Controllers;

import Entities.Article;
import Services.ArticleService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;

public class ArticleController {

    @FXML
    private GridPane articleContainer;

    @FXML
    private TextField searchField;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private ComboBox<String> sortComboBox;

    private final ArticleService articleService = new ArticleService();

    public void initialize() throws SQLException {
        sortComboBox.getItems().addAll("Titre", "Contenu");
        loadArticleCards();
    }

    @FXML
    void handleAddArticle(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/addArticle.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Ajouter Article");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleSearch(ActionEvent event) {
        String keyword = searchField.getText().trim();
        try {
            List<Article> filteredArticles = articleService.rechercher(keyword);
            displayArticles(filteredArticles);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleSort(ActionEvent event) {
        String sortBy = sortComboBox.getValue();
        try {
            List<Article> articles = articleService.afficher();

            if ("Titre".equals(sortBy)) {
                articles.sort(Comparator.comparing(Article::getTitle, String.CASE_INSENSITIVE_ORDER));
            } else if ("Contenu".equals(sortBy)) {
                articles.sort(Comparator.comparing(Article::getContent, String.CASE_INSENSITIVE_ORDER));
            }

            displayArticles(articles);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadArticleCards() throws SQLException {
        List<Article> articles = articleService.afficher();
        displayArticles(articles);
    }

    private void displayArticles(List<Article> articles) {
        articleContainer.getChildren().clear();
        int column = 0;
        int row = 1;
        int maxCols = 3;

        articleContainer.setHgap(10);
        articleContainer.setVgap(10);
        articleContainer.setPadding(new Insets(10));

        for (Article article : articles) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/articleCard.fxml"));
                VBox card = loader.load();

                ArticleCardController controller = loader.getController();
                controller.setData(article);

                articleContainer.add(card, column, row);

                column++;
                if (column == maxCols) {
                    column = 0;
                    row++;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
