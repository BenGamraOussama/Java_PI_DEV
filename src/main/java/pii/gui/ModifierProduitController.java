package pii.gui;

import pii.entities.Produit;
import pii.entities.Produit_categorie;
import pii.services.ProduitServices;
import pii.services.Produit_CategoriesServices;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import java.sql.SQLException;

public class ModifierProduitController {

    @FXML private TextField nomTextField;
    @FXML private TextField descriptionTextField;
    @FXML private TextField quantiteTextField;
    @FXML private TextField prixTextField;
    @FXML private ComboBox<Produit_categorie> categorieComboBox;

    private ProduitServices produitServices = new ProduitServices();
    private Produit_CategoriesServices categorieServices = new Produit_CategoriesServices();
    private Produit produit;

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
    private void modifierProduit(ActionEvent event) {
        try {
            // Récupérer les valeurs des champs
            String nom = nomTextField.getText();
            String description = descriptionTextField.getText();
            int quantite = Integer.parseInt(quantiteTextField.getText());
            double prix = Double.parseDouble(prixTextField.getText());  // Exemple de champ Prix
            Produit_categorie categorie = categorieComboBox.getValue();

            // Modifier le produit
            produit.setNom(nom);
            produit.setDescription(description);
            produit.setQuantite(quantite);
            produit.setPrix(prix);  // Exemple de champ Prix
            produit.setCategorie(categorie);

            // Appeler le service pour mettre à jour le produit dans la base de données
            produitServices.updateProduit(produit);

            // Optionnel : Fermer la fenêtre ou revenir à la liste des produits
            // Fermer l'écran actuel ou rediriger vers la liste des produits
            // ... (ajoute ici la logique pour fermer ou rediriger vers la page d'accueil)

        } catch (SQLException e) {
            e.printStackTrace();
            // Afficher un message d'erreur si la modification échoue
            showError("Erreur lors de la mise à jour du produit");
        } catch (NumberFormatException e) {
            showError("Veuillez entrer des valeurs valides");
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
