package universite_paris8.iut.aboudhan.saes2javafx.vue.tour;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import universite_paris8.iut.aboudhan.saes2javafx.modele.tour.Tour;

public class PanneauActionVue extends VBox {

    private final Button btnAmeliorer;
    private final Button btnRanger;
    private final Button btnVendre;
    private final Button btnFermer;
    private final Button fauxbtn;
    private final Label labelInfo;

    public PanneauActionVue() {
        this.getStyleClass().add("panneau-action-tour");
        this.setAlignment(Pos.CENTER);
        this.setSpacing(10);
        this.setVisible(false);

        btnFermer = new Button("X");
        btnFermer.getStyleClass().add("btn-action-fermer");

        fauxbtn = new Button("X");
        fauxbtn.getStyleClass().add("faux-btn");

        labelInfo = new Label();
        labelInfo.getStyleClass().add("panneau-action-titre");
        labelInfo.setAlignment(Pos.CENTER);

        BorderPane ligneEntete = new BorderPane();
        ligneEntete.setLeft(fauxbtn);
        ligneEntete.setCenter(labelInfo);
        ligneEntete.setRight(btnFermer);

        BorderPane.setAlignment(fauxbtn, Pos.CENTER);
        BorderPane.setAlignment(btnFermer, Pos.CENTER);

        btnAmeliorer = new Button();
        btnAmeliorer.getStyleClass().add("btn-action-ameliorer");

        btnRanger = new Button();
        btnRanger.getStyleClass().add("btn-action-ranger");

        btnVendre = new Button();
        btnVendre.getStyleClass().add("btn-action-vendre");

        HBox conteneurBoutons = new HBox(10, btnAmeliorer, btnRanger, btnVendre);
        conteneurBoutons.setAlignment(Pos.CENTER);

        this.getChildren().addAll(ligneEntete, conteneurBoutons);
    }

    public void actualiser(Tour tour, boolean estPosee) {
        this.setVisible(true);
        String nomTour = tour.getClass().getSimpleName().replace("Tour", "");
        nomTour = nomTour.toUpperCase();

        if(tour.getNiveau() == 5)
            labelInfo.setText(nomTour + " - LEVEL MAX");
        else
            labelInfo.setText(nomTour + " - LEVEL " + tour.getNiveau());

        if (tour.peutEtreAmelioree()) {
            btnAmeliorer.setText("Améliorer (-" + tour.calculerPrixAmelioration() + "$)");
            btnAmeliorer.setDisable(false);
        } else {
            btnAmeliorer.setText("Niveau MAX (5)");
            btnAmeliorer.setDisable(true);
        }

        if (estPosee)
            btnRanger.setText("Ranger");
        else
            btnRanger.setText("Poser");

        btnVendre.setText("Vendre (+" + tour.calculerValeurVente() + "$)");
    }

    public Button getBtnAmeliorer() { return btnAmeliorer; }
    public Button getBtnRanger() { return btnRanger; }
    public Button getBtnVendre() { return btnVendre; }
    public Button getBtnFermer() { return btnFermer; }
}