package entities;

public class Multimedia extends Document {
    private String typeMultimedia; // Par exemple : CD ou DVD
    private int dureeTotale; // Durée totale en minutes

    // Constructeur complet avec validations
    public Multimedia(int id, String titre, String auteur, String description, String datePublication, 
                      int quantite, int quantiteDispo, String typeMultimedia, int dureeTotale) {
        super(id, titre, auteur, description, datePublication, quantite, quantiteDispo, "Multimedia"); // Type fixé
        if (typeMultimedia == null || typeMultimedia.trim().isEmpty()) {
            throw new IllegalArgumentException("Le type de multimédia ne peut pas être vide ou null.");
        }
        if (dureeTotale <= 0) {
            throw new IllegalArgumentException("La durée totale doit être un nombre positif.");
        }
        this.typeMultimedia = typeMultimedia;
        this.dureeTotale = dureeTotale;
    }

    // Getters et Setters avec validations
    public String getTypeMultimedia() {
        return typeMultimedia;
    }

    public void setTypeMultimedia(String typeMultimedia) {
        if (typeMultimedia == null || typeMultimedia.trim().isEmpty()) {
            throw new IllegalArgumentException("Le type de multimédia ne peut pas être vide ou null.");
        }
        this.typeMultimedia = typeMultimedia;
    }

    public int getDureeTotale() {
        return dureeTotale;
    }

    public void setDureeTotale(int dureeTotale) {
        if (dureeTotale <= 0) {
            throw new IllegalArgumentException("La durée totale doit être un nombre positif.");
        }
        this.dureeTotale = dureeTotale;
    }

    // Surcharge de la méthode toString
    @Override
    public String toString() {
        return super.toString() + ", typeMultimedia='" + typeMultimedia + '\'' +
                ", dureeTotale=" + dureeTotale + " minutes";
    }

    // Méthode spécifique pour afficher les détails (optionnelle)
    public void afficherDetails() {
        System.out.println("Multimedia: " + toString());
    }
}
