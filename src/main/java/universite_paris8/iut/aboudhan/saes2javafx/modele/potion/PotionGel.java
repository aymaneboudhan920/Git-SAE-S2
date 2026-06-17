package universite_paris8.iut.aboudhan.saes2javafx.modele.potion;

import javafx.animation.PauseTransition;
import javafx.util.Duration;
import universite_paris8.iut.aboudhan.saes2javafx.modele.jeu.Environnement;
import universite_paris8.iut.aboudhan.saes2javafx.modele.microbe.Microbe;

public class PotionGel {

    public static final int prixAchat = 75;

    public void appliquerEffet(Environnement env) {
        env.setMicrobesGeles(true);

        for (Microbe m : env.getMicrobesActifs()) {
            m.setVitesseActu(0);
        }

        PauseTransition pause = new PauseTransition(Duration.seconds(3));
        pause.setOnFinished(event -> {
            env.setMicrobesGeles(false);

            for (Microbe m : env.getMicrobesActifs()) {
                m.reinitialiserVitesse();
            }
        });

        pause.play();
    }
}
