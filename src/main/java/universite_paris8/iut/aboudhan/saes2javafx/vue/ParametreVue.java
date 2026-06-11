package universite_paris8.iut.aboudhan.saes2javafx.vue;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.function.Consumer;

public class ParametreVue {
    private final StackPane calqueFond;

    public ParametreVue(double volumeMusiqueInitial, double volumeBruitagesInitial,
                        Consumer<Double> onMusiqueChange, Consumer<Double> onBruitagesChange,
                        Runnable onClose) {

        calqueFond = new StackPane();
        calqueFond.getStyleClass().add("fond-flou-shop");
        calqueFond.setPrefSize(1020, 680);

        VBox fenetre = new VBox(20);
        fenetre.getStyleClass().add("fenetre-shop");
        fenetre.setMaxSize(400, 350);
        fenetre.setAlignment(Pos.CENTER);

        Label titre = new Label("PARAMÈTRES");
        titre.getStyleClass().add("titre-shop");

        Label labelMusique = new Label("Volume Musique");
        labelMusique.getStyleClass().add("label-popup");

        Slider sliderMusique = new Slider(0, 1, volumeMusiqueInitial);
        sliderMusique.valueProperty().addListener((obs, old, newVal) -> onMusiqueChange.accept(newVal.doubleValue()));
        sliderMusique.getStyleClass().add("slider");

        Label labelEffets = new Label("Volume Effets");
        labelEffets.getStyleClass().add("label-popup");

        Slider sliderBruitages = new Slider(0, 1, volumeBruitagesInitial);
        sliderBruitages.valueProperty().addListener((obs, old, newVal) -> onBruitagesChange.accept(newVal.doubleValue()));
        sliderBruitages.getStyleClass().add("slider");

        Button btnFermer = new Button("Fermer");
        btnFermer.getStyleClass().add("btn-fermer-shop");
        btnFermer.setOnAction(e -> onClose.run());

        fenetre.getChildren().addAll(titre, labelMusique, sliderMusique, labelEffets, sliderBruitages, btnFermer);
        calqueFond.getChildren().add(fenetre);
    }

    public void afficherSur(Pane conteneur) { conteneur.getChildren().add(calqueFond); }
    public void cacher(Pane conteneur) { conteneur.getChildren().remove(calqueFond); }
}