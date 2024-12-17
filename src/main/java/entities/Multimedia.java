package entities;

public class Multimedia extends Document {
    private String typeMultimedia; // CD ou DVD
    private int dureeTotale;

    public Multimedia(int id, String titre, String auteur, String description, String datePublication, int quantite, int quantiteDispo, String typeMultimedia, int dureeTotale) {
        super(id, titre, auteur, description, datePublication, quantite, quantiteDispo);
        this.typeMultimedia = typeMultimedia;
        this.dureeTotale = dureeTotale;
    }

    public String getTypeMultimedia() {
        return typeMultimedia;
    }

    public void setTypeMultimedia(String typeMultimedia) {
        this.typeMultimedia = typeMultimedia;
    }

    public int getDureeTotale() {
        return dureeTotale;
    }

    public void setDureeTotale(int dureeTotale) {
        this.dureeTotale = dureeTotale;
    }

    @Override
    public void afficherDetails() {
        System.out.println("Multimédia - " + toString() + ", Type: " + typeMultimedia + ", Durée: " + dureeTotale + " minutes");
    }
}
