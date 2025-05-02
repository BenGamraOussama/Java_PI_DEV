package org.example.pi__dev_.exceptions;


import org.example.pi__dev_.enteties.Patient;
import org.example.pi__dev_.enteties.Psychiatre;

import java.sql.Date;
import java.sql.Time;

public class AppointmentConflictException extends Exception {
    public AppointmentConflictException() {
        super("The requested appointment time conflicts with an existing appointment");
    }

    public AppointmentConflictException(String message) {
        super(message);
    }

    public AppointmentConflictException(Psychiatre psychiatre, Date date, Time heure) {
        super(String.format("Dr. %s %s already has an appointment at %s on %s",
                psychiatre.getFirstName(),
                psychiatre.getLastName(),
                heure.toString(),
                date.toString()));
    }

    public AppointmentConflictException(Patient patient, Date date, Time heure) {
        super(String.format("Patient %s %s already has an appointment at %s on %s",
                patient.getFirstName(),
                patient.getLastName(),
                heure.toString(),
                date.toString()));
    }
}
