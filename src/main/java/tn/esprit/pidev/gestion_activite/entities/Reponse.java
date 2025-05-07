package tn.esprit.pidev.gestion_activite.entities;

public class Reponse {
    public int id;
    public Exercice exercice;
    public String contenu;
    public int patient_id;

    public Reponse() {
    }

    public Reponse(int id, Exercice exercice, String contenu) {
        this.id = id;
        this.exercice = exercice;
        this.contenu = contenu;
        this.patient_id = 1; // Default patient ID
    }

    public Reponse(int id, Exercice exercice, String contenu, int patient_id) {
        this.id = id;
        this.exercice = exercice;
        this.contenu = contenu;
        this.patient_id = patient_id;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Exercice getExercice() { return exercice; }
    public void setExercice(Exercice exercice) { this.exercice = exercice; }

    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }

    public int getPatient_id() { return patient_id; }
    public void setPatient_id(int patient_id) { this.patient_id = patient_id; }

    @Override
    public String toString() {
        return "Reponse{" +
                "id=" + id +
                ", exercice=" + exercice.getId() +
                ", contenu='" + contenu + '\'' +
                ", patient_id=" + patient_id +
                '}';
    }
}