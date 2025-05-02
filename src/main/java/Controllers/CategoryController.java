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
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;

public class CategoryController {

    @FXML
    private TextField searchField;
    @FXML
    private GridPane categoryContainer;
    @FXML
    private Button backToHome;

    private final CategoryService categoryService = new CategoryService();

    @FXML
    public void initialize() throws SQLException {
        loadCategoryCards();
    }

    private void loadCategoryCards() throws SQLException {
        List<Category> categories = categoryService.afficher();
        displayCategories(categories);
    }

    private void displayCategories(List<Category> categories) {
        categoryContainer.getChildren().clear();

        int column = 0;
        int row = 1;
        int maxCols = 5;

        categoryContainer.setHgap(10);
        categoryContainer.setVgap(10);
        categoryContainer.setPadding(new Insets(10));

        for (Category category : categories) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/categoryCard.fxml"));
                VBox card = loader.load();

                CategoryCardController controller = loader.getController();
                controller.setData(category);

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
        String keyword = searchField.getText().trim();

        try {
            List<Category> filteredCategories = categoryService.rechercher(keyword);
            displayCategories(filteredCategories);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleSortByName(ActionEvent event) {
        try {
            List<Category> sorted = categoryService.afficher();
            sorted.sort(Comparator.comparing(Category::getName));
            displayCategories(sorted);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleSortByDescription(ActionEvent event) {
        try {
            List<Category> sorted = categoryService.afficher();
            sorted.sort(Comparator.comparing(Category::getDescription));
            displayCategories(sorted);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleBackToHome() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/home.fxml"));
        Stage stage = (Stage) categoryContainer.getScene().getWindow();
        stage.setScene(new Scene(loader.load()));
        stage.show();
    }
}
