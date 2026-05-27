package universite_paris8.iut.aboudhan.saes2javafx.controller;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.util.Duration;

import universite_paris8.iut.aboudhan.saes2javafx.modele.Microbe;
import universite_paris8.iut.aboudhan.saes2javafx.modele.Environnement;
import universite_paris8.iut.aboudhan.saes2javafx.vue.DefaiteVue;
import universite_paris8.iut.aboudhan.saes2javafx.vue.TerrainVue;
import universite_paris8.iut.aboudhan.saes2javafx.vue.MicrobeVue;

public class Controller implements Initializable {

    @FXML private Pane conteneurPrincipal;
    @FXML private TilePane grilleJeu;
    @FXML private Label labelArgent;
    @FXML private Label labelInfectes;

    // Le contrôleur instancie son modèle global : l'environnement
    private Environnement env = new Environnement();

    private final List<Microbe> microbesActifs = new ArrayList<>();
    private final java.util.Map<Microbe, MicrobeVue> vuesMicrobes = new java.util.HashMap<>();

    private AnimationTimer gameLoop;
    private Timeline timeline;
    private final List<Microbe> fileAttenteMicrobes = new ArrayList<>();
    private boolean jeuDemarre = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // On passe la grille et la taille des tuiles de l'environnement à la vue du terrain
        TerrainVue terrainVue = new TerrainVue(env.getGrille(), env.getTailleTuile());
        terrainVue.dessinerTerrain(grilleJeu);

        // On ajoute les microbes à la file
        remplirFileAttente();

