package pii.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class MainFX extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // -------------------- Fenêtre 1 : Ajout Catégories --------------------
        Parent rootCategories = FXMLLoader.load(
                Objects.requireNonNull(getClass().getResource("/AjouterCategories.fxml"))
        );
        Scene categoryScene = new Scene(rootCategories, 800, 600);
        primaryStage.setTitle("Gestion des Catégories");
        primaryStage.setScene(categoryScene);
        primaryStage.show();

        // -------------------- Fenêtre 2 : Ajout Produits --------------------
        Stage productStage = new Stage();
        Parent rootProducts = FXMLLoader.load(
                Objects.requireNonNull(getClass().getResource("/AjouterProduit.fxml"))
        );
        Scene productScene = new Scene(rootProducts, 800, 600);
        productStage.setTitle("Ajout de Produit");
        productStage.setScene(productScene);
        productStage.show();

        // -------------------- Fenêtre 3 : Liste Produits --------------------
        Stage listeProduitsStage = new Stage();
        Parent rootListeProduits = FXMLLoader.load(
                Objects.requireNonNull(getClass().getResource("/ListeProduits.fxml"))
        );
        Scene listeProduitsScene = new Scene(rootListeProduits, 800, 600);
        listeProduitsStage.setTitle("Liste des Produits");
        listeProduitsStage.setScene(listeProduitsScene);
        listeProduitsStage.show();

        // -------------------- Fenêtre 4 : Liste Catégories --------------------
        Stage listeCategoriesStage = new Stage();
        Parent rootListeCategories = FXMLLoader.load(
                Objects.requireNonNull(getClass().getResource("/ListeCategories.fxml"))
        );
        Scene listeCategoriesScene = new Scene(rootListeCategories, 800, 600);
        listeCategoriesStage.setTitle("Liste des Catégories");
        listeCategoriesStage.setScene(listeCategoriesScene);
        listeCategoriesStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
