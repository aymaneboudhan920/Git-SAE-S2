package universite_paris8.iut.aboudhan.saes2javafx.vue.tour;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import universite_paris8.iut.aboudhan.saes2javafx.modele.tour.TourScanner;

public class TourVue extends Pane {

    private final ImageView imageTour;
    private ImageView imageOndeRadar = null;

    public TourVue(String cheminImage, double initialX, double initialY) {
        this.imageTour = new ImageView(new Image(getClass().getResourceAsStream(cheminImage)));
        this.imageTour.setFitWidth(34);
        this.imageTour.setFitHeight(34);

        this.setTranslateX(initialX);
        this.setTranslateY(initialY);

        if (cheminImage.contains("tour_scanner")) {
            Image textureOnde = new Image(getClass().getResourceAsStream("/universite_paris8/iut/aboudhan/saes2javafx/vue/radar.png"));
            this.imageOndeRadar = new ImageView(textureOnde);
            this.imageOndeRadar.setPreserveRatio(false);
            this.getChildren().add(imageOndeRadar);
        }

        this.getChildren().add(imageTour);
    }

    public void cacherRadar(){
        if (this.imageOndeRadar != null) {
            this.imageOndeRadar.setVisible(false);
        }
    }

    public void rafraichirOndeScanner(TourScanner modeleScanner) {
        if (imageOndeRadar == null) return;

        double rayon = modeleScanner.getRayonAnimationActuel();
        double diametre = rayon * 2;

        if (diametre <= 0) {
            cacherRadar();
            return;
        }

        imageOndeRadar.setVisible(true);
        imageOndeRadar.setFitWidth(diametre);
        imageOndeRadar.setFitHeight(diametre);

        double centreTuile = 17.0;
        imageOndeRadar.setTranslateX(centreTuile - rayon);
        imageOndeRadar.setTranslateY(centreTuile - rayon);

        double opacite = 1.0 - (0.7 * rayon / modeleScanner.getPortee());
        imageOndeRadar.setOpacity(opacite);
    }
}