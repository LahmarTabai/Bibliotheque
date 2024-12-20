package dao;

import database.DatabaseConnection;
import entities.Emprunt;
import entities.Utilisateur;

import java.sql.*;
import java.time.LocalDate;

public class EmpruntDAO {

    private static final double TAUX_JOUR = 0.5;
    private static final double TAUX_MULTIMEDIA = 1.0;
    private static final double TAUX_MAGAZINE_SUPP = 2.0;

    // Ajouter un nouvel emprunt avec contrôle des règles métier
    public boolean ajouterEmprunt(Emprunt emprunt, String docType) {
        if (!peutEmprunter(emprunt.getUtilisateur().getId(), docType)) {
            System.out.println("Règles métier non respectées : L'utilisateur ne peut pas emprunter ce document.");
            return false;
        }

        String sqlInsert = "INSERT INTO EMPRUNT (USER_ID, DOC_ID, DATE_EMPRUNT, DATE_ECHEANCE, STATUS) VALUES (?, ?, ?, ?, 'Actif')";
        String sqlUpdateQuantite = "UPDATE DOCUMENTS SET DOC_QUANTITE_DISPO = DOC_QUANTITE_DISPO - 1 WHERE DOC_ID = ? AND DOC_QUANTITE_DISPO > 0";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmtInsert = conn.prepareStatement(sqlInsert);
             PreparedStatement pstmtUpdate = conn.prepareStatement(sqlUpdateQuantite)) {

            conn.setAutoCommit(false); // Début de la transaction

            // Ajouter l'emprunt
            pstmtInsert.setInt(1, emprunt.getUtilisateur().getId());
            pstmtInsert.setInt(2, emprunt.getDocumentId());
            pstmtInsert.setDate(3, Date.valueOf(emprunt.getDateEmprunt()));
            pstmtInsert.setDate(4, Date.valueOf(emprunt.getDateEcheance()));
            pstmtInsert.executeUpdate();

            // Diminuer la quantité disponible
            pstmtUpdate.setInt(1, emprunt.getDocumentId());
            int rows = pstmtUpdate.executeUpdate();
            if (rows == 0) {
                throw new SQLException("Erreur : Quantité insuffisante pour le document ID : " + emprunt.getDocumentId());
            }

            conn.commit(); // Valider la transaction
            System.out.println("Emprunt ajouté avec succès !");
            return true;

        } catch (SQLException e) {
            try {
                System.err.println("Erreur lors de l'ajout de l'emprunt, rollback effectué.");
                if (DatabaseConnection.getConnection() != null) {
                    DatabaseConnection.getConnection().rollback();
                }
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                DatabaseConnection.getConnection().setAutoCommit(true); // Assurez-vous que l'auto-commit est rétabli
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    // Vérifier si l'utilisateur peut emprunter un document
    public boolean peutEmprunter(int userId, String docType) {
        String sqlTotal = "SELECT COUNT(*) AS total FROM EMPRUNT WHERE USER_ID = ? AND STATUS = 'Actif'";
        String sqlType = "SELECT COUNT(*) AS count FROM EMPRUNT e " +
                         "JOIN DOCUMENTS d ON e.DOC_ID = d.DOC_ID " +
                         "WHERE e.USER_ID = ? AND d.DOC_TYPE = ? AND e.STATUS = 'Actif'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmtTotal = conn.prepareStatement(sqlTotal);
             PreparedStatement pstmtType = conn.prepareStatement(sqlType)) {

            // Vérifier le total des emprunts actifs
            pstmtTotal.setInt(1, userId);
            ResultSet rsTotal = pstmtTotal.executeQuery();
            if (rsTotal.next() && rsTotal.getInt("total") >= 4) {
                System.out.println("Erreur : Limite de 4 emprunts atteinte pour l'utilisateur ID : " + userId);
                return false;
            }

            // Vérifier si un document de ce type est déjà emprunté
            pstmtType.setInt(1, userId);
            pstmtType.setString(2, docType);
            ResultSet rsType = pstmtType.executeQuery();
            if (rsType.next() && rsType.getInt("count") >= 1) {
                System.out.println("Erreur : L'utilisateur a déjà emprunté un document de type : " + docType);
                return false;
            }

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Retourner un document avec mise à jour des quantités et calcul des frais de retard
    public void retournerDocument(int empruntId, int docId, LocalDate dateRetour, String docType, int dureeDocument) {
    String sqlUpdateEmprunt = "UPDATE EMPRUNT SET DATE_RETOUR = ?, FRAIS_RETARD = ?, STATUS = 'Clôturé' WHERE EMPRUNT_ID = ?";
    String sqlUpdateQuantite = "UPDATE DOCUMENTS SET DOC_QUANTITE_DISPO = DOC_QUANTITE_DISPO + 1 WHERE DOC_ID = ?";

    Connection conn = null;

    try {
        conn = DatabaseConnection.getConnection();
        conn.setAutoCommit(false);

        try (PreparedStatement pstmtEmprunt = conn.prepareStatement(sqlUpdateEmprunt);
             PreparedStatement pstmtQuantite = conn.prepareStatement(sqlUpdateQuantite)) {

            LocalDate dateEcheance = getDateEcheance(empruntId);
            double fraisRetard = calculerFraisRetard(dateRetour, dateEcheance, docType, dureeDocument);

            // Mettre à jour l'emprunt avec les frais de retard
            pstmtEmprunt.setDate(1, Date.valueOf(dateRetour));
            pstmtEmprunt.setDouble(2, fraisRetard);
            pstmtEmprunt.setInt(3, empruntId);
            pstmtEmprunt.executeUpdate();

            // Réaugmenter la quantité disponible
            pstmtQuantite.setInt(1, docId);
            pstmtQuantite.executeUpdate();

            conn.commit();
            System.out.println("Document retourné avec succès. Frais de retard : " + fraisRetard + " EUR");
        }
    } catch (SQLException e) {
        try {
            if (conn != null) {
                conn.rollback();
                System.err.println("Erreur lors du retour du document, rollback effectué.");
            }
        } catch (SQLException rollbackEx) {
            rollbackEx.printStackTrace();
        }
        e.printStackTrace();
    } finally {
        try {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close(); // Fermer la connexion proprement
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}

    // Obtenir la date d'échéance pour un emprunt
    private LocalDate getDateEcheance(int empruntId) {
        String sql = "SELECT DATE_ECHEANCE FROM EMPRUNT WHERE EMPRUNT_ID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, empruntId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDate("DATE_ECHEANCE").toLocalDate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return LocalDate.now();
    }

    // Calculer les frais de retard
    private double calculerFraisRetard(LocalDate dateRetour, LocalDate dateEcheance, String docType, int dureeDocument) {
        long joursRetard = dateRetour.toEpochDay() - dateEcheance.toEpochDay();
        if (joursRetard > 0) {
            switch (docType) {
                case "Livre":
                case "Journal":
                    return TAUX_JOUR * joursRetard;
                case "Magazine":
                    return TAUX_JOUR * joursRetard + TAUX_MAGAZINE_SUPP;
                case "Multimédia":
                    return TAUX_MULTIMEDIA * dureeDocument + TAUX_JOUR * joursRetard;
            }
        }
        return 0.0;
    }
    
    // Lister les emprunts actifs pour un utilisateur donné
    public void listerEmpruntsActifsParUtilisateur(int userId) {
        String sql = "SELECT e.EMPRUNT_ID, e.DOC_ID, e.DATE_EMPRUNT, e.DATE_ECHEANCE, d.DOC_TITRE " +
                     "FROM EMPRUNT e " +
                     "JOIN DOCUMENTS d ON e.DOC_ID = d.DOC_ID " +
                     "WHERE e.USER_ID = ? AND e.STATUS = 'Actif'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            System.out.println("Liste des emprunts actifs pour l'utilisateur ID : " + userId);
            while (rs.next()) {
                System.out.println("Emprunt ID: " + rs.getInt("EMPRUNT_ID") +
                                   ", Document: " + rs.getString("DOC_TITRE") +
                                   ", Date Emprunt: " + rs.getDate("DATE_EMPRUNT") +
                                   ", Date Échéance: " + rs.getDate("DATE_ECHEANCE"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Lister tous les emprunts clôturés
    public void listerEmpruntsClotures() {
        String sql = "SELECT e.EMPRUNT_ID, e.USER_ID, e.DOC_ID, e.DATE_EMPRUNT, e.DATE_RETOUR, e.FRAIS_RETARD, d.DOC_TITRE " +
                     "FROM EMPRUNT e " +
                     "JOIN DOCUMENTS d ON e.DOC_ID = d.DOC_ID " +
                     "WHERE e.STATUS = 'Clôturé'";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("Liste des emprunts clôturés :");
            while (rs.next()) {
                System.out.println("Emprunt ID: " + rs.getInt("EMPRUNT_ID") +
                                   ", Document: " + rs.getString("DOC_TITRE") +
                                   ", Utilisateur ID: " + rs.getInt("USER_ID") +
                                   ", Date Emprunt: " + rs.getDate("DATE_EMPRUNT") +
                                   ", Date Retour: " + rs.getDate("DATE_RETOUR") +
                                   ", Frais de retard: " + rs.getDouble("FRAIS_RETARD") + " EUR");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
