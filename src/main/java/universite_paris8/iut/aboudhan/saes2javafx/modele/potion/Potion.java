package universite_paris8.iut.aboudhan.saes2javafx.modele.potion;
import universite_paris8.iut.aboudhan.saes2javafx.modele.jeu.Environnement;

public abstract class Potion {
    private String nom;
    private int prixAchat;

    public Potion(String nom, int prixAchat) {
        this.nom = nom;
        this.prixAchat = prixAchat;
    }
    public abstract void appliquerEffet(Environnement env);
}