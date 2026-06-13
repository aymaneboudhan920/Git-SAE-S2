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

    private final Pane ancrageInitial;

    public PotionVue(Pane ancrageInitial) {
        this.ancrageInitial = ancrageInitial;
    }

    public void animerJaugeActive(Button bouton, double secondesActif, Runnable actionFin) {
        // Récupérer les dimensions réelles du bouton
        double largeur = bouton.getWidth() > 0 ? bouton.getWidth() : 50;
        double hauteur = bouton.getHeight() > 0 ? bouton.getHeight() : 45;

        // Créer le volet sombre
        // Au départ, sa hauteur est de 0 pour qu'on voie entièrement la potion
        Rectangle jaugeSombre = new Rectangle(largeur, 0);
        jaugeSombre.setFill(Color.web("#000000", 0.65));

        // Trouver la racine globale pour éviter les problèmes de compartiment
        Parent racineGlobale = bouton.getScene().getRoot();
        if (!(racineGlobale instanceof Pane)) {
            return;
        }
        Pane conteneurRacine = (Pane) racineGlobale;

        // Positionner le haut du rectangle pile sur le haut du bouton
        Point2D positionAbsolue = bouton.localToScene(0, 0);
        jaugeSombre.setLayoutX(positionAbsolue.getX());
        jaugeSombre.setLayoutY(positionAbsolue.getY());

        conteneurRacine.getChildren().add(jaugeSombre);

        // La hauteur du rectangle passe de 0 à la hauteur totale du bouton
        // Cela crée l'effet du rideau/sablier sombre qui descend et recouvre la potion
        Timeline timelineSablier = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(jaugeSombre.heightProperty(), 0)),
                new KeyFrame(Duration.seconds(secondesActif), new KeyValue(jaugeSombre.heightProperty(), hauteur))
        );

        timelineSablier.setOnFinished(event -> {
            // Le cooldown est fini : on vire le rectangle sombre d'un coup sec
            conteneurRacine.getChildren().remove(jaugeSombre);

            // On signale au contrôleur que le bouton peut être réactivé
            if (actionFin != null) {
                actionFin.run();
            }
        });

        timelineSablier.play();
    }
}