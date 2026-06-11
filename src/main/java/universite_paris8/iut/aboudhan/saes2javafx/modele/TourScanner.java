package universite_paris8.iut.aboudhan.saes2javafx.modele;

import java.util.List;

public class TourScanner extends Tour {
    public static int prixAchat = 150;

    public TourScanner(double x, double y) {
        super(x, y, 102, 10, 0.8, prixAchat,
                "/universite_paris8/iut/aboudhan/saes2javafx/vue/tour_scanner.png",
                "/universite_paris8/iut/aboudhan/saes2javafx/vue/seringue.png");
    }

    @Override
    public void attaquer(List<Microbe> microbesActifs) {
        // Todo : A faire plus tard pour le sprint 3
    }
}