package com.exemple.controllers;

import com.exemple.entities.Commande;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import com.exemple.services.CommandeService;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ListViewCommande implements Initializable {
    @FXML
    private FlowPane commandesContainer;

    private static final Logger LOGGER = Logger.getLogger(ListViewCommande.class.getName());
    private CommandeService serviceCommande;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        serviceCommande = new CommandeService(); // Initialize your service
        loadCommandes();
    }

    private void loadCommandes() {
        try {
            // Clear existing content
            commandesContainer.getChildren().clear();

            // Fetch real commandes from database
            List<Commande> commandes = serviceCommande.afficher();
            System.out.println(commandes);
            // Display each commande as a card
            for (Commande commande : commandes) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/commande/CommandeCard.fxml"));
                VBox CommandeCard = loader.load();

                // Get the controller to set the data
                CommandeCardController cardController = loader.getController();
                cardController.setCommandeData(commande);

                // Add the card to the container
                commandesContainer.getChildren().add(CommandeCard);
            }

            // If no commandes found, you might want to show a message
            if (commandes.isEmpty()) {
                // You could add a label or other UI element to show "No commandes found"
                LOGGER.info("No commandes found in database");
            }

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading commande cards", e);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error when fetching commandes", e);
        }
    }
}