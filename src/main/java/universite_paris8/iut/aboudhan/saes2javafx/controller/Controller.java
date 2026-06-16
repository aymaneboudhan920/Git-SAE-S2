package universite_paris8.iut.aboudhan.saes2javafx.controller;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;

import java.net.URL;
import java.util.*;

import javafx.fxml.Initializable;
import javafx.util.Duration;


import universite_paris8.iut.aboudhan.saes2javafx.modele.jeu.Configuration;
import universite_paris8.iut.aboudhan.saes2javafx.modele.jeu.Environnement;
import universite_paris8.iut.aboudhan.saes2javafx.modele.microbe.Microbe;
import universite_paris8.iut.aboudhan.saes2javafx.modele.microbe.Vague;
import universite_paris8.iut.aboudhan.saes2javafx.modele.potion.PotionGel;
import universite_paris8.iut.aboudhan.saes2javafx.modele.potion.PotionRage;
import universite_paris8.iut.aboudhan.saes2javafx.modele.potion.PotionSoin;
import universite_paris8.iut.aboudhan.saes2javafx.modele.tour.*;
import universite_paris8.iut.aboudhan.saes2javafx.vue.bouton.*;
import universite_paris8.iut.aboudhan.saes2javafx.vue.ecran.DefaiteVue;
import universite_paris8.iut.aboudhan.saes2javafx.vue.ecran.TerrainVue;
import universite_paris8.iut.aboudhan.saes2javafx.vue.ecran.VagueGagneeVue;
import universite_paris8.iut.aboudhan.saes2javafx.vue.ecran.VictoireVue;
import universite_paris8.iut.aboudhan.saes2javafx.vue.microbe.GestionnaireEffets;
import universite_paris8.iut.aboudhan.saes2javafx.vue.microbe.MicrobeVue;
import universite_paris8.iut.aboudhan.saes2javafx.vue.tour.InventaireVue;
import universite_paris8.iut.aboudhan.saes2javafx.vue.tour.PanneauActionVue;
import universite_paris8.iut.aboudhan.saes2javafx.vue.tour.ProjectileVue;
import universite_paris8.iut.aboudhan.saes2javafx.vue.tour.TourVue;

public class Controller implements Initializable {
    @FXML public Pane conteneurPrincipal;
    @FXML public TilePane grilleJeu;
    @FXML public Button boutonStart;

    @FXML private Button boutonShop;
    @FXML private Button boutonTuto;
    @FXML private Button boutonParametres;
    @FXML private Button boutonInfo;

    @FXML private Label labelArgent;
    @FXML private Label labelInfectes;
    @FXML private Label labelVague;

    @FXML private Button btnPotionSoin;
    @FXML private Button btnPotionRage;
    @FXML private Button btnPotionGel;

    @FXML private Label labelPotionSoin;
    @FXML private Label labelPotionRage;
    @FXML private Label labelPotionGel;
    @FXML private Button btnPlusShop;

    @FXML private Button caseInventaire1, caseInventaire2, caseInventaire3, caseInventaire4, caseInventaire5, caseInventaire6, caseInventaire7, caseInventaire8;
    @FXML private ImageView imageInventaire1, imageInventaire2, imageInventaire3, imageInventaire4, imageInventaire5, imageInventaire6, imageInventaire7, imageInventaire8;
    @FXML private Label labelInventaire1, labelInventaire2, labelInventaire3, labelInventaire4, labelInventaire5, labelInventaire6, labelInventaire7, labelInventaire8;

    @FXML private StackPane conteneurMenuPrincipal;
    @FXML private BorderPane conteneurJeu;
    @FXML private VBox pageAccueil;
    @FXML private TextField champPseudo;
    @FXML private Label labelPseudoJoueur;

    public final Environnement env = new Environnement();
    public Inventaire inventaireModele;
    public InventaireVue inventaireVue;
    public boolean jeuDemarre = false;

    public final List<Button> boutonsInventaire = new ArrayList<>();
    private final List<ImageView> imagesInventaire = new ArrayList<>();
    private final List<Label> labelsInventaire = new ArrayList<>();

    private final java.util.Map<Microbe, MicrobeVue> vuesMicrobes = new java.util.HashMap<>();
    public final java.util.Map<Tour, TourVue> vuesTours = new java.util.HashMap<>();
    private final java.util.Map<Projectile, ProjectileVue> vuesProjectiles = new java.util.HashMap<>();

    private PotionVue potionVue;
    private AnimationTimer gameLoop;
    private Timeline timeline;
    private ShopVue shopActuel = null;

    private GestionnaireTours gestionnaireTours;
    private GestionnaireEffets gestionnaireEffets;

    private PanneauActionVue panneauActionTour;
    private Tour tourEnInspection = null;
    private int indexTourInspectee = -1;

    private Configuration configJeu;
    private ParametreVue vueParametresActive = null;
    private InfoVue vueInfoActive = null;
    private HelpVue vueHelpActive = null;
    private javafx.scene.layout.StackPane calqueRegles = null;

