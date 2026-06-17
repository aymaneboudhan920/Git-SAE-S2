package universite_paris8.iut.aboudhan.saes2javafx.vue.bouton;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.io.InputStream;
import java.util.function.Consumer;

public class ShopVue {

    private StackPane rootShop;
    private VBox menuInterieur;
    private Runnable actionFermer;
    private Consumer<String> actionClicItem;

    public ShopVue(Runnable actionFermer, Consumer<String> actionClicItem, int numVagueActuelle, int argentActuel,
                   int prixScientifique, int prixChimiste, int prixScanner, int prixRayonX, int prixSoin, int prixRage, int prixGel) {
        this.actionFermer = actionFermer;
        this.actionClicItem = actionClicItem;
        creerInterface(numVagueActuelle, argentActuel, prixScientifique, prixChimiste, prixScanner, prixRayonX, prixSoin, prixRage, prixGel);
    }

    private void creerInterface(int numVagueActuelle, int argentActuel, int prixScientifique, int prixChimiste, int prixScanner, int prixRayonX, int prixSoin, int prixRage, int prixGel) {
        rootShop = new StackPane();
        rootShop.setPrefSize(1020, 680);
        rootShop.getStyleClass().add("fond-flou-shop");

        menuInterieur = new VBox(20);
        menuInterieur.setAlignment(Pos.CENTER);
        menuInterieur.setMaxSize(850, 550);
        menuInterieur.getStyleClass().add("fenetre");

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_RIGHT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label titre = new Label("SHOP du LABORATOIRE");
        titre.getStyleClass().add("titre");

        Button btnFermer = new Button("X");
        btnFermer.getStyleClass().add("btn-fermer");
        btnFermer.setPrefSize(35, 35);

        btnFermer.setOnAction(e -> {
            actionFermer.run();
        });

        header.getChildren().addAll(titre, spacer, btnFermer);

        VBox sectionTours = creerSection("TOURS DE DÉFENSE", "titre-categorie-tours");
        HBox containerTours = new HBox(15);
        containerTours.setAlignment(Pos.CENTER);

        Button btnScientifique = creerItem("Scientifique", "tour_scientifique.png", prixScientifique + "$");
        btnScientifique.setOnAction(e -> declencherAchat("scientifique"));
        StackPane conteneurScientifique = configurerBlocage(btnScientifique, "scientifique", numVagueActuelle, 1, argentActuel, prixScientifique);

        Button btnChimiste = creerItem("Chimiste", "tour_chimiste.png", prixChimiste + "$");
        btnChimiste.setOnAction(e -> declencherAchat("chimiste"));
        StackPane conteneurChimiste = configurerBlocage(btnChimiste, "chimiste", numVagueActuelle, 1, argentActuel, prixChimiste);

        Button btnScanner = creerItem("Scanner", "tour_scanner.png", prixScanner + "$");
        btnScanner.setOnAction(e -> declencherAchat("scanner"));
        StackPane conteneurScanner = configurerBlocage(btnScanner, "scanner", numVagueActuelle, 3, argentActuel, prixScanner);

        Button btnRayonX = creerItem("Rayon_X", "tour_rayon_x.png", prixRayonX + "$");
        btnRayonX.setOnAction(e -> declencherAchat("rayon_x"));
        StackPane conteneurRayonX = configurerBlocage(btnRayonX, "rayon_x", numVagueActuelle, 5, argentActuel, prixRayonX);

        containerTours.getChildren().addAll(conteneurScientifique, conteneurChimiste, conteneurScanner, conteneurRayonX);

        VBox sectionPotions = creerSection("POTIONS", "titre-categorie-potions");
        HBox containerPotions = new HBox(15);
        containerPotions.setAlignment(Pos.CENTER);

        Button btnSoin = creerItem("Soin", "potion_soin.png", prixSoin + "$");
        btnSoin.setOnAction(e -> declencherAchat("potion_soin"));
        StackPane conteneurSoin = configurerBlocage(btnSoin, "potion_soin", numVagueActuelle, 3, argentActuel, prixSoin);

        Button btnRage = creerItem("Rage", "potion_rage.png", prixRage + "$");
        btnRage.setOnAction(e -> declencherAchat("potion_rage"));
        StackPane conteneurRage = configurerBlocage(btnRage, "potion_rage", numVagueActuelle, 5, argentActuel, prixRage);

        Button btnGel = creerItem("Gel", "potion_gel.png", prixGel + "$");
        btnGel.setOnAction(e -> declencherAchat("potion_gel"));
        StackPane conteneurGel = configurerBlocage(btnGel, "potion_gel", numVagueActuelle, 7, argentActuel, prixGel);

        containerPotions.getChildren().addAll(conteneurSoin, conteneurRage, conteneurGel);

        menuInterieur.getChildren().addAll(header, sectionTours, containerTours, sectionPotions, containerPotions);
        rootShop.getChildren().add(menuInterieur);
    }

    private StackPane configurerBlocage(Button bouton, String typeItem, int vagueActuelle, int vagueRequise, int argentActuel, int prixItem) {
        StackPane conteneur = new StackPane();
        conteneur.getChildren().add(bouton);

        boolean estUneTour = typeItem.equals("scientifique") ||
                typeItem.equals("chimiste") ||
                typeItem.equals("scanner") ||
                typeItem.equals("rayon_x");

        String cssBoutonSpecifique = estUneTour ? "btn-tour-shop" : "btn-potion-shop";
        String cssTexteSpecifique  = estUneTour ? "texte-tour-bloquee" : "texte-potion-bloquee";

        if (vagueActuelle < vagueRequise) {
            bouton.setDisable(true);
            bouton.getStyleClass().addAll("btn-item-shop", cssBoutonSpecifique);

            Label txtBloque = new Label("VAGUE " + vagueRequise);
            txtBloque.getStyleClass().addAll("texte-bloquee", cssTexteSpecifique);
            txtBloque.setRotate(-30);
            txtBloque.setMouseTransparent(true);
            conteneur.getChildren().add(txtBloque);
        }
        else if (argentActuel < prixItem) {
            bouton.setDisable(true);
            bouton.getStyleClass().addAll("btn-item-shop", cssBoutonSpecifique);

            Label txtNoMoney = new Label("NO CASH");
            txtNoMoney.getStyleClass().addAll("texte-bloquee", "texte-nomoney-bloquee");
            txtNoMoney.setRotate(-30);
            txtNoMoney.setMouseTransparent(true);
            conteneur.getChildren().add(txtNoMoney);
        }
        else {
            bouton.getStyleClass().addAll("btn-item-shop", cssBoutonSpecifique);
        }

        return conteneur;
    }

    private void declencherAchat(String typeItem) {
        if (actionClicItem != null) {
            actionClicItem.accept(typeItem);
        }
    }

    private VBox creerSection(String nom, String nomClasseCSS) {
        VBox vb = new VBox(5);
        Label titre = new Label(nom);
        titre.getStyleClass().add(nomClasseCSS);
        vb.getChildren().add(titre);
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
