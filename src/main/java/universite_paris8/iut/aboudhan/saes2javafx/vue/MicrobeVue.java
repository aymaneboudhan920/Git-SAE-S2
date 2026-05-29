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
        super(4);
        this.microbe = m;

        // Création de la barre de vie
        this.barreDeVie = new ProgressBar(microbe.getRatioPV());
        this.barreDeVie.setPrefWidth(25);
        this.barreDeVie.setPrefHeight(3);
        this.barreDeVie.getStyleClass().add("barre-vie-microbe");

        // Création de l'image du microbe
        ImageView imageMicrobe = new ImageView(new Image(getClass().getResourceAsStream(microbe.getNomImage())));
        imageMicrobe.setFitWidth(34);
        imageMicrobe.setFitHeight(34);
        imageMicrobe.setPreserveRatio(true);

        // Configuration de la VBox
        this.setAlignment(Pos.CENTER);
        this.setMaxWidth(34);
        this.setMinWidth(34);

        mettreAJourPosition();

        // Ajout de la barre de vie et de l'image directement dans nous-mêmes
        this.getChildren().addAll(this.barreDeVie, imageMicrobe);
    }

    public void mettreAJourPosition() {
        this.setTranslateX(this.microbe.getX());
        this.setTranslateY(this.microbe.getY());

        // met à jour la barre de vie graphiquement en temps réel
        this.barreDeVie.setProgress(this.microbe.getRatioPV());
    }
}
