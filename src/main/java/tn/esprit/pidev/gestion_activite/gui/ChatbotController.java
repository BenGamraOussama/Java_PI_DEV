package tn.esprit.pidev.gestion_activite.gui;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ChatbotController {

    @FXML private TextArea chatArea;
    @FXML private TextField userInput;
    @FXML private Button sendButton;

    private static final String API_KEY = "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";

    @FXML
    private void sendMessage() {
        String userMessage = userInput.getText().trim();
        if (userMessage.isEmpty()) return;

        chatArea.appendText("Vous: " + userMessage + "\n");
        userInput.clear();
        chatArea.appendText("Bot: ...\n");

        new Thread(() -> {
            try {
                HttpClient client = HttpClient.newHttpClient();
                String json = """
                        {
                          "model": "deepseek/deepseek-r1:free",
                          "messages": [
                            { "role": "user", "content": "%s" }
                          ]
                        }
                        """.formatted(userMessage);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://openrouter.ai/api/v1/chat/completions"))
                        .header("Authorization", "Bearer " + API_KEY)
                        .header("Content-Type", "application/json")
                        .header("HTTP-Referer", "https://www.pii.com")
                        .header("X-Title", "PII")
                        .POST(HttpRequest.BodyPublishers.ofString(json))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                JsonObject jsonResponse = JsonParser.parseString(response.body()).getAsJsonObject();
                String botReply = jsonResponse.getAsJsonArray("choices")
                        .get(0).getAsJsonObject()
                        .getAsJsonObject("message")
                        .get("content").getAsString();

                Platform.runLater(() -> {
                    // Remplace "Bot: ..." par la vraie réponse
                    int lastIndex = chatArea.getText().lastIndexOf("Bot: ...");
                    if (lastIndex != -1) {
                        chatArea.replaceText(lastIndex, lastIndex + 8, "Bot: " + botReply + "\n");
                    } else {
                        chatArea.appendText("Bot: " + botReply + "\n");
                    }
                });

            } catch (Exception e) {
                Platform.runLater(() -> chatArea.appendText("Bot: Erreur - " + e.getMessage() + "\n"));
            }
        }).start();
    }
} 