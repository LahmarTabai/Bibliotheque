package dao;

import database.DatabaseConnection;
import entities.Magazine;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MagazineDAO {

    public int ajouterMagazine(Magazine magazine) {
        Connection conn = null;
        PreparedStatement pstmtDocuments = null;
        PreparedStatement pstmtMagazines = null;
        ResultSet generatedKeys = null;
        int docId = -1;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Insérer dans DOCUMENTS
            String sqlDocuments = "INSERT INTO DOCUMENTS (DOC_TITRE, DOC_AUTEUR, DOC_DESCRIPTION, DOC_DATE_PUBLICATION, DOC_QUANTITE, DOC_QUANTITE_DISPO, DOC_TYPE) VALUES (?, ?, ?, ?, ?, ?, 'Magazine')";
            pstmtDocuments = conn.prepareStatement(sqlDocuments, PreparedStatement.RETURN_GENERATED_KEYS);
            pstmtDocuments.setString(1, magazine.getTitre());
            pstmtDocuments.setString(2, magazine.getAuteur());
            pstmtDocuments.setString(3, magazine.getDescription());
            pstmtDocuments.setString(4, magazine.getDatePublication());
            pstmtDocuments.setInt(5, magazine.getQuantite());
            pstmtDocuments.setInt(6, magazine.getQuantiteDispo());

            pstmtDocuments.executeUpdate();
            generatedKeys = pstmtDocuments.getGeneratedKeys();

            if (generatedKeys.next()) {
                docId = generatedKeys.getInt(1);
                magazine.setId(docId);
            }

            // Insérer dans MAGAZINES
            String sqlMagazines = "INSERT INTO MAGAZINES (DOC_ID, FREQUENCE_PUBLICATION, NUMERO_PARUTION, EDITEUR) VALUES (?, ?, ?, ?)";
            pstmtMagazines = conn.prepareStatement(sqlMagazines);
            pstmtMagazines.setInt(1, docId);
            pstmtMagazines.setString(2, magazine.getFrequencePublication());
            pstmtMagazines.setInt(3, magazine.getNumeroParution());
            pstmtMagazines.setString(4, magazine.getEditeur());
            pstmtMagazines.executeUpdate();

            conn.commit();
            System.out.println("Magazine ajouté avec succès, ID : " + docId);

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
                if (pstmtMagazines != null) pstmtMagazines.close();
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return docId;
    }
}
