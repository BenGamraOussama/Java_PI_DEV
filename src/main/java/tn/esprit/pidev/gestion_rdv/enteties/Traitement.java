package tn.esprit.pidev.gestion_rdv.enteties;



public class Traitement {
    private int id;
    private String type;
    private String medicament;
    private String suivi;
    private int consultationId;  // Ajout du champ pour la relation

    public Traitement(String type, String medicament, String suivi) {
        this.type = type;
        this.medicament = medicament;
        this.suivi = suivi;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMedicament() {
        return medicament;
    }

    public void setMedicament(String medicament) {
        this.medicament = medicament;
    }

    public String getSuivi() {
        return suivi;
    }

    public void setSuivi(String suivi) {
        this.suivi = suivi;
    }

    public int getConsultationId() {
        return consultationId;
    }

    public void setConsultationId(int consultationId) {
        this.consultationId = consultationId;
    }

    @Override
    public String toString() {
        return "Traitement{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", medicament='" + medicament + '\'' +
                ", suivi='" + suivi + '\'' +
                ", consultationId=" + consultationId +
                '}';
    }
}
