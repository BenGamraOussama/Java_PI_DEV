package Controllers;

import Entities.Category;
import Services.CategoryService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;

public class CategoryController {

    @FXML
    private GridPane categoryContainer;
    @FXML
    private Button backToHome;
    private final CategoryService categoryService = new CategoryService(); // your service to fetch categories

    @FXML
    public void initialize() throws SQLException {
        loadCategoryCards();
    }

    private void loadCategoryCards() throws SQLException {
        categoryContainer.getChildren().clear(); // Clear previous items

        List<Category> categories = categoryService.afficher(); // Fetch from your DB

        int column = 0;
        int row = 1;
        int maxCols = 5 ;
        categoryContainer.getChildren().clear();

        categoryContainer.setHgap(10);
        categoryContainer.setVgap(10);
        categoryContainer.setPadding(new Insets(10));

        for (int i = 0; i < categories.size(); i++) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/categoryCard.fxml"));
                Node card = loader.load();

                // Optional: send data to the controller
                CategoryCardController controller = loader.getController();
                controller.setData(categories.get(i));

                // Add card to GridPane
                categoryContainer.add(card, column, row);



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
    @FXML
    void handleAddCat(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/addCategory.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Ajouter Catégorie");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    void handleSearch(ActionEvent event) {

    }
    @FXML
    void handleBackToHome() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/home.fxml"));
        Stage stage = (Stage) categoryContainer.getScene().getWindow();
        stage.setScene(new Scene(loader.load()));
        stage.show();
    }
}
