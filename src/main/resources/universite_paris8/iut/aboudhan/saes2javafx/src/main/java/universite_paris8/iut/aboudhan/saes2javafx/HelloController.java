package universite_paris8.iut.aboudhan.saes2javafx;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.util.Duration;

public class HelloController implements Initializable {

    @FXML
    private Pane conteneurPrincipal;

    @FXML
    private TilePane grilleJeu;

    private Terrain terrain = new Terrain();
    private List<Microbe> microbesActifs = new ArrayList<>();
    private java.util.Map<Microbe, ImageView> vuesMicrobes = new java.util.HashMap<>();
    private AnimationTimer gameLoop;
    private Timeline timelineSpawn;

    private List<Microbe> fileAttenteMicrobes = new ArrayList<>();

    int[][] grille = terrain.grille;
    int tailleTuile = 34;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        creerMap();

        // Ajout des microbes dans la file d'attente
        fileAttenteMicrobes.add(new Microbe(0.8, 30, 2, "rhinovirus.png", creerItineraireAleatoire()));
        fileAttenteMicrobes.add(new Microbe(1, 40, 3, "norovirus.png", creerItineraireAleatoire()));
        fileAttenteMicrobes.add(new Microbe(0.9, 60, 5, "streptocoque.png", creerItineraireAleatoire()));
        fileAttenteMicrobes.add(new Microbe(0.7, 10, 10, "influenza.png", creerItineraireAleatoire()));
        fileAttenteMicrobes.add(new Microbe(1, 120, 15, "varicelle.png", creerItineraireAleatoire()));
        fileAttenteMicrobes.add(new Microbe(0.8, 200, 20, "covid.png", creerItineraireAleatoire()));
        fileAttenteMicrobes.add(new Microbe(0.6, 250, 30, "vih.png", creerItineraireAleatoire()));
        fileAttenteMicrobes.add(new Microbe(0.5, 600, 40, "tuberculose.png", creerItineraireAleatoire()));
        fileAttenteMicrobes.add(new Microbe(0.8, 450, 50, "peste.png", creerItineraireAleatoire()));
        fileAttenteMicrobes.add(new Microbe(1, 300, 70, "rage.png", creerItineraireAleatoire()));
        fileAttenteMicrobes.add(new Microbe(0.3, 2000, 100, "variole.png", creerItineraireAleatoire()));

