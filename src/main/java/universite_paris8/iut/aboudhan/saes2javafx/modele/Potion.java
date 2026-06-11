package universite_paris8.iut.aboudhan.saes2javafx.modele;

public abstract class Potion {
    private String nom;
    private int prixAchat;

    public Potion(String nom, int prixAchat) {
        this.nom = nom;
        this.prixAchat = prixAchat;
    }

    public String getNom() { return nom; }
    public int getPrixAchat() { return prixAchat; }

    // Chaque potion implémentera cette méthode pour appliquer son pouvoir
    public abstract void appliquerEffet(Environnement env);
}