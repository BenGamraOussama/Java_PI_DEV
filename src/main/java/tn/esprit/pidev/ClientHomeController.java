package tn.esprit.pidev;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import tn.esprit.pidev.Model.User;

import java.awt.event.MouseEvent;
import java.io.IOException;

public class ClientHomeController {

    @FXML
    private Button contactButton;

    @FXML
    public void initialize() {
        // Initialisation si nécessaire
    }

    // Méthode pour gérer le clic sur le bouton profil
    @FXML
    public void handleProfileButtonClick(javafx.scene.input.MouseEvent event) {
        if (User.connecte != null) {
            // Rediriger vers la page de profil
            try {
                Parent root = FXMLLoader.load(getClass().getResource("profile.fxml"));
                Scene scene = new Scene(root);
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                // Gérer l'erreur (peut-être afficher un message à l'utilisateur)
            }
        } else {
            // Rediriger vers la page de login
            try {
                Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
                Scene scene = new Scene(root);
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                // Gérer l'erreur
            }
        }
    }

    @FXML
    public void handleAvtiviteButtonClick(javafx.scene.input.MouseEvent event) {
        if (User.connecte != null) {
            // Rediriger vers la page de profil
            try {
                Parent root = FXMLLoader.load(getClass().getResource("liste_exercices.fxml"));
                Scene scene = new Scene(root);
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                // Gérer l'erreur (peut-être afficher un message à l'utilisateur)
            }
        } else {
            // Rediriger vers la page de login
            try {
                Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
                Scene scene = new Scene(root);
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                // Gérer l'erreur
            }
        }
    }

    // Méthode pour vérifier si l'utilisateur est connecté

    // Méthode pour naviguer vers la page de profil
    private void navigateToProfile(MouseEvent event) {

    }

    // Méthode pour naviguer vers la page de login
    private void navigateToLogin(MouseEvent event) {

    }


}