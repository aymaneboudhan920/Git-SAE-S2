package universite_paris8.iut.aboudhan.saes2javafx.modele.microbe;

import javafx.collections.ObservableList;
import universite_paris8.iut.aboudhan.saes2javafx.modele.jeu.Environnement;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static javafx.collections.FXCollections.observableArrayList;

public class GestionnaireVagues {
    public final List<Vague> listeVagues;
    public int numVagueActu;
    private final ObservableList<Microbe> listeMicrobes = observableArrayList();
    private final Random random = new Random();

    public GestionnaireVagues() {
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
        listeVagues.clear();
        listeVagues.add(creerVague(env, 1.0, 10, 0, 1, 1));
        listeVagues.add(creerVague(env, 1.5, 15, 0, 2, 2));
        listeVagues.add(creerVague(env, 1.5, 15, 0, 3, 3));
        listeVagues.add(creerVague(env, 1.5, 15, 1, 4, 4));
        listeVagues.add(creerVague(env, 1.0, 20, 1, 5, 5));
        listeVagues.add(creerVague(env, 1.0, 20, 2, 6, 6));
        listeVagues.add(creerVague(env, 0.5, 25, 2, 7, 7));
        listeVagues.add(creerVague(env, 0.5, 25, 3, 8, 8));
        listeVagues.add(creerVague(env, 0.5, 25, 4, 9, 9));
        listeVagues.add(creerVague(env, 1.0, 30, 4, 10, 10));
    }

    private Vague creerVague(Environnement env, double intervalle, int nbMicrobes, int indexMin, int indexMax, int numVague) {
        int bonusArgent = 12 * numVague;
        Vague vague = new Vague(intervalle, bonusArgent);

        for (int i = 0; i < nbMicrobes; i++) {
            int indexAleatoire = random.nextInt((indexMax - indexMin) + 1) + indexMin;
            Microbe m = listeMicrobes.get(indexAleatoire);

            Microbe microbeJeu = new Microbe(
                    m.vitesseDeBase,
                    m.pvMax,
                    m.getRecompense(),
                    m.getInfection(),
                    m.getType(),
                    env.creerItineraireAleatoire()
            );

            vague.getFileAttenteMicrobes().add(microbeJeu);
        }

        return vague;
    }

    public List<Vague> getListeVagues() { return listeVagues; }
    public int getNumVagueActu() { return numVagueActu; }
    public void setNumVagueActu(int numVagueActu) { this.numVagueActu = numVagueActu; }

    public Vague getVagueActuelle() {
        if (numVagueActu < listeVagues.size()) {
            return listeVagues.get(numVagueActu);
        }
        return null;
    }

    public void AugmenterVague() { this.numVagueActu++; }
    public boolean estDerniereVague() { return numVagueActu == this.listeVagues.size() - 1; }
}
