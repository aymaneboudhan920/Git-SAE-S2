package universite_paris8.iut.aboudhan.saes2javafx.modele.jeu;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import universite_paris8.iut.aboudhan.saes2javafx.modele.microbe.GestionnaireVagues;
import universite_paris8.iut.aboudhan.saes2javafx.modele.microbe.Microbe;
import universite_paris8.iut.aboudhan.saes2javafx.modele.tour.Projectile;
import universite_paris8.iut.aboudhan.saes2javafx.modele.tour.Tour;
import universite_paris8.iut.aboudhan.saes2javafx.modele.jeu.Waypoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Environnement {

    private final Terrain terrain;
    private final int[][] grille;
    private final int tailleTuile;

    private final IntegerProperty nbPotionSoin = new SimpleIntegerProperty(0);
    private final IntegerProperty nbPotionRage = new SimpleIntegerProperty(0);
    private final IntegerProperty nbPotionGel = new SimpleIntegerProperty(0);

    private final IntegerProperty argent = new SimpleIntegerProperty(50);
    private final IntegerProperty gensInfectes = new SimpleIntegerProperty(0);

    private final GestionnaireVagues gestionnaireVagues;
    private final List<Microbe> microbesActifs = new ArrayList<>();
    private final List<Tour> toursPosees = new ArrayList<>();
    private final List<Projectile> projectilesActifs = new ArrayList<>();

    private final Map<Tour, Integer> tourVersIndexInventaire = new HashMap<>();
    private final Map<Tour, int[]> tourVersCaseGrille = new HashMap<>();

    private boolean microbesGeles = false;

    public Environnement() {
        this.terrain = new Terrain();
        this.grille = this.terrain.grille;
        this.tailleTuile = 34;
        this.gestionnaireVagues = new GestionnaireVagues();
    }

    public int getArgent() { return this.argent.get(); }
    public List<Projectile> getProjectilesActifs() { return projectilesActifs; }
    public int getGensInfectes() { return this.gensInfectes.get(); }
    public void setGensInfectes(int valeur) { this.gensInfectes.set(valeur); }

    public int[][] getGrille() { return grille; }
    public int getTailleTuile() { return tailleTuile; }
    public GestionnaireVagues getGestionnaireVagues() { return gestionnaireVagues; }
    public List<Microbe> getMicrobesActifs() { return microbesActifs; }
    public boolean isMicrobesGeles() { return this.microbesGeles; }
    public void setMicrobesGeles(boolean etat) { this.microbesGeles = etat; }

    public List<Tour> getToursPosees() { return toursPosees; }

    public Map<Tour, Integer> getTourVersIndexInventaire() { return tourVersIndexInventaire; }

    public int getNbPotionSoin() { return this.nbPotionSoin.get(); }
    public void setNbPotionSoin(int valeur) { this.nbPotionSoin.set(valeur); }
    public IntegerProperty nbPotionSoinProperty() { return this.nbPotionSoin; }

    public int getNbPotionRage() { return this.nbPotionRage.get(); }
    public void setNbPotionRage(int valeur) { this.nbPotionRage.set(valeur); }
    public IntegerProperty nbPotionRageProperty() { return this.nbPotionRage; }

    public int getNbPotionGel() { return this.nbPotionGel.get(); }
    public void setNbPotionGel(int valeur) { this.nbPotionGel.set(valeur); }
    public IntegerProperty nbPotionGelProperty() { return this.nbPotionGel; }

    public IntegerProperty argentProperty() { return this.argent; }
    public IntegerProperty gensInfectesProperty() { return this.gensInfectes; }

    public void ajouterArgent(int montant) {
        this.argent.set(this.argent.get() + montant);
    }

    public void reduireArgent(int montant) {
        this.argent.set(Math.max(0, this.argent.get() - montant));
    }

    public void AugmenterNbInfectes(Microbe m) {
        this.gensInfectes.set(this.gensInfectes.get() + m.infection);
    }

    public boolean verifierDefaite() {
        return this.gensInfectes.get() >= 70;
    }

    public void enregistrerTourPosee(Tour tour, int caseX, int caseY, int indexInventaire) {
        grille[caseY][caseX] = 99;
        tourVersCaseGrille.put(tour, new int[]{caseX, caseY});
        tourVersIndexInventaire.put(tour, indexInventaire);
        toursPosees.add(tour);
    }

    public void rappelerTour(Tour tour) {
        int[] caseGrille = tourVersCaseGrille.remove(tour);
        if (caseGrille != null) {
            grille[caseGrille[1]][caseGrille[0]] = 0;
        }
        toursPosees.remove(tour);
    }

    public boolean ameliorerTour(Tour tour) {
        if (tour != null && tour.peutEtreAmelioree()) {
            int cout = tour.calculerPrixAmelioration();
            if (this.argent.get() >= cout) {
                this.argent.set(this.argent.get() - cout);
                tour.ameliorer();
                return true;
            }
        }
        return false;
    }

    public void vendreTour(Tour tour) {
        if (tour != null) {
            int gain = tour.calculerValeurVente();
            this.argent.set(this.argent.get() + gain);
            int[] caseGrille = tourVersCaseGrille.remove(tour);
            if (caseGrille != null) {
                grille[caseGrille[1]][caseGrille[0]] = 0;
            }
            toursPosees.remove(tour);
            this.getTourVersIndexInventaire().remove(tour);
        }
    }

    public void ajouterProjectile(Projectile p) {
        this.projectilesActifs.add(p);
    }

    public void mettreAJourProjectiles() {
        for (int i = projectilesActifs.size() - 1; i >= 0; i--) {
            Projectile p = projectilesActifs.get(i);
            p.deplacer(microbesActifs);
            if (p.estDetruit()) {
                projectilesActifs.remove(i);
            }
        }
    }

    public boolean updateMicrobes() {
        if (this.microbesGeles) {
            return false;
        }
        boolean microbeSorti = false;

        for (int i = microbesActifs.size() - 1; i >= 0; i--) {
            Microbe m = microbesActifs.get(i);
            int caseJ = (int) (m.getX() / tailleTuile);
            int caseI = (int) (m.getY() / tailleTuile);

            if (caseI >= 0 && caseI < grille.length && caseJ >= 0 && caseJ < grille[0].length) {
                m.appliquerRalentissement(grille[caseI][caseJ] == 6);
            }
            m.deplacer();

            if (m.getWaypointCible() == null) {
                AugmenterNbInfectes(m);
                microbesActifs.remove(i);
                microbeSorti = true;
            }
        }
        return microbeSorti;
    }

    public void reinitialiser() {
        this.microbesActifs.clear();
        this.toursPosees.clear();
        this.tourVersIndexInventaire.clear();
        this.tourVersCaseGrille.clear();
        this.microbesGeles = false;

        this.argent.set(50);
        this.gensInfectes.set(0);

        this.nbPotionSoin.set(0);
        this.nbPotionRage.set(0);
        this.nbPotionGel.set(0);

        this.gestionnaireVagues.listeVagues.clear();
        this.gestionnaireVagues.numVagueActu = 0;
        this.gestionnaireVagues.initialiserVagues(this);

        for (int i = 0; i < grille.length; i++) {
            for (int j = 0; j < grille[i].length; j++) {
                if (grille[i][j] == 99) {
                    grille[i][j] = 0;
                }
            }
        }
    }

    public Waypoint creerItineraireAleatoire() {
        int ligneEntree = -1, colonneEntree = -1;
        int ligneSortie = -1, colonneSortie = -1;
        int ligneConduit4 = -1, colonneConduit4 = -1;
        List<int[]> conduits5 = new ArrayList<>();

        for (int i = 0; i < grille.length; i++) {
            for (int j = 0; j < grille[i].length; j++) {
                if (grille[i][j] == 2) {
                    ligneEntree = i;
                    colonneEntree = j;
                } else if (grille[i][j] == 3) {
                    ligneSortie = i;
                    colonneSortie = j;
                } else if (grille[i][j] == 4) {
                    ligneConduit4 = i;
                    colonneConduit4 = j;
                } else if (grille[i][j] == 5) {
                    conduits5.add(new int[]{i, j});
                }
            }
        }

        Waypoint pointDepart = new Waypoint(colonneEntree * tailleTuile, ligneEntree * tailleTuile);
        List<List<Waypoint>> cheminsValides = new ArrayList<>();

        List<Waypoint> cheminClassique = calculerCheminEntrePoints(ligneEntree, colonneEntree, ligneSortie, colonneSortie);
        if (!cheminClassique.isEmpty()) {
            cheminsValides.add(cheminClassique);
        }

        List<Waypoint> cheminVersConduit4 = calculerCheminEntrePoints(ligneEntree, colonneEntree, ligneConduit4, colonneConduit4);

        if (!cheminVersConduit4.isEmpty()) {
            for (int[] conduitSelectionne : conduits5) {
                int ligneConduit5 = conduitSelectionne[0];
                int colonneConduit5 = conduitSelectionne[1];

                List<Waypoint> cheminDepuisConduit5 = calculerCheminEntrePoints(ligneConduit5, colonneConduit5, ligneSortie, colonneSortie);

                if (!cheminDepuisConduit5.isEmpty()) {
                    List<Waypoint> cheminConduitComplet = new ArrayList<>(cheminVersConduit4);
                    cheminConduitComplet.add(new Waypoint(colonneConduit5 * tailleTuile, ligneConduit5 * tailleTuile));
                    cheminConduitComplet.addAll(cheminDepuisConduit5);
                    cheminsValides.add(cheminConduitComplet);
                }
            }
        }

        List<Waypoint> cheminFinal;
        if (!cheminsValides.isEmpty()) {
            int indexAleatoire = (int) (Math.random() * cheminsValides.size());
            cheminFinal = cheminsValides.get(indexAleatoire);
        } else {
            return pointDepart;
        }

        Waypoint precedent = pointDepart;
        for (Waypoint wp : cheminFinal) {
            precedent.ajouterSuivant(wp);
            precedent = wp;
        }

        return pointDepart;
    }

    public List<Waypoint> calculerCheminEntrePoints(int ligneDepart, int colonneDepart, int ligneArrivee, int colonneArrivee) {
        int[][] historiqueLignes = new int[grille.length][grille[0].length];
        int[][] historiqueColonnes = new int[grille.length][grille[0].length];
        boolean[][] casesVisitees = new boolean[grille.length][grille[0].length];

        List<Integer> fileAttenteLignes = new ArrayList<>();
        List<Integer> fileAttenteColonnes = new ArrayList<>();

        fileAttenteLignes.add(ligneDepart);
        fileAttenteColonnes.add(colonneDepart);
        casesVisitees[ligneDepart][colonneDepart] = true;

        int[] decalageLignes = {-1, 1, 0, 0};
        int[] decalageColonnes = {0, 0, -1, 1};
        boolean cibleTrouvee = false;

        while (!fileAttenteLignes.isEmpty() && !cibleTrouvee) {
            int ligneActuelle = fileAttenteLignes.remove(0);
            int colonneActuelle = fileAttenteColonnes.remove(0);

            if (ligneActuelle == ligneArrivee && colonneActuelle == colonneArrivee) {
                cibleTrouvee = true;
            }

            for (int direction = 0; direction < 4 && !cibleTrouvee; direction++) {
                int ligneVoisine = ligneActuelle + decalageLignes[direction];
                int colonneVoisine = colonneActuelle + decalageColonnes[direction];

                if (ligneVoisine >= 0 && ligneVoisine < grille.length && colonneVoisine >= 0 && colonneVoisine < grille[0].length) {
                    int typeDalle = grille[ligneVoisine][colonneVoisine];
                    boolean estMarchable = (typeDalle >= 1 && typeDalle <= 6);
                    boolean estDejaExploree = casesVisitees[ligneVoisine][colonneVoisine];

                    if (estMarchable && !estDejaExploree) {
                        casesVisitees[ligneVoisine][colonneVoisine] = true;
                        historiqueLignes[ligneVoisine][colonneVoisine] = ligneActuelle;
                        historiqueColonnes[ligneVoisine][colonneVoisine] = colonneActuelle;

                        fileAttenteLignes.add(ligneVoisine);
                        fileAttenteColonnes.add(colonneVoisine);
                    }
                }
            }
        }

        List<Waypoint> listeCheminWaypoints = new ArrayList<>();
        if (cibleTrouvee) {
            int ligneCurseur = ligneArrivee;
            int colonneCurseur = colonneArrivee;

            while (ligneCurseur != ligneDepart || colonneCurseur != colonneDepart) {
                int positionXEnPixels = colonneCurseur * tailleTuile;
                int positionYEnPixels = ligneCurseur * tailleTuile;

                listeCheminWaypoints.add(0, new Waypoint(positionXEnPixels, positionYEnPixels));

                int lignePrecedente = ligneCurseur;
                ligneCurseur = historiqueLignes[lignePrecedente][colonneCurseur];
                colonneCurseur = historiqueColonnes[lignePrecedente][colonneCurseur];
            }
        }
        return listeCheminWaypoints;
    }
}
