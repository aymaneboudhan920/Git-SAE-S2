package universite_paris8.iut.aboudhan.saes2javafx.modele.tour;

import universite_paris8.iut.aboudhan.saes2javafx.modele.jeu.Environnement;

public abstract class Tour {
    private double x, y, degats, vitesseTir;
    private final int portee;
    private int prix, niveau;
    private String nomImage;
    private double tempsRechargeRestant = 0.0;
    private double multiplicateurVitesse = 1.0;
    private double tempsEtourdissementRestant = 0;

    public Tour(double x, double y, int portee, double degats, double vitesseTir, int prix, String nomImage) {
        this.x = x;
        this.y = y;
        this.portee = portee;
        this.degats = degats;
        this.vitesseTir = vitesseTir;
        this.prix = prix;
        this.nomImage = nomImage;
        this.niveau = 1;
    }

    public abstract void attaquer(Environnement env);

    public double getX() { return x; }
    public double getY() { return y; }
    public int getPortee() { return portee; }
    public double getDegats() { return degats; }
    public double getVitesseTir() { return vitesseTir; }
    public int getPrix() { return prix; }
    public String getNomImage() { return nomImage; }
    public int getNiveau() { return niveau; }
    public double getTempsEtourdissementRestant() {
        return this.tempsEtourdissementRestant;
    }

    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public void setDegats(double degats) { this.degats = degats; }
    public void incrementerNiveau() { this.niveau++; }

    public void setMultiplicateurVitesse(double coeff) { this.multiplicateurVitesse = coeff; }
    public double getMultiplicateurVitesse() { return this.multiplicateurVitesse; }

    public void mettreAJourRecharge(double t) {
        if (tempsEtourdissementRestant > 0) {
            tempsEtourdissementRestant -= t;
            return;
        }
        if (this.tempsRechargeRestant > 0) {
            this.tempsRechargeRestant -= t;
            if (this.tempsRechargeRestant < 0) {
                this.tempsRechargeRestant = 0;
            }
        }
    }

    public void etourdir(double duree) {
        this.tempsEtourdissementRestant = duree;
        this.reinitialiserAttaque();
    }

    public boolean peutAttaquer() {
        return this.tempsRechargeRestant <= 0 && this.tempsEtourdissementRestant <= 0;
    }

    public void recharger() {
        this.tempsRechargeRestant = (1.0 / (this.vitesseTir * this.multiplicateurVitesse));
    }

    public int calculerPrixAmelioration() {
        return this.prix * (int) Math.pow(2, this.niveau);
    }

    public int calculerValeurVente() {
        return (this.prix / 2) * (int) Math.pow(2, this.niveau - 1);
    }

    public boolean peutEtreAmelioree() {
        return this.niveau < 5;
    }

    public void ameliorer() {
        this.incrementerNiveau();
        this.setDegats(this.getDegats() * 1.5);
        this.vitesseTir = this.vitesseTir * 1.1;
    }

    public void reinitialiserAttaque() {}
}