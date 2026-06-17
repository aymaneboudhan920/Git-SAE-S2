package universite_paris8.iut.aboudhan.saes2javafx.modele.microbe;

import javafx.collections.ObservableList;
import universite_paris8.iut.aboudhan.saes2javafx.modele.jeu.Environnement;

import java.util.ArrayList;
import java.util.List;

import static javafx.collections.FXCollections.observableArrayList;

public class GestionnaireVagues {
    public List<Vague> listeVagues;
    public int numVagueActu;
    public Environnement env;

    private final ObservableList<Microbe> listeMicrobes = observableArrayList();

    public GestionnaireVagues(){
        this.listeVagues = new ArrayList<>();
        this.numVagueActu = 0;
        creerListeMicrobes();
    }

    public void creerListeMicrobes() {
        listeMicrobes.add(new Microbe(0.8, 45, 2, 1, "RHINOVIRUS", null));
        listeMicrobes.add(new Microbe(1.0, 60, 3, 1, "NOROVIRUS", null));
        listeMicrobes.add(new Microbe(0.9, 90, 5, 2, "STREPTOCOQUE", null));
        listeMicrobes.add(new Microbe(0.7, 20, 10, 3, "INFLUENZA", null));
        listeMicrobes.add(new Microbe(1.0, 180, 15, 5, "VARICELLE", null));
        listeMicrobes.add(new Microbe(0.8, 300, 20, 8, "COVID", null));
        listeMicrobes.add(new Microbe(0.6, 375, 30, 10, "VIH", null));
        listeMicrobes.add(new Microbe(0.5, 900, 40, 12, "TUBERCULOSE", null));
        listeMicrobes.add(new Microbe(0.8, 675, 50, 20, "PESTE", null));
        listeMicrobes.add(new Microbe(1.0, 450, 70, 15, "RAGE", null));
        listeMicrobes.add(new Microbe(0.3, 3000, 100, 50, "VARIOLE", null));
    }

    public void initialiserVagues(Environnement env) {

        listeVagues.add(creerVague(env, 1.0, 10, 0, 1));
        listeVagues.add(creerVague(env, 1.5, 15, 0, 2));
        listeVagues.add(creerVague(env, 1.5, 15, 0, 3));
        listeVagues.add(creerVague(env, 1.5, 15, 1, 4));
        listeVagues.add(creerVague(env, 1.0, 20, 1, 5));
        listeVagues.add(creerVague(env, 1.0, 20, 2, 6));
        listeVagues.add(creerVague(env, 0.5, 25, 2, 7));
        listeVagues.add(creerVague(env, 0.5, 25, 3, 8));
        listeVagues.add(creerVague(env, 0.5, 25, 4, 9));
        listeVagues.add(creerVague(env, 1.0, 30, 4, 10));
    }

    private Vague creerVague(Environnement env, double intervalle, int nbMicrobes, int indexMin, int indexMax) {
        Vague vague = new Vague(intervalle, nbMicrobes);

        for (int i = 0; i < nbMicrobes; i++) {
            Microbe m = listeMicrobes.get((int)(Math.random() * (indexMax-indexMin + 1)) + indexMin);

            Microbe microbeJeu = new Microbe(
                    m.vitesseDeBase,
                    m.pvMax,
                    m.getRecompense(),
                    m.infection,
                    m.getType(),
                    env.creerItineraireAleatoire()
            );

            vague.getFileAttenteMicrobes().add(microbeJeu);

        }

        return vague;
    }

    public int getNumVagueActu() { return numVagueActu; }

    public Vague getVagueActuelle() {
        if (numVagueActu < listeVagues.size()) {
            return listeVagues.get(numVagueActu);
        }
        return null;
    }

    public void AugmenterVague(){ this.numVagueActu++; }
    public boolean estDerniereVague(){ return numVagueActu == this.listeVagues.size() - 1; }
}
