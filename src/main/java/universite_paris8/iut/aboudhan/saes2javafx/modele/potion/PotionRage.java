package universite_paris8.iut.aboudhan.saes2javafx.modele.potion;

import javafx.animation.PauseTransition;
import javafx.util.Duration;
import universite_paris8.iut.aboudhan.saes2javafx.modele.jeu.Environnement;
import universite_paris8.iut.aboudhan.saes2javafx.modele.tour.Tour;

public class PotionRage extends Potion {
    public static int prixAchat = 250;

    public PotionRage() {
        super("Potion de Rage", prixAchat);
    }

    @Override
    public void appliquerEffet(Environnement env) {
        // On double le multiplicateur de vitesse de toutes les tours actuellement posées
        for (Tour tour : env.getToursPosees()) {
            tour.setMultiplicateurVitesse(2.0);
        }
        // On crée un chrono de 5 secondes
        PauseTransition dureeEffet = new PauseTransition(Duration.seconds(5));

        // Quand les 5 secondes sont écoulées, on remet le multiplicateur à 1.0 (vitesse normale)
        dureeEffet.setOnFinished(event -> {
            for (Tour tour : env.getToursPosees()) {
                tour.setMultiplicateurVitesse(1.0);
            }
        });
        dureeEffet.play();
    }
}