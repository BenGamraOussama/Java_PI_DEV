package org.example.pi__dev_.enteties;

import java.util.ArrayList;
import java.util.List;

public class Psychiatre {
    private int id;
    private String specialite;
    private String firstName;
    private String lastName;
    private List<Consultation> consultations = new ArrayList<>();

    public Psychiatre(int id, String specialite, String firstName, String lastName) {
        this.id = id;
        this.specialite = specialite;
        this.firstName = firstName;
        this.lastName = lastName;

    }
    public String getSpecialite() {
        return specialite;
    }

    public void setSpecialite(String specialite) {
        this.specialite = specialite;
    }
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    // Gestion des consultations

    public void addConsultation(Consultation consultation) {
        consultations.add(consultation);
        consultation.setPsychiatre(this);
    }
    @Override
    public String toString() {
        return "Psychiatre{" +
                "id=" + id +
                ", specialite='" + specialite + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}
