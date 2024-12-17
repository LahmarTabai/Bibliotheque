package entities;

public abstract class Document {
    protected int id;
    protected String titre;
    protected String auteur;
    protected String description;
    protected String datePublication;
    protected int quantite;
    protected int quantiteDispo;

    // Constructeur
    public Document(int id, String titre, String auteur, String description, String datePublication, int quantite, int quantiteDispo) {
        this.id = id;
        this.titre = titre;
        this.auteur = auteur;
        this.description = description;
        this.datePublication = datePublication;
        this.quantite = quantite;
        this.quantiteDispo = quantiteDispo;
    }

    // Getters et Setters
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
        this.titre = titre;
    }

    public String getAuteur() {
        return auteur;
    }

    public void setAuteur(String auteur) {
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
        this.datePublication = datePublication;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public int getQuantiteDispo() {
        return quantiteDispo;
    }

    public void setQuantiteDispo(int quantiteDispo) {
        this.quantiteDispo = quantiteDispo;
    }

    // Méthode abstraite à implémenter dans les sous-classes
    public abstract void afficherDetails();

    // Méthode toString pour afficher les détails d'un document
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
                '}';
    }
}
