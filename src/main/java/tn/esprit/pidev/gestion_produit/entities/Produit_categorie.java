package tn.esprit.pidev.gestion_produit.entities;

import javafx.beans.value.ObservableValue;

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

    // Surcharge de la méthode toString pour afficher le nom de la catégorie
    @Override
    public String toString() {
        return nom; // Affiche seulement le nom de la catégorie
    }

    public ObservableValue<String> nomProperty() {
        return null;
    }
}
