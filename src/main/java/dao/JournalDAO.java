package dao;

import database.DatabaseConnection;
import entities.Journal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JournalDAO {

    public int ajouterJournal(Journal journal) {
        Connection conn = null;
        PreparedStatement pstmtDocuments = null;
        PreparedStatement pstmtJournaux = null;
        ResultSet generatedKeys = null;
        int docId = -1;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Insérer dans DOCUMENTS
            String sqlDocuments = "INSERT INTO DOCUMENTS (DOC_TITRE, DOC_AUTEUR, DOC_DESCRIPTION, DOC_DATE_PUBLICATION, DOC_QUANTITE, DOC_QUANTITE_DISPO, DOC_TYPE) VALUES (?, ?, ?, ?, ?, ?, 'Journal')";
            pstmtDocuments = conn.prepareStatement(sqlDocuments, PreparedStatement.RETURN_GENERATED_KEYS);
            pstmtDocuments.setString(1, journal.getTitre());
            pstmtDocuments.setString(2, journal.getAuteur());
            pstmtDocuments.setString(3, journal.getDescription());
            pstmtDocuments.setString(4, journal.getDatePublication());
            pstmtDocuments.setInt(5, journal.getQuantite());
            pstmtDocuments.setInt(6, journal.getQuantiteDispo());

            pstmtDocuments.executeUpdate();
            generatedKeys = pstmtDocuments.getGeneratedKeys();

            if (generatedKeys.next()) {
                docId = generatedKeys.getInt(1);
                journal.setId(docId);
            }

            // Insérer dans JOURNAUX
            String sqlJournaux = "INSERT INTO JOURNAUX (DOC_ID, DATE_PUBLICATION_SPECIFIQUE) VALUES (?, ?)";
            pstmtJournaux = conn.prepareStatement(sqlJournaux);
            pstmtJournaux.setInt(1, docId);
            pstmtJournaux.setString(2, journal.getDatePublicationSpecifique());
            pstmtJournaux.executeUpdate();

            conn.commit();
            System.out.println("Journal ajouté avec succès, ID : " + docId);

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
                if (pstmtJournaux != null) pstmtJournaux.close();
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return docId;
    }
}
