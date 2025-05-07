package tn.esprit.pidev.gestion_rdv.enteties;

import java.sql.Time;
import java.util.Date;


public class RDV {
    private int id;
    private Time heure;
    private Date date; // Correction : Utilisation de java.sql.Date
    private String priorite;
    private Patient patient;
    private Psychiatre psychiatre;
    private Etat etat = Etat.EN_ATTENTE; // Default status is pending
    public RDV(Time heure, Date date, String priorite) {
        this.heure = heure;
        this.date = date;
        this.priorite = priorite;
        this.etat = Etat.EN_ATTENTE; // Default status is pending
    }

    public RDV(Time heure, Date date, String priorite, Etat etat) {
        this.heure = heure;
        this.date = date;
        this.priorite = priorite;
        this.etat = etat;
    }

    public RDV() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Time getHeure() {
        return heure;
    }

    public void setHeure(Time heure) {
        this.heure = heure;
    }

    public java.sql.Date getDate() { // Retourne java.sql.Date
        if (date instanceof java.sql.Date) {
            return (java.sql.Date) date;
        } else if (date != null) {
            return new java.sql.Date(date.getTime());
        }
        return null;
    }

    public void setDate(Date date) { // Correction du setter
        this.date = date;
    }

    public String getPriorite() {
        return priorite;
    }
    public void setPriorite(String priorite) {
        this.priorite = priorite;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Psychiatre getPsychiatre() {
        return psychiatre;
    }

    public void setPsychiatre(Psychiatre psychiatre) {
        this.psychiatre = psychiatre;
    }

    public Etat getEtat() {
        return etat;
    }

    public void setEtat(Etat etat) {
        this.etat = etat;
    }

    @Override
    public String toString() {
        return "RDV{" + "heure=" + heure + ", date=" + date + ", priorite=" + priorite + ", etat=" + etat + '}';
    }
}
