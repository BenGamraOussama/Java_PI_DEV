package tn.esprit.pidev.gestion_produit.services;

import okhttp3.*;
import org.json.JSONObject;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class TranslationService {
    private static TranslationService instance;
    private final OkHttpClient client;
    private static final String MYMEMORY_API_URL = "https://api.mymemory.translated.net/get";

    private TranslationService() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
                .build();
    }

    public static TranslationService getInstance() {
        if (instance == null) {
            instance = new TranslationService();
        }
        return instance;
    }

    public String translateText(String text, String targetLanguage) {
        if (text == null || text.trim().isEmpty()) {
            return text;
        }

        try {
            // Encode the text for URL
            String encodedText = URLEncoder.encode(text, StandardCharsets.UTF_8.toString());

            // Build the URL with parameters
            String url = String.format("%s?q=%s&langpair=fr|%s",
                    MYMEMORY_API_URL,
                    encodedText,
                    targetLanguage);

            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("Translation API returned error code: " + response.code());
                }

                String responseBody = response.body().string();
                JSONObject jsonResponse = new JSONObject(responseBody);

                // Check if the response contains an error
                if (jsonResponse.has("responseStatus") && jsonResponse.getInt("responseStatus") != 200) {
                    String errorMessage = jsonResponse.optString("responseDetails", "Unknown error");
                    throw new IOException("Translation API error: " + errorMessage);
                }

                // Get the translated text
                JSONObject responseData = jsonResponse.getJSONObject("responseData");
                String translatedText = responseData.getString("translatedText");

                // If translation failed or returned the same text, return original
                if (translatedText.equals(text) || translatedText.isEmpty()) {
                    return text;
                }

                return translatedText;
            }
        } catch (Exception e) {
            System.err.println("Error translating text: " + e.getMessage());
            // Show a more user-friendly error message
            showTranslationError(e.getMessage());
            return text; // Return original text if translation fails
        }
    }

    private void showTranslationError(String errorMessage) {
        javafx.application.Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Translation Error");
            alert.setHeaderText("Could not translate the text");
            alert.setContentText("The original text will be displayed instead.\nError: " + errorMessage);
            alert.showAndWait();
        });
    }

    // Common language codes
    public static final String ENGLISH = "en";
    public static final String FRENCH = "fr";
    public static final String ARABIC = "ar";
    public static final String SPANISH = "es";
    public static final String GERMAN = "de";
}