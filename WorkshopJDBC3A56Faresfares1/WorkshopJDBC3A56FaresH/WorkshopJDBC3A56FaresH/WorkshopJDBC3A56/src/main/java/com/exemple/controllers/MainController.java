package com.exemple.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/produit/ProduitView.fxml"));
            VBox produitsView = loader.load();
            produitView.getChildren().add(produitsView);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading products view", e);
        }
    }
} 