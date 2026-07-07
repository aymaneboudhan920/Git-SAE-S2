package universite_paris8.iut.aboudhan.saes2javafx.vue.bouton;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import java.io.InputStream;
import javafx.scene.control.ScrollPane;

public class CatalogueVue {

    private final StackPane calqueFond;
    private final Button btnFermer;

    // Boutons pour les onglets
    private final Button tabEquipement;
    private final Button tabEnnemis;

    private final VBox contenuEquipement;
    private final VBox contenuEnnemis;
    private final StackPane zoneContenuCentral;

    private final StackPane zoneDescriptionDynamique;

    public CatalogueVue(Runnable onClose) {

        calqueFond = new StackPane();
        calqueFond.getStyleClass().add("fond-flou");
        calqueFond.setPrefSize(1020, 680);

        VBox fenetre = new VBox(20);
        fenetre.getStyleClass().add("fenetre");
        fenetre.setMaxSize(650, 550); // Un peu plus grand pour accueillir les cases du shop
        fenetre.setAlignment(Pos.TOP_CENTER);
        fenetre.setPadding(new Insets(20));

        Label titre = new Label("CATALOGUE");
        titre.getStyleClass().add("titre");

        HBox barreOnglets = new HBox(10);
        barreOnglets.setAlignment(Pos.CENTER);

        tabEquipement = new Button("ÉQUIPEMENT");
        tabEnnemis = new Button("ENNEMIS");

        tabEquipement.getStyleClass().add("btn-regles-action");
        tabEnnemis.getStyleClass().add("btn-regles-action");
        barreOnglets.getChildren().addAll(tabEquipement, tabEnnemis);

        zoneContenuCentral = new StackPane();
        zoneContenuCentral.setPrefHeight(380);

        contenuEquipement = new VBox(15);
        contenuEquipement.setAlignment(Pos.TOP_CENTER);

        // Barre horizontale des Tours
        HBox rangeeTours = new HBox(20);
        rangeeTours.setAlignment(Pos.CENTER);

        // Barre horizontale des Potions
        HBox rangeePotions = new HBox(20);
        rangeePotions.setAlignment(Pos.CENTER);

        // Zone où s'affichera le texte descriptif quand on clique
        zoneDescriptionDynamique = new StackPane();
        zoneDescriptionDynamique.setPrefHeight(120);

        Button btnScientifique = creerBoutonImage("tour_scientifique.png");
        Button btnChimiste     = creerBoutonImage("tour_chimiste.png");
        Button btnScanner      = creerBoutonImage("tour_scanner.png");
        Button btnRayonX       = creerBoutonImage("tour_rayon_x.png");

        Button btnSoin = creerBoutonImage("potion_soin.png");
        Button btnRage = creerBoutonImage("potion_rage.png");
        Button btnGel  = creerBoutonImage("potion_gel.png");

        // Définition des actions au clic pour afficher leur texte respectif
        btnScientifique.setOnAction(e -> afficherTexteDescriptif("TOUR SCIENTIFIQUE", "Prix : 25$\n" + "Envoie des seringues en coup par coup sur le premier microbe entrant dans sa zone de portée.\n" + "■ Portée : 4 cases\n" + "■ Dégâts : 10 par seconde\n" + "■ Vitesse de tir : 1.0/s" ));
        btnChimiste.setOnAction(e -> afficherTexteDescriptif("TOUR CHIMISTE", "Prix : 50$\n" + "Jette des potions coup par coup sur premier microbe entrant dans sa zone de portée et fais des dégâts sur les microbes qui se trouve au alentour.\n" + "■ Portée : 3 cases\n" + "■ Dégâts : 16 par seconde\n" + "■ Vitesse de tir : 0.33/s" ));
        btnScanner.setOnAction(e -> afficherTexteDescriptif("TOUR SCANNER", "Prix : 75$\n" + "Fait des dégats en scannant tous les microbes qui se trouve dans sa zone de portée.\n" + "■ Portée : 3 cases\n" + "■ Dégâts : 10 par seconde\n" + "■ Vitesse de tir : 0.8/s"));
        btnRayonX.setOnAction(e -> afficherTexteDescriptif("TOUR RAYON X", "Prix : 100$\n" + "Envoie un rayon laser sur le 1er microbes qui se trouve dans sa zone de portée et fait de plus en plus de dégats.\n" + "■ Portée : 3 cases\n" + "■ Dégâts : 2 par seconde\n" + "■ Vitesse de tir : 10/s et se multiplie par 3 chaque seconde."));

        btnSoin.setOnAction(e -> afficherTexteDescriptif("POTION DE SOIN", "Prix : 150$\n" + "Soigne 10 personnes qui ont été infectées."));
        btnRage.setOnAction(e -> afficherTexteDescriptif("POTION DE RAGE", "Prix : 125$\n" + "Multiplie par 2 la vitesse de tir des tours qui se trouve sur la map pendant 5 secondes."));
        btnGel.setOnAction(e -> afficherTexteDescriptif("POTION DE GEL", "Prix : 75$\n" + "Permet de gelé tous les microbes qui se trouve sur la map pendant 3 secondes."));

        // Assemblage des rangées
        rangeeTours.getChildren().addAll(btnScientifique, btnChimiste, btnScanner, btnRayonX);
        rangeePotions.getChildren().addAll(btnSoin, btnRage, btnGel);

        // Ajout des composants dans l'onglet Équipement
        Label lblTours = new Label("--- TOURS DE DÉFENSE ---");
        lblTours.getStyleClass().add("label-categorie-tours");

        Label lblPotions = new Label("--- POTIONS ---");
        lblPotions.getStyleClass().add("label-categorie-potions");

        contenuEquipement.getChildren().addAll(lblTours, rangeeTours, lblPotions, rangeePotions, zoneDescriptionDynamique);

        contenuEnnemis = new VBox(10);
        contenuEnnemis.setAlignment(Pos.CENTER);
        Label lblEnnemis = new Label("Détails et faiblesses des différents virus et microbes...");
        lblEnnemis.getStyleClass().add("label-ennemis-defaut");
        contenuEnnemis.getChildren().add(lblEnnemis);

        // Par défaut, on affiche l'équipement
        zoneContenuCentral.getChildren().add(contenuEquipement);
        tabEquipement.getStyleClass().add("onglet-actif");

        // Logique de switch au clic sur les onglets
        tabEquipement.setOnAction(e -> {
            zoneContenuCentral.getChildren().clear();
            zoneContenuCentral.getChildren().add(contenuEquipement);
            tabEquipement.getStyleClass().add("onglet-actif");
            tabEnnemis.getStyleClass().remove("onglet-actif");
        });

        tabEnnemis.setOnAction(e -> {
            zoneContenuCentral.getChildren().clear();
            zoneContenuCentral.getChildren().add(contenuEnnemis);
            tabEnnemis.getStyleClass().add("onglet-actif");
            tabEquipement.getStyleClass().remove("onglet-actif");
        });

        // Bouton Fermer
        btnFermer = new Button("FERMER");
        btnFermer.getStyleClass().add("btn-lancer");
        btnFermer.setOnAction(e -> onClose.run());

        // Assemblage final
        fenetre.getChildren().addAll(titre, barreOnglets, zoneContenuCentral, btnFermer);
        calqueFond.getChildren().add(fenetre);
    }

