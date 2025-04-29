package Controllers;

import Entities.Category;
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

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

public class CategoryCardController {
    private Category category; // à utiliser dans handledelete

    @FXML
    private Label createdAt;

    @FXML
    private Button delete;

    @FXML
    private Label description;

    @FXML
    private ImageView media;

    @FXML
    private Label name;

    @FXML
    private Button update;

    @FXML
    void handledelete(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText(null);
        alert.setContentText("Êtes-vous sûr de vouloir supprimer cette catégorie ?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                CategoryService service = new CategoryService();
                service.supprimer(category.getId());

                // Refresh the main view (reloading categories)
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/categoryList.fxml"));
                Stage stage = (Stage) delete.getScene().getWindow();
                stage.setScene(new Scene(loader.load()));
                stage.show();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }




    @FXML
    void handleupdate(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/updateCategory.fxml"));
            Parent root = loader.load();

            updateCategoryController controller = loader.getController();
            controller.setCategoryData(category); // Pass current category

            Stage stage = new Stage();
            stage.setTitle("Modifier Catégorie");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setData(Category category) {
        this.category = category;
        name.setText(category.getName());
        description.setText(category.getDescription());
        createdAt.setText("Created at: " + category.getCreatedAt().toString());

        if (category.getImage() != null && !category.getImage().isEmpty()) {
            File file = new File("src/main/resources/uploads/" + category.getImage());
            if (file.exists()) {
                Image img = new Image(file.toURI().toString());
                media.setImage(img);
            } else {
                System.out.println("Image file not found: " + file.getAbsolutePath());
            }
        }

    }

}
