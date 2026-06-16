package universite_paris8.iut.aboudhan.saes2javafx.modele.jeu;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import universite_paris8.iut.aboudhan.saes2javafx.modele.microbe.GestionnaireVagues;
import universite_paris8.iut.aboudhan.saes2javafx.modele.microbe.Microbe;
import universite_paris8.iut.aboudhan.saes2javafx.modele.tour.Projectile;
import universite_paris8.iut.aboudhan.saes2javafx.modele.tour.Tour;

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

    private final IntegerProperty argent = new SimpleIntegerProperty(4000);
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
        this.argent.set(this.argent.get() - montant);
        if (this.argent.get() < 0) this.argent.set(0);
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
        if (tour == null) return;
        int[] caseGrille = tourVersCaseGrille.remove(tour);
        if (caseGrille != null) {
            grille[caseGrille[1]][caseGrille[0]] = 0;
        }
        else {
            // Sécurité si les coordonnées en pixels sont utilisées
            int caseX = (int) (tour.getX() / tailleTuile);
            int caseY = (int) (tour.getY() / tailleTuile);
            if (caseY >= 0 && caseY < grille.length && caseX >= 0 && caseX < grille[0].length) {
                grille[caseY][caseX] = 0;
            }
        }
        // Retirer de la liste des tours qui attaquent
        toursPosees.remove(tour);
        // Supprimer l'association avec l'inventaire
        tourVersIndexInventaire.remove(tour);
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
        // Si l'environnement global est gelé, aucun microbe ne bouge et aucun ne sort
        if (this.microbesGeles) {
            return false;
        }
        boolean microbeSorti = false;

        // On parcourt tous les microbes présents dans le jeu en commençant par la fin
        for (int i = microbesActifs.size() - 1; i >= 0; i--) {
            Microbe m = microbesActifs.get(i);
            // Calcul des coordonnées de la case où se trouve le microbe
            int caseJ = (int) (m.getX() / tailleTuile);
            int caseI = (int) (m.getY() / tailleTuile);

            // On vérifie que le microbe n'est pas sorti de la map
            if (caseI >= 0 && caseI < grille.length && caseJ >= 0 && caseJ < grille[0].length) {
                // On le ralentit s'il se trouve sur une case contenant de l'acide ralentissant
                m.appliquerRalentissement(grille[caseI][caseJ] == 6);
            }
            m.deplacer();

            // Si un microbe est sorti,
            if (m.getWaypointCible() == null) {
                AugmenterNbInfectes(m); // Mettre à jour le nb d'infections
                microbesActifs.remove(i); // Retirer le microbe de la liste des microbes en jeu
                microbeSorti = true; // Mettre à true la variable microbeSorti
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

        this.argent.set(100);
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

        // Parcours de la map pour trouver les dalles spéciales
        for (int i = 0; i < grille.length; i++) {
            for (int j = 0; j < grille[i].length; j++) {
                if (grille[i][j] == 2) {
                    ligneEntree = i;
                    colonneEntree = j;
                }
                if (grille[i][j] == 3) {
                    ligneSortie = i;
                    colonneSortie = j;
                }
                if (grille[i][j] == 4) {
                    ligneConduit4 = i;
                    colonneConduit4 = j;
                }
                if (grille[i][j] == 5) {
                    conduits5.add(new int[]{i, j});
                }
            }
        }

        // Création du point de départ initial
        Waypoint pointDepart = new Waypoint(colonneEntree * tailleTuile, ligneEntree * tailleTuile);

        // Liste qui va stocker tous les chemins qui fonctionnent
        List<List<Waypoint>> cheminsValides = new ArrayList<>();

        // Test du chemin sans conduit
        List<Waypoint> cheminClassique = calculerCheminEntrePoints(ligneEntree, colonneEntree, ligneSortie, colonneSortie);
        if (!cheminClassique.isEmpty()) {
            cheminsValides.add(cheminClassique);
        }

        // Test du chemin avec les conduits
        List<Waypoint> cheminVersConduit4 = calculerCheminEntrePoints(ligneEntree, colonneEntree, ligneConduit4, colonneConduit4);

        // Si l'accès au conduit 4 n'est pas bloqué, on teste chaque sortie possible (Dalle 5)
        if (!cheminVersConduit4.isEmpty()) {
            for (int[] conduitSelectionne : conduits5) {
                int ligneConduit5 = conduitSelectionne[0];
                int colonneConduit5 = conduitSelectionne[1];

                // Calcul du trajet de la sortie du conduit (5) vers la sortie finale (3)
                List<Waypoint> cheminDepuisConduit5 = calculerCheminEntrePoints(ligneConduit5, colonneConduit5, ligneSortie, colonneSortie);

                // Si la seconde partie est valide, on assemble le chemin du conduit complet
                if (!cheminDepuisConduit5.isEmpty()) {
                    List<Waypoint> cheminConduitComplet = new ArrayList<>(cheminVersConduit4);

                    // Ajout du point pivot (Bouche de sortie du conduit)
                    cheminConduitComplet.add(new Waypoint(colonneConduit5 * tailleTuile, ligneConduit5 * tailleTuile));

                    cheminConduitComplet.addAll(cheminDepuisConduit5);

                    cheminsValides.add(cheminConduitComplet);
                }
            }
        }

        // Choix du chemin final parmi ceux qui fonctionnent
        List<Waypoint> cheminFinal;

        if (!cheminsValides.isEmpty()) {
            // Choix aléatoire d'un chemin
            int indexAleatoire = (int) (Math.random() * cheminsValides.size());
            cheminFinal = cheminsValides.get(indexAleatoire);
        } else {
            // Si tout est bloqué, on renvoie une liste vide pour éviter de crash.
            return pointDepart;
        }

        // Chaînage de tous les Waypoints sélectionnés
        Waypoint precedent = pointDepart;
        for (Waypoint wp : cheminFinal) {
            precedent.ajouterSuivant(wp);
            precedent = wp;
        }

        return pointDepart;
    }

    private List<Waypoint> calculerCheminEntrePoints(int ligneDepart, int colonneDepart, int ligneArrivee, int colonneArrivee) {
        // Tableaux pour mémoriser d'où l'on vient
        int[][] historiqueLignes = new int[grille.length][grille[0].length];
        int[][] historiqueColonnes = new int[grille.length][grille[0].length];

        // Tableau pour marquer les cases déjà explorées et éviter de tourner en rond
        boolean[][] casesVisitees = new boolean[grille.length][grille[0].length];

        // File pour stocker les dalles à analyser
        List<Integer> fileAttenteLignes = new ArrayList<>();
        List<Integer> fileAttenteColonnes = new ArrayList<>();

        // Initialisation avec les coordonnées de la case de départ
        fileAttenteLignes.add(ligneDepart);
        fileAttenteColonnes.add(colonneDepart);
        // On marque la case de départ comme visitée pour pas y revenir
        casesVisitees[ligneDepart][colonneDepart] = true;

        // Tableaux de décalage formant des paires d'index pour cibler les 4 directions
        // Haut (-1,0), Bas (1,0), Gauche (0,-1), Droite (0,1)
        int[] decalageLignes = {-1, 1, 0, 0};
        int[] decalageColonnes = {0, 0, -1, 1};
        boolean cibleTrouvee = false;

        // Tant qu'il reste des cases à explorer dans la file ET qu'on n'a pas atteint l'arrivée
        while (!fileAttenteLignes.isEmpty() && !cibleTrouvee) {
            // On extrait la première case de la file
            int ligneActuelle = fileAttenteLignes.remove(0);
            int colonneActuelle = fileAttenteColonnes.remove(0);

            // Si la case actuelle est la destination, on arrête l'algorithme
            if (ligneActuelle == ligneArrivee && colonneActuelle == colonneArrivee) {
                cibleTrouvee = true;
            }

            // On regarde les 4 cases voisines
            for (int direction = 0; direction < 4 && !cibleTrouvee; direction++) {
                int ligneVoisine = ligneActuelle + decalageLignes[direction];
                int colonneVoisine = colonneActuelle + decalageColonnes[direction];

                // On vérifie que la case voisine ne sort pas des limites du tableau
                if (ligneVoisine >= 0 && ligneVoisine < grille.length && colonneVoisine >= 0 && colonneVoisine < grille[0].length) {
                    // Récupération du type de la dalle
                    int typeDalle = grille[ligneVoisine][colonneVoisine];

                    // On vérifie si la dalle est marchable et pas encore visitée
                    boolean estMarchable = (typeDalle == 1 || typeDalle == 2 || typeDalle == 3 || typeDalle == 4 || typeDalle == 5 || typeDalle == 6);
                    boolean estDejaExploree = casesVisitees[ligneVoisine][colonneVoisine];

                    // Si la case est marchable est pas encore visitée
                    if (estMarchable && !estDejaExploree) {
                        // On valide la case voisine en tant que case visitée
                        casesVisitees[ligneVoisine][colonneVoisine] = true;

                        // On note dans l'historique quelle case a découvert cette case voisine
                        historiqueLignes[ligneVoisine][colonneVoisine] = ligneActuelle;
                        historiqueColonnes[ligneVoisine][colonneVoisine] = colonneActuelle;

                        // On l'ajoute à la file pour aller voir ses propres voisines plus tard
                        fileAttenteLignes.add(ligneVoisine);
                        fileAttenteColonnes.add(colonneVoisine);
                    }
                }
            }
        }

        // Reconstruction du chemin final 
        List<Waypoint> listeCheminWaypoints = new ArrayList<>();

        if (cibleTrouvee) {
            int ligneCurseur = ligneArrivee;
            int colonneCurseur = colonneArrivee;

            // Tant qu'on n'est pas revenu aux coordonnées de départ
            while (ligneCurseur != ligneDepart || colonneCurseur != colonneDepart) {
                // On convertit les cases en pixels de jeu (Axe X = Colonne, Axe Y = Ligne)
                int positionXEnPixels = colonneCurseur * tailleTuile;
                int positionYEnPixels = ligneCurseur * tailleTuile;

                // On ajoute le point au début de la liste pour remettre le chemin à l'endroit
                listeCheminWaypoints.add(0, new Waypoint(positionXEnPixels, positionYEnPixels));

                // On recule d'une case en lisant l'historique de nos parents
                int lignePrecedente = ligneCurseur;
                ligneCurseur = historiqueLignes[lignePrecedente][colonneCurseur];
                colonneCurseur = historiqueColonnes[lignePrecedente][colonneCurseur];
            }
        }
        return listeCheminWaypoints;
    }
}
