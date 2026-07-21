package universite_paris8.iut.aboudhan.saes2javafx.vue.bouton;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import java.io.InputStream;
import java.util.Map;

public class CatalogueVue {

    private final StackPane calqueFond;
    private final Button btnFermer;
    private final Button tabEquipement;
    private final Button tabEnnemis;
    private final VBox contenuEquipement;
    private final VBox contenuEnnemis;
    private final StackPane zoneContenuCentral;
    private final StackPane zoneDescriptionDynamique;

    // Le constructeur reçoit désormais les données du contrôleur sous forme de Map (clé/valeur)
    public CatalogueVue(Map<String, Integer> prixEquipements, Runnable onClose) {

        calqueFond = new StackPane();
        calqueFond.getStyleClass().add("fond-flou");
        calqueFond.setPrefSize(1020, 680);

        VBox fenetre = new VBox(15);
        fenetre.getStyleClass().add("fenetre");
        fenetre.setMaxSize(650, 640);
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
        zoneContenuCentral.setPrefHeight(280);

        contenuEquipement = new VBox(10);
        contenuEquipement.setAlignment(Pos.TOP_CENTER);

        HBox rangeeTours = new HBox(20);
        rangeeTours.setAlignment(Pos.CENTER);

        HBox rangeePotions = new HBox(20);
        rangeePotions.setAlignment(Pos.CENTER);

        zoneDescriptionDynamique = new StackPane();
        zoneDescriptionDynamique.setPrefHeight(160);

        Button btnScientifique = creerBoutonImage("tour_scientifique.png");
        Button btnChimiste     = creerBoutonImage("tour_chimiste.png");
        Button btnScanner      = creerBoutonImage("tour_scanner.png");
        Button btnRayonX       = creerBoutonImage("tour_rayon_x.png");

        Button btnSoin = creerBoutonImage("potion_soin.png");
        Button btnRage = creerBoutonImage("potion_rage.png");
        Button btnGel  = creerBoutonImage("potion_gel.png");

        // On récupère les prix fournis par le contrôleur sans toucher au modèle !
        int prixSci = prixEquipements.getOrDefault("scientifique", 25);
        int prixChi = prixEquipements.getOrDefault("chimiste", 50);
        int prixScn = prixEquipements.getOrDefault("scanner", 75);
        int prixRay = prixEquipements.getOrDefault("rayon_x", 100);
        int prixSoin = prixEquipements.getOrDefault("potion_soin", 150);
        int prixRage = prixEquipements.getOrDefault("potion_rage", 125);
        int prixGel = prixEquipements.getOrDefault("potion_gel", 75);

        btnScientifique.setOnAction(e -> afficherTexteDescriptif(
                "TOUR SCIENTIFIQUE",
                "Prix : " + prixSci + "$\n\n" +
                        "Envoie des seringues en coup par coup sur le premier microbe entrant dans sa zone de portée.\n\n" +
                        "■ Portée : 4 cases\n■ Dégâts : 10 par seconde\n■ Vitesse de tir : 1.0/s"
        ));

        btnChimiste.setOnAction(e -> afficherTexteDescriptif(
                "TOUR CHIMISTE",
                "Prix : " + prixChi + "$\n\n" +
                        "Jette des potions coup par coup sur premier microbe entrant dans sa zone de portée et fais des dégâts sur les microbes qui se trouve au alentour.\n\n" +
                        "■ Portée : 3 cases\n■ Dégâts : 16 par seconde\n■ Vitesse de tir : 0.33/s"
        ));

        btnScanner.setOnAction(e -> afficherTexteDescriptif(
                "TOUR SCANNER",
                "Prix : " + prixScn + "$\n\n" +
                        "Fait des dégats en scannant tous les microbes qui se trouve dans sa zone de portée.\n\n" +
                        "■ Portée : 3 cases\n■ Dégâts : 10 par seconde\n■ Vitesse de tir : 0.8/s"
        ));

        btnRayonX.setOnAction(e -> afficherTexteDescriptif(
                "TOUR RAYON X",
                "Prix : " + prixRay + "$\n\n" +
                        "Envoie un rayon laser sur le 1er microbes qui se trouve dans sa zone de portée et fait de plus en plus de dégats.\n\n" +
                        "■ Portée : 3 cases\n■ Dégâts : 2 par seconde\n■ Vitesse de tir : 10/s et se multiplie par 3 chaque seconde."
        ));

        btnSoin.setOnAction(e -> afficherTexteDescriptif(
                "POTION DE SOIN",
                "Prix : " + prixSoin + "$\n\n" +
                        "Soigne 10 personnes qui ont été infectées."
        ));

        btnRage.setOnAction(e -> afficherTexteDescriptif(
                "POTION DE RAGE",
                "Prix : " + prixRage + "$\n\n" +
                        "Multiplie par 2 la vitesse de tir des tours qui se trouve sur la map pendant 5 secondes."
        ));
        btnGel.setOnAction(e -> afficherTexteDescriptif(
                "POTION DE GEL",
                "Prix : " + prixGel + "$\n\n" +
                        "Permet de gelé tous les microbes qui se trouve sur la map pendant 3 secondes."
        ));

        rangeeTours.getChildren().addAll(btnScientifique, btnChimiste, btnScanner, btnRayonX);
        rangeePotions.getChildren().addAll(btnSoin, btnRage, btnGel);

        Label lblTours = new Label("--- TOURS DE DÉFENSE ---");
        lblTours.getStyleClass().add("label-categorie-tours");

        Label lblPotions = new Label("--- POTIONS ---");
        lblPotions.getStyleClass().add("label-categorie-potions");

        contenuEquipement.getChildren().addAll(lblTours, rangeeTours, lblPotions, rangeePotions);

        // --- PARTIE ENNEMIS (Inchangée mais isolée) ---
        contenuEnnemis = new VBox(10);
        contenuEnnemis.setAlignment(Pos.TOP_CENTER);

        HBox sixPremier = new HBox(20);
        sixPremier.setAlignment(Pos.CENTER);
        HBox sixDernier = new HBox(20);
        sixDernier.setAlignment(Pos.CENTER);

        Button btnMicrobe1  = creerBoutonImage("rhinovirus.png");
        Button btnMicrobe2  = creerBoutonImage("norovirus.png");
        Button btnMicrobe3  = creerBoutonImage("streptocoque.png");
        Button btnMicrobe4  = creerBoutonImage("influenza.png");
        Button btnMicrobe5  = creerBoutonImage("varicelle.png");
        Button btnMicrobe6  = creerBoutonImage("covid.png");
        Button btnMicrobe7  = creerBoutonImage("vih.png");
        Button btnMicrobe8  = creerBoutonImage("tuberculose.png");
        Button btnMicrobe9  = creerBoutonImage("peste.png");
        Button btnMicrobe10 = creerBoutonImage("rage.png");
        Button btnMicrobe11 = creerBoutonImage("variole.png");

        btnMicrobe1.setOnAction(e -> afficherTexteDescriptif("RHINOVIRUS", "Virus assez faible qui tente de s'échapper du laboratoire.\n\n" +
                "■ Vie : 30 pv\n" +
                "■ Dégâts : 1 personne intecter\n" +
                "■ Vitesse : 0,8 case/s\n" +
                "■ Gain : 2 pièce a chaque mort."
        ));
        btnMicrobe2.setOnAction(e -> afficherTexteDescriptif("NOROVIRUS", "Virus assez faible qui tente de s'échapper du laboratoire.\n\n" +
                "■ Vie : 40 pv\n" +
                "■ Dégâts : 1 personne intecter\n" +
                "■ Vitesse : 1 case/s\n" +
                "■ Gain : 3 pièce a chaque mort."
        ));
        btnMicrobe3.setOnAction(e -> afficherTexteDescriptif("STREPTOCOQUE", "Microbe plus solide que les 2 premiers.\n\n" +
                "■ Vie : 60 pv\n" +
                "■ Dégâts : 2 personne intecter\n" +
                "■ Vitesse : 0,9 case/s\n" +
                "■ Gain : 5 pièce a chaque mort."
        ));
        btnMicrobe4.setOnAction(e -> afficherTexteDescriptif("INFLUENZA", "Microbe invisible que ne peux pas se faire toucher par les tours de défense sauf par le SCANNER.\n\n" +
                "■ Vie : 10 pv\n" +
                "■ Dégâts : 3 personne intecter\n" +
                "■ Vitesse : 0,7 case/s\n" +
                "■ Gain : 10 pièce a chaque mort."
        ));
        btnMicrobe5.setOnAction(e -> afficherTexteDescriptif("VARICELLE", "La varicelle peux somnoler un court instant et reprend sont chemin avec une vitesse 2 fois plus rapide pendant 3 secondes.\n\n" +
                "■ Vie : 120 pv\n" +
                "■ Dégâts : 5 personne intecter\n" +
                "■ Vitesse : 1 case/s || 2 case/s\n" +
                "■ Gain : 15 pièce a chaque mort."
        ));
        btnMicrobe6.setOnAction(e -> afficherTexteDescriptif("COVID-19", "C'est un virus qui n'a pas de capaciter supplementaire mais il est plus resistant.\n\n" +
                "■ Vie : 200 pv\n" +
                "■ Dégâts : 8 personne intecter\n" +
                "■ Vitesse : 0,8 case/s\n" +
                "■ Gain : 20 pièce a chaque mort."
        ));
        btnMicrobe7.setOnAction(e -> afficherTexteDescriptif("VIH", "Permet de reinitialiser les dégats des tours de rayon-x pour qu'il ne fasse pas beaucoup de dégats.\n\n" +
                "■ Vie : 250 pv\n" +
                "■ Dégâts : 10 personne intecter\n" +
                "■ Vitesse : 0,6 case/s\n" +
                "■ Gain : 30 pièce a chaque mort."
        ));
        btnMicrobe8.setOnAction(e -> afficherTexteDescriptif("TUBERCULOSE", "Pas de capaciter supplementaire.\n\n" +
                "■ Vie : 600 pv\n" +
                "■ Dégâts : 12 personne intecter\n" +
                "■ Vitesse : 0,5 case/s\n" +
                "■ Gain : 40 pièce a chaque mort."
        ));
        btnMicrobe9.setOnAction(e -> afficherTexteDescriptif("PESTE", "Pas de capaciter supplementaire\n\n" +
                "■ Vie : 450 pv\n" +
                "■ Dégâts : 20 personne intecter\n" +
                "■ Vitesse : 0,8 case/s\n" +
                "■ Gain : 50 pièce a chaque mort."
        ));
        btnMicrobe10.setOnAction(e -> afficherTexteDescriptif("RAGE", "Lorsque celui-ci tombe sous la barre des 25 pourcents de ces pv il se met en rage et va 2 fois plus vite.\n\n" +
                "■ Vie : 300 pv\n" +
                "■ Dégâts : 15 personne intecter\n" +
                "■ Vitesse : 1 case/s || 2 case/s\n" +
                "■ Gain : 70 pièce a chaque mort."
        ));
        btnMicrobe11.setOnAction(e -> afficherTexteDescriptif("VARIOLE", "Le virus le plus puissant avec une tres grande barre de vie et une vitesse faible.\n\n" +
                "■ Vie : 2000 pv\n" +
                "■ Dégâts : 50 personne intecter\n" +
                "■ Vitesse : 0,3 case/s\n" +
                "■ Gain : 100 pièce a chaque mort."
        ));

        sixPremier.getChildren().addAll(btnMicrobe1, btnMicrobe2, btnMicrobe3, btnMicrobe4, btnMicrobe5, btnMicrobe6);
        sixDernier.getChildren().addAll(btnMicrobe7, btnMicrobe8, btnMicrobe9, btnMicrobe10, btnMicrobe11);

        Label lblEnnemis = new Label("--- LES ENNEMIS DU LABO ---");
        lblEnnemis.getStyleClass().add("label-categorie-ennemis");

        contenuEnnemis.getChildren().addAll(lblEnnemis, sixPremier, sixDernier);

        zoneContenuCentral.getChildren().add(contenuEquipement);
        tabEquipement.getStyleClass().add("onglet-actif");

        tabEquipement.setOnAction(e -> {
            zoneDescriptionDynamique.getChildren().clear();
            zoneContenuCentral.getChildren().clear();
            zoneContenuCentral.getChildren().add(contenuEquipement);
            tabEquipement.getStyleClass().add("onglet-actif");
            tabEnnemis.getStyleClass().remove("onglet-actif");
        });

        tabEnnemis.setOnAction(e -> {
            zoneDescriptionDynamique.getChildren().clear();
            zoneContenuCentral.getChildren().clear();
            zoneContenuCentral.getChildren().add(contenuEnnemis);
            tabEnnemis.getStyleClass().add("onglet-actif");
            tabEquipement.getStyleClass().remove("onglet-actif");
        });

        btnFermer = new Button("FERMER");
        btnFermer.getStyleClass().add("btn-lancer");
        btnFermer.setOnAction(e -> onClose.run());

        fenetre.getChildren().addAll(titre, barreOnglets, zoneContenuCentral, zoneDescriptionDynamique, btnFermer);
        calqueFond.getChildren().add(fenetre);
    }