    /**
     * Méthode calquée à 100% sur la création d'item de ton ShopVue
     */
    private Button creerBoutonImage(String imgNom) {
        VBox boiteInterieure = new VBox();
        boiteInterieure.setAlignment(Pos.CENTER);

        // Reprise exacte de ton chemin d'accès vers le dossier vue
        String cheminImg = "/universite_paris8/iut/aboudhan/saes2javafx/vue/" + imgNom;
        InputStream stream = getClass().getResourceAsStream(cheminImg);

        ImageView image = new ImageView();
        if (stream != null) {
            image.setImage(new Image(stream));
        }

        // Tailles exactes de ton ShopVue
        image.setFitHeight(40);
        image.setFitWidth(40);
        image.setPreserveRatio(true);

        boiteInterieure.getChildren().add(image);

        Button boutonItem = new Button();
        boutonItem.setGraphic(boiteInterieure);
        boutonItem.setPrefSize(110, 110); // Même taille de case de bouton (110x110)
        boutonItem.getStyleClass().add("btn-catalogue-item");

        return boutonItem;
    }

    private void afficherTexteDescriptif(String nomItem, String description) {
        zoneDescriptionDynamique.getChildren().clear();

        // Conteneur principal de la description
        BorderPane blocTexte = new BorderPane();
        blocTexte.getStyleClass().add("bloc-description");
        blocTexte.setPadding(new Insets(10, 15, 10, 15));

        // Partie textuelle (Titre + Corps)
        VBox textes = new VBox(5);
        textes.setAlignment(Pos.TOP_LEFT); // Aligné en haut à gauche pour le défilement

        Label lblTitre = new Label(nomItem);
        lblTitre.getStyleClass().add("description-titre");

        Label lblDesc = new Label(description);
        lblDesc.getStyleClass().add("description-corps");
        lblDesc.setWrapText(true); // Permet le retour à la ligne automatique

        textes.getChildren().addAll(lblTitre, lblDesc);

        // Ajout d'un ScrollPane pour faire défiler le texte s'il est trop long !
        ScrollPane defilement = new ScrollPane();
        defilement.setContent(textes);
        defilement.setFitToWidth(true); // Le texte prend toute la largeur disponible
        defilement.getStyleClass().add("scroll-description"); // Style personnalisé pour cacher les bordures blanches par défaut

        blocTexte.setCenter(defilement);

        // Bouton de fermeture 'X'
        Button btnX = new Button("X");
        btnX.getStyleClass().add("btn-fermer-description");
        btnX.setOnAction(e -> zoneDescriptionDynamique.getChildren().clear());

        BorderPane.setAlignment(btnX, Pos.TOP_RIGHT);
        blocTexte.setRight(btnX);

        zoneDescriptionDynamique.getChildren().add(blocTexte);
    }

    public void afficherSur(Pane conteneur) {
        conteneur.getChildren().add(calqueFond);
    }

    public void cacher(Pane conteneur) {
        conteneur.getChildren().remove(calqueFond);
    }

    public VBox getContenuEquipement() { return contenuEquipement; }
    public VBox getContenuEnnemis() { return contenuEnnemis; }
}