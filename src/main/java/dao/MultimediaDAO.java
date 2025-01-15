package dao;

import database.DatabaseConnection;
import entities.Multimedia;
import utils.DateUtils;

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
        // --- Vérifications basiques ---
        if (multimedia.getTitre() == null || multimedia.getTitre().isEmpty()) {
            throw new IllegalArgumentException("Le titre du multimédia ne peut pas être vide.");
        }
        if (multimedia.getDureeTotale() <= 0) {
            throw new IllegalArgumentException("La durée totale doit être un nombre positif.");
        }
        if (multimedia.getQuantite() <= 0 || multimedia.getQuantiteDispo() < 0) {
            throw new IllegalArgumentException("Les quantités doivent être positives (quantité totale et dispo).");
        }

        // (Optionnel) Vérifier la fiche technique s'il y en a une
        // if (multimedia.getFicheTechnique() == null || multimedia.getFicheTechnique().isEmpty()) {
        //     throw new IllegalArgumentException("La fiche technique du multimédia ne peut pas être vide.");
        // }

        // --- Connexion et début de transaction ---
        conn = DatabaseConnection.getConnection();
        conn.setAutoCommit(false);

        // ==============================
        // 1) Insérer dans DOCUMENTS
        // ==============================
        String sqlDocuments = 
            "INSERT INTO DOCUMENTS (" +
            "  DOC_TITRE, DOC_AUTEUR, DOC_DESCRIPTION, DOC_FICHE_TECHNIQUE, DOC_DATE_PUBLICATION, " +
            "  DOC_QUANTITE, DOC_QUANTITE_DISPO, DOC_TYPE" +
            ") VALUES (?, ?, ?, ?, ?, ?, ?, 'Multimédia')";

        pstmtDocuments = conn.prepareStatement(sqlDocuments, PreparedStatement.RETURN_GENERATED_KEYS);
        pstmtDocuments.setString(1, multimedia.getTitre());
        pstmtDocuments.setString(2, multimedia.getAuteur());
        pstmtDocuments.setString(3, multimedia.getDescription());
        pstmtDocuments.setString(4, multimedia.getFicheTechnique());

        // --- Conversion de la date "JJ/MM/AAAA" en "YYYY-MM-DD HH:mm:ss" ---
        String datePublicationMySQL = DateUtils.convertDateFormat(multimedia.getDatePublication());
        pstmtDocuments.setString(5, datePublicationMySQL);

        pstmtDocuments.setInt(6, multimedia.getQuantite());
        pstmtDocuments.setInt(7, multimedia.getQuantiteDispo());

        pstmtDocuments.executeUpdate();
        generatedKeys = pstmtDocuments.getGeneratedKeys();

        if (generatedKeys.next()) {
            docId = generatedKeys.getInt(1);
            multimedia.setId(docId);
        } else {
            throw new SQLException("Échec de la récupération de l'ID généré pour le document.");
        }

        // ==============================
        // 2) Insérer dans MULTIMEDIA
        // ==============================
        String sqlMultimedia = 
            "INSERT INTO MULTIMEDIA (DOC_ID, TYPE_MULTIMEDIA, DUREE_TOTALE) " +
            "VALUES (?, ?, ?)";
        pstmtMultimedia = conn.prepareStatement(sqlMultimedia);
        pstmtMultimedia.setInt(1, docId);
        pstmtMultimedia.setString(2, multimedia.getTypeMultimedia());
        pstmtMultimedia.setInt(3, multimedia.getDureeTotale());
        pstmtMultimedia.executeUpdate();

        // --- Tout s'est bien passé => commit ---
        conn.commit();
        System.out.println("Multimédia ajouté avec succès, ID : " + docId);

    } catch (SQLException e) {
        // En cas d'erreur SQL, rollback
        if (conn != null) {
            try {
                conn.rollback();
                System.err.println("Transaction annulée en raison d'une erreur SQL.");
            } catch (SQLException rollbackEx) {
                System.err.println("Erreur lors de l'annulation de la transaction : " + rollbackEx.getMessage());
            }
        }
        System.err.println("Erreur SQL : " + e.getMessage());
    } catch (IllegalArgumentException e) {
        System.err.println("Erreur de validation : " + e.getMessage());
    } finally {
        // Fermeture des ressources
        try {
            if (generatedKeys != null) generatedKeys.close();
            if (pstmtDocuments != null) pstmtDocuments.close();
            if (pstmtMultimedia != null) pstmtMultimedia.close();
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la fermeture des ressources : " + e.getMessage());
        }
    }

    return docId;
}
}
