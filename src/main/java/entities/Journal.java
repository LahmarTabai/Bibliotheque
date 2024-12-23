package entities;

public class Journal extends Document {
    private String datePublicationSpecifique;

    // Constructeur complet
    public Journal(int id, String titre, String auteur, String description, String datePublication, 
                   int quantite, int quantiteDispo, String datePublicationSpecifique) {
        super(id, titre, auteur, description, datePublication, quantite, quantiteDispo, "Journal"); // Type fixé
        if (datePublicationSpecifique == null || datePublicationSpecifique.isEmpty()) {
            throw new IllegalArgumentException("La date de publication spécifique ne peut pas être vide.");
        }
        this.datePublicationSpecifique = datePublicationSpecifique;
    }

    // Getter
    public String getDatePublicationSpecifique() {
        return datePublicationSpecifique;
    }

    // Setter avec validation
    public void setDatePublicationSpecifique(String datePublicationSpecifique) {
        if (datePublicationSpecifique == null || datePublicationSpecifique.isEmpty()) {
            throw new IllegalArgumentException("La date de publication spécifique ne peut pas être vide.");
        }
        this.datePublicationSpecifique = datePublicationSpecifique;
    }

    // Surcharge de la méthode toString pour inclure les détails spécifiques au Journal
    @Override
    public String toString() {
        return super.toString() + ", datePublicationSpecifique='" + datePublicationSpecifique + "'";
    }

    // Méthode spécifique pour afficher les détails (optionnelle)
    public void afficherDetails() {
        System.out.println("Journal: " + toString());
    }
}
