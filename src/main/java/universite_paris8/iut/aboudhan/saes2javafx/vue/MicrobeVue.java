package universite_paris8.iut.aboudhan.saes2javafx.vue;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import universite_paris8.iut.aboudhan.saes2javafx.modele.Microbe;

public class MicrobeVue extends ImageView {

    private Microbe microbe;
    private ProgressBar barreDeVie;
    private VBox conteneurGraphique;

    public MicrobeVue(Microbe m) {
        this.modele = m;

        //Pour barre de vie
        this.barreDeVie = new ProgressBar(microbe.getRatioPV());
        this.barreDeVie.setPrefWidth(25);
        this.barreDeVie.setPrefHeight(3);
        this.barreDeVie.getStyleClass().add("barre-vie-microbe");

        //Pour afficher l'image du microbe
        ImageView imageMicrobe = new ImageView(new Image(getClass().getResourceAsStream(microbe.getNomImage())));
        imageMicrobe.setFitWidth(34);
        imageMicrobe.setFitHeight(34);
        imageMicrobe.setPreserveRatio(true);

        // Positionnement initial
        mettreAJourPosition();
        
        //Rassembler la barre de vie avec le microbe
        this.conteneurGraphique = new VBox(4);
        this.conteneurGraphique.setAlignment(Pos.CENTER);
        this.conteneurGraphique.getChildren().addAll(this.barreDeVie, imageMicrobe);

        this.conteneurGraphique.setMaxWidth(34);
        this.conteneurGraphique.setMinWidth(34);

    }

    public void mettreAJourPosition() {
        this.setTranslateX(this.microbe.getX());
        this.setTranslateY(this.microbe.getY());
        // Met à jour le remplissage de la jauge (entre 0.0 et 1.0)
        this.barreDeVie.setProgress(modele.getRatioPV());
    }

    public VBox getConteneurGraphique(){
        return this.conteneurGraphique;
    }
}
