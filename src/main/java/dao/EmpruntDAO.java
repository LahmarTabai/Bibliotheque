package dao;

import database.DatabaseConnection;
import entities.Emprunt;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EmpruntDAO {

    private static final Logger LOGGER = Logger.getLogger(EmpruntDAO.class.getName());
    private static final double TAUX_JOUR = 0.5;
    private static final double TAUX_MULTIMEDIA = 1.0;
    private static final double TAUX_MAGAZINE_SUPP = 2.0;

    // Ajouter un nouvel emprunt avec contrôle des règles métier
    public boolean ajouterEmprunt(Emprunt emprunt, String docType) {
        if (!peutEmprunter(emprunt.getUtilisateur().getId(), docType)) {
            LOGGER.warning("L'utilisateur ne peut pas emprunter ce document : " + docType);
            return false;
        }

        String sqlInsert = "INSERT INTO EMPRUNT (USER_ID, DOC_ID, DATE_EMPRUNT, DATE_ECHEANCE, STATUS) VALUES (?, ?, ?, ?, 'Actif')";
        String sqlUpdateQuantite = "UPDATE DOCUMENTS SET DOC_QUANTITE_DISPO = DOC_QUANTITE_DISPO - 1 WHERE DOC_ID = ? AND DOC_QUANTITE_DISPO > 0";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmtInsert = conn.prepareStatement(sqlInsert);
             PreparedStatement pstmtUpdate = conn.prepareStatement(sqlUpdateQuantite)) {

            conn.setAutoCommit(false);

            // Ajouter l'emprunt
            pstmtInsert.setInt(1, emprunt.getUtilisateur().getId());
            pstmtInsert.setInt(2, emprunt.getDocumentId());
            pstmtInsert.setDate(3, Date.valueOf(emprunt.getDateEmprunt()));
            pstmtInsert.setDate(4, Date.valueOf(emprunt.getDateEcheance()));
            pstmtInsert.executeUpdate();

            // Réduire la quantité disponible
            pstmtUpdate.setInt(1, emprunt.getDocumentId());
            int rows = pstmtUpdate.executeUpdate();
            if (rows == 0) {
                conn.rollback();
                throw new SQLException("Quantité insuffisante pour le document ID : " + emprunt.getDocumentId());
            }

            conn.commit();
            LOGGER.info("Emprunt ajouté avec succès : " + emprunt);
            return true;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'ajout de l'emprunt : " + emprunt, e);
            return false;
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
                LOGGER.warning("Limite de 4 emprunts atteinte pour l'utilisateur ID : " + userId);
                return false;
            }

            // Vérifier les emprunts par type de document
            pstmtType.setInt(1, userId);
            pstmtType.setString(2, docType);
            ResultSet rsType = pstmtType.executeQuery();
            if (rsType.next() && rsType.getInt("count") >= 1) {
                LOGGER.warning("L'utilisateur a déjà emprunté un document de type : " + docType);
                return false;
            }

            return true;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la validation des emprunts pour l'utilisateur ID : " + userId, e);
            return false;
        }
    }

    // Retourner un document avec mise à jour des quantités et calcul des frais de retard
    public void retournerDocument(int empruntId, int docId, LocalDate dateRetour, String docType, int dureeDocument) {
        String sqlUpdateEmprunt = "UPDATE EMPRUNT SET DATE_RETOUR = ?, FRAIS_RETARD = ?, STATUS = 'Cloturee' WHERE EMPRUNT_ID = ?";
        String sqlUpdateQuantite = "UPDATE DOCUMENTS SET DOC_QUANTITE_DISPO = DOC_QUANTITE_DISPO + 1 WHERE DOC_ID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmtEmprunt = conn.prepareStatement(sqlUpdateEmprunt);
             PreparedStatement pstmtQuantite = conn.prepareStatement(sqlUpdateQuantite)) {

            LocalDate dateEcheance = getDateEcheance(empruntId);
            double fraisRetard = calculerFraisRetard(dateRetour, dateEcheance, docType, dureeDocument);

            conn.setAutoCommit(false);

            // Mettre à jour l'emprunt
            pstmtEmprunt.setDate(1, Date.valueOf(dateRetour));
            pstmtEmprunt.setDouble(2, fraisRetard);
            pstmtEmprunt.setInt(3, empruntId);
            pstmtEmprunt.executeUpdate();

            // Augmenter la quantité disponible
            pstmtQuantite.setInt(1, docId);
            pstmtQuantite.executeUpdate();

            conn.commit();
            LOGGER.info("Document retourné avec succès. Frais de retard : " + fraisRetard);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du retour du document ID : " + docId, e);
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
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération de la date d'échéance pour l'emprunt ID : " + empruntId, e);
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
                default:
                    return 0.0;
            }
        }
        return 0.0;
    }


    // Lister les emprunts actifs pour un utilisateur
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
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des emprunts actifs pour l'utilisateur ID : " + userId, e);
        }
    }

    // Lister les emprunts actifs
    public void listerEmpruntsActifs() {
        String sql = "SELECT e.EMPRUNT_ID, e.USER_ID, e.DOC_ID, e.DATE_EMPRUNT, e.DATE_ECHEANCE, d.DOC_TITRE " +
                     "FROM EMPRUNT e " +
                     "JOIN DOCUMENTS d ON e.DOC_ID = d.DOC_ID " +
                     "WHERE e.STATUS = 'Actif'";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\nListe des emprunts actifs :");
            while (rs.next()) {
                System.out.println("Emprunt ID: " + rs.getInt("EMPRUNT_ID") +
                                   ", Utilisateur ID: " + rs.getInt("USER_ID") +
                                   ", Document: " + rs.getString("DOC_TITRE") +
                                   ", Date Emprunt: " + rs.getDate("DATE_EMPRUNT") +
                                   ", Date Échéance: " + rs.getDate("DATE_ECHEANCE"));
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des emprunts actifs", e);
        }
    }

    // Lister les emprunts clôturés
    public void listerEmpruntsClotures() {
        String sql = "SELECT e.EMPRUNT_ID, e.USER_ID, e.DOC_ID, e.DATE_EMPRUNT, e.DATE_RETOUR, e.FRAIS_RETARD, d.DOC_TITRE " +
                     "FROM EMPRUNT e " +
                     "JOIN DOCUMENTS d ON e.DOC_ID = d.DOC_ID " +
                     "WHERE e.STATUS = 'Cloturee'";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\nListe des emprunts clôturés :");
            while (rs.next()) {
                System.out.println("Emprunt ID: " + rs.getInt("EMPRUNT_ID") +
                                   ", Utilisateur ID: " + rs.getInt("USER_ID") +
                                   ", Document: " + rs.getString("DOC_TITRE") +
                                   ", Date Emprunt: " + rs.getDate("DATE_EMPRUNT") +
                                   ", Date Retour: " + rs.getDate("DATE_RETOUR") +
                                   ", Frais de Retard: " + rs.getDouble("FRAIS_RETARD") + " EUR");
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des emprunts clôturés", e);
        }
    }
    
    
    // Méthode pour lister les emprunts d'un utilisateur spécifique
    public void listerEmpruntsParUtilisateur(int userId) {
        String sql = "SELECT e.EMPRUNT_ID, e.DOC_ID, e.DATE_EMPRUNT, e.DATE_ECHEANCE, e.STATUS, d.DOC_TITRE " +
                     "FROM EMPRUNT e " +
                     "JOIN DOCUMENTS d ON e.DOC_ID = d.DOC_ID " +
                     "WHERE e.USER_ID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            System.out.println("\n===== Emprunts de l'utilisateur ID : " + userId + " =====");
            while (rs.next()) {
                System.out.println("Emprunt ID : " + rs.getInt("EMPRUNT_ID") +
                                   ", Document : " + rs.getString("DOC_TITRE") +
                                   ", Date d'emprunt : " + rs.getDate("DATE_EMPRUNT") +
                                   ", Date d'échéance : " + rs.getDate("DATE_ECHEANCE") +
                                   ", Statut : " + rs.getString("STATUS"));
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des emprunts : " + e.getMessage());
        }
    }
    
    
    // Méthode pour compter les emprunts par utilisateur 
    public List<Map<String, Object>> compterEmpruntsParUtilisateur() {
        List<Map<String, Object>> empruntsParUtilisateur = new ArrayList<>();
        String sql = "SELECT u.USER_ID, u.USER_NOM, u.USER_PRENOM, COUNT(e.EMPRUNT_ID) AS TOTAL_EMPRUNTS " +
                "FROM utilisateur u " +
                "LEFT JOIN emprunt e ON u.USER_ID = e.USER_ID " +
                "WHERE u.ROLE = 'USER' " +
                "GROUP BY u.USER_ID, u.USER_NOM, u.USER_PRENOM " +
                "ORDER BY TOTAL_EMPRUNTS DESC";


        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Map<String, Object> utilisateurData = new HashMap<>();
                utilisateurData.put("USER_ID", rs.getInt("USER_ID"));
                utilisateurData.put("USER_NOM", rs.getString("USER_NOM"));
                utilisateurData.put("USER_PRENOM", rs.getString("USER_PRENOM"));
                utilisateurData.put("TOTAL_EMPRUNTS", rs.getInt("TOTAL_EMPRUNTS"));
                empruntsParUtilisateur.add(utilisateurData);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de la récupération des statistiques des emprunts par utilisateur : " + e.getMessage());
        }

        return empruntsParUtilisateur;
    }

    
    // Méthode pour trouver les types de documents les plus empruntés
    public Map<String, Integer> compterTypesDocumentsEmpruntes() {
        Map<String, Integer> typesDocumentsEmpruntes = new HashMap<>();
        String sql = "SELECT d.DOC_TYPE, COUNT(*) AS TOTAL " +
	                 "FROM EMPRUNT e " +
	                 "LEFT JOIN DOCUMENTS d ON e.DOC_ID = d.DOC_ID " +
	                 "GROUP BY d.DOC_TYPE " +
	                 "ORDER BY TOTAL DESC";
//        System.out.println(sql);
        


        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                typesDocumentsEmpruntes.put(rs.getString("DOC_TYPE"), rs.getInt("TOTAL"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de la récupération des statistiques des types de documents empruntés : " + e.getMessage());
        }

        return typesDocumentsEmpruntes;
    }



    // Supprimer un emprunt
    public boolean supprimerEmprunt(int empruntId) {
        String sql = "DELETE FROM EMPRUNT WHERE EMPRUNT_ID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, empruntId);
            int rowsDeleted = pstmt.executeUpdate();

            if (rowsDeleted > 0) {
                LOGGER.info("Emprunt supprimé avec succès !");
                return true;
            } else {
                LOGGER.warning("Erreur : Emprunt non trouvé.");
                return false;
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la suppression de l'emprunt", e);
            return false;
        }
    }
}