        creerGameLoop();
        creerTimelineSpawn();
    }

    private boolean jeuDemarre = false;

    @FXML
    private void actionBoutonStart(javafx.event.ActionEvent event) {
        if (!jeuDemarre) { // Vérification que le jeu n'est pas déjà lancé dans le cas d'un double clic sur le bouton
            jeuDemarre = true;

            Button boutonSource = (Button) event.getSource();

            // Animation de clic sur le bouton Start
            ScaleTransition st = new ScaleTransition(Duration.millis(100), boutonSource); // Durée de l'animation -> 100 Ms
            // Réduction de la taille du bouton à 95%
            st.setToX(0.95);
            st.setToY(0.95);
            // Effet inverse
            st.setAutoReverse(true);
            // 2 pour l'aller et le retour
            st.setCycleCount(2);

            // Désactiver le bouton après clic
            st.setOnFinished(e -> boutonSource.setDisable(true));
            st.play();

            // Démarrage de la loop
            gameLoop.start();
            timelineSpawn.play();
        }
    }

    private void creerTimelineSpawn() {
        timelineSpawn = new Timeline(
                new KeyFrame(Duration.seconds(3), event -> {
                    if (!fileAttenteMicrobes.isEmpty()) {
                        Microbe prochainMicrobe = fileAttenteMicrobes.remove(0);
                        microbesActifs.add(prochainMicrobe);

                        ImageView imageVue = new ImageView(new Image(getClass().getResourceAsStream(prochainMicrobe.getNomImage())));
                        imageVue.setFitWidth(34);
                        imageVue.setFitHeight(34);
                        imageVue.setTranslateX(prochainMicrobe.getX());
                        imageVue.setTranslateY(prochainMicrobe.getY());

                        vuesMicrobes.put(prochainMicrobe, imageVue);
                        conteneurPrincipal.getChildren().add(imageVue);
                    } else {
                        // Si la file d'attente est vide, on arrête la Timeline pour économiser les ressources
                        timelineSpawn.stop();
                    }
                })
        );
        // Indique que la Timeline doit se répéter en boucle tant qu'on ne l'arrête pas
        timelineSpawn.setCycleCount(Animation.INDEFINITE);
    }

    private void creerGameLoop(){
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                for (int i = microbesActifs.size() - 1; i >= 0; i--){
                    Microbe m = microbesActifs.get(i);
                    int caseJ = (int) (m.getX() / tailleTuile);
                    int caseI = (int) (m.getY() / tailleTuile);

                    if (caseI >= 0 && caseI < grille.length && caseJ >= 0 && caseJ < grille[0].length) {
                        if (grille[caseI][caseJ] == 6) {
                            m.appliquerRalentissement(true);
                        } else {
                            m.appliquerRalentissement(false);
                        }
                    }
                    m.deplacer();

                    ImageView imageVue = vuesMicrobes.get(m);
                    if (imageVue != null) {
                        imageVue.setTranslateX(m.getX());
                        imageVue.setTranslateY(m.getY());
                    }

                    if(m.getWaypointCible() == null){
                        if (imageVue != null) {
                            conteneurPrincipal.getChildren().remove(imageVue);
                        }
                        vuesMicrobes.remove(m);
                        microbesActifs.remove(i);
                    }
                }
            }
        };
    }

    private List<Waypoint> calculerCheminEntrePoints(int ligneDepart, int colonneDepart, int ligneArrivee, int colonneArrivee) {
        // Tableaux pour mémoriser d'où l'on vient
        int[][] historiqueLignes = new int[grille.length][grille[0].length];
        int[][] historiqueColonnes = new int[grille.length][grille[0].length];

        // Tableau pour marquer les cases déjà explorées et éviter de tourner en rond
        boolean[][] casesVisitees = new boolean[grille.length][grille[0].length];

        // File d'attente (FIFO) pour stocker les dalles à analyser (Le cœur du BFS)
        List<Integer> fileAttenteLignes = new ArrayList<>();
        List<Integer> fileAttenteColonnes = new ArrayList<>();

        // Initialisation avec la case de départ
        fileAttenteLignes.add(ligneDepart);
        fileAttenteColonnes.add(colonneDepart);
        casesVisitees[ligneDepart][colonneDepart] = true;

        // Tableaux de décalage pour tester les 4 directions : Haut, Bas, Gauche, Droite
        int[] decalageLignes = {-1, 1, 0, 0};
        int[] decalageColonnes = {0, 0, -1, 1};
        boolean cibleTrouvee = false;

        // Boucle principale d'exploration du labyrinthe
        while (!fileAttenteLignes.isEmpty() && !cibleTrouvee) {
            // On extrait la première case de la file (celle qu'on va analyser maintenant)
            int ligneActuelle = fileAttenteLignes.remove(0);
            int colonneActuelle = fileAttenteColonnes.remove(0);

            // Si la case actuelle est la destination, on arrête l'algorithme
            if (ligneActuelle == ligneArrivee && colonneActuelle == colonneArrivee) {
                cibleTrouvee = true;
            }

            // On regarde les 4 cases voisines (Haut, Bas, Gauche, Droite)
            for (int direction = 0; direction < 4 && !cibleTrouvee; direction++) {
                int ligneVoisine = ligneActuelle + decalageLignes[direction];
                int colonneVoisine = colonneActuelle + decalageColonnes[direction];

                // 1. Sécurité : On vérifie que la case voisine ne sort pas des limites du tableau
                if (ligneVoisine >= 0 && ligneVoisine < grille.length && colonneVoisine >= 0 && colonneVoisine < grille[0].length) {

                    int typeDalle = grille[ligneVoisine][colonneVoisine];

                    // 2. Conditions : Est-ce une dalle marchable ET non visitée ?
                    boolean estMarchable = (typeDalle == 1 || typeDalle == 2 || typeDalle == 3 || typeDalle == 4 || typeDalle == 5 || typeDalle == 6);
                    boolean estDejaExploree = casesVisitees[ligneVoisine][colonneVoisine];

                    if (estMarchable && !estDejaExploree) {
                        // On valide la case voisine
                        casesVisitees[ligneVoisine][colonneVoisine] = true;

                        // On note dans l'historique que c'est la "case actuelle" qui a découvert cette "case voisine"
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

    private Waypoint creerItineraireAleatoire() {
        int lEntree = -1, cEntree = -1;
        int lSortie = -1, cSortie = -1;
        int lVentil4 = -1, cVentil4 = -1;
        List<int[]> ventilations5 = new ArrayList<>();

        for (int i = 0; i < grille.length; i++) {
            for (int j = 0; j < grille[i].length; j++) {
                if (grille[i][j] == 2) { lEntree = i; cEntree = j; }
                if (grille[i][j] == 3) { lSortie = i; cSortie = j; }
                if (grille[i][j] == 4) { lVentil4 = i; cVentil4 = j; }
                if (grille[i][j] == 5) { ventilations5.add(new int[]{i, j}); }
            }
        }

        Waypoint pointDepart = new Waypoint(cEntree * tailleTuile, lEntree * tailleTuile);
        List<Waypoint> cheminComplet = new ArrayList<>();

        int choixChemin = (int) (Math.random() * 4);

        if (choixChemin == 0)
            cheminComplet = calculerCheminEntrePoints(lEntree, cEntree, lSortie, cSortie);
        else {
            List<Waypoint> versVentil = calculerCheminEntrePoints(lEntree, cEntree, lVentil4, cVentil4);
            cheminComplet.addAll(versVentil);

            int[] boucheSelectionnee = ventilations5.get(choixChemin - 1);
            int lVentil5 = boucheSelectionnee[0];
            int cVentil5 = boucheSelectionnee[1];

            cheminComplet.add(new Waypoint(cVentil5 * tailleTuile, lVentil5 * tailleTuile));

            List<Waypoint> depuisVentil = calculerCheminEntrePoints(lVentil5, cVentil5, lSortie, cSortie);
            cheminComplet.addAll(depuisVentil);
        }

        Waypoint precedent = pointDepart;
        for (Waypoint wp : cheminComplet) {
            precedent.ajouterSuivant(wp);
            precedent = wp;
        }

        return pointDepart;
    }

    private void creerMap() {
        grilleJeu.setPrefColumns(30);
        grilleJeu.setPrefRows(20);

        for (int i = 0; i < grille.length; i++) {
            for (int j = 0; j < grille[i].length; j++) {
                ImageView imageVue = new ImageView();
                imageVue.setFitHeight(tailleTuile);
                imageVue.setFitWidth(tailleTuile);

                switch (grille[i][j]) {
                    case 0: // Sol du labo
                        imageVue.setImage(new Image(getClass().getResourceAsStream("sol.png")));
                        break;
                    case 1: // Chemin des microbes
                        imageVue.setImage(new Image(getClass().getResourceAsStream("chemin.png")));
                        break;
                    case 2: // Entrée
                        imageVue.setImage(new Image(getClass().getResourceAsStream("entree.png")));
                        break;
                    case 3: // Sortie
                        imageVue.setImage(new Image(getClass().getResourceAsStream("sortie.png")));
                        break;
                    case 4: // Entrée bouche d'aération
                    case 5: // Sortie bouche d'aération
                        imageVue.setImage(new Image(getClass().getResourceAsStream("ventilation.png")));
                        break;
                    case 6: // Acide
                        imageVue.setImage(new Image(getClass().getResourceAsStream("acide.png")));
                        break;
                    case 7: // Pots De Fleur
                        imageVue.setImage(new Image(getClass().getResourceAsStream("pot_de_fleur.png")));
                        break;
                    case 8: // Plantes
                        imageVue.setImage(new Image(getClass().getResourceAsStream("plantes.png")));
                        break;
                    case 9: // Incubateur_haut_centre
                        imageVue.setImage(new Image(getClass().getResourceAsStream("incubateur_haut_centre.png")));
                        break;
                   case 10: // Incubateur_milieu_gauche
                        imageVue.setImage(new Image(getClass().getResourceAsStream("incubateur_milieu_gauche.png")));
                        break;
                    case 11: // Incubateur_milieu_centre
                        imageVue.setImage(new Image(getClass().getResourceAsStream("incubateur_milieu_centre.png")));
                        break;
                    case 12: // Incubateur_milieu_droit
                        imageVue.setImage(new Image(getClass().getResourceAsStream("incubateur_milieu_droit.png")));
                        break;
                    case 13: // Incubateur_bas_gauche
                        imageVue.setImage(new Image(getClass().getResourceAsStream("incubateur_bas_gauche.png")));
                        break;
                    case 14: // Incubateur_bas_centre
                        imageVue.setImage(new Image(getClass().getResourceAsStream("incubateur_bas_centre.png")));
                        break;
                    case 15: // Incubateur_bas_droit
                        imageVue.setImage(new Image(getClass().getResourceAsStream("incubateur_bas_droit.png")));
                        break;
                    case 16: // Bas de l'échantillon
                        imageVue.setImage(new Image(getClass().getResourceAsStream("echantillon_bas.png")));
                        break;
                    case 17: // Haut de l'échantillon
                        imageVue.setImage(new Image(getClass().getResourceAsStream("echantillon_haut.png")));
                        break;
                    case 18: // Haut gauche du frigo
                        imageVue.setImage(new Image(getClass().getResourceAsStream("frigo_haut_gauche.png")));
                        break;
                    case 19: // Haut droit du frigo
                        imageVue.setImage(new Image(getClass().getResourceAsStream("frigo_haut_droit.png")));
                        break;
                    case 20: // Bas gauche du frigo
                        imageVue.setImage(new Image(getClass().getResourceAsStream("frigo_bas_gauche.png")));
                        break;
                    case 21: // Bas droit du frigo
                        imageVue.setImage(new Image(getClass().getResourceAsStream("frigo_bas_droit.png")));
                        break;
                    case 22: // Haut gauche bureau
                        imageVue.setImage(new Image(getClass().getResourceAsStream("bureau_haut_gauche.png")));
                        break;
                    case 23: // Haut droit bureau
                        imageVue.setImage(new Image(getClass().getResourceAsStream("bureau_haut_droit.png")));
                        break;
                    case 24: // Bas gauche bureau
                        imageVue.setImage(new Image(getClass().getResourceAsStream("bureau_bas_gauche.png")));
                        break;
                    case 25: // Bas droit bureau
                        imageVue.setImage(new Image(getClass().getResourceAsStream("bureau_bas_droit.png")));
                        break;
                }
                grilleJeu.getChildren().add(imageVue);
            }
            System.out.println();
        }
    }
}