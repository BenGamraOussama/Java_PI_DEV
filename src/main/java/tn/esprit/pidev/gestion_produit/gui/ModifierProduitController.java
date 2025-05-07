package tn.esprit.pidev.gestion_produit.gui;

import javafx.stage.Stage;
import tn.esprit.pidev.gestion_produit.entities.Produit;
import tn.esprit.pidev.gestion_produit.entities.Produit_categorie;
import tn.esprit.pidev.gestion_produit.services.ProduitServices;
import tn.esprit.pidev.gestion_produit.services.ProduitCategorieServices;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;

import java.sql.Connection;
import java.sql.SQLException;

public class ModifierProduitController {

    @FXML private TextField nomTextField;
    @FXML private TextField descriptionTextField;
    @FXML private TextField quantiteTextField;
    @FXML private TextField prixTextField;
    @FXML private ComboBox<Produit_categorie> categorieComboBox;

    private Connection connection;
    private ProduitServices produitServices = new ProduitServices();
    private final ProduitCategorieServices categorieServices = new ProduitCategorieServices(connection);
    private Produit produit;

    public ModifierProduitController() throws SQLException {
    }

    public void initialize() {
        try {
            // Charger toutes les catégories dans la ComboBox
            categorieComboBox.getItems().addAll(categorieServices.afficher());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setProduit(Produit produit) {
        this.produit = produit;
        // Remplir les champs avec les informations du produit
        nomTextField.setText(produit.getNom());
        descriptionTextField.setText(produit.getDescription());
        quantiteTextField.setText(String.valueOf(produit.getQuantite()));
        prixTextField.setText(String.valueOf(produit.getPrix()));  // Exemple de champ Prix
        categorieComboBox.setValue(produit.getCategorie());
    }
    @FXML
    private void modifierProduit(ActionEvent event) throws SQLException {
        if (nomTextField.getText().isEmpty() || descriptionTextField.getText().isEmpty() ||
                quantiteTextField.getText().isEmpty() || prixTextField.getText().isEmpty() ||
                categorieComboBox.getValue() == null) {
            showError("Tous les champs doivent être remplis.");
            return;
        }

        String nom = nomTextField.getText();
        String description = descriptionTextField.getText();
        int quantite;
        double prix;

        try {
            quantite = Integer.parseInt(quantiteTextField.getText());
            prix = Double.parseDouble(prixTextField.getText());
        } catch (NumberFormatException e) {
            showError("Quantité et prix doivent être des nombres valides.");
            return;
        }

        Produit_categorie categorie = categorieComboBox.getValue();

        produit.setNom(nom);
        produit.setDescription(description);
        produit.setQuantite(quantite);
        produit.setPrix((float) prix);
        produit.setCategorie(categorie);


        showConfirmation("Produit mis à jour avec succès.");

        // ✅ Fermer la fenêtre actuelle
        ((Stage) nomTextField.getScene().getWindow()).close();

    }


    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showConfirmation(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void setListeProduitsController(ListeProduitsController listeProduitsController) {

    }
}
