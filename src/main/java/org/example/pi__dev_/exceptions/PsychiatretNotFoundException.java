package org.example.pi__dev_.exceptions;


import org.example.pi__dev_.enteties.Psychiatre;

public class PsychiatretNotFoundException extends Exception {
    public PsychiatretNotFoundException() {
        super("Psychiatrist not found");
    }

    public PsychiatretNotFoundException(int psychiatristId) {
        super(String.format("Psychiatrist with ID %d not found", psychiatristId));
    }

    public PsychiatretNotFoundException(String firstName, String lastName) {
        super(String.format("Dr. %s %s not found", firstName, lastName));
    }

    public PsychiatretNotFoundException(Psychiatre psychiatrist) {
        this(psychiatrist.getId());
    }
}