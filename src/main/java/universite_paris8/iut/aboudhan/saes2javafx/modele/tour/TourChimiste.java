package universite_paris8.iut.aboudhan.saes2javafx.modele.tour;

import universite_paris8.iut.aboudhan.saes2javafx.modele.jeu.Environnement;
import universite_paris8.iut.aboudhan.saes2javafx.modele.microbe.Microbe;

import java.util.List;

public class TourChimiste extends Tour {
    public static int prixAchat = 100;
    public static int portee = 102;


    public TourChimiste(double x, double y) {
        super(x, y, portee, 15, 0.33, prixAchat,
                "/universite_paris8/iut/aboudhan/saes2javafx/vue/tour_chimiste.png");
    }

    @Override
    public void attaquer(Environnement env) {
        List<Microbe> microbesActifs = env.getMicrobesActifs();
        Microbe cible = null;

        for (int i = 0; i < microbesActifs.size() && cible == null; i++) {
            Microbe m = microbesActifs.get(i);
            if (!m.estMort()) {
                double diffX = m.getX() - this.getX();
                double diffY = m.getY() - this.getY();
                double distance = Math.sqrt(diffX * diffX + diffY * diffY);

                if (distance <= this.getPortee()) {
                    cible = m;
                }
            }
        }

        if (cible != null && peutAttaquer()) {
            Projectile potion = new Projectile(this.getX(), this.getY(), 5.0, cible, "CHIMISTE", this.getDegats());
            env.ajouterProjectile(potion);
            this.recharger();
        }
    }
}