    private Button creerBoutonImage(String imgNom) {
        VBox boiteInterieure = new VBox();
        boiteInterieure.setAlignment(Pos.CENTER);

        String cheminImg = "/universite_paris8/iut/aboudhan/saes2javafx/vue/" + imgNom;
        InputStream stream = getClass().getResourceAsStream(cheminImg);

        ImageView image = new ImageView();
        if (stream != null) {
            image.setImage(new Image(stream));
        } else {
            System.err.println("[WARN] Impossible de charger l'image : " + cheminImg);
        }

        image.setFitHeight(40);
        image.setFitWidth(40);
        image.setPreserveRatio(true);
        boiteInterieure.getChildren().add(image);

        Button boutonItem = new Button();
        boutonItem.setGraphic(boiteInterieure);
        boutonItem.setPrefSize(110, 110);
        boutonItem.getStyleClass().add("btn-catalogue-item");

        return boutonItem;
    }

    private void afficherTexteDescriptif(String nomItem, String description) {
        zoneDescriptionDynamique.getChildren().clear();

        BorderPane blocTexte = new BorderPane();
        blocTexte.getStyleClass().add("bloc-description");
        blocTexte.setPadding(new Insets(10, 15, 10, 15));

        VBox textes = new VBox(5);
        textes.setAlignment(Pos.TOP_LEFT);

        Label lblTitre = new Label(nomItem);
        lblTitre.getStyleClass().add("description-titre");

        Label lblDesc = new Label(description);
        lblDesc.getStyleClass().add("description-corps");
        lblDesc.setWrapText(true);

        textes.getChildren().addAll(lblTitre, lblDesc);

        ScrollPane defilement = new ScrollPane();
        defilement.setContent(textes);
        defilement.setFitToWidth(true);
        defilement.getStyleClass().add("scroll-description");

        blocTexte.setCenter(defilement);

        Button btnX = new Button("X");
        btnX.getStyleClass().add("btn-fermer-description");
        btnX.setOnAction(e -> zoneDescriptionDynamique.getChildren().clear());

        BorderPane.setAlignment(btnX, Pos.TOP_RIGHT);
        blocTexte.setRight(btnX);

        zoneDescriptionDynamique.getChildren().add(blocTexte);
    }

    public void afficherSur(Pane conteneur) {
        if (!conteneur.getChildren().contains(calqueFond)) {
            conteneur.getChildren().add(calqueFond);
        }
    }

    public void cacher(Pane conteneur) {
        conteneur.getChildren().remove(calqueFond);
    }

    public VBox getContenuEquipement() { return contenuEquipement; }
    public VBox getContenuEnnemis() { return contenuEnnemis; }
}