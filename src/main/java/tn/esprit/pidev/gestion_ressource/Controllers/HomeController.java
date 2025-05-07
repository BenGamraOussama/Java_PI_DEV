package tn.esprit.pidev.gestion_ressource.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class HomeController implements Initializable {

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    private AnchorPane contentPane; // This is where we will load reclamationList.fxml

    private void loadScene(ActionEvent event, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestion Reclamations");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace(); // For debugging
        }
    }


    @FXML
    void GoToArticles(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/articleList.fxml"));
            Parent articleView = loader.load();
            contentPane.getChildren().clear();
            contentPane.getChildren().add(articleView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void GoToCategories(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/categoryList.fxml"));
            Parent categoryView = loader.load();
            contentPane.getChildren().clear();
            contentPane.getChildren().add(categoryView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
