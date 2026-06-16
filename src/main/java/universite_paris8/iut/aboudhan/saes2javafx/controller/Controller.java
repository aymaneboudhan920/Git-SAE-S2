package universite_paris8.iut.aboudhan.saes2javafx.controller;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import universite_paris8.iut.aboudhan.saes2javafx.modele.IControleurTuto;
import universite_paris8.iut.aboudhan.saes2javafx.modele.DidactitielVisuel;
import universite_paris8.iut.aboudhan.saes2javafx.modele.jeu.Configuration;
import universite_paris8.iut.aboudhan.saes2javafx.modele.jeu.Environnement;
import universite_paris8.iut.aboudhan.saes2javafx.modele.microbe.Microbe;
import universite_paris8.iut.aboudhan.saes2javafx.modele.microbe.Vague;
import universite_paris8.iut.aboudhan.saes2javafx.modele.potion.PotionGel;
import universite_paris8.iut.aboudhan.saes2javafx.modele.potion.PotionRage;
import universite_paris8.iut.aboudhan.saes2javafx.modele.potion.PotionSoin;
import universite_paris8.iut.aboudhan.saes2javafx.modele.tour.*;
import universite_paris8.iut.aboudhan.saes2javafx.vue.*;
import universite_paris8.iut.aboudhan.saes2javafx.vue.bouton.*;
import universite_paris8.iut.aboudhan.saes2javafx.vue.ecran.DefaiteVue;
import universite_paris8.iut.aboudhan.saes2javafx.vue.ecran.TerrainVue;
import universite_paris8.iut.aboudhan.saes2javafx.vue.ecran.VagueGagneeVue;
import universite_paris8.iut.aboudhan.saes2javafx.vue.ecran.VictoireVue;
import universite_paris8.iut.aboudhan.saes2javafx.vue.tour.InventaireVue;
import universite_paris8.iut.aboudhan.saes2javafx.vue.tour.ProjectileVue;
import universite_paris8.iut.aboudhan.saes2javafx.vue.tour.TourVue;

public class Controller implements Initializable {
    @FXML public Pane conteneurPrincipal;
    @FXML public TilePane grilleJeu;
    @FXML public Button boutonStart;

    @FXML public Button boutonShop;
    @FXML public Button boutonTuto;
    @FXML public Button boutonParametres;
    @FXML public Button boutonInfo;

    @FXML private Label labelArgent;
    @FXML private Label labelInfectes;
    @FXML private Label labelVague;

    @FXML public Button btnPotionSoin;
    @FXML public Button btnPotionRage;
    @FXML public Button btnPotionGel;

    @FXML private Label labelPotionSoin;
    @FXML private Label labelPotionRage;
    @FXML private Label labelPotionGel;
    @FXML private Button btnPlusShop;

    @FXML private Button caseInventaire1, caseInventaire2, caseInventaire3, caseInventaire4, caseInventaire5, caseInventaire6, caseInventaire7, caseInventaire8;
    @FXML private ImageView imageInventaire1, imageInventaire2, imageInventaire3, imageInventaire4, imageInventaire5, imageInventaire6, imageInventaire7, imageInventaire8;
    @FXML private Label labelInventaire1, labelInventaire2, labelInventaire3, labelInventaire4, labelInventaire5, labelInventaire6, labelInventaire7, labelInventaire8;

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
    private DidactitielVisuel didacticielActif;

    private AnimationTimer gameLoop;
    private Timeline timeline;
    private ShopVue shopActuel = null;

    private GestionnaireTours gestionnaireTours;

    private Configuration configJeu;
    private ParametreVue vueParametresActive = null;
    private TutorielVue vueTutorielActive = null;
    private InfoVue vueInfoActive = null;
    private javafx.scene.layout.StackPane calqueRegles = null;

    private boolean soinEnCooldown = false;
    private boolean rageEnCooldown = false;
    private boolean gelEnCooldown = false;
    // Variables pour gérer l'interception du premier microbe pendant le tuto
    private boolean premierMicrobeTutoMontre = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.configJeu = new Configuration();

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

