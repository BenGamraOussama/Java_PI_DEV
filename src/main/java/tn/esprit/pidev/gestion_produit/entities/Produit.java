package tn.esprit.pidev.gestion_produit.entities;

import javafx.scene.image.Image;
import java.io.ByteArrayInputStream;

public class Produit {
    private int id;
    private String nom;
    private double prix;
    private int quantite;
    private String description;
    private boolean disponible;
    private byte[] image; // Champ image pour correspondre à la base
    private Produit_categorie categorie;
    private transient Image fxImage; // Cache pour l'image JavaFX

    public Produit() {}

    public Produit(int id, String nom, double prix, int quantite,
                   String description, boolean disponible, byte[] image,
                   Produit_categorie categorie) {
        this.id = id;
        this.nom = nom;
        this.prix = prix;
        this.quantite = quantite;
        this.description = description;
        this.disponible = disponible;
        this.image = image;
        this.categorie = categorie;
    }

    public Produit(int id, Produit_categorie cat, String nom, String desc, boolean dispo, byte[] image, int qte, float prix) {
        this.id = id;
        this.categorie = cat;
        this.nom = nom;
        this.description = desc;
        this.disponible = dispo;
        this.image = image;
        this.quantite = qte;
        this.prix = prix;
    }

    public Produit(int id, String nom, double prix, int quantite, String description, boolean disponible, byte[] image, Object o) {
        this.id = id;
        this.nom = nom;
        this.prix = prix;
        this.quantite = quantite;
        this.description = description;
        this.disponible = disponible;
        this.image = image;
        // this.categorie = (Produit_categorie) o; // à adapter si besoin
    }

    // Méthodes pour la gestion des images
    public Image getFxImage() {
        if (fxImage == null && image != null) {
            try {
                fxImage = new Image(new ByteArrayInputStream(image));
            } catch (Exception e) {
                System.err.println("Erreur de conversion de l'image: " + e.getMessage());
                return getDefaultImage();
            }
        }
        return fxImage != null ? fxImage : getDefaultImage();
    }

    private Image getDefaultImage() {
        try {
            return new Image(getClass().getResourceAsStream("/image/logo.png"));
        } catch (Exception e) {
            System.err.println("Erreur de chargement de l'image par défaut");
            return null;
        }
    }

    public void updateImage(byte[] newImage) {
        this.image = newImage;
        this.fxImage = null; // Reset le cache
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public double getPrix() { return prix; }
    public void setPrix(double prix) { this.prix = prix; }
    public int getQuantite() { return quantite; }
    public void setQuantite(int quantite) { this.quantite = quantite; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }
    public byte[] getImage() { return image; }
    public void setImage(byte[] image) {
        this.image = image;
        this.fxImage = null;
    }
    public Produit_categorie getCategorie() { return categorie; }
    public void setCategorie(Produit_categorie categorie) { this.categorie = categorie; }

    public boolean estDisponible() {
        return quantite > 0;
    }
}