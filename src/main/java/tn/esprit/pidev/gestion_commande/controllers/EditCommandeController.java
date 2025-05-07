package tn.esprit.pidev.gestion_commande.controllers;

import tn.esprit.pidev.gestion_commande.entities.Commande;
import tn.esprit.pidev.gestion_commande.services.CommandeService;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;

public class EditCommandeController {

    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private DatePicker datePicker;

    private Commande commande;
    private final CommandeService commandeService = new CommandeService();

    public void setCommande(Commande commande) {
        this.commande = commande;
        nomField.setText(commande.getNomClient());
        prenomField.setText(commande.getPrenomClient());
        datePicker.setValue(commande.getDateCommande());
    }

    @FXML
    public void handleSave() {
        commande.setNomClient(nomField.getText());
        commande.setPrenomClient(prenomField.getText());
        commande.setDateCommande(datePicker.getValue());

        try {
            commandeService.modifier(commande);
            ((Stage) nomField.getScene().getWindow()).close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