        // Création de la loop et de la timeline
        creerGameLoop();
        creerTimeline();
    }

    private void remplirFileAttente() {
        String prefixe = "/universite_paris8/iut/aboudhan/saes2javafx/vue/";

        // C'est maintenant l'environnement qui génère l'itinéraire de chaque microbe
        fileAttenteMicrobes.add(new Microbe(0.8, 30, 2, 1,prefixe + "rhinovirus.png", env.creerItineraireAleatoire()));
        fileAttenteMicrobes.add(new Microbe(1, 40, 3, 1, prefixe + "norovirus.png", env.creerItineraireAleatoire()));
        fileAttenteMicrobes.add(new Microbe(0.9, 60, 5, 2, prefixe + "streptocoque.png", env.creerItineraireAleatoire()));
        fileAttenteMicrobes.add(new Microbe(0.7, 10, 10, 3, prefixe + "influenza.png", env.creerItineraireAleatoire()));
        fileAttenteMicrobes.add(new Microbe(1, 120, 15, 5, prefixe + "varicelle.png", env.creerItineraireAleatoire()));
        fileAttenteMicrobes.add(new Microbe(0.8, 200, 20, 8, prefixe + "covid.png", env.creerItineraireAleatoire()));
        fileAttenteMicrobes.add(new Microbe(0.6, 250, 30, 10, prefixe + "vih.png", env.creerItineraireAleatoire()));
        fileAttenteMicrobes.add(new Microbe(0.5, 600, 40, 12, prefixe + "tuberculose.png", env.creerItineraireAleatoire()));
        fileAttenteMicrobes.add(new Microbe(0.8, 450, 50, 20, prefixe + "peste.png", env.creerItineraireAleatoire()));
        fileAttenteMicrobes.add(new Microbe(1, 300, 70, 15, prefixe + "rage.png", env.creerItineraireAleatoire()));
        fileAttenteMicrobes.add(new Microbe(0.3, 2000, 100, 50, prefixe + "variole.png", env.creerItineraireAleatoire()));
    }

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
            timeline.play();
        }
    }

    @FXML
    private void actionBoutonShop(javafx.event.ActionEvent event) {
        if (shopActuel != null) {
            shopActuel.cacherDe(conteneurPrincipal);

            //On relance les moteurs UNIQUEMENT si le jeu a déjà été démarré par le bouton START
            if (jeuDemarre) {
                gameLoop.start();
                timeline.play();
            }

            shopActuel = null;
            return;
        }

        // On met en pause uniquement si le jeu est en train de tourner
        if (jeuDemarre) {
            gameLoop.stop();
            timeline.pause();
        }

        // On crée l'interface du shop
        shopActuel = new ShopVue(() -> {
            // Ce code s'exécute si le joueur clique sur la croix "X"
            if (shopActuel != null) {
                shopActuel.cacherDe(conteneurPrincipal);

                // On ne relance que si le bouton START a déjà été cliqué au moins une fois
                if (jeuDemarre) {
                    gameLoop.start();
                    timeline.play();
                }

                shopActuel = null;
            }
        });

        shopActuel.afficherSur(conteneurPrincipal);
    }

    private void creerTimeline() {
        timeline = new Timeline(
                // Répeter toutes les 3 secondes
                new KeyFrame(Duration.seconds(3), event -> {
                    // Si il reste encore des microbes à ajouter dans la map
                    if (!fileAttenteMicrobes.isEmpty()) {
                        // Prendre le premier microbe de la file d'attente et l'ajouter dans la liste des microbes actifs
                        Microbe prochainMicrobe = fileAttenteMicrobes.remove(0);
                        microbesActifs.add(prochainMicrobe);

                        MicrobeVue vue = new MicrobeVue(prochainMicrobe);
                        // Ajouter le microbe et son image correspondante au dictionnaire
                        vuesMicrobes.put(prochainMicrobe, vue);
                        conteneurPrincipal.getChildren().add(vue);
                    } else {
                        // Si la file d'attente est vide, on arrête la Timeline
                        timeline.stop();
                    }
                })
        );
        // Répéter en boucle tant qu'on ne l'arrête pas
        timeline.setCycleCount(Animation.INDEFINITE);
    }

    private void creerGameLoop(){
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
            int[][] grille = env.getGrille();
            int tailleTuile = env.getTailleTuile();

            // Compteurs dynamiques
            labelArgent.setText(String.valueOf(env.getArgent()));
            labelInfectes.setText(String.valueOf(env.getGensInfectes()));

            changerCouleurCompteur(env.getGensInfectes());
            // Parcourir tous les microbes actifs
            for (int i = microbesActifs.size() - 1; i >= 0; i--){
                Microbe m = microbesActifs.get(i);
                // Convertir les pixels en indices de cases
                int caseJ = (int) (m.getX() / tailleTuile);
                int caseI = (int) (m.getY() / tailleTuile);

                // Vérifier que les indices ne dépassent pas les dimensions de la grille de jeu
                if (caseI >= 0 && caseI < grille.length && caseJ >= 0 && caseJ < grille[0].length) {
                    // Ralentir le microbe si le microbe se trouve sur une case n°6 (acide ralentissant)
                    m.appliquerRalentissement(grille[caseI][caseJ] == 6);
                }
                // Mettre à jour les coordonnées du microbe après déplacement
                m.deplacer();

                // Récupérer l'image correspondante au microbe
                MicrobeVue imageVue = vuesMicrobes.get(m);
                // Si l'image existe
                if (imageVue != null) {
                    // Mettre à jour les coordonnées de l'image
                    imageVue.mettreAJourPosition();
                }

                // Si le microbe a atteint la sortie
                if(m.getWaypointCible() == null){
                    // Mettre à jour le compteur d'infections
                    env.incrementerInfectes(m);
                    // Retirer l'image si elle existe
                    if (imageVue != null) {
                        conteneurPrincipal.getChildren().remove(imageVue);
                    }
                    // Supprimer le microbe du dictionnaire ainsi que la liste des microbes actifs
                    vuesMicrobes.remove(m);
                    microbesActifs.remove(i);
                }

                if (env.verifierDefaite()) {
                    gameLoop.stop();
                    timeline.stop();

                    labelInfectes.getStyleClass().add("compteur-critique");
                    labelInfectes.setText(String.valueOf(env.getGensInfectes()));

                    afficherEcranDefaite();
                    return;
                }

//              if (env.verifierVictoire()) {
//                  gameLoop.stop();
//                  timeline.stop();
//
//                  afficherEcranVictoire();
//                  return;
//              }
            }
        };
    }

    public void changerCouleurCompteur(int nbInfectes){
        labelInfectes.getStyleClass().removeAll("compteur-danger-faible", "compteur-danger-fort", "compteur-defaite");
        if(nbInfectes >= 20 && nbInfectes < 50)
            labelInfectes.getStyleClass().add("compteur-danger-faible");
        if(nbInfectes >= 50 && nbInfectes < 70)
            labelInfectes.getStyleClass().add("compteur-danger-fort");
        if(nbInfectes >= 70)
            labelInfectes.getStyleClass().add("compteur-defaite");
    }

    private void afficherEcranDefaite() {
        DefaiteVue ecranDefaite = new DefaiteVue(conteneurPrincipal, grilleJeu, () -> {

            env = new Environnement();
            microbesActifs.clear();
            vuesMicrobes.clear();
            fileAttenteMicrobes.clear();

            conteneurPrincipal.getChildren().clear();
            conteneurPrincipal.getChildren().add(grilleJeu);

            TerrainVue terrainVue = new TerrainVue(env.getGrille(), env.getTailleTuile());
            terrainVue.dessinerTerrain(grilleJeu);

            labelInfectes.getStyleClass().remove("compteur-defaite");

            remplirFileAttente();
            gameLoop.start();
            timeline.play();
        });

        ecranDefaite.afficherSur(conteneurPrincipal);
    }

    public void afficherEcranVictoire() {
        VictoireVue ecranVictoire = new VictoireVue(conteneurPrincipal, grilleJeu, () -> {
            env = new Environnement();
            microbesActifs.clear();
            vuesMicrobes.clear();
            fileAttenteMicrobes.clear();

            conteneurPrincipal.getChildren().clear();
            conteneurPrincipal.getChildren().add(grilleJeu);

            TerrainVue terrainVue = new TerrainVue(env.getGrille(), env.getTailleTuile());
            terrainVue.dessinerTerrain(grilleJeu);

            labelInfectes.getStyleClass().remove("compteur-critique");

            remplirFileAttente();
            gameLoop.start();
            timeline.play();
        });
        ecranVictoire.afficherSur(conteneurPrincipal);
    }
}
