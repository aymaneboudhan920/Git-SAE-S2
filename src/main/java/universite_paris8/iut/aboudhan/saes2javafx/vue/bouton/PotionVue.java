package universite_paris8.iut.aboudhan.saes2javafx.vue.bouton;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class PotionVue {

    public void animerJaugeActive(Button bouton, double secondesActif, Runnable actionFin) {
        double largeur = bouton.getWidth() > 0 ? bouton.getWidth() : 50;
        double hauteur = bouton.getHeight() > 0 ? bouton.getHeight() : 45;

        Rectangle jaugeSombre = new Rectangle(largeur, 0);
        jaugeSombre.setFill(Color.web("#000000", 0.65));

        Parent racineGlobale = bouton.getScene().getRoot();
        if (!(racineGlobale instanceof Pane)) {
            return;
        }
        Pane conteneurRacine = (Pane) racineGlobale;

        Point2D positionAbsolue = bouton.localToScene(0, 0);
        jaugeSombre.setLayoutX(positionAbsolue.getX());
        jaugeSombre.setLayoutY(positionAbsolue.getY());

        conteneurRacine.getChildren().add(jaugeSombre);

        Timeline timelineSablier = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(jaugeSombre.heightProperty(), 0)),
                new KeyFrame(Duration.seconds(secondesActif), new KeyValue(jaugeSombre.heightProperty(), hauteur))
        );

        timelineSablier.setOnFinished(event -> {
            conteneurRacine.getChildren().remove(jaugeSombre);

            if (actionFin != null) {
                actionFin.run();
            }
        });

        timelineSablier.play();
    }
}