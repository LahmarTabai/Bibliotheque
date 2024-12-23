package entities;

public class Document {
    private int id;
    private String titre;
    private String auteur;
    private String description;
    private String datePublication;
    private int quantite;
    private int quantiteDispo;
    private String type;

    // Constructeur complet
    public Document(int id, String titre, String auteur, String description, String datePublication, int quantite, int quantiteDispo, String type) {
        if (quantite < 0 || quantiteDispo < 0) {
            throw new IllegalArgumentException("Quantité et quantité disponible doivent être positives.");
        }
        this.id = id;
        this.titre = titre;
        this.auteur = auteur;
        this.description = description;
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

    public String getDatePublication() {
        return datePublication;
    }

    public void setDatePublication(String datePublication) {
        if (datePublication == null || !datePublication.matches("\\d{4}-\\d{2}-\\d{2}")) {
            throw new IllegalArgumentException("La date de publication doit être au format AAAA-MM-JJ.");
        }
        this.datePublication = datePublication;
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
                ", datePublication='" + datePublication + '\'' +
                ", quantite=" + quantite +
                ", quantiteDispo=" + quantiteDispo +
                ", type='" + type + '\'' +
                '}';
    }
}
