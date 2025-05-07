package tn.esprit.pidev.gestion_produit.gui;

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
                Objects.requireNonNull(getClass().getResource("/tn/esprit/pidev/gestion_produit/AjouterCategories.fxml"))
        );
        Scene categoryScene = new Scene(rootCategories, 800, 600);
        primaryStage.setTitle("Gestion des Catégories");
        primaryStage.setScene(categoryScene);
        primaryStage.show();

        // -------------------- Fenêtre 2 : Ajout Produits --------------------
        Stage productStage = new Stage();
        Parent rootProducts = FXMLLoader.load(
                Objects.requireNonNull(getClass().getResource("/tn/esprit/pidev/gestion_produit/AjouterProduit.fxml"))
        );
        Scene productScene = new Scene(rootProducts, 800, 600);
        productStage.setTitle("Ajout de Produit");
        productStage.setScene(productScene);
        productStage.show();

        // -------------------- Fenêtre 3 : Liste Produits --------------------
        Stage listeProduitsStage = new Stage();
        Parent rootListeProduits = FXMLLoader.load(
                Objects.requireNonNull(getClass().getResource("/tn/esprit/pidev/gestion_produit/ListeProduits.fxml"))
        );
        Scene listeProduitsScene = new Scene(rootListeProduits, 800, 600);
        listeProduitsStage.setTitle("Liste des Produits");
        listeProduitsStage.setScene(listeProduitsScene);
        listeProduitsStage.show();

        // -------------------- Fenêtre 4 : Liste Catégories --------------------
        Stage listeCategoriesStage = new Stage();
        Parent rootListeCategories = FXMLLoader.load(
                Objects.requireNonNull(getClass().getResource("/tn/esprit/pidev/gestion_produit/ListeCategories.fxml"))
        );
        Scene listeCategoriesScene = new Scene(rootListeCategories, 800, 600);
        listeCategoriesStage.setTitle("Liste des Catégories");
        listeCategoriesStage.setScene(listeCategoriesScene);
        listeCategoriesStage.show();

        // -------------------- Fenêtre 5 : Modifier Produit --------------------
        Stage modifierProduitStage = new Stage();
        Parent rootModifierProduit = FXMLLoader.load(
                Objects.requireNonNull(getClass().getResource("/tn/esprit/pidev/gestion_produit/ModifierProduit.fxml"))
        );
        Scene modifierProduitScene = new Scene(rootModifierProduit, 800, 600);
        modifierProduitStage.setTitle("Modifier Produit");
        modifierProduitStage.setScene(modifierProduitScene);
        modifierProduitStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
