package pii.entities;

public class Panier {

    private int id;
    private int utilisateurId;  // Correspond à l'ID de l'utilisateur
    private int produitId;      // Correspond à l'ID du produit
    private int quantite;       // Quantité de produits dans le panier

    // Constructeurs, getters et setters
    public Panier(int id, int utilisateurId, int produitId, int quantite) {
        this.id = id;
        this.utilisateurId = utilisateurId;
        this.produitId = produitId;
        this.quantite = quantite;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUtilisateurId() {
        return utilisateurId;
    }

    public void setUtilisateurId(int utilisateurId) {
        this.utilisateurId = utilisateurId;
    }

    public int getProduitId() {
        return produitId;
    }

    public void setProduitId(int produitId) {
        this.produitId = produitId;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }
}

