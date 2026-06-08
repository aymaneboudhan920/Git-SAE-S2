package universite_paris8.iut.aboudhan.saes2javafx.modele;

import java.util.List;

public class TourScientifique extends Tour {
    public static int prixAchat = 50;

    public TourScientifique(double x, double y) {
        super(x, y, 170, 10, 1.0, prixAchat,
                "/universite_paris8/iut/aboudhan/saes2javafx/vue/tour_scientifique.png",
                "/universite_paris8/iut/aboudhan/saes2javafx/vue/seringue.png");
    }

    @Override
    public void attaquer(List<Microbe> microbesActifs) {
        // Todo : A faire plus tard pour le sprint 3
    }
}