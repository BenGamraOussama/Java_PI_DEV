package tn.esprit.pidev.gestion_activite.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.image.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import tn.esprit.pidev.gestion_activite.entities.Exercice;
import tn.esprit.pidev.gestion_activite.entities.Activite;
import tn.esprit.pidev.gestion_activite.services.ExerciceService;
import tn.esprit.pidev.gestion_activite.services.ActiviteService;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class ListeExerciceController implements Initializable {

    @FXML
    private GridPane contentGrid;

    @FXML
    private ComboBox<String> typeFilter;

    @FXML
    private ComboBox<String> statusFilter;

    @FXML
    private Label patientNameLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private ProgressIndicator loadingIndicator;

    private List<Exercice> exercices;
    private List<Activite> activites;
    private ActiviteService activiteService;
    private ExerciceService exerciceService;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            activiteService = new ActiviteService();
            exerciceService = new ExerciceService();

            // Initialize filters with correct activity types
            typeFilter.getItems().addAll("Tous", "mentale", "relaxation", "sociale", "exercice");
            statusFilter.getItems().addAll("Tous", "en cours", "complété", "pas commencé");

            // Set default values
            typeFilter.setValue("Tous");
            statusFilter.setValue("Tous");

            // Add listeners to filters
            typeFilter.setOnAction(e -> applyFilters());
            statusFilter.setOnAction(e -> applyFilters());

            // Load data
            loadData();
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Erreur lors du chargement des données: " + e.getMessage());
        }
    }

    private void loadData() {
        try {
            loadingIndicator.setVisible(true);

            // Load both activities and exercises
            activites = activiteService.getAll();
            exercices = exerciceService.getAll();

            // Populate the combined grid
            populateCombinedGrid();

            loadingIndicator.setVisible(false);
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Erreur lors du chargement des données: " + e.getMessage());
            loadingIndicator.setVisible(false);
        }
    }

    private void populateCombinedGrid() {
        contentGrid.getChildren().clear();
        int column = 0;
        int row = 0;
        int maxColumns = 3; // Number of cards per row

        // First add all activities that are not exercises
        for (Activite activite : activites) {
            // Skip if this activity is an exercise
            if (exercices.stream().anyMatch(ex -> ex.getActivite().getId() == activite.getId())) {
                continue;
            }

            VBox card = createActiviteCard(activite);
            contentGrid.add(card, column, row);

            column++;
            if (column >= maxColumns) {
                column = 0;
                row++;
            }
        }

        // Then add all exercises
        for (Exercice exercice : exercices) {
            VBox card = createExerciseCard(exercice);
            contentGrid.add(card, column, row);

            column++;
            if (column >= maxColumns) {
                column = 0;
                row++;
            }
        }
    }

    private VBox createActiviteCard(Activite activite) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");
        card.setPrefWidth(300);
        card.setPrefHeight(400);

        // Default activity image
        ImageView imageView = new ImageView();
        try {
            Image image = new Image(getClass().getResourceAsStream("/tn/esprit/pidev/images/default_image.png"));
            imageView.setImage(image);
        } catch (Exception e) {
            System.err.println("Image not found: " + e.getMessage());
        }
        imageView.setFitWidth(250);
        imageView.setFitHeight(180);
        imageView.setPreserveRatio(true);

        // Title
        Label titleLabel = new Label(activite.getTitre());
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        titleLabel.setWrapText(true);
        titleLabel.setMaxWidth(250);

        // Description
        Text descriptionText = new Text(activite.getDescription());
        descriptionText.setWrappingWidth(250);
        descriptionText.setTextAlignment(TextAlignment.JUSTIFY);
        descriptionText.setStyle("-fx-font-size: 14px;");

        // Status and Type
        HBox infoBox = new HBox(10);
        infoBox.setAlignment(Pos.CENTER);

        Label statusLabel = new Label("État: " + activite.getStatus());
        statusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");

        Label typeLabel = new Label("Type: " + activite.getType());
        typeLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");

        infoBox.getChildren().addAll(statusLabel, typeLabel);

        // Status change buttons
        HBox statusButtonsBox = new HBox(10);
        statusButtonsBox.setAlignment(Pos.CENTER);
        statusButtonsBox.setPadding(new Insets(10, 0, 0, 0));

        // "En cours" button
        Button enCoursButton = new Button("En cours");
        enCoursButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-min-width: 100; " +
                "-fx-background-radius: 5; -fx-cursor: hand; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 1);");
        enCoursButton.setOnMouseEntered(e -> enCoursButton.setStyle("-fx-background-color: #1976D2; -fx-text-fill: white; -fx-min-width: 100; " +
                "-fx-background-radius: 5; -fx-cursor: hand; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 8, 0, 0, 2);"));
        enCoursButton.setOnMouseExited(e -> enCoursButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-min-width: 100; " +
                "-fx-background-radius: 5; -fx-cursor: hand; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 1);"));
        enCoursButton.setOnAction(e -> updateActivityStatus(activite, "En cours"));

        // "Terminé" button
        Button termineButton = new Button("Terminé");
        termineButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-min-width: 100; " +
                "-fx-background-radius: 5; -fx-cursor: hand; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 1);");
        termineButton.setOnMouseEntered(e -> termineButton.setStyle("-fx-background-color: #388E3C; -fx-text-fill: white; -fx-min-width: 100; " +
                "-fx-background-radius: 5; -fx-cursor: hand; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 8, 0, 0, 2);"));
        termineButton.setOnMouseExited(e -> termineButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-min-width: 100; " +
                "-fx-background-radius: 5; -fx-cursor: hand; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 1);"));
        termineButton.setOnAction(e -> updateActivityStatus(activite, "Terminé"));

        // Disable buttons if activity is already in that state
        enCoursButton.setDisable(activite.getStatus().equals("En cours"));
        termineButton.setDisable(activite.getStatus().equals("Terminé"));

        statusButtonsBox.getChildren().addAll(enCoursButton, termineButton);

        // Button container
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));

        // Listen button
        Button listenButton = new Button("Écouter");
        listenButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-min-width: 80; " +
                "-fx-background-radius: 5; -fx-cursor: hand; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 1);");
        listenButton.setOnMouseEntered(e -> listenButton.setStyle("-fx-background-color: #1976D2; -fx-text-fill: white; -fx-min-width: 80; " +
                "-fx-background-radius: 5; -fx-cursor: hand; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 8, 0, 0, 2);"));
        listenButton.setOnMouseExited(e -> listenButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-min-width: 80; " +
                "-fx-background-radius: 5; -fx-cursor: hand; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 1);"));
        listenButton.setOnAction(e -> speakDescription(activite.getDescription()));

        buttonBox.getChildren().add(listenButton);

        card.getChildren().addAll(imageView, titleLabel, descriptionText, infoBox, statusButtonsBox, buttonBox);
        card.setAlignment(Pos.CENTER);

        return card;
    }

    private void updateActivityStatus(Activite activite, String newStatus) {
        try {
            // Update the activity status in the database
            activite.setStatus(newStatus);
            activiteService.update(activite);

            // Refresh the grid to show the updated status
            populateCombinedGrid();

            // Show success message
            statusLabel.setText("Statut de l'activité mis à jour avec succès!");
            statusLabel.setStyle("-fx-text-fill: #4CAF50; -fx-padding: 10;");
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Erreur lors de la mise à jour du statut: " + e.getMessage());
            statusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-padding: 10;");
        }
    }

    private VBox createExerciseCard(Exercice exercice) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");
        card.setPrefWidth(300);
        card.setPrefHeight(400);

        // Default exercise image
        ImageView imageView = new ImageView();
        try {
            Image image = new Image(getClass().getResourceAsStream("/tn/esprit/pidev/images/default_image.png"));
            imageView.setImage(image);
        } catch (Exception e) {
            System.err.println("Image not found: " + e.getMessage());
        }
        imageView.setFitWidth(250);
        imageView.setFitHeight(180);
        imageView.setPreserveRatio(true);

        // Title
        Label titleLabel = new Label(exercice.getActivite().getTitre());
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        titleLabel.setWrapText(true);
        titleLabel.setMaxWidth(250);

        // Question
        Text questionText = new Text(exercice.getQuestion());
        questionText.setWrappingWidth(250);
        questionText.setTextAlignment(TextAlignment.JUSTIFY);
        questionText.setStyle("-fx-font-size: 14px;");

        // Status and Type info
        HBox infoBox = new HBox(10);
        infoBox.setAlignment(Pos.CENTER);

        Label statusLabel = new Label("État: " + exercice.getActivite().getStatus());
        statusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");

        Label typeLabel = new Label("Type: " + exercice.getActivite().getType());
        typeLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");

        infoBox.getChildren().addAll(statusLabel, typeLabel);

        // Button container
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));

        // Listen button
        Button listenButton = new Button("Écouter");
        listenButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-min-width: 80; " +
                "-fx-background-radius: 5; -fx-cursor: hand; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 1);");
        listenButton.setOnMouseEntered(e -> listenButton.setStyle("-fx-background-color: #1976D2; -fx-text-fill: white; -fx-min-width: 80; " +
                "-fx-background-radius: 5; -fx-cursor: hand; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 8, 0, 0, 2);"));
        listenButton.setOnMouseExited(e -> listenButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-min-width: 80; " +
                "-fx-background-radius: 5; -fx-cursor: hand; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 1);"));
        listenButton.setOnAction(e -> speakQuestion(exercice.getQuestion()));

        // Check if exercise has been answered
        boolean hasAnswer = false;
        try {
            hasAnswer = exerciceService.hasAnswer(exercice.getId());
            if (hasAnswer) {
                // Update activity status to "complété" if it has an answer
                exercice.getActivite().setStatus("complété");
                activiteService.update(exercice.getActivite());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Answer button (only show if not answered)
        if (!hasAnswer) {
            Button answerButton = new Button("Répondre");
            answerButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-min-width: 80; " +
                    "-fx-background-radius: 5; -fx-cursor: hand; " +
                    "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 1);");
            answerButton.setOnMouseEntered(e -> answerButton.setStyle("-fx-background-color: #388E3C; -fx-text-fill: white; -fx-min-width: 80; " +
                    "-fx-background-radius: 5; -fx-cursor: hand; " +
                    "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 8, 0, 0, 2);"));
            answerButton.setOnMouseExited(e -> answerButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-min-width: 80; " +
                    "-fx-background-radius: 5; -fx-cursor: hand; " +
                    "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 1);"));
            answerButton.setOnAction(e -> {
                openAnswerWindow(exercice);
                // After answering, refresh the grid to update the status
                populateCombinedGrid();
            });
            buttonBox.getChildren().add(answerButton);
        }

        // View responses button (only show if answered)
        if (hasAnswer) {
            Button viewResponsesButton = new Button("Voir Réponse");
            viewResponsesButton.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-min-width: 100; " +
                    "-fx-background-radius: 5; -fx-cursor: hand; " +
                    "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 1);");
            viewResponsesButton.setOnMouseEntered(e -> viewResponsesButton.setStyle("-fx-background-color: #F57C00; -fx-text-fill: white; -fx-min-width: 100; " +
                    "-fx-background-radius: 5; -fx-cursor: hand; " +
                    "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 8, 0, 0, 2);"));
            viewResponsesButton.setOnMouseExited(e -> viewResponsesButton.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-min-width: 100; " +
                    "-fx-background-radius: 5; -fx-cursor: hand; " +
                    "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 1);"));
            viewResponsesButton.setOnAction(e -> openResponsesWindow(exercice));
            buttonBox.getChildren().add(viewResponsesButton);
        }

        buttonBox.getChildren().add(listenButton);
        card.getChildren().addAll(imageView, titleLabel, questionText, infoBox, buttonBox);
        card.setAlignment(Pos.CENTER);

        return card;
    }

    @FXML
    private void resetFilters() {
        typeFilter.setValue("Tous");
        statusFilter.setValue("Tous");
        populateCombinedGrid();
    }

    private void applyFilters() {
        String selectedType = typeFilter.getValue();
        String selectedStatus = statusFilter.getValue();

        System.out.println("Filtering with Type: " + selectedType + ", Status: " + selectedStatus);
        System.out.println("Total activities before filter: " + activites.size());
        System.out.println("Total exercises before filter: " + exercices.size());

        // Filter activities (excluding exercises)
        List<Activite> filteredActivites = activites.stream()
                .filter(activite -> {
                    // Skip if this activity is an exercise
                    if (exercices.stream().anyMatch(ex -> ex.getActivite().getId() == activite.getId())) {
                        return false;
                    }

                    boolean typeMatch = selectedType.equals("Tous") ||
                            activite.getType().equalsIgnoreCase(selectedType);
                    boolean statusMatch = selectedStatus.equals("Tous") ||
                            activite.getStatus().equalsIgnoreCase(selectedStatus);

                    System.out.println("Activity: " + activite.getTitre() +
                            " Type: " + activite.getType() +
                            " Status: " + activite.getStatus() +
                            " Type Match: " + typeMatch +
                            " Status Match: " + statusMatch);

                    return typeMatch && statusMatch;
                })
                .collect(Collectors.toList());

        // Filter exercises
        List<Exercice> filteredExercices = exercices.stream()
                .filter(exercice -> {
                    boolean typeMatch = selectedType.equals("Tous") ||
                            exercice.getActivite().getType().equalsIgnoreCase(selectedType);
                    boolean statusMatch = selectedStatus.equals("Tous") ||
                            exercice.getActivite().getStatus().equalsIgnoreCase(selectedStatus);

                    System.out.println("Exercise: " + exercice.getQuestion() +
                            " Type: " + exercice.getActivite().getType() +
                            " Status: " + exercice.getActivite().getStatus() +
                            " Type Match: " + typeMatch +
                            " Status Match: " + statusMatch);

                    return typeMatch && statusMatch;
                })
                .collect(Collectors.toList());

        System.out.println("Filtered activities: " + filteredActivites.size());
        System.out.println("Filtered exercises: " + filteredExercices.size());

        // Clear and repopulate the grid with filtered items
        contentGrid.getChildren().clear();
        int column = 0;
        int row = 0;
        int maxColumns = 3;

        // Add filtered activities
        for (Activite activite : filteredActivites) {
            VBox card = createActiviteCard(activite);
            contentGrid.add(card, column, row);

            column++;
            if (column >= maxColumns) {
                column = 0;
                row++;
            }
        }

        // Add filtered exercises
        for (Exercice exercice : filteredExercices) {
            VBox card = createExerciseCard(exercice);
            contentGrid.add(card, column, row);

            column++;
            if (column >= maxColumns) {
                column = 0;
                row++;
            }
        }

        // Show message if no items match the filter
        if (filteredActivites.isEmpty() && filteredExercices.isEmpty()) {
            statusLabel.setText("Aucun élément ne correspond aux filtres sélectionnés.");
            statusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-padding: 10;");
        } else {
            statusLabel.setText("");
        }
    }

    private void speakQuestion(String text) {
        try {
            // Use ProcessBuilder instead of Runtime.exec()
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "powershell",
                    "-Command",
                    "Add-Type -AssemblyName System.Speech; (New-Object System.Speech.Synthesis.SpeechSynthesizer).Speak('" + text.replace("'", "''") + "')"
            );
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Erreur lors de la lecture: " + e.getMessage());
        }
    }

    private void speakDescription(String text) {
        try {
            // Use ProcessBuilder instead of Runtime.exec()
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "powershell",
                    "-Command",
                    "Add-Type -AssemblyName System.Speech; (New-Object System.Speech.Synthesis.SpeechSynthesizer).Speak('" + text.replace("'", "''") + "')"
            );
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Erreur lors de la lecture: " + e.getMessage());
        }
    }

    private void openAnswerWindow(Exercice exercice) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/pidev/ajouter_reponse.fxml"));
            Parent root = loader.load();

            AjouterReponseController controller = loader.getController();
            controller.setExercice(exercice);

            Stage stage = new Stage();
            stage.setTitle("Ajouter Réponse");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openResponsesWindow(Exercice exercice) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/pidev/afficher_reponses.fxml"));
            Parent root = loader.load();

            ListeReponsesController controller = loader.getController();
            controller.setExercice(exercice);

            Stage stage = new Stage();
            stage.setTitle("Réponses de l'exercice");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}