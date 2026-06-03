package universite_paris8.iut.aboudhan.saes2javafx.vue;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import universite_paris8.iut.aboudhan.saes2javafx.modele.TourChimiste;
import universite_paris8.iut.aboudhan.saes2javafx.modele.TourRayonX;
import universite_paris8.iut.aboudhan.saes2javafx.modele.TourScanner;
import universite_paris8.iut.aboudhan.saes2javafx.modele.TourScientifique;

import java.io.InputStream;
import java.util.function.Consumer;

public class ShopVue {

    private StackPane rootShop;
    private VBox menuInterieur;
    private Runnable actionFermer;

    private Consumer<String> actionClicItem;

    public ShopVue(Runnable actionFermer, Consumer<String> actionClicItem) {
        this.actionFermer = actionFermer;
        this.actionClicItem = actionClicItem;
        creerInterface();
    }

    private void creerInterface() {
        rootShop = new StackPane();
        rootShop.setPrefSize(1020, 680);
        rootShop.getStyleClass().add("fond-flou-shop");

        menuInterieur = new VBox(20);
        menuInterieur.setAlignment(Pos.CENTER);
        menuInterieur.setMaxSize(850, 550);
        menuInterieur.getStyleClass().add("fenetre-shop");

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_RIGHT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label titre = new Label("SHOP du LABORATOIRE");
        titre.getStyleClass().add("titre-shop");

        Button btnFermer = new Button("X");
        btnFermer.getStyleClass().add("btn-fermer-shop");
        btnFermer.setPrefSize(35, 35);

        btnFermer.setOnAction(e -> {
            // On laisse le contrôleur gérer la fermeture via son Runnable
            actionFermer.run();
        });

        header.getChildren().addAll(titre, spacer, btnFermer);

        VBox sectionTours = creerSection("TOURS DE DÉFENSE", "titre-categorie-tours");
        HBox containerTours = new HBox(15);
        containerTours.setAlignment(Pos.CENTER);

        // Création des boutons d'items
        Button btnScientifique = creerItem("Scientifique", "tour_scientifique.png", TourScientifique.prixAchat + "$");
        btnScientifique.setOnAction(e -> declencherAchat("scientifique"));

        Button btnChimiste = creerItem("Chimiste", "tour_chimiste.png", TourChimiste.prixAchat + "$");
        btnChimiste.setOnAction(e -> declencherAchat("chimiste"));

        Button btnScanner = creerItem("Scanner", "tour_scanner.png", TourScanner.prixAchat + "$");
        btnScanner.setOnAction(e -> declencherAchat("scanner"));

        Button btnRayonX = creerItem("Rayon_X", "tour_rayon_x.png", TourRayonX.prixAchat + "$");
        btnRayonX.setOnAction(e -> declencherAchat("rayon_x"));

        containerTours.getChildren().addAll(btnScientifique, btnChimiste, btnRayonX, btnScanner);

        VBox sectionPotions = creerSection("POTIONS", "titre-categorie-potions");
        HBox containerPotions = new HBox(15);
        containerPotions.setAlignment(Pos.CENTER);

        Button btnRage = creerItem("Rage", "potion_rage.png", "50$");
        btnRage.setOnAction(e -> declencherAchat("potion_rage"));

        Button btnSoin = creerItem("Soin", "potion_soin.png", "80$");
        btnSoin.setOnAction(e -> declencherAchat("potion_soin"));

        Button btnGel = creerItem("Gel", "potion_gel.png", "30$");
        btnGel.setOnAction(e -> declencherAchat("potion_gel"));

        containerPotions.getChildren().addAll(btnRage, btnSoin, btnGel);

        menuInterieur.getChildren().addAll(header, sectionTours, containerTours, sectionPotions, containerPotions);
        rootShop.getChildren().add(menuInterieur);
    }

    private void declencherAchat(String typeItem) {
        if (actionClicItem != null) {
            actionClicItem.accept(typeItem);
        }
    }

    private VBox creerSection(String nom, String nomClasseCSS) {
        VBox vb = new VBox(5);
        Label l = new Label(nom);
        l.getStyleClass().add(nomClasseCSS);
        vb.getChildren().add(l);
        return vb;
    }

    private Button creerItem(String nom, String imgNom, String prix) {
        VBox boiteInterieure = new VBox(5);
        boiteInterieure.setAlignment(Pos.CENTER);

        String cheminImg = "/universite_paris8/iut/aboudhan/saes2javafx/vue/" + imgNom;
        InputStream stream = getClass().getResourceAsStream(cheminImg);

        ImageView image = new ImageView();
        if (stream != null) {
            image.setImage(new Image(stream));
        }

        image.setFitHeight(40);
        image.setFitWidth(40);
        image.setPreserveRatio(true);

        Label labelNom = new Label(nom);
        labelNom.getStyleClass().add("nom-item-shop");

        Label labelPrix = new Label(prix);
        labelPrix.getStyleClass().add("prix-item-shop");

        boiteInterieure.getChildren().addAll(image, labelNom, labelPrix);

        Button boutonItem = new Button();
        boutonItem.setGraphic(boiteInterieure);
        boutonItem.setPrefSize(110, 110);
        boutonItem.getStyleClass().add("btn-item-shop");

        return boutonItem;
    }

    public void afficherSur(Pane parent) {
        if (!parent.getChildren().isEmpty()) {
            parent.getChildren().get(0).setEffect(new javafx.scene.effect.BoxBlur(5, 5, 3));
        }
        if (!parent.getChildren().contains(rootShop)) {
            parent.getChildren().add(rootShop);
        }
    }

    public void cacher(Pane parent) {
        if (!parent.getChildren().isEmpty()) {
            parent.getChildren().get(0).setEffect(null);
        }
        parent.getChildren().remove(rootShop);
    }
}
