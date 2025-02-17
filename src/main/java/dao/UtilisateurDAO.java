package dao;

import database.DatabaseConnection;
import entities.Utilisateur;

import java.io.Console;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class UtilisateurDAO {

    // Vérifier si un utilisateur existe déjà
	public boolean utilisateurExiste(String email) {
	    String sql = "SELECT 1 FROM UTILISATEUR WHERE USER_EMAIL = ?";
	    try (Connection conn = DatabaseConnection.getConnection();
	         PreparedStatement pstmt = conn.prepareStatement(sql)) {

	        pstmt.setString(1, email);
	        ResultSet rs = pstmt.executeQuery();
	        return rs.next(); // Retourne true si l'email existe déjà

	    } catch (SQLException e) {
	        System.err.println("❌ Erreur lors de la vérification de l'existence de l'utilisateur : " + e.getMessage());
	        return false;
	    }
	}


 // Ajouter un nouvel utilisateur avec un mot de passe initial "test"
	public int ajouterUtilisateur(Utilisateur utilisateur) {
	    if (!validerEmail(utilisateur.getEmail())) {
	        System.out.println("❌ Email invalide.");
	        return -1;
	    }

	    if (utilisateurExiste(utilisateur.getEmail())) {
	        System.out.println("❌ L'email " + utilisateur.getEmail() + " est déjà utilisé.");
	        return -1;
	    }

	    boolean passwordChanged = !utilisateur.getPassword().equals("test"); // Si différent de "test", pas besoin de forcer le changement

	    String sql = "INSERT INTO UTILISATEUR (USER_NOM, USER_PRENOM, USER_EMAIL, USER_TEL, ROLE, PASSWORD, PASSWORD_CHANGED) VALUES (?, ?, ?, ?, ?, ?, ?)";
	    try (Connection conn = DatabaseConnection.getConnection();
	         PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

	        pstmt.setString(1, utilisateur.getNom());
	        pstmt.setString(2, utilisateur.getPrenom());
	        pstmt.setString(3, utilisateur.getEmail());
	        pstmt.setString(4, utilisateur.getTelephone());
	        pstmt.setString(5, "USER"); // Toujours USER
	        pstmt.setString(6, utils.PasswordUtils.hashPassword(utilisateur.getPassword())); // Hash du mot de passe
	        pstmt.setBoolean(7, passwordChanged); // false si "test", true sinon

	        pstmt.executeUpdate();
	        ResultSet generatedKeys = pstmt.getGeneratedKeys();
	        if (generatedKeys.next()) {
	            int userId = generatedKeys.getInt(1);
	            System.out.println("✅ Compte créé avec succès, ID : " + userId);
	            return userId;
	        }

	    } catch (SQLException e) {
	        System.err.println("❌ Erreur lors de l'ajout de l'utilisateur : " + e.getMessage());
	    }
	    return -1;
	}




 // Authentifier un utilisateur
	public Utilisateur authentifier(String email, String password) {
	    String sql = "SELECT * FROM UTILISATEUR WHERE USER_EMAIL = ?";
	    try (Connection conn = DatabaseConnection.getConnection();
	         PreparedStatement pstmt = conn.prepareStatement(sql)) {

	        pstmt.setString(1, email);
	        ResultSet rs = pstmt.executeQuery();

	        if (rs.next()) {
	            String hashedPassword = rs.getString("PASSWORD");
	            boolean passwordChanged = rs.getBoolean("PASSWORD_CHANGED");

	            if (utils.PasswordUtils.checkPassword(password, hashedPassword)) {
	                Utilisateur utilisateur = new Utilisateur(
	                        rs.getInt("USER_ID"),
	                        rs.getString("USER_NOM"),
	                        rs.getString("USER_PRENOM"),
	                        rs.getString("USER_EMAIL"),
	                        rs.getString("USER_TEL"),
	                        rs.getString("ROLE"),
	                        null // Ne jamais retourner le mot de passe
	                );

	                // Si l'utilisateur utilise encore "test", il doit changer son mot de passe
	                if (!passwordChanged) {
	                    System.out.println("⚠️ Vous utilisez le mot de passe par défaut 'test'. Veuillez le changer immédiatement !");
	                }

	                return utilisateur;
	            }
	        }
	        System.out.println("❌ Email ou mot de passe incorrect.");
	    } catch (SQLException e) {
	        System.err.println("❌ Erreur lors de l'authentification : " + e.getMessage());
	    }
	    return null;
	}




    // Récupérer un utilisateur par ID
    public Utilisateur recupererUtilisateurParId(int userId) {
        String sql = "SELECT * FROM UTILISATEUR WHERE USER_ID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Utilisateur(
                        rs.getInt("USER_ID"),
                        rs.getString("USER_NOM"),
                        rs.getString("USER_PRENOM"),
                        rs.getString("USER_EMAIL"),
                        rs.getString("USER_TEL"),
                        rs.getString("ROLE"),
                        null // Ne pas charger le mot de passe
                );
            } else {
                System.out.println("Utilisateur non trouvé avec l'ID : " + userId);
                return null;
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de l'utilisateur : " + e.getMessage());
            return null;
        }
    }

    // Modifier un utilisateur existant
    public boolean modifierUtilisateur(Utilisateur utilisateur) {
        String sql = "UPDATE UTILISATEUR SET USER_NOM = ?, USER_PRENOM = ?, USER_EMAIL = ?, USER_TEL = ?, ROLE = ?, PASSWORD = ? WHERE USER_ID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, utilisateur.getNom());
            pstmt.setString(2, utilisateur.getPrenom());
            pstmt.setString(3, utilisateur.getEmail());
            pstmt.setString(4, utilisateur.getTelephone());
            pstmt.setString(5, utilisateur.getRole());
            pstmt.setString(6, utilisateur.getPassword());
            pstmt.setInt(7, utilisateur.getId());

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Utilisateur modifié avec succès.");
                return true;
            } else {
                System.out.println("Erreur : Utilisateur non trouvé.");
                return false;
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification de l'utilisateur : " + e.getMessage());
            return false;
        }
    }

    // Supprimer un utilisateur par ID
    public boolean supprimerUtilisateur(int userId) {
        String sql = "DELETE FROM UTILISATEUR WHERE USER_ID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            int rowsDeleted = pstmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Utilisateur supprimé avec succès.");
                return true;
            } else {
                System.out.println("Erreur : Utilisateur non trouvé.");
                return false;
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de l'utilisateur : " + e.getMessage());
            return false;
        }
    }
    
    // Modifier le mot de passe de l'utilisateur
    public boolean modifierMotDePasse(int userId, String nouveauMotDePasse) {
        String sql = "UPDATE UTILISATEUR SET PASSWORD = ?, PASSWORD_CHANGED = TRUE WHERE USER_ID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

        	pstmt.setString(1, utils.PasswordUtils.hashPassword(nouveauMotDePasse));
            pstmt.setInt(2, userId);

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Mot de passe modifié avec succès !");
                return true;
            } else {
                System.out.println("Erreur lors de la modification du mot de passe.");
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de la modification du mot de passe : " + e.getMessage());
            return false;
        }
    }

    // Vérification si le mot de passe a été changé
    public boolean aChangeMotDePasse(int userId) {
        String sql = "SELECT PASSWORD_CHANGED FROM UTILISATEUR WHERE USER_ID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getBoolean("PASSWORD_CHANGED");
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification du mot de passe changé : " + e.getMessage());
        }
        return false; // Par défaut, considérer que le mot de passe n'a pas été changé
    }


    // Lister tous les utilisateurs
    public List<Utilisateur> listerTousLesUtilisateurs() {
        List<Utilisateur> utilisateurs = new ArrayList<>();
        String sql = "SELECT * FROM UTILISATEUR";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\nListe des utilisateurs :");
            while (rs.next()) {
                Utilisateur utilisateur = new Utilisateur(
                        rs.getInt("USER_ID"),
                        rs.getString("USER_NOM"),
                        rs.getString("USER_PRENOM"),
                        rs.getString("USER_EMAIL"),
                        rs.getString("USER_TEL"),
                        rs.getString("ROLE"),
                        null // Ne pas afficher les mots de passe
                );
                utilisateurs.add(utilisateur);

                System.out.println("ID : " + utilisateur.getId() +
                        ", Nom : " + utilisateur.getNom() +
                        ", Prénom : " + utilisateur.getPrenom() +
                        ", Email : " + utilisateur.getEmail() +
                        ", Téléphone : " + utilisateur.getTelephone() +
                        ", Rôle : " + utilisateur.getRole());
            }

            if (utilisateurs.isEmpty()) {
                System.out.println("Aucun utilisateur trouvé.");
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des utilisateurs : " + e.getMessage());
        }
        return utilisateurs;
    }

    // Valider l'email
    private boolean validerEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }
    
    
    
    
    
    
    
    
    
    
    
	//    Méthodes pour gerer les users
    
 

    // Modifier un utilisateur (depuis la console)
    public boolean modifierUtilisateurConsole(Utilisateur utilisateur, Scanner scanner) {
        System.out.println("Nom actuel : " + utilisateur.getNom());
        System.out.print("Nouveau nom (laissez vide pour ne pas changer) : ");
        String nouveauNom = scanner.nextLine();
        if (!nouveauNom.isEmpty()) utilisateur.setNom(nouveauNom);

        System.out.println("Prénom actuel : " + utilisateur.getPrenom());
        System.out.print("Nouveau prénom (laissez vide pour ne pas changer) : ");
        String nouveauPrenom = scanner.nextLine();
        if (!nouveauPrenom.isEmpty()) utilisateur.setPrenom(nouveauPrenom);

        System.out.println("Email actuel : " + utilisateur.getEmail());
        System.out.print("Nouvel email (laissez vide pour ne pas changer) : ");
        String nouvelEmail = scanner.nextLine();
        if (!nouvelEmail.isEmpty() && !validerEmail(nouvelEmail)) {
            System.out.println("Email invalide. Veuillez réessayer.");
            return false;
        }
        if (!nouvelEmail.isEmpty()) utilisateur.setEmail(nouvelEmail);

        System.out.println("Téléphone actuel : " + utilisateur.getTelephone());
        System.out.print("Nouveau téléphone (laissez vide pour ne pas changer) : ");
        String nouveauTelephone = scanner.nextLine();
        if (!nouveauTelephone.isEmpty() && !nouveauTelephone.matches("\\d{10}")) {
            System.out.println("Numéro de téléphone invalide. Veuillez réessayer.");
            return false;
        }
        if (!nouveauTelephone.isEmpty()) utilisateur.setTelephone(nouveauTelephone);

        System.out.println("Rôle actuel : " + utilisateur.getRole());
        System.out.print("Nouveau rôle (ADMIN ou USER, laissez vide pour ne pas changer) : ");
        String nouveauRole = scanner.nextLine();
        if (!nouveauRole.isEmpty()) utilisateur.setRole(nouveauRole);

        System.out.println("Password actuel : " + "******");
        Console console = System.console();
        String nouveauPassword = "";

        while (true) {
            if (console != null) {
                char[] passwordArray = console.readPassword("Nouveau Password (laissez vide pour ne pas changer) : ");
                nouveauPassword = new String(passwordArray);
            } else {
                System.out.print("Nouveau Password (laissez vide pour ne pas changer) : ");
                nouveauPassword = scanner.nextLine();
            }

            if (nouveauPassword.isEmpty()) {
                System.out.println("Aucun changement du mot de passe.");
                break;
            }

            String confirmationPassword = console != null
                    ? new String(console.readPassword("Confirmez le nouveau Password : "))
                    : scanner.nextLine();

            if (!nouveauPassword.equals(confirmationPassword)) {
                System.out.println("Les mots de passe ne correspondent pas. Veuillez réessayer.");
            } else if (!utils.PasswordUtils.validerMotDePasse(nouveauPassword)) {
                System.out.println("Mot de passe invalide. Il doit contenir au moins 8 caractères, une majuscule, une minuscule, un chiffre et un caractère spécial.");
            } else {
                String hashedPassword = utils.PasswordUtils.hashPassword(nouveauPassword);
                utilisateur.setPassword(hashedPassword);
                System.out.println("Le mot de passe a été mis à jour avec succès.");
                break;
            }
        }

        return modifierUtilisateur(utilisateur); // Réutilise la méthode DAO existante
    }





	//    
}
