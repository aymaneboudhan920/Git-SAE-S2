package universite_paris8.iut.aboudhan.saes2javafx.vue;

import javafx.geometry.Pos;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import universite_paris8.iut.aboudhan.saes2javafx.modele.Microbe;

public class MicrobeVue extends VBox {

    private Microbe microbe;
    private ProgressBar barreDeVie;

    public MicrobeVue(Microbe m) {
        this.microbe = m;

        // Création de l'image du microbe
        ImageView imageMicrobe = new ImageView(new Image(getClass().getResourceAsStream(microbe.getNomImage())));
        imageMicrobe.setFitWidth(34);
        imageMicrobe.setFitHeight(34);
        imageMicrobe.setPreserveRatio(true);

        // Création de la barre de vie
        this.barreDeVie = new ProgressBar(microbe.getRatioPV());
        this.barreDeVie.setPrefWidth(25);
        this.barreDeVie.setPrefHeight(3);
        this.barreDeVie.getStyleClass().add("barre-vie-microbe");

        // On décale un peu la barre de vie vers le haut
        this.barreDeVie.setTranslateY(-6);

        // On met la VBox à la taille de l'image
        this.setAlignment(Pos.CENTER);
        this.setMinWidth(34);
        this.setMaxWidth(34);
        this.setMinHeight(34);
        this.setMaxHeight(34);

        mettreAJourPosition();
        this.getChildren().addAll(this.barreDeVie, imageMicrobe);
    }

    public void mettreAJourPosition() {
        // On donne à la VBox les mêmes coordonnées que le microbe
        this.setTranslateX(this.microbe.getX());
        this.setTranslateY(this.microbe.getY());

        // Mise à jour la barre de vie en temps réel
        this.barreDeVie.setProgress(this.microbe.getRatioPV());
    }
}
