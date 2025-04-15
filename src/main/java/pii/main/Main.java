package pii.main;

import pii.entities.Produit;
import pii.entities.Produit_categorie;
import pii.services.ProduitServices;
import pii.services.Produit_CategoriesServices;
import pii.utils.MyDatabase;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try {
            Produit_CategoriesServices categorieService = new Produit_CategoriesServices();
            ProduitServices produitService = new ProduitServices();
            Scanner scanner = new Scanner(System.in);
            int choix;

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
                scanner.nextLine(); // vider le buffer

                switch (choix) {
                    case 1 -> {
                        System.out.print("Nom de la catégorie : ");
                        String nomCat = scanner.nextLine();
                        Produit_categorie cat = new Produit_categorie(0, nomCat);
                        categorieService.ajouter(cat);
                        System.out.println("✅ Catégorie ajoutée.");
                    }
                    case 2 -> {
                        List<Produit_categorie> cats = categorieService.afficher();
                        System.out.println("📋 Liste des catégories :");
                        cats.forEach(c -> System.out.println(c.getId() + " - " + c.getNom()));
                    }
                    case 3 -> {
                        System.out.print("ID de la catégorie à modifier : ");
                        int id = scanner.nextInt();
                        scanner.nextLine();
                        System.out.print("Nouveau nom : ");
                        String newNom = scanner.nextLine();
                        Produit_categorie updatedCat = new Produit_categorie(id, newNom);
                        categorieService.update(updatedCat);
                        System.out.println("✅ Catégorie mise à jour.");
                    }
                    case 4 -> {
                        System.out.print("ID de la catégorie à supprimer : ");
                        int id = scanner.nextInt();
                        categorieService.delete(id);
                        System.out.println("🗑️ Catégorie supprimée.");
                    }
                    case 5 -> {
                        List<Produit_categorie> cats = categorieService.afficher();
                        if (cats.isEmpty()) {
                            System.out.println("❌ Aucune catégorie disponible. Veuillez en créer une d'abord.");
                            break;
                        }

                        System.out.println("📂 Choisissez une catégorie :");
                        cats.forEach(c -> System.out.println(c.getId() + " - " + c.getNom()));
                        System.out.print("ID catégorie : ");
                        int idCat = scanner.nextInt();
                        scanner.nextLine();

                        Produit_categorie cat = categorieService.trouverParId(idCat);

                        System.out.print("Nom du produit : ");
                        String nom = scanner.nextLine();
                        System.out.print("Description : ");
                        String desc = scanner.nextLine();
                        System.out.print("Disponible (true/false) : ");
                        boolean dispo = scanner.nextBoolean();
                        scanner.nextLine();
                        System.out.print("Chemin complet de l’image : ");
                        String cheminImage = scanner.nextLine();

                        String image = "";
                        Object FileUtils = null;
                        image = FileUtils.toString();
                        System.out.println("🖼️ Image copiée dans le dossier /images avec succès.");

                        System.out.print("Quantité : ");
                        int qte = scanner.nextInt();
                        System.out.print("Note : ");
                        double note = scanner.nextDouble();

                        Produit p = new Produit(0, cat, nom, desc, dispo, image, qte, note);
                        produitService.ajouter(p);
                        System.out.println("✅ Produit ajouté.");
                    }

                    case 6 -> {
                        List<Produit> produits = produitService.readList();
                        System.out.println("📋 Liste des produits :");
                        produits.forEach(p -> System.out.printf("%d | %-20s | Catégorie: %-15s | Qté: %d | Note: %.1f\n",
                                p.getId(), p.getNom(), p.getCategorie().getNom(), p.getQuantite(), p.getNote()));
                    }
                    case 7 -> {
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
                        System.out.print("Nouvelle note : ");
                        double note = scanner.nextDouble();

                        produit.setNom(nom);
                        produit.setDescription(desc);
                        produit.setDisponible(dispo);
                        produit.setImage(image);
                        produit.setQuantite(qte);
                        produit.setNote(note);


                        Produit p = null;
                        produitService.updateProduit(p);
                        System.out.println("✅ Produit mis à jour.");
                    }
                    case 8 -> {
                        System.out.print("ID du produit à supprimer : ");
                        int id = scanner.nextInt();
                        produitService.delete(id);
                        System.out.println("🗑️ Produit supprimé.");
                    }
                    case 0 -> System.out.println("👋 Au revoir !");
                    default -> System.out.println("❌ Choix invalide.");
                }

            } while (choix != 0);

            scanner.close();
        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL : " + e.getMessage());
            e.printStackTrace();
        } finally {
            MyDatabase.closeConnection();
        }
    }
}
