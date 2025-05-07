package tn.esprit.pidev.gestion_rdv.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import tn.esprit.pidev.gestion_rdv.enteties.Consultation;
import tn.esprit.pidev.gestion_rdv.enteties.TodoTask;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class TodoistAPI {
    // API token should ideally be loaded from a configuration file or environment variable
    private static final String TOKEN = "e627a0d249744827203581b7a3f4f09b6e616891";
    private static final String API_BASE_URL = "https://api.todoist.com/rest/v2";
    private final HttpClient client;
    private final Gson gson;

    public TodoistAPI() {
        this.client = HttpClient.newHttpClient();
        // Configure Gson to access private fields during serialization/deserialization
        this.gson = new GsonBuilder()
                .serializeNulls()
                .setLenient()
                .create();
    }

    /**
     * Get all tasks from Todoist
     * @return List of TodoTask objects
     * @throws IOException If an I/O error occurs
     * @throws InterruptedException If the operation is interrupted
     */
    public List<TodoTask> getTasks() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_BASE_URL + "/tasks"))
                .header("Authorization", "Bearer " + TOKEN)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            System.err.println("Error fetching tasks: " + response.statusCode() + " - " + response.body());
            return Collections.emptyList();
        }

        // Deserialize JSON response into list of TodoTask objects
        return gson.fromJson(response.body(), new TypeToken<List<TodoTask>>() {}.getType());
    }

    /**
     * Create a new task in Todoist based on a Consultation
     * @param consultation The consultation to create a task for
     * @return true if the task was created successfully
     * @throws IOException If an I/O error occurs
     * @throws InterruptedException If the operation is interrupted
     */
    public boolean createConsultationTask(Consultation consultation) throws IOException, InterruptedException {
        // Format the date for the due date
        LocalDate consultationDate = consultation.getDate().toLocalDate();
        String dueDate = consultationDate.format(DateTimeFormatter.ISO_DATE);

        // Create task payload
        TaskPayload payload = new TaskPayload(
                consultation.getId() + " - Consultation le " + consultation.getDate(),
                "Motif: " + consultation.getModeconsultation(),
                dueDate
        );

        String jsonBody = gson.toJson(payload);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_BASE_URL + "/tasks"))
                .header("Authorization", "Bearer " + TOKEN)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            System.err.println("Failed to create task: " + response.statusCode() + " - " + response.body());
            return false;
        }

        System.out.println("Successfully created task: " + response.body());
        return true;
    }




    // Helper class for task payload
    static class TaskPayload {
        private String content;
        private String description;
        private String due_date;

        public TaskPayload(String content, String description, String due_date) {
            this.content = content;
            this.description = description;
            this.due_date = due_date;
        }

        // For backward compatibility
        public TaskPayload(String content, String description) {
            this.content = content;
            this.description = description;
            // No due date
        }
    }
}
