package universite_paris8.iut.aboudhan.saes2javafx.modele;

import java.util.List;

public class TourScientifique extends Tour {
    public static int prixAchat = 50;

    public TourScientifique(double x, double y) {
        super(x, y, 136, 10, 1.0, prixAchat,
                "/universite_paris8/iut/aboudhan/saes2javafx/vue/tour_scientifique.png",
                "/universite_paris8/iut/aboudhan/saes2javafx/vue/seringue.png");
    }

    @Override
    public void attaquer(List<Microbe> microbesActifs) {
        Microbe cible = null;

        for (int i = 0; i < microbesActifs.size() && cible == null; i++) {
            Microbe m = microbesActifs.get(i);

            // On vérifie le microbe est vivant
            if (!m.estMort()) {
                double diffX = m.getX() - this.getX();
                double diffY = m.getY() - this.getY();
                double distance = Math.sqrt(diffX * diffX + diffY * diffY);

                if (distance <= this.getPortee()) {
                    cible = m;
                }
            }
        }

        // Si on a trouvé un microbe vivant et à portée
        if (cible != null) {
            if (peutAttaquer()) {
                cible.perdreVie(this.getDegats());
            }
        }
    }
}