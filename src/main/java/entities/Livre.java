package entities;

public class Livre extends Document {
    private int nbPages;
    private String genreLitteraire;

    public Livre(int id, String titre, String auteur, String description, String datePublication, int quantite, int quantiteDispo, int nbPages, String genreLitteraire) {
        super(id, titre, auteur, description, datePublication, quantite, quantiteDispo);
        this.nbPages = nbPages;
        this.genreLitteraire = genreLitteraire;
    }

    public int getNbPages() {
        return nbPages;
    }

    public void setNbPages(int nbPages) {
        this.nbPages = nbPages;
    }

    public String getGenreLitteraire() {
        return genreLitteraire;
    }

    public void setGenreLitteraire(String genreLitteraire) {
        this.genreLitteraire = genreLitteraire;
    }

    @Override
    public void afficherDetails() {
        System.out.println("Livre - " + toString() + ", Pages: " + nbPages + ", Genre: " + genreLitteraire);
    }
}
