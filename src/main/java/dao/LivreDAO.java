package dao;

import database.DatabaseConnection;
import entities.Livre;
import utils.DateUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LivreDAO {

    /**
     * Ajoute un nouveau livre dans la table 'documents' (type='Livre') puis dans la table 'livres'.
     * @param livre Le livre à insérer (doit avoir titre, auteur, nbPages, etc.)
     * @return L'ID (DOC_ID) généré pour ce livre, ou -1 en cas d'erreur.
     */
    public int ajouterLivre(Livre livre) {
        Connection conn = null;
        PreparedStatement pstmtDocuments = null;
        PreparedStatement pstmtLivres = null;
        ResultSet generatedKeys = null;
        int docId = -1;

        try {
            // Vérifier les données d'entrée
            if (livre.getTitre() == null || livre.getTitre().isEmpty()) {
                throw new IllegalArgumentException("Le titre du livre ne peut pas être vide.");
            }
            if (livre.getAuteur() == null || livre.getAuteur().isEmpty()) {
                throw new IllegalArgumentException("L'auteur du livre ne peut pas être vide.");
            }
            if (livre.getNbPages() <= 0) {
                throw new IllegalArgumentException("Le nombre de pages doit être positif.");
            }

            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // ==============================
            // Insérer dans la table DOCUMENTS
            // ==============================
            String sqlDocuments = 
                "INSERT INTO DOCUMENTS (" +
                "   DOC_TITRE, DOC_AUTEUR, DOC_DESCRIPTION, DOC_FICHE_TECHNIQUE, DOC_DATE_PUBLICATION, " +
                "   DOC_QUANTITE, DOC_QUANTITE_DISPO, DOC_TYPE" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, 'Livre')";

            pstmtDocuments = conn.prepareStatement(sqlDocuments, PreparedStatement.RETURN_GENERATED_KEYS);
            pstmtDocuments.setString(1, livre.getTitre());
            pstmtDocuments.setString(2, livre.getAuteur());
            pstmtDocuments.setString(3, livre.getDescription());
            pstmtDocuments.setString(4, livre.getFicheTechnique());

            // --- Conversion de la date "JJ/MM/AAAA" en "YYYY-MM-DD HH:mm:ss" ---
            String datePublicationMySQL = DateUtils.convertDateFormat(livre.getDatePublication());
            pstmtDocuments.setString(5, datePublicationMySQL);

            pstmtDocuments.setInt(6, livre.getQuantite());
            pstmtDocuments.setInt(7, livre.getQuantiteDispo());

            pstmtDocuments.executeUpdate();
            generatedKeys = pstmtDocuments.getGeneratedKeys();

            if (generatedKeys.next()) {
                docId = generatedKeys.getInt(1);
                livre.setId(docId);
            } else {
                throw new SQLException("Échec de la récupération de l'ID généré pour le document.");
            }

            // ==============================
            // Insérer dans la table LIVRES
            // ==============================
            String sqlLivres = 
                "INSERT INTO LIVRES (DOC_ID, NB_PAGES, GENRE_LITTERAIRE) " +
                "VALUES (?, ?, ?)";
            pstmtLivres = conn.prepareStatement(sqlLivres);
            pstmtLivres.setInt(1, docId);
            pstmtLivres.setInt(2, livre.getNbPages());
            pstmtLivres.setString(3, livre.getGenreLitteraire());
            pstmtLivres.executeUpdate();

            conn.commit();
            System.out.println("Livre ajouté avec succès, ID : " + docId);

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            System.err.println("Erreur : Transaction annulée. " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Erreur de validation des données : " + e.getMessage());
        } finally {
            try {
                if (generatedKeys != null) generatedKeys.close();
                if (pstmtDocuments != null) pstmtDocuments.close();
                if (pstmtLivres != null) pstmtLivres.close();
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
