package universite_paris8.iut.aboudhan.saes2javafx.modele;

import javafx.animation.PauseTransition;
import javafx.util.Duration;

public class PotionGel {

    public static final int prixAchat = 150;

    public void appliquerEffet(Environnement env) {
        // On active le drapeau global dans l'environnement
        env.setMicrobesGeles(true);

        // On fige immédiatement TOUS les microbes actuellement sur le terrain
        for (Microbe m : env.getMicrobesActifs()) {
            m.setVitesseActu(0);
        }

        // On crée un compte à rebours de 3 secondes pour annuler l'effet
        PauseTransition pause = new PauseTransition(Duration.seconds(3));
        pause.setOnFinished(event -> {
            // Le temps est écoulé : on libère le jeu
            env.setMicrobesGeles(false);

            // On redonne leur vitesse d'origine à tous les microbes survivants
            for (Microbe m : env.getMicrobesActifs()) {
                m.reinitialiserVitesse();
            }
        });

        pause.play();
    }
}