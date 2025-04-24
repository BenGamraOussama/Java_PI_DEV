package com.exemple.tests;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
public class MainFX extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Front/PasserCommande.fxml"));
        // FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/commande/ListViewCommande.fxml"));

        Parent root = loader.load();
        Scene scene = new Scene(root);
        primaryStage.setTitle("fares");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}