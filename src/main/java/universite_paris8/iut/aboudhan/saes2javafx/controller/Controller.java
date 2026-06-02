package universite_paris8.iut.aboudhan.saes2javafx.controller;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.util.Duration;

import universite_paris8.iut.aboudhan.saes2javafx.modele.*;
import universite_paris8.iut.aboudhan.saes2javafx.vue.*;

public class Controller implements Initializable {

    @FXML private Pane conteneurPrincipal;
    @FXML private TilePane grilleJeu;
    @FXML private Label labelArgent;
    @FXML private Label labelInfectes;
    @FXML private Label labelVague;
    @FXML private Button boutonStart;

    @FXML private Button caseInventaire1;
    @FXML private ImageView imageInventaire1;
    @FXML private Label labelInventaire1;

    @FXML private Button caseInventaire2;
    @FXML private ImageView imageInventaire2;
    @FXML private Label labelInventaire2;

    @FXML private Button caseInventaire3;
    @FXML private ImageView imageInventaire3;
    @FXML private Label labelInventaire3;

    @FXML private Button caseInventaire4;
    @FXML private ImageView imageInventaire4;
    @FXML private Label labelInventaire4;

    @FXML private Button caseInventaire5;
    @FXML private ImageView imageInventaire5;
    @FXML private Label labelInventaire5;

    @FXML private Button caseInventaire6;
    @FXML private ImageView imageInventaire6;
    @FXML private Label labelInventaire6;

    @FXML private Button caseInventaire7;
    @FXML private ImageView imageInventaire7;
    @FXML private Label labelInventaire7;

    @FXML private Button caseInventaire8;
    @FXML private ImageView imageInventaire8;
    @FXML private Label labelInventaire8;

    private Environnement env = new Environnement();

    private final List<Button> boutonsInventaire = new ArrayList<>();
    private final List<ImageView> imagesInventaire = new ArrayList<>();
    private final List<Label> labelsInventaire = new ArrayList<>();

    private Inventaire inventaireModele;
    private InventaireVue inventaireVue;

    private final List<Microbe> microbesActifs = new ArrayList<>();
    private final java.util.Map<Microbe, MicrobeVue> vuesMicrobes = new java.util.HashMap<>();
    private final java.util.Map<universite_paris8.iut.aboudhan.saes2javafx.modele.Tour, TourVue> vuesTours = new java.util.HashMap<>();


    private AnimationTimer gameLoop;
    private Timeline timeline;
    private boolean jeuDemarre = false;
    private ShopVue shopActuel = null;
    private boolean modePlacementTour = false;
    private String tourAchetee = "";
    private int indexInventaireActu = -1;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // On passe la grille et la taille des tuiles de l'environnement à la vue du terrain
        TerrainVue terrainVue = new TerrainVue(env.getGrille(), env.getTailleTuile());
        terrainVue.dessinerTerrain(grilleJeu);

        // Lancement des vagues
        env.getGestionnaireVagues().initialiserVagues(env);

        // Création de la loop et de la timeline
        creerGameLoop();
        creerTimeline();
        mettreAJourLabelVague();

        labelArgent.setText(String.valueOf(env.getArgent()));
        labelInfectes.setText(String.valueOf(env.getGensInfectes()));

        boutonsInventaire.addAll(Arrays.asList(
                caseInventaire1, caseInventaire2, caseInventaire3, caseInventaire4,
                caseInventaire5, caseInventaire6, caseInventaire7, caseInventaire8
        ));

        imagesInventaire.addAll(Arrays.asList(
                imageInventaire1, imageInventaire2, imageInventaire3, imageInventaire4,
                imageInventaire5, imageInventaire6, imageInventaire7, imageInventaire8
        ));

        labelsInventaire.addAll(Arrays.asList(
                labelInventaire1, labelInventaire2, labelInventaire3, labelInventaire4,
                labelInventaire5, labelInventaire6, labelInventaire7, labelInventaire8
        ));

        // Initialisation propre du modèle et de la vue d'inventaire
        this.inventaireModele = new Inventaire(8);
        this.inventaireVue = new InventaireVue(boutonsInventaire, imagesInventaire, labelsInventaire);

