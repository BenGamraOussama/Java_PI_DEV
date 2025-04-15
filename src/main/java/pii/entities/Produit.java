package pii.entities;

public class Produit {
    private int id;
    private Produit_categorie categorie;
    private String nom;
    private String description;
    private boolean disponible;
    private String image;
    private int quantite;
    private double average_rating;

    // 🔨 Constructeur complet
    public Produit(int id, Produit_categorie categorie, String nom, String description, boolean disponible, String image, int quantite, double average_rating) {
        this.id = id;
        this.categorie = categorie;
        this.nom = nom;
        this.description = description;
        this.disponible = disponible;
        this.image = image;
        this.quantite = quantite;
        this.average_rating = average_rating;
    }

    // 🔨 Constructeur sans ID (utile pour l'ajout où l'ID est auto-généré)
    public Produit(Produit_categorie categorie, String nom, String description, boolean disponible, String image, int quantite, double average_rating) {
        this.categorie = categorie;
        this.nom = nom;
        this.description = description;
        this.disponible = disponible;
        this.image = image;
        this.quantite = quantite;
        this.average_rating = average_rating;
    }

    // 🔨 Autre constructeur simple (utilisable pour des tests ou affichages)
    public Produit(String nom, String description, boolean disponible, int quantite, double average_rating) {
        this.nom = nom;
        this.description = description;
        this.disponible = disponible;
        this.quantite = quantite;
        this.average_rating = average_rating;
    }

    // ✅ Getters et Setters
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

    public double getAverage_rating() {
        return average_rating;
    }

    public void setAverage_rating(double average_rating) {
        this.average_rating = average_rating;
    }

    // 🧾 toString() utile pour le debug ou les ComboBox
    @Override
    public String toString() {
        return nom + " (" + quantite + " en stock)";
    }

    public Object getNote() {
        return null;
    }

    public void setNote(double note) {
    }

    public double getAverageRating() {
        return 0;
    }

    public void setPrix(double prix) {
    }

    public int getPrix() {
        return 0;
    }
}
