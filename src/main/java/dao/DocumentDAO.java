package dao;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import database.DatabaseConnection;
import entities.Document;

public class DocumentDAO {

    private static final Logger LOGGER = Logger.getLogger(DocumentDAO.class.getName());

    // Lister tous les documents
//    public List<Document> listerTousLesDocuments() {
//        List<Document> documents = new ArrayList<>();
//        String sql = "SELECT * FROM DOCUMENTS";
//
//        try (Connection conn = DatabaseConnection.getConnection();
//             Statement stmt = conn.createStatement();
//             ResultSet rs = stmt.executeQuery(sql)) {
//
//            while (rs.next()) {
//                Document document = new Document(
//                        rs.getInt("DOC_ID"),
//                        rs.getString("DOC_TITRE"),
//                        rs.getString("DOC_AUTEUR"),
//                        rs.getString("DOC_DESCRIPTION"),
//                        rs.getString("DOC_FICHE_TECHNIQUE"),
//                        rs.getString("DOC_DATE_PUBLICATION"),
//                        rs.getInt("DOC_QUANTITE"),
//                        rs.getInt("DOC_QUANTITE_DISPO"),
//                        rs.getString("DOC_TYPE")
//                );
//                documents.add(document);
//            }
//
//        } catch (SQLException e) {
//            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des documents", e);
//        }
//        return documents;
//    }
    
    
    /**
     * Lister tous les documents, en recréant la bonne sous-classe :
     * Livre, Magazine, Journal ou Multimedia.
     */
    public List<Document> listerTousLesDocuments() {
        List<Document> documents = new ArrayList<>();
        String sql = "SELECT * FROM DOCUMENTS";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int docId = rs.getInt("DOC_ID");
                String docType = rs.getString("DOC_TYPE");
                String titre = rs.getString("DOC_TITRE");
                String auteur = rs.getString("DOC_AUTEUR");
                String description = rs.getString("DOC_DESCRIPTION");
                String ficheTechnique = rs.getString("DOC_FICHE_TECHNIQUE");
                String datePub = rs.getString("DOC_DATE_PUBLICATION");
                int quantite = rs.getInt("DOC_QUANTITE");
                int quantiteDispo = rs.getInt("DOC_QUANTITE_DISPO");

                Document doc = null;
                switch (docType) {
                    case "Livre":
                        // Charger les champs spécifiques (NB_PAGES, GENRE_LITTERAIRE) depuis la table LIVRES
                        doc = construireLivre(conn, docId, titre, auteur, description, ficheTechnique, datePub, quantite, quantiteDispo);
                        break;
                    case "Magazine":
                        doc = construireMagazine(conn, docId, titre, auteur, description, ficheTechnique, datePub, quantite, quantiteDispo);
                        break;
                    case "Journal":
                        doc = construireJournal(conn, docId, titre, auteur, description, ficheTechnique, datePub, quantite, quantiteDispo);
                        break;
                    case "Multimédia":
                    case "Multimedia":
                        doc = construireMultimedia(conn, docId, titre, auteur, description, ficheTechnique, datePub, quantite, quantiteDispo);
                        break;
                    default:
                        // Si type inconnu, on crée juste un Document "générique"
                        doc = new Document(docId, titre, auteur, description, ficheTechnique, datePub, quantite, quantiteDispo, docType);
                }

                if (doc != null) {
                    documents.add(doc);
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des documents", e);
        }
        return documents;
    }
    
    
    
    
    

    // Ajouter un nouveau document
    public boolean ajouterDocument(Document document) {
        if (!validerDocument(document)) {
            LOGGER.warning("Validation échouée pour le document : " + document);
            return false;
        }

        String sql = "INSERT INTO DOCUMENTS (DOC_TITRE, DOC_AUTEUR, DOC_DESCRIPTION, DOC_FICHE_TECHNIQUE, DOC_DATE_PUBLICATION, DOC_QUANTITE, DOC_QUANTITE_DISPO, DOC_TYPE) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, document.getTitre());
            pstmt.setString(2, document.getAuteur());
            pstmt.setString(3, document.getDescription());
            pstmt.setString(4, document.getFicheTechnique());

            // Conversion de la date avant insertion
            String datePublication = document.getDatePublication();
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            DateTimeFormatter dbFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDate localDate = LocalDate.parse(datePublication, inputFormatter);
            LocalDateTime localDateTime = localDate.atTime(0, 0, 0);
            String formattedDate = localDateTime.format(dbFormatter);
            pstmt.setString(5, formattedDate); // Date formatée pour MySQL

            pstmt.setInt(6, document.getQuantite());
            pstmt.setInt(7, document.getQuantiteDispo());
            pstmt.setString(8, document.getType());

            pstmt.executeUpdate();
            LOGGER.info("Document ajouté avec succès : " + document);
            return true;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'ajout du document : " + document, e);
            return false;
        }
    }

    // Modifier un document existant
