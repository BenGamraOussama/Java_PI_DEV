package Controllers;

import Entities.Article;
import Services.ArticleService;
import Services.CategoryService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class ArticleController  {

    @FXML
    private GridPane articleContainer;

    @FXML
    private TextField searchField;


    @FXML
    private ScrollPane scrollPane;

    private ArticleService articleService = new ArticleService();

    public void initialize() throws SQLException {
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

    }



    private void loadArticleCards() throws SQLException {
        articleContainer.getChildren().clear(); // Clear previous items

        List<Article> articles = articleService.afficher(); // Fetch from your DB

        int column = 0;
        int row = 1;
        int maxCols = 3; // You can adjust the max columns as needed

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
