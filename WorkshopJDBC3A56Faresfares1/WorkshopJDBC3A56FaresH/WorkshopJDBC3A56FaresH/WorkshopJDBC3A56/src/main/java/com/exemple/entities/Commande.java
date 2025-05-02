package com.exemple.entities;

import javafx.beans.property.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class Commande {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final ObjectProperty<LocalDate> dateCommande = new SimpleObjectProperty<>();
    private final IntegerProperty userId = new SimpleIntegerProperty();
    private final StringProperty nomClient = new SimpleStringProperty();
    private final StringProperty prenomClient = new SimpleStringProperty();
    private final DoubleProperty montantTotal = new SimpleDoubleProperty();
    private Map<Produit, Integer> produits; // Map<Produit, Quantité>

    public Commande() {
        this.produits = new HashMap<>();
    }

    public Commande(LocalDate dateCommande, int userId, String nomClient, String prenomClient) {
        this();
        setDateCommande(dateCommande);
        setUserId(userId);
        setNomClient(nomClient);
        setPrenomClient(prenomClient);
    }

    // Getters et Setters pour les propriétés
    public int getId() { return id.get(); }
    public void setId(int value) { id.set(value); }
    public IntegerProperty idProperty() { return id; }

    public LocalDate getDateCommande() { return dateCommande.get(); }
    public void setDateCommande(LocalDate value) { dateCommande.set(value); }
    public ObjectProperty<LocalDate> dateCommandeProperty() { return dateCommande; }

    public int getUserId() { return userId.get(); }
    public void setUserId(int value) { userId.set(value); }
    public IntegerProperty userIdProperty() { return userId; }

    public String getNomClient() { return nomClient.get(); }
    public void setNomClient(String value) { nomClient.set(value); }
    public StringProperty nomClientProperty() { return nomClient; }

    public String getPrenomClient() { return prenomClient.get(); }
    public void setPrenomClient(String value) { prenomClient.set(value); }
    public StringProperty prenomClientProperty() { return prenomClient; }

    public double getMontantTotal() { return montantTotal.get(); }
    public void setMontantTotal(double value) { montantTotal.set(value); }
    public DoubleProperty montantTotalProperty() { return montantTotal; }

    public Map<Produit, Integer> getProduits() {
        return produits;
    }

    public void setProduits(Map<Produit, Integer> produits) {
        this.produits = produits;
        updateMontantTotal();
    }

    public void ajouterProduit(Produit produit, int quantite) {
        produits.merge(produit, quantite, Integer::sum);
        updateMontantTotal();
    }

    public void supprimerProduit(Produit produit) {
        produits.remove(produit);
        updateMontantTotal();
    }

    private void updateMontantTotal() {
        double total = produits.entrySet().stream()
                .mapToDouble(entry -> entry.getKey().getPrix() * entry.getValue())
                .sum();
        setMontantTotal(total);
    }
}
