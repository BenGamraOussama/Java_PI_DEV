package tn.esprit.pidev.gestion_rdv.enteties;


import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

public class WebCalendar {
    private WebView webView;
    private WebEngine webEngine;
    private JSObject jsConnector;

    public WebCalendar() {
        webView = new WebView();
        webEngine = webView.getEngine();

        // Configuration de base
        webView.setPrefSize(800, 600);

        // Chargement du contenu HTML
        loadCalendarContent();
    }

    private void loadCalendarContent() {
        String htmlContent = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset='utf-8' />
                <link href='https://cdn.jsdelivr.net/npm/fullcalendar@5.10.1/main.min.css' rel='stylesheet' />
                <script src='https://cdn.jsdelivr.net/npm/fullcalendar@5.10.1/main.min.js'></script>
                <script src='https://cdn.jsdelivr.net/npm/fullcalendar@5.10.1/locales/fr.js'></script>
                <style>
                    body { margin: 0; padding: 0; font-family: Arial; }
                    #calendar { max-width: 1100px; margin: 0 auto; }
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
                            },
                            eventDrop: function(info) {
                                window.jsConnector.handleEventDrop(
                                    info.event.id, 
                                    info.event.start.toISOString(),
                                    info.event.end ? info.event.end.toISOString() : null
                                );
                            }
                        });
                        calendar.render();
                        
                        // Fonction pour ajouter des événements depuis Java
                        window.addEvent = function(event) {
                            calendar.addEvent(JSON.parse(event));
                        };
                        
                        // Fonction pour supprimer des événements depuis Java
                        window.removeEvent = function(eventId) {
                            var event = calendar.getEventById(eventId);
                            if (event) event.remove();
                        };
                    });
                </script>
            </body>
            </html>
            """;

        webEngine.loadContent(htmlContent);
    }

    public WebView getView() {
        return webView;
    }
}
