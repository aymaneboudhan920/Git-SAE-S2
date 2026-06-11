package universite_paris8.iut.aboudhan.saes2javafx.modele;

import java.util.List;

public class TourChimiste extends Tour {
    public static int prixAchat = 100;

    public TourChimiste(double x, double y) {
        super(x, y, 102, 15, 0.33, prixAchat,
                "/universite_paris8/iut/aboudhan/saes2javafx/vue/tour_chimiste.png",
                "/universite_paris8/iut/aboudhan/saes2javafx/vue/seringue.png");
    }

    @Override
    public void attaquer(List<Microbe> microbesActifs) {
        // On vérifie d'abord si le temps de recharge est écoulé
        if (peutAttaquer()) {

            // On parcourt tous les microbes actifs pour l'attaque de zone
            for (int i = 0; i < microbesActifs.size(); i++) {
                Microbe m = microbesActifs.get(i);

                // On ne touche que les microbes vivants
                if (!m.estMort()) {
                    double diffX = m.getX() - this.getX();
                    double diffY = m.getY() - this.getY();
                    double distance = Math.sqrt(diffX * diffX + diffY * diffY);

                    // Tous les microbes présents dans le rayon d'action subissent les dégâts
                    if (distance <= this.getPortee()) {
                        m.perdreVie(this.getDegats());
                    }
                }
            }
        }
    }
}