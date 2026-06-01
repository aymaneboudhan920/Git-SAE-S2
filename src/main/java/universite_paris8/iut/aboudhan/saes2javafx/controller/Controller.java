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
import universite_paris8.iut.aboudhan.saes2javafx.modele.Vague;
import universite_paris8.iut.aboudhan.saes2javafx.vue.*;

public class Controller implements Initializable {

    @FXML private Pane conteneurPrincipal;
    @FXML private TilePane grilleJeu;
    @FXML private Label labelArgent;
    @FXML private Label labelInfectes;
    @FXML private Label labelVague;
    @FXML private Button boutonStart;

    private Environnement env = new Environnement();

    private final List<Microbe> microbesActifs = new ArrayList<>();
    private final java.util.Map<Microbe, MicrobeVue> vuesMicrobes = new java.util.HashMap<>();
    private final java.util.Map<universite_paris8.iut.aboudhan.saes2javafx.modele.Tours, ToursVue> vuesTours = new java.util.HashMap<>();

    private AnimationTimer gameLoop;
    private Timeline timeline;
    private boolean jeuDemarre = false;

    private ShopVue shopActuel = null;
    private boolean modePlacementTour = false;
    private String typeTourEnCoursAchat = "";

    @Override
    
    public void initialize(URL location, ResourceBundle resources) {
        TerrainVue terrainVue = new TerrainVue(env.getGrille(), env.getTailleTuile());
        terrainVue.dessinerTerrain(grilleJeu);

        env.getGestionnaireVagues().initialiserVagues(env);

        creerGameLoop();
        creerTimeline();
        mettreAJourLabelVague();

        grilleJeu.setOnMouseClicked(event -> {
            if (!modePlacementTour) {
                return;
            }

            int tailleTuile = env.getTailleTuile();

            int caseX = (int) (event.getX() / tailleTuile);
            int caseY = (int) (event.getY() / tailleTuile);

            int[][] grille = env.getGrille();

            if (caseY >= 0 && caseY < grille.length && caseX >= 0 && caseX < grille[0].length) {

                if (grille[caseY][caseX] == 0) {

                    if (typeTourEnCoursAchat.equals("scientifique")) {
                        int portee = 120;
                        int degats = 15;
                        double vitesse = 1.2;
                        String image = "/universite_paris8/iut/aboudhan/saes2javafx/vue/tour_scientifique.png";

                        ajouterTourSurTerrain(caseX, caseY, portee, degats, vitesse, image);

                        grille[caseY][caseX] = 99;
                        modePlacementTour = false;
                        typeTourEnCoursAchat = "";

                        if (!jeuDemarre && boutonStart != null) {
                            boutonStart.setDisable(false);
                        }

                        actionBoutonShop(null);
                    }

                } else {
                    System.out.println("Impossible de poser une tour ici, la case ne vaut pas 0 ! (Valeur actuelle : " + grille[caseY][caseX] + ")");
                }
            }
        });
    }

    @FXML
    private void actionBoutonStart(javafx.event.ActionEvent event) {
        if (!jeuDemarre) {
            jeuDemarre = true;

            if (boutonStart != null) {
                // Animation de clic
                ScaleTransition st = new ScaleTransition(Duration.millis(100), boutonStart);
                st.setToX(0.95);
                st.setToY(0.95);
                st.setAutoReverse(true);
                st.setCycleCount(2);

                // on désactive le bouton et on lance les microbes après animation
                st.setOnFinished(e -> {
                    boutonStart.setDisable(true);

                    creerTimeline();
                    if (timeline != null) {
                        gameLoop.start();
                        timeline.play();
                    } else {
                        jeuDemarre = false;
                        boutonStart.setDisable(false);
                    }
                });
                st.play();
            }
        }
    }

