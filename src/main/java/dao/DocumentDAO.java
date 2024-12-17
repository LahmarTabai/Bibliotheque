package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import database.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DocumentDAO {

    public List<String> listerTousLesDocuments() {
        List<String> documents = new ArrayList<>();
        String sql = "SELECT * FROM DOCUMENTS";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                documents.add("ID: " + rs.getInt("DOC_ID") + ", Titre: " + rs.getString("DOC_TITRE") +
                              ", Auteur: " + rs.getString("DOC_AUTEUR") + ", Type: " + rs.getString("DOC_TYPE"));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des documents : " + e.getMessage());
        }
        return documents;
    }
    
    public void mettreAJourDisponibilite(int documentId, int nouvelleQuantiteDispo) {
        String sql = "UPDATE DOCUMENTS SET DOC_QUANTITE_DISPO = ? WHERE DOC_ID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, nouvelleQuantiteDispo);
            pstmt.setInt(2, documentId);

            pstmt.executeUpdate();
            System.out.println("Disponibilité du document mise à jour !");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de la disponibilité : " + e.getMessage());
        }
    }

}
