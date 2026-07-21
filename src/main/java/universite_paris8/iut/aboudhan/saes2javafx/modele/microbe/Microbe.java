package universite_paris8.iut.aboudhan.saes2javafx.modele.microbe;

import universite_paris8.iut.aboudhan.saes2javafx.modele.jeu.Waypoint;
import universite_paris8.iut.aboudhan.saes2javafx.modele.tour.Tour;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Microbe {
    private final DoubleProperty x = new SimpleDoubleProperty();
    private final DoubleProperty y = new SimpleDoubleProperty();
    public double vitesseDeBase;
    private double vitesseActu;
    private final DoubleProperty pv = new SimpleDoubleProperty();
    public double pvMax;
    private final int recompense;
    public int infection;
    private Waypoint waypointCible;
    private String type;
    private boolean estGele = false;
    private boolean estRalenti = false;
    private boolean aDeclencheRage = false;
    private boolean doitAfficherEclair = false;
    private final List<Tour> tourAReset = new ArrayList<>();
    private final Random random = new Random();

    private double chronoCycle = 0;
    private double tempsProchainSommeil;
    private int etatVaricelle = 0;

    public Microbe(double v, double pm, int r, int i, String type, Waypoint waypointDepart){
        this.vitesseDeBase = v;
        this.vitesseActu = v;
        this.pv.set(pm);
        this.pvMax = pm;
        this.recompense = r;
        this.infection = i;
        this.type = type;

        if ("VARICELLE".equals(this.type)) {
            this.tempsProchainSommeil = 2 + (random.nextDouble() * 3);
        }

        if (waypointDepart != null) {
            this.x.set(waypointDepart.getX());
            this.y.set(waypointDepart.getY());
            this.waypointCible = waypointDepart.obtenirProchainWaypoint();
        } else {
            this.x.set(0);
            this.y.set(0);
            this.waypointCible = null;
        }
    }

    public void deplacer(){
        if ("VARICELLE".equals(this.type)) {
            mettreAJourComportementVaricelle(0.012);
        }
        if (this.estGele || this.vitesseActu == 0 || this.waypointCible == null) return;

        double diffX = this.waypointCible.getX() - this.getX();
        double diffY = this.waypointCible.getY() - this.getY();
        double distance = Math.sqrt(diffX * diffX + diffY * diffY);

        if (distance > this.vitesseActu && distance > 0){
            this.setX(this.getX() + (diffX / distance) * this.vitesseActu);
            this.setY(this.getY() + (diffY / distance) * this.vitesseActu);
        } else {
            this.setX(this.waypointCible.getX());
            this.setY(this.waypointCible.getY());
            this.waypointCible = waypointCible.obtenirProchainWaypoint();

            if (waypointCible != null){
                double distanceSaut = Math.sqrt(
                        Math.pow(waypointCible.getX() - this.getX(), 2) +
                                Math.pow(waypointCible.getY() - this.getY(), 2)
                );
                if (distanceSaut > 70) {
                    this.setX(waypointCible.getX());
                    this.setY(waypointCible.getY());
                }
            }
        }
    }

    private void mettreAJourComportementVaricelle(double tps) {
        if (!"VARICELLE".equals(this.type) || this.estGele) return;

        chronoCycle += tps;

        switch (this.etatVaricelle) {
            case 0 -> { // ÉTAT NORMAL
                if (chronoCycle >= tempsProchainSommeil) {
                    this.etatVaricelle = 1;
                    this.chronoCycle = 0;
                    this.vitesseActu = 0;
                } else {
                    this.vitesseActu = this.vitesseDeBase;
                }
            }
            case 1 -> { // ÉTAT SOMMEIL
                if (chronoCycle >= 2) {
                    this.etatVaricelle = 2;
                    this.chronoCycle = 0;
                    this.vitesseActu = 2.0;
                } else {
                    this.vitesseActu = 0;
                }
            }
            case 2 -> { // ÉTAT ACCÉLÉRATION
                if (chronoCycle >= 3.0) {
                    this.etatVaricelle = 0;
                    this.chronoCycle = 0;
                    this.tempsProchainSommeil = 2 + (random.nextDouble() * 3);
                    this.vitesseActu = this.vitesseDeBase;
                } else {
                    this.vitesseActu = 2.0;
                }
            }
        }

        if (estRalenti && this.vitesseActu > 0) {
            this.vitesseActu /= 2.0;
        }
    }

    public void appliquerRalentissement(boolean ralenti) {
        if (this.estGele) return;
        this.estRalenti = ralenti;
        this.vitesseActu = ralenti ? this.vitesseDeBase / 2 : this.vitesseDeBase;
    }

    public double getRatioPV(){ return pv.get() / pvMax; }
    public boolean estMort(){ return this.pv.get() <= 0; }
    public DoubleProperty pvProperty() { return this.pv; }
    public int getRecompense() { return recompense; }
    public int getInfection() { return infection; }
    public DoubleProperty xProperty() { return this.x; }
    public DoubleProperty yProperty() { return this.y; }
    public String getType() { return this.type; }
    public Waypoint getWaypointCible() { return this.waypointCible; }
    public double getVitesseActu() { return vitesseActu; }

    public void setVitesseActu(double valeur) {
        this.vitesseActu = valeur;
        if (valeur == 0) this.estGele = true;
    }

    public double getX() { return x.get(); }
    public void setX(double x) { this.x.set(x); }

    public double getY() { return y.get(); }
    public void setY(double y) { this.y.set(y); }

    public void reinitialiserVitesse() {
        this.estGele = false;
        if ("VARICELLE".equals(this.type)) {
            if (this.etatVaricelle == 2) this.vitesseActu = 2.0;
            else if (this.etatVaricelle == 1) this.vitesseActu = 0;
            else this.vitesseActu = this.vitesseDeBase;
        } else {
            this.vitesseActu = this.vitesseDeBase;
        }
    }

    public void perdreVie(double degats, Tour tour){
        this.pv.set(Math.max(0, this.pv.get() - degats));
        if ("VIH".equals(this.type) && tour != null) {
            tour.etourdir(0.1);
            this.doitAfficherEclair = true;
            this.tourAReset.add(tour);
        }
        if ("RAGE".equals(this.type) && this.estEnrage() && !aDeclencheRage) {
            this.type = "RAGE_ENRAGE";
            this.vitesseDeBase = 2.0;
            this.vitesseActu = 2.0;
            this.aDeclencheRage = true;
        }
    }

    public boolean doitAfficherEclair() { return this.doitAfficherEclair; }
    public List<Tour> getTourAReset() { return this.tourAReset; }

    public void resetEclair() {
        this.doitAfficherEclair = false;
        this.tourAReset.clear();
    }

    public boolean estEnrage() { return this.getRatioPV() <= 0.25; }
}
