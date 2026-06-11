package universite_paris8.iut.aboudhan.saes2javafx.modele;

import java.util.ArrayList;
import java.util.List;

public class GestionnaireVagues {
    public List<Vague> listeVagues;
    public int numVagueActu;

    public GestionnaireVagues(){
        this.listeVagues = new ArrayList<>();
        this.numVagueActu = 0;
    }

    public void initialiserVagues(Environnement env) {
        String prefixe = "/universite_paris8/iut/aboudhan/saes2javafx/vue/";

        Vague v1 = new Vague(2.0, 30);
        for (int i = 0; i < 14; i++) {
            int choixMicrobe = (int) (Math.random() * 2);

            switch (choixMicrobe) {
                case 0:
                    v1.getFileAttenteMicrobes().add(new Microbe(0.8, 30, 2, 1, prefixe + "rhinovirus.png", env.creerItineraireAleatoire()));
                    break;
                case 1:
                    v1.getFileAttenteMicrobes().add(new Microbe(1, 40, 3, 1, prefixe + "norovirus.png", env.creerItineraireAleatoire()));
                    break;
            }
        }
        this.listeVagues.add(v1);

        Vague v2 = new Vague(2.0, 40);
        for (int i = 0; i < 12; i++) {
            int choixMicrobe = (int) (Math.random() * 3);
            switch (choixMicrobe) {
                case 0:
                    v2.getFileAttenteMicrobes().add(new Microbe(0.8, 30, 2, 1, prefixe + "rhinovirus.png", env.creerItineraireAleatoire()));
                    break;
                case 1:
                    v2.getFileAttenteMicrobes().add(new Microbe(1, 40, 3, 1, prefixe + "norovirus.png", env.creerItineraireAleatoire()));
                    break;
                case 2:
                    v2.getFileAttenteMicrobes().add(new Microbe(0.9, 60, 5, 2, prefixe + "streptocoque.png", env.creerItineraireAleatoire()));
                    break;
            }
        }
        this.listeVagues.add(v2);

        Vague v3 = new Vague(2.5, 50);
        for (int i = 0; i < 12; i++) {
            int choixMicrobe = (int) ((Math.random() * 3)+1);
            switch (choixMicrobe) {
                case 1:
                    v3.getFileAttenteMicrobes().add(new Microbe(1, 40, 3, 1, prefixe + "norovirus.png", env.creerItineraireAleatoire()));
                    break;
                case 2:
                    v3.getFileAttenteMicrobes().add(new Microbe(0.9, 60, 5, 2, prefixe + "streptocoque.png", env.creerItineraireAleatoire()));
                    break;
                case 3:
                    v3.getFileAttenteMicrobes().add(new Microbe(0.7, 10, 10, 3, prefixe + "influenza.png", env.creerItineraireAleatoire()));
                    break;
            }
        }
        this.listeVagues.add(v3);

        Vague v4 = new Vague(2.5, 60);
        for (int i = 0; i < 16; i++) {
            int choixMicrobe = (int) ((Math.random() * 4)+1);
            switch (choixMicrobe) {
                case 1:
                    v4.getFileAttenteMicrobes().add(new Microbe(1, 40, 3, 1, prefixe + "norovirus.png", env.creerItineraireAleatoire()));
                    break;
                case 2:
                    v4.getFileAttenteMicrobes().add(new Microbe(0.9, 60, 5, 2, prefixe + "streptocoque.png", env.creerItineraireAleatoire()));
                    break;
                case 3:
                    v4.getFileAttenteMicrobes().add(new Microbe(0.7, 10, 10, 3, prefixe + "influenza.png", env.creerItineraireAleatoire()));
                    break;
                case 4:
                    v4.getFileAttenteMicrobes().add(new Microbe(1, 120, 15, 5, prefixe + "varicelle.png", env.creerItineraireAleatoire()));
                    break;
            }
        }
        this.listeVagues.add(v4);

        Vague v5 = new Vague(3.0, 30);
        for (int i = 0; i < 15; i++) {
            int choixMicrobe = (int) ((Math.random() * 5)+1);
            switch (choixMicrobe) {
                case 1:
                    v5.getFileAttenteMicrobes().add(new Microbe(1, 40, 3, 1, prefixe + "norovirus.png", env.creerItineraireAleatoire()));
                    break;
                case 2:
                    v5.getFileAttenteMicrobes().add(new Microbe(0.9, 60, 5, 2, prefixe + "streptocoque.png", env.creerItineraireAleatoire()));
                    break;
                case 3:
                    v5.getFileAttenteMicrobes().add(new Microbe(0.7, 10, 10, 3, prefixe + "influenza.png", env.creerItineraireAleatoire()));
                    break;
                case 4:
                    v5.getFileAttenteMicrobes().add(new Microbe(1, 120, 15, 5, prefixe + "varicelle.png", env.creerItineraireAleatoire()));
                    break;
                case 5:
                    v5.getFileAttenteMicrobes().add(new Microbe(0.8, 200, 20, 8, prefixe + "covid.png", env.creerItineraireAleatoire()));
                    break;
            }
        }
        this.listeVagues.add(v5);
    }

    public int getNumVagueActu() {
        return numVagueActu;
    }

    public Vague getVagueActuelle() {
        if (numVagueActu < listeVagues.size()) {
            return listeVagues.get(numVagueActu);
        }
        return null;
    }

    public void AugmenterVague(){
        this.numVagueActu ++;
    }

    public boolean estDerniereVague(){
        return numVagueActu == 9;
    }
}
