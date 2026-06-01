package universite_paris8.iut.aboudhan.saes2javafx.vue;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import universite_paris8.iut.aboudhan.saes2javafx.modele.Tours;

public class ToursVue extends Pane {

    private Tours tour;
    private ImageView imageTour;
    private Circle cerclePortee;

    public ToursVue(Tours tour) {
        this.tour = tour;

        this.imageTour = new ImageView(new Image(getClass().getResourceAsStream(tour.getNomImage())));
        this.imageTour.setFitWidth(34);
        this.imageTour.setFitHeight(34);

        this.cerclePortee = new Circle(17, 17, tour.getPortee());
        this.cerclePortee.setFill(Color.web("#3ae374", 0.15));
        this.cerclePortee.setStroke(Color.web("#3ae374", 0.5));
        this.cerclePortee.setVisible(false);

        this.setTranslateX(tour.getX());
        this.setTranslateY(tour.getY());

        this.getChildren().addAll(cerclePortee, imageTour);

        this.setOnMouseEntered(e -> cerclePortee.setVisible(true));
        this.setOnMouseExited(e -> cerclePortee.setVisible(false));
    }
}