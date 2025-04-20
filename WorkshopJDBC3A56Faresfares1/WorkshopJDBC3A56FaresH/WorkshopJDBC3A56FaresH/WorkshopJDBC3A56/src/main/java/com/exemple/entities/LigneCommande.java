package com.exemple.entities;

public class LigneCommande {
    private int id;
    private int produit_id;
    private int quantite;
    private double prix_unitaire;
    private double total;
    private int commande_id;

    public LigneCommande() {}

    public LigneCommande(int id, int produit, int quantite, double prix, int commandeId) {
        this.id = id;
        this.produit_id = produit;
        this.quantite = quantite;
        this.prix_unitaire = prix;
        this.total = quantite * prix;
        this.commande_id = commandeId;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getProduit() { return produit_id; }
    public void setProduit(int produit_id) { this.produit_id = produit_id; }

    public int getQuantite() { return quantite; }
    public void setQuantite(int quantite) {
        this.quantite = quantite;
        calculateTotal();
    }

    public double getPrix() { return prix_unitaire; }
    public void setPrix(double prix) {
        this.prix_unitaire = prix;
        calculateTotal();
    }

    public double getTotal() { return total; }
    public void calculateTotal() {
        this.total = this.quantite * this.prix_unitaire;
    }

    public int getCommandeId() { return commande_id; }
    public void setCommandeId(int commandeId) { this.commande_id = commandeId; }
}
