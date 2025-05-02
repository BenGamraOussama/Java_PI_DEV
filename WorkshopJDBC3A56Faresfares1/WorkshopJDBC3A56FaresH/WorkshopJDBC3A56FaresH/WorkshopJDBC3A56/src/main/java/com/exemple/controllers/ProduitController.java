package com.exemple.controllers;

import com.exemple.entities.Produit;
import com.exemple.services.ProduitService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProduitController {
    @FXML
    private FlowPane produitsContainer;
    @FXML
    private Label panierCount;
    @FXML
    private AnchorPane mainContent;

    private ProduitService produitService;
    private static final Logger LOGGER = Logger.getLogger(ProduitController.class.getName());
    
    // Panier : Map<Produit, Quantité>
    private final Map<Produit, Integer> panier = new HashMap<>();
    private PanierController panierController;

    public ProduitController() {
        produitService = new ProduitService();
    }

    @FXML
    public void initialize() {
        chargerProduits();
        updatePanierCount();
    }

    private void chargerProduits() {
        try {
            produitsContainer.getChildren().clear();
            List<Produit> produits = produitService.afficher();
            for (Produit produit : produits) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/produit/ProduitCard.fxml"));
                VBox produitCard = loader.load();
                
                ProduitCardController cardController = loader.getController();
                cardController.setProduit(produit);
                cardController.setParentController(this);
                
                produitsContainer.getChildren().add(produitCard);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading products", e);
        }
    }

    public void ajouterAuPanier(Produit produit, int quantite) {
        panier.merge(produit, quantite, Integer::sum);
        updatePanierCount();
    }

    private void updatePanierCount() {
        int totalItems = panier.values().stream().mapToInt(Integer::intValue).sum();
        panierCount.setText(totalItems + " articles");
    }

    @FXML
    private void showPanier() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/panier/PanierView.fxml"));
            VBox panierView = loader.load();
            panierController = loader.getController();
            panierController.setProduits(panier);
            panierController.setParentController(this);
            
            // Remplacer le contenu de la scène
            mainContent.getChildren().set(0, panierView);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading cart view", e);
        }
    }

    public void showProduits() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/produit/ProduitView.fxml"));
            VBox produitView = loader.load();
            
            // Remplacer le contenu de la scène
            mainContent.getChildren().set(0, produitView);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading products view", e);
        }
    }

    public void clearPanier() {
        panier.clear();
        updatePanierCount();
    }
} 