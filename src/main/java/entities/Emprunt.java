package entities;

import java.time.LocalDate;

public class Emprunt {
    private int id;
    private Utilisateur utilisateur;
    private int documentId;
    private LocalDate dateEmprunt;
    private LocalDate dateEcheance;
    private LocalDate dateRetour;
    private double fraisRetard;
    private String status; // Statut : "Actif" ou "Clôturé"

    // Constructeur complet (sans dateRetour ni fraisRetard)
    public Emprunt(int id, Utilisateur utilisateur, int documentId, LocalDate dateEmprunt, LocalDate dateEcheance, String status) {
        if (dateEmprunt == null || dateEcheance == null || status == null || utilisateur == null) {
            throw new IllegalArgumentException("Les champs dateEmprunt, dateEcheance, utilisateur et status sont obligatoires.");
        }
        if (dateEcheance.isBefore(dateEmprunt)) {
            throw new IllegalArgumentException("La date d'échéance ne peut pas être antérieure à la date d'emprunt.");
        }
        this.id = id;
        this.utilisateur = utilisateur;
        this.documentId = documentId;
        this.dateEmprunt = dateEmprunt;
        this.dateEcheance = dateEcheance;
        this.status = status;
    }

    // Constructeur pour un emprunt retourné (avec date de retour et frais de retard)
    public Emprunt(int id, Utilisateur utilisateur, int documentId, LocalDate dateEmprunt, LocalDate dateEcheance, LocalDate dateRetour, double fraisRetard, String status) {
        this(id, utilisateur, documentId, dateEmprunt, dateEcheance, status);
        if (dateRetour != null && dateRetour.isBefore(dateEcheance)) {
            throw new IllegalArgumentException("La date de retour ne peut pas être antérieure à la date d'échéance.");
        }
        if (fraisRetard < 0) {
            throw new IllegalArgumentException("Les frais de retard ne peuvent pas être négatifs.");
        }
        this.dateRetour = dateRetour;
        this.fraisRetard = fraisRetard;
    }

    // Constructeur vide (optionnel)
    public Emprunt() {
    }

    // Getters et setters avec validations
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        if (utilisateur == null) {
            throw new IllegalArgumentException("L'utilisateur ne peut pas être null.");
        }
        this.utilisateur = utilisateur;
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
        if (dateEmprunt == null) {
            throw new IllegalArgumentException("La date d'emprunt ne peut pas être null.");
        }
        this.dateEmprunt = dateEmprunt;
    }

    public LocalDate getDateEcheance() {
        return dateEcheance;
    }

    public void setDateEcheance(LocalDate dateEcheance) {
        if (dateEcheance == null || dateEcheance.isBefore(this.dateEmprunt)) {
            throw new IllegalArgumentException("La date d'échéance est invalide.");
        }
        this.dateEcheance = dateEcheance;
    }

    public LocalDate getDateRetour() {
        return dateRetour;
    }

    public void setDateRetour(LocalDate dateRetour) {
        if (dateRetour != null && dateRetour.isBefore(this.dateEcheance)) {
            throw new IllegalArgumentException("La date de retour est invalide.");
        }
        this.dateRetour = dateRetour;
    }

    public double getFraisRetard() {
        return fraisRetard;
    }

    public void setFraisRetard(double fraisRetard) {
        if (fraisRetard < 0) {
            throw new IllegalArgumentException("Les frais de retard ne peuvent pas être négatifs.");
        }
        this.fraisRetard = fraisRetard;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        if (status == null || (!status.equals("Actif") && !status.equals("Cloturee"))) {
            throw new IllegalArgumentException("Le statut doit être 'Actif' ou 'Clôturé'.");
        }
        this.status = status;
    }

    @Override
    public String toString() {
        return "Emprunt{" +
                "id=" + id +
                ", utilisateur=" + utilisateur +
                ", documentId=" + documentId +
                ", dateEmprunt=" + dateEmprunt +
                ", dateEcheance=" + dateEcheance +
                (dateRetour != null ? ", dateRetour=" + dateRetour : "") +
                (fraisRetard > 0 ? ", fraisRetard=" + fraisRetard : "") +
                ", status='" + status + '\'' +
                '}';
    }
}
