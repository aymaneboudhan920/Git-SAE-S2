package universite_paris8.iut.aboudhan.saes2javafx.vue;

import javafx.geometry.Pos;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class MicrobeVue extends VBox {

    private final ProgressBar barreDeVie;

    public MicrobeVue(String cheminImage, double initialX, double initialY, double ratioPV) {
        // Création de l'image du microbe
        ImageView imageMicrobe = new ImageView(new Image(getClass().getResourceAsStream(cheminImage)));
        imageMicrobe.setFitWidth(34);
        imageMicrobe.setFitHeight(34);
        imageMicrobe.setPreserveRatio(true);

        // Création de la barre de vie
        this.barreDeVie = new ProgressBar(ratioPV);
        this.barreDeVie.setPrefWidth(25);
        this.barreDeVie.setPrefHeight(3);
        this.barreDeVie.getStyleClass().add("barre-vie-microbe");

        // On décale un peu la barre de vie vers le haut
        this.barreDeVie.setTranslateY(-6);

        // Configuration de la VBox
        this.setAlignment(Pos.CENTER);
        this.setMinWidth(34);
        this.setMaxWidth(34);
        this.setMinHeight(34);
        this.setMaxHeight(34);

        this.setTranslateX(initialX);
        this.setTranslateY(initialY);

        this.getChildren().addAll(this.barreDeVie, imageMicrobe);
    }

    public void mettreAJour(double x, double y, double ratioPV) {
        this.setTranslateX(x);
        this.setTranslateY(y);
        this.barreDeVie.setProgress(ratioPV);
    }
}