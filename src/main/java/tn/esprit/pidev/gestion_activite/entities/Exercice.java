package tn.esprit.pidev.gestion_activite.entities;

public class Exercice extends Activite {
    public int id;
    public Activite activite;
    public String question;

    public Exercice() {
        super();
    }

    public Exercice(int id, Activite activite, String question) {
        super(activite.getId(), activite.getTitre(), activite.getDescription(), activite.getStatus(), activite.getType());
        this.id = id;
        this.activite = activite;
        this.question = question;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Activite getActivite() {
        return activite;
    }

    public void setActivite(Activite activite) {
        this.activite = activite;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    @Override
    public String toString() {
        return "Exercice{id=" + id +
                ", activite=" + activite.getId() +
                ", question='" + question + '\'' +
                ", status='" + getStatus() + '\'' + '}';
    }
}
