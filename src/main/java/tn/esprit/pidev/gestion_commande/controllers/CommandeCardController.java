package tn.esprit.pidev.gestion_commande.controllers;

import tn.esprit.pidev.gestion_commande.entities.Commande;
import tn.esprit.pidev.gestion_commande.entities.LigneCommande;
import tn.esprit.pidev.gestion_commande.entities.Produit;
import tn.esprit.pidev.gestion_commande.services.CommandeService;
import tn.esprit.pidev.gestion_commande.services.LigneCommandeService;
import tn.esprit.pidev.gestion_commande.services.ProduitService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

public class CommandeCardController {
    @FXML private Label dateLabel;
    @FXML private Label montantLabel;
    @FXML private Label userLabel;
    @FXML private Button btnModifier;
    @FXML private Button btnSupprimer;
    @FXML private Button btnQRCode;
    
    private Commande commande;
    private ListViewCommande parentController;
    private final CommandeService commandeService = new CommandeService();
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public void setParentController(ListViewCommande controller) {
        this.parentController = controller;
    }

    public void setCommandeData(Commande commande) {
        this.commande = commande;
        dateLabel.setText("Date: " + commande.getDateCommande().format(DATE_FORMATTER));
        montantLabel.setText("Montant: " + String.format("%.2f €", commande.getMontantTotal()));
        userLabel.setText("Utilisateur ID: " + commande.getUserId());

        btnSupprimer.setOnAction(e -> handleDelete());
        btnModifier.setOnAction(e -> handleEdit());
        btnQRCode.setOnAction(e -> generateQRCode());
    }

    private void handleDelete() {
        try {
            commandeService.supprimer(commande.getId());
            // Refresh the parent view
            if (parentController != null) {
            }
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de supprimer la commande: " + e.getMessage());
        }
    }

    private void handleEdit() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/pidev/FXML/commande/EditCommande.fxml"));
            Scene scene = new Scene(loader.load());
            EditCommandeController controller = loader.getController();
            controller.setCommande(commande);
            
            Stage stage = new Stage();
            stage.setTitle("Modifier Commande");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir l'éditeur: " + e.getMessage());
        }
    }

    private void generateQRCode() {
        try {
            // Create QR Code data
            String qrData = String.format("Commande #%d%nClient: %s %s%nDate: %s%nMontant: %.2f €",
                commande.getId(),
                commande.getNomClient(),
                commande.getPrenomClient(),
                commande.getDateCommande().format(DATE_FORMATTER),
                commande.getMontantTotal());

            // Generate QR Code
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(qrData, BarcodeFormat.QR_CODE, 300, 300);

            // Convert to image
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            Image qrImage = new Image(inputStream);

            // Update QR code in parent controller
            if (parentController != null) {
                parentController.updateQRCode(qrImage, "QR Code - Commande #" + commande.getId());
            }

        } catch (Exception e) {
            showAlert("Erreur", "Erreur de génération du QR Code: " + e.getMessage());
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
