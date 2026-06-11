package universite_paris8.iut.aboudhan.saes2javafx.vue;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

public class InfoVue {
    private final StackPane calqueFond;
    private final Label contenu;
    private final Button btnPrecedent;
    private final Button btnSuivant;
    private final Button btnFermer;

    public InfoVue(String texteInitial, boolean premierePage, boolean dernierePage,
                   Runnable onPrecedent, Runnable onSuivant, Runnable onClose) {
        calqueFond = new StackPane();
        calqueFond.getStyleClass().add("fond-flou-shop");
        calqueFond.setPrefSize(1020, 680);

        VBox fenetre = new VBox(20);
        fenetre.getStyleClass().add("fenetre-shop");
        fenetre.setMaxSize(450, 400);
        fenetre.setAlignment(Pos.CENTER);

        Label titre = new Label("GUIDE DE SURVIE");
        titre.getStyleClass().add("titre-shop");

        contenu = new Label(texteInitial);
        contenu.getStyleClass().add("label-guide-contenu");
        contenu.setPrefWidth(380);

        HBox navigation = new HBox(20);
        navigation.setAlignment(Pos.CENTER);

        btnPrecedent = new Button("<");
        btnSuivant = new Button(">");
        btnPrecedent.getStyleClass().add("btn-changer-page");
        btnSuivant.getStyleClass().add("btn-changer-page");

        btnFermer = new Button("Compris !");
        btnFermer.getStyleClass().add("btn-fermer-shop");

        btnPrecedent.setDisable(premierePage);
        btnSuivant.setDisable(dernierePage);
        btnFermer.setDisable(!dernierePage);

        btnPrecedent.setOnAction(e -> onPrecedent.run());
        btnSuivant.setOnAction(e -> onSuivant.run());
        btnFermer.setOnAction(e -> onClose.run());

        navigation.getChildren().addAll(btnPrecedent, btnSuivant);
        fenetre.getChildren().addAll(titre, contenu, navigation, btnFermer);
        calqueFond.getChildren().add(fenetre);
    }

    public void rafraichirPage(String texte, boolean estPremiere, boolean estDerniere) {
        this.contenu.setText(texte);
        this.btnPrecedent.setDisable(estPremiere);
        this.btnSuivant.setDisable(estDerniere);
        this.btnFermer.setDisable(!estDerniere);
    }

    public void afficherSur(Pane conteneur) { conteneur.getChildren().add(calqueFond); }
    public void cacher(Pane conteneur) { conteneur.getChildren().remove(calqueFond); }
}