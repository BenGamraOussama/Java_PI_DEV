package tn.esprit.pidev.gestion_activite.entities;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
//oooooooooooooooooooooooooooooooo
public class Patient {
    private int id;
    private String name;
    private String adresse;
    private String phone;
    private List<Activite> activites;
    private List<Exercice> exercices;
    private int bad_word_attempts;
    private LocalDateTime suspended_until;
    private int user_id;

    public Patient() {
        this.activites = new ArrayList<>();
        this.exercices = new ArrayList<>();
        this.bad_word_attempts = 0;
        this.suspended_until = null;
    }

    public Patient(int id, String name, String adresse, String phone, int user_id) {
        this.id = id;
        this.name = name;
        this.adresse = adresse;
        this.phone = phone;
        this.user_id = user_id;
        this.activites = new ArrayList<>();
        this.exercices = new ArrayList<>();
        this.bad_word_attempts = 0;
        this.suspended_until = null;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<Activite> getActivites() {
        return activites;
    }

    public void setActivites(List<Activite> activites) {
        this.activites = activites;
    }

    public List<Exercice> getExercices() {
        return exercices;
    }

    public void setExercices(List<Exercice> exercices) {
        this.exercices = exercices;
    }

    public int getBad_word_attempts() {
        return bad_word_attempts;
    }

    public void setBad_word_attempts(int bad_word_attempts) {
        this.bad_word_attempts = bad_word_attempts;
    }

    public LocalDateTime getSuspended_until() {
        return suspended_until;
    }

    public void setSuspended_until(LocalDateTime suspended_until) {
        this.suspended_until = suspended_until;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public void addActivite(Activite activite) {
        this.activites.add(activite);
    }

    public void addExercice(Exercice exercice) {
        this.exercices.add(exercice);
    }

    @Override
    public String toString() {
        return "Patient{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", adresse='" + adresse + '\'' +
                ", phone='" + phone + '\'' +
                ", bad_word_attempts=" + bad_word_attempts +
                ", suspended_until=" + suspended_until +
                '}';
    }
}