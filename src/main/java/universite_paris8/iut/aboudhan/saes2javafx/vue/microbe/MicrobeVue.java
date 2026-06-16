package universite_paris8.iut.aboudhan.saes2javafx.vue.microbe;

import javafx.geometry.Pos;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class MicrobeVue extends VBox {

    private final ProgressBar barreDeVie;
    private final ImageView imageMicrobe;

    public MicrobeVue(String typeMicrobe, double initialX, double initialY, double ratioPV) {
        String cheminImage = associerTypeAImage(typeMicrobe);

        // Création de l'image du microbe
        this.imageMicrobe = new ImageView(new Image(getClass().getResourceAsStream(cheminImage)));
        this.imageMicrobe.setFitWidth(34);
        this.imageMicrobe.setFitHeight(34);
        this.imageMicrobe.setPreserveRatio(true);

        // Création de la barre de vie
        this.barreDeVie = new ProgressBar(ratioPV);
        this.barreDeVie.setPrefWidth(25);
        this.barreDeVie.setPrefHeight(3);
        this.barreDeVie.getStyleClass().add("barre-vie-microbe");

        this.setSpacing(2);
        this.setAlignment(Pos.CENTER);
        this.setMinWidth(34);
        this.setMaxWidth(34);

        // Positionnement initial
        this.setTranslateX(initialX);
        this.setTranslateY(initialY - 5);

        this.getChildren().addAll(this.barreDeVie, this.imageMicrobe);
    }

    private String associerTypeAImage(String type) {
        String prefixe = "/universite_paris8/iut/aboudhan/saes2javafx/vue/";
        switch (type) {
            case "RHINOVIRUS":   return prefixe + "rhinovirus.png";
            case "NOROVIRUS":    return prefixe + "norovirus.png";
            case "STREPTOCOQUE": return prefixe + "streptocoque.png";
            case "INFLUENZA":    return prefixe + "influenza.png";
            case "VARICELLE":    return prefixe + "varicelle.png";
            case "COVID":        return prefixe + "covid.png";
            case "VIH":          return prefixe + "vih.png";
            case "TUBERCULOSE":  return prefixe + "tuberculose.png";
            case "PESTE":        return prefixe + "peste.png";
            case "RAGE":         return prefixe + "rage.png";
            case "RAGE_ENRAGE":  return prefixe + "rage_enrage.png";
            case "VARIOLE":      return prefixe + "variole.png";
            default:             return null;
        }
    }

    public void mettreAJour(double x, double y, double ratioPV) {
        this.setTranslateX(x);
        this.setTranslateY(y - 5);
        this.barreDeVie.setProgress(ratioPV);
    }

    public void changerImage(String nouveauType) {
        String chemin = associerTypeAImage(nouveauType);
        if (chemin != null) {
            this.imageMicrobe.setImage(new Image(getClass().getResourceAsStream(chemin)));
        }
    }
}