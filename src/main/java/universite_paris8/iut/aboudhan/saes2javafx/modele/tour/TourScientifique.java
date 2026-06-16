package universite_paris8.iut.aboudhan.saes2javafx.modele.tour;

import universite_paris8.iut.aboudhan.saes2javafx.modele.jeu.Environnement;
import universite_paris8.iut.aboudhan.saes2javafx.modele.microbe.Microbe;

import java.util.List;

public class TourScientifique extends Tour {
    public static int prixAchat = 50;

    public TourScientifique(double x, double y) {
        super(x, y, 136, 10, 1.0, prixAchat,
                "/universite_paris8/iut/aboudhan/saes2javafx/vue/tour_scientifique.png");
    }

    @Override
    public void attaquer(Environnement env) {
        List<Microbe> microbesActifs = env.getMicrobesActifs();
        Microbe cible = null;

        for (int i = 0; i < microbesActifs.size() && cible == null; i++) {
            Microbe m = microbesActifs.get(i);

            if (!m.estMort() && !m.getType().equals("INFLUENZA")) {
                double diffX = m.getX() - this.getX();
                double diffY = m.getY() - this.getY();
                double distance = Math.sqrt(diffX * diffX + diffY * diffY);

                if (distance <= this.getPortee()) {
                    cible = m;
                }
            }
        }

        if (cible != null && peutAttaquer()) {
            Projectile p = new Projectile(this.getX(), this.getY(), 8.0, cible, "SCIENTIFIQUE", this.getDegats(), this);
            env.ajouterProjectile(p);

            recharger();
        }
    }
}