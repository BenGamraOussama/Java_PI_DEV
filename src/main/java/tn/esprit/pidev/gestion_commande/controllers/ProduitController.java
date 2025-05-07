package tn.esprit.pidev.gestion_commande.controllers;

import org.json.JSONArray;
import org.json.JSONObject;
import tn.esprit.pidev.gestion_commande.entities.Produit;
import tn.esprit.pidev.gestion_commande.services.ProduitService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ProduitController {
    @FXML
    private FlowPane produitsContainer;

    @FXML
    private Label panierCount;

    @FXML
    private TextField searchField;

    @FXML
    private TextField minPriceField;

    @FXML
    private TextField maxPriceField;

    private final ProduitService produitService = new ProduitService();
    private static final Logger LOGGER = Logger.getLogger(ProduitController.class.getName());

    private final Map<Produit, Integer> panier = new HashMap<>();
    private PanierController panierController;
    private MainController mainController;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    public void initialize() {
        chargerProduits();
        updatePanierCount();
    }

    private void chargerProduits() {
        String keyword = searchField != null ? searchField.getText().toLowerCase() : "";
        double min = parseDoubleOrDefault(minPriceField != null ? minPriceField.getText() : "", 0);
        double max = parseDoubleOrDefault(maxPriceField != null ? maxPriceField.getText() : "", Double.MAX_VALUE);

        try {
            produitsContainer.getChildren().clear();
            List<Produit> produits = produitService.afficher();
            for (Produit produit : produits) {
                boolean matchesKeyword = produit.getNom().toLowerCase().contains(keyword);
                boolean matchesPrice = produit.getPrix() >= min && produit.getPrix() <= max;
                if (matchesKeyword && matchesPrice) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/pidev/FXML/produit/ProduitCard.fxml"));
                    VBox produitCard = loader.load();
                    ProduitCardController cardController = loader.getController();
                    cardController.setProduit(produit);
                    cardController.setParentController(this);
                    produitsContainer.getChildren().add(produitCard);
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading products", e);
        }
    }

    private double parseDoubleOrDefault(String text, double defaultValue) {
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    @FXML
    private void onSearch() {
        chargerProduits();
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
        if (mainController == null) {
            LOGGER.severe("mainController is not set in showPanier!");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/pidev/FXML/panier/PanierView.fxml"));
            VBox panierView = loader.load();
            panierController = loader.getController();
            panierController.setProduits(panier);
            panierController.setParentController(this);
            mainController.setContent(panierView);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading cart view", e);
        }
    }

    public void showProduits() {
        if (mainController == null) {
            LOGGER.severe("mainController is not set in showProduits!");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/pidev/FXML/produit/ProduitView.fxml"));
            VBox produitView = loader.load();
            ProduitController controller = loader.getController();
            controller.setMainController(mainController);
            controller.setPanier(panier);
            mainController.setContent(produitView);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading products view", e);
        }
    }

    public void clearPanier() {
        panier.clear();
        updatePanierCount();
    }

    public void setPanier(Map<Produit, Integer> panierData) {
        this.panier.clear();
        this.panier.putAll(panierData);
        updatePanierCount();
    }
    @FXML private TextField countryField;
    @FXML private Label inflationLabel;
    @FXML

    private void onShowInflation() {
        String country = countryField.getText().trim();
        if (country.isEmpty()) {
            showAlert("Erreur", "Veuillez entrer un pays.");
            return;
        }

        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                String apiUrl = "https://api.api-ninjas.com/v1/inflation?country=" + country;
                URL url = new URL(apiUrl);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("X-Api-Key", "MuKtpeOZ2a0anzwcfy5vAQ==6RdKfM1IsEFkYido");
                conn.setRequestMethod("GET");

                int status = conn.getResponseCode();
                if (status != 200) {
                    throw new IOException("HTTP Error: " + status);
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder responseStr = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    responseStr.append(line);
                }
                reader.close();

                JSONArray jsonArray = new JSONArray(responseStr.toString());
                if (jsonArray.length() > 0) {
                    JSONObject data = jsonArray.getJSONObject(0);

                    String pays = data.optString("country", "N/A");
                    String periode = data.optString("period", "N/A");
                    double tauxMensuel = data.optDouble("monthly_rate_pct", 0.0);
                    double tauxAnnuel = data.optDouble("yearly_rate_pct", 0.0);

                    String info = String.format(
                            "Pays : %s\nPériode : %s\nTaux mensuel : %.2f%%\nTaux annuel : %.2f%%",
                            pays, periode, tauxMensuel, tauxAnnuel
                    );

                    Platform.runLater(() -> showAlert("Inflation", info));
                } else {
                    Platform.runLater(() -> showAlert("Inflation", "Aucune donnée trouvée."));
                }

            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> showAlert("Erreur", "Erreur lors de la récupération des données."));
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }).start();
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }




}
