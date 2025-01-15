package dao;

import database.DatabaseConnection;
import entities.Journal;
import utils.DateUtils;

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
        // --- Vérifications minimales (optionnel) ---
        // if (journal.getTitre() == null || journal.getTitre().isEmpty()) {
        //     throw new IllegalArgumentException("Le titre du journal ne peut pas être vide.");
        // }
        // if (journal.getAuteur() == null || journal.getAuteur().isEmpty()) {
        //     throw new IllegalArgumentException("L'auteur du journal ne peut pas être vide.");
        // }
        // etc.

        conn = DatabaseConnection.getConnection();
        conn.setAutoCommit(false);

        // ==============================
        // 1) Insérer dans DOCUMENTS
        // ==============================
        String sqlDocuments = 
            "INSERT INTO DOCUMENTS (" +
            "  DOC_TITRE, DOC_AUTEUR, DOC_DESCRIPTION, DOC_FICHE_TECHNIQUE, DOC_DATE_PUBLICATION, " +
            "  DOC_QUANTITE, DOC_QUANTITE_DISPO, DOC_TYPE" +
            ") VALUES (?, ?, ?, ?, ?, ?, ?, 'Journal')";

        pstmtDocuments = conn.prepareStatement(sqlDocuments, PreparedStatement.RETURN_GENERATED_KEYS);
        pstmtDocuments.setString(1, journal.getTitre());
        pstmtDocuments.setString(2, journal.getAuteur());
        pstmtDocuments.setString(3, journal.getDescription());
        pstmtDocuments.setString(4, journal.getFicheTechnique()); // si besoin, ou mets "" si Journal n'a pas de fiche technique

        // --- Conversion de la date "JJ/MM/AAAA" en "YYYY-MM-DD HH:mm:ss" ---
        String datePublicationMySQL = DateUtils.convertDateFormat(journal.getDatePublication());
        pstmtDocuments.setString(5, datePublicationMySQL);

        pstmtDocuments.setInt(6, journal.getQuantite());
        pstmtDocuments.setInt(7, journal.getQuantiteDispo());

        pstmtDocuments.executeUpdate();
        generatedKeys = pstmtDocuments.getGeneratedKeys();

        if (generatedKeys.next()) {
            docId = generatedKeys.getInt(1);
            journal.setId(docId);
        } else {
            throw new SQLException("Échec de la récupération de l'ID généré pour le document (Journal).");
        }

        // ==============================
        // 2) Insérer dans JOURNAUX
        // ==============================
        String sqlJournaux = 
            "INSERT INTO JOURNAUX (DOC_ID, DATE_PUBLICATION_SPECIFIQUE) " +
            "VALUES (?, ?)";
        pstmtJournaux = conn.prepareStatement(sqlJournaux);
        pstmtJournaux.setInt(1, docId);
        pstmtJournaux.setString(2, journal.getDatePublicationSpecifique());
        pstmtJournaux.executeUpdate();

        // --- Tout est OK => commit ---
        conn.commit();
        System.out.println("Journal ajouté avec succès, ID : " + docId);

    } catch (SQLException e) {
        // En cas d’erreur SQL, rollback
        if (conn != null) {
            try {
                conn.rollback();
                System.err.println("Transaction annulée en raison d'une erreur SQL.");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        e.printStackTrace();
    } finally {
        // Fermeture des ressources
        try {
            if (generatedKeys != null) generatedKeys.close();
            if (pstmtDocuments != null) pstmtDocuments.close();
            if (pstmtJournaux != null) pstmtJournaux.close();
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    return docId;
}
}
