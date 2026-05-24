package universite_paris8.iut.aboudhan.saes2javafx.vue;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import universite_paris8.iut.aboudhan.saes2javafx.modele.Microbe;

public class MicrobeVue extends ImageView {

    private final Microbe modele;

    public MicrobeVue(Microbe modele) {
        this.modele = modele;

        // Configuration de la texture à partir du chemin complet stocké dans le modèle
        this.setImage(new Image(getClass().getResourceAsStream(modele.getNomImage())));
        this.setFitWidth(34);
        this.setFitHeight(34);

        // Positionnement initial
        mettreAJourPosition();
    }

    public void mettreAJourPosition() {
        this.setTranslateX(modele.getX());
        this.setTranslateY(modele.getY());
    }
}