        grilleJeu.setOnMouseClicked(event -> {
            if (!modePlacementTour || tourAchetee.isEmpty()) {
                return;
            }

            int tailleTuile = env.getTailleTuile();
            int caseX = (int) (event.getX() / tailleTuile);
            int caseY = (int) (event.getY() / tailleTuile);
            int[][] grille = env.getGrille();

            if (caseY >= 0 && caseY < grille.length && caseX >= 0 && caseX < grille[0].length) {

                if (grille[caseY][caseX] == 0) {

                    boolean tourPosee = false;

                    if (tourAchetee.equals("scientifique")) {
                        ajouterTourSurTerrain(caseX, caseY, 120, 15, 1.2, "/universite_paris8/iut/aboudhan/saes2javafx/vue/tour_scientifique.png");
                        tourPosee = true;
                    } else if (tourAchetee.equals("chimiste")) {
                        ajouterTourSurTerrain(caseX, caseY, 90, 25, 0.8, "/universite_paris8/iut/aboudhan/saes2javafx/vue/tour_chimiste.png");
                        tourPosee = true;
                    }

                    if (tourPosee) {

                        grille[caseY][caseX] = 99;

                        if (indexInventaireActu >= 0) {
                            inventaireVue.desactiveBoutonTour(indexInventaireActu);
                            inventaireModele.setTourCase(indexInventaireActu, null);
                        }

                        modePlacementTour = false;
                        tourAchetee = "";
                        indexInventaireActu = -1;

                        if (!jeuDemarre && boutonStart != null) {
                            boutonStart.setDisable(false);
                        }
                    }

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
    private void actionBoutonShop() {
        if (shopActuel != null) {
            shopActuel.cacher(conteneurPrincipal);
            if (jeuDemarre) {
                gameLoop.start();
                timeline.play();
            }
            shopActuel = null;
            return;
        }

        if (jeuDemarre) {
            gameLoop.stop();
            timeline.pause();
        }

        shopActuel = new ShopVue(
            () -> {
                if (shopActuel != null) {
                    shopActuel.cacher(conteneurPrincipal);
                    if (jeuDemarre) {
                        gameLoop.start();
                        timeline.play();
                    }
                    shopActuel = null;
                }
            },
            (typeItem) -> {
                // Recherche de la première case vide
                int caseLibre = inventaireModele.getPremiereCaseLibre();

                if (caseLibre == -1) {
                    System.out.println("Inventaire complet ! Posez des structures pour libérer de la place.");
                } else {
                    // Mise à jour du modèle et de la vue
                    inventaireModele.setTourCase(caseLibre, typeItem);
                    inventaireVue.installerTour(caseLibre, typeItem);

                    // Capture de l'index pour notre expression Lambda
                    final int indexActuel = caseLibre;

                    // Configuration de l'action de clic sur le bouton fourni par la vue
                    inventaireVue.getIndexInventaire(caseLibre).setOnAction(e -> {
                        this.modePlacementTour = true;
                        this.tourAchetee = typeItem;
                        this.indexInventaireActu = indexActuel; // On enregistre quelle case va poser
                    });
                }

                if (shopActuel != null) {
                    shopActuel.cacher(conteneurPrincipal);
                    if (jeuDemarre) {
                        gameLoop.start();
                        timeline.play();
                    }
                    shopActuel = null;
                }
            }
        );

        shopActuel.afficherSur(conteneurPrincipal);
    }
    private void mettreAJourLabelVague() {
        int numActu =  env.getGestionnaireVagues().getNumVagueActu() + 1;
        labelVague.setText("VAGUE " + numActu);
    }

    private void creerTimeline() {
        Vague vagueActuelle = env.getGestionnaireVagues().getVagueActuelle();
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

                    // Récupération du numéro de la vague qui vient de se terminer (Ex: Index 0 = Vague 1)
                    int numVagueTerminee = env.getGestionnaireVagues().getNumVagueActu() + 1;

                    // Vérification de la victoire finale
                    if (env.getGestionnaireVagues().estDerniereVague()) {
                        afficherEcranVictoire();
                    } else {
                        // Désactivation temporaire du bouton start pendant l'attente des 5 secondes
                        if (boutonStart != null) {
                            boutonStart.setDisable(true);
                        }

                        // Création et affichage de l'écran inter-vague modernisé
                        VagueGagneeVue ecranInterVague = new VagueGagneeVue(conteneurPrincipal, grilleJeu, numVagueTerminee,
                            () -> {
                                // Passage à la vague suivante
                                env.getGestionnaireVagues().AugmenterVague();
                                mettreAJourLabelVague();

                                // Réactivation du bouton start pour la nouvelle vague
                                if (boutonStart != null) {
                                    boutonStart.setDisable(false);
                                }
                            }
                        );

                        // Affichage immédiat de l'overlay sur toute la grille
                        ecranInterVague.afficherSur(conteneurPrincipal);
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
                    if (m.getWaypointCible() == null) {
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

            for (TourVue vueT : vuesTours.values()) {
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
            }

            creerGameLoop();
        });
        ecranDefaite.afficherSur(conteneurPrincipal);
    }

    public void afficherEcranVictoire() {
        VictoireVue ecranVictoire = new VictoireVue(conteneurPrincipal, grilleJeu, () -> {
            env = new Environnement();
            microbesActifs.clear();
            vuesMicrobes.clear();

            for (TourVue vueT : vuesTours.values()) {
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
        Tour nouvelleTour = new Tour(pixelX, pixelY, portee, degats, vitesse, nomImage);
        TourVue nouvelleTourVue = new TourVue(nouvelleTour);
        vuesTours.put(nouvelleTour, nouvelleTourVue);

        conteneurPrincipal.getChildren().add(nouvelleTourVue);
    }
}
