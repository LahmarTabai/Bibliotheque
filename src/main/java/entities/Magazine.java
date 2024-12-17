package entities;

public class Magazine extends Document {
    private String frequencePublication;
    private int numeroParution;
    private String editeur;

    public Magazine(int id, String titre, String auteur, String description, String datePublication, int quantite, int quantiteDispo, String frequencePublication, int numeroParution, String editeur) {
        super(id, titre, auteur, description, datePublication, quantite, quantiteDispo);
        this.frequencePublication = frequencePublication;
        this.numeroParution = numeroParution;
        this.editeur = editeur;
    }

    public String getFrequencePublication() {
        return frequencePublication;
    }

    public void setFrequencePublication(String frequencePublication) {
        this.frequencePublication = frequencePublication;
    }

    public int getNumeroParution() {
        return numeroParution;
    }

    public void setNumeroParution(int numeroParution) {
        this.numeroParution = numeroParution;
    }

    public String getEditeur() {
        return editeur;
    }

    public void setEditeur(String editeur) {
        this.editeur = editeur;
    }

    @Override
    public void afficherDetails() {
        System.out.println("Magazine - " + toString() + ", Fréquence: " + frequencePublication + ", Numéro: " + numeroParution + ", Éditeur: " + editeur);
    }
}
