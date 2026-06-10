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
        // Todo : A faire plus tard pour le sprint 3
    }
}