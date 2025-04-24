package com.exemple.controllers;

import com.exemple.entities.Commande;
import com.exemple.services.CommandeService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class PassercommandeController {

    @FXML
    private FlowPane cardsContainer;

    private CommandeService commandeService = new CommandeService();

    @FXML
    public void initialize() {
        try {
            List<Commande> commandes = commandeService.afficher();
            for (Commande commande : commandes) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Front/CommandeCard.fxml"));
                AnchorPane card = loader.load();

                CommandCardController controller = loader.getController();
                controller.setCommandeData(commande);

                cardsContainer.getChildren().add(card);
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
}
