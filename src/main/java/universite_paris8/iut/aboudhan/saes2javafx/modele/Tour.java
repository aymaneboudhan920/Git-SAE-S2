package universite_paris8.iut.aboudhan.saes2javafx.modele;

import java.util.List;

public abstract class Tour {
    private double x, y, vitesseTir;
    private int portee, degats, prix, niveau;
    private String nomImage, nomArme;
    private int compteurRecharge = 0;

    public Tour(double x, double y, int portee, int degats, double vitesseTir, int prix, String nomImage, String nomArme) {
        this.x = x;
        this.y = y;
        this.portee = portee;
        this.degats = degats;
        this.vitesseTir = vitesseTir;
        this.prix = prix;
        this.nomImage = nomImage;
        this.nomArme = nomArme;
        this.niveau = 1;
    }

    public abstract void attaquer(List<Microbe> microbesActifs);

    public double getX() { return x; }
    public double getY() { return y; }
    public int getPortee() { return portee; }
    public int getDegats() { return degats; }
    public double getVitesseTir() { return vitesseTir; }
    public int getPrix() { return prix; }
    public String getNomImage() { return nomImage; }
    public int getNiveau() { return niveau; }
    public int getCompteurRecharge() { return compteurRecharge; }

    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public void setDegats(int degats) { this.degats = degats; }
    public void setPortee(int portee) { this.portee = portee; }
    public void incrementerNiveau() { this.niveau++; }
    public void setCompteurRecharge(int temps) { this.compteurRecharge = temps; }
}
