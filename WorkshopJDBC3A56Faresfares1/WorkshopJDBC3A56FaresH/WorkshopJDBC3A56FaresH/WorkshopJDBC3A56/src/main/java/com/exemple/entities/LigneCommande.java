package com.exemple.entities;

import javafx.beans.property.*;

public class LigneCommande {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final IntegerProperty produit = new SimpleIntegerProperty();
    private final IntegerProperty quantite = new SimpleIntegerProperty();
    private final DoubleProperty prix = new SimpleDoubleProperty();
    private final DoubleProperty total = new SimpleDoubleProperty();
    private final IntegerProperty commandeId = new SimpleIntegerProperty();

    public LigneCommande() {
        // Ajouter des listeners pour calculer automatiquement le total
        quantite.addListener((obs, oldVal, newVal) -> calculateTotal());
        prix.addListener((obs, oldVal, newVal) -> calculateTotal());
    }

    public LigneCommande(int id, int produit, int quantite, double prix, int commandeId) {
        this();
        setId(id);
        setProduit(produit);
        setQuantite(quantite);
        setPrix(prix);
        setCommandeId(commandeId);
    }

    // Getters & Setters pour les propriétés
    public int getId() { return id.get(); }
    public void setId(int value) { id.set(value); }
    public IntegerProperty idProperty() { return id; }

    public int getProduit() { return produit.get(); }
    public void setProduit(int value) { produit.set(value); }
    public IntegerProperty produitProperty() { return produit; }

    public int getQuantite() { return quantite.get(); }
    public void setQuantite(int value) { quantite.set(value); }
    public IntegerProperty quantiteProperty() { return quantite; }

    public double getPrix() { return prix.get(); }
    public void setPrix(double value) { prix.set(value); }
    public DoubleProperty prixProperty() { return prix; }

    public double getTotal() { return total.get(); }
    public DoubleProperty totalProperty() { return total; }

    public int getCommandeId() { return commandeId.get(); }
    public void setCommandeId(int value) { commandeId.set(value); }
    public IntegerProperty commandeIdProperty() { return commandeId; }

    private void calculateTotal() {
        total.set(getQuantite() * getPrix());
    }
}
