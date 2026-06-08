package universite_paris8.iut.aboudhan.saes2javafx.controller;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
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

        // Préparer les vagues du jeu
        env.getGestionnaireVagues().initialiserVagues(env);

        // Mise à jour des compteurs et du numéro de vague
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

            btn.setOnAction(event -> {
                String typeTour = inventaireModele.getTourCase(indexActuel);

                // On demande au modèle de gérer la logique
                boolean tourRappelee = gestionnaireTours.gererClicInventaire(indexActuel, typeTour);

                if (tourRappelee) {
                    btn.getStyleClass().remove("case-tour-posee");
                    btn.setDisable(false);
                } else if (gestionnaireTours.isModePlacementTour()) {
                    for (Button b : boutonsInventaire)
                        b.getStyleClass().remove("case-inventaire-selectionnee");
                    btn.getStyleClass().add("case-inventaire-selectionnee");
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

            if (nouvelleTour != null) {
                int tailleTuile = env.getTailleTuile();
                int caseX = (int) (xSurGrille / tailleTuile);
                int caseY = (int) (ySurGrille / tailleTuile);

                nouvelleTour.setX(grilleJeu.getLayoutX() + (caseX * tailleTuile));
                nouvelleTour.setY(grilleJeu.getLayoutY() + (caseY * tailleTuile));

                TourVue tourVue = new TourVue(nouvelleTour);
                vuesTours.put(nouvelleTour, tourVue);
                conteneurPrincipal.getChildren().add(tourVue);

                boutonsInventaire.get(indexBouton).getStyleClass().remove("case-inventaire-selectionnee");
                inventaireVue.desactiveBoutonTour(indexBouton);

                if (!jeuDemarre && boutonStart != null)
                    boutonStart.setDisable(false);
            }
        });    }

    public void updateCompteurs() {
        // Mise à jour du nombre d'infections / d'argent
        labelArgent.setText(String.valueOf(env.getArgent()));
        labelInfectes.setText(String.valueOf(env.getGensInfectes()));

        // Change la couleur du label d'infections en fonction de son nombre
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
        // Affichage du label de la vague après mise à jour
        int numActu = env.getGestionnaireVagues().getNumVagueActu() + 1;
        labelVague.setText("VAGUE " + numActu);
    }

    private void creerTimeline() {
        Vague vagueActuelle = env.getGestionnaireVagues().getVagueActuelle();
        if (vagueActuelle == null)
            return;

        timeline = new Timeline(
                new KeyFrame(Duration.seconds(vagueActuelle.getTempsIntervalle()), event -> {
                    List<Microbe> fileAttente = vagueActuelle.getFileAttenteMicrobes();

                    // Faire entrer le prochain microbe dans le jeu si il en reste encore
                    if (!fileAttente.isEmpty()) {
                        Microbe prochainMicrobe = fileAttente.remove(0);
                        env.getMicrobesActifs().add(prochainMicrobe);

                        // Crée son image l'associe à son microbe puis l'affiche sur le jeu
                        MicrobeVue vue = new MicrobeVue(prochainMicrobe);
                        vuesMicrobes.put(prochainMicrobe, vue);
                        conteneurPrincipal.getChildren().add(vue);
                    } else {
                        timeline.stop(); // Sinon on arrête de générer des monstres
                    }
                })
        );
        timeline.setCycleCount(Animation.INDEFINITE); // Répéter en boucle tant qu'on l'a pas arrêter
    }

    private void creerGameLoop() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // Condition de défaite
                if (env.verifierDefaite()) {
                    gameLoop.stop(); // Arrêt du jeu
                    if (timeline != null)
                        timeline.stop();
                    // Change la couleur du compteur d'infections en rouge et affiche l'écran de défaite
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

                    // Donner le bonus d'argent de survie de fin de vague
                    env.ajouterArgent(vagueActuelle.getBonus());
                    updateCompteurs();

                    int numVagueTerminee = env.getGestionnaireVagues().getNumVagueActu() + 1;

                    // Affichage de l'écran de victoire si c'était la dérnière vague ( victoire finale )
                    if (env.getGestionnaireVagues().estDerniereVague()) {
                        afficherEcranVictoire();
                    } else {
                        if (boutonStart != null) boutonStart.setDisable(true);
                        // Affichage de l'écran de victoire entre 2 vagues
                        VagueGagneeVue ecranInterVague = new VagueGagneeVue(conteneurPrincipal, grilleJeu, numVagueTerminee,
                                () -> {
                                    env.getGestionnaireVagues().AugmenterVague(); // Passe à la vague suivante
                                    mettreAJourLabelVague();
                                    if (boutonStart != null) boutonStart.setDisable(false); // Ractivation du bouton start
                                });
                        ecranInterVague.afficherSur(conteneurPrincipal);
                    }
                }

                // Si un microbe est sorti, on met à jour les compteurs
                boolean ennemiSorti = env.updateMicrobes();
                if (ennemiSorti) {
                    updateCompteurs();
                }

                // Crée une copie des microbes actifs pour éviter des bugs si un microbe est sorti pou est mort
                List<Microbe> copiesActifs = new ArrayList<>(env.getMicrobesActifs());
                for (Microbe m : copiesActifs) {
                    MicrobeVue imageVue = vuesMicrobes.get(m);
                    // Mise à jour de la position de l'image en fonction du microbe
                    if (imageVue != null)
                        imageVue.mettreAJourPosition();
                }

                vuesMicrobes.keySet().removeIf(m -> {
                    // On supprime l'image si le microbe n'est plus présent sur le terrain
                    if (!env.getMicrobesActifs().contains(m)) {
                        MicrobeVue imageVue = vuesMicrobes.get(m);
                        if (imageVue != null)
                            conteneurPrincipal.getChildren().remove(imageVue);
                        return true;
                    }
                    return false;
                });
            }
        };
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
        // Nettoyage des listes de vues
        vuesMicrobes.clear();
        for (TourVue vueT : vuesTours.values())
            conteneurPrincipal.getChildren().remove(vueT);
        vuesTours.clear();

        // Reset du conteneur de jeu
        conteneurPrincipal.getChildren().clear();
        conteneurPrincipal.getChildren().add(grilleJeu);

        // Redessiner le terrain de base
        TerrainVue terrainVue = new TerrainVue(env.getGrille(), env.getTailleTuile());
        terrainVue.dessinerTerrain(grilleJeu);

        // Reset des variables de contrôle d'affichage
        jeuDemarre = false;
        boutonStart.setDisable(false);

        // Reset visuel et textuel des cases de l'inventaire
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