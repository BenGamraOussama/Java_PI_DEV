package tn.esprit.pidev;

import javafx.beans.property.SimpleStringProperty;

public class Appointment {
    private final SimpleStringProperty appointmentId;
    private final SimpleStringProperty patientName;
    private final SimpleStringProperty appointmentDate;

    public Appointment(String id, String name, String date) {
        this.appointmentId = new SimpleStringProperty(id);
        this.patientName = new SimpleStringProperty(name);
        this.appointmentDate = new SimpleStringProperty(date);
    }

    public String getAppointmentId() { return appointmentId.get(); }
    public String getPatientName() { return patientName.get(); }
    public String getAppointmentDate() { return appointmentDate.get(); }
}