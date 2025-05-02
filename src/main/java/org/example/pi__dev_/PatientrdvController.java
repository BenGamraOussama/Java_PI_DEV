/*
package org.example.pi__dev_;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.util.Callback;
import org.example.pi__dev_.enteties.Consultation;
import org.example.pi__dev_.enteties.Psychiatre;
import org.example.pi__dev_.enteties.RDV;
import org.example.pi__dev_.exceptions.AppointmentConflictException;
import org.example.pi__dev_.exceptions.ConsultationNotFoundException;
import org.example.pi__dev_.exceptions.PatientNotFoundException;
import org.example.pi__dev_.services.PatientRdvService;
import org.example.pi__dev_.services.PsychiatreService;

import java.awt.event.ActionEvent;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class PatientrdvController implements Initializable {

    // Services
    private final PatientRdvService RDVService = new PatientRdvService();
    private final PsychiatreService PsychiatreService = new PsychiatreService();
    // UI Components for New RDV
    @FXML private ComboBox<String> specialtyComboBox;
    @FXML private ComboBox<Psychiatre> PsychiatreComboBox;
    @FXML private DatePicker RDVDatePicker;
    @FXML private TilePane timeSlotsTilePane;
    @FXML private TextArea reasonTextArea;
    @FXML private ToggleGroup urgencyToggleGroup;

    // UI Components for My RDVs
    @FXML private TableView<RDV> RDVsTableView;
    @FXML private TableColumn<RDV, String> dateColumn;
    @FXML private TableColumn<RDV, String> timeColumn;
    @FXML private TableColumn<RDV, String> PsychiatreColumn;
    @FXML private TableColumn<RDV, String> specialtyColumn;
    @FXML private TableColumn<RDV, String> statusColumn;
    @FXML private TableColumn<RDV, Void> actionsColumn;
    @FXML private ComboBox<String> RDVFilterComboBox;
    @FXML private DatePicker dateRangeStartPicker;
    @FXML private DatePicker dateRangeEndPicker;

    // Other UI Components
    @FXML private Label welcomeLabel;
    @FXML private Label statusLabel;
    @FXML private Label lastSyncLabel;

    // Data
    private ObservableList<RDV> RDVsList = FXCollections.observableArrayList();
    private ObservableList<Psychiatre> PsychiatresList = FXCollections.observableArrayList();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupNewRDVTab();
        setupMyRDVsTab();
        loadInitialData();
    }

    private void setupNewRDVTab() {
        // Specialty ComboBox
        specialtyComboBox.getItems().addAll(
                "Cardiology", "Dermatology", "Endocrinology",
                "Gastroenterology", "Neurology", "Pediatrics"
        );

        specialtyComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                filterPsychiatresBySpecialty(newVal);
            }
        });

        // Psychiatre ComboBox
        PsychiatreComboBox.setCellFactory(param -> new ListCell<Psychiatre>() {
            @Override
            protected void updateItem(Psychiatre Psychiatre, boolean empty) {
                super.updateItem(Psychiatre, empty);
                if (empty || Psychiatre == null) {
                    setText(null);
                } else {
                    setText(Psychiatre.getFirstName() + " (" + Psychiatre.getLastName() + "(" + Psychiatre.getSpecialite() + ")");
                }
            }
        });

        PsychiatreComboBox.setButtonCell(new ListCell<Psychiatre>() {
            @Override
            protected void updateItem(Psychiatre Psychiatre, boolean empty) {
                super.updateItem(Psychiatre, empty);
                if (empty || Psychiatre == null) {
                    setText(null);
                } else {
                    setText(Psychiatre.getFirstName() + " (" + Psychiatre.getLastName() +  "(" + Psychiatre.getSpecialite() + ")");
                }
            }
        });

        // Date Picker
        RDVDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();
                setDisable(date.isBefore(today) || date.isAfter(today.plusMonths(3)));
            }
        });

        RDVDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && PsychiatreComboBox.getValue() != null) {
                loadAvailableTimeSlots(PsychiatreComboBox.getValue(), newVal);
            }
        });
    }

    private void setupMyRDVsTab() {
        // Initialize Table Columns
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("RDVDate"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("RDVTime"));
        PsychiatreColumn.setCellValueFactory(cellData ->
                (ObservableValue<String>) cellData.getValue().getPsychiatre());
        specialtyColumn.setCellValueFactory(cellData ->
                (ObservableValue<String>) cellData.getValue().getPsychiatre());
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("priotite"));

        // Add action buttons to each row
        addActionButtonsToTable();

        // Initialize filter ComboBox
        RDVFilterComboBox.getItems().addAll(
                "All", "Upcoming", "Completed", "Cancelled", "Pending"
        );
        RDVFilterComboBox.getSelectionModel().selectFirst();
    }

    private void addActionButtonsToTable() {
        Callback<TableColumn<RDV, Void>, TableCell<RDV, Void>> cellFactory =
                new Callback<>() {
                    @Override
                    public TableCell<RDV, Void> call(final TableColumn<RDV, Void> param) {
                        return new TableCell<>() {
                            private final Button viewBtn = new Button("View");
                            private final Button cancelBtn = new Button("Cancel");
                            private final HBox pane = new HBox(viewBtn, cancelBtn);

                            {
                                pane.setSpacing(5);
                                viewBtn.getStyleClass().add("small-button");
                                cancelBtn.getStyleClass().add("small-button");

                                viewBtn.setOnAction(event -> {
                                    RDV RDV = getTableView().getItems().get(getIndex());
                                    viewRDVDetails(RDV);
                                });

                                cancelBtn.setOnAction(event -> {
                                    RDV RDV = getTableView().getItems().get(getIndex());
                                    cancelRDV(RDV);
                                });
                            }

                            @Override
                            public void updateItem(Void item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null);
                                } else {
                                    RDV RDV = getTableView().getItems().get(getIndex());
                                    // Only show cancel button for upcoming RDVs
                                    cancelBtn.setVisible(RDV.getPriorite().equals("En_ATTENTE"));
                                    setGraphic(pane);
                                }
                            }
                        };
                    }
                };

        actionsColumn.setCellFactory(cellFactory);
    }

    private void loadInitialData() throws SQLException {
        // Load Psychiatres
        PsychiatresList.setAll(PsychiatreService.getAllPsychiatres());

        // Load RDVs for current patient (in real app, you'd filter by patient ID)
        RDVsList.setAll(RDVService.getAllRDVs());
        RDVsTableView.setItems(RDVsList);

        // Set welcome message
        welcomeLabel.setText("Welcome, Patient Name");

        // Set last sync time
        lastSyncLabel.setText(LocalTime.now().format(timeFormatter));
    }

    private void filterPsychiatresBySpecialty(String specialty) {
        ObservableList<Psychiatre> filteredPsychiatres = FXCollections.observableArrayList();
        for (Psychiatre Psychiatre : PsychiatresList) {
            if (Psychiatre.getSpecialite().equals(specialty)) {
                filteredPsychiatres.add(Psychiatre);
            }
        }
        PsychiatreComboBox.setItems(filteredPsychiatres);
    }

    private void loadAvailableTimeSlots(Psychiatre Psychiatre, LocalDate date) {
        timeSlotsTilePane.getChildren().clear();

        // In a real app, you'd fetch available slots from your service
        List<String> availableSlots = Arrays.asList(
                "08:00", "09:00", "10:00", "11:00",
                "13:00", "14:00", "15:00", "16:00"
        );

        for (String slot : availableSlots) {
            ToggleButton slotButton = new ToggleButton(slot);
            slotButton.getStyleClass().add("time-slot-button");
            slotButton.setUserData(slot);
            timeSlotsTilePane.getChildren().add(slotButton);
        }
    }

    // Action Handlers
    @FXML
    private void confirmRDV() {
        try {
            if (validateRDVForm()) {
                RDV newRDV = new RDV();

                // Set psychiatrist
                newRDV.setPsychiatre(PsychiatreComboBox.getValue());

                // Handle date conversion
                LocalDate localDate = RDVDatePicker.getValue();
                if (localDate != null) {
                    newRDV.setDate(java.sql.Date.valueOf(RDVDatePicker.getValue()));                }

                // Handle time conversion
                String timeStr = getSelectedTimeSlot();
                if (timeStr != null) {
                    // Ensure time format is HH:mm:ss
                    if (timeStr.split(":").length == 2) {
                        timeStr += ":00";
                    }
                    newRDV.setHeure(Time.valueOf(timeStr));
                }

                newRDV.setPriorite("Scheduled");

                // Call service
                boolean success = RDVService.bookRDV(newRDV);

                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "RDV booked successfully!");
                    resetRDVForm();
                    refreshRDVsList();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to book RDV.");
                }
            }
        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Format", "Please check date/time format");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }
    private ObservableList<RDV> convertConsultationsToRDVs(List<Consultation> consultations) {
        ObservableList<RDV> rdvList = FXCollections.observableArrayList();

        for (Consultation consultation : consultations) {
            RDV rdv = new RDV(
                    consultation.getDate(),       // java.sql.Date
                    consultation.getHeure(),     // java.sql.Time
                    consultation.getEtatenum().toString() // Status as String
            );
            rdvList.add(rdv);
        }

        return rdvList;
    }

    @FXML
    private void cancelBooking() {
        resetRDVForm();
    }

    private void resetRDVForm() {
        specialtyComboBox.getSelectionModel().clearSelection();
        PsychiatreComboBox.getItems().clear();
        RDVDatePicker.setValue(null);
        timeSlotsTilePane.getChildren().clear();
        reasonTextArea.clear();
        urgencyToggleGroup.selectToggle(null);
    }

    @FXML
    private void filterRDVs() {
        String statusFilter = RDVFilterComboBox.getValue();
        LocalDate startDate = dateRangeStartPicker.getValue();
        LocalDate endDate = dateRangeEndPicker.getValue();

        ObservableList<RDV> filteredList = FXCollections.observableArrayList();
        for (RDV RDV : RDVsList) {
            boolean matchesStatus = statusFilter.equals("All") ||
                    RDV.getPriorite().equalsIgnoreCase(statusFilter);

            LocalDate apptDate = RDV.getDate().toLocalDate(); // conversion directe
            boolean matchesDate = true;

            if (startDate != null && endDate != null) {
                matchesDate = !apptDate.isBefore(startDate) && !apptDate.isAfter(endDate);
            } else if (startDate != null) {
                matchesDate = !apptDate.isBefore(startDate);
            } else if (endDate != null) {
                matchesDate = !apptDate.isAfter(endDate);
            }

            if (matchesStatus && matchesDate) {
                filteredList.add(RDV);
            }
        }

        RDVsTableView.setItems(filteredList);
    }

    @FXML
    private void clearFilters() {
        RDVFilterComboBox.getSelectionModel().selectFirst();
        dateRangeStartPicker.setValue(null);
        dateRangeEndPicker.setValue(null);
        RDVsTableView.setItems(RDVsList);
    }

    @FXML
    private void exportToPDF() {
        // Implement PDF export logic
        showAlert(Alert.AlertType.INFORMATION, "Export", "RDVs exported to PDF");
    }

    @FXML
    private void printRDVs() {
        // Implement print logic
        showAlert(Alert.AlertType.INFORMATION, "Print", "Printing RDVs...");
    }

    private void viewRDVDetails(RDV RDV) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("RDV Details");
        alert.setHeaderText("RDV with Dr. " + RDV.getPsychiatre());

        String content = String.format(
                "Date: %s\nTime: %s\nStatus: %s\nReason: %s\nUrgency: %s",
                RDV.getRdv(),
                RDV.getHeure(),
                RDV.getPriorite()
        );

        alert.setContentText(content);
        alert.showAndWait();
    }

    private void cancelRDV(RDV RDV) throws SQLException, ConsultationNotFoundException {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Cancellation");
        confirmation.setHeaderText("Cancel this RDV?");
        confirmation.setContentText("Are you sure you want to cancel your RDV with Dr. " +
                RDV.getPsychiatre() + " on " +
                RDV.getDate() + " at " +
                RDV.getHeure() + "?");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (RDVService.cancelRDV(RDV.getId())) {
                showAlert(Alert.AlertType.INFORMATION, "Cancelled", "RDV cancelled successfully.");
                refreshRDVsList();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to cancel RDV.");
            }
        }
    }

    private void refreshRDVsList() throws SQLException {
        RDVsList.setAll(RDVService.getAllRDVs());
    }

    // Helper Methods
    private boolean validateRDVForm() {
        if (specialtyComboBox.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please select a specialty");
            return false;
        }

        if (PsychiatreComboBox.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please select a Psychiatre");
            return false;
        }

        if (RDVDatePicker.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please select a date");
            return false;
        }

        if (getSelectedTimeSlot() == null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please select a time slot");
            return false;
        }

        if (reasonTextArea.getText().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter a reason for your visit");
            return false;
        }

        if (urgencyToggleGroup.getSelectedToggle() == null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please select an urgency level");
            return false;
        }

        return true;
    }

    private String getSelectedTimeSlot() {
        for (Node node : timeSlotsTilePane.getChildren()) {
            if (node instanceof ToggleButton) {
                ToggleButton button = (ToggleButton) node;
                if (button.isSelected()) {
                    return (String) button.getUserData();
                }
            }
        }
        return null;
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Navigation Methods
    @FXML
    private void showDashboard() {
        // Implement dashboard navigation
    }

    @FXML
    private void showNewRDV() {
        // Implement navigation to new RDV tab
    }

    @FXML
    private void showMyRDVs() {
        // Implement navigation to RDVs list tab
    }

    @FXML
    private void showMedicalHistory() {
        // Implement navigation to medical history
    }

    @FXML
    private void handleLogout() {
        // Implement logout logic
    }
    @FXML
    private void handleConfirmButton(ActionEvent event) {
        try {
            confirmRDV();
        } catch (AppointmentConflictException | SQLException | PatientNotFoundException e) {
            showAlert(Alert.AlertType.ERROR, "Booking Error", e.getMessage());
        }
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupNewRDVTab();
        setupMyRDVsTab();
        try {
            loadInitialData();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load initial data: " + e.getMessage());
        }
    }

}*/
