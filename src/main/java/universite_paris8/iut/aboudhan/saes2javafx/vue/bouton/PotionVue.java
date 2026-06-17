package universite_paris8.iut.aboudhan.saes2javafx.vue.bouton;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class PotionVue {

    public void animerJaugeActive(Button bouton, double secondesActif, Runnable actionFin) {
        double largeur = bouton.getWidth() > 0 ? bouton.getWidth() : 50;
        double hauteur = bouton.getHeight() > 0 ? bouton.getHeight() : 45;

        Rectangle jaugeSombre = new Rectangle(largeur, 0);
        jaugeSombre.setFill(Color.web("#000000", 0.65));

        double rayonArrondi = 20.0;
        jaugeSombre.setArcWidth(rayonArrondi);
        jaugeSombre.setArcHeight(rayonArrondi);

        Parent racineGlobale = bouton.getScene().getRoot();
        if (!(racineGlobale instanceof Pane)) {
            return;
        }
        Pane conteneurRacine = (Pane) racineGlobale;

        Point2D positionScene = bouton.localToScene(0, 0);
        if (conteneurRacine instanceof StackPane) {
            StackPane.setAlignment(jaugeSombre, Pos.TOP_LEFT);
            jaugeSombre.setTranslateX(positionScene.getX());
            jaugeSombre.setTranslateY(positionScene.getY());
        } else {
            jaugeSombre.setLayoutX(positionScene.getX());
            jaugeSombre.setLayoutY(positionScene.getY());
        }

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