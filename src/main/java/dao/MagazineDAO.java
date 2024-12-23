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
            // Vérification des données d'entrée
            if (magazine.getTitre() == null || magazine.getTitre().isEmpty()) {
                throw new IllegalArgumentException("Le titre du magazine ne peut pas être vide.");
            }
            if (magazine.getAuteur() == null || magazine.getAuteur().isEmpty()) {
                throw new IllegalArgumentException("L'auteur du magazine ne peut pas être vide.");
            }
            if (magazine.getQuantite() <= 0 || magazine.getQuantiteDispo() < 0) {
                throw new IllegalArgumentException("Les quantités doivent être positives.");
            }
            if (magazine.getNumeroParution() <= 0) {
                throw new IllegalArgumentException("Le numéro de parution doit être positif.");
            }

            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Début de la transaction

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
            } else {
                throw new SQLException("Échec de la récupération de l'ID généré pour le document.");
            }

            // Insérer dans MAGAZINES
            String sqlMagazines = "INSERT INTO MAGAZINES (DOC_ID, FREQUENCE_PUBLICATION, NUMERO_PARUTION, EDITEUR) VALUES (?, ?, ?, ?)";
            pstmtMagazines = conn.prepareStatement(sqlMagazines);
            pstmtMagazines.setInt(1, docId);
            pstmtMagazines.setString(2, magazine.getFrequencePublication());
            pstmtMagazines.setInt(3, magazine.getNumeroParution());
            pstmtMagazines.setString(4, magazine.getEditeur());
            pstmtMagazines.executeUpdate();

            conn.commit(); // Valider la transaction
            System.out.println("Magazine ajouté avec succès, ID : " + docId);

        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback(); // Annuler la transaction en cas d'erreur
                    System.err.println("Transaction annulée en raison d'une erreur.");
                }
            } catch (SQLException rollbackEx) {
                System.err.println("Erreur lors de l'annulation de la transaction : " + rollbackEx.getMessage());
            }
            System.err.println("Erreur SQL : " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Erreur de validation : " + e.getMessage());
        } finally {
            try {
                if (generatedKeys != null) generatedKeys.close();
                if (pstmtDocuments != null) pstmtDocuments.close();
                if (pstmtMagazines != null) pstmtMagazines.close();
                if (conn != null) {
                    conn.setAutoCommit(true); // Rétablir le mode auto-commit
                    conn.close(); // Fermer la connexion
                }
            } catch (SQLException e) {
                System.err.println("Erreur lors de la fermeture des ressources : " + e.getMessage());
            }
        }

        return docId;
    }
}
