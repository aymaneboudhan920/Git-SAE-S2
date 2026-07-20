package universite_paris8.iut.aboudhan.saes2javafx.modele.tour;

import universite_paris8.iut.aboudhan.saes2javafx.modele.jeu.Environnement;
import universite_paris8.iut.aboudhan.saes2javafx.modele.microbe.Microbe;

import java.util.List;

public class TourRayonX extends Tour {
    public static final int prixAchat = 100;
    private static final double DEGATS_MIN = 0.5;
    private static final double DEGATS_MAX = 40.0;
    private static final double PAS_AUGMENTATION = 1.3;

    private Microbe cibleActuelle = null;
    private Projectile rayonActuel = null;
    private double degatsActuels = DEGATS_MIN;

    public TourRayonX(double x, double y) {
        super(x, y, 102, 2, 10, prixAchat,
                "/universite_paris8/iut/aboudhan/saes2javafx/vue/tour_rayon_x.png");
    }

    @Override
    public void attaquer(Environnement env) {
        List<Microbe> microbesActifs = env.getMicrobesActifs();

        if (cibleActuelle != null) {
            if (cibleActuelle.estMort() || !microbesActifs.contains(cibleActuelle) || !estAPortee(cibleActuelle)) {
                if (rayonActuel != null) {
                    rayonActuel.setDetruit(true);
                    rayonActuel = null;
                }
                cibleActuelle = null;
                degatsActuels = DEGATS_MIN;
            }
        }

        if (cibleActuelle == null) {
            for (int i = 0; i < microbesActifs.size() && cibleActuelle == null; i++) {
                Microbe m = microbesActifs.get(i);
                if (!m.estMort() && estAPortee(m) && !"INFLUENZA".equals(m.getType())) {
                    cibleActuelle = m;
                    degatsActuels = DEGATS_MIN;
                }
            }
        }

        if (cibleActuelle != null && peutAttaquer()) {
            if (rayonActuel == null || rayonActuel.estDetruit()) {
                rayonActuel = new Projectile(this.getX(), this.getY(), 0.0, cibleActuelle, "RAYON_X", this.degatsActuels, this);
                env.ajouterProjectile(rayonActuel);
            }

            cibleActuelle.perdreVie(this.degatsActuels, this);
            degatsActuels *= PAS_AUGMENTATION;
            if (degatsActuels > DEGATS_MAX) {
                degatsActuels = DEGATS_MAX;
            }

            recharger();
        }
    }

    @Override
    public void reinitialiserAttaque() {
        this.degatsActuels = DEGATS_MIN;
        this.cibleActuelle = null;
    }

    private boolean estAPortee(Microbe m) {
        double diffX = m.getX() - this.getX();
        double diffY = m.getY() - this.getY();
        return Math.sqrt(diffX * diffX + diffY * diffY) <= this.getPortee();
    }

    public Projectile getRayonActuel(){ return this.rayonActuel; }
    public Microbe getCibleActuelle() { return cibleActuelle; }
}
