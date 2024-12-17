package dao;

import database.DatabaseConnection;
import entities.Utilisateur;

import java.sql.*;

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
            e.printStackTrace();
            return false;
        }
    }

    // Ajouter un nouvel utilisateur si l'email n'existe pas
    public int ajouterUtilisateur(Utilisateur utilisateur) {
        if (utilisateurExiste(utilisateur.getEmail())) {
            System.out.println("Erreur : L'utilisateur avec l'email " + utilisateur.getEmail() + " existe déjà.");
            return -1; // Indiquer que l'utilisateur existe déjà
        }

        String sql = "INSERT INTO UTILISATEUR (USER_NOM, USER_PRENOM, USER_EMAIL, USER_TEL) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, utilisateur.getNom());
            pstmt.setString(2, utilisateur.getPrenom());
            pstmt.setString(3, utilisateur.getEmail());
            pstmt.setString(4, utilisateur.getTelephone());

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
}
