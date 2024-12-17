package entities;

public class Journal extends Document {
    private String datePublicationSpecifique;

    public Journal(int id, String titre, String auteur, String description, String datePublication, int quantite, int quantiteDispo, String datePublicationSpecifique) {
        super(id, titre, auteur, description, datePublication, quantite, quantiteDispo);
        this.datePublicationSpecifique = datePublicationSpecifique;
    }

    public String getDatePublicationSpecifique() {
        return datePublicationSpecifique;
    }

    public void setDatePublicationSpecifique(String datePublicationSpecifique) {
        this.datePublicationSpecifique = datePublicationSpecifique;
    }

    @Override
    public void afficherDetails() {
        System.out.println("Journal - " + toString() + ", Date spécifique: " + datePublicationSpecifique);
    }
}
