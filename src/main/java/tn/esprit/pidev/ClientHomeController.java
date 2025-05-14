package tn.esprit.pidev;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.event.ActionEvent;
import tn.esprit.pidev.Model.User;

import java.awt.event.MouseEvent;
import java.io.IOException;

public class ClientHomeController {

    @FXML
    private Button contactButton;

    @FXML
    private javafx.scene.layout.HBox logoutContainer;

    @FXML
    private javafx.scene.layout.HBox headerNav;

    @FXML
    private javafx.scene.control.ScrollPane scrollPane;

    @FXML
    public void initialize() {
        // Vérifier si l'utilisateur est connecté et afficher le bouton de déconnexion si c'est le cas
        if (User.connecte != null) {
            logoutContainer.setVisible(true);
        } else {
            logoutContainer.setVisible(false);
        }

        // Make the header responsive
        if (headerNav != null && scrollPane != null) {
            // Ensure the header takes the full width of the window
            headerNav.prefWidthProperty().bind(scrollPane.widthProperty());

            // Apply styles programmatically for better responsiveness
            headerNav.setStyle("-fx-background-color: white; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");

            // Make the header stay at the top when scrolling
            // Store the original header position
            double originalTranslateY = headerNav.getTranslateY();

            // Add a listener to the scrollPane's vvalue property
            scrollPane.vvalueProperty().addListener((observable, oldValue, newValue) -> {
                // If scrolled down, make the header stick to the top
                if (newValue.doubleValue() > 0.02) {
                    // Calculate the position based on scroll amount
                    headerNav.setTranslateY(scrollPane.getVvalue() * scrollPane.getContent().getBoundsInLocal().getHeight() - originalTranslateY);
                    headerNav.setStyle("-fx-background-color: white; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2); -fx-opacity: 0.95;");
                } else {
                    // Reset to original position
                    headerNav.setTranslateY(originalTranslateY);
                    headerNav.setStyle("-fx-background-color: white; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2); -fx-opacity: 1.0;");
                }
            });

            // Add a listener to adjust layout when the scene is available and resized
            headerNav.sceneProperty().addListener((observable, oldScene, newScene) -> {
                if (newScene != null) {
                    // Add listener to scene width changes
                    newScene.widthProperty().addListener((obs, oldWidth, newWidth) -> {
                        adjustHeaderLayout(newWidth.doubleValue());
                    });

                    // Initial adjustment
                    if (newScene.getWidth() > 0) {
                        adjustHeaderLayout(newScene.getWidth());
                    }
                }
            });
        }
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
    public void handleConsultationButtonClick(javafx.scene.input.MouseEvent event) {
        if (User.connecte != null) {
            // Rediriger vers la page de profil
            try {
                Parent root = FXMLLoader.load(getClass().getResource("gestion_rdv/PatientrdvView.fxml"));
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

    @FXML
    public void handleProduitButtonClick(javafx.scene.input.MouseEvent event) {
        if (User.connecte != null) {
            // Rediriger vers la page de profil
            try {
                Parent root = FXMLLoader.load(getClass().getResource("gestion_produit/Front_view.fxml"));
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

    /**
     * Adjusts the header layout based on the window width
     * @param width The current width of the window
     */
    private void adjustHeaderLayout(double width) {
        if (headerNav != null) {
            // For smaller screens
            if (width < 600) {
                // Reduce padding for smaller screens
                headerNav.setStyle("-fx-background-color: white; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2); -fx-padding: 5px 0;");
            } 
            // For medium screens
            else if (width < 900) {
                headerNav.setStyle("-fx-background-color: white; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2); -fx-padding: 8px 0;");
            } 
            // For large screens
            else {
                headerNav.setStyle("-fx-background-color: white; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2); -fx-padding: 10px 0;");
            }
        }
    }

    // Méthode pour gérer le clic sur le bouton de déconnexion
    @FXML
    public void handleLogoutButtonClick(javafx.scene.input.MouseEvent event) {
        // Déconnecter l'utilisateur
        User.connecte = null;

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
