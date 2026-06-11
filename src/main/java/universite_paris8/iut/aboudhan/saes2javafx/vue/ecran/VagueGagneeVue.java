package universite_paris8.iut.aboudhan.saes2javafx.vue.ecran;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class VagueGagneeVue {

    private final StackPane conteneurOverlay;
    private int secondesRestantes = 5; // Temps d'affichage total de l'écran

    public VagueGagneeVue(Pane conteneurPrincipal, TilePane grilleJeu, int numeroVagueTerminee, Runnable actionApresFin) {
        // Conteneur racine de l'overlay qui va empiler le fond sombre et le texte
        this.conteneurOverlay = new StackPane();

        // On le cale exactement sur les dimensions et la position de la grille de jeu
        this.conteneurOverlay.prefWidthProperty().bind(grilleJeu.widthProperty());
        this.conteneurOverlay.prefHeightProperty().bind(grilleJeu.heightProperty());
        this.conteneurOverlay.layoutXProperty().bind(grilleJeu.layoutXProperty());
        this.conteneurOverlay.layoutYProperty().bind(grilleJeu.layoutYProperty());

        // Création du calque sombre de fond
        Pane calqueSombre = new Pane();
        calqueSombre.getStyleClass().add("calque-sombre");

        // On force le calque sombre à prendre toute la taille du StackPane
        calqueSombre.prefWidthProperty().bind(conteneurOverlay.prefWidthProperty());
        calqueSombre.prefHeightProperty().bind(conteneurOverlay.prefHeightProperty());

        // Création des textes de fin de vague
        Text titre = new Text("VAGUE " + numeroVagueTerminee + " NETTOYÉE !");
        titre.getStyleClass().addAll("titre-fin", "titre-vague-gagnee");

        Text sousTitre = new Text("Préparation pour la vague suivante...");
        sousTitre.getStyleClass().add("sous-titre-fin");

        // Décompte
        Text decompteText = new Text("");
        decompteText.getStyleClass().add("decompte-vague");

        // Assemblement des textes
        VBox layoutMessage = new VBox(15, titre, sousTitre, decompteText);
        layoutMessage.setAlignment(Pos.CENTER);

        // On ajoute le fond, puis le texte par-dessus dans le StackPane
        this.conteneurOverlay.getChildren().addAll(calqueSombre, layoutMessage);

        // Fait passer le texte au premier plan
        this.conteneurOverlay.setViewOrder(-10.0);

        Timeline chrono = new Timeline(
                new KeyFrame(Duration.seconds(1), event -> {
                    secondesRestantes--;

                    // Affiche le décompte pendants les 3 dernières secondes
                    if (secondesRestantes <= 3 && secondesRestantes > 0) {
                        decompteText.setText(String.valueOf(secondesRestantes));
                    // Enlève à la fin du décompte
                    } else if (secondesRestantes == 0) {
                        conteneurPrincipal.getChildren().remove(conteneurOverlay);
                        actionApresFin.run();
                    }
                })
        );

        // Répéter 5 fois la Timeline pour faire 5 secondes au total
        chrono.setCycleCount(5);
        chrono.play();
    }

    public void afficherSur(Pane conteneur) {
        conteneur.getChildren().add(conteneurOverlay);
    }
}