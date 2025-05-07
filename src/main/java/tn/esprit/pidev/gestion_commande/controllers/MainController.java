package tn.esprit.pidev.gestion_commande.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainController {
    @FXML
    private AnchorPane mainContent;
    @FXML
    private VBox produitView;

    private static final Logger LOGGER = Logger.getLogger(MainController.class.getName());

    @FXML
    public void initialize() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/pidev/FXML/produit/ProduitView.fxml"));
            VBox produitsView = loader.load();
            ProduitController produitController = loader.getController();
            produitController.setMainController(this); // Set MainController reference
            produitView.getChildren().add(produitsView);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading products view", e);
        }
    }

    public void setContent(VBox content) {
        if (mainContent == null) {
            LOGGER.severe("mainContent is null in setContent!");
            return;
        }
        mainContent.getChildren().setAll(content);
        AnchorPane.setTopAnchor(content, 0.0);
        AnchorPane.setBottomAnchor(content, 0.0);
        AnchorPane.setLeftAnchor(content, 0.0);
        AnchorPane.setRightAnchor(content, 0.0);
    }

    public void handleVoirCommandes(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/pidev/FXML/commande/ListViewCommande.fxml"));
            Parent root = loader.load(); // ✅ No cast to VBox

            Scene scene = new Scene(root);
            javafx.stage.Stage stage = (javafx.stage.Stage) mainContent.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading MainView.fxml", e);
        }
    }
}