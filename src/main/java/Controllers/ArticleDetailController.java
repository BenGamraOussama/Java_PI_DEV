package Controllers;

import Entities.Article;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ArticleDetailController {

    @FXML private VBox mediaContainer;
    @FXML private Label title;
    @FXML private Label categoryLabel;
    @FXML private Label publishedAt;
    @FXML private TextArea content;
    @FXML private Button translateButton; // Add this button in your FXML
    @FXML private ComboBox<String> languageComboBox; // Add this to your FXML

    private boolean isTranslated = false;
    private String originalDesription;
    private String originalTitle;

    @FXML private Button closeButton;

    private Article article;

    // Supported languages map (code -> display name)
    private final Map<String, String> supportedLanguages = new LinkedHashMap<String,String>() {{
        put("fr", "French");
        put("de", "German");
        put("it", "Italian");
        put("pt", "Portuguese");

    }};
    @FXML
    public void initialize() {
        // Set up language selection dropdown
        ObservableList<String> languageOptions = FXCollections.observableArrayList(supportedLanguages.values());
        languageComboBox.setItems(languageOptions);
        languageComboBox.getSelectionModel().selectFirst(); // Select first language by default
    }

    public void setArticle(Article article) {
        this.article = article;
        originalDesription = article.getContent(); // Store original text
        originalTitle = article.getTitle();
        title.setText(originalTitle);

        content.setText(originalDesription);
        publishedAt.setText("Published: " + article.getPublishedAt().toString());
        categoryLabel.setText("Category ID: " + article.getCategoryId());

        loadMedia(article.getMediaPath());
    }

    private void loadMedia(String mediaPath) {
        mediaContainer.getChildren().clear();

        if (mediaPath == null || mediaPath.isEmpty()) return;

        String lower = mediaPath.toLowerCase();

        try {
            if (lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".png")) {
                Image image = new Image("file:" + mediaPath);
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(800);
                imageView.setPreserveRatio(true);
                imageView.getStyleClass().add("fullpage-image");
                mediaContainer.getChildren().add(imageView);

            } else if (lower.endsWith(".mp4") || lower.endsWith(".mov")) {
                Media media = new Media(new File(mediaPath).toURI().toString());
                MediaPlayer mediaPlayer = new MediaPlayer(media);
                MediaView mediaView = new MediaView(mediaPlayer);
                mediaView.setFitWidth(800);
                mediaView.setPreserveRatio(true);
                mediaContainer.getChildren().add(mediaView);
                mediaPlayer.play();

            } else if (lower.endsWith(".pdf")) {
                Hyperlink link = new Hyperlink("Open PDF");
                link.setOnAction(e -> {
                    try {
                        Desktop.getDesktop().open(new File(mediaPath));
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });
                mediaContainer.getChildren().add(link);
            } else {
                Label unsupported = new Label("Unsupported media type.");
                mediaContainer.getChildren().add(unsupported);
            }

        } catch (Exception e) {
            Label errorLabel = new Label("Error loading media.");
            mediaContainer.getChildren().add(errorLabel);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
    @FXML
    private void handleTranslate() {
        try {
            if (!isTranslated) {
                // Get selected language code
                String selectedLanguageName = languageComboBox.getValue();
                String targetLang = getLanguageCode(selectedLanguageName);

                // Translate to selected language

                String translatedContent = translateWithMyMemory(content.getText(), "en", targetLang);
                String translatedTitle = translateWithMyMemory(title.getText(), "en", targetLang);
                title.setText(translatedTitle);

                content.setText(translatedContent);
                translateButton.setText("Show Original");
                isTranslated = true;
            } else {
                // Show original text
                content.setText(originalDesription);
                title.setText(originalTitle);

                translateButton.setText("Translate");
                isTranslated = false;
            }
        } catch (IOException e) {
            content.setText("Translation failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Helper method to get language code from display name
    private String getLanguageCode(String displayName) {
        return supportedLanguages.entrySet().stream()
                .filter(entry -> entry.getValue().equals(displayName))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse("fr"); // Default to French if not found
    }

    private String translateWithMyMemory(String text, String sourceLang, String targetLang) throws IOException {
        if (text == null || text.trim().isEmpty()) {
            return text;
        }

        String urlStr = "https://api.mymemory.translated.net/get?" +
                "q=" + URLEncoder.encode(text, StandardCharsets.UTF_8) +
                "&langpair=" + sourceLang + "|" + targetLang;

        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(10000);

        int responseCode = conn.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new IOException("HTTP error code: " + responseCode);
        }

        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {

            String jsonResponse = in.lines().collect(Collectors.joining());

            // Simple JSON parsing - consider using a library like Gson for production
            int start = jsonResponse.indexOf("\"translatedText\":\"") + 18;
            int end = jsonResponse.indexOf("\"", start);

            if (start == -1 || end == -1) {
                throw new IOException("Invalid response format");
            }

            return jsonResponse.substring(start, end)
                    .replace("\\\"", "\"")
                    .replace("\\n", "\n");
        }
    }


}
