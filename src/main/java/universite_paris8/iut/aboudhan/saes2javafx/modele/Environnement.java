package universite_paris8.iut.aboudhan.saes2javafx.modele;

import java.util.ArrayList;
import java.util.List;

public class Environnement {

    private final Terrain terrain;
    private final int[][] grille;
    private final int tailleTuile;
    private int argent = 100, gensInfectes = 0;

    public Environnement() {
        this.terrain = new Terrain();
        this.grille = this.terrain.grille;
        this.tailleTuile = 34;
    }

    // GETTERS
    public int[][] getGrille() {
        return grille;
    }

    public int getTailleTuile() {
        return tailleTuile;
    }

    public int getArgent() {
        return argent;
    }

    public int getGensInfectes() {
        return gensInfectes;
    }

    public Waypoint creerItineraireAleatoire() {
        int ligneEntree = -1, colonneEntree = -1; // Coordonnées de la case de départ
        int ligneSortie = -1, colonneSortie = -1; // Coordonnées de la case d'arrivée
        int ligneConduit4 = -1, colonneConduit4 = -1; // Coordonnées dee l'entrée du conduit ( conduit n°4 )
        // Création d'une liste de tableaux d'entiers pour stocker les coordonnées des sorties de conduits ( conduit n°5 )
        List<int[]> conduits5 = new ArrayList<>();

        // Parcours de la map
        for (int i = 0; i < grille.length; i++) {
            for (int j = 0; j < grille[i].length; j++) {
                // Recherche des cases spéciales ( entrée, sortie, conduits d'aérations )
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
                // Si on trouve une sortie de conduit, on ajoute son couple à la liste
                if (grille[i][j] == 5) {
                    conduits5.add(new int[]{i, j});
                }
            }
        }

        // Création du point de départ
        Waypoint pointDepart = new Waypoint(colonneEntree * tailleTuile, ligneEntree * tailleTuile);
        // Création d'une liste où on stockera dans l'ordre toutes les dalles par lesquelles les microbes passeront pour ensuite pouvoir reconstruire le chemin.
        List<Waypoint> cheminComplet = new ArrayList<>();

        // Tirage au sort du trajet en fonction du nombre de chemins
        int nombreTotalDeChemins = 1 + conduits5.size();
        int choixChemin = (int) (Math.random() * nombreTotalDeChemins);

        if (choixChemin == 0) {
            // Chemin classique direct sans passer par les conduits
            cheminComplet = calculerCheminEntrePoints(ligneEntree, colonneEntree, ligneSortie, colonneSortie);
        } else {
            // On calcule d'abord la première partie du chemin ( entrée -> conduit n°4 )
            List<Waypoint> CheminVersConduit = calculerCheminEntrePoints(ligneEntree, colonneEntree, ligneConduit4, colonneConduit4);
            cheminComplet.addAll(CheminVersConduit); // On ajoute cette première partie au chemin complet

            // On Sélectionne ensuite la bouche de sortie
            int[] conduitSelectionne = conduits5.get(choixChemin - 1);
            int ligneConduit5 = conduitSelectionne[0];
            int colonneConduit5 = conduitSelectionne[1];

            // Insertion des coordonnées de la sortie de conduit selectionnée dans le chemin final
            cheminComplet.add(new Waypoint(colonneConduit5 * tailleTuile, ligneConduit5 * tailleTuile));

            // Calcul de l'autre partie du chemin ( conduit n°5 sélectionné -> sortie )
            List<Waypoint> CheminDepuisConduit5 = calculerCheminEntrePoints(ligneConduit5, colonneConduit5, ligneSortie, colonneSortie);
            cheminComplet.addAll(CheminDepuisConduit5); // On ajoute maintenant la 2ème partie
        }

        // Chaînage de tous les Waypoints
        Waypoint precedent = pointDepart;
        for (Waypoint wp : cheminComplet) {
            precedent.ajouterSuivant(wp);
            precedent = wp;
        }

        // Retourne le point de départ
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

        // Reconstruction du chemin final (A l'envers, en partant de l'arrivée vers le départ)
        List<Waypoint> listeCheminWaypoints = new ArrayList<>();

        if (cibleTrouvee) {
            int ligneCurseur = ligneArrivee;
            int colonneCurseur = colonneArrivee;

            // Tant qu'on n'est pas revenu aux coordonnées de départ
            while (ligneCurseur != ligneDepart || colonneCurseur != colonneDepart) {
                // On convertit les cases en pixels de jeu (Axe X = Colonne, Axe Y = Ligne)
                int positionXEnPixels = colonneCurseur * tailleTuile;
                int positionYEnPixels = ligneCurseur * tailleTuile;

                // On ajoute le point AU DÉBUT de la liste (index 0) pour remettre le chemin à l'endroit
                listeCheminWaypoints.add(0, new Waypoint(positionXEnPixels, positionYEnPixels));

                // On recule d'une case en lisant l'historique de nos parents
                int lignePrecedente = ligneCurseur;
                ligneCurseur = historiqueLignes[lignePrecedente][colonneCurseur];
                colonneCurseur = historiqueColonnes[lignePrecedente][colonneCurseur];
            }
        }
        return listeCheminWaypoints;
    }

    public void ajouterArgent(int montant) {
        this.argent += montant;
    }

    public void reduireArgent(int montant) {
        this.argent -= montant;
        if (this.argent < 0) this.argent = 0; // Sécurité
    }

    public void incrementerInfectes(Microbe m) {
        this.gensInfectes += m.infection;
    }

    public boolean verifierDefaite() {
        return this.gensInfectes >= 70;
    }
}