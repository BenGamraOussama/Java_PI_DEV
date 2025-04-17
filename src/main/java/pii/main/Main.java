package pii.main;

import pii.entities.Produit;
import pii.entities.Produit_categorie;
import pii.services.ProduitServices;
import pii.services.Produit_CategoriesService;
import pii.utils.MyDatabase;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Création des services
        Produit_CategoriesService categorieService = new Produit_CategoriesService();
        ProduitServices produitService = new ProduitServices();
        Scanner scanner = new Scanner(System.in);
        int choix;

        try {
            do {
                System.out.println("\n=== MENU PRINCIPAL ===");
                System.out.println("1. Ajouter une catégorie");
                System.out.println("2. Afficher les catégories");
                System.out.println("3. Modifier une catégorie");
                System.out.println("4. Supprimer une catégorie");
                System.out.println("5. Ajouter un produit");
                System.out.println("6. Afficher les produits");
                System.out.println("7. Modifier un produit");
                System.out.println("8. Supprimer un produit");
                System.out.println("0. Quitter");
                System.out.print("Choix : ");
                choix = scanner.nextInt();
                scanner.nextLine(); // Vider le buffer

                switch (choix) {
                    case 1 -> { // Ajouter une catégorie
                        System.out.print("Nom de la catégorie : ");
                        String nomCat = scanner.nextLine();
                        Produit_categorie cat = new Produit_categorie(0, nomCat);
                        categorieService.ajouter(cat);
                        System.out.println("✅ Catégorie ajoutée.");
                    }
                    case 2 -> { // Afficher les catégories
                        List<Produit_categorie> cats = categorieService.afficher();
                        System.out.println("📋 Liste des catégories :");
                        cats.forEach(c -> System.out.println(c.getId() + " - " + c.getNom()));
                    }
                    case 3 -> { // Modifier une catégorie
                        System.out.print("ID de la catégorie à modifier : ");
                        int id = scanner.nextInt();
                        scanner.nextLine();
                        System.out.print("Nouveau nom : ");
                        String newNom = scanner.nextLine();
                        Produit_categorie updatedCat = new Produit_categorie(id, newNom);
                        categorieService.update(updatedCat);
                        System.out.println("✅ Catégorie mise à jour.");
                    }
                    case 4 -> { // Supprimer une catégorie
                        System.out.print("ID de la catégorie à supprimer : ");
                        int id = scanner.nextInt();
                        categorieService.supprimer(id);
                        System.out.println("🗑️ Catégorie supprimée.");
                    }
                    case 5 -> { // Ajouter un produit
                        List<Produit_categorie> cats = categorieService.afficher();
                        if (cats.isEmpty()) {
                            System.out.println("❌ Aucune catégorie disponible. Veuillez en créer une d'abord.");
                            break;
                        }

                        System.out.println("📂 Choisissez une catégorie :");
                        cats.forEach(c -> System.out.println(c.getId() + " - " + c.getNom()));
                        System.out.print("ID catégorie : ");
                        int idCat = scanner.nextInt();
                        scanner.nextLine(); // Vider le buffer

                        Produit_categorie cat = categorieService.trouverParId(idCat);

                        // Demande des informations sur le produit
                        System.out.print("Nom du produit : ");
                        String nom = scanner.nextLine();
                        System.out.print("Description : ");
                        String desc = scanner.nextLine();
                        System.out.print("Disponible (true/false) : ");
                        boolean dispo = scanner.nextBoolean();
                        scanner.nextLine();
                        System.out.print("Chemin complet de l’image : ");
                        String cheminImage = scanner.nextLine();
                        System.out.println("🖼️ Image copiée dans le dossier /images avec succès.");

                        System.out.print("Quantité : ");
                        int qte = scanner.nextInt();

                        // Demander le prix
                        System.out.print("Prix : ");
                        float prix = scanner.nextFloat();

                        // Créer un produit et l'ajouter
                        Produit p = new Produit(0, cat, nom, desc, dispo, cheminImage, qte, prix);
                        produitService.ajouter(p);
                        System.out.println("✅ Produit ajouté.");
                    }
                    case 6 -> { // Afficher les produits
                        List<Produit> produits = produitService.readList();
                        System.out.println("📋 Liste des produits :");
                        produits.forEach(p -> System.out.printf("%d | %-20s | Catégorie: %-15s | Qté: %d | Prix: %.2f\n",
                                p.getId(), p.getNom(), p.getCategorie().getNom(), p.getQuantite(), p.getPrix()));
                    }
                    case 7 -> { // Modifier un produit
                        System.out.print("ID du produit à modifier : ");
                        int id = scanner.nextInt();
                        scanner.nextLine();

                        Produit produit = produitService.findById(id);
                        if (produit == null) {
                            System.out.println("❌ Produit introuvable.");
                            break;
                        }

                        System.out.print("Nouveau nom : ");
                        String nom = scanner.nextLine();
                        System.out.print("Nouvelle description : ");
                        String desc = scanner.nextLine();
                        System.out.print("Disponible (true/false) : ");
                        boolean dispo = scanner.nextBoolean();
                        scanner.nextLine();
                        System.out.print("Nouvelle image : ");
                        String image = scanner.nextLine();
                        System.out.print("Nouvelle quantité : ");
                        int qte = scanner.nextInt();
                        System.out.print("Nouveau prix : ");
                        float prix = scanner.nextFloat();

                        produit.setNom(nom);
                        produit.setDescription(desc);
                        produit.setDisponible(dispo);
                        produit.setImage(image);
                        produit.setQuantite(qte);
                        produit.setPrix(prix);

                        produitService.updateProduit(produit);
                        System.out.println("✅ Produit mis à jour.");
                    }
                    case 8 -> { // Supprimer un produit
                        System.out.print("ID du produit à supprimer : ");
                        int id = scanner.nextInt();
                        produitService.supprimerProduit(id);
                        System.out.println("🗑️ Produit supprimé.");
                    }
                    case 0 -> System.out.println("👋 Au revoir !");
                    default -> System.out.println("❌ Choix invalide.");
                }

            } while (choix != 0); // Continue le menu jusqu'à ce que l'utilisateur quitte

        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL : " + e.getMessage());
            e.printStackTrace();
        } finally {
            MyDatabase.closeConnection(); // Assurez-vous que la connexion est fermée
            scanner.close(); // Fermeture du scanner
        }
    }
}
