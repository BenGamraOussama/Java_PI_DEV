package pii;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class MainFront extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // 1. Chargement du FXML (sans modification du chemin)
        Parent root = FXMLLoader.load(getClass().getResource("/VitrineProduits.fxml"));

        // 2. Création de la scène avec style moderne
        Scene scene = new Scene(root);

        // 3. Application du CSS (nouveau fichier sans affecter l'existant)
        scene.getStylesheets().add(getClass().getResource("/modern-style.css").toExternalForm());

        // 4. Configuration de la fenêtre
        primaryStage.setTitle("Pysylo - Boutique en Ligne");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/image/logo.png")));

        // 5. Paramètres responsives
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        // 6. Initialisation des paramètres globaux
        System.setProperty("prism.lcdtext", "false"); // Améliore le rendu texte
        System.setProperty("javafx.animation.pulse", "60"); // Fluidité animations

        launch(args);
    }
}