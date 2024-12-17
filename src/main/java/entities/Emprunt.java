package entities;

import java.time.LocalDate;

public class Emprunt {
    private int id;
    private int userId;       // Référence vers un utilisateur (ID)
    private int documentId;   // Référence vers un document
    private LocalDate dateEmprunt;
    private LocalDate dateRetour;
    private LocalDate dateEcheance;
    private double fraisRetard;
    private String status;    // Par exemple : "Actif", "Clôturé"

    private Utilisateur utilisateur; // Liaison vers l'objet Utilisateur

    // Constructeur complet sans Utilisateur (pour compatibilité avec l’ancien code)
    public Emprunt(int id, int userId, int documentId, LocalDate dateEmprunt, LocalDate dateEcheance, LocalDate dateRetour, double fraisRetard, String status) {
        this.id = id;
        this.userId = userId;
        this.documentId = documentId;
        this.dateEmprunt = dateEmprunt;
        this.dateEcheance = dateEcheance;
        this.dateRetour = dateRetour;
        this.fraisRetard = fraisRetard;
        this.status = status;
    }

    // Nouveau constructeur avec Utilisateur
    public Emprunt(int id, Utilisateur utilisateur, int documentId, LocalDate dateEmprunt, LocalDate dateEcheance, LocalDate dateRetour, double fraisRetard, String status) {
        this.id = id;
        this.utilisateur = utilisateur;
        this.userId = utilisateur.getId(); // On initialise userId à partir de l'objet Utilisateur
        this.documentId = documentId;
        this.dateEmprunt = dateEmprunt;
        this.dateEcheance = dateEcheance;
        this.dateRetour = dateRetour;
        this.fraisRetard = fraisRetard;
        this.status = status;
    }

    // Getters et Setters pour Utilisateur
    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
        this.userId = utilisateur.getId(); // Mettre à jour userId pour garantir la cohérence
    }

    // Autres Getters et Setters existants
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getDocumentId() {
        return documentId;
    }

    public void setDocumentId(int documentId) {
        this.documentId = documentId;
    }

    public LocalDate getDateEmprunt() {
        return dateEmprunt;
    }

    public void setDateEmprunt(LocalDate dateEmprunt) {
        this.dateEmprunt = dateEmprunt;
    }

    public LocalDate getDateRetour() {
        return dateRetour;
    }

    public void setDateRetour(LocalDate dateRetour) {
        this.dateRetour = dateRetour;
    }

    public LocalDate getDateEcheance() {
        return dateEcheance;
    }

    public void setDateEcheance(LocalDate dateEcheance) {
        this.dateEcheance = dateEcheance;
    }

    public double getFraisRetard() {
        return fraisRetard;
    }

    public void setFraisRetard(double fraisRetard) {
        this.fraisRetard = fraisRetard;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Méthode pour afficher les détails de l'emprunt
    public void afficherDetails() {
        System.out.println("Emprunt - ID: " + id + 
                           ", Utilisateur: " + (utilisateur != null ? utilisateur.getNom() + " " + utilisateur.getPrenom() : "ID: " + userId) +
                           ", Document ID: " + documentId +
                           ", Date Emprunt: " + dateEmprunt + 
                           ", Date Échéance: " + dateEcheance +
                           ", Date Retour: " + (dateRetour != null ? dateRetour : "Non retourné") +
                           ", Frais de retard: " + fraisRetard + 
                           ", Status: " + status);
    }
}
