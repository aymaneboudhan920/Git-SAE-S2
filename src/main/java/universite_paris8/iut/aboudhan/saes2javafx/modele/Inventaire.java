package universite_paris8.iut.aboudhan.saes2javafx.modele;

public class Inventaire {
    private final String[] caseInventaire;

    public Inventaire(int taille) {
        this.caseInventaire = new String[taille];
    }

    // Trouve le premier index vide (null)
    public int getPremiereCaseLibre() {
        for (int i = 0; i < caseInventaire.length; i++) {
            if (caseInventaire[i] == null) {
                return i;
            }
        }
        return -1; // Inventaire plein
    }

    public void setTourCase(int index, String typeTour) {
        if (index >= 0 && index < caseInventaire.length) {
            this.caseInventaire[index] = typeTour;
        }
    }

    public String getTourCase(int index) {
        if (index >= 0 && index < caseInventaire.length) {
            return this.caseInventaire[index];
        }
        return null;
    }
}