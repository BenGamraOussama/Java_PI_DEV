package tn.esprit.pidev.gestion_rdv;


import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.scene.web.WebEngine;
import netscape.javascript.JSObject;

public class CalendarConnector {
    private final WebEngine webEngine;

    public CalendarConnector(WebEngine webEngine) {
        this.webEngine = webEngine;
        setupBridge();
    }

    private void setupBridge() {
        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                JSObject window = (JSObject) webEngine.executeScript("window");
                window.setMember("jsConnector", this);
            }
        });
    }

    // Called from JavaScript when a date is clicked
    public void handleDateClick(String dateStr) {
        Platform.runLater(() -> {
            System.out.println("Date selected: " + dateStr);
            // Here you would typically open a dialog to create a new appointment
        });
    }

    // Called from JavaScript when an event is clicked
    public void handleEventClick(String eventId) {
        Platform.runLater(() -> {
            System.out.println("Appointment clicked: " + eventId);
            // Here you would typically show appointment details
        });
    }
}