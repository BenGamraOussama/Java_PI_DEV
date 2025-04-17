package pii.entities;

public class Produit_categorie {

    private int id;
    private String nom;

    // Constructeur
    public Produit_categorie(int id, String nom) {
        this.id = id;
        this.nom = nom;
    }

    public Produit_categorie() {

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
        return "Produit_categorie{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                '}';
    }
}
