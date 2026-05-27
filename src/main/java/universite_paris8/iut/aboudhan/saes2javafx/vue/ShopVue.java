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

    private StackPane rootShop; // Conteneur global (fond flou + menu)
    private VBox menuInterieur;
    private Runnable actionFermer;

    public ShopVue(Runnable actionFermer) {
        this.actionFermer = actionFermer;
        creerInterface();
    }

    private void creerInterface() {
        // Fond transparent de la scène
        rootShop = new StackPane();
        rootShop.setPrefSize(1020, 680);
        rootShop.getStyleClass().add("fond-flou-shop");

        // Fenêtre du menu central
        menuInterieur = new VBox(20);
        menuInterieur.setAlignment(Pos.CENTER);
        menuInterieur.setMaxSize(850, 550); // Légèrement agrandi pour accueillir 4 tours de large
        menuInterieur.getStyleClass().add("fenetre-shop");

        // titre + bouton fermer
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_RIGHT);

        Label titre = new Label("SHOP du LABORATOIRE");
        titre.getStyleClass().add("titre-shop");

        Button btnFermer = new Button("X");
        btnFermer.getStyleClass().add("btn-fermer-shop");
        btnFermer.setPrefSize(35, 35);

        // CORRECTION 2 : Quand on clique sur X, le shop s'enlève de l'écran TOUT SEUL, puis relance le jeu
        btnFermer.setOnAction(e -> {
            Pane parent = (Pane) rootShop.getParent();
            if (parent != null) {
                cacherDe(parent);
            }
            actionFermer.run();
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(titre, spacer, btnFermer);

        // Section 1 : Les Tours
        VBox sectionTours = creerSection("TOURS DE DÉFENSE", "titre-categorie-tours");
        HBox containerTours = new HBox(15);
        containerTours.setAlignment(Pos.CENTER);
        containerTours.getChildren().addAll(
                creerItem("Le Scientifique", "tour_scientifique.png", "100$"),
                creerItem("Le Chimiste", "tour_chimiste.png", "200$"),
                creerItem("Le Rayon X", "rayon_x.png", "250$"),
                creerItem("Le Scanner", "scanner.png", "250$")
        );

        // Section 2 : Les Potions
        VBox sectionPotions = creerSection("POTIONS", "titre-categorie-potions");
        HBox containerPotions = new HBox(15);
        containerPotions.setAlignment(Pos.CENTER);
        containerPotions.getChildren().addAll(
                creerItem("Rage", "potion_rage.png", "50$"),
                creerItem("Soin", "potion_soin.png", "80$"),
                creerItem("Gel", "potion_gel.png", "30$")
        );

        // Assemblage final
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
        // Évite d'ajouter deux fois le shop si on clique comme un fou
        if (!parent.getChildren().contains(rootShop)) {
            parent.getChildren().add(rootShop);
        }
    }

    public void cacherDe(Pane parent) {
        if (!parent.getChildren().isEmpty()) {
            parent.getChildren().get(0).setEffect(null); // Retire le flou
        }
        parent.getChildren().remove(rootShop);
    }
}