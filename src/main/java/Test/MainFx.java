package Test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainFx extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        // Load the Article FXML file
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/home.fxml"));
        Parent root = fxmlLoader.load();

        // Set up the scene with the article management interface
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Article Management");
        stage.setWidth(1300);
        stage.setHeight(700);

        // Show the stage
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
