package universite_paris8.iut.aboudhan.saes2javafx.modele.tour;

import universite_paris8.iut.aboudhan.saes2javafx.modele.jeu.Environnement;
import universite_paris8.iut.aboudhan.saes2javafx.modele.microbe.Microbe;

import java.util.List;

public class TourScanner extends Tour {
    public static int prixAchat = 150;
    private double rayonAnimationActuel = 0;
    private final double vitesseExtension;

    public TourScanner(double x, double y) {
        super(x, y, 102, 10, 0.8, prixAchat,
                "/universite_paris8/iut/aboudhan/saes2javafx/vue/tour_scanner.png");
        this.vitesseExtension = this.getPortee() * this.getVitesseTir();
    }

    @Override
    public void attaquer(Environnement env) {
        if (!peutAttaquer() || this.rayonAnimationActuel < this.getPortee()) {
            return;
        }

        List<Microbe> microbesActifs = env.getMicrobesActifs();
        boolean aAttaque = false;

        for (int i = 0; i < microbesActifs.size(); i++) {
            Microbe m = microbesActifs.get(i);
            if (!m.estMort() && estAPortee(m)) {
                m.perdreVie(this.getDegats(), this);
                aAttaque = true;
            }
        }

        recharger();
        this.rayonAnimationActuel = 0;
    }

    @Override
    public void mettreAJourRecharge(double tps) {
        super.mettreAJourRecharge(tps);

        if (this.rayonAnimationActuel < this.getPortee()) {
            this.rayonAnimationActuel += vitesseExtension * tps;
        }

        // On bloque le rayon à la portée maximale
        if (this.rayonAnimationActuel > this.getPortee()) {
            this.rayonAnimationActuel = this.getPortee();
        }
    }

    @Override
    public void reinitialiserAttaque() {
        this.rayonAnimationActuel = 0;
    }

    private boolean estAPortee(Microbe m) {
        double diffX = m.getX() - this.getX();
        double diffY = m.getY() - this.getY();
        return Math.sqrt(diffX * diffX + diffY * diffY) <= this.getPortee();
    }

    public double getRayonAnimationActuel() {
        return this.rayonAnimationActuel;
    }
}