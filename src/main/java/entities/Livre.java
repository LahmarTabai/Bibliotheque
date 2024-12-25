package entities;

public class Livre extends Document {
    private int nbPages;
    private String genreLitteraire;

    // Constructeur complet avec validations
    public Livre(int id, String titre, String auteur, String description, String ficheTechnique, String datePublication, 
                 int quantite, int quantiteDispo, int nbPages, String genreLitteraire) {
        super(id, titre, auteur, description, ficheTechnique, datePublication, quantite, quantiteDispo, "Livre"); // Type fixé
        if (nbPages <= 0) {
            throw new IllegalArgumentException("Le nombre de pages doit être supérieur à 0.");
        }
        if (genreLitteraire == null || genreLitteraire.trim().isEmpty()) {
            throw new IllegalArgumentException("Le genre littéraire ne peut pas être vide ou null.");
        }
        this.nbPages = nbPages;
        this.genreLitteraire = genreLitteraire;
    }

    // Getters et Setters avec validations
    public int getNbPages() {
        return nbPages;
    }

    public void setNbPages(int nbPages) {
        if (nbPages <= 0) {
            throw new IllegalArgumentException("Le nombre de pages doit être supérieur à 0.");
        }
        this.nbPages = nbPages;
    }

    public String getGenreLitteraire() {
        return genreLitteraire;
    }

    public void setGenreLitteraire(String genreLitteraire) {
        if (genreLitteraire == null || genreLitteraire.trim().isEmpty()) {
            throw new IllegalArgumentException("Le genre littéraire ne peut pas être vide ou null.");
        }
        this.genreLitteraire = genreLitteraire;
    }

    // Surcharge de la méthode toString
    @Override
    public String toString() {
        return super.toString() + ", nbPages=" + nbPages + ", genreLitteraire='" + genreLitteraire + "'";
    }

    // Méthode spécifique pour afficher les détails (optionnelle)
    public void afficherDetails() {
        System.out.println("Livre: " + toString());
    }
}