        // Configuration des boutons de l'inventaire
        for (int i = 0; i < boutonsInventaire.size(); i++) {
            final int indexActuel = i;
            Button btn = boutonsInventaire.get(i);
            btn.setDisable(true);
            labelsInventaire.get(i).setText("");

            btn.setOnMouseClicked(event -> {
                String typeTour = inventaireModele.getTourCase(indexActuel);

                if (typeTour == null || typeTour.isEmpty()) {
                    return;
                }

                // CLIC DROIT -> Ranger la tour posée sur le terrain dans l'inventaire
                if (event.getButton() == MouseButton.SECONDARY) {

                    // On cherche d'abord s'il y a une tour associée à cet index dans le modèle avant qu'elle soit supprimée
                    Tour tourAEnlever = null;
                    for (java.util.Map.Entry<Tour, Integer> assoc : env.getTourVersIndexInventaire().entrySet()) {
                        if (assoc.getValue() == indexActuel) {
                            tourAEnlever = assoc.getKey();
                            break;
                        }
                    }

                    // On appelle le gestionnaire pour mettre à jour le modèle
                    boolean tourRappelee = gestionnaireTours.gererClicInventaire(indexActuel, typeTour);

                    if (tourRappelee || tourAEnlever != null) {
                        final Tour tourFinale = tourAEnlever;

                        // Supprime visuellement la vue de la tour du terrain JavaFX
                        vuesTours.entrySet().removeIf(association -> {
                            // On compare soit par l'instance de la tour trouvée, soit par l'index enregistré dans l'env
                            if (association.getKey() == tourFinale ||
                                    (env.getTourVersIndexInventaire().containsKey(association.getKey()) &&
                                            env.getTourVersIndexInventaire().get(association.getKey()) == indexActuel)) {

                                conteneurPrincipal.getChildren().remove(association.getValue());
                                return true; // Supprime de la map vuesTours
                            }
                            return false;
                        });

                        // Nettoyage complet des styles CSS sur le bouton d'inventaire
                        while (btn.getStyleClass().contains("case-tour-posee")) {
                            btn.getStyleClass().remove("case-tour-posee");
                        }
                        while (btn.getStyleClass().contains("case-inventaire-selectionnee")) {
                            btn.getStyleClass().remove("case-inventaire-selectionnee");
                        }

                        // Force la vue à réinstaller proprement l'image de la tour dans l'inventaire
                        inventaireVue.installerTour(indexActuel, typeTour);
                        btn.setDisable(false);

                        // On annule tout état de placement résiduel pour repartir sur de bonnes bases
                        gestionnaireTours.annulerPlacement();
                    }
                }

                // CLIC GAUCHE -> Sélectionner la tour pour la poser ou la REPOSER
                else if (event.getButton() == MouseButton.PRIMARY) {

                    // On ne peut la sélectionner au clic gauche QUE si elle n'est pas déjà sur la map
                    if (!btn.getStyleClass().contains("case-tour-posee")) {

                        // Sécurité : on annule d'abord tout placement précédent
                        gestionnaireTours.annulerPlacement();

                        // On active le mode placement
                        gestionnaireTours.gererClicInventaire(indexActuel, typeTour);

                        // Gestion visuelle de la sélection (bordure jaune)
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
                for (Button b : boutonsInventaire)
                    b.getStyleClass().remove("case-inventaire-selectionnee");
                return;
            }

            double xSurGrille = event.getX() - grilleJeu.getLayoutX();
            double ySurGrille = event.getY() - grilleJeu.getLayoutY();

            // On demande au modèle s'il crée une tour suite à ce clic
            int indexBouton = gestionnaireTours.getIndexInventaireActu();
            Tour nouvelleTour = gestionnaireTours.gererClicTerrain(xSurGrille, ySurGrille);

            // Dans ton Controller.java (dans l'événement setOnMouseClicked de conteneurPrincipal) :
            if (nouvelleTour != null) {
                int tailleTuile = env.getTailleTuile();
                int caseX = (int) (xSurGrille / tailleTuile);
                int caseY = (int) (ySurGrille / tailleTuile);

                double pixelX = grilleJeu.getLayoutX() + (caseX * tailleTuile);
                double pixelY = grilleJeu.getLayoutY() + (caseY * tailleTuile);

                nouvelleTour.setX(pixelX);
                nouvelleTour.setY(pixelY);

                TourVue tourVue = new TourVue(nouvelleTour.getNomImage(), pixelX, pixelY);
                vuesTours.put(nouvelleTour, tourVue);
                conteneurPrincipal.getChildren().add(tourVue);

                Button caseInventaire = boutonsInventaire.get(indexBouton);
                caseInventaire.getStyleClass().remove("case-inventaire-selectionnee");
                caseInventaire.getStyleClass().add("case-tour-posee");
                caseInventaire.setDisable(false);

                if (!jeuDemarre && boutonStart != null)
                    boutonStart.setDisable(false);

                if (this.didacticielActif != null) {
                    this.didacticielActif.etapeDidcatitielTermine();
                }
            }
        });
        this.potionVue = new PotionVue(grilleJeu);
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

        // Gestion de l'inventaire
        for (int i = 0; i < boutonsInventaire.size(); i++) {
            if (verrouiller) {
                boutonsInventaire.get(i).setDisable(true);
            } else {
                // Au déverrouillage, on ne réactive que les cases qui possèdent une tour achetée
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
                    
                    if (this.didacticielActif != null) {
                        // Le jeu se fige pour laisser lire
                        timeline.pause();

                        this.didacticielActif.etapeMontrerEntree(env.creerItineraireAleatoire(), this);
                    }
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
        
        // Récupération de la vague actuelle
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
                    // variable pour savoir si le joueur vient d'acheter le scientifique
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

                            // Si c'est le scientifique, on passe le flag à true
                            if (typeItem.equals("scientifique")) {
                                scientifiqueAchete = true;
                            }
                        }
                    }

                    if (shopActuel != null) {
                        shopActuel.cacher(conteneurPrincipal);
                        shopActuel = null;
                        verrouillerInterface(false);
                        if (jeuDemarre && this.didacticielActif == null) { gameLoop.start(); timeline.play(); }
                    }
                    if (scientifiqueAchete && this.didacticielActif != null) {
                        javafx.application.Platform.runLater(() -> {
                            // On récupère physiquement la première case de ton inventaire pour l'illuminer
                            javafx.scene.Node premiereCase = boutonsInventaire.get(0);
                            this.didacticielActif.etapeMontrerInventaire(premiereCase);
                        });
                    }
                },
                vagueActu,
                TourScientifique.prixAchat, TourChimiste.prixAchat, TourScanner.prixAchat, TourRayonX.prixAchat,
                PotionSoin.prixAchat, PotionRage.prixAchat, PotionGel.prixAchat
        );
        shopActuel.afficherSur(conteneurPrincipal);

