package pii.entities;

import javafx.beans.property.*;

public class Produit {

    private final StringProperty nom = new SimpleStringProperty();
    private final DoubleProperty prix = new SimpleDoubleProperty(); // Utilisation de DoubleProperty pour prix
    private final IntegerProperty quantite = new SimpleIntegerProperty();
    private final StringProperty description = new SimpleStringProperty();
    private final BooleanProperty enStock = new SimpleBooleanProperty();

    // Constructeur
    public Produit(String nom, double prix, int quantite, String description, boolean enStock) {
        this.nom.set(nom);
        this.prix.set(prix);
        this.quantite.set(quantite);
        this.description.set(description);
        this.enStock.set(enStock);
    }

    public Produit(int id, Produit_categorie idCategorie, String nom, String description, boolean disponible, String image, int quantite, double prix) {
        // Utiliser double pour prix au lieu de float
    }

    // Getters et Setters pour les propriétés
    public StringProperty nomProperty() {
        return nom;
    }

    public DoubleProperty prixProperty() {
        return prix;
    }

    public IntegerProperty quantiteProperty() {
        return quantite;
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public BooleanProperty enStockProperty() {
        return enStock;
    }

    // Getters classiques
    public String getNom() {
        return nom.get();
    }

    public double getPrix() {
        return prix.get(); // Retourne un double
    }

    public int getQuantite() {
        return quantite.get();
    }

    public String getDescription() {
        return description.get();
    }

    public boolean isEnStock() {
        return enStock.get();
    }

    public boolean isEmpty() {
        return false;
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public Produit_categorie getCategorie() {
        return null; // Implémentation à adapter selon votre logique
    }

    public void setNom(String nom) {
        this.nom.set(nom);
    }

    public int getId() {
        return 0; // À compléter avec votre logique d'ID
    }

    public void setPrix(double prix) {
        this.prix.set(prix); // Assurez-vous de passer un double
    }

    public void setQuantite(int quantite) {
        this.quantite.set(quantite);
    }

    public void setCategorie(Produit_categorie categorie) {
        // Implémentation à adapter selon votre logique
    }

    public String getImage() {
        return null; // Implémentation à adapter selon votre logique d'image
    }

    public boolean isDisponible() {
        return false; // Implémentation à adapter selon votre logique de disponibilité
    }

    public void setDisponible(boolean dispo) {
        this.enStock.set(dispo); // Mise à jour de la disponibilité
    }

    public void setImage(String image) {
        // Implémentation à adapter pour mettre à jour l'image
    }
}
