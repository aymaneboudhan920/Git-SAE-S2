package universite_paris8.iut.aboudhan.saes2javafx.vue;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.effect.BoxBlur;
import java.io.InputStream;

public class ShopVue {

    private StackPane rootShop;
    private VBox menuInterieur;
    private Runnable actionFermer;
    private Runnable actionClicScientifique;

    public ShopVue(Runnable actionFermer, Runnable actionClicScientifique) {
        this.actionFermer = actionFermer;
        this.actionClicScientifique = actionClicScientifique;
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

        Label titre = new Label("SHOP du LABORATOIRE");
        titre.getStyleClass().add("titre-shop");

        Button btnFermer = new Button("X");
        btnFermer.getStyleClass().add("btn-fermer-shop");
        btnFermer.setPrefSize(35, 35);

        btnFermer.setOnAction(e -> {
            Pane parent = (Pane) rootShop.getParent();
            if (parent != null) {
                cacher(parent);
            }
            actionFermer.run();
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(titre, spacer, btnFermer);

        // Les Tours
        VBox sectionTours = creerSection("TOURS DE DÉFENSE", "titre-categorie-tours");
        HBox containerTours = new HBox(15);
        containerTours.setAlignment(Pos.CENTER);

        Button btnScientifique = creerItem("Scientifique", "tour_scientifique.png", "100$");
        btnScientifique.setOnAction(e -> {
            Pane parent = (Pane) rootShop.getParent();
            if (parent != null) {
                cacher(parent);
            }
            if (actionClicScientifique != null) {
                actionClicScientifique.run();
            } else {
                System.out.println("Erreur : l'action clic scientifique est null dans ShopVue !");
            }
        });

        containerTours.getChildren().addAll(
                btnScientifique,
                creerItem("Chimiste", "tour_chimiste.png", "200$"),
                creerItem("Rayon_X", "rayon_x.png", "250$"),
                creerItem("Scanner", "scanner.png", "250$")
        );

        // Les Potions
        VBox sectionPotions = creerSection("POTIONS", "titre-categorie-potions");
        HBox containerPotions = new HBox(15);
        containerPotions.setAlignment(Pos.CENTER);
        containerPotions.getChildren().addAll(
                creerItem("Rage", "potion_rage.png", "50$"),
                creerItem("Soin", "potion_soin.png", "80$"),
                creerItem("Gel", "potion_gel.png", "30$")
        );

        menuInterieur.getChildren().addAll(header, sectionTours, containerTours, sectionPotions, containerPotions);
        rootShop.getChildren().add(menuInterieur);
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

        ImageView iv = new ImageView();
        if (stream != null) {
            iv.setImage(new Image(stream));
        } else {
            System.out.println("Image introuvable : " + cheminImg + " ! ");
        }

        iv.setFitHeight(40);
        iv.setFitWidth(40);
        iv.setPreserveRatio(true);

        Label lblNom = new Label(nom);
        lblNom.getStyleClass().add("nom-item-shop");

        Label lblPrix = new Label(prix);
        lblPrix.getStyleClass().add("prix-item-shop");

        boiteInterieure.getChildren().addAll(iv, lblNom, lblPrix);

        Button boutonItem = new Button();
        boutonItem.setGraphic(boiteInterieure);
        boutonItem.setPrefSize(110, 110);
        boutonItem.getStyleClass().add("btn-item-shop");

        return boutonItem;
    }

    public void afficherSur(Pane parent) {
        if (!parent.getChildren().isEmpty()) {
            parent.getChildren().get(0).setEffect(new BoxBlur(5, 5, 3));
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
