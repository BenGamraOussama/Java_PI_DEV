package com.exemple.entities;

import java.time.LocalDate;

public class Commande {
    private int id;
    private LocalDate dateCommande;
    private double montantTotal;
    private int userId;

    // Getters & Setters

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getDateCommande() {
        return dateCommande;
    }
    public void setDateCommande(LocalDate dateCommande) {
        this.dateCommande = dateCommande;
    }

    public double getMontantTotal() {
        return montantTotal;
    }
    public void setMontantTotal(double montantTotal) {
        this.montantTotal = montantTotal;
    }

    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
}
