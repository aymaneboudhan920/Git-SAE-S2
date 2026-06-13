package universite_paris8.iut.aboudhan.saes2javafx.modele.tour;

import universite_paris8.iut.aboudhan.saes2javafx.modele.jeu.Environnement;
import universite_paris8.iut.aboudhan.saes2javafx.modele.microbe.Microbe;

import java.util.List;

public class TourRayonX extends Tour {
    private final double degatsMin = 0.5;
    private final double degatsMax = 40.0;
    public static int prixAchat = 200;

    private Microbe cibleActuelle = null;
    private double degatsActuels = degatsMin;
    private final double pasAugmentation = 1.5;

    public TourRayonX(double x, double y) {
        super(x, y, 102, 2, 10, prixAchat,
                "/universite_paris8/iut/aboudhan/saes2javafx/vue/tour_rayon_x.png");
    }

    @Override
    public void attaquer(Environnement env) {
        List<Microbe> microbesActifs = env.getMicrobesActifs();

        if (cibleActuelle != null) {
            if (cibleActuelle.estMort() || !microbesActifs.contains(cibleActuelle) || !estAPortee(cibleActuelle)) {
                cibleActuelle = null;
                degatsActuels = degatsMin;
            }
        }

        if (cibleActuelle == null) {
            for (int i = 0; i < microbesActifs.size() && cibleActuelle == null; i++) {
                Microbe m = microbesActifs.get(i);
                if (!m.estMort() && estAPortee(m)) {
                    cibleActuelle = m;
                    degatsActuels = degatsMin;
                }
            }
        }

        if (cibleActuelle != null && peutAttaquer()) {
            // Ajout du rayon laser 
            Projectile rayon = new Projectile(this.getX(), this.getY(), 0.0, cibleActuelle, "RAYON_X", 0);
            env.ajouterProjectile(rayon);

            // Appliquer les dégâts continus
            cibleActuelle.perdreVie((int) degatsActuels);

            degatsActuels *= pasAugmentation;
            if (degatsActuels > degatsMax) {
                degatsActuels = degatsMax;
            }

            recharger();
        }
    }

    private boolean estAPortee(Microbe m) {
        double diffX = m.getX() - this.getX();
        double diffY = m.getY() - this.getY();
        return Math.sqrt(diffX * diffX + diffY * diffY) <= this.getPortee();
    }
}
