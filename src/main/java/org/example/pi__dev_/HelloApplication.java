package org.example.pi__dev_;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.pi__dev_.dao.DatabaseConnection;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("NovaTeam!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        DatabaseConnection.testConnection();
        launch();

    }
}