package tn.esprit.pidev.gestion_commande.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.IOException;

public class SideNavBarController {

    @FXML
    private HBox InvestBtn;

    @FXML
    private HBox actualitesBtn;

    @FXML
    private ImageView actualitesIcon;

    @FXML
    private Label actualitesText;

    @FXML
    private HBox chartContainer;

    @FXML
    private HBox compteBtn;

    @FXML
    private ImageView comptesIcon;

    @FXML
    private Label comptesText;

    @FXML
    private Pane content_area;

    @FXML
    private HBox creditsBtn;

    @FXML
    private ImageView creditsIcon;

    @FXML
    private Label creditsText;

    @FXML
    private HBox dashboardBtn;

    @FXML
    private ImageView dashboardIcon;

    @FXML
    private Label dashboardText;

    @FXML
    private ImageView investissementsIcon;

    @FXML
    private Label investissementsText;

    @FXML
    private ImageView logo;

    @FXML
    private HBox navBarLogout;

    @FXML
    private Text navFullname;

    @FXML
    private HBox recBtn;

    @FXML
    private Label reclamationText;

    @FXML
    private ImageView reclamationsIcon;

    @FXML
    private HBox sideBarLogout;

    @FXML
    private HBox stagesBtn;

    @FXML
    private ImageView stagesIcon;

    @FXML
    private Label stagesText;

    @FXML
    private HBox usersBtn;

    @FXML
    private ImageView usersIcon;

    @FXML
    private Label usersText;

    @FXML
    private Label panierCount;

    private boolean sideBarVisible = true;

    @FXML
    void openProduitList(MouseEvent event) {
        try {
            // Charger le fichier FXML de listArticleAdmin
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/pidev/FXML/ligneCommande/LigneCommandeView.fxml"));
            Pane listArticleAdminPane = loader.load();

            // Remplacer le contenu de content_area par le contenu de listArticleAdmin
            content_area.getChildren().setAll(listArticleAdminPane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void openCommandeList(MouseEvent event) {
        try {
            // Charger le fichier FXML de listArticleAdmin
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/pidev/FXML/commande/AddCommande.fxml"));
            Pane listRecAdminPane = loader.load();

            // Remplacer le contenu de content_area par le contenu de listArticleAdmin
            content_area.getChildren().setAll(listRecAdminPane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showProduits() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/pidev/FXML/produit/ProduitView.fxml"));
            VBox produitView = loader.load();
            
            // Remplacer le contenu de la scène
            AnchorPane root = (AnchorPane) panierCount.getScene().getRoot();
            root.getChildren().set(1, produitView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showPanier() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/pidev/FXML/panier/PanierView.fxml"));
            VBox panierView = loader.load();
            
            // Remplacer le contenu de la scène
            AnchorPane root = (AnchorPane) panierCount.getScene().getRoot();
            root.getChildren().set(1, panierView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showCommandes() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/pidev/FXML/commande/CommandeView.fxml"));
            VBox commandeView = loader.load();
            
            // Remplacer le contenu de la scène
            AnchorPane root = (AnchorPane) panierCount.getScene().getRoot();
            root.getChildren().set(1, commandeView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updatePanierCount(int count) {
        panierCount.setText(count + " articles");
    }
}