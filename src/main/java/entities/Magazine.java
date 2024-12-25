package entities;

public class Magazine extends Document {
    private String frequencePublication; // Fréquence de publication (ex: mensuel, hebdomadaire)
    private int numeroParution; // Numéro de parution
    private String editeur; // Nom de l'éditeur

    // Constructeur complet avec validations
    public Magazine(int id, String titre, String auteur, String description, String ficheTechnique, String datePublication, 
                    int quantite, int quantiteDispo, String frequencePublication, int numeroParution, String editeur) {
        super(id, titre, auteur, description, ficheTechnique, datePublication, quantite, quantiteDispo, "Magazine"); // Type fixé
        if (frequencePublication == null || frequencePublication.trim().isEmpty()) {
            throw new IllegalArgumentException("La fréquence de publication ne peut pas être vide ou null.");
        }
        if (numeroParution <= 0) {
            throw new IllegalArgumentException("Le numéro de parution doit être un nombre positif.");
        }
        if (editeur == null || editeur.trim().isEmpty()) {
            throw new IllegalArgumentException("L'éditeur ne peut pas être vide ou null.");
        }
        this.frequencePublication = frequencePublication;
        this.numeroParution = numeroParution;
        this.editeur = editeur;
    }

    // Getters et Setters avec validations
    public String getFrequencePublication() {
        return frequencePublication;
    }

    public void setFrequencePublication(String frequencePublication) {
        if (frequencePublication == null || frequencePublication.trim().isEmpty()) {
            throw new IllegalArgumentException("La fréquence de publication ne peut pas être vide ou null.");
        }
        this.frequencePublication = frequencePublication;
    }

    public int getNumeroParution() {
        return numeroParution;
    }

    public void setNumeroParution(int numeroParution) {
        if (numeroParution <= 0) {
            throw new IllegalArgumentException("Le numéro de parution doit être un nombre positif.");
        }
        this.numeroParution = numeroParution;
    }

    public String getEditeur() {
        return editeur;
    }

    public void setEditeur(String editeur) {
        if (editeur == null || editeur.trim().isEmpty()) {
            throw new IllegalArgumentException("L'éditeur ne peut pas être vide ou null.");
        }
        this.editeur = editeur;
    }

    // Surcharge de la méthode toString
    @Override
    public String toString() {
        return super.toString() + ", frequencePublication='" + frequencePublication + '\'' +
                ", numeroParution=" + numeroParution +
                ", editeur='" + editeur + '\'';
    }

    // Méthode spécifique pour afficher les détails (optionnelle)
    public void afficherDetails() {
        System.out.println("Magazine: " + toString());
    }
}
