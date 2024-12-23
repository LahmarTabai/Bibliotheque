package dao;

import database.DatabaseConnection;
import entities.Utilisateur;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class UtilisateurDAO {

    // Vérifier si un utilisateur existe déjà
    public boolean utilisateurExiste(String email) {
        String sql = "SELECT 1 FROM UTILISATEUR WHERE USER_EMAIL = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            return rs.next(); // Retourne true si l'email existe

        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification de l'existence de l'utilisateur : " + e.getMessage());
            return false;
        }
    }

    // Ajouter un nouvel utilisateur avec ROLE et PASSWORD
    public int ajouterUtilisateur(Utilisateur utilisateur) {
        if (!validerEmail(utilisateur.getEmail())) {
            System.out.println("Erreur : Email invalide.");
            return -1;
        }

        if (utilisateurExiste(utilisateur.getEmail())) {
            System.out.println("Erreur : L'utilisateur avec l'email " + utilisateur.getEmail() + " existe déjà.");
            return -1; // Indiquer que l'utilisateur existe déjà
        }

        String sql = "INSERT INTO UTILISATEUR (USER_NOM, USER_PRENOM, USER_EMAIL, USER_TEL, ROLE, PASSWORD) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, utilisateur.getNom());
            pstmt.setString(2, utilisateur.getPrenom());
            pstmt.setString(3, utilisateur.getEmail());
            pstmt.setString(4, utilisateur.getTelephone());
            pstmt.setString(5, utilisateur.getRole()); // Ajouter le rôle (Admin/User)
            pstmt.setString(6, utilisateur.getPassword()); // Ajouter le mot de passe

            pstmt.executeUpdate();
            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int userId = generatedKeys.getInt(1);
                System.out.println("Utilisateur ajouté avec succès, ID : " + userId);
                return userId;
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de l'utilisateur : " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    // Authentifier un utilisateur
    public Utilisateur authentifier(String email, String password) {
        String sql = "SELECT * FROM UTILISATEUR WHERE USER_EMAIL = ? AND PASSWORD = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Utilisateur(
                        rs.getInt("USER_ID"),
                        rs.getString("USER_NOM"),
                        rs.getString("USER_PRENOM"),
                        rs.getString("USER_EMAIL"),
                        rs.getString("USER_TEL"),
                        rs.getString("ROLE"),  // Charger le rôle
                        null // Ne pas charger le mot de passe pour des raisons de sécurité
                );
            } else {
                System.out.println("Email ou mot de passe incorrect.");
                return null;
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de l'authentification : " + e.getMessage());
            return null;
        }
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
}
