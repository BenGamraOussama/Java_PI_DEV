package com.exemple.models;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Produit {
    private final SimpleIntegerProperty id;
    private final SimpleStringProperty nom;
    private final SimpleStringProperty description;
    private final SimpleDoubleProperty prix;
    private final SimpleIntegerProperty stock;
    private final SimpleStringProperty image;

    public Produit(int id, String nom, String description, double prix, int stock, String image) {
        this.id = new SimpleIntegerProperty(id);
        this.nom = new SimpleStringProperty(nom);
        this.description = new SimpleStringProperty(description);
        this.prix = new SimpleDoubleProperty(prix);
        this.stock = new SimpleIntegerProperty(stock);
        this.image = new SimpleStringProperty(image);
    }

    // Getters pour les propriétés
    public int getId() {
        return id.get();
    }

    public String getNom() {
        return nom.get();
    }

    public String getDescription() {
        return description.get();
    }

    public double getPrix() {
        return prix.get();
    }

    public int getStock() {
        return stock.get();
    }

    public String getImage() {
        return image.get();
    }

    // Property getters pour la liaison avec JavaFX
    public SimpleIntegerProperty idProperty() {
        return id;
    }

    public SimpleStringProperty nomProperty() {
        return nom;
    }

    public SimpleStringProperty descriptionProperty() {
        return description;
    }

    public SimpleDoubleProperty prixProperty() {
        return prix;
    }

    public SimpleIntegerProperty stockProperty() {
        return stock;
    }

    public SimpleStringProperty imageProperty() {
        return image;
    }
} 