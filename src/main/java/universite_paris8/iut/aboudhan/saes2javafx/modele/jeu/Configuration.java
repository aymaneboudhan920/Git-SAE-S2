package universite_paris8.iut.aboudhan.saes2javafx.modele.jeu;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.net.URL;

public class Configuration {
    private double volumeMusique;
    private double volumeBruitages;
    private boolean effetsVisuelsActifs;

    private Clip clipMusique;

    private final String[] pagesInfo;
    private int pageInfoCourante;

    public Configuration() {
        this.volumeMusique = 0.5;
        this.volumeBruitages = 0.7;
        this.effetsVisuelsActifs = true;

        this.pagesInfo = new String[]{
                "Bienvenue au Laboratoire !\n\nVotre but est d'éliminer les microbes avant qu'ils atteignent la sortie en plaçant judicieusement des tours de défense sur le terrain.",
                "Les Tours :\n\nChaque tour a un coût et des caractéristiques propres. Utilisez l'argent gagné au cours de la partie pour en acheter ou pour améliorer celles que vous possédez déjà !",
                "Les Potions :\n\nVous disposez aussi de 3 types de potions (Soin, Rage, Gel). Utilisez-les au bon moment pour réussir à inverser le cours de la partie !",
                "Le Shop :\n\nBien évidemment, il va de soi qu'il faut un shop pour pouvoir équiper son arsenal de la meilleure façon possible !"
        };
        this.pageInfoCourante = 0;

        initialiserEtJouerMusique();
    }

    private void chargerEtJouerClip(String cheminRessource) {
        try {
            URL ressourceAudio = getClass().getResource(cheminRessource);

            if (clipMusique != null) {
                clipMusique.stop();
                clipMusique.close();
            }

            if (ressourceAudio != null) {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(ressourceAudio);
                clipMusique = AudioSystem.getClip();
                clipMusique.open(audioStream);

                clipMusique.loop(Clip.LOOP_CONTINUOUSLY);
                setVolumeMusique(this.volumeMusique);
                clipMusique.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initialiserEtJouerMusique() {
        chargerEtJouerClip("/universite_paris8/iut/aboudhan/saes2javafx/musiqueMenu.wav");
    }

    public void changerDeMusique(String nomFichier) {
        String chemin = "/universite_paris8/iut/aboudhan/saes2javafx/" + nomFichier;
        chargerEtJouerClip(chemin);
    }

    public double getVolumeMusique() {
        return volumeMusique;
    }

    public void setVolumeMusique(double v) {
        this.volumeMusique = v;

        if (clipMusique != null && clipMusique.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl gainControl = (FloatControl) clipMusique.getControl(FloatControl.Type.MASTER_GAIN);

            if (v <= 0.0) {
                gainControl.setValue(gainControl.getMinimum());
                return;
            }

            float minimum = gainControl.getMinimum();
            float maximum = gainControl.getMaximum();

            float plancherAudible = Math.max(minimum, -40.0f);
            float db = plancherAudible + (float)(v * (maximum - plancherAudible));

            db = Math.max(gainControl.getMinimum(), Math.min(gainControl.getMaximum(), db));
            gainControl.setValue(db);
        }
    }

    public double getVolumeBruitages() { return volumeBruitages; }
    public void setVolumeBruitages(double v) { this.volumeBruitages = v; }
    public boolean isEffetsVisuelsActifs() { return effetsVisuelsActifs; }
    public void setEffetsVisuelsActifs(boolean b) { this.effetsVisuelsActifs = b; }

    public String getTexteTutorielCourant() { return pagesInfo[pageInfoCourante]; }
    public void pageSuivante() { if (pageInfoCourante < pagesInfo.length - 1) pageInfoCourante++; }
    public void pagePrecedente() { if (pageInfoCourante > 0) pageInfoCourante--; }
    public boolean estPremierePage() { return pageInfoCourante == 0; }
    public boolean estDernierePage() { return pageInfoCourante == pagesInfo.length - 1; }
    public void reinitialiserTutoriel() { this.pageInfoCourante = 0; }
}
