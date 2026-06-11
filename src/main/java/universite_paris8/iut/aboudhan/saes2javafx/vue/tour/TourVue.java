package universite_paris8.iut.aboudhan.saes2javafx.vue.tour;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class TourVue extends Pane {

    private final ImageView imageTour;

    public TourVue(String cheminImage, double initialX, double initialY) {
        this.imageTour = new ImageView(new Image(getClass().getResourceAsStream(cheminImage)));
        this.imageTour.setFitWidth(34);
        this.imageTour.setFitHeight(34);

        this.setTranslateX(initialX);
        this.setTranslateY(initialY);

        this.getChildren().add(imageTour);
    }
}