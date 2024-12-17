import dao.*;
import entities.*;
import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        // Initialisation des DAO
        UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
        LivreDAO livreDAO = new LivreDAO();
        MagazineDAO magazineDAO = new MagazineDAO();
        JournalDAO journalDAO = new JournalDAO();
        MultimediaDAO multimediaDAO = new MultimediaDAO();
        EmpruntDAO empruntDAO = new EmpruntDAO();
        DocumentDAO documentDAO = new DocumentDAO();

        System.out.println("===== Début des Tests =====\n");

        try {
            // 1. Ajouter des utilisateurs
            System.out.println("Ajout des utilisateurs...");
            Utilisateur utilisateur1 = new Utilisateur(0, "Dupont", "Jean", "jean.dupont@example.com", "0612345678");
            Utilisateur utilisateur2 = new Utilisateur(0, "Martin", "Sophie", "sophie.martin@example.com", "0623456789");

            int userId1 = utilisateurDAO.ajouterUtilisateur(utilisateur1);
            int userId2 = utilisateurDAO.ajouterUtilisateur(utilisateur2);

            utilisateur1.setId(userId1);
            utilisateur2.setId(userId2);

            System.out.println("Utilisateur 1 ajouté avec ID : " + userId1);
            System.out.println("Utilisateur 2 ajouté avec ID : " + userId2);

            // 2. Ajouter des documents
            System.out.println("\nAjout des documents...");

            Livre livre = new Livre(0, "Harry Potter", "J.K. Rowling", "Premier tome", LocalDate.now().toString(), 5, 5, 320, "Fantastique");
            int idLivre = livreDAO.ajouterLivre(livre);

            Magazine magazine = new Magazine(0, "National Geographic", "Editeur NatGeo", "Science et Nature", LocalDate.now().toString(), 3, 3, "mensuelle", 150, "Nat Geo");
            int idMagazine = magazineDAO.ajouterMagazine(magazine);

            Journal journal = new Journal(0, "Le Monde", "Rédaction Le Monde", "Actualités", LocalDate.now().toString(), 2, 2, LocalDate.now().toString());
            int idJournal = journalDAO.ajouterJournal(journal);

            Multimedia multimedia = new Multimedia(0, "Star Wars Soundtrack", "John Williams", "Musique originale", LocalDate.now().toString(), 2, 2, "CD", 90);
            int idMultimedia = multimediaDAO.ajouterMultimedia(multimedia);

            System.out.println("Documents ajoutés avec succès.");

            // 3. Tester les emprunts
            System.out.println("\n===== Test des emprunts =====");
            System.out.println("Tentative d'emprunt par l'utilisateur Jean Dupont...");

            empruntDAO.ajouterEmprunt(new Emprunt(0, utilisateur1, idLivre, LocalDate.now(), LocalDate.now().plusDays(30), null, 0.0, "Actif"), "Livre");
            empruntDAO.ajouterEmprunt(new Emprunt(0, utilisateur1, idMagazine, LocalDate.now(), LocalDate.now().plusDays(30), null, 0.0, "Actif"), "Magazine");
            empruntDAO.ajouterEmprunt(new Emprunt(0, utilisateur1, idJournal, LocalDate.now(), LocalDate.now().plusDays(30), null, 0.0, "Actif"), "Journal");
            empruntDAO.ajouterEmprunt(new Emprunt(0, utilisateur1, idMultimedia, LocalDate.now(), LocalDate.now().plusDays(30), null, 0.0, "Actif"), "Multimédia");

            // Tentative d'emprunt supplémentaire (devrait échouer)
            boolean success = empruntDAO.ajouterEmprunt(new Emprunt(0, utilisateur1, idLivre, LocalDate.now(), LocalDate.now().plusDays(30), null, 0.0, "Actif"), "Livre");
            if (!success) {
                System.out.println("Emprunt refusé : Limite de 4 documents atteinte.");
            }

          
            // 5. Retourner un document avec frais de retard
            System.out.println("\n===== Retourner un document =====");
            System.out.println("Retour du document : Harry Potter (avec 10 jours de retard).");
            LocalDate retourTardif = LocalDate.now().plusDays(40); // 10 jours de retard
            empruntDAO.retournerDocument(1, idLivre, retourTardif, "Livre", 0);

            // 6. Afficher les documents disponibles après retour
            System.out.println("\n===== Liste des documents après retour =====");
            documentDAO.listerTousLesDocuments().forEach(System.out::println);

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("\n===== Fin des Tests =====");
    }
}
