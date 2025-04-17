package pii.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import pii.entities.Produit_categorie;
import pii.services.Produit_CategoriesService;

public class Ajout_CategorieController {

    @FXML
    private Button btnAjouter;

    @FXML
    private TextField nomCategorie;

    private final Produit_CategoriesService categorieService = new Produit_CategoriesService();

    // Méthode qui est appelée lorsque le bouton "Ajouter" est cliqué
    @FXML
    private void ajouterCategorie() {
        // Vérifier que le champ "Nom de la catégorie" n'est pas vide
        if (nomCategorie.getText().isEmpty()) {
            showAlert("Erreur", "Le nom de la catégorie ne peut pas être vide.");
            return;
        }

        // Créer une nouvelle catégorie avec le nom fourni par l'utilisateur
        Produit_categorie nouvelleCategorie = new Produit_categorie();
        nouvelleCategorie.setNom(nomCategorie.getText());

        // Appeler le service pour ajouter la catégorie à la base de données
        try {
            categorieService.ajouter(nouvelleCategorie);  // Assurez-vous que votre service ajoute la catégorie
            showAlert("Succès", "Catégorie ajoutée avec succès !");
            // Après avoir ajouté la catégorie, naviguer vers la liste des catégories
            ouvrirListeCategories();
        } catch (Exception e) {
            showAlert("Erreur", "Une erreur est survenue lors de l'ajout de la catégorie : " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Méthode pour fermer la fenêtre actuelle (par exemple, si l'utilisateur annule)
    @FXML
    private void annulerAction() {
        fermerFenetre();
    }

    // Méthode pour fermer la fenêtre actuelle
    private void fermerFenetre() {
        Stage stage = (Stage) nomCategorie.getScene().getWindow();
        stage.close();
    }

    // Méthode pour ouvrir l'interface "Liste des Categories"
    private void ouvrirListeCategories() {
        try {
            // Charger le fichier FXML de la liste des catégories
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListeCategories.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Liste des Catégories");
            stage.setScene(new Scene(root));
            stage.show();
            fermerFenetre();  // Optionnel : fermer la fenêtre actuelle après avoir ouvert la liste
        } catch (Exception e) {
            showAlert("Erreur", "Impossible d'ouvrir la liste des catégories : " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Méthode pour afficher des alertes d'erreur ou de succès
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Méthode pour gérer les événements de sélection d'image (si nécessaire)
    public void handleBrowseImage(ActionEvent actionEvent) {
        // Logique pour le bouton de sélection d'image (si besoin)
    }
}
