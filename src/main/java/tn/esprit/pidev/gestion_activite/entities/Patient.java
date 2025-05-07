package tn.esprit.pidev.gestion_activite.entities;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
//oooooooooooooooooooooooooooooooo
public class Patient {
    private int id;
    private String nom;
    private String prenom;
    private String email;
    private String phone;
    private List<Activite> activites;
    private List<Exercice> exercices;
    private int bad_word_attempts;
    private LocalDateTime suspended_until;

    public Patient() {
        this.activites = new ArrayList<>();
        this.exercices = new ArrayList<>();
        this.bad_word_attempts = 0;
        this.suspended_until = null;
    }

    public Patient(int id, String nom, String prenom, String email, String phone) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.phone = phone;
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

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", bad_word_attempts=" + bad_word_attempts +
                ", suspended_until=" + suspended_until +
                '}';
    }
} 