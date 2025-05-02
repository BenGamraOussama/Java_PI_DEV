package org.example.pi__dev_.exceptions;


import org.example.pi__dev_.enteties.Patient;

public class PatientNotFoundException extends Exception {
    public PatientNotFoundException() {
        super("Patient not found");
    }

    public PatientNotFoundException(int patientId) {
        super(String.format("Patient with ID %d not found", patientId));
    }

    public PatientNotFoundException(String firstName, String lastName) {
        super(String.format("Patient %s %s not found", firstName, lastName));
    }

    public PatientNotFoundException(Patient patient) {
        this(patient.getId());
    }
}