    private boolean soinEnCooldown = false;
    private boolean rageEnCooldown = false;
    private boolean gelEnCooldown = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> conteneurMenuPrincipal.requestFocus());
        this.configJeu = new Configuration();

        this.gestionnaireEffets = new GestionnaireEffets(conteneurPrincipal);
        TerrainVue terrainVue = new TerrainVue(env.getGrille(), env.getTailleTuile());
        terrainVue.dessinerTerrain(grilleJeu);

        // Préparer les vagues du jeu
        env.getGestionnaireVagues().initialiserVagues(env);

        // Bindings pour l'argent et le nb d'infections
        labelArgent.textProperty().bind(env.argentProperty().asString());
        labelInfectes.textProperty().bind(env.gensInfectesProperty().asString());
        labelPotionSoin.textProperty().bind(env.nbPotionSoinProperty().asString());
        labelPotionRage.textProperty().bind(env.nbPotionRageProperty().asString());
        labelPotionGel.textProperty().bind(env.nbPotionGelProperty().asString());

        // Premier rafraîchissement des compteurs et label de la vague
        updateCompteurs();
        mettreAJourLabelVague();

        // Création de la loop et de la timeline
        creerGameLoop();
        creerTimeline();

        // On rassemble les éléments de l'inventaire individuellement dans des listes
        boutonsInventaire.addAll(Arrays.asList(caseInventaire1, caseInventaire2, caseInventaire3, caseInventaire4, caseInventaire5, caseInventaire6, caseInventaire7, caseInventaire8));
        imagesInventaire.addAll(Arrays.asList(imageInventaire1, imageInventaire2, imageInventaire3, imageInventaire4, imageInventaire5, imageInventaire6, imageInventaire7, imageInventaire8));
        labelsInventaire.addAll(Arrays.asList(labelInventaire1, labelInventaire2, labelInventaire3, labelInventaire4, labelInventaire5, labelInventaire6, labelInventaire7, labelInventaire8));

        // Création des structures d'inventaire et du gestionnaire de tours
        this.inventaireModele = new Inventaire(8);
        this.inventaireVue = new InventaireVue(boutonsInventaire, imagesInventaire, labelsInventaire);
        this.gestionnaireTours = new GestionnaireTours(this.env);

        this.panneauActionTour = new PanneauActionVue();
        this.panneauActionTour.setVisible(false);
        conteneurPrincipal.getChildren().add(panneauActionTour);

        // Configuration des événements des boutons d'action
        this.panneauActionTour.getBtnAmeliorer().setOnAction(e -> gererAmelioration());
        this.panneauActionTour.getBtnRanger().setOnAction(e -> {
            if (this.panneauActionTour.getBtnRanger().getText().equals("Ranger")) {
                gererRappel();
            }
            if (this.panneauActionTour.getBtnRanger().getText().equals("Poser")) {
                if (this.indexTourInspectee != -1) {
                    String typeTour = inventaireModele.getTourCase(this.indexTourInspectee);
                    gestionnaireTours.gererClicInventaire(this.indexTourInspectee, typeTour);
                    this.panneauActionTour.setVisible(false);
                }
            }
        });

        this.panneauActionTour.getBtnVendre().setOnAction(e -> gererVente());
        this.panneauActionTour.getBtnFermer().setOnAction(e -> masquerPanneau());

        for (int i = 0; i < boutonsInventaire.size(); i++) {
            final int indexActuel = i;
            Button btn = boutonsInventaire.get(i);
            btn.setDisable(true);

            btn.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY) {
                    if (this.indexTourInspectee == indexActuel) {
                        masquerPanneau();
                        return;
                    }

                    String typeTour = inventaireModele.getTourCase(indexActuel);
                    boolean estUneTourExistante = gestionnaireTours.gererClicInventaire(indexActuel, typeTour);

                    for (Map.Entry<Tour, Integer> entree : env.getTourVersIndexInventaire().entrySet()) {
                        if (entree.getValue() == indexActuel) {
                            this.tourEnInspection = entree.getKey();
                        }
                    }
                    if (!estUneTourExistante) {
                        gestionnaireTours.annulerPlacement();

                        if (this.tourEnInspection == null && typeTour != null && !typeTour.isEmpty()) {
                            this.tourEnInspection = switch (typeTour) {
                                case "scientifique" -> new TourScientifique(0, 0);
                                case "chimiste"     -> new TourChimiste(0, 0);
                                case "scanner"      -> new TourScanner(0, 0);
                                case "rayon_x"      -> new TourRayonX(0, 0);
                                default -> null;
                            };

                            if (this.tourEnInspection != null) {
                                env.getTourVersIndexInventaire().put(this.tourEnInspection, indexActuel);
                            }
                        }
                    }

                    if (this.tourEnInspection != null) {
                        this.indexTourInspectee = indexActuel;

                        Point2D coordsScene = btn.localToScene(0, 0);
                        Point2D coordsConteneur = conteneurPrincipal.sceneToLocal(coordsScene);

                        double caseCenterX = coordsConteneur.getX() + (btn.getWidth() / 2);
                        double posY = coordsConteneur.getY();

                        this.panneauActionTour.setLayoutX(caseCenterX - (this.panneauActionTour.getBoundsInLocal().getWidth() / 2));
                        this.panneauActionTour.setLayoutY(posY - this.panneauActionTour.getHeight() - 10);
                        this.panneauActionTour.widthProperty().addListener((obs, oldWidth, newWidth) -> {
                            double largeurReelle = newWidth.doubleValue();
                            double hauteurReelle = this.panneauActionTour.getHeight();

                            double posX = caseCenterX - (largeurReelle / 2);
                            double correctedPosY = posY - hauteurReelle - 10;

                            double largeurMaxConteneur = conteneurPrincipal.getWidth();
                            if (posX + largeurReelle > largeurMaxConteneur - env.getTailleTuile()) {
                                posX = largeurMaxConteneur - largeurReelle - env.getTailleTuile();
                            }

                            this.panneauActionTour.setLayoutX(posX);
                            this.panneauActionTour.setLayoutY(correctedPosY);
                        });

                        this.panneauActionTour.actualiser(this.tourEnInspection, estUneTourExistante);

                        for (Button b : boutonsInventaire) {
                            b.getStyleClass().remove("case-inventaire-selectionnee");
                        }
                        btn.getStyleClass().add("case-inventaire-selectionnee");
                    }
                }
            });
        }

        // Si on clique n'importe où sur l'écran, le gestionnaire regarde si on veut poser une tour
        conteneurPrincipal.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                gestionnaireTours.annulerPlacement();
                masquerPanneau();
                return;
            }

            if (gestionnaireTours.estModePlacementTour() && this.tourEnInspection != null) {
                double xSurGrille = event.getX() - grilleJeu.getLayoutX();
                double ySurGrille = event.getY() - grilleJeu.getLayoutY();

                int indexBouton = gestionnaireTours.getIndexInventaireActu();
                Tour nouvelleTour = gestionnaireTours.gererClicTerrain(xSurGrille, ySurGrille);

                if (nouvelleTour != null) {
                    int tailleTuile = env.getTailleTuile();
                    int caseX = (int) (xSurGrille / tailleTuile);
                    int caseY = (int) (ySurGrille / tailleTuile);

                    double pixelX = grilleJeu.getLayoutX() + (caseX * tailleTuile);
                    double pixelY = grilleJeu.getLayoutY() + (caseY * tailleTuile);

                    nouvelleTour.setX(pixelX);
                    nouvelleTour.setY(pixelY);

                    TourVue tourVue = new TourVue(nouvelleTour.getNomImage(), pixelX, pixelY);
                    if(!jeuDemarre)
                        tourVue.cacherRadar();
                    vuesTours.put(nouvelleTour, tourVue);
                    conteneurPrincipal.getChildren().add(tourVue);

                    Button caseInventaire = boutonsInventaire.get(indexBouton);
                    caseInventaire.getStyleClass().remove("case-inventaire-selectionnee");
                    caseInventaire.getStyleClass().add("case-tour-posee");
                    caseInventaire.setDisable(false);

                    if (!jeuDemarre && boutonStart != null)
                        boutonStart.setDisable(false);
                }
            }
        });

        this.potionVue = new PotionVue();
        pageAccueil.setVisible(true);
        conteneurJeu.setVisible(false);
    }

    private void verrouillerInterface(boolean verrouiller) {
        if (boutonStart != null) boutonStart.setDisable(verrouiller || jeuDemarre);
        if (boutonShop != null) boutonShop.setDisable(verrouiller);
        if (boutonTuto != null) boutonTuto.setDisable(verrouiller);
        if (boutonParametres != null) boutonParametres.setDisable(verrouiller);
        if (boutonInfo != null) boutonInfo.setDisable(verrouiller);

        if (btnPotionSoin != null) btnPotionSoin.setDisable(verrouiller);
        if (btnPotionRage != null) btnPotionRage.setDisable(verrouiller);
        if (btnPotionGel != null) btnPotionGel.setDisable(verrouiller);
        if (btnPlusShop != null) btnPlusShop.setDisable(verrouiller);

        for (int i = 0; i < boutonsInventaire.size(); i++) {
            if (verrouiller) {
                boutonsInventaire.get(i).setDisable(true);
            } else {
                String typeTour = inventaireModele.getTourCase(i);
                boutonsInventaire.get(i).setDisable(typeTour == null);
            }
        }
    }

    public void updateCompteurs() {
        int infectes = env.getGensInfectes();
        labelInfectes.getStyleClass().removeAll("compteur-danger-faible", "compteur-danger-fort", "compteur-defaite");
        if (infectes >= 20 && infectes < 50) labelInfectes.getStyleClass().add("compteur-danger-faible");
        if (infectes >= 50 && infectes < 70) labelInfectes.getStyleClass().add("compteur-danger-fort");
        if (infectes >= 70) labelInfectes.getStyleClass().add("compteur-defaite");
    }

    @FXML
    private void actionBoutonStart() {
        if (!jeuDemarre && boutonStart != null) {
            jeuDemarre = true;

            ScaleTransition st = new ScaleTransition(Duration.millis(100), boutonStart);
            st.setToX(0.95);
            st.setToY(0.95);
            st.setAutoReverse(true);
            st.setCycleCount(2);

            st.setOnFinished(e -> {
                boutonStart.setDisable(true);

                if (configJeu != null) {
                    configJeu.changerDeMusique("musiqueJeu.wav");
                }

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

    @FXML
    private void actionBoutonShop() {
        if (shopActuel != null) {
            shopActuel.cacher(conteneurPrincipal);
            if (jeuDemarre) { gameLoop.start(); timeline.play(); }
            shopActuel = null;
            return;
        }

        if (jeuDemarre) { gameLoop.stop(); timeline.pause(); }
        verrouillerInterface(true);

        int vagueActu = env.getGestionnaireVagues().getNumVagueActu() + 1;

        shopActuel = new ShopVue(
                () -> {
                    if (shopActuel != null) {
                        shopActuel.cacher(conteneurPrincipal);
                        shopActuel = null;
                        verrouillerInterface(false);
                        if (jeuDemarre) { gameLoop.start(); timeline.play(); }
                    }
                },
                (typeItem) -> {

                    boolean scientifiqueAchete = false;
                    if (typeItem.equals("potion_soin")) {
                        if (env.getArgent() >= PotionSoin.prixAchat) {
                            env.reduireArgent(PotionSoin.prixAchat);
                            env.setNbPotionSoin(env.getNbPotionSoin() + 1);
                        }
                    } else if (typeItem.equals("potion_rage")) {
                        if (env.getArgent() >= PotionRage.prixAchat) {
                            env.reduireArgent(PotionRage.prixAchat);
                            env.setNbPotionRage(env.getNbPotionRage() + 1);
                        }
                    } else if (typeItem.equals("potion_gel")) {
                        if (env.getArgent() >= PotionGel.prixAchat) {
                            env.reduireArgent(PotionGel.prixAchat);
                            env.setNbPotionGel(env.getNbPotionGel() + 1);
                        }
                    } else {
                        int prix = switch (typeItem) {
                            case "scientifique" -> TourScientifique.prixAchat;
                            case "chimiste" -> TourChimiste.prixAchat;
                            case "scanner" -> TourScanner.prixAchat;
                            case "rayon_x" -> TourRayonX.prixAchat;
                            default -> 0;
                        };

                        int caseLibre = inventaireModele.getPremiereCaseLibre();
                        if (env.getArgent() >= prix && caseLibre != -1) {
                            env.reduireArgent(prix);
                            updateCompteurs();
                            inventaireModele.setTourCase(caseLibre, typeItem);
                            inventaireVue.installerTour(caseLibre, typeItem);
                            Tour nouvelleTourAchetee = switch (typeItem) {
                                case "scientifique" -> new TourScientifique(0, 0);
                                case "chimiste"     -> new TourChimiste(0, 0);
                                case "scanner"      -> new TourScanner(0, 0);
                                case "rayon_x"      -> new TourRayonX(0, 0);
                                default -> null;
                            };
                            if (nouvelleTourAchetee != null) {
                                env.getTourVersIndexInventaire().put(nouvelleTourAchetee, caseLibre);
                            }
                            if (typeItem.equals("scientifique")) {
                                scientifiqueAchete = true;
                            }
                        }
                    }

                    if (shopActuel != null) {
                        shopActuel.cacher(conteneurPrincipal);
                        shopActuel = null;
                        verrouillerInterface(false);
                        if (jeuDemarre) { gameLoop.start(); timeline.play(); }
                    }
                },
                vagueActu,
                TourScientifique.prixAchat, TourChimiste.prixAchat, TourScanner.prixAchat, TourRayonX.prixAchat,
                PotionSoin.prixAchat, PotionRage.prixAchat, PotionGel.prixAchat
        );
        shopActuel.afficherSur(conteneurPrincipal);
    }

    private void mettreAJourLabelVague() {
        int numActu = env.getGestionnaireVagues().getNumVagueActu() + 1;
        labelVague.setText("VAGUE " + numActu);
    }

    private void creerTimeline() {
        Vague vagueActuelle = env.getGestionnaireVagues().getVagueActuelle();
        if (vagueActuelle == null) return;

        timeline = new Timeline(
                new KeyFrame(Duration.seconds(vagueActuelle.getTempsIntervalle()), event -> {
                    if (env.isMicrobesGeles()) {
                        return;
                    }

                    List<Microbe> fileAttente = vagueActuelle.getFileAttenteMicrobes();

                    if (!fileAttente.isEmpty()) {
                        Microbe prochainMicrobe = fileAttente.remove(0);
                        env.getMicrobesActifs().add(prochainMicrobe);

                        MicrobeVue vue = new MicrobeVue(
                                prochainMicrobe.getType(),
                                prochainMicrobe.getX(),
                                prochainMicrobe.getY(),
                                prochainMicrobe.getRatioPV()
                        );
                        vuesMicrobes.put(prochainMicrobe, vue);
                        conteneurPrincipal.getChildren().add(vue);
                    } else {
                        timeline.stop();
                    }
                })
        );
        timeline.setCycleCount(Animation.INDEFINITE);
    }

    private void creerGameLoop() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // Condition de défaite
                if (env.verifierDefaite()) {
                    gameLoop.stop();
                    if (timeline != null) timeline.stop();
                    updateCompteurs();
                    verrouillerInterface(true);
                    afficherEcranDefaite();
                    return;
                }

                // Condition de victoire de la vague
                Vague vagueActuelle = env.getGestionnaireVagues().getVagueActuelle();
                if (vagueActuelle != null && vagueActuelle.getFileAttenteMicrobes().isEmpty()
                        && env.getMicrobesActifs().isEmpty() && jeuDemarre) {
                    jeuDemarre = false;
                    gameLoop.stop();
                    cacherRadarsScanners();

                    env.ajouterArgent(vagueActuelle.getBonus());
                    updateCompteurs();

                    int numVagueTerminee = env.getGestionnaireVagues().getNumVagueActu() + 1;

                    if (env.getGestionnaireVagues().estDerniereVague()) {
                        verrouillerInterface(true);
                        if (configJeu != null) {
                            configJeu.changerDeMusique("musiqueVictoireFinale.wav");
                        }
                        afficherEcranVictoire();
                    } else {
                        if (boutonStart != null) boutonStart.setDisable(true);
                        VagueGagneeVue ecranInterVague = new VagueGagneeVue(conteneurPrincipal, grilleJeu, numVagueTerminee,
                                () -> {
                                    env.getGestionnaireVagues().AugmenterVague();
                                    mettreAJourLabelVague();
                                    verrouillerInterface(false);
                                    if (boutonStart != null) boutonStart.setDisable(false);
                                });

                        verrouillerInterface(true);
                        ecranInterVague.afficherSur(conteneurPrincipal);
                        if (configJeu != null) {
                            configJeu.changerDeMusique("musiqueInterVague.wav");
                        }
                    }
                }

                // Si un microbe est sorti, l'argent/infectés s'adaptent d'eux-mêmes, on met à jour les styles
                boolean ennemiSorti = env.updateMicrobes();
                if (ennemiSorti) {
                    updateCompteurs();
                }

                double tps = 0.012;

                List<Tour> toursEnJeu = new ArrayList<>(vuesTours.keySet());
                for (Tour tour : toursEnJeu) {
                    tour.mettreAJourRecharge(tps);
                    tour.attaquer(env);
                    if (tour instanceof TourScanner) {
                        TourVue vueTour = vuesTours.get(tour);
                        if (vueTour != null) {
                            vueTour.rafraichirOndeScanner((TourScanner) tour);
                        }
                    }
                }

                env.mettreAJourProjectiles();
                rafraichirProjectiles();

                boolean unMicrobeEstMort = false;

                for (Microbe m : env.getMicrobesActifs()) {
                    if (m.estMort()) {
                        env.ajouterArgent(m.getRecompense());
                        unMicrobeEstMort = true;
                    }
                    MicrobeVue imageVue = vuesMicrobes.get(m);
                    if (imageVue != null && m.getType().equals("RAGE_ENRAGE")) {
                        imageVue.changerImage("RAGE_ENRAGE");
                    }
                    if (m.getType().equals("VIH") && m.doitAfficherEclair()) {
                        for(int i = 0; i < m.getTourAReset().size(); i++) {
                            Tour tourCible = m.getTourAReset().get(i);
                            if (tourCible != null) {
                                Controller.this.gestionnaireEffets.afficherEclairFlash(
                                        m.getX(),
                                        m.getY(),
                                        tourCible.getX(),
                                        tourCible.getY()
                                );
                            }
                        }
                        m.resetEclair();
                    }
                }

                if (unMicrobeEstMort) {
                    updateCompteurs();
                }

                env.getMicrobesActifs().removeIf(m -> m.estMort());

                List<Microbe> copiesActifs = new ArrayList<>(env.getMicrobesActifs());
                for (Microbe m : copiesActifs) {
                    MicrobeVue imageVue = vuesMicrobes.get(m);
                    if (imageVue != null)
                        imageVue.mettreAJour(m.getX(), m.getY(), m.getRatioPV());
                }

                vuesMicrobes.keySet().removeIf(m -> {
                    if (!env.getMicrobesActifs().contains(m)) {
                        MicrobeVue imageVue = vuesMicrobes.get(m);
                        if (imageVue != null) conteneurPrincipal.getChildren().remove(imageVue);
                        return true;
                    }
                    return false;
                });
            }
        };
    }

    private void cacherRadarsScanners() {
        for (Map.Entry<Tour, TourVue> entree : vuesTours.entrySet()) {
            if (entree.getKey() instanceof TourScanner) {
                TourScanner scanner = (TourScanner) entree.getKey();
                TourVue vue = entree.getValue();

                scanner.reinitialiserAttaque();
                vue.rafraichirOndeScanner(scanner);
            }
        }
    }

    private void rafraichirProjectiles() {
        for (Projectile p : env.getProjectilesActifs()) {
            if (!vuesProjectiles.containsKey(p)) {
                double tourX = 0;
                double tourY = 0;

                List<Tour> listeTours = new ArrayList<>(vuesTours.keySet());
                boolean trouve = false;

                for (int i = 0; i < listeTours.size() && !trouve; i++) {
                    Tour t = listeTours.get(i);
                    if (p.getType().equals("RAYON_X") && t.getX() == p.getX() && t.getY() == p.getY()) {
                        tourX = t.getX();
                        tourY = t.getY();
                        trouve = true;
                    }
                }

                // Création et stockage de la vue graphique associée au modèle
                ProjectileVue pVue = new ProjectileVue(p, tourX, tourY, env);
                vuesProjectiles.put(p, pVue);

                // Ajout visuel dans le conteneur principal
                conteneurPrincipal.getChildren().add(pVue);
            } else {
                // Le projectile existe déjà, on met à jour son tracé ou ses coordonnées
                vuesProjectiles.get(p).rafraichirVue(p.getX(), p.getY());
            }
        }

        List<Projectile> projectilesAEnlever = new ArrayList<>();
        for (Projectile p : vuesProjectiles.keySet()) {
            ProjectileVue vue = vuesProjectiles.get(p);
            if (vue.doitEtreRetire()) {
                projectilesAEnlever.add(p);
            }
        }

        for (Projectile p : projectilesAEnlever) {
            ProjectileVue vueRetiree = vuesProjectiles.remove(p);
            if (vueRetiree != null) {
                conteneurPrincipal.getChildren().remove(vueRetiree);
            }
        }
    }

    private void afficherEcranDefaite() {
        DefaiteVue ecranDefaite = new DefaiteVue(conteneurPrincipal, grilleJeu, () -> {
            env.reinitialiser();
            reinitialiserJeuVisuel();
        });
        ecranDefaite.afficherSur(conteneurPrincipal);
    }

    private void afficherEcranVictoire() {
        VictoireVue ecranVictoire = new VictoireVue(conteneurPrincipal, grilleJeu, () -> {
            env.reinitialiser();
            reinitialiserJeuVisuel();
            mettreAJourLabelVague();
        });
        ecranVictoire.afficherSur(conteneurPrincipal);
    }

    private void reinitialiserJeuVisuel() {
        for (MicrobeVue vueM : vuesMicrobes.values()) {
            conteneurPrincipal.getChildren().remove(vueM);
        }

        vuesMicrobes.clear();

        for (TourVue vueT : vuesTours.values()) {
            conteneurPrincipal.getChildren().remove(vueT);
        }
        vuesTours.clear();

        conteneurPrincipal.getChildren().clear();
        conteneurPrincipal.getChildren().add(grilleJeu);

        TerrainVue terrainVue = new TerrainVue(env.getGrille(), env.getTailleTuile());
        terrainVue.dessinerTerrain(grilleJeu);

        jeuDemarre = false;
        verrouillerInterface(false);

        for (int i = 0; i < boutonsInventaire.size(); i++) {
            inventaireModele.setTourCase(i, null);
            imagesInventaire.get(i).setImage(null);
            labelsInventaire.get(i).setText("");
            boutonsInventaire.get(i).setDisable(true);
            boutonsInventaire.get(i).getStyleClass().remove("case-inventaire-selectionnee");
        }

        if (configJeu != null) {
            configJeu.changerDeMusique("musiqueMenu.wav");
        }

        gestionnaireTours.reinitialiser();

        updateCompteurs();
        creerGameLoop();
    }

    @FXML
    private void actionBoutonInfo() {
        if (vueInfoActive != null) return;

        if (jeuDemarre) { gameLoop.stop(); timeline.pause(); }
        verrouillerInterface(true);

        vueInfoActive = new InfoVue(() -> {
            if (vueInfoActive != null) {
                vueInfoActive.cacher(conteneurPrincipal);
                vueInfoActive = null;
                verrouillerInterface(false);
                if (jeuDemarre) { gameLoop.start(); timeline.play(); }
            }
        });
        vueInfoActive.afficherSur(conteneurPrincipal);
    }

    @FXML
    private void actionBoutonHelp() {
        if (vueHelpActive != null) return;
        if (jeuDemarre) { gameLoop.stop(); timeline.pause(); }
        verrouillerInterface(true);

        vueHelpActive = new HelpVue(
                configJeu.getTexteTutorielCourant(),
                configJeu.estPremierePage(),
                configJeu.estDernierePage(),
                () -> {
                    configJeu.pagePrecedente();
                    vueHelpActive.rafraichirPage(configJeu.getTexteTutorielCourant(), configJeu.estPremierePage(), configJeu.estDernierePage());
                },
                () -> {
                    configJeu.pageSuivante();
                    vueHelpActive.rafraichirPage(configJeu.getTexteTutorielCourant(), configJeu.estPremierePage(), configJeu.estDernierePage());
                },
                () -> {
                    if (vueHelpActive != null) {
                        vueHelpActive.cacher(conteneurPrincipal);
                        configJeu.reinitialiserTutoriel();
                        vueHelpActive = null;
                        verrouillerInterface(false);
                        if (jeuDemarre) { gameLoop.start(); timeline.play(); }
                    }
                }
        );
        vueHelpActive.afficherSur(conteneurPrincipal);
    }


    @FXML
    private void actionBoutonParametres() {
        if (vueParametresActive != null) return;

        if (jeuDemarre) { gameLoop.stop(); timeline.pause(); }
        verrouillerInterface(true);

        vueParametresActive = new ParametreVue(
                configJeu.getVolumeMusique(),
                configJeu.getVolumeBruitages(),
                nouveauVolMusique -> configJeu.setVolumeMusique(nouveauVolMusique),
                nouveauVolBruit -> configJeu.setVolumeBruitages(nouveauVolBruit),
                () -> {
                    if (vueParametresActive != null) {
                        vueParametresActive.cacher(conteneurPrincipal);
                        vueParametresActive = null;
                        verrouillerInterface(false);
                        if (jeuDemarre) { gameLoop.start(); timeline.play(); }
                    }
                }
        );
        vueParametresActive.afficherSur(conteneurPrincipal);
    }

    @FXML
    private void AfficherParametresMenu() {
        if (vueParametresActive != null) return;

        vueParametresActive = new ParametreVue(
                configJeu.getVolumeMusique(),
                configJeu.getVolumeBruitages(),
                nouveauVolMusique -> configJeu.setVolumeMusique(nouveauVolMusique),
                nouveauVolBruit -> configJeu.setVolumeBruitages(nouveauVolBruit),
                () -> {
                    if (vueParametresActive != null) {
                        vueParametresActive.cacher(conteneurMenuPrincipal);
                        vueParametresActive = null;
                    }
                }
        );

        vueParametresActive.afficherSur(conteneurMenuPrincipal);
    }

    @FXML
    private void actionUtiliserSoin() {
        if (env.getNbPotionSoin() > 0 && !soinEnCooldown) {
            soinEnCooldown = true;
            env.setNbPotionSoin(env.getNbPotionSoin() - 1);

            PotionSoin soin = new PotionSoin();
            soin.appliquerEffet(env);

            btnPotionSoin.setDisable(true);
            potionVue.animerJaugeActive(btnPotionSoin, 0.5, () -> {
                btnPotionSoin.setDisable(false);
                soinEnCooldown = false;
            });
        }
    }

    @FXML
    private void actionUtiliserRage() {
        if (env.getNbPotionRage() > 0 && !rageEnCooldown) {
            rageEnCooldown = true;
            env.setNbPotionRage(env.getNbPotionRage() - 1);

            PotionRage rage = new PotionRage();
            rage.appliquerEffet(env);

            btnPotionRage.setDisable(true);
            potionVue.animerJaugeActive(btnPotionRage, 5.0, () -> {
                btnPotionRage.setDisable(false);
                rageEnCooldown = false;
            });
        }
    }

    @FXML
    private void actionUtiliserGel() {
        if (env.getNbPotionGel() > 0 && !gelEnCooldown) {
            gelEnCooldown = true;
            env.setNbPotionGel(env.getNbPotionGel() - 1);

            PotionGel gel = new PotionGel();
            gel.appliquerEffet(env);

            btnPotionGel.setDisable(true);

            potionVue.animerJaugeActive(btnPotionGel, 3.0, () -> {
                btnPotionGel.setDisable(false);
                gelEnCooldown = false;
            });
        }
    }

    @FXML
    private void LancerJeu() {
        // Récupération du pseudo
        String pseudoSaisi = champPseudo.getText().trim();
        if (!pseudoSaisi.isEmpty())
            labelPseudoJoueur.setText(pseudoSaisi);
        else
            // Nom donné par défaut si le joueur n'a pas entré de pseudo
            labelPseudoJoueur.setText("Joueur Anonyme");

        // On cache l'accueil et on affiche l'interface de jeu
        conteneurMenuPrincipal.setVisible(false);
        conteneurJeu.setVisible(true);
    }

    @FXML
    private void AfficherReglesMenu(ActionEvent event) {
        if (calqueRegles != null) {
            return;
        }

        // Conteneur pour isoler les règles
        calqueRegles = new StackPane();
        calqueRegles.getStyleClass().add("fond-regles-obscur");
        calqueRegles.setPrefSize(1020, 680);

        // Fenêtre centrale contenant le protocole
        VBox fenetreRegles = new VBox(25);
        fenetreRegles.getStyleClass().add("panneau-regles-terminal");
        fenetreRegles.setMaxSize(600, 500);
        fenetreRegles.setAlignment(Pos.CENTER);
        fenetreRegles.setPadding(new Insets(30));

        // Titre
        Label titre = new Label("PROTOCOLE SANITAIRE OBLIGATOIRE");
        titre.getStyleClass().add("titre-regles-neon");

        // Texte des règles de jeu
        Label contenu = new Label(
                "1. OBJECTIF : Protégez la zone en empêchant les microbes d'atteindre la sortie. Éliminez un maximum de microbes pour survivre un maximum de temps et gagner assez d'argent.\n\n" +
                        "2. TOURS DE DÉFENSE : Achetez vos unités (Scientifique, Chimiste...) dans le Shop et installez-les judicieusement sur le terrain. Vous pourrez ensuite les améliorer au fur et à mesure lorsque votre budget vous le permettra !\n\n" +
                        "3. POTIONS DE CRISE : Utilisez votre argent malignement pour utiliser les potions de Soin, de Rage ou de Gel intelligemment dans les moments les plus critiques. NE LES GASPILLEZ PAS !\n\n" +
                        "4. PROGRESSION : Soyez toujours prêt, les microbes tenteront tout pour s'échapper et chaque vague de microbes est plus redoutable que la précédente !"
        );
        contenu.getStyleClass().add("texte-regles-corps");
        contenu.setPrefWidth(540);
        contenu.setWrapText(true);

        // Bouton d'acceptation
        Button btnAccepter = new Button("COMPRIS !");
        btnAccepter.getStyleClass().add("btn-regles-action");

        // Action lors du clic sur le bouton de fermeture
        btnAccepter.setOnAction(e -> {
            // On enlève les règles
            Pane parentConteneur = (Pane) calqueRegles.getParent();
            if (parentConteneur != null) {
                parentConteneur.getChildren().remove(calqueRegles);
            }
            calqueRegles = null;
        });

        // Assemblage de la fenêtre de règles
        fenetreRegles.getChildren().addAll(titre, contenu, btnAccepter);
        calqueRegles.getChildren().add(fenetreRegles);

        Button boutonSource = (Button) event.getSource();
        Pane racineAbsolue = (Pane) boutonSource.getScene().getRoot();

        racineAbsolue.getChildren().add(calqueRegles);
    }

    private void gererAmelioration() {
        if (this.tourEnInspection != null) {
            boolean succes = env.ameliorerTour(this.tourEnInspection);
            if (succes) {
                if (tourEnInspection.getNiveau() == 5) {
                    labelsInventaire.get(indexTourInspectee).setText("LEVEL MAX");
                } else {
                    labelsInventaire.get(indexTourInspectee).setText("LEVEL " + tourEnInspection.getNiveau());
                }
                boolean estPosee = vuesTours.containsKey(this.tourEnInspection);
                this.panneauActionTour.actualiser(this.tourEnInspection, estPosee);
            }
        }
    }

    private void gererRappel() {
        if (this.tourEnInspection != null) {
            env.rappelerTour(this.tourEnInspection);
            TourVue vueAEnlever = vuesTours.get(this.tourEnInspection);
            if (vueAEnlever != null) {
                conteneurPrincipal.getChildren().remove(vueAEnlever);
                vuesTours.remove(this.tourEnInspection);
            }

            Button btn = boutonsInventaire.get(indexTourInspectee);
            btn.getStyleClass().remove("case-tour-posee");
            btn.getStyleClass().remove("case-inventaire-selectionnee");

            masquerPanneau();
        }
    }

    private void gererVente() {
        if (this.tourEnInspection != null) {
            TourVue vueAEnlever = vuesTours.get(this.tourEnInspection);
            if (vueAEnlever != null) {
                conteneurPrincipal.getChildren().remove(vueAEnlever);
                vuesTours.remove(this.tourEnInspection);
            }

            imagesInventaire.get(indexTourInspectee).setImage(null);
            labelsInventaire.get(indexTourInspectee).setText("");
            Button btn = boutonsInventaire.get(indexTourInspectee);
            btn.getStyleClass().remove("case-tour-posee");
            btn.getStyleClass().remove("case-inventaire-selectionnee");
            btn.setDisable(true);

            inventaireModele.viderCase(indexTourInspectee);
            env.vendreTour(this.tourEnInspection);
            masquerPanneau();
        }
    }

    private void masquerPanneau() {
        this.panneauActionTour.setVisible(false);
        this.tourEnInspection = null;
        this.indexTourInspectee = -1;
        for (Button b : boutonsInventaire) {
            b.getStyleClass().remove("case-inventaire-selectionnee");
        }
    }
}