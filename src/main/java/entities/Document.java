package entities;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class Document {
    private int id;
    private String titre;
    private String auteur;
    private String description;
    private String ficheTechnique;
    private String datePublication;
    private int quantite;
    private int quantiteDispo;
    private String type;

    // Constructeur complet
    public Document(int id, String titre, String auteur, String description, String ficheTechnique, String datePublication, int quantite, int quantiteDispo, String type) {
        if (quantite < 0 || quantiteDispo < 0) {
            throw new IllegalArgumentException("Quantité et quantité disponible doivent être positives.");
        }
        this.id = id;
        this.titre = titre;
        this.auteur = auteur;
        this.description = description;
        this.ficheTechnique = ficheTechnique;
        this.datePublication = datePublication;
        this.quantite = quantite;
        this.quantiteDispo = quantiteDispo;
        this.type = type;
    }

    // Constructeur vide (optionnel)
    public Document() {
    }

    // Getters et Setters avec validations
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        if (titre == null || titre.isEmpty()) {
            throw new IllegalArgumentException("Le titre ne peut pas être vide.");
        }
        this.titre = titre;
    }

    public String getAuteur() {
        return auteur;
    }

    public void setAuteur(String auteur) {
        if (auteur == null || auteur.isEmpty()) {
            throw new IllegalArgumentException("L'auteur ne peut pas être vide.");
        }
        this.auteur = auteur;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getFicheTechnique() {
        return ficheTechnique;
    }

    public void setFicheTechnique(String ficheTechnique) {
        this.ficheTechnique = ficheTechnique;
    }

    public String getDatePublication() {
        return datePublication;
    }

    public void setDatePublication(String datePublication) {
        if (datePublication == null || !datePublication.matches("\\d{2}/\\d{2}/\\d{4}")) {
            throw new IllegalArgumentException("La date de publication doit être au format JJ/MM/AAAA.");
        }

        try {
            // Convertir la date entrée en LocalDate
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate localDate = LocalDate.parse(datePublication, inputFormatter);

            // Ajouter une heure fictive pour l'enregistrement (par exemple, 00:00:00)
            LocalDateTime localDateTime = localDate.atTime(0, 0, 0);

            // Convertir au format attendu par MySQL (AAAA-MM-JJ HH:MM:SS)
            DateTimeFormatter dbFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            this.datePublication = localDateTime.format(dbFormatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("La date de publication est invalide, veuillez respecter le format JJ/MM/AAAA.");
        }
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        if (quantite < 0) {
            throw new IllegalArgumentException("La quantité ne peut pas être négative.");
        }
        this.quantite = quantite;
    }

    public int getQuantiteDispo() {
        return quantiteDispo;
    }

    public void setQuantiteDispo(int quantiteDispo) {
        if (quantiteDispo < 0) {
            throw new IllegalArgumentException("La quantité disponible ne peut pas être négative.");
        }
        this.quantiteDispo = quantiteDispo;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Document{" +
                "id=" + id +
                ", titre='" + titre + '\'' +
                ", auteur='" + auteur + '\'' +
                ", description='" + description + '\'' +
                ", Fiche Technique='" + ficheTechnique + '\'' +
                ", datePublication='" + datePublication + '\'' +
                ", quantite=" + quantite +
                ", quantiteDispo=" + quantiteDispo +
                ", type='" + type + '\'' +
                '}';
    }
}
