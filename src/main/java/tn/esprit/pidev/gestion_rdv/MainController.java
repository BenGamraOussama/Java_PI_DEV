package tn.esprit.pidev.gestion_rdv;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebView;

import java.io.IOException;

public class MainController {

    private WebView calendarWebView;
    private CalendarConnector calendarConnector;

    @FXML
    private TabPane mainTabPane;

    // Initialisation si nécessaire
    public void initialize() {
        // Configuration supplémentaire peut être ajoutée ici
    }

    @FXML
    private BorderPane mainPane; // Maintenant correctement injecté grâce au fx:id

    @FXML
    private void showConsultationView() {
        loadView("/tn/esprit/pidev/gestion_rdv/ConsultationView.fxml");
    }

    @FXML
    private void showRendezVousView() {
        loadView("/tn/esprit/pidev/gestion_rdv/RendezVousView.fxml");
    }

    @FXML
    private void showTraitementView() {
        loadView("/tn/esprit/pidev/gestion_rdv/TraitementView.fxml");
    }

    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            mainPane.setCenter(view);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la vue: " + fxmlPath);
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    public void initializeCalendar() {
        try {
            // Initialize WebView and connector
            calendarWebView = new WebView();
            calendarConnector = new CalendarConnector(calendarWebView.getEngine());

            // Load HTML content from external file (recommended)
            String htmlContent = loadCalendarHTML();
            calendarWebView.getEngine().loadContent(htmlContent);

            // Set WebView properties
            calendarWebView.setPrefSize(1200, 800);
        } catch (Exception e) {
            showAlert("Calendar Error", "Failed to initialize calendar: " + e.getMessage());
        }
    }

    private String loadCalendarHTML() {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset='utf-8' />
                <link href='https://cdn.jsdelivr.net/npm/fullcalendar@5.10.1/main.min.css' rel='stylesheet' />
                <script src='https://cdn.jsdelivr.net/npm/fullcalendar@5.10.1/main.min.js'></script>
                <script src='https://cdn.jsdelivr.net/npm/fullcalendar@5.10.1/locales/fr.js'></script>
                <style>
                    body { margin: 0; padding: 0; font-family: Arial; }
                    #calendar { max-width: 100%; margin: 20px auto; }
                </style>
            </head>
            <body>
                <div id='calendar'></div>
                <script>
                    document.addEventListener('DOMContentLoaded', function() {
                        var calendarEl = document.getElementById('calendar');
                        var calendar = new FullCalendar.Calendar(calendarEl, {
                            locale: 'fr',
                            initialView: 'dayGridMonth',
                            headerToolbar: {
                                left: 'prev,next today',
                                center: 'title',
                                right: 'dayGridMonth,timeGridWeek,timeGridDay'
                            },
                            editable: true,
                            selectable: true,
                            eventClick: function(info) {
                                window.jsConnector.handleEventClick(info.event.id);
                            },
                            dateClick: function(info) {
                                window.jsConnector.handleDateClick(info.dateStr);
                            }
                        });
                        calendar.render();
                    });
                </script>
            </body>
            </html>
            """;
    }

}