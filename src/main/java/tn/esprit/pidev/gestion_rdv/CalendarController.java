package tn.esprit.pidev.gestion_rdv;


import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import tn.esprit.pidev.gestion_rdv.enteties.RDV;
import tn.esprit.pidev.gestion_rdv.services.RDVservice;
import tn.esprit.pidev.Database.Database;

import java.sql.SQLException;

public class CalendarController {

    @FXML private FlowPane calendar;
    @FXML private Text year;
    @FXML private Text month;
    private LocalDate currentlyShownDate;
    private final RDVservice rdVservice = new RDVservice(Database.getConnection());
    private List<RDV> rdvs;
    public CalendarController() throws SQLException {
    }

    @FXML
    public void initialize() {
        try {
            currentlyShownDate = LocalDate.now();

            // Load CSS if it exists
            String cssResource = getClass().getResource("/tn/esprit/pidev/gestion_rdv/Calendar.css").toExternalForm();
            calendar.getStylesheets().add(cssResource);

            this.rdvs = rdVservice.readList();
            System.out.println("Number of consultations: " + rdvs.size());

            updateCalendar();
        } catch (Exception e) {
            showError("Erreur d'initialisation", "Échec du chargement du calendrier");
            e.printStackTrace();
        }
    }

    private void updateCalendar() {
        try {
            // Clear existing calendar cells
            calendar.getChildren().clear();

            if (currentlyShownDate == null) {
                currentlyShownDate = LocalDate.now();
            }

            // Update year and month display
            year.setText(String.valueOf(currentlyShownDate.getYear()));
            String monthName = currentlyShownDate.getMonth().getDisplayName(TextStyle.FULL, Locale.FRENCH);
            month.setText(monthName.substring(0, 1).toUpperCase() + monthName.substring(1).toLowerCase());

            // Get all reservations
            System.out.println("[DEBUG] Total reservations loaded: " + rdvs.size());

            // Add day headers (replacing the FXML static headers)
            calendar.getChildren().clear(); // Clear everything including headers
            String[] dayHeaders = {"Dim", "Lun", "Mar", "Mer", "Jeu", "Ven", "Sam"};
            for (String day : dayHeaders) {
                Label header = new Label(day);
                header.getStyleClass().add("calendar-header");
                calendar.getChildren().add(header);
            }

            // Calculate first day of month and total days
            LocalDate firstDayOfMonth = currentlyShownDate.withDayOfMonth(1);
            int daysInMonth = currentlyShownDate.lengthOfMonth();

            // Get day of week for first day (0=Sunday, 1=Monday, etc. in our UI)
            int dayOfWeek = firstDayOfMonth.getDayOfWeek().getValue();
            // Adjust for Sunday as first day in the UI
            dayOfWeek = dayOfWeek == 7 ? 0 : dayOfWeek;

            // Add empty cells for days before the 1st of the month
            for (int i = 0; i < dayOfWeek; i++) {
                StackPane emptyCell = new StackPane();
                emptyCell.getStyleClass().add("calendar-cell-empty");
                calendar.getChildren().add(emptyCell);
            }

            // Create cells for each day of the month
            for (int day = 1; day <= daysInMonth; day++) {
                LocalDate cellDate = firstDayOfMonth.withDayOfMonth(day);

                // Filter reservations for this specific date
                int year = cellDate.getYear();
                int month = cellDate.getMonthValue();
                int dayOfMonth = cellDate.getDayOfMonth();

                List<RDV> rdvsForDay = rdvs.stream()
                        .filter(r -> {
                            if (r.getDate() == null) return false;
                            LocalDate date = r.getDate().toLocalDate();
                            return date.getYear() == year &&
                                    date.getMonthValue() == month &&
                                    date.getDayOfMonth() == dayOfMonth;
                        })
                        .collect(Collectors.toList());

                // Create and add the calendar cell
                StackPane cell = createCalendarCell(cellDate, rdvsForDay);
                calendar.getChildren().add(cell);
            }
        } catch (Exception e) {
            showError("Erreur de calendrier", "Échec de la mise à jour du calendrier");
            e.printStackTrace();
        }
    }

    private StackPane createCalendarCell(LocalDate date, List<RDV> rdvs) {
        StackPane cell = new StackPane();
        cell.getStyleClass().add("calendar-cell");
        cell.setMinHeight(70);
        cell.setMinWidth(95);

        VBox vbox = new VBox();
        vbox.setSpacing(2);
        vbox.setMaxWidth(Double.MAX_VALUE);

        Label dateLabel = new Label(String.valueOf(date.getDayOfMonth()));
        dateLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #333333;");
        vbox.getChildren().add(dateLabel);

        if (rdvs != null && !rdvs.isEmpty()) {
            for (RDV r : rdvs) {
                String patientName = r.getPatient().getLastName();
                if (r.getPatient().getFirstName() != null && !r.getPatient().getFirstName().isEmpty()) {
                    patientName += " " + r.getPatient().getFirstName().charAt(0) + ".";
                }

                Label consultLabel = new Label(patientName);
                consultLabel.getStyleClass().add("calendar-reservation");
                consultLabel.setMaxWidth(Double.MAX_VALUE);
                consultLabel.setStyle("-fx-background-color: #e6f7ff; -fx-padding: 2px 4px; " +
                        "-fx-background-radius: 3px; -fx-font-size: 10px;");
                vbox.getChildren().add(consultLabel);
            }
        }

        cell.getChildren().add(vbox);

        if (date.equals(LocalDate.now())) {
            cell.setStyle("-fx-background-color: #f0f7ff; -fx-border-color: #0078d7; -fx-border-width: 2px;");
        }

        return cell;
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void backOneMonth() {
        currentlyShownDate = currentlyShownDate.minusMonths(1);
        updateCalendar();
    }

    @FXML
    private void forwardOneMonth() {
        currentlyShownDate = currentlyShownDate.plusMonths(1);
        updateCalendar();
    }

    public void showAsFullScreen(Stage stage) {
        stage.setFullScreen(true);
    }
}
