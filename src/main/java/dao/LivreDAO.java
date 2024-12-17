package dao;

import database.DatabaseConnection;
import entities.Livre;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LivreDAO {

    public int ajouterLivre(Livre livre) {
        Connection conn = null;
        PreparedStatement pstmtDocuments = null;
        PreparedStatement pstmtLivres = null;
        ResultSet generatedKeys = null;
        int docId = -1;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Insérer dans DOCUMENTS
            String sqlDocuments = "INSERT INTO DOCUMENTS (DOC_TITRE, DOC_AUTEUR, DOC_DESCRIPTION, DOC_DATE_PUBLICATION, DOC_QUANTITE, DOC_QUANTITE_DISPO, DOC_TYPE) VALUES (?, ?, ?, ?, ?, ?, 'Livre')";
            pstmtDocuments = conn.prepareStatement(sqlDocuments, PreparedStatement.RETURN_GENERATED_KEYS);
            pstmtDocuments.setString(1, livre.getTitre());
            pstmtDocuments.setString(2, livre.getAuteur());
            pstmtDocuments.setString(3, livre.getDescription());
            pstmtDocuments.setString(4, livre.getDatePublication());
            pstmtDocuments.setInt(5, livre.getQuantite());
            pstmtDocuments.setInt(6, livre.getQuantiteDispo());

            pstmtDocuments.executeUpdate();
            generatedKeys = pstmtDocuments.getGeneratedKeys();

            if (generatedKeys.next()) {
                docId = generatedKeys.getInt(1);
                livre.setId(docId);
            }

            // Insérer dans LIVRES
            String sqlLivres = "INSERT INTO LIVRES (DOC_ID, NB_PAGES, GENRE_LITTERAIRE) VALUES (?, ?, ?)";
            pstmtLivres = conn.prepareStatement(sqlLivres);
            pstmtLivres.setInt(1, docId);
            pstmtLivres.setInt(2, livre.getNbPages());
            pstmtLivres.setString(3, livre.getGenreLitteraire());
            pstmtLivres.executeUpdate();

            conn.commit();
            System.out.println("Livre ajouté avec succès, ID : " + docId);

        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                if (generatedKeys != null) generatedKeys.close();
                if (pstmtDocuments != null) pstmtDocuments.close();
                if (pstmtLivres != null) pstmtLivres.close();
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return docId;
    }
}
