package tn.esprit.pidev.gestion_commande.tests;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainFX extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Chargement de l'interface principale avec la barre de navigation latérale
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/SideNavBar.fxml"));

        // Autres interfaces disponibles (commentées pour référence)
        // FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Front/PasserCommande.fxml"));
        // FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/commande/ListViewCommande.fxml"));

        Parent root = loader.load();
        Scene scene = new Scene(root);

        // Configuration de la fenêtre principale
        primaryStage.setTitle("Application de Gestion");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}