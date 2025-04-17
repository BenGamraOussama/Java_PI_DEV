package pii.entities;

public class Produit {
    private int id;
    private Produit_categorie categorie;
    private String nom;
    private String description;
    private boolean disponible;
    private String image;
    private int quantite;
    private double prix;

    // Constructeur avec tous les attributs
    public Produit(int id, Produit_categorie categorie, String nom, String description, boolean disponible, String image, int quantite, double prix) {
        this.id = id;
        this.categorie = categorie;
        this.nom = nom;
        this.description = description;
        this.disponible = disponible;
        this.image = image;
        this.quantite = quantite;
        this.prix = prix;
    }

    // Constructeur sans paramètres (par défaut)
    public Produit() {
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Produit_categorie getCategorie() {
        return categorie;
    }

    public void setCategorie(Produit_categorie categorie) {
        this.categorie = categorie;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    // Méthode pour afficher un résumé du produit
    @Override
    public String toString() {
        return "Produit [id=" + id + ", nom=" + nom + ", description=" + description + ", prix=" + prix + "]";
    }

    // Méthode pour vérifier si le produit est disponible en stock
    public boolean isEnStock() {
        return quantite > 0 && disponible;
    }
}
