import dao.UtilisateurDAO;
import dao.DocumentDAO;
import dao.EmpruntDAO;
import entities.Utilisateur;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        UtilisateurDAO utilisateurDAO = new UtilisateurDAO();

        // Connexion
        System.out.println("===== Connexion =====");
        System.out.print("Email : ");
        String email = scanner.nextLine();
        System.out.print("Mot de passe : ");
        String password = scanner.nextLine();

        Utilisateur utilisateur = utilisateurDAO.authentifier(email, password);

        if (utilisateur != null) {
            System.out.println("\nBienvenue, " + utilisateur.getNom() + " (" + utilisateur.getRole() + ")");
            if (utilisateur.getRole().equalsIgnoreCase("ADMIN")) {
                afficherMenuAdmin(scanner, utilisateurDAO);
            } else if (utilisateur.getRole().equalsIgnoreCase("USER")) {
                afficherMenuUtilisateur(scanner, utilisateur);
            }
        } else {
            System.out.println("Connexion échouée !");
        }
    }

    // Menu Admin
    private static void afficherMenuAdmin(Scanner scanner, UtilisateurDAO utilisateurDAO) {
        while (true) {
            System.out.println("\n===== Menu Admin =====");
            System.out.println("1. Gérer les utilisateurs");
            System.out.println("2. Gérer les documents");
            System.out.println("3. Gérer les emprunts");
            System.out.println("0. Quitter");

            System.out.print("Votre choix : ");
            int choix = scanner.nextInt();
            scanner.nextLine(); // Consommer le retour à la ligne

            switch (choix) {
                case 1:
                    gererUtilisateurs(scanner, utilisateurDAO); // Appel de la méthode
                    break;
                case 2:
                    System.out.println("Gestion des documents (non implémenté).");
                    break;
                case 3:
                    System.out.println("Gestion des emprunts (non implémenté).");
                    break;
                case 0:
                    System.out.println("Au revoir !");
                    return;
                default:
                    System.out.println("Choix invalide. Réessayez.");
            }
        }
    }

    // Méthode pour gérer les utilisateurs
    private static void gererUtilisateurs(Scanner scanner, UtilisateurDAO utilisateurDAO) {
        while (true) {
            System.out.println("\n===== Gestion des Utilisateurs =====");
            System.out.println("1. Ajouter un utilisateur");
            System.out.println("2. Modifier un utilisateur");
            System.out.println("3. Supprimer un utilisateur");
            System.out.println("4. Lister tous les utilisateurs");
            System.out.println("0. Retour au menu Admin");

            System.out.print("Votre choix : ");
            int choix = scanner.nextInt();
            scanner.nextLine(); // Consommer le retour à la ligne

            switch (choix) {
                case 1: // Ajouter un utilisateur
                    System.out.print("Nom : ");
                    String nom = scanner.nextLine();
                    System.out.print("Prénom : ");
                    String prenom = scanner.nextLine();
                    System.out.print("Email : ");
                    String email = scanner.nextLine();
                    System.out.print("Téléphone : ");
                    String telephone = scanner.nextLine();
                    System.out.print("Rôle (ADMIN/USER) : ");
                    String role = scanner.nextLine();
                    System.out.print("Mot de passe : ");
                    String password = scanner.nextLine();

                    Utilisateur utilisateur = new Utilisateur(0, nom, prenom, email, telephone, role, password);
                    utilisateurDAO.ajouterUtilisateur(utilisateur);
                    break;

                
                case 2: // Modifier un utilisateur
                    System.out.print("ID de l'utilisateur à modifier : ");
                    int userId = scanner.nextInt();
                    scanner.nextLine(); // Consommer le retour à la ligne
                    Utilisateur utilisateurExistant = utilisateurDAO.recupererUtilisateurParId(userId);

                    if (utilisateurExistant != null) {
                        System.out.print("Nouveau nom (actuel : " + utilisateurExistant.getNom() + ") : ");
                        utilisateurExistant.setNom(scanner.nextLine());
                        System.out.print("Nouveau prénom (actuel : " + utilisateurExistant.getPrenom() + ") : ");
                        utilisateurExistant.setPrenom(scanner.nextLine());
                        System.out.print("Nouveau email (actuel : " + utilisateurExistant.getEmail() + ") : ");
                        utilisateurExistant.setEmail(scanner.nextLine());
                        System.out.print("Nouveau téléphone (actuel : " + utilisateurExistant.getTelephone() + ") : ");
                        utilisateurExistant.setTelephone(scanner.nextLine());
                        System.out.print("Nouveau mot de passe : ");
                        utilisateurExistant.setPassword(scanner.nextLine());

                        boolean success = utilisateurDAO.modifierUtilisateur(utilisateurExistant);
                        if (!success) {
                            System.out.println("Erreur lors de la modification de l'utilisateur.");
                        }
                    } else {
                        System.out.println("Utilisateur introuvable.");
                    }
                    break;


                case 3: // Supprimer un utilisateur
                    System.out.print("ID de l'utilisateur à supprimer : ");
                    int userIdToDelete = scanner.nextInt();
                    utilisateurDAO.supprimerUtilisateur(userIdToDelete);
                    break;

                case 4: // Lister tous les utilisateurs
                    utilisateurDAO.listerTousLesUtilisateurs();
                    break;

                case 0: // Retour au menu Admin
                    return;

                default:
                    System.out.println("Choix invalide. Réessayez.");
            }
        }
    }

    // Menu pour Utilisateur (reste identique pour l'instant)
    private static void afficherMenuUtilisateur(Scanner scanner, Utilisateur utilisateur) {
        while (true) {
            System.out.println("\n===== Menu Utilisateur =====");
            System.out.println("1. Consulter les documents disponibles");
            System.out.println("2. Rechercher un document");
            System.out.println("3. Emprunter un document");
            System.out.println("4. Voir mes emprunts actifs");
            System.out.println("0. Quitter");

            System.out.print("Votre choix : ");
            int choix = scanner.nextInt();
            scanner.nextLine(); // Consommer le retour à la ligne

            switch (choix) {
                case 1:
                    System.out.println("Liste des documents disponibles (non implémenté).");
                    break;
                case 2:
                    System.out.println("Recherche de document (non implémenté).");
                    break;
                case 3:
                    System.out.println("Emprunt de document (non implémenté).");
                    break;
                case 4:
                    System.out.println("Mes emprunts actifs (non implémenté).");
                    break;
                case 0:
                    System.out.println("Au revoir !");
                    return;
                default:
                    System.out.println("Choix invalide. Réessayez.");
            }
        }
    }
}