//    public boolean modifierDocument(Document document) {
//        if (!validerDocument(document)) {
//            LOGGER.warning("Validation échouée pour le document : " + document);
//            return false;
//        }
//
//        String sql = "UPDATE DOCUMENTS SET DOC_TITRE = ?, DOC_AUTEUR = ?, DOC_DESCRIPTION = ?, DOC_FICHE_TECHNIQUE = ?, DOC_DATE_PUBLICATION = ?, DOC_QUANTITE = ?, DOC_QUANTITE_DISPO = ?, DOC_TYPE = ? " +
//                     "WHERE DOC_ID = ?";
//
//        try (Connection conn = DatabaseConnection.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(sql)) {
//
//            pstmt.setString(1, document.getTitre());
//            pstmt.setString(2, document.getAuteur());
//            pstmt.setString(3, document.getDescription());
//            pstmt.setString(4, document.getFicheTechnique());
//            pstmt.setString(5, document.getDatePublication());
//            pstmt.setInt(6, document.getQuantite());
//            pstmt.setInt(7, document.getQuantiteDispo());
//            pstmt.setString(8, document.getType());
//            pstmt.setInt(9, document.getId());
//
//            int rowsUpdated = pstmt.executeUpdate();
//            if (rowsUpdated > 0) {
//                LOGGER.info("Document modifié avec succès : " + document);
//                return true;
//            } else {
//                LOGGER.warning("Document non trouvé pour modification : " + document);
//                return false;
//            }
//
//        } catch (SQLException e) {
//            LOGGER.log(Level.SEVERE, "Erreur lors de la modification du document : " + document, e);
//            return false;
//        }
//    }
    
    
    /**
     * Méthode pour modifier un document (table documents + table fille selon le type).
     */
    public boolean modifierDocument(Document document) {
        // 1) Validation
        if (!validerDocument(document)) {
            LOGGER.warning("Validation échouée pour le document : " + document);
            return false;
        }

        String typeDoc = document.getType();
        if (typeDoc == null || typeDoc.isEmpty()) {
            LOGGER.warning("Impossible de modifier : le type du document est introuvable.");
            return false;
        }

        String sqlUpdateDocs = 
            "UPDATE DOCUMENTS "
          + "   SET DOC_TITRE = ?, DOC_AUTEUR = ?, DOC_DESCRIPTION = ?, "
          + "       DOC_FICHE_TECHNIQUE = ?, DOC_DATE_PUBLICATION = ?, "
          + "       DOC_QUANTITE = ?, DOC_QUANTITE_DISPO = ?, DOC_TYPE = ? "
          + " WHERE DOC_ID = ?";

        Connection conn = null;
        PreparedStatement pstmtDocs = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // =============================
            // 1) Mise à jour de DOCUMENTS
            // =============================
            pstmtDocs = conn.prepareStatement(sqlUpdateDocs);
            pstmtDocs.setString(1, document.getTitre());
            pstmtDocs.setString(2, document.getAuteur());
            pstmtDocs.setString(3, document.getDescription());
            pstmtDocs.setString(4, document.getFicheTechnique());

            // Conversion de la date
            String dateMySQL = convertDateFormat(document.getDatePublication());
            pstmtDocs.setString(5, dateMySQL);

            pstmtDocs.setInt(6, document.getQuantite());
            pstmtDocs.setInt(7, document.getQuantiteDispo());
            pstmtDocs.setString(8, typeDoc);
            pstmtDocs.setInt(9, document.getId());

            int rowsDocs = pstmtDocs.executeUpdate();
            if (rowsDocs == 0) {
                conn.rollback();
                LOGGER.warning("Aucun document trouvé (ID=" + document.getId() + ")");
                return false;
            }

            // ===============================
            // 2) Mise à jour de la table fille
            // ===============================
            switch (typeDoc) {
                case "Livre":
                    if (!(document instanceof entities.Livre)) {
                        LOGGER.warning("Le Document n’est pas une instance de Livre, impossible de caster.");
                        conn.rollback();
                        return false;
                    }
                    entities.Livre livre = (entities.Livre) document;
                    updateLivre(conn, livre);
                    break;

                case "Magazine":
                    if (!(document instanceof entities.Magazine)) {
                        LOGGER.warning("Le Document n’est pas une instance de Magazine, impossible de caster.");
                        conn.rollback();
                        return false;
                    }
                    entities.Magazine mag = (entities.Magazine) document;
                    updateMagazine(conn, mag);
                    break;

                case "Journal":
                    if (!(document instanceof entities.Journal)) {
                        LOGGER.warning("Le Document n’est pas une instance de Journal, impossible de caster.");
                        conn.rollback();
                        return false;
                    }
                    entities.Journal jour = (entities.Journal) document;
                    updateJournal(conn, jour);
                    break;

                case "Multimédia":
                case "Multimedia":
                    if (!(document instanceof entities.Multimedia)) {
                        LOGGER.warning("Le Document n’est pas une instance de Multimedia, impossible de caster.");
                        conn.rollback();
                        return false;
                    }
                    entities.Multimedia multi = (entities.Multimedia) document;
                    updateMultimedia(conn, multi);
                    break;

                default:
                    LOGGER.warning("Type de document non géré pour la modification : " + typeDoc);
                    conn.rollback();
                    return false;
            }

            conn.commit();
            LOGGER.info("Document + table fille modifiés avec succès : " + document);
            return true;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la modification du document : " + document, e);
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            return false;
        } finally {
            if (pstmtDocs != null) {
                try { pstmtDocs.close(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ex) { ex.printStackTrace(); }
            }
        }
    }

    // ============================
    // Méthodes privées de mise à jour
    // ============================
    private void updateLivre(Connection conn, entities.Livre livre) throws SQLException {
        String sql = 
            "UPDATE LIVRES "
          + "   SET NB_PAGES = ?, GENRE_LITTERAIRE = ? "
          + " WHERE DOC_ID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, livre.getNbPages());
            pstmt.setString(2, livre.getGenreLitteraire());
            pstmt.setInt(3, livre.getId());
            int rows = pstmt.executeUpdate();
            if (rows == 0) {
                throw new SQLException("Aucun enregistrement LIVRES pour DOC_ID=" + livre.getId());
            }
        }
    }

    private void updateMagazine(Connection conn, entities.Magazine magazine) throws SQLException {
        String sql = 
            "UPDATE MAGAZINES "
          + "   SET FREQUENCE_PUBLICATION = ?, NUMERO_PARUTION = ?, EDITEUR = ? "
          + " WHERE DOC_ID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, magazine.getFrequencePublication());
            pstmt.setInt(2, magazine.getNumeroParution());
            pstmt.setString(3, magazine.getEditeur());
            pstmt.setInt(4, magazine.getId());
            int rows = pstmt.executeUpdate();
            if (rows == 0) {
                throw new SQLException("Aucun enregistrement MAGAZINES pour DOC_ID=" + magazine.getId());
            }
        }
    }

    private void updateJournal(Connection conn, entities.Journal journal) throws SQLException {
        String sql = 
            "UPDATE JOURNAUX "
          + "   SET DATE_PUBLICATION_SPECIFIQUE = ? "
          + " WHERE DOC_ID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, journal.getDatePublicationSpecifique());
            pstmt.setInt(2, journal.getId());
            int rows = pstmt.executeUpdate();
            if (rows == 0) {
                throw new SQLException("Aucun enregistrement JOURNAUX pour DOC_ID=" + journal.getId());
            }
        }
    }

    private void updateMultimedia(Connection conn, entities.Multimedia multi) throws SQLException {
        String sql = 
            "UPDATE MULTIMEDIA "
          + "   SET TYPE_MULTIMEDIA = ?, DUREE_TOTALE = ? "
          + " WHERE DOC_ID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, multi.getTypeMultimedia());
            pstmt.setInt(2, multi.getDureeTotale());
            pstmt.setInt(3, multi.getId());
            int rows = pstmt.executeUpdate();
            if (rows == 0) {
                throw new SQLException("Aucun enregistrement MULTIMEDIA pour DOC_ID=" + multi.getId());
            }
        }
    }

    // ============================
    // Méthodes privées "double SELECT" (pour listerTousLesDocuments)
    // ============================
    private entities.Livre construireLivre(Connection conn, int docId,
        String titre, String auteur, String description, String ficheTechnique,
        String datePub, int quantite, int quantiteDispo
    ) throws SQLException {
        String sql = "SELECT NB_PAGES, GENRE_LITTERAIRE FROM LIVRES WHERE DOC_ID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, docId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int nbPages = rs.getInt("NB_PAGES");
                    String genre = rs.getString("GENRE_LITTERAIRE");
                    return new entities.Livre(
                            docId, titre, auteur, description, ficheTechnique,
                            datePub, quantite, quantiteDispo,
                            nbPages, genre
                    );
                }
            }
        }
        // pas trouvé => incohérence, on peut retourner null
        return null;
    }

    private entities.Magazine construireMagazine(Connection conn, int docId,
        String titre, String auteur, String description, String ficheTechnique,
        String datePub, int quantite, int quantiteDispo
    ) throws SQLException {
        String sql = "SELECT FREQUENCE_PUBLICATION, NUMERO_PARUTION, EDITEUR FROM MAGAZINES WHERE DOC_ID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, docId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String freq = rs.getString("FREQUENCE_PUBLICATION");
                    int num = rs.getInt("NUMERO_PARUTION");
                    String editeur = rs.getString("EDITEUR");
                    return new entities.Magazine(
                        docId, titre, auteur, description, ficheTechnique,
                        datePub, quantite, quantiteDispo,
                        freq, num, editeur
                    );
                }
            }
        }
        return null;
    }

    private entities.Journal construireJournal(Connection conn, int docId,
        String titre, String auteur, String description, String ficheTechnique,
        String datePub, int quantite, int quantiteDispo
    ) throws SQLException {
        String sql = "SELECT DATE_PUBLICATION_SPECIFIQUE FROM JOURNAUX WHERE DOC_ID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, docId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String dateSpec = rs.getString("DATE_PUBLICATION_SPECIFIQUE");
                    return new entities.Journal(
                        docId, titre, auteur, description, ficheTechnique,
                        datePub, quantite, quantiteDispo,
                        dateSpec
                    );
                }
            }
        }
        return null;
    }

    private entities.Multimedia construireMultimedia(Connection conn, int docId,
        String titre, String auteur, String description, String ficheTechnique,
        String datePub, int quantite, int quantiteDispo
    ) throws SQLException {
        String sql = "SELECT TYPE_MULTIMEDIA, DUREE_TOTALE FROM MULTIMEDIA WHERE DOC_ID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, docId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String typeMulti = rs.getString("TYPE_MULTIMEDIA");
                    int duree = rs.getInt("DUREE_TOTALE");
                    return new entities.Multimedia(
                        docId, titre, auteur, description, ficheTechnique,
                        datePub, quantite, quantiteDispo,
                        typeMulti, duree
                    );
                }
            }
        }
        return null;
    }

    
    /**
     * Met à jour la disponibilité d'un document (DOC_QUANTITE_DISPO).
     */
    public boolean mettreAJourDisponibilite(int documentId, int nouvelleQuantiteDispo) {
        String sql = "UPDATE DOCUMENTS SET DOC_QUANTITE_DISPO = ? WHERE DOC_ID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, nouvelleQuantiteDispo);
            pstmt.setInt(2, documentId);

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                LOGGER.info("Disponibilité mise à jour pour le document, ID : " + documentId);
                return true;
            } else {
                LOGGER.warning("Document non trouvé pour mise à jour de la disponibilité, ID : " + documentId);
                return false;
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la mise à jour de la disponibilité pour le document, ID : " + documentId, e);
            return false;
        }
    }
    
    
    
    
    
    
    
  
    
    
    
    
    

    /**
     * Convertit la date si nécessaire (ex: "JJ/MM/AAAA" => "YYYY-MM-DD HH:mm:ss").
     * Si déjà au bon format, on renvoie tel quel.
     */
    private String convertDateFormat(String dateVal) throws SQLException {
        if (dateVal == null || dateVal.isEmpty()) {
            // tu peux décider de mettre une date par défaut
            return "1970-01-01 00:00:00";
        }
        // Si c’est déjà "yyyy-MM-dd..." => on suppose correct
        if (dateVal.matches("\\d{4}-\\d{2}-\\d{2}.*")) {
            return dateVal;
        }
        // Sinon, on tente de parser "dd/MM/yyyy"
        if (!dateVal.matches("\\d{2}/\\d{2}/\\d{4}")) {
            throw new SQLException("Date invalide : " + dateVal);
        }
        try {
            DateTimeFormatter inFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate ld = LocalDate.parse(dateVal, inFmt);
            LocalDateTime ldt = ld.atTime(0, 0, 0);
            DateTimeFormatter dbFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return ldt.format(dbFmt);
        } catch (Exception e) {
            throw new SQLException("Impossible de convertir la date : " + dateVal, e);
        }
    }


    // Supprimer un document par ID
