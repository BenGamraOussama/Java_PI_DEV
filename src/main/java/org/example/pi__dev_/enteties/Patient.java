package org.example.pi__dev_.enteties;

public class Patient {
    private int id;
    private String dossier_medical;

    // Constructors
    public Patient() {}

    public Patient(int id, String dossier_medical) {
        this.id = id;
        this.dossier_medical = dossier_medical;
    }

    public Patient(int id) {
        this.id = id;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDossier_medical() { return dossier_medical; }
    public void setDossier_medical(String dossier_medical) {
        this.dossier_medical = dossier_medical;
    }
}