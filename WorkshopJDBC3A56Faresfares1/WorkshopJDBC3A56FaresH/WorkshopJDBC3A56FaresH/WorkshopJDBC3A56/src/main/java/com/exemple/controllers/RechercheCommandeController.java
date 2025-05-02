package com.exemple.controllers;

import com.exemple.entities.Commande;
import com.exemple.services.CommandeService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RechercheCommandeController {
    @FXML
    private TextField idField;

    @FXML
    private TextField montantField;

    @FXML
    private TextField userField;

    @FXML
    private FlowPane resultsContainer;

    private CommandeService commandeService;
    private static final Logger LOGGER = Logger.getLogger(RechercheCommandeController.class.getName());

    @FXML
    public void initialize() {
        commandeService = new CommandeService();
    }

    @FXML
    public void handleSearch() {
        try {
            // Clear existing results
            resultsContainer.getChildren().clear();

            // Get search criteria
            String id = idField.getText().trim();
            String montant = montantField.getText().trim();
            String user = userField.getText().trim();

            // Perform search
            List<Commande> results = commandeService.rechercher(id, montant, user);

            // Display results
            for (Commande commande : results) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/commande/CommandeCard.fxml"));
                VBox commandeCard = loader.load();

                CommandeCardController cardController = loader.getController();
                cardController.setCommandeData(commande);

                resultsContainer.getChildren().add(commandeCard);
            }

            if (results.isEmpty()) {
                LOGGER.info("No commandes found matching the search criteria");
            }

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading commande cards", e);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error when searching commandes", e);
        }
    }
} 