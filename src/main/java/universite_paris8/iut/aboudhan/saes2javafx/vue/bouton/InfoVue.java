package universite_paris8.iut.aboudhan.saes2javafx.vue.bouton;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

public class InfoVue {
    private final StackPane calqueFond;

    public InfoVue(Runnable onClose) {
        calqueFond = new StackPane();
        calqueFond.getStyleClass().add("fond-flou-shop");
        calqueFond.setPrefSize(1020, 680);

        VBox fenetre = new VBox(20);
        fenetre.getStyleClass().add("fenetre-shop");
        fenetre.setMaxSize(400, 300);
        fenetre.setAlignment(Pos.CENTER);

        Label titre = new Label("À PROPOS");
        titre.getStyleClass().add("titre-shop");

        Label credits = new Label("SAE S2 - Jeu de Tower Defense\n\nDéveloppé par l'équipe d'IUT Paris 8.\nEvalué par M.Homps le 17/06/2026\nVersion 1.0 - Tous droits réservés.");
        credits.getStyleClass().add("label-credits");

        Button btnFermer = new Button("Fermer");
        btnFermer.getStyleClass().add("btn-fermer-shop");
        btnFermer.setOnAction(e -> onClose.run());

        fenetre.getChildren().addAll(titre, credits, btnFermer);
        calqueFond.getChildren().add(fenetre);
    }

    public void afficherSur(Pane conteneur) { conteneur.getChildren().add(calqueFond); }
    public void cacher(Pane conteneur) { conteneur.getChildren().remove(calqueFond); }
}