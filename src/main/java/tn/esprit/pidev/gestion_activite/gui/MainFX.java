package tn.esprit.pidev.gestion_activite.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainFX extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load main interface
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainInterface.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);
        primaryStage.setTitle("PII Application");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true); // Start maximized
        primaryStage.show();

        // Load chatbot interface
        FXMLLoader chatbotLoader = new FXMLLoader(getClass().getResource("/chatbot.fxml"));
        Parent chatbotRoot = chatbotLoader.load();
        Stage chatbotStage = new Stage();
        Scene chatbotScene = new Scene(chatbotRoot);
        chatbotStage.setTitle("Chatbot");
        chatbotStage.setScene(chatbotScene);
        chatbotStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
