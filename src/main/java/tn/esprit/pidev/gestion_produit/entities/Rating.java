package tn.esprit.pidev.gestion_produit.entities;

public class Rating {
    private int id;
    private int produitId;
    private int note;

    public Rating() {
    }

    public Rating(int produitId, int note) {
        this.produitId = produitId;
        this.note = note;
    }

    // Getters et setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProduitId() {
        return produitId;
    }

    public void setProduitId(int produitId) {
        this.produitId = produitId;
    }

    public int getNote() {
        return note;
    }

    public void setNote(int note) {
        if (note >= 1 && note <= 5) {
            this.note = note;
        } else {
            throw new IllegalArgumentException("La note doit être entre 1 et 5");
        }
    }

    public void setRatingValue(int starValue) {
    }

    public int getRatingValue() {
        return 0;
    }
}