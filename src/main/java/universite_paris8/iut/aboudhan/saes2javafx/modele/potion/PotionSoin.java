package universite_paris8.iut.aboudhan.saes2javafx.modele.potion;

import universite_paris8.iut.aboudhan.saes2javafx.modele.jeu.Environnement;

public class PotionSoin extends Potion {
    public static int prixAchat = 300;
    private final int nbSoignee = 10;

    public PotionSoin() {
        super("Potion de Soin", prixAchat); // Nom et prix exemple
    }

    @Override
    public void appliquerEffet(Environnement env) {
        int infectesActuels = env.getGensInfectes();
        // On soigne en réduisant le nombre d'infectés, sans descendre sous 0
        env.setGensInfectes(Math.max(0, infectesActuels - nbSoignee));
    }
}