package universite_paris8.iut.aboudhan.saes2javafx.vue;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import universite_paris8.iut.aboudhan.saes2javafx.modele.Tour;

public class TourVue extends Pane {

    private Tour tour;
    private ImageView imageTour;

    public TourVue(Tour tour) {
        this.tour = tour;

        this.imageTour = new ImageView(new Image(getClass().getResourceAsStream(tour.getNomImage())));
        this.imageTour.setFitWidth(34);
        this.imageTour.setFitHeight(34);

        this.setTranslateX(tour.getX());
        this.setTranslateY(tour.getY());

        this.getChildren().add(imageTour);
    }
}
