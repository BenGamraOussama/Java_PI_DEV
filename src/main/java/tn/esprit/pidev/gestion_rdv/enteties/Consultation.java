package tn.esprit.pidev.gestion_rdv.enteties;



import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.util.Objects;

public class Consultation {
    private int id;
    private Date date;
    private Time heure;
    private double prix;
    private String modeconsultation;
    private Etat etatenum;
    private Patient patient;  // Référence directe au patient
    private String meet_link;


    public Consultation(Date date, Time heure, double prix,
                        String modeconsultation, Etat etatenum,Patient patient, String zoomLink) {
        this.date = Objects.requireNonNull(date, "Date cannot be null");
        this.heure = Objects.requireNonNull(heure, "Time cannot be null");
        this.etatenum = Objects.requireNonNull(etatenum, "Etat cannot be null");

        if (prix < 0) throw new IllegalArgumentException("Price cannot be negative");

        this.prix = prix;
        this.modeconsultation = modeconsultation != null ? modeconsultation : "";
        this.patient = patient;
        this.meet_link = meet_link != null ? meet_link : "";
    }

    public Consultation() {

    }

    public Consultation(Date date, Time time, double v, String text, Etat value, String meet_link, Patient patient) {
    }

    public LocalDate getLocalDate() {
        return this.date != null ? this.date.toLocalDate() : null;
    }

    // Safer version that handles null dates
    public LocalDate getLocalDateSafe() {
        if (this.date == null) {
            throw new IllegalStateException("Date is not set");
        }
        return this.date.toLocalDate();
    }

    // Getters and Setters with proper null checks
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = Objects.requireNonNull(date, "Date cannot be null");
    }

    public Time getHeure() {
        return heure;
    }

    public void setHeure(Time heure) {
        this.heure = Objects.requireNonNull(heure, "Time cannot be null");
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        if (prix < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        this.prix = prix;
    }

    public String getModeconsultation() {
        return modeconsultation;
    }

    public void setModeconsultation(String modeconsultation) {
        this.modeconsultation = modeconsultation != null ? modeconsultation : "";
    }

    public Etat getEtatenum() {
        return etatenum;
    }

    public void setEtatenum(Etat etatenum) {
        this.etatenum = Objects.requireNonNull(etatenum, "Etat cannot be null");
    }


    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public String getMeetLink() {
        return meet_link;
    }

    public void setMeetLink(String zoomLink) {
        this.meet_link = meet_link != null ? zoomLink : "";
    }


    @Override
    public String toString() {
        return "Consultation{" +
                "id=" + id +
                ", date=" + (date != null ? date.toString() : "null") +
                ", heure=" + (heure != null ? heure.toString() : "null") +
                ", prix=" + prix +
                ", modeconsultation='" + modeconsultation + '\'' +
                ", patient_id=" + patient +
                ", etatenum=" + etatenum +
                ", zoomLink='" + meet_link + '\'' +
                '}';
    }

    public Patient getPatient() {
        return patient;
    }
}