//    public boolean supprimerDocument(int documentId) {
//
//        String sql = "DELETE FROM DOCUMENTS WHERE DOC_ID = ?";
//
//        try (Connection conn = DatabaseConnection.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(sql)) {
//
//            pstmt.setInt(1, documentId);
//
//            int rowsDeleted = pstmt.executeUpdate();
//            if (rowsDeleted > 0) {
//                LOGGER.info("Document supprimé avec succès, ID : " + documentId);
//                return true;
//            } else {
//                LOGGER.warning("Document non trouvé pour suppression, ID : " + documentId);
//                return false;
//            }
//
//        } catch (SQLException e) {
//            LOGGER.log(Level.SEVERE, "Erreur lors de la suppression du document, ID : " + documentId, e);
//            return false;
//        }
//    }
    
    public boolean supprimerDocument(int documentId) {
        String sqlCheckEmprunt = 
            "SELECT COUNT(*) AS nb FROM EMPRUNT " +
            "WHERE DOC_ID = ? AND STATUS = 'Actif'";

        String sqlGetType =
            "SELECT DOC_TYPE FROM DOCUMENTS WHERE DOC_ID = ?";

        String sqlDeleteFilleLivre      = "DELETE FROM LIVRES WHERE DOC_ID = ?";
        String sqlDeleteFilleMagazine   = "DELETE FROM MAGAZINES WHERE DOC_ID = ?";
        String sqlDeleteFilleJournal    = "DELETE FROM JOURNAUX WHERE DOC_ID = ?";
        String sqlDeleteFilleMultimedia = "DELETE FROM MULTIMEDIA WHERE DOC_ID = ?";

        String sqlDeleteDocuments =
            "DELETE FROM DOCUMENTS WHERE DOC_ID = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);  // On gère la transaction manuellement

            // 1) Vérifier si le document est emprunté
            try (PreparedStatement pstmtCheck = conn.prepareStatement(sqlCheckEmprunt)) {
                pstmtCheck.setInt(1, documentId);
                ResultSet rsCheck = pstmtCheck.executeQuery();
                if (rsCheck.next()) {
                    int nbEmprunts = rsCheck.getInt("nb");
                    if (nbEmprunts > 0) {
                        // Le document est encore emprunté => on interdit la suppression
                        System.err.println("Impossible de supprimer : Le document (ID=" + documentId + 
                                           ") est actuellement emprunté (" + nbEmprunts + " emprunt(s) actif(s)).");
                        conn.rollback();
                        return false;
                    }
                }
            }

            // 2) Récupérer le type du document (Livre, Magazine, Journal, etc.)
            String docType = null;
            try (PreparedStatement pstmtType = conn.prepareStatement(sqlGetType)) {
                pstmtType.setInt(1, documentId);
                try (ResultSet rsType = pstmtType.executeQuery()) {
                    if (rsType.next()) {
                        docType = rsType.getString("DOC_TYPE");
                    } else {
                        // Document introuvable => on rollback
                        System.err.println("Document introuvable (ID=" + documentId + ").");
                        conn.rollback();
                        return false;
                    }
                }
            }

            // 3) Supprimer dans la table fille correspondante
            //    (Seulement si on connaît ce type)
            if (docType != null) {
                switch (docType) {
                    case "Livre":
                        try (PreparedStatement pstmtLivre = conn.prepareStatement(sqlDeleteFilleLivre)) {
                            pstmtLivre.setInt(1, documentId);
                            pstmtLivre.executeUpdate();
                        }
                        break;
                    case "Magazine":
                        try (PreparedStatement pstmtMag = conn.prepareStatement(sqlDeleteFilleMagazine)) {
                            pstmtMag.setInt(1, documentId);
                            pstmtMag.executeUpdate();
                        }
                        break;
                    case "Journal":
                        try (PreparedStatement pstmtJour = conn.prepareStatement(sqlDeleteFilleJournal)) {
                            pstmtJour.setInt(1, documentId);
                            pstmtJour.executeUpdate();
                        }
                        break;
                    case "Multimédia":
                    case "Multimedia":
                        try (PreparedStatement pstmtMulti = conn.prepareStatement(sqlDeleteFilleMultimedia)) {
                            pstmtMulti.setInt(1, documentId);
                            pstmtMulti.executeUpdate();
                        }
                        break;
                    default:
                        // Si c’est un type inconnu, on peut ignorer ou rollback
                        System.err.println("Type de document inconnu : " + docType + 
                                           ". Impossible de supprimer dans la table fille.");
                        conn.rollback();
                        return false;
                }
            }

            // 4) Supprimer dans documents
            try (PreparedStatement pstmtDocs = conn.prepareStatement(sqlDeleteDocuments)) {
                pstmtDocs.setInt(1, documentId);
                int rowsDeleted = pstmtDocs.executeUpdate();
                if (rowsDeleted > 0) {
                    conn.commit();
                    LOGGER.info("Document (et table fille) supprimés avec succès, ID : " + documentId);
                    return true;
                } else {
                    System.err.println("Document non trouvé pour suppression, ID : " + documentId);
                    conn.rollback();
                    return false;
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, 
                       "Erreur lors de la suppression du document (ID=" + documentId + ")", 
                       e);
            return false;
        }
    }


    // Mettre à jour la disponibilité d'un document
