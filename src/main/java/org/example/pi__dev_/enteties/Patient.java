package org.example.pi__dev_.enteties;

import java.util.ArrayList;
import java.util.List;

public class Patient {
    private int id;
    private String dossier_medical;
    private String firstName;
    private String lastName;
    private List<Consultation> consultations = new ArrayList<>();
    private List<RDV> rdvs = new ArrayList<>();


    public Patient(int id, String dossier_medical, String firstName, String lastName) {
        this.id = id;
        this.dossier_medical = dossier_medical;
        this.firstName = firstName;
        this.lastName = lastName;

    }


    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getFirstName(){ return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getDossier_medical() { return dossier_medical; }
    public void setDossier_medical(String dossier_medical) {
        this.dossier_medical = dossier_medical;
    }

    public void ajouterRendezVous(RDV rdv) {
            if (rdv == null) {
                throw new IllegalArgumentException("RDV cannot be null");
            }

            if (this.rdvs == null) {
                this.rdvs = new ArrayList<>();
            }

            if (!this.rdvs.contains(rdv)) {
                this.rdvs.add(rdv);
                rdv.setPatient(this);
            }
        }
    }

