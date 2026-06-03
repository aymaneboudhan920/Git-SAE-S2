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

    // Variables FXML (sans private pour que le GestionnaireTours puisse y accéder)
    @FXML
    public Pane conteneurPrincipal;
    @FXML
    public TilePane grilleJeu;
    @FXML
    public Button boutonStart;

    @FXML private Label labelArgent;
    @FXML private Label labelInfectes;
    @FXML private Label labelVague;

    @FXML private Button caseInventaire1, caseInventaire2, caseInventaire3, caseInventaire4, caseInventaire5, caseInventaire6, caseInventaire7, caseInventaire8;
    @FXML private ImageView imageInventaire1, imageInventaire2, imageInventaire3, imageInventaire4, imageInventaire5, imageInventaire6, imageInventaire7, imageInventaire8;
    @FXML private Label labelInventaire1, labelInventaire2, labelInventaire3, labelInventaire4, labelInventaire5, labelInventaire6, labelInventaire7, labelInventaire8;

    public final Environnement env = new Environnement();
    public Inventaire inventaireModele;
    public InventaireVue inventaireVue;
    public boolean jeuDemarre = false;

    public final List<Button> boutonsInventaire = new ArrayList<>();
    private final List<ImageView> imagesInventaire = new ArrayList<>();
    private final List<Label> labelsInventaire = new ArrayList<>();

    private final java.util.Map<Microbe, MicrobeVue> vuesMicrobes = new java.util.HashMap<>();
    public final java.util.Map<Tour, TourVue> vuesTours = new java.util.HashMap<>();

    private AnimationTimer gameLoop;
    private Timeline timeline;
    private ShopVue shopActuel = null;

    private GestionnaireTours gestionnaireTours;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        TerrainVue terrainVue = new TerrainVue(env.getGrille(), env.getTailleTuile());
        terrainVue.dessinerTerrain(grilleJeu);

        env.getGestionnaireVagues().initialiserVagues(env);

        updateCompteurs();
        mettreAJourLabelVague();

        creerGameLoop();
        creerTimeline();

        boutonsInventaire.addAll(Arrays.asList(caseInventaire1, caseInventaire2, caseInventaire3, caseInventaire4, caseInventaire5, caseInventaire6, caseInventaire7, caseInventaire8));
        imagesInventaire.addAll(Arrays.asList(imageInventaire1, imageInventaire2, imageInventaire3, imageInventaire4, imageInventaire5, imageInventaire6, imageInventaire7, imageInventaire8));
        labelsInventaire.addAll(Arrays.asList(labelInventaire1, labelInventaire2, labelInventaire3, labelInventaire4, labelInventaire5, labelInventaire6, labelInventaire7, labelInventaire8));

        this.inventaireModele = new Inventaire(8);
        this.inventaireVue = new InventaireVue(boutonsInventaire, imagesInventaire, labelsInventaire);

        this.gestionnaireTours = new GestionnaireTours(this);

        for (int i = 0; i < boutonsInventaire.size(); i++) {
            final int indexActuel = i;
            Button btn = boutonsInventaire.get(i);
            btn.setDisable(true);
            labelsInventaire.get(i).setText("");

            // Délégation du clic d'inventaire
            btn.setOnAction(event -> gestionnaireTours.gererClicInventaire(indexActuel, btn));
        }

        // Délégation complète du clic sur le terrain pour poser ou annuler
        conteneurPrincipal.setOnMouseClicked(event -> gestionnaireTours.gererClicTerrain(event));
    }

    public void updateCompteurs() {
        labelArgent.setText(String.valueOf(env.getArgent()));
        labelInfectes.setText(String.valueOf(env.getGensInfectes()));

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
            st.setToX(0.95); st.setToY(0.95);
            st.setAutoReverse(true); st.setCycleCount(2);

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

    @FXML
    private void actionBoutonShop() {
        if (shopActuel != null) {
            shopActuel.cacher(conteneurPrincipal);
            if (jeuDemarre) { gameLoop.start(); timeline.play(); }
            shopActuel = null;
            return;
        }

        if (jeuDemarre) { gameLoop.stop(); timeline.pause(); }

        shopActuel = new ShopVue(
                () -> {
                    if (shopActuel != null) {
                        shopActuel.cacher(conteneurPrincipal);
                        if (jeuDemarre) { gameLoop.start(); timeline.play(); }
                        shopActuel = null;
                    }
                },
                (typeItem) -> {
                    if (typeItem.equals("potion_soin") || typeItem.equals("potion_rage") || typeItem.equals("potion_gel")) {
                        if (shopActuel != null) {
                            shopActuel.cacher(conteneurPrincipal);
                            if (jeuDemarre) {
                                gameLoop.start();
                                timeline.play();
                            }
                            shopActuel = null;
                        }
                        return;
                    }

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
                    }

                    if (shopActuel != null) {
                        shopActuel.cacher(conteneurPrincipal);
                        if (jeuDemarre) { gameLoop.start(); timeline.play(); }
                        shopActuel = null;
                    }
                }
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
                    List<Microbe> fileAttente = vagueActuelle.getFileAttenteMicrobes();
                    if (!fileAttente.isEmpty()) {
                        Microbe prochainMicrobe = fileAttente.remove(0);
                        env.getMicrobesActifs().add(prochainMicrobe);

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

    private void creerGameLoop() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (env.verifierDefaite()) {
                    gameLoop.stop();
                    if (timeline != null) timeline.stop();
                    updateCompteurs();
                    afficherEcranDefaite();
                    return;
                }

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
                    }
                }

                boolean unEnnemiEstPasse = env.unPasDeTemps();
                if (unEnnemiEstPasse) {
                    updateCompteurs();
                }

                List<Microbe> copiesActifs = new ArrayList<>(env.getMicrobesActifs());
                for (Microbe m : copiesActifs) {
                    MicrobeVue imageVue = vuesMicrobes.get(m);
                    if (imageVue != null) imageVue.mettreAJourPosition();
                }

                vuesMicrobes.keySet().removeIf(m -> {
                    if (!env.getMicrobesActifs().contains(m)) {
                        MicrobeVue imageVue = vuesMicrobes.get(m);
                        if (imageVue != null) conteneurPrincipal.getChildren().remove(imageVue);
                        return true;
                    }
                    return false;
                });

                List<Microbe> microbes = env.getMicrobesActifs();
                for (Tour tour : vuesTours.keySet()) {
                    tour.attaquer(microbes);
                }
            }
        };
    }

    private void afficherEcranDefaite() {
        DefaiteVue ecranDefaite = new DefaiteVue(conteneurPrincipal, grilleJeu, () -> {
            env.reinitialiser();
            reinitialiserToutLeJeuVisuel();
        });
        ecranDefaite.afficherSur(conteneurPrincipal);
    }

    private void afficherEcranVictoire() {
        VictoireVue ecranVictoire = new VictoireVue(conteneurPrincipal, grilleJeu, () -> {
            env.reinitialiser();
            reinitialiserToutLeJeuVisuel();
            mettreAJourLabelVague();
        });
        ecranVictoire.afficherSur(conteneurPrincipal);
    }

    private void reinitialiserToutLeJeuVisuel() {
        // Nettoyage des listes de vues
        vuesMicrobes.clear();
        for (TourVue vueT : vuesTours.values()) conteneurPrincipal.getChildren().remove(vueT);
        vuesTours.clear();

        // Reset du conteneur de jeu
        conteneurPrincipal.getChildren().clear();
        conteneurPrincipal.getChildren().add(grilleJeu);

        // Redessiner le terrain de base
        TerrainVue terrainVue = new TerrainVue(env.getGrille(), env.getTailleTuile());
        terrainVue.dessinerTerrain(grilleJeu);

        // Reset des variables de contrôle d'affichage
        jeuDemarre = false;
        if (boutonStart != null) boutonStart.setDisable(false);

        // Reset visuel et textuel des slots d'inventaire
        for (int i = 0; i < boutonsInventaire.size(); i++) {
            inventaireModele.setTourCase(i, null);
            imagesInventaire.get(i).setImage(null);
            labelsInventaire.get(i).setText("");
            boutonsInventaire.get(i).setDisable(true);
            boutonsInventaire.get(i).getStyleClass().remove("case-inventaire-selectionnee");
        }

        // Reset du gestionnaire de clics
        gestionnaireTours.reinitialiser();

        // Rafraîchissement global
        updateCompteurs();
        creerGameLoop();
    }
}
