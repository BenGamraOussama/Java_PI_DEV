package tn.esprit.pidev.gestion_commande.controllers;

import tn.esprit.pidev.gestion_commande.entities.Commande;
import tn.esprit.pidev.gestion_commande.entities.LigneCommande;
import tn.esprit.pidev.gestion_commande.entities.Produit;
import tn.esprit.pidev.gestion_commande.services.LigneCommandeService;
import tn.esprit.pidev.gestion_commande.services.ProduitService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

public class CommandCardController {

    @FXML
    private Label dateLabel;

    @FXML
    private Label montantLabel;

    @FXML
    private Label userLabel;

    @FXML
    private Button detailButton;

    @FXML
    private Button incrementButton;

    @FXML
    private Button decrementButton;

    @FXML
    private TextField quantityField;

    @FXML
    private Button btnQRCode;

    @FXML
    private ImageView qrCodeImageView;

    private int produitId = 1;  // Exemple : produit fixe pour l'instant

    private final ProduitService produitService = new ProduitService();

    private final LigneCommandeService ligneCommandeService = new LigneCommandeService();

    private int quantite = 1;

    private Commande commande;

    private ListViewCommande parentController;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public void setParentController(ListViewCommande controller) {
        this.parentController = controller;
    }

    public void setCommandeData(Commande commande) {
        this.commande = commande;
        dateLabel.setText("Date: " + commande.getDateCommande());
        montantLabel.setText("Montant: " + commande.getMontantTotal() + " €");
        userLabel.setText("Utilisateur ID: " + commande.getUserId());

        quantityField.setText(String.valueOf(quantite));

        // 🔹 Listener pour n'accepter que des chiffres
        quantityField.textProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                quantityField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        // 🔹 Bouton +
        incrementButton.setOnAction(e -> {
            quantite = getQuantiteFromField();
            quantite++;
            quantityField.setText(String.valueOf(quantite));
        });

        // 🔹 Bouton -
        decrementButton.setOnAction(e -> {
            quantite = getQuantiteFromField();
            if (quantite > 1) {
                quantite--;
                quantityField.setText(String.valueOf(quantite));
            }
        });

        // 🔹 Bouton Détails
        detailButton.setOnAction(event -> {
            try {
                quantite = getQuantiteFromField();

                // 🔹 Récupérer le produit complet
                Produit produit = produitService.getProduitById(produitId);

                LigneCommande ligne = new LigneCommande();
                ligne.setProduit(produit.getId());
                ligne.setQuantite(quantite);
                ligne.setPrix(produit.getPrix());   // Prix dynamique
                ligne.setCommandeId(commande.getId());

                ligneCommandeService.ajouter(ligne);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Succès");
                alert.setHeaderText(null);
                alert.setContentText("Ligne de commande pour '" + produit.getNom() + "' ajoutée avec " + quantite + " unité(s) !");
                alert.showAndWait();

                quantite = 1;
                quantityField.setText(String.valueOf(quantite));

            } catch (SQLException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText(null);
                alert.setContentText("Erreur lors de l'ajout de la ligne de commande.");
                alert.showAndWait();
            }
        });

        // Set up QR Code button handler
        btnQRCode.setOnAction(e -> generateAndShowQRCode());
    }

    private void generateAndShowQRCode() {
        try {
            // Create QR Code data
            String qrData = String.format("Commande #%d%nClient: %s %s%nDate: %s%nMontant: %.2f €",
                commande.getId(),
                commande.getNomClient(),
                commande.getPrenomClient(),
                commande.getDateCommande(),
                commande.getMontantTotal());

            // Generate QR Code
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(qrData, BarcodeFormat.QR_CODE, 300, 300);

            // Convert to image
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            Image qrImage = new Image(inputStream);
            qrCodeImageView.setImage(qrImage);
            qrCodeImageView.setFitWidth(300);
            qrCodeImageView.setFitHeight(300);

            // Update QR code in parent controller
            if (parentController != null) {
                parentController.updateQRCode(qrImage, "QR Code - Commande #" + commande.getId());
            }

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Erreur de génération du QR Code");
            alert.setContentText("Une erreur est survenue lors de la génération du QR Code: " + e.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * Récupère la quantité depuis le TextField de manière sécurisée
     */
    private int getQuantiteFromField () {
        try {
            int qte = Integer.parseInt(quantityField.getText());
            return qte > 0 ? qte : 1;
        } catch (NumberFormatException e) {
            return 1;
        }
    }
}
