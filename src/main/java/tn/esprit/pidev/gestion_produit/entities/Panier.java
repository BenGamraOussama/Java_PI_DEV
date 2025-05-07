package tn.esprit.pidev.gestion_produit.entities;

public class Panier {
    private int id;
    private int utilisateur_id;  // Notez le underscore pour correspondre à la BDD
    private int produit_id;
    private int quantite;

    public Panier() {}

    public Panier(int utilisateur_id, int produit_id, int quantite) {
        this.utilisateur_id = utilisateur_id;
        this.produit_id = produit_id;
        this.quantite = quantite;
    }

    // Getters et Setters (avec noms exacts comme dans la BDD)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUtilisateur_id() {
        return utilisateur_id;
    }

    public void setUtilisateur_id(int utilisateur_id) {
        this.utilisateur_id = utilisateur_id;
    }

    public int getProduit_id() {
        return produit_id;
    }

    public void setProduit_id(int produit_id) {
        this.produit_id = produit_id;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public void setProduit(Produit produit) {
    }

    public Produit getProduit() {
        return null;
    }
}