//    public boolean mettreAJourDisponibilite(int documentId, int nouvelleQuantiteDispo) {
//        String sql = "UPDATE DOCUMENTS SET DOC_QUANTITE_DISPO = ? WHERE DOC_ID = ?";
//
//        try (Connection conn = DatabaseConnection.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(sql)) {
//
//            pstmt.setInt(1, nouvelleQuantiteDispo);
//            pstmt.setInt(2, documentId);
//
//            int rowsUpdated = pstmt.executeUpdate();
//            if (rowsUpdated > 0) {
//                LOGGER.info("Disponibilité mise à jour pour le document, ID : " + documentId);
//                return true;
//            } else {
//                LOGGER.warning("Document non trouvé pour mise à jour de la disponibilité, ID : " + documentId);
//                return false;
//            }
//
//        } catch (SQLException e) {
//            LOGGER.log(Level.SEVERE, "Erreur lors de la mise à jour de la disponibilité pour le document, ID : " + documentId, e);
//            return false;
//        }
//    }

    // Valider un document avant l'ajout ou la modification
    private boolean validerDocument(Document document) {
        return document.getTitre() != null && !document.getTitre().isEmpty()
                && document.getQuantiteDispo() <= document.getQuantite();
    }
    
 // Méthode dans DocumentDAO pour lister les documents par type
    public List<Document> listerDocumentsParType(String type) {
        List<Document> documents = new ArrayList<>();
        String sql = "SELECT * FROM DOCUMENTS WHERE DOC_TYPE = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, type);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Document document = new Document(
                    rs.getInt("DOC_ID"),
                    rs.getString("DOC_TITRE"),
                    rs.getString("DOC_AUTEUR"),
                    rs.getString("DOC_DESCRIPTION"),
                    rs.getString("DOC_FICHE_TECHNIQUE"),
                    rs.getString("DOC_DATE_PUBLICATION"),
                    rs.getInt("DOC_QUANTITE"),
                    rs.getInt("DOC_QUANTITE_DISPO"),
                    rs.getString("DOC_TYPE")
                );
                documents.add(document);
            }

            if (documents.isEmpty()) {
                System.out.println("Aucun document trouvé pour le type : " + type);
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des documents par type : " + e.getMessage());
        }

        return documents;
    }
    
    // lister les fiches techniques par types
    public void afficherFichesTechniquesParType(String type) {
        String sql = "SELECT * FROM DOCUMENTS WHERE DOC_TYPE = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, type);
            ResultSet rs = pstmt.executeQuery();

            System.out.println("\n===== Fiches Techniques : " + type + " =====");
            while (rs.next()) {
                System.out.println("ID : " + rs.getInt("DOC_ID"));
                System.out.println("Titre : " + rs.getString("DOC_TITRE"));
                System.out.println("Auteur : " + rs.getString("DOC_AUTEUR"));
                System.out.println("Description : " + rs.getString("DOC_DESCRIPTION"));
                System.out.println("Description : " + rs.getString("DOC_FICHE_TECHNIQUE"));
                System.out.println("Date de Publication : " + rs.getString("DOC_DATE_PUBLICATION"));
                System.out.println("Quantité : " + rs.getInt("DOC_QUANTITE"));
                System.out.println("Quantité Disponible : " + rs.getInt("DOC_QUANTITE_DISPO"));
                System.out.println("-----------------------------");
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de l'affichage des fiches techniques : " + e.getMessage());
        }
    }
    
    
 // lister les documents disponibles