        // OUVERTURE DE LA BOUTIQUE
        if (this.didacticielActif != null) {
            // On laisse un mini-instant à JavaFX pour ajouter et positionner les éléments de la boutique
            javafx.application.Platform.runLater(() -> {
                // On cherche le bouton ou la case du Scientifique dans le conteneur principal.
                javafx.scene.Node boutonScientifique = conteneurPrincipal.lookup(".btn-item-shop");

                if (boutonScientifique != null) {
                    // On envoie le composant graphique trouvé à l'étape 6 du tutoriel
                    this.didacticielActif.etapeMontrerScientifiqueDansShop(boutonScientifique);
                }
            });
        }
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
                                prochainMicrobe.getNomImage(),
                                prochainMicrobe.getX(),
                                prochainMicrobe.getY(),
                                prochainMicrobe.getRatioPV()
                        );
                        vuesMicrobes.put(prochainMicrobe, vue);
                        conteneurPrincipal.getChildren().add(vue);
                        
                        if (this.didacticielActif != null && !premierMicrobeTutoMontre) {
                            premierMicrobeTutoMontre = true;

                            // On fige immédiatement le jeu à l'instant T du spawn
                            if (timeline != null) timeline.pause();
                            if (gameLoop != null) gameLoop.stop();

                            // On laisse à JavaFX un mini instant pour placer le nœud, puis on l'illumine
                            javafx.application.Platform.runLater(() -> {
                                this.didacticielActif.etapeMontrerPremierMicrobe(vue);
                            });
                        }

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
                    afficherEcranDefaite();
                    return;
                }

                // Condition de victoire de la vague
                Vague vagueActuelle = env.getGestionnaireVagues().getVagueActuelle();
                if (vagueActuelle != null && vagueActuelle.getFileAttenteMicrobes().isEmpty()
                        && env.getMicrobesActifs().isEmpty() && jeuDemarre) {
                    jeuDemarre = false;
                    gameLoop.stop();

                    env.ajouterArgent(vagueActuelle.getBonus());
                    updateCompteurs();

                    int numVagueTerminee = env.getGestionnaireVagues().getNumVagueActu() + 1;

                    if (env.getGestionnaireVagues().estDerniereVague()) {
                        afficherEcranVictoire();
                    } else {
                        if (boutonStart != null) boutonStart.setDisable(true);
                        VagueGagneeVue ecranInterVague = new VagueGagneeVue(conteneurPrincipal, grilleJeu, numVagueTerminee,
                                () -> {
                                    env.getGestionnaireVagues().AugmenterVague();
                                    mettreAJourLabelVague();
                                    if (boutonStart != null) boutonStart.setDisable(false);
                                });
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
                }

                env.mettreAJourProjectiles();
                rafraichirProjectiles();

                boolean unMicrobeEstMort = false;

                for (Microbe m : env.getMicrobesActifs()) {
                    if (m.estMort()) {
                        env.ajouterArgent(m.getRecompense());
                        unMicrobeEstMort = true;
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
        boutonStart.setDisable(false);
        verrouillerInterface(false);
        premierMicrobeTutoMontre = false;

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

    public void relancerJeuTemporairementTuto() {
        if (timeline != null) {
            timeline.play();
        }
        if (gameLoop != null) {
            gameLoop.start();
        }
    }

    public void pauserJeuTuto() {
        if (timeline != null) {
            timeline.pause();
        }
        if (gameLoop != null) {
            gameLoop.stop();
        }
    }

    public void arreterJeuApresTuto() {
        if (timeline != null) {
            timeline.stop();
        }
        if (gameLoop != null) {
            gameLoop.stop();
        }

        // Nettoyage des résidus graphiques des microbes et des tours créés pendant le tuto
        for (MicrobeVue vueMicrobe : vuesMicrobes.values()) {
            conteneurPrincipal.getChildren().remove(vueMicrobe);
        }
        vuesMicrobes.clear();

        for (TourVue vueTour : vuesTours.values()) {
            conteneurPrincipal.getChildren().remove(vueTour);
        }
        vuesTours.clear();

        // Ton code d'origine pour vider l'inventaire du modèle
        if (this.inventaireModele != null) {
            for (int i = 0; i < inventaireModele.getCaseInventaire().length; i++) {
                inventaireModele.getCaseInventaire()[i] = null;
            }
        }

        // On nettoie les styles CSS de sélection et on force l'opacité à 1.0 (enlève la transparence bugguée)
        for (Button btn : boutonsInventaire) {
            btn.getStyleClass().removeAll("case-tour-posee", "case-inventaire-selectionnee");
            btn.setDisable(true);
            btn.setOpacity(1.0);
        }

        // Reset des variables d'état du contrôleur pour que le 2ème tuto reparte à zéro
        this.jeuDemarre = false;
        this.didacticielActif = null;
        this.premierMicrobeTutoMontre = false;

        // Tes deux appels d'origine pour remettre l'environnement et la vue à neuf
        env.reinitialiser();
        reinitialiserJeuVisuel();
    }

    @FXML
    private void actionBoutonInfo() {
        if (vueInfoActive != null) return;
        if (jeuDemarre) { gameLoop.stop(); timeline.pause(); }
        verrouillerInterface(true);

        vueInfoActive = new InfoVue(
                configJeu.getTexteTutorielCourant(),
                configJeu.estPremierePage(),
                configJeu.estDernierePage(),
                () -> {
                    configJeu.pagePrecedente();
                    vueInfoActive.rafraichirPage(configJeu.getTexteTutorielCourant(), configJeu.estPremierePage(), configJeu.estDernierePage());
                },
                () -> {
                    configJeu.pageSuivante();
                    vueInfoActive.rafraichirPage(configJeu.getTexteTutorielCourant(), configJeu.estPremierePage(), configJeu.estDernierePage());
                },
                () -> {
                    if (vueInfoActive != null) {
                        vueInfoActive.cacher(conteneurPrincipal);
                        configJeu.reinitialiserTutoriel();
                        vueInfoActive = null;
                        verrouillerInterface(false);
                        if (jeuDemarre) { gameLoop.start(); timeline.play(); }
                    }
                }
        );
        vueInfoActive.afficherSur(conteneurPrincipal);
    }

    @FXML
    private void actionBoutonTuto() {
        if (boutonTuto.getScene() == null) return;

        // Mettre le jeu en pause le temps que le joueur réponde OUI ou NON
        if (jeuDemarre) {
            if (gameLoop != null) gameLoop.stop();
            if (timeline != null) timeline.pause();
        }

        Pane racine = (Pane) boutonTuto.getScene().getRoot();
        // On crée juste la popup — didacticielActif reste null jusqu'au clic OUI
        DidactitielVisuel popup = new DidactitielVisuel(racine, this.grilleJeu);
        popup.demarrerSiConfirme(boutonStart, this, () -> {
            // Callback NON : reprendre la partie si elle était en cours
            if (jeuDemarre) {
                if (gameLoop != null) gameLoop.start();
                if (timeline != null) timeline.play();
            }
        });
    }

    // Appelée automatiquement quand on clique sur "OK" à l'étape de la sortie
    public void declencherSpawnPremierMicrobe() {
        if (timeline != null) {
            timeline.play(); // Relance brièvement la timeline pour faire spawn le premier microbe
        }
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

            // On désactive ici ---
            btnPotionGel.setDisable(true);

            // On lance l'animation de 3s (le temps de recharge)
            potionVue.animerJaugeActive(btnPotionGel, 3.0, () -> {
                // actionFin s'exécute quand l'animation est finie ---
                btnPotionGel.setDisable(false); // On réactive
                gelEnCooldown = false; // On libère le flag technique
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
        pageAccueil.setVisible(false);
        conteneurJeu.setVisible(true);
    }

    @FXML
    private void AfficherReglesMenu(javafx.event.ActionEvent event) {
        if (calqueRegles != null) {
            return;
        }

        // Conteneur pour isoler les règles
        calqueRegles = new javafx.scene.layout.StackPane();
        calqueRegles.getStyleClass().add("fond-regles-obscur");
        calqueRegles.setPrefSize(1020, 680);

        // Fenêtre centrale contenant le protocole
        javafx.scene.layout.VBox fenetreRegles = new javafx.scene.layout.VBox(25);
        fenetreRegles.getStyleClass().add("panneau-regles-terminal");
        fenetreRegles.setMaxSize(600, 500);
        fenetreRegles.setAlignment(javafx.geometry.Pos.CENTER);
        fenetreRegles.setPadding(new javafx.geometry.Insets(30));

        // Titre
        javafx.scene.control.Label titre = new javafx.scene.control.Label("PROTOCOLE SANITAIRE OBLIGATOIRE");
        titre.getStyleClass().add("titre-regles-neon");

        // Texte des règles de jeu
        javafx.scene.control.Label contenu = new javafx.scene.control.Label(
                "1. OBJECTIF : Protégez la zone en empêchant les microbes d'atteindre la sortie. Éliminez un maximum de microbes pour survivre un maximum de temps et gagner assez d'argent.\n\n" +
                        "2. TOURS DE DÉFENSE : Achetez vos unités (Scientifique, Chimiste...) dans le Shop et installez-les judicieusement sur le terrain. Vous pourrez ensuite les améliorer au fur et à mesure lorsque votre budget vous le permettra !\n\n" +
                        "3. POTIONS DE CRISE : Utilisez votre argent malignement pour utiliser les potions de Soin, de Rage ou de Gel intelligemment dans les moments les plus critiques. NE LES GASPILLEZ PAS !\n\n" +
                        "4. PROGRESSION : Soyez toujours prêt, les microbes tenteront tout pour s'échapper et chaque vague de microbes est plus redoutable que la précédente !"
        );
        contenu.getStyleClass().add("texte-regles-corps");
        contenu.setPrefWidth(540);
        contenu.setWrapText(true);

        // Bouton d'acceptation
        javafx.scene.control.Button btnAccepter = new javafx.scene.control.Button("COMPRIS !");
        btnAccepter.getStyleClass().add("btn-regles-action");

        // Action lors du clic sur le bouton de fermeture
        btnAccepter.setOnAction(e -> {
            // On enlève les règles
            javafx.scene.layout.Pane parentConteneur = (javafx.scene.layout.Pane) calqueRegles.getParent();
            if (parentConteneur != null) {
                parentConteneur.getChildren().remove(calqueRegles);
            }
            calqueRegles = null;
        });

        // Assemblage de la fenêtre de règles
        fenetreRegles.getChildren().addAll(titre, contenu, btnAccepter);
        calqueRegles.getChildren().add(fenetreRegles);

        javafx.scene.control.Button boutonSource = (javafx.scene.control.Button) event.getSource();
        javafx.scene.layout.Pane racineAbsolue = (javafx.scene.layout.Pane) boutonSource.getScene().getRoot();

        racineAbsolue.getChildren().add(calqueRegles);
    }

    // Implémentation de IControleurTuto
    @Override
    public void reinitialiserPourTuto() {
        if (timeline != null) timeline.stop();
        if (gameLoop != null) gameLoop.stop();

        for (MicrobeVue vueMicrobe : vuesMicrobes.values()) {
            conteneurPrincipal.getChildren().remove(vueMicrobe);
        }
        vuesMicrobes.clear();

        for (TourVue vueTour : vuesTours.values()) {
            conteneurPrincipal.getChildren().remove(vueTour);
        }
        vuesTours.clear();

        env.reinitialiser();

        conteneurPrincipal.getChildren().clear();
        conteneurPrincipal.getChildren().add(grilleJeu);
        TerrainVue terrainVue = new TerrainVue(env.getGrille(), env.getTailleTuile());
        terrainVue.dessinerTerrain(grilleJeu);

        for (int i = 0; i < boutonsInventaire.size(); i++) {
            inventaireModele.setTourCase(i, null);
            imagesInventaire.get(i).setImage(null);
            labelsInventaire.get(i).setText("");
            boutonsInventaire.get(i).setDisable(true);
            boutonsInventaire.get(i).getStyleClass().removeAll("case-tour-posee", "case-inventaire-selectionnee");
            boutonsInventaire.get(i).setOpacity(1.0);
        }

        jeuDemarre = false;
        premierMicrobeTutoMontre = false;
        gestionnaireTours.reinitialiser();
        creerGameLoop();
        updateCompteurs();
        mettreAJourLabelVague();
    }

    @Override
    public void setBoutonStartDisable(boolean disable) {
        if (boutonStart != null) boutonStart.setDisable(disable);
    }

    @Override
    public void setBoutonShopDisable(boolean disable) {
        if (boutonShop != null) boutonShop.setDisable(disable);
    }

    @Override
    public javafx.scene.Node getBoutonShop() {
        return boutonShop;
    }

    @Override
    public void setBtnPotionSoinDisable(boolean disable) {
        if (btnPotionSoin != null) btnPotionSoin.setDisable(disable);
    }

    @Override
    public void setBtnPotionRageDisable(boolean disable) {
        if (btnPotionRage != null) btnPotionRage.setDisable(disable);
    }

    @Override
    public void setBtnPotionGelDisable(boolean disable) {
        if (btnPotionGel != null) btnPotionGel.setDisable(disable);
    }

    @Override
    public void setBoutonTutoDisable(boolean disable) {
        if (boutonTuto != null) boutonTuto.setDisable(disable);
    }

    @Override
    public void setBoutonParametresDisable(boolean disable) {
        if (boutonParametres != null) boutonParametres.setDisable(disable);
    }

    @Override
    public void setBoutonInfoDisable(boolean disable) {
        if (boutonInfo != null) boutonInfo.setDisable(disable);
    }

    @Override
    public List<Button> getBoutonsInventaire() {
        return boutonsInventaire;
    }

    @Override
    public void setDidacticielActif(DidactitielVisuel didacticiel) {
        this.didacticielActif = didacticiel;
    }
}
