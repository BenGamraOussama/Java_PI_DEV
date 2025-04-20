package com.exemple.entities;

public class Produit {
    private int id;
    private int categorieId;
    private String nom;
    private String description;
    private double prix;
    private int disponible;
    private String image;

    // Getters & Setters

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public int getCategorieId() {
        return categorieId;
    }
    public void setCategorieId(int categorieId) {
        this.categorieId = categorieId;
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

    public double getPrix() {
        return prix;
    }
    public void setPrix(double prix) {
        this.prix = prix;
    }

    public int getDisponible() {
        return disponible;
    }
    public void setDisponible(int disponible) {
        this.disponible = disponible;
    }

    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }
}