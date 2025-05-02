package org.example.pi__dev_.enteties;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class RDV {
    private int id;
    private Time heure;
    private Date date; // Correction : Utilisation de java.sql.Date
    private String priorite;
    private Patient patient;
    private Psychiatre psychiatre;
    public RDV(Date date,Time heure, String priorite) {
        this.date = date;
        this.heure = heure;
        this.priorite = priorite;
    }
    // Relation One-to-Many avec RDV
    private List<RDV> rdvs = new ArrayList<>();

    public RDV() {

    }

    public RDV(Time heure, Date date, String priorite) {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public java.sql.Date getDate() {
        return (java.sql.Date) date;
    }
    public void setDate(java.sql.Date date){
        this.date = date;
    }

    public Time getHeure() {
        return heure;
    }

    public void setHeure(java.sql.Time heure){
        this.heure = heure;
    }


    public String getPriorite() {
        return priorite;
    }
    public void setPriorite(String priorite) {
        this.priorite = priorite;
    }
    public List<RDV> getRdv() { return rdvs; }
    public void setAppointments(List<RDV> appointments) { this.rdvs = rdvs; }

    @Override
    public String toString() {
        return "RDV{" + "heure=" + heure + ", date=" + date + " priorite" + priorite + '}';
    }

    public Object getPsychiatre() {
        return psychiatre;
    }

    public void setPsychiatre(Psychiatre psychiatre) {
        this.psychiatre = psychiatre;
    }
    public Patient getPatient() {
        return patient;
    }
    public void setPatient(Patient patient) {
        this.patient = patient;
    }
}