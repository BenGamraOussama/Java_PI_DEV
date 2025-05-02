package org.example.pi__dev_.exceptions;


import java.sql.Date;
import java.sql.Time;

public class ConsultationNotFoundException extends Exception {
    public ConsultationNotFoundException() {
        super("Consultation not found");
    }

    public ConsultationNotFoundException(int consultationId) {
        super(String.format("Consultation with ID %d not found", consultationId));
    }

    public ConsultationNotFoundException(String message) {
        super(message);
    }

    public ConsultationNotFoundException(Date date, Time heure) {
        super(String.format("No consultation found scheduled at %s on %s",
                heure.toString(),
                date.toString()));
    }
}
