package tn.esprit.pidev.gestion_produit.gui;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import tn.esprit.pidev.gestion_produit.entities.Produit;
import tn.esprit.pidev.gestion_produit.services.PanierService;
import tn.esprit.pidev.Database.Database;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.List;

public class PanierController {

    @FXML private TableView<PanierItem> panierTable;
    @FXML private TableColumn<PanierItem, String> nomCol;
    @FXML private TableColumn<PanierItem, Double> prixCol;
    @FXML private TableColumn<PanierItem, Integer> quantiteCol;
    @FXML private TableColumn<PanierItem, Double> totalCol;
    @FXML private TableColumn<PanierItem, Void> supprimerCol;
    @FXML private Label totalAvantRemiseLabel;
    @FXML private Label remiseLabel;
    @FXML private Label totalAPayerLabel;
    @FXML private HBox remiseContainer;

    private final ObservableList<PanierItem> panierItems = FXCollections.observableArrayList();
    private PanierService panierService;
    private int currentUserId = 1; // Valeur par défaut
    private final DecimalFormat decimalFormat = new DecimalFormat("0.00 DT");

    @FXML
    public void initialize() {
        // Initialisation de la connexion et du service Panier
        Connection connection = Database.getConnection();
        this.panierService = new PanierService(connection);

        setupTableColumns();
        setupTableEditable();
        setupBindings();
        setupSupprimerCol();

        loadPanierItems(); // Charge les éléments au démarrage
    }

    private void setupTableColumns() {
        // Configuration des colonnes de la TableView
        nomCol.setCellValueFactory(new PropertyValueFactory<>("nom"));
        prixCol.setCellValueFactory(new PropertyValueFactory<>("prix"));
        quantiteCol.setCellValueFactory(new PropertyValueFactory<>("quantite"));
        totalCol.setCellValueFactory(new PropertyValueFactory<>("total"));
    }

    private void setupTableEditable() {
        panierTable.setItems(panierItems);
        panierTable.setEditable(true);

        // Mise en place de l'édition des quantités avec TextFieldTableCell
        quantiteCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        quantiteCol.setOnEditCommit(event -> {
            PanierItem item = event.getRowValue();
            int newQuantite = event.getNewValue();
            if (newQuantite > 0) {
                item.setQuantite(newQuantite);
                updateItemInDatabase(item);
                updateTotals();
                panierTable.refresh(); // Mise à jour immédiate
            } else {
                showAlert("La quantité doit être supérieure à 0");
                panierTable.refresh(); // Revenir à l'ancienne valeur
            }
        });
    }

    private void setupBindings() {
        // Mise à jour des totaux chaque fois qu'un item change dans le panier
        panierItems.addListener((ListChangeListener<? super PanierItem>) change -> updateTotals());
    }

    private void updateTotals() {
        // Calcul du total avant remise
        double totalAvantRemise = panierItems.stream()
                .mapToDouble(PanierItem::getTotal)  // Récupère le total de chaque produit
                .sum();

        totalAvantRemiseLabel.setText(decimalFormat.format(totalAvantRemise));  // Affiche le total avant remise

        // Application de la remise si applicable
        double remise = 0;
        if (totalAvantRemise >= 100) {
            remise = totalAvantRemise * 0.1;  // Remise de 10% si total >= 100
        }

        double totalApresRemise = totalAvantRemise - remise;  // Total après application de la remise

        remiseLabel.setText(decimalFormat.format(remise));  // Affiche la remise
        totalAPayerLabel.setText(decimalFormat.format(totalApresRemise));  // Affiche le total après remise
    }

    private void updateItemInDatabase(PanierItem item) {
        try {
            panierService.modifierQuantite(item.getId(), item.getQuantite());
        } catch (SQLException e) {
            showAlert("Erreur lors de la mise à jour de la quantité : " + e.getMessage());
        }
    }

    @FXML
    private void handleSupprimer() {
        // Gestion de la suppression d'un produit
        PanierItem selected = panierTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                panierService.supprimerProduit(selected.getId());
                panierItems.remove(selected);
            } catch (SQLException e) {
                showAlert("Erreur lors de la suppression du produit : " + e.getMessage());
            }
        } else {
            showAlert("Veuillez sélectionner un produit à supprimer.");
        }
    }

    @FXML
    private void handlePasserCommande() {
        // Gestion de la commande
        if (panierItems.isEmpty()) {
            showAlert("Votre panier est vide.");
            return;
        }
        else {
            try {
                // Charger la vue CommandeCard.fxml
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/pidev/FXML/panier/PaymentView.fxml"));
                Parent root = loader.load();

                // Créer une nouvelle scène
                Scene scene = new Scene(root);

                // Obtenir la fenêtre actuelle
                Stage stage = (Stage) panierTable.getScene().getWindow();

                // Remplacer la scène actuelle par la nouvelle
                stage.setScene(scene);
                stage.setTitle("Passer Commande");
                stage.show();
            } catch (Exception e) {
                showAlert("Erreur lors de la navigation vers la page de commande: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void setCurrentUserId(int userId) {
        // Mise à jour de l'identifiant utilisateur
        this.currentUserId = userId;
        loadPanierItems();
    }

    private void loadPanierItems() {
        // Chargement des articles du panier depuis la base de données
        try {
            List<PanierItem> items = panierService.getPanierItems(currentUserId);
            panierItems.setAll(items);
        } catch (SQLException e) {
            showAlert("Erreur de chargement du panier : " + e.getMessage());
        }
    }

    private void showAlert(String message) {
        // Affichage d'une alerte
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void commander(ActionEvent actionEvent) {
        handlePasserCommande();
    }

    @FXML
    public void fermerFenetre(ActionEvent actionEvent) {
        // Fermer la fenêtre
        ((Stage) ((Button) actionEvent.getSource()).getScene().getWindow()).close();
    }

    private void setupSupprimerCol() {
        // Configuration de la colonne pour supprimer un produit
        supprimerCol.setCellFactory(param -> new TableCell<PanierItem, Void>() {
            private final Button btn = new Button("Supprimer");

            {
                btn.setOnAction(event -> {
                    PanierItem item = getTableView().getItems().get(getIndex());
                    try {
                        panierService.supprimerProduit(item.getId());
                        panierItems.remove(item);
                    } catch (SQLException e) {
                        showAlert("Erreur lors de la suppression du produit");
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btn);
                }
            }
        });
    }

    public void setPanierItems(List<Produit> panier) {
        // Convertir les produits en PanierItem et les ajouter à la liste
        if (panier != null && !panier.isEmpty()) {
            for (Produit produit : panier) {
                PanierItem item = new PanierItem(
                    produit.getId(),
                    produit.getNom(),
                    produit.getPrix(),
                    1  // Quantité par défaut
                );
                // Vérifier si l'item n'est pas déjà dans la liste
                if (!panierItems.stream().anyMatch(i -> i.getId() == item.getId())) {
                    panierItems.add(item);
                }
            }
            updateTotals();
        }
    }

    // ======================== CLASSE INTERNE ========================
    public static class PanierItem {
        private final int id;
        private final String nom;
        private final double prix;
        private int quantite;
        private double total;

        public PanierItem(int id, String nom, double prix, int quantite) {
            this.id = id;
            this.nom = nom;
            this.prix = prix;
            setQuantite(quantite); // Met à jour le total automatiquement
        }

        public int getId() { return id; }
        public String getNom() { return nom; }
        public double getPrix() { return prix; }
        public int getQuantite() { return quantite; }
        public double getTotal() { return total; }

        public void setQuantite(int quantite) {
            this.quantite = quantite;
            this.total = prix * quantite;
        }
    }
}
