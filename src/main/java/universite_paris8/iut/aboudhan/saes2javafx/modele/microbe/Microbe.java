package universite_paris8.iut.aboudhan.saes2javafx.modele.microbe;

import universite_paris8.iut.aboudhan.saes2javafx.modele.jeu.Waypoint;
import universite_paris8.iut.aboudhan.saes2javafx.modele.tour.Tour;

import java.util.ArrayList;

public class Microbe {
    public double x, y, vitesseDeBase, vitesseActu;
    public double pv, pvMax;
    public int recompense, infection;
    public Waypoint waypointCible;
    private String type;
    private boolean estGele = false;
    private boolean estRalenti = false;
    private boolean aDeclencheRage = false;
    private boolean doitAfficherEclair = false;
    private final ArrayList<Tour> tourAReset = new ArrayList<>();;

    private double chronoCycle = 0;
    private double tempsProchainSommeil;
    private int etatVaricelle = 0;

    public Microbe(double v, double pm, int r, int i, String type, Waypoint waypointDepart){
        this.vitesseDeBase = v;
        this.vitesseActu = v;
        this.pv = pm;
        this.pvMax = pm;
        this.recompense = r;
        this.infection = i;
        this.type = type;

        if (this.type.equals("VARICELLE")) {
            this.tempsProchainSommeil = 2 + (Math.random() * 3);
        }

        if (waypointDepart != null) {
            this.x = waypointDepart.getX();
            this.y = waypointDepart.getY();
            this.waypointCible = waypointDepart.obtenirProchainWaypoint();
        } else {
            this.x = 0;
            this.y = 0;
            this.waypointCible = null;
        }
    }

    public void deplacer(){
        if (this.type.equals("VARICELLE")) {
            mettreAJourComportementVaricelle(0.012);
        }
        if (this.estGele || this.vitesseActu == 0) return;
        if (this.waypointCible == null) return;

        double diffX = this.waypointCible.getX() - this.x;
        double diffY = this.waypointCible.getY() - this.y;
        double distance = Math.sqrt(diffX * diffX + diffY * diffY);

        if (distance > this.vitesseActu){
            this.x += (diffX / distance) * this.vitesseActu;
            this.y += (diffY / distance) * this.vitesseActu;
        } else {
            this.x = this.waypointCible.getX();
            this.y = this.waypointCible.getY();
            this.waypointCible = waypointCible.obtenirProchainWaypoint();

            if(waypointCible != null){
                double distanceSaut = Math.sqrt(
                        Math.pow(waypointCible.getX() - this.x, 2) +
                                Math.pow(waypointCible.getY() - this.y, 2)
                );
                if (distanceSaut > 70) {
                    this.x = waypointCible.getX();
                    this.y = waypointCible.getY();
                }
            }
        }
    }

    private void mettreAJourComportementVaricelle(double tps) {
        if (!this.type.equals("VARICELLE") || this.estGele) return;

        chronoCycle += tps;

        switch (this.etatVaricelle) {
            case 0: // ÉTAT NORMAL
                if (chronoCycle >= tempsProchainSommeil) {
                    this.etatVaricelle = 1; // Passe en sommeil
                    this.chronoCycle = 0;
                    this.vitesseActu = 0;   // S'arrête brutalement
                } else {
                    this.vitesseActu = this.vitesseDeBase;
                    if (estRalenti)
                        this.vitesseActu = this.vitesseActu/2;
                }
                break;

            case 1: // ÉTAT SOMMEIL
                if (chronoCycle >= 2) {
                    this.etatVaricelle = 2; // Passe en accélération brutale
                    this.chronoCycle = 0;
                    this.vitesseActu = 2.0; // Vitesse monte à 2 cases/s
                    if (estRalenti)
                        this.vitesseActu = this.vitesseActu/2;
                } else {
                    this.vitesseActu = 0;
                }
                break;

            case 2: // ÉTAT ACCÉLÉRATION
                if (chronoCycle >= 3.0) {
                    this.etatVaricelle = 0; // Retour à l'état normal
                    this.chronoCycle = 0;
                    this.tempsProchainSommeil = 2 + (Math.random() * 3);
                    this.vitesseActu = this.vitesseDeBase;
                } else
                    this.vitesseActu = 2.0;
                if (estRalenti)
                    this.vitesseActu = this.vitesseActu/2;
                break;
        }
    }

    public void appliquerRalentissement(boolean ralenti) {
        if (this.estGele) return;
        this.estRalenti = ralenti;
        this.vitesseActu = ralenti ? this.vitesseDeBase/2 : this.vitesseDeBase;
    }

    public double getRatioPV(){ return pv / pvMax; }
    public boolean estMort(){ return pv <= 0; }
    public int getRecompense() { return recompense; }
    public double getX() { return x; }
    public double getY() { return y; }
    public String getType() { return this.type; } // Getter renommé
    public Waypoint getWaypointCible() { return this.waypointCible; }
    public double getVitesseActu() { return vitesseActu; }

    public void setVitesseActu(double valeur) {
        this.vitesseActu = valeur;
        if (valeur == 0) this.estGele = true;
    }

    public void reinitialiserVitesse() {
        this.estGele = false;
        if (this.type.equals("VARICELLE")) {
            if (this.etatVaricelle == 2) this.vitesseActu = 2.0;
            else if (this.etatVaricelle == 1) this.vitesseActu = 0;
            else this.vitesseActu = this.vitesseDeBase;
        } else {
            this.vitesseActu = this.vitesseDeBase;
        }
    }

    public void perdreVie(double degats, Tour tourAttaquante){
        pv = Math.max(0, pv - degats);
        if (this.type.equals("VIH") && tourAttaquante != null) {
            tourAttaquante.etourdir(0.1);
            this.doitAfficherEclair = true;
            this.tourAReset.add(tourAttaquante);
        }
        if (this.type.equals("RAGE") && this.estEnrage() && !aDeclencheRage) {
            this.type = "RAGE_ENRAGE";
            this.vitesseDeBase = 2.0;
            this.vitesseActu = 2.0;
            this.aDeclencheRage = true;
        }
    }

    public boolean doitAfficherEclair() {
        return this.doitAfficherEclair;
    }

    public ArrayList<Tour> getTourAReset() {
        return this.tourAReset;
    }

    public void resetEclair() {
        this.doitAfficherEclair = false;
        this.tourAReset.clear();
    }

    public boolean estEnrage() {
        return this.getRatioPV() <= 0.25 ;
    }
}