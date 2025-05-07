package tn.esprit.pidev.gestion_commande.controllers;

import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import tn.esprit.pidev.gestion_commande.entities.Commande;
import tn.esprit.pidev.gestion_commande.services.CommandeService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.collections.FXCollections;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Label;
import javafx.scene.control.Button;

import java.awt.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.text.Document;
import java.io.File;
import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;

public class ListViewCommande {
    @FXML
    private VBox commandesContainer;

    @FXML
    private TextField searchField;

    @FXML
    private TextField emailField;

    @FXML
    private VBox statsContainer;

    @FXML
    private PieChart productStatsChart;

    @FXML
    private ImageView qrCodeImageView;

    @FXML
    private Label qrCodeLabel;

    @FXML
    private Button btnExportPDF;

    private final CommandeService serviceCommande = new CommandeService();
    private static final Logger LOGGER = Logger.getLogger(ListViewCommande.class.getName());

    // Email configuration
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String EMAIL_USERNAME = "culeks.here@gmail.com";  // Replace with your email
    private static final String EMAIL_PASSWORD = "lcrvncymoakisiuk";     // Replace with your app password

    @FXML
    public void initialize() {
        loadCommandes();
        loadProductStatistics();
        setupSearch();
    }

    private void setupSearch() {
        searchField.textProperty().addListener((obs, oldValue, newValue) -> {
            loadCommandes();
        });
    }

    @FXML
    public void loadCommandes() {
        try {
            commandesContainer.getChildren().clear();
            List<Commande> commandes = serviceCommande.afficher();

            String keyword = (searchField != null && searchField.getText() != null)
                    ? searchField.getText().toLowerCase()
                    : "";

            for (Commande commande : commandes) {
                if (commande.getNomClient().toLowerCase().contains(keyword)) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/pidev/FXML/commande/CommandeCard.fxml"));
                    HBox commandeCard = loader.load();
                    CommandeCardController cardController = loader.getController();
                    cardController.setParentController(this);
                    cardController.setCommandeData(commande);
                    commandesContainer.getChildren().add(commandeCard);
                }
            }
        } catch (IOException | SQLException e) {
            LOGGER.log(Level.SEVERE, "Error loading commands", e);
            showAlert(Alert.AlertType.ERROR, "Erreur",
                     "Impossible de charger les commandes: " + e.getMessage());
        }
    }

    @FXML
    private void loadProductStatistics() {
        try {
            Map<String, Double> stats = serviceCommande.getCommandeStatistics();

            // Clear existing data
            productStatsChart.getData().clear();

            if (stats.isEmpty()) {
                showAlert(Alert.AlertType.INFORMATION, "Information",
                         "Aucune donnée de commande disponible.");
                return;
            }

            // Add new data
            for (Map.Entry<String, Double> entry : stats.entrySet()) {
                PieChart.Data slice = new PieChart.Data(
                    entry.getKey() + " (" + entry.getValue() + "%)",
                    entry.getValue()
                );
                productStatsChart.getData().add(slice);
            }

            // Update chart title based on data
            productStatsChart.setTitle("Statistiques des Commandes");

            // Make sure the chart is visible
            productStatsChart.setVisible(true);

            // Add hover effect for better visibility
            productStatsChart.getData().forEach(data -> {
                String category = data.getName().startsWith("Client:") ? "clients" : "mois";
                String tooltipText = String.format("%.1f%% des commandes %s",
                    data.getPieValue(),
                    category.equals("clients") ? "par ce client" : "ce " + category);

                Tooltip tooltip = new Tooltip(tooltipText);
                Tooltip.install(data.getNode(), tooltip);

                data.getNode().setOnMouseEntered(e -> {
                    data.getNode().setStyle("-fx-scale-x: 1.1; -fx-scale-y: 1.1;");
                });
                data.getNode().setOnMouseExited(e -> {
                    data.getNode().setStyle("");
                });
            });

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error loading command statistics", e);
            showAlert(Alert.AlertType.ERROR, "Erreur",
                     "Impossible de charger les statistiques: " + e.getMessage());
        }
    }

    @FXML
    private void onSearch() {
        loadCommandes();
    }

    @FXML
    private void handleSendEmail() {
        String recipientEmail = emailField.getText().trim();
        if (recipientEmail.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez entrer une adresse e-mail valide.");
            return;
        }

        try {
            sendEmail(recipientEmail);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "L'e-mail a été envoyé avec succès.");
            emailField.clear();
        } catch (MessagingException e) {
            LOGGER.log(Level.SEVERE, "Error sending email", e);
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'envoyer l'e-mail: " + e.getMessage());
        }
    }

    private void sendEmail(String recipientEmail) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.smtp.ssl.trust", SMTP_HOST);

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_USERNAME, EMAIL_PASSWORD);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(EMAIL_USERNAME));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
        message.setSubject("Détails de votre commande");

        // Create HTML content
        String htmlContent = String.format("""
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; }
                    .header { color: #2C3E50; font-size: 24px; margin-bottom: 20px; }
                    .content { color: #34495E; margin: 20px 0; }
                    .footer { color: #7F8C8D; font-size: 14px; margin-top: 30px; }
                    .highlight { color: #3498DB; font-weight: bold; }
                </style>
            </head>
            <body>
                <div class="header">
                    Confirmation de Commande
                </div>
                <div class="content">
                    Cher client,<br><br>
                    
                    Nous vous remercions pour votre commande. Voici un récapitulatif :<br><br>
                    
                    <span class="highlight">Détails de la commande :</span><br>
                    • Date : %s<br>
                    • Montant total : %.2f €<br>
                    • Statut : En cours de traitement<br><br>
                    
                    Votre commande est en cours de traitement et sera expédiée dans les plus brefs délais.
                </div>
                <div class="footer">
                    Cordialement,<br>
                    L'équipe Boutique en Ligne<br>
                    <small>Cet email est généré automatiquement, merci de ne pas y répondre.</small>
                </div>
            </body>
            </html>
        """, java.time.LocalDate.now(), 0.0); // You can replace 0.0 with actual order amount

        // Set the email's content type to HTML
        message.setContent(htmlContent, "text/html; charset=utf-8");

        Transport.send(message);
    }

    public void updateQRCode(Image qrImage, String description) {
        if (qrCodeImageView != null) {
            qrCodeImageView.setImage(qrImage);
            qrCodeLabel.setText(description);
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void toggleStats() {
        boolean isVisible = !statsContainer.isVisible();
        statsContainer.setVisible(isVisible);
        statsContainer.setManaged(isVisible);

        if (isVisible) {
            loadProductStatistics(); // Refresh stats when showing
        }
    }

    public void handleAddToCart(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/pidev/FXML/commande/AddToCart.fxml"));
            Parent root = loader.load(); // No cast to VBox

            Scene scene = new Scene(root);
            Stage stage = (Stage) commandesContainer.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading MainView.fxml", e);
        }

    }

    @FXML
    private void handleSortAscending() {
        try {
            commandesContainer.getChildren().clear();
            List<Commande> commandes = serviceCommande.afficher();

            // Sort by date
            commandes.sort((c1, c2) -> {
                if (c1.getDateCommande() == null && c2.getDateCommande() == null) return 0;
                if (c1.getDateCommande() == null) return -1;
                if (c2.getDateCommande() == null) return 1;
                return c1.getDateCommande().compareTo(c2.getDateCommande());
            });

            for (Commande commande : commandes) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/pidev/FXML/commande/CommandeCard.fxml"));
                HBox commandeCard = loader.load();
                CommandeCardController cardController = loader.getController();
                cardController.setCommandeData(commande);
                commandesContainer.getChildren().add(commandeCard);
            }
        } catch (IOException | SQLException e) {
            LOGGER.log(Level.SEVERE, "Error sorting commands ascending", e);
            showAlert(Alert.AlertType.ERROR, "Erreur", 
                     "Impossible de trier les commandes: " + e.getMessage());
        }
    }

    @FXML
    private void handleSortDescending() {
        try {
            commandesContainer.getChildren().clear();
            List<Commande> commandes = serviceCommande.afficher();

            // Sort by date in reverse order
            commandes.sort((c1, c2) -> {
                if (c1.getDateCommande() == null && c2.getDateCommande() == null) return 0;
                if (c1.getDateCommande() == null) return 1;
                if (c2.getDateCommande() == null) return -1;
                return c2.getDateCommande().compareTo(c1.getDateCommande());
            });

            for (Commande commande : commandes) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/pidev/FXML/commande/CommandeCard.fxml"));
                HBox commandeCard = loader.load();
                CommandeCardController cardController = loader.getController();
                cardController.setCommandeData(commande);
                commandesContainer.getChildren().add(commandeCard);
            }
        } catch (IOException | SQLException e) {
            LOGGER.log(Level.SEVERE, "Error sorting commands descending", e);
            showAlert(Alert.AlertType.ERROR, "Erreur", 
                     "Impossible de trier les commandes: " + e.getMessage());
        }
    }

}
