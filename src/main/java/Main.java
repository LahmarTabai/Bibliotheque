import dao.UtilisateurDAO;
import database.DatabaseConnection;
import dao.DocumentDAO;
import dao.EmpruntDAO;
import entities.Emprunt;
import entities.Utilisateur;
import entities.Document;

import java.beans.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
        DocumentDAO documentDAO = new DocumentDAO();
        EmpruntDAO empruntDAO = new EmpruntDAO();

        afficherMenuPrincipal(scanner, utilisateurDAO, documentDAO, empruntDAO);
    }

    // Menu principal
    private static void afficherMenuPrincipal(Scanner scanner, UtilisateurDAO utilisateurDAO, DocumentDAO documentDAO, EmpruntDAO empruntDAO) {
        while (true) {
            System.out.println("\n===== Menu Principal =====");
            System.out.println("1. Connexion");
            System.out.println("2. Afficher les fiches techniques des documents");
            System.out.println("0. Quitter");

            System.out.print("Votre choix : ");
            int choix = scanner.nextInt();
            scanner.nextLine(); // Consommer le retour à la ligne

            switch (choix) {
	            case 1: // Connexion
	                System.out.println("===== Connexion =====");
	                System.out.print("Email : ");
	                String email = scanner.nextLine();
	                System.out.print("Mot de passe : ");
	                String password = scanner.nextLine();
	
	                Utilisateur utilisateur = utilisateurDAO.authentifier(email, password);
	                if (utilisateur != null) {
	                    // Vérification si le mot de passe doit être changé
	                    if ("test".equals(password) || !utilisateurDAO.aChangeMotDePasse(utilisateur.getId())) {
	                        System.out.println("Vous utilisez le mot de passe initial. Vous devez le changer.");
	                        System.out.print("Entrez votre nouveau mot de passe : ");
	                        String nouveauMotDePasse = scanner.nextLine();
	
	                        if (utilisateurDAO.modifierMotDePasse(utilisateur.getId(), nouveauMotDePasse)) {
	                            System.out.println("Votre mot de passe a été changé avec succès.");
	                            utilisateur.setPassword(nouveauMotDePasse); // Mettre à jour le mot de passe localement
	                        } else {
	                            System.out.println("Erreur lors du changement de mot de passe. Réessayez.");
	                        }
	                        // Continuer vers le menu approprié après le changement de mot de passe
	                    }
	
	                    // L'utilisateur peut accéder à son menu en fonction de son rôle
	                    System.out.println("\nBienvenue, " + utilisateur.getNom() + " (" + utilisateur.getRole() + ")");
	                    if (utilisateur.getRole().equalsIgnoreCase("ADMIN")) {
	                        afficherMenuAdmin(scanner, utilisateurDAO, documentDAO, empruntDAO);
	                    } else if (utilisateur.getRole().equalsIgnoreCase("USER")) {
	                        afficherMenuUtilisateur(scanner, utilisateur, documentDAO, empruntDAO);
	                    }
	                } else {
	                    System.out.println("Connexion échouée !");
	                }
	                break;

                case 2: // Afficher les fiches techniques
                    afficherFichesTechniques(scanner, documentDAO);
                    break;

                case 0: // Quitter
                    System.out.println("Au revoir !");
                    return;

                default:
                    System.out.println("Choix invalide. Réessayez.");
            }
        }
    }


    // Afficher les fiches techniques
    private static void afficherFichesTechniques(Scanner scanner, DocumentDAO documentDAO) {
        System.out.println("\n===== Fiches Techniques =====");
        System.out.println("1. Livres");
        System.out.println("2. Magazines");
        System.out.println("3. Journaux");
        System.out.println("4. Multimédias");
        System.out.print("Choisissez un type de document : ");
        int choix = scanner.nextInt();
        scanner.nextLine(); // Consommer le retour à la ligne

        String type;
        switch (choix) {
            case 1:
                type = "Livre";
                break;
            case 2:
                type = "Magazine";
                break;
            case 3:
                type = "Journal";
                break;
            case 4:
                type = "Multimédia";
                break;
            default:
                type = null;
        }

        if (type != null) {
            List<Document> documents = documentDAO.listerDocumentsParType(type);
            if (documents.isEmpty()) {
                System.out.println("Aucun document trouvé pour le type : " + type);
            } else {
                System.out.println("\nListe des documents de type " + type + " :");
                for (Document doc : documents) {
                    System.out.println(doc);
                }
            }
        } else {
            System.out.println("Choix invalide.");
        }
    }

    // Afficher les documents disponibles
    private static void afficherDocumentsDisponibles(DocumentDAO documentDAO) {
        System.out.println("\n===== Documents Disponibles =====");
        List<Document> documentsDisponibles = documentDAO.listerDocumentsDisponibles();

        if (documentsDisponibles.isEmpty()) {
            System.out.println("Aucun document disponible pour le moment.");
        } else {
            for (Document doc : documentsDisponibles) {
                System.out.println(doc);
            }
        }
    }
    
    
    private static void rechercherDocuments(Scanner scanner, DocumentDAO documentDAO) {
        System.out.println("\n===== Recherche de Documents =====");
        System.out.print("Titre (ou laissez vide) : ");
        String titre = scanner.nextLine();
        System.out.print("Auteur (ou laissez vide) : ");
        String auteur = scanner.nextLine();
        System.out.print("Description (ou laissez vide) : ");
        String description = scanner.nextLine();
        System.out.print("Fiche Technique (ou laissez vide) : ");
        String ficheTechnique = scanner.nextLine();
        System.out.print("Date de publication (AAAA-MM-JJ ou laissez vide) : ");
        String dateInput = scanner.nextLine();
        LocalDate datePublication = dateInput.isEmpty() ? null : LocalDate.parse(dateInput);
        System.out.print("Type (Livre, Magazine, Journal, Multimédia ou laissez vide) : ");
        String type = scanner.nextLine();

        List<Document> documents = documentDAO.rechercherDocuments(titre, auteur, description, ficheTechnique, datePublication, type);

        if (documents.isEmpty()) {
            System.out.println("Aucun document trouvé avec les critères spécifiés.");
        } else {
            System.out.println("\nRésultats de la recherche :");
            for (Document doc : documents) {
                System.out.println(doc);
            }
        }
    }
    
    // Affiche les statistiques sur les emprunts réalisés par chaque utilisateur
    private static void afficherStatistiquesEmpruntsParUtilisateur(EmpruntDAO empruntDAO) {
        System.out.println("\n===== Statistiques : Nombre d'emprunts par utilisateur =====");
        List<Map<String, Object>> empruntsParUtilisateur = empruntDAO.compterEmpruntsParUtilisateur();

        if (empruntsParUtilisateur.isEmpty()) {
            System.out.println("Aucun emprunt trouvé.");
        } else {
            for (Map<String, Object> utilisateurData : empruntsParUtilisateur) {
                System.out.println("Utilisateur ID : " + utilisateurData.get("USER_ID") +
                                   ", Nom : " + utilisateurData.get("USER_NOM") +
                                   ", Prénom : " + utilisateurData.get("USER_PRENOM") +
                                   ", Nombre d'emprunts : " + utilisateurData.get("TOTAL_EMPRUNTS"));
            }
        }
    }



    
    
    // Affiche les statistiques sur les types de documents empruntés
    private static void afficherStatistiquesTypesDocuments(EmpruntDAO empruntDAO) {
        System.out.println("\n===== Statistiques : Types de documents empruntés =====");
        Map<String, Integer> typesDocumentsEmpruntes = empruntDAO.compterTypesDocumentsEmpruntes();

        if (typesDocumentsEmpruntes.isEmpty()) {
            System.out.println("Aucun document emprunté.");
        } else {
            for (Map.Entry<String, Integer> entry : typesDocumentsEmpruntes.entrySet()) {
                System.out.println("Type de document : " + entry.getKey() + " - Nombre d'emprunts : " + entry.getValue());
            }
        }
    }
    
    
    // methode qui gere les users
    private static void gererUtilisateurs(Scanner scanner, UtilisateurDAO utilisateurDAO) {
        while (true) {
            System.out.println("\n===== Gestion des Utilisateurs =====");
            System.out.println("1. Ajouter un utilisateur");
            System.out.println("2. Lister les utilisateurs");
            System.out.println("3. Modifier un utilisateur");
            System.out.println("4. Supprimer un utilisateur");
            System.out.println("0. Retour");

            System.out.print("Votre choix : ");
            int choix = scanner.nextInt();
            scanner.nextLine(); // Consommer la ligne restante

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
                    System.out.print("Rôle (ADMIN ou USER) : ");
                    String role = scanner.nextLine();

                    Utilisateur nouvelUtilisateur = new Utilisateur(0, nom, prenom, email, telephone, role, "test");
                    int userId = utilisateurDAO.ajouterUtilisateur(nouvelUtilisateur);
                    if (userId != -1) {
                        System.out.println("Utilisateur ajouté avec succès, ID : " + userId);
                    }
                    break;

                case 2: // Lister les utilisateurs
                    utilisateurDAO.listerTousLesUtilisateurs();
                    break;

                case 3: // Modifier un utilisateur
                    System.out.print("ID de l'utilisateur à modifier : ");
                    int userIdModifier = scanner.nextInt();
                    scanner.nextLine(); // Consommer la ligne restante
                    Utilisateur utilisateurAModifier = utilisateurDAO.recupererUtilisateurParId(userIdModifier);
                    if (utilisateurAModifier == null) {
                        System.out.println("Utilisateur non trouvé !");
                        break;
                    }
                    if (utilisateurDAO.modifierUtilisateurConsole(utilisateurAModifier, scanner)) {
                        System.out.println("Utilisateur modifié avec succès.");
                    }
                    break;

                case 4: // Supprimer un utilisateur
                    System.out.print("ID de l'utilisateur à supprimer : ");
                    int userIdSupprimer = scanner.nextInt();
                    scanner.nextLine(); // Consommer la ligne restante
                    if (utilisateurDAO.supprimerUtilisateur(userIdSupprimer)) {
                        System.out.println("Utilisateur supprimé avec succès.");
                    }
                    break;

                case 0: // Retour
                    return;

                default:
                    System.out.println("Choix invalide. Réessayez.");
            }
        }
    }
    
    
    
    // Cette méthode gère l'ajout, la modification, la suppression, et l'affichage des documents
    
    private static void gererDocuments(Scanner scanner, DocumentDAO documentDAO) {
        while (true) {
            System.out.println("\n===== Gestion des Documents =====");
            System.out.println("1. Ajouter un document");
            System.out.println("2. Lister tous les documents");
            System.out.println("3. Modifier un document");
            System.out.println("4. Supprimer un document");
            System.out.println("0. Retour");

            System.out.print("Votre choix : ");
            int choix = scanner.nextInt();
            scanner.nextLine(); // Consommer la ligne restante

            switch (choix) {
                case 1: // Ajouter un document
                    System.out.print("Titre : ");
                    String titre = scanner.nextLine();
                    System.out.print("Auteur : ");
                    String auteur = scanner.nextLine();
                    System.out.print("Description : ");
                    String description = scanner.nextLine();
                    System.out.print("Fiche Technique : ");
                    String ficheTechnique = scanner.nextLine();
                    System.out.print("Date de publication (JJ/MM/AAAA) : ");
                    String datePublication = scanner.nextLine();
                    System.out.print("Quantité : ");
                    int quantite = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Type (Livre, Magazine, Journal, Multimédia) : ");
                    String type = scanner.nextLine();

                    Document nouveauDocument = new Document(0, titre, auteur, description, ficheTechnique, datePublication, quantite, quantite, type);
                    if (documentDAO.ajouterDocument(nouveauDocument)) {
                        System.out.println("Document ajouté avec succès.");
                    } else {
                        System.out.println("Erreur lors de l'ajout du document.");
                    }
                    break;

                case 2: // Lister tous les documents
                    List<Document> documents = documentDAO.listerTousLesDocuments();
                    if (documents.isEmpty()) {
                        System.out.println("Aucun document trouvé.");
                    } else {
                        for (Document doc : documents) {
                            System.out.println(doc);
                        }
                    }
                    break;

                case 3: // Modifier un document
                    System.out.print("ID du document à modifier : ");
                    int docId = scanner.nextInt();
                    scanner.nextLine(); // Consommer la ligne restante

                    // Recherche du document par ID
                    Document documentAModifier = documentDAO.listerTousLesDocuments().stream()
                                                           .filter(doc -> doc.getId() == docId)
                                                           .findFirst()
                                                           .orElse(null);

                    if (documentAModifier == null) {
                        System.out.println("Document non trouvé !");
                        break;
                    }

                    // Modification des champs
                    System.out.print("Titre actuel : " + documentAModifier.getTitre() + ". Nouveau titre (laissez vide pour ne pas changer) : ");
                    String nouveauTitre = scanner.nextLine();
                    if (!nouveauTitre.isEmpty()) documentAModifier.setTitre(nouveauTitre);

                    System.out.print("Auteur actuel : " + documentAModifier.getAuteur() + ". Nouvel auteur (laissez vide pour ne pas changer) : ");
                    String nouvelAuteur = scanner.nextLine();
                    if (!nouvelAuteur.isEmpty()) documentAModifier.setAuteur(nouvelAuteur);

                    System.out.print("Description actuelle : " + documentAModifier.getDescription() + ". Nouvelle description (laissez vide pour ne pas changer) : ");
                    String nouvelleDescription = scanner.nextLine();
                    if (!nouvelleDescription.isEmpty()) documentAModifier.setDescription(nouvelleDescription);

                    System.out.print("Fiche Technique actuelle : " + documentAModifier.getFicheTechnique() + ". Nouvelle Fiche Technique (laissez vide pour ne pas changer) : ");
                    String nouvelleFicheTechnique = scanner.nextLine();
                    if (!nouvelleFicheTechnique.isEmpty()) documentAModifier.setFicheTechnique(nouvelleFicheTechnique);

                    System.out.print("Quantité actuelle : " + documentAModifier.getQuantite() + ". Nouvelle quantité (entrez -1 pour ne pas changer) : ");
                    int nouvelleQuantite = scanner.nextInt();
                    scanner.nextLine(); // Consommer la ligne restante
                    if (nouvelleQuantite >= 0) documentAModifier.setQuantite(nouvelleQuantite);

                    System.out.print("Date de publication actuelle : " + documentAModifier.getDatePublication() + ". Nouvelle date (format JJ/MM/AAAA, laissez vide pour ne pas changer) : ");
                    String nouvelleDate = scanner.nextLine();
                    if (!nouvelleDate.isEmpty()) {
                        try {
                            documentAModifier.setDatePublication(nouvelleDate);
                        } catch (IllegalArgumentException e) {
                            System.out.println("Date invalide. Modification annulée pour ce champ.");
                        }
                    }

                    // Appel à la méthode DAO pour sauvegarder les modifications
                    if (documentDAO.modifierDocument(documentAModifier)) {
                        System.out.println("Document modifié avec succès.");
                    } else {
                        System.out.println("Erreur lors de la modification du document.");
                    }
                    break;


                case 4: // Supprimer un document
                    System.out.print("ID du document à supprimer : ");
                    int docIdSupprimer = scanner.nextInt();
                    if (documentDAO.supprimerDocument(docIdSupprimer)) {
                        System.out.println("Document supprimé avec succès.");
                    } else {
                        System.out.println("Erreur lors de la suppression du document.");
                    }
                    break;

                case 0: // Retour
                    return;

                default:
                    System.out.println("Choix invalide. Réessayez.");
            }
        }
    }







    
    
    

    // Menu pour les utilisateurs
    private static void afficherMenuUtilisateur(Scanner scanner, Utilisateur utilisateur, DocumentDAO documentDAO, EmpruntDAO empruntDAO) {
        while (true) {
            System.out.println("\n===== Menu Utilisateur =====");
            System.out.println("1. Emprunter un document");
            System.out.println("2. Retourner un document");
            System.out.println("3. Voir mes emprunts actifs");
            System.out.println("4. Afficher les documents disponibles");
            System.out.println("5. Lister les documents empruntés");
            System.out.println("6. Rechercher un document par critères");
            System.out.println("7. Recommandations personnalisées");

            System.out.println("0. Quitter");

            System.out.print("Votre choix : ");
            int choix = scanner.nextInt();
            scanner.nextLine(); // Consommer le retour à la ligne

            switch (choix) {
                case 1: // Emprunter un document
                    System.out.print("ID du document à emprunter : ");
                    int docId = scanner.nextInt();
                    scanner.nextLine(); // Consommer le retour à la ligne
                    System.out.print("Entrez la date d'échéance (AAAA-MM-JJ) : ");
                    String dateEcheance = scanner.nextLine();

                    Emprunt emprunt = new Emprunt(0, utilisateur, docId, LocalDate.now(), LocalDate.parse(dateEcheance), "Actif");
                    System.out.print("Type du document (Livre, Magazine, Multimédia, etc.) : ");
                    String docType = scanner.nextLine();

                    if (empruntDAO.ajouterEmprunt(emprunt, docType)) {
                        System.out.println("Emprunt ajouté avec succès !");
                    } else {
                        System.out.println("Échec de l'ajout de l'emprunt. Vérifiez les règles d'emprunt.");
                    }
                    break;

                case 2: // Retourner un document
                    System.out.print("ID de l'emprunt à retourner : ");
                    int empruntId = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("ID du document : ");
                    int docIdToReturn = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Date de retour (AAAA-MM-JJ) : ");
                    LocalDate dateRetour = LocalDate.parse(scanner.nextLine());
                    System.out.print("Type du document : ");
                    String typeDocument = scanner.nextLine();
                    System.out.print("Durée totale du document (en minutes pour multimédia, sinon 0) : ");
                    int dureeDocument = scanner.nextInt();

                    empruntDAO.retournerDocument(empruntId, docIdToReturn, dateRetour, typeDocument, dureeDocument);
                    break;

                case 3: // Voir mes emprunts actifs
                    empruntDAO.listerEmpruntsActifsParUtilisateur(utilisateur.getId());
                    break;

                case 4: // Afficher les documents disponibles
                    afficherDocumentsDisponibles(documentDAO);
                    break;
                    
                case 5: // Lister les documents empruntés
                    System.out.println("\n===== Vos documents empruntés =====");
                    empruntDAO.listerEmpruntsActifsParUtilisateur(utilisateur.getId());
                    break;
                    
                case 6: // Rechercher un document par critères
                	rechercherDocuments(scanner, documentDAO);
                    break;
                    
                case 7: // Afficher les recommandations
                    List<Document> recommandations = documentDAO.recommanderDocuments(utilisateur.getId());
                    if (recommandations.isEmpty()) {
                        System.out.println("Aucune recommandation disponible pour le moment.");
                    } else {
                        System.out.println("\n===== Recommandations =====");
                        for (Document doc : recommandations) {
                            System.out.println(doc);
                        }
                    }
                    break;


                case 0: // Quitter
                    System.out.println("Au revoir !");
                    return;

                default:
                    System.out.println("Choix invalide. Réessayez.");
            }
        }
    }

    // Menu pour l'Admin
    private static void afficherMenuAdmin(Scanner scanner, UtilisateurDAO utilisateurDAO, DocumentDAO documentDAO, EmpruntDAO empruntDAO) {
        while (true) {
            System.out.println("\n===== Menu Admin =====");
            System.out.println("1. Gérer les utilisateurs");
            System.out.println("2. Lister les emprunts actifs");
            System.out.println("3. Lister les emprunts clôturés");
            System.out.println("4. Supprimer un emprunt");
            System.out.println("5. Afficher les documents disponibles");
            System.out.println("6. Voir les emprunts d'un utilisateur spécifique");
            System.out.println("7. Rechercher un document par critères");
            System.out.println("8. Afficher les emprunts par utilisateur (Stats)");
            System.out.println("9. Afficher les types de documents les plus empruntés");
            System.out.println("10. Gérer les documents");
            System.out.println("0. Quitter");

            System.out.print("Votre choix : ");
            int choix = scanner.nextInt();
            scanner.nextLine();

            switch (choix) {
                case 1: // Gérer les utilisateurs (implémentation future)
                	gererUtilisateurs(scanner, utilisateurDAO);
                    break;

                case 2: // Lister les emprunts actifs
                    empruntDAO.listerEmpruntsActifs();
                    break;

                case 3: // Lister les emprunts clôturés
                    empruntDAO.listerEmpruntsClotures();
                    break;

                case 4: // Supprimer un emprunt
                    System.out.print("ID de l'emprunt à supprimer : ");
                    int empruntId = scanner.nextInt();
                    if (empruntDAO.supprimerEmprunt(empruntId)) {
                        System.out.println("Emprunt supprimé avec succès !");
                    } else {
                        System.out.println("Échec de la suppression de l'emprunt.");
                    }
                    break;

                case 5: // Afficher les documents disponibles
                    afficherDocumentsDisponibles(documentDAO);
                    break;
                    
                case 6: // Voir les emprunts d'un utilisateur spécifique
                    System.out.print("ID de l'utilisateur : ");
                    int userId = scanner.nextInt();
                    scanner.nextLine(); // Consommer la ligne restante
                    empruntDAO.listerEmpruntsParUtilisateur(userId);
                    break;
                    
                case 7: // Rechercher un document par critères
                	rechercherDocuments(scanner, documentDAO);
                    break;
                    
                case 8:
                    afficherStatistiquesEmpruntsParUtilisateur(empruntDAO);
                    break;

                case 9:
                    afficherStatistiquesTypesDocuments(empruntDAO);
                    break;
                    
                case 10: // Gérer les documents
                    gererDocuments(scanner, documentDAO);
                    break;



                case 0: // Quitter
                    System.out.println("Au revoir !");
                    return;

                default:
                    System.out.println("Choix invalide. Réessayez.");
            }
        }
    }
}
