package universite_paris8.iut.aboudhan.saes2javafx.modele;

import java.util.List;

public class TourRayonX extends Tour {
    private double degatsMin = 2.0;
    private double degatsMax = 40.0;
    public static int prixAchat = 200;

    public TourRayonX(double x, double y) {
        super(x, y, 170, 2, 0.8, prixAchat,
                "/universite_paris8/iut/aboudhan/saes2javafx/vue/tour_rayon_x.png",
                "/universite_paris8/iut/aboudhan/saes2javafx/vue/seringue.png");
    }

    @Override
    public void attaquer(List<Microbe> microbesActifs) {
        // Todo : A faire plus tard pour le sprint 3
    }
}