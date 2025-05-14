package tn.esprit.pidev.gestion_rdv.Model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Consultation {
    private int id;
    private LocalDate date;
    private LocalTime heure;
    private double prix;
    private String modeConsultation;
    private String etatEnum;
    private int patientId;
    private String meetLink;

    // Constructors
    public Consultation() {
    }

    public Consultation(LocalDate date, LocalTime heure, double prix, String modeConsultation,
                        String etatEnum, int patientId, String meetLink) {
        this.date = date;
        this.heure = heure;
        this.prix = prix;
        this.modeConsultation = modeConsultation;
        this.etatEnum = etatEnum;
        this.patientId = patientId;
        this.meetLink = meetLink;
    }

    public Consultation(int id, LocalDate date, LocalTime heure, double prix, String modeConsultation,
                        String etatEnum, int patientId, String meetLink) {
        this.id = id;
        this.date = date;
        this.heure = heure;
        this.prix = prix;
        this.modeConsultation = modeConsultation;
        this.etatEnum = etatEnum;
        this.patientId = patientId;
        this.meetLink = meetLink;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getHeure() {
        return heure;
    }

    public void setHeure(LocalTime heure) {
        this.heure = heure;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public String getModeConsultation() {
        return modeConsultation;
    }

    public void setModeConsultation(String modeConsultation) {
        this.modeConsultation = modeConsultation;
    }

    public String getEtatEnum() {
        return etatEnum;
    }

    public void setEtatEnum(String etatEnum) {
        this.etatEnum = etatEnum;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public String getMeetLink() {
        return meetLink;
    }

    public void setMeetLink(String meetLink) {
        this.meetLink = meetLink;
    }

    @Override
    public String toString() {
        return "Consultation{" +
                "id=" + id +
                ", date=" + date +
                ", heure=" + heure +
                ", prix=" + prix +
                ", modeConsultation='" + modeConsultation + '\'' +
                ", etatEnum='" + etatEnum + '\'' +
                ", patientId=" + patientId +
                ", meetLink='" + meetLink + '\'' +
                '}';
    }
}