    @FXML
    private void actionBoutonShop(javafx.event.ActionEvent event) {
        if (shopActuel != null) {
            shopActuel.cacherDe(conteneurPrincipal);

            // On relance les moteurs UNIQUEMENT si le jeu a déjà été démarré par le bouton START
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

        shopActuel = new ShopVue(
                () -> {
                    // PARAMÈTRE 1 (actionFermer) : S'exécute si le joueur clique sur la croix "X"
                    if (shopActuel != null) {
                        shopActuel.cacherDe(conteneurPrincipal);

                        // On ne relance que si le bouton START a déjà été cliqué au moins une fois
                        if (jeuDemarre) {
                            gameLoop.start();
                            timeline.play();
                        }

                        shopActuel = null;
                    }
                },
                () -> {
                    this.modePlacementTour = true;
                    this.typeTourEnCoursAchat = "scientifique";

                    this.shopActuel = null;

                    System.out.println("Mode placement activé : Cliquez sur une case du terrain !");
                }
        );

        shopActuel.afficherSur(conteneurPrincipal);
    }

    private void mettreAJourLabelVague() {
        int numeroActuel = env.getGestionnaireVagues().getNumVagueActu() + 1;
        labelVague.setText("VAGUE " + numeroActuel);
    }

    private void creerTimeline() {
        Vague vagueActuelle = env.getGestionnaireVagues().getVagueActuelle();

        // Sécurité : Si le modèle n'a pas de vagues chargées, on ne fait rien
        if (vagueActuelle == null) {
            return;
        }

        double intervalle = vagueActuelle.getTempsIntervalle();

        timeline = new Timeline(
                new KeyFrame(Duration.seconds(intervalle), event -> {
                    List<Microbe> fileAttente = vagueActuelle.getFileAttenteMicrobes();

                    if (!fileAttente.isEmpty()) {
                        Microbe prochainMicrobe = fileAttente.remove(0);
                        microbesActifs.add(prochainMicrobe);

                        MicrobeVue vue = new MicrobeVue(prochainMicrobe);
                        vuesMicrobes.put(prochainMicrobe, vue);
                        conteneurPrincipal.getChildren().add(vue);
                    } else {
                        timeline.stop();
                    }
                })
        );
        timeline.setCycleCount(Animation.INDEFINITE);
    }

    private void creerGameLoop(){
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (env.verifierDefaite()) {
                    gameLoop.stop();
                    if (timeline != null)
                        timeline.stop();

                    labelInfectes.getStyleClass().add("compteur-critique");
                    labelInfectes.setText(String.valueOf(env.getGensInfectes()));

                    afficherEcranDefaite();
                    return;
                }

                // Vérification de fin de vague
                Vague vagueActuelle = env.getGestionnaireVagues().getVagueActuelle();
                if (vagueActuelle != null && vagueActuelle.getFileAttenteMicrobes().isEmpty() && microbesActifs.isEmpty() && jeuDemarre) {
                    jeuDemarre = false;
                    gameLoop.stop();

                    // Distribution du bonus de fin de vague
                    env.ajouterArgent(vagueActuelle.getBonus());

                    // Vérification de la victoire finale
                    if (env.getGestionnaireVagues().estDerniereVague()) {
                        afficherEcranVictoire(); // CORRECTION : Déclenchement de l'écran de victoire
                        return;
                    } else {
                        // On passe au numéro de vague suivant
                        env.getGestionnaireVagues().AugmenterVague();
                        mettreAJourLabelVague(); // CORRECTION : Met à jour l'UI (ex: VAGUE 2)

                        if (boutonStart != null) {
                            boutonStart.setDisable(false);
                            boutonStart.setText("Lancer Vague " + (env.getGestionnaireVagues().getNumVagueActu() + 1));
                        }
                    }
                }

                int[][] grille = env.getGrille();
                int tailleTuile = env.getTailleTuile();

                // Compteurs dynamiques
                labelArgent.setText(String.valueOf(env.getArgent()));
                labelInfectes.setText(String.valueOf(env.getGensInfectes()));

                changerCouleurCompteur(env.getGensInfectes());

                // Parcourir tous les microbes actifs
                for (int i = microbesActifs.size() - 1; i >= 0; i--) {
                    Microbe m = microbesActifs.get(i);
                    int caseJ = (int) (m.getX() / tailleTuile);
                    int caseI = (int) (m.getY() / tailleTuile);

                    if (caseI >= 0 && caseI < grille.length && caseJ >= 0 && caseJ < grille[0].length) {
                        m.appliquerRalentissement(grille[caseI][caseJ] == 6);
                    }
                    m.deplacer();

                    MicrobeVue imageVue = vuesMicrobes.get(m);
                    if (imageVue != null) {
                        imageVue.mettreAJourPosition();
                    }

                    if (m.getWaypointCible() == null) {
                        env.incrementerInfectes(m);
                        if (imageVue != null) {
                            conteneurPrincipal.getChildren().remove(imageVue);
                        }
                        vuesMicrobes.remove(m);
                        microbesActifs.remove(i);
                    }
//                  if (env.verifierVictoire()) {
//                  gameLoop.stop();
//                  timeline.stop();
//
//                  afficherEcranVictoire();
//                  return;
//              }
                }
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

            for (ToursVue vueT : vuesTours.values()) {
                conteneurPrincipal.getChildren().remove(vueT);
            }
            vuesTours.clear();

            conteneurPrincipal.getChildren().clear();
            conteneurPrincipal.getChildren().add(grilleJeu);

            TerrainVue terrainVue = new TerrainVue(env.getGrille(), env.getTailleTuile());
            terrainVue.dessinerTerrain(grilleJeu);

            labelInfectes.getStyleClass().remove("compteur-defaite");

            // Réinitialisation complète du gestionnaire de vagues
            env.getGestionnaireVagues().initialiserVagues(env);
            jeuDemarre = false;

            if (boutonStart != null) {
                boutonStart.setDisable(false);
                boutonStart.setText("Lancer Vague 1");
            }
            mettreAJourLabelVague();
            creerGameLoop();
        });
        ecranDefaite.afficherSur(conteneurPrincipal);
    }

    public void afficherEcranVictoire() {
        VictoireVue ecranVictoire = new VictoireVue(conteneurPrincipal, grilleJeu, () -> {
            env = new Environnement();
            microbesActifs.clear();
            vuesMicrobes.clear();

            for (ToursVue vueT : vuesTours.values()) {
                conteneurPrincipal.getChildren().remove(vueT);
            }
            vuesTours.clear();

            env.getGestionnaireVagues().listeVagues.clear();
            env.getGestionnaireVagues().numVagueActu = 0;

            conteneurPrincipal.getChildren().clear();
            conteneurPrincipal.getChildren().add(grilleJeu);

            TerrainVue terrainVue = new TerrainVue(env.getGrille(), env.getTailleTuile());
            terrainVue.dessinerTerrain(grilleJeu);

            labelInfectes.getStyleClass().remove("compteur-critique");

            env.getGestionnaireVagues().initialiserVagues(env);
            jeuDemarre = false;

            if (boutonStart != null) {
                boutonStart.setDisable(false);
                boutonStart.setText("Lancer Vague 1");
            }

            mettreAJourLabelVague();
            creerGameLoop();
        });
        ecranVictoire.afficherSur(conteneurPrincipal);
    }

    public void ajouterTourSurTerrain(int caseX, int caseY, int portee, int degats, double vitesse, String nomImage) {
        int tailleTuile = env.getTailleTuile();
        double pixelX = caseX * tailleTuile;
        double pixelY = caseY * tailleTuile;
        universite_paris8.iut.aboudhan.saes2javafx.modele.Tours nouvelleTour =
                new universite_paris8.iut.aboudhan.saes2javafx.modele.Tours(pixelX, pixelY, portee, degats, vitesse, nomImage);
        ToursVue nouvelleTourVue = new ToursVue(nouvelleTour);
        vuesTours.put(nouvelleTour, nouvelleTourVue);

        conteneurPrincipal.getChildren().add(nouvelleTourVue);

        System.out.println("Tour posée avec succès en case [" + caseX + ", " + caseY + "]");
    }
}
