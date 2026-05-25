package universite_paris8.iut.aboudhan.saes2javafx.vue;

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

public class DefaiteVue {

    private final Pane calqueSombre;
    private final VBox layoutDefaite;

    public DefaiteVue(Pane conteneurPrincipal, TilePane grilleJeu, Runnable actionRejouer) {
        // On donne au calque la taille EXACTE de la map
        this.calqueSombre = new Pane();
        this.calqueSombre.prefWidthProperty().bind(grilleJeu.widthProperty());
        this.calqueSombre.prefHeightProperty().bind(grilleJeu.heightProperty());

        // On le positionne pile au même endroit que la map dans le conteneur
        this.calqueSombre.layoutXProperty().bind(grilleJeu.layoutXProperty());
        this.calqueSombre.layoutYProperty().bind(grilleJeu.layoutYProperty());
        this.calqueSombre.getStyleClass().add("calque-sombre");

        // 2. Création des textes
        Text titreDefaite = new Text("DÉFAITE");
        titreDefaite.getStyleClass().add("titre-defaite");

        Text sousTitre = new Text("+70 personnes ont été infectées ! L'épidémie est devenue incontrôlable...");
        sousTitre.getStyleClass().add("sous-titre-defaite");

        String prefixe = "/universite_paris8/iut/aboudhan/saes2javafx/vue/";

        // 3. Bouton Rejouer
        Button btnRejouer = new Button();
        ImageView imgRejouer = new ImageView(new Image(getClass().getResourceAsStream(prefixe + "rejouer.png")));
        imgRejouer.setFitWidth(80); // Légère réduction pour coller à ton screenshot
        imgRejouer.setPreserveRatio(true);
        btnRejouer.setGraphic(imgRejouer);

        // CORRECTION DU FOND BLANC : On applique la classe CSS qui retire le fond par défaut
        btnRejouer.getStyleClass().add("btn-fin");
        btnRejouer.setOnAction(e -> actionRejouer.run());

        // 4. Bouton Quitter
        Button btnQuitter = new Button();
        ImageView imgQuitter = new ImageView(new Image(getClass().getResourceAsStream(prefixe + "quitter.png")));
        imgQuitter.setFitWidth(80);
        imgQuitter.setPreserveRatio(true);
        btnQuitter.setGraphic(imgQuitter);

        // CORRECTION DU FOND BLANC
        btnQuitter.getStyleClass().add("btn-fin");
        btnQuitter.setOnAction(e -> Platform.exit());

        // 5. Agencement
        HBox blocBoutons = new HBox(30, btnRejouer, btnQuitter);
        blocBoutons.setAlignment(Pos.CENTER);
        blocBoutons.setOpacity(0); // Prêt pour le fondu après 2 secondes

        this.layoutDefaite = new VBox(25, titreDefaite, sousTitre, blocBoutons);
        this.layoutDefaite.setAlignment(Pos.CENTER);

        // Centrage parfait au milieu du conteneur
        this.layoutDefaite.layoutXProperty().bind(conteneurPrincipal.widthProperty().subtract(layoutDefaite.widthProperty()).divide(2));
        this.layoutDefaite.layoutYProperty().bind(conteneurPrincipal.heightProperty().subtract(layoutDefaite.heightProperty()).divide(2));

        // 6. Animation (Pause 2s puis fondu de 1s)
        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        FadeTransition fondu = new FadeTransition(Duration.seconds(1), blocBoutons);
        fondu.setFromValue(0);
        fondu.setToValue(1);

        SequentialTransition sequence = new SequentialTransition(pause, fondu);
        sequence.play();
    }

    public void afficherSur(Pane conteneur) {
        // Force le calque et le texte à s'afficher au-dessus de TOUT (y compris la barre latérale)
        this.calqueSombre.setViewOrder(-10.0);
        this.layoutDefaite.setViewOrder(-11.0); // Encore un peu plus haut pour être sur le calque

        // On ajoute le tout au conteneur
        conteneur.getChildren().addAll(calqueSombre, layoutDefaite);
    }
}