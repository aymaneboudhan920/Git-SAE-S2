package universite_paris8.iut.aboudhan.saes2javafx.modele.tour;

import universite_paris8.iut.aboudhan.saes2javafx.modele.microbe.Microbe;

import java.util.List;

public class TourRayonX extends Tour {
    private final double degatsMin = 2.0;
    private final double degatsMax = 40.0;
    public static int prixAchat = 200;

    private Microbe cibleActuelle = null;
    private double degatsActuels = degatsMin;

    // Valeur ajoutée aux dégâts à chaque tir sur un même microbe
    private final double pasAugmentation = 1.5;

    public TourRayonX(double x, double y) {
        super(x, y, 102, 2, 10, prixAchat,
                "/universite_paris8/iut/aboudhan/saes2javafx/vue/tour_rayon_x.png",
                "/universite_paris8/iut/aboudhan/saes2javafx/vue/seringue.png");
    }

    @Override
    public void attaquer(List<Microbe> microbesActifs) {
        if (cibleActuelle != null) {
            if (cibleActuelle.estMort() || !microbesActifs.contains(cibleActuelle) || !estAPortee(cibleActuelle)) {
                // On réinitialise la puissance du laser quand on change de cible
                cibleActuelle = null;
                degatsActuels = degatsMin;
            }
        }

        // Si la tour n'a pas de cible, on cherche le premier microbe à portée pour se verrouiller dessus
        if (cibleActuelle == null) {
            for (int i = 0; i < microbesActifs.size() && cibleActuelle == null; i++) {
                Microbe m = microbesActifs.get(i);
                if (!m.estMort() && estAPortee(m)) {
                    cibleActuelle = m;
                    degatsActuels = degatsMin; // Le laser commence à faire le minimum de dégâts
                }
            }
        }

        // Si une cible est verrouillée et que la tour est prête à infliger un tick de dégâts
        if (cibleActuelle != null && peutAttaquer()) {
            // Appliquer les dégâts progressifs (convertis en int pour correspondre à ton modèle)
            cibleActuelle.perdreVie((int) degatsActuels);

            // Faire monter la puissance du laser pour le prochain tick
            degatsActuels *= pasAugmentation;
            if (degatsActuels > degatsMax) {
                degatsActuels = degatsMax; // On cape les dégâts au maximum autorisé
            }
        }
    }

    private boolean estAPortee(Microbe m) {
        double diffX = m.getX() - this.getX();
        double diffY = m.getY() - this.getY();
        double distance = Math.sqrt(diffX * diffX + diffY * diffY);
        return distance <= this.getPortee();
    }
}