//    public List<Document> listerDocumentsDisponibles() {
//        List<Document> documentsDisponibles = new ArrayList<>();
//        String sql = "SELECT * FROM DOCUMENTS WHERE DOC_QUANTITE_DISPO > 0";
//
//        try (Connection conn = DatabaseConnection.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(sql);
//             ResultSet rs = pstmt.executeQuery()) {
//
//            while (rs.next()) {
//                Document document = new Document(
//                    rs.getInt("DOC_ID"),
//                    rs.getString("DOC_TITRE"),
//                    rs.getString("DOC_AUTEUR"),
//                    rs.getString("DOC_DESCRIPTION"),
//                    rs.getString("DOC_FICHE_TECHNIQUE"),
//                    rs.getString("DOC_DATE_PUBLICATION"),
//                    rs.getInt("DOC_QUANTITE"),
//                    rs.getInt("DOC_QUANTITE_DISPO"),
//                    rs.getString("DOC_TYPE")
//                );
//                documentsDisponibles.add(document);
//            }
//
//        } catch (SQLException e) {
//            System.err.println("Erreur lors de la récupération des documents disponibles : " + e.getMessage());
//        }
//        return documentsDisponibles;
//    }
    
    public List<Document> listerDocumentsDisponibles() {
        // Pareil => si tu veux la double select
        // On laisse le code existant tel quel.
        List<Document> documentsDisponibles = new ArrayList<>();
        String sql = "SELECT * FROM DOCUMENTS WHERE DOC_QUANTITE_DISPO > 0";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int docId = rs.getInt("DOC_ID");
                String docType = rs.getString("DOC_TYPE");
                String titre = rs.getString("DOC_TITRE");
                String auteur = rs.getString("DOC_AUTEUR");
                String description = rs.getString("DOC_DESCRIPTION");
                String ficheTechnique = rs.getString("DOC_FICHE_TECHNIQUE");
                String datePub = rs.getString("DOC_DATE_PUBLICATION");
                int quantite = rs.getInt("DOC_QUANTITE");
                int quantiteDispo = rs.getInt("DOC_QUANTITE_DISPO");

                Document doc;
                switch (docType) {
                    case "Livre":
                        doc = construireLivre(conn, docId, titre, auteur, description, ficheTechnique, datePub, quantite, quantiteDispo);
                        break;
                    case "Magazine":
                        doc = construireMagazine(conn, docId, titre, auteur, description, ficheTechnique, datePub, quantite, quantiteDispo);
                        break;
                    case "Journal":
                        doc = construireJournal(conn, docId, titre, auteur, description, ficheTechnique, datePub, quantite, quantiteDispo);
                        break;
                    case "Multimédia":
                    case "Multimedia":
                        doc = construireMultimedia(conn, docId, titre, auteur, description, ficheTechnique, datePub, quantite, quantiteDispo);
                        break;
                    default:
                        doc = new Document(docId, titre, auteur, description, ficheTechnique, datePub, quantite, quantiteDispo, docType);
                }

                if (doc != null) {
                    documentsDisponibles.add(doc);
                }
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des documents disponibles : " + e.getMessage());
        }
        return documentsDisponibles;
    }
    
    
    // Méthode pour rechercher des documents en fonction de plusieurs critères
    public List<Document> rechercherDocuments(String titre, String auteur, String description, String ficheTechnique, LocalDate datePublication, String type) {
        List<Document> documents = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM DOCUMENTS WHERE 1=1");

        // Ajout dynamique des critères à la requête
        if (titre != null && !titre.trim().isEmpty()) {
            sql.append(" AND DOC_TITRE LIKE ?");
        }
        if (auteur != null && !auteur.trim().isEmpty()) {
            sql.append(" AND DOC_AUTEUR LIKE ?");
        }
        if (description != null && !description.trim().isEmpty()) {
            sql.append(" AND DOC_DESCRIPTION LIKE ?");
        }
        if (ficheTechnique != null && !ficheTechnique.trim().isEmpty()) {
            sql.append(" AND DOC_FICHE_TECHNIQUE LIKE ?");
        }
        if (datePublication != null) {
            sql.append(" AND DOC_DATE_PUBLICATION = ?");
        }
        if (type != null && !type.trim().isEmpty()) {
            sql.append(" AND DOC_TYPE = ?");
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;

            // Définition des valeurs des paramètres
            if (titre != null && !titre.trim().isEmpty()) {
                pstmt.setString(paramIndex++, "%" + titre + "%");
            }
            if (auteur != null && !auteur.trim().isEmpty()) {
                pstmt.setString(paramIndex++, "%" + auteur + "%");
            }
            if (description != null && !description.trim().isEmpty()) {
                pstmt.setString(paramIndex++, "%" + description + "%");
            }
            if (ficheTechnique != null && !ficheTechnique.trim().isEmpty()) {
                pstmt.setString(paramIndex++, "%" + ficheTechnique + "%");
            }
            if (datePublication != null) {
                pstmt.setDate(paramIndex++, Date.valueOf(datePublication));
            }
            if (type != null && !type.trim().isEmpty()) {
                pstmt.setString(paramIndex++, type);
            }

            // Exécution de la requête
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Document document = new Document(
                    rs.getInt("DOC_ID"),
                    rs.getString("DOC_TITRE"),
                    rs.getString("DOC_AUTEUR"),
                    rs.getString("DOC_DESCRIPTION"),
                    rs.getString("DOC_FICHE_TECHNIQUE"),
                    rs.getString("DOC_DATE_PUBLICATION"),
                    rs.getInt("DOC_QUANTITE"),
                    rs.getInt("DOC_QUANTITE_DISPO"),
                    rs.getString("DOC_TYPE")
                );
                documents.add(document);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de la recherche des documents : " + e.getMessage());
        }

        return documents;
    }
    
    
 // Méthode pour recommander des documents basés sur Les types de documents empruntés par l'utilisateur et Exclure les documents déjà empruntés par l'utilisateu
    public List<Document> recommanderDocuments(int userId) {
        List<Document> recommandations = new ArrayList<>();
        String sql = "SELECT d.* " +
                     "FROM DOCUMENTS d " +
                     "LEFT JOIN EMPRUNT e ON d.DOC_ID = e.DOC_ID AND e.USER_ID = ? " +
                     "WHERE e.EMPRUNT_ID IS NULL " +
                     "AND d.DOC_TYPE IN ( " +
                     "    SELECT DISTINCT d2.DOC_TYPE " +
                     "    FROM EMPRUNT e2 " +
                     "    JOIN DOCUMENTS d2 ON e2.DOC_ID = d2.DOC_ID " +
                     "    WHERE e2.USER_ID = ? " +
                     ") " +
                     "AND d.DOC_QUANTITE_DISPO > 0";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Document document = new Document(
                    rs.getInt("DOC_ID"),
                    rs.getString("DOC_TITRE"),
                    rs.getString("DOC_AUTEUR"),
                    rs.getString("DOC_DESCRIPTION"),
                    rs.getString("DOC_FICHE_TECHNIQUE"),
                    rs.getString("DOC_DATE_PUBLICATION"),
                    rs.getInt("DOC_QUANTITE"),
                    rs.getInt("DOC_QUANTITE_DISPO"),
                    rs.getString("DOC_TYPE")
                );
                recommandations.add(document);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de la recommandation des documents : " + e.getMessage());
        }

        return recommandations;
    }




    


    
    
    

}
