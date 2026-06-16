package universite_paris8.iut.aboudhan.saes2javafx.vue.microbe;

import javafx.animation.FadeTransition;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.util.Duration;

public class GestionnaireEffets {
    private final Pane conteneurPrincipal;

    public GestionnaireEffets(Pane conteneurPrincipal) {
        this.conteneurPrincipal = conteneurPrincipal;
    }

    public void afficherEclairFlash(double deX, double deY, double versX, double versY) {
        Line eclair = new Line(deX + 17, deY, versX + 16, versY + 16);
        eclair.setStroke(javafx.scene.paint.Color.YELLOW);
        eclair.setStrokeWidth(3);
        eclair.getStyleClass().add("effet-eclair");

        conteneurPrincipal.getChildren().add(eclair);

        FadeTransition ft = new FadeTransition(Duration.millis(150), eclair);
        ft.setFromValue(1.0);
        ft.setToValue(0.0);
        ft.setOnFinished(e -> conteneurPrincipal.getChildren().remove(eclair));
        ft.play();
    }
}