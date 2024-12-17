package dao;

import database.DatabaseConnection;
import entities.Multimedia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MultimediaDAO {

    public int ajouterMultimedia(Multimedia multimedia) {
        Connection conn = null;
        PreparedStatement pstmtDocuments = null;
        PreparedStatement pstmtMultimedia = null;
        ResultSet generatedKeys = null;
        int docId = -1;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Insérer dans DOCUMENTS
            String sqlDocuments = "INSERT INTO DOCUMENTS (DOC_TITRE, DOC_AUTEUR, DOC_DESCRIPTION, DOC_DATE_PUBLICATION, DOC_QUANTITE, DOC_QUANTITE_DISPO, DOC_TYPE) VALUES (?, ?, ?, ?, ?, ?, 'Multimédia')";
            pstmtDocuments = conn.prepareStatement(sqlDocuments, PreparedStatement.RETURN_GENERATED_KEYS);
            pstmtDocuments.setString(1, multimedia.getTitre());
            pstmtDocuments.setString(2, multimedia.getAuteur());
            pstmtDocuments.setString(3, multimedia.getDescription());
            pstmtDocuments.setString(4, multimedia.getDatePublication());
            pstmtDocuments.setInt(5, multimedia.getQuantite());
            pstmtDocuments.setInt(6, multimedia.getQuantiteDispo());

            pstmtDocuments.executeUpdate();
            generatedKeys = pstmtDocuments.getGeneratedKeys();

            if (generatedKeys.next()) {
                docId = generatedKeys.getInt(1);
                multimedia.setId(docId);
            }

            // Insérer dans MULTIMEDIA
            String sqlMultimedia = "INSERT INTO MULTIMEDIA (DOC_ID, TYPE_MULTIMEDIA, DUREE_TOTALE) VALUES (?, ?, ?)";
            pstmtMultimedia = conn.prepareStatement(sqlMultimedia);
            pstmtMultimedia.setInt(1, docId);
            pstmtMultimedia.setString(2, multimedia.getTypeMultimedia());
            pstmtMultimedia.setInt(3, multimedia.getDureeTotale());
            pstmtMultimedia.executeUpdate();

            conn.commit();
            System.out.println("Multimédia ajouté avec succès, ID : " + docId);

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
                if (pstmtMultimedia != null) pstmtMultimedia.close();
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return docId;
    }
}
