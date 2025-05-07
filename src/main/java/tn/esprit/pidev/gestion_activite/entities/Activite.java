package tn.esprit.pidev.gestion_activite.entities;

import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;
import java.util.ArrayList;

public class Activite {
    public int id;
    public StringProperty titre = new SimpleStringProperty("");
    public StringProperty description = new SimpleStringProperty("");
    public StringProperty status = new SimpleStringProperty("");
    public StringProperty type = new SimpleStringProperty("");
    public Patient patient;
    public ArrayList<Exercice> exercices;

    public Activite() {}

    public Activite(int id, String titre, String description, String status, String type, Patient patient) {
        this.id = id;
        this.titre.set(titre);
        this.description.set(description);
        this.status.set(status);
        this.type.set(type);
        this.patient = patient;
        this.exercices = new ArrayList<>();
    }

    public Activite(int activiteId) {
        this.id = activiteId;
    }

    public Activite(int id, String titre) {
        this.id = id;
        this.titre.set(titre);
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getTitre() {
        return titre.get();
    }

    public String getDescription() {
        return description.get();
    }

    public String getStatus() {
        return status.get();
    }

    public String getType() {
        return type.get();
    }

    public Patient getPatient() {
        return patient;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setTitre(String titre) {
        this.titre.set(titre);
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public void setType(String type) {
        this.type.set(type);
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public StringProperty titreProperty() {
        return titre;
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public StringProperty statusProperty() {
        return status;
    }

    public StringProperty typeProperty() {
        return type;
    }

    public Activite(int id, String titre, String description, String status, String type) {
        this.id = id;
        this.titre.set(titre);
        this.description.set(description);
        this.status.set(status);
        this.type.set(type);
        this.exercices = new ArrayList<>();
    }
}
