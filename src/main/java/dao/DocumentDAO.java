package dao;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import database.DatabaseConnection;
import entities.Document;

public class DocumentDAO {

    private static final Logger LOGGER = Logger.getLogger(DocumentDAO.class.getName());

    // Lister tous les documents
    public List<Document> listerTousLesDocuments() {
        List<Document> documents = new ArrayList<>();
        String sql = "SELECT * FROM DOCUMENTS";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Document document = new Document(
                        rs.getInt("DOC_ID"),
                        rs.getString("DOC_TITRE"),
                        rs.getString("DOC_AUTEUR"),
                        rs.getString("DOC_DESCRIPTION"),
                        rs.getString("DOC_DATE_PUBLICATION"),
                        rs.getInt("DOC_QUANTITE"),
                        rs.getInt("DOC_QUANTITE_DISPO"),
                        rs.getString("DOC_TYPE")
                );
                documents.add(document);
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

        String sql = "INSERT INTO DOCUMENTS (DOC_TITRE, DOC_AUTEUR, DOC_DESCRIPTION, DOC_DATE_PUBLICATION, DOC_QUANTITE, DOC_QUANTITE_DISPO, DOC_TYPE) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, document.getTitre());
            pstmt.setString(2, document.getAuteur());
            pstmt.setString(3, document.getDescription());
            pstmt.setString(4, document.getDatePublication());
            pstmt.setInt(5, document.getQuantite());
            pstmt.setInt(6, document.getQuantiteDispo());
            pstmt.setString(7, document.getType());

            pstmt.executeUpdate();
            LOGGER.info("Document ajouté avec succès : " + document);
            return true;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'ajout du document : " + document, e);
            return false;
        }
    }

    // Modifier un document existant
    public boolean modifierDocument(Document document) {
        if (!validerDocument(document)) {
            LOGGER.warning("Validation échouée pour le document : " + document);
            return false;
        }

        String sql = "UPDATE DOCUMENTS SET DOC_TITRE = ?, DOC_AUTEUR = ?, DOC_DESCRIPTION = ?, DOC_DATE_PUBLICATION = ?, DOC_QUANTITE = ?, DOC_QUANTITE_DISPO = ?, DOC_TYPE = ? " +
                     "WHERE DOC_ID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, document.getTitre());
            pstmt.setString(2, document.getAuteur());
            pstmt.setString(3, document.getDescription());
            pstmt.setString(4, document.getDatePublication());
            pstmt.setInt(5, document.getQuantite());
            pstmt.setInt(6, document.getQuantiteDispo());
            pstmt.setString(7, document.getType());
            pstmt.setInt(8, document.getId());

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                LOGGER.info("Document modifié avec succès : " + document);
                return true;
            } else {
                LOGGER.warning("Document non trouvé pour modification : " + document);
                return false;
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la modification du document : " + document, e);
            return false;
        }
    }

    // Supprimer un document par ID
    public boolean supprimerDocument(int documentId) {
        String sql = "DELETE FROM DOCUMENTS WHERE DOC_ID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, documentId);

            int rowsDeleted = pstmt.executeUpdate();
            if (rowsDeleted > 0) {
                LOGGER.info("Document supprimé avec succès, ID : " + documentId);
                return true;
            } else {
                LOGGER.warning("Document non trouvé pour suppression, ID : " + documentId);
                return false;
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la suppression du document, ID : " + documentId, e);
            return false;
        }
    }

    // Mettre à jour la disponibilité d'un document
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
    public List<Document> listerDocumentsDisponibles() {
        List<Document> documentsDisponibles = new ArrayList<>();
        String sql = "SELECT * FROM DOCUMENTS WHERE DOC_QUANTITE_DISPO > 0";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Document document = new Document(
                    rs.getInt("DOC_ID"),
                    rs.getString("DOC_TITRE"),
                    rs.getString("DOC_AUTEUR"),
                    rs.getString("DOC_DESCRIPTION"),
                    rs.getString("DOC_DATE_PUBLICATION"),
                    rs.getInt("DOC_QUANTITE"),
                    rs.getInt("DOC_QUANTITE_DISPO"),
                    rs.getString("DOC_TYPE")
                );
                documentsDisponibles.add(document);
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des documents disponibles : " + e.getMessage());
        }
        return documentsDisponibles;
    }
    
    
    // Méthode pour rechercher des documents en fonction de plusieurs critères
    public List<Document> rechercherDocuments(String titre, String auteur, String description, LocalDate datePublication, String type) {
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



    


    
    
    

}
