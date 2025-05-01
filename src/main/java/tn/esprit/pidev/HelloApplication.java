package tn.esprit.pidev;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("ClientHome.fxml"));

        // Appliquer la feuille de style CSS
        primaryStage.setTitle("HopeNest - Dashboard");
        primaryStage.show();
        primaryStage.setScene(new Scene(root, 800, 600));
    }

    public static void main(String[] args) {
        launch();
    }
}