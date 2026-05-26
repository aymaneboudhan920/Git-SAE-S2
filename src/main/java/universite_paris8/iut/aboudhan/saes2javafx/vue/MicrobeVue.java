package universite_paris8.iut.aboudhan.saes2javafx.vue;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import universite_paris8.iut.aboudhan.saes2javafx.modele.Microbe;

public class MicrobeVue extends ImageView {

    private Microbe modele;
    private ProgressBar barreDeVie;
    private VBox conteneurGraphique;

    public MicrobeVue(Microbe modele) {
        this.modele = modele;

        //Pour barre de vie
        this.barreDeVie = new ProgressBar(modele.getRatioPV());
        this.barreDeVie.setPrefWidth(25);
        this.barreDeVie.setPrefHeight(3);
        this.barreDeVie.getStyleClass().add("barre-vie-microbe");

        //Pour afficher l'image du microbe
        ImageView imageMicrobe = new ImageView(new Image(getClass().getResourceAsStream(modele.getNomImage())));
        imageMicrobe.setFitWidth(34);
        imageMicrobe.setFitHeight(34);
        imageMicrobe.setPreserveRatio(true);

        //Rassembler la barre de vie avec le microbe
        this.conteneurGraphique = new VBox(4);
        this.conteneurGraphique.setAlignment(Pos.CENTER);
        this.conteneurGraphique.getChildren().addAll(this.barreDeVie, imageMicrobe);

        this.conteneurGraphique.setMaxWidth(34);
        this.conteneurGraphique.setMinWidth(34);

    }

    public void mettreAJour() {

        // On retire 8 pixels en Y pour que la barre de vie soit au-dessus sans décaler le microbe
        this.conteneurGraphique.setLayoutX(modele.getX());
        this.conteneurGraphique.setLayoutY(modele.getY() - 8);

        // Met à jour le remplissage de la jauge (entre 0.0 et 1.0)
        this.barreDeVie.setProgress(modele.getRatioPV());
    }

    public VBox getConteneurGraphique(){
        return this.conteneurGraphique;
    }
}
