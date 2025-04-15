package pii.entities;
import pii.entities.Produit_categorie;

public class Produit_categorie {
    private int id;
    private String nom;

    // Constructeurs
    public Produit_categorie() {}

    public Produit_categorie(int id, String nom) {
        this.id = id;
        this.nom = nom;
    }

    // Getters et Setters
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

    @Override
    public String toString() {
        return nom; // nom est l’attribut de la catégorie
    }
}
