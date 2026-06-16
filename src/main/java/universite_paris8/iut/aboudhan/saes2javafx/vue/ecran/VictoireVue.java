package universite_paris8.iut.aboudhan.saes2javafx.vue.ecran;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class VictoireVue {

    private final Pane calqueSombre;
    private final VBox layoutVictoire;

    public VictoireVue(Pane conteneurPrincipal, TilePane grilleJeu, Runnable actionRejouer) {
        // On donne au calque la taille de la map
        this.calqueSombre = new Pane();
        this.calqueSombre.prefWidthProperty().bind(grilleJeu.widthProperty());
        this.calqueSombre.prefHeightProperty().bind(grilleJeu.heightProperty());

        // On le positionne au même endroit que la map dans le conteneur
        this.calqueSombre.layoutXProperty().bind(grilleJeu.layoutXProperty());
        this.calqueSombre.layoutYProperty().bind(grilleJeu.layoutYProperty());
        this.calqueSombre.getStyleClass().add("calque-sombre");

        // Création des textes
        Text titreVictoire = new Text("VICTOIRE");
        titreVictoire.getStyleClass().addAll("titre-fin", "titre-victoire");

        Text sousTitre = new Text(" Vous avez réussi à empêcher la contamination de notre monde. MERCI !");
        sousTitre.getStyleClass().add("sous-titre-fin");

        String prefixe = "/universite_paris8/iut/aboudhan/saes2javafx/vue/";

        // Bouton Rejouer
        Button btnRejouer = new Button();
        ImageView imgRejouer = new ImageView(new Image(getClass().getResourceAsStream(prefixe + "rejouer.png")));
        imgRejouer.setFitWidth(80);
        imgRejouer.setPreserveRatio(true);
        btnRejouer.setGraphic(imgRejouer);

        btnRejouer.getStyleClass().add("btn-fin");
        btnRejouer.setOnAction(e -> actionRejouer.run());

        // Bouton Quitter
        Button btnQuitter = new Button();
        ImageView imgQuitter = new ImageView(new Image(getClass().getResourceAsStream(prefixe + "quitter.png")));
        imgQuitter.setFitWidth(80);
        imgQuitter.setPreserveRatio(true);
        btnQuitter.setGraphic(imgQuitter);

        btnQuitter.getStyleClass().add("btn-fin");
        btnQuitter.setOnAction(e -> Platform.exit());

        // Agencement des boutons
        HBox blocBoutons = new HBox(30, btnRejouer, btnQuitter);
        blocBoutons.setAlignment(Pos.CENTER);
        blocBoutons.setOpacity(0); // Prêt pour le fondu après 2 secondes

        this.layoutVictoire = new VBox(25, titreVictoire, sousTitre, blocBoutons);
        this.layoutVictoire.setAlignment(Pos.CENTER);

        // Centrage au milieu du conteneur
        this.layoutVictoire.layoutXProperty().bind(conteneurPrincipal.widthProperty().subtract(layoutVictoire.widthProperty()).divide(2));
        this.layoutVictoire.layoutYProperty().bind(conteneurPrincipal.heightProperty().subtract(layoutVictoire.heightProperty()).divide(2));

        // Animation des 2s de pause et du fondu de 1s
        PauseTransition pause = new PauseTransition(Duration.seconds(1));
        FadeTransition fondu = new FadeTransition(Duration.seconds(1), blocBoutons);
        fondu.setFromValue(0);
        fondu.setToValue(1);

        SequentialTransition sequence = new SequentialTransition(pause, fondu);
        sequence.play();
    }

    public void afficherSur(Pane conteneur) {
        this.calqueSombre.setViewOrder(-10.0);
        this.layoutVictoire.setViewOrder(-11.0);
        conteneur.getChildren().addAll(calqueSombre, layoutVictoire);
    }
}