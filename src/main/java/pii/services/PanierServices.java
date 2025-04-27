package pii.services;

import pii.entities.Produit;

import java.util.ArrayList;
import java.util.List;

public class PanierServices {

    private List<Produit> panier = new ArrayList<>(); // Liste temporaire pour simuler le panier

    // PanierServices.java
    public void ajouterProduitAuPanier(int utilisateurId, Produit produit) {
        if (produit != null) {
            panier.add(produit);  // Ajoute le produit à la liste du panier
        }
    }


    // Méthode pour récupérer tous les produits dans le panier
    public List<Produit> getPanier(int utilisateurId) {
        return panier; // Retourne les produits dans le panier
    }

    // Méthode pour supprimer un produit du panier
    public void supprimerProduitDuPanier(Produit produit) {
        panier.remove(produit); // Supprime le produit du panier
    }

    // Méthode pour récupérer le nombre total d'articles dans le panier
    public int getTotalItems(int utilisateurId) {
        return panier.size(); // Retourne le nombre d'articles dans le panier
    }

    public void viderPanier() {
    }

    public Produit getProduitsDansPanier() {
        return null;
    }
}
