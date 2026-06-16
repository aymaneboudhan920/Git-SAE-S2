package universite_paris8.iut.aboudhan.saes2javafx.modele;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import universite_paris8.iut.aboudhan.saes2javafx.modele.jeu.Waypoint;

import java.util.List;

public class DidactitielVisuel {

    private final Pane racineGlobale;
    private Pane masqueSombre;
    private final Pane grilleJeu;
    private IControleurTuto controller;

    public DidactitielVisuel(Pane racineGlobale, Pane grilleJeu) {
        this.racineGlobale = racineGlobale;
        this.grilleJeu = grilleJeu;
    }

    public void demarrerSiConfirme(Button boutonStart, IControleurTuto ctrl, Runnable callbackNon) {
        this.controller = ctrl;

        double largeurEcran = racineGlobale.getWidth();
        double hauteurEcran = racineGlobale.getHeight();

        masqueSombre = new Pane();
        masqueSombre.setPrefSize(largeurEcran, hauteurEcran);
        masqueSombre.getStyleClass().add("masque-sombre-tuto");
        masqueSombre.setPickOnBounds(false);

        VBox popupQuestion = new VBox();
        popupQuestion.getStyleClass().add("tuto-popup-question");
        popupQuestion.setPrefWidth(400);

        Label labelQuestion = new Label("VOULEZ-VOUS FAIRE\nLE DIDACTICIEL ?");
        labelQuestion.getStyleClass().add("tuto-titre-question");

        HBox conteneurBoutons = new HBox();
        conteneurBoutons.getStyleClass().add("tuto-box-boutons");

        Button btnOui = new Button("OUI");
        btnOui.getStyleClass().add("btn-tuto-oui");

        Button btnNon = new Button("NON");
        btnNon.getStyleClass().add("btn-tuto-non");

        btnNon.setOnAction(e -> {
            finDidacticiel();
            if (callbackNon != null) callbackNon.run();
        });

        btnOui.setOnAction(e -> {
            masqueSombre.getChildren().remove(popupQuestion);
            // On enregistre this comme didacticiel actif SEULEMENT maintenant
            this.controller.setDidacticielActif(this);
            this.controller.reinitialiserPourTuto();
            etapeBoutonStart(boutonStart);
        });

        conteneurBoutons.getChildren().addAll(btnOui, btnNon);
        popupQuestion.getChildren().addAll(labelQuestion, conteneurBoutons);

        popupQuestion.setLayoutX((largeurEcran - 400) / 2);
        popupQuestion.setLayoutY((hauteurEcran - 180) / 2);

        masqueSombre.getChildren().add(popupQuestion);
        racineGlobale.getChildren().add(masqueSombre);
    }

    private void etapeBoutonStart(Button boutonStart) {
        if (this.controller != null) {
            this.controller.setBoutonStartDisable(false);
            this.controller.setBoutonShopDisable(true);
            this.controller.setBtnPotionSoinDisable(true);
            this.controller.setBtnPotionRageDisable(true);
            this.controller.setBtnPotionGelDisable(true);
            this.controller.setBoutonTutoDisable(true);
            this.controller.setBoutonParametresDisable(true);
            this.controller.setBoutonInfoDisable(true);
            for (Button btn : this.controller.getBoutonsInventaire()) {
                if (btn != null) btn.setDisable(true);
            }
        }
        mettreEnValeurElement(boutonStart, "Appuyez sur le bouton START pour lancer la partie !", false, null);
    }

    public void etapeMontrerEntree(Waypoint depart, IControleurTuto ctrl) {
        this.controller = ctrl;
        this.controller.setBoutonStartDisable(true);
        this.controller.setBoutonShopDisable(true);
        this.controller.setBtnPotionSoinDisable(true);
        this.controller.setBtnPotionRageDisable(true);
        this.controller.setBtnPotionGelDisable(true);
        for (Button btn : this.controller.getBoutonsInventaire()) {
            if (btn != null) btn.setDisable(true);
        }
        Point2D posEntree = calculerPositionCase((int) depart.getX()/34, (int) depart.getY()/34);
        Rectangle zoneEntree = new Rectangle(posEntree.getX(), posEntree.getY(), 34, 34);
        String textExplicaif = "C'est par ici que les microbes vont apparaître (Spawn) ! Préparez vos défenses.";

        mettreEnValeurForme(zoneEntree, textExplicaif, true, () -> {
            etapeMontrerSortie(0, 4);
        }, posEntree.getX() - 285, posEntree.getY() - 40);
    }

    public void etapeMontrerSortie(int x, int y) {
        if (masqueSombre != null && racineGlobale != null && !racineGlobale.getChildren().contains(masqueSombre)) {
            racineGlobale.getChildren().add(masqueSombre);
        }

        if (this.controller != null) {
            this.controller.setBoutonStartDisable(true);
            this.controller.setBoutonShopDisable(true);
            this.controller.setBtnPotionSoinDisable(true);
            this.controller.setBtnPotionRageDisable(true);
            this.controller.setBtnPotionGelDisable(true);
            this.controller.setBoutonTutoDisable(true);
            this.controller.setBoutonParametresDisable(true);
            this.controller.setBoutonInfoDisable(true);
            for (Button btn : this.controller.getBoutonsInventaire()) {
                if (btn != null) btn.setDisable(true);
            }
        }

        Point2D posSortie = calculerPositionCase(x, y);

        Rectangle zoneSortie = new Rectangle(posSortie.getX(), posSortie.getY(), 34, 34);
        zoneSortie.setArcWidth(10);
        zoneSortie.setArcHeight(10);

        String texteExplicatif = "C'est ici que se trouve votre base ! Si les microbes l'atteignent, des innocents seront infectés ! Protégez-la à tout prix !";

        mettreEnValeurForme(zoneSortie, texteExplicatif, true, () -> {
            masqueSombre.getChildren().clear();
            if (this.controller != null) {
                this.controller.declencherSpawnPremierMicrobe();
            }
        }, posSortie.getX() + 50, posSortie.getY() - 40);
    }

    private void mettreEnValeurElement(Button element, String texte, boolean avecBoutonOk, Runnable actionOk) {
        Point2D pos = element.localToScene(0, 0);
        Rectangle forme = new Rectangle(pos.getX(), pos.getY(), element.getWidth(), element.getHeight());
        genererMasqueEtBulle(forme, texte, avecBoutonOk, actionOk, pos.getX() - 270, pos.getY());
    }

    private void mettreEnValeurForme(Rectangle forme, String texte, boolean avecBoutonOk, Runnable actionOk, double xBulle, double yBulle) {
        genererMasqueEtBulle(forme, texte, avecBoutonOk, actionOk, xBulle, yBulle);
    }

    private void genererMasqueEtBulle(Rectangle formeTrou, String texte, boolean avecBoutonOk, Runnable actionOk, double xBulle, double yBulle) {
        double largeurEcran = racineGlobale.getWidth();
        double hauteurEcran = racineGlobale.getHeight();

        Rectangle fondTotal = new Rectangle(largeurEcran, hauteurEcran);
        Shape masqueAvecTrou = Shape.subtract(fondTotal, formeTrou);
        masqueAvecTrou.setFill(Color.web("#000000", 0.75));

        masqueSombre.getChildren().clear();
        masqueSombre.setPickOnBounds(false);

        Pane calqueMasque = new Pane(masqueAvecTrou);
        calqueMasque.setPickOnBounds(false);

        masqueSombre.getChildren().add(calqueMasque);

        VBox bulleAide = new VBox();
        bulleAide.getStyleClass().add("tuto-bulle");
        bulleAide.setPickOnBounds(true);

        Label labelTexte = new Label(texte);
        labelTexte.getStyleClass().add("tuto-texte");
        labelTexte.setPrefWidth(220);
        labelTexte.setWrapText(true);
        bulleAide.getChildren().add(labelTexte);

        if (avecBoutonOk) {
            Button btnOk = new Button("OK, J'AI COMPRIS");
            btnOk.getStyleClass().addAll("tuto-btn-ok", "tuto-btn-ok-margin");
            btnOk.setOnAction(e -> actionOk.run());
            bulleAide.getChildren().add(btnOk);
        }

        bulleAide.setLayoutX(Math.max(10, Math.min(xBulle, largeurEcran - 240)));
        bulleAide.setLayoutY(Math.max(10, Math.min(yBulle, hauteurEcran - 150)));

        masqueSombre.getChildren().add(bulleAide);
    }

    private void genererMessageFinal(String texteFin, Runnable actionOk) {
        double largeurEcran = racineGlobale.getWidth();
        double hauteurEcran = racineGlobale.getHeight();

        masqueSombre.getChildren().clear();
        masqueSombre.setMouseTransparent(false); // Bloque l'interface générale pour forcer le clic sur TERMINER

        // Un masque sombre plein (sans trou) pour se concentrer sur la fin
        Rectangle fondTotal = new Rectangle(largeurEcran, hauteurEcran);
        fondTotal.setFill(Color.web("#000000", 0.75));
        masqueSombre.getChildren().add(fondTotal);

        VBox bulleFin = new VBox();
        bulleFin.getStyleClass().add("tuto-bulle");
        bulleFin.setPickOnBounds(true);

        Label labelTexte2 = new Label(texteFin);
        labelTexte2.getStyleClass().add("tuto-texte");
        labelTexte2.setPrefWidth(240);
        labelTexte2.setWrapText(true);
        bulleFin.getChildren().add(labelTexte2);

        Button btnOk = new Button("TERMINER !");
        btnOk.getStyleClass().addAll("tuto-btn-ok", "tuto-btn-ok-margin");
        btnOk.setOnAction(e -> actionOk.run());
        bulleFin.getChildren().add(btnOk);

        bulleFin.setLayoutX((largeurEcran - 300) / 2);
        bulleFin.setLayoutY((hauteurEcran - 200) / 2);

        masqueSombre.getChildren().add(bulleFin);
    }

    private Point2D calculerPositionCase(int x, int y) {
        Point2D positionGrille = grilleJeu.localToScene(0, 0);
        double tailleCaseX = 34;
        double tailleCaseY = 34;
        return new Point2D(positionGrille.getX() + (x * tailleCaseX), positionGrille.getY() + (y * tailleCaseY));
    }

    public void etapeMontrerPremierMicrobe(javafx.scene.Node visuelMicrobe) {
        if (this.controller != null) {
            this.controller.setBoutonStartDisable(true);
            this.controller.setBoutonShopDisable(true);
            this.controller.setBtnPotionSoinDisable(true);
            this.controller.setBtnPotionRageDisable(true);
            this.controller.setBtnPotionGelDisable(true);
            this.controller.setBoutonTutoDisable(true);
            this.controller.setBoutonParametresDisable(true);
            this.controller.setBoutonInfoDisable(true);
            for (Button btn : this.controller.getBoutonsInventaire()) {
                if (btn != null) btn.setDisable(true);
            }
        }
        Point2D posMicrobe = visuelMicrobe.localToScene(0, 0);
        Rectangle zoneMicrobe = new Rectangle(posMicrobe.getX(), posMicrobe.getY(), 34, 34);
        String texteExplicatif = "Attention ! Un microbe vient d'entrer dans le laboratoire. Il va suivre le chemin pour tenter de s'échapper par la sortie !";

        // Clic sur le bouton "OK, J'AI COMPRIS"
        mettreEnValeurForme(zoneMicrobe, texteExplicatif, true, () -> {
            masqueSombre.getChildren().clear();

            if (this.controller != null) {
                // Maintien du blocage absolu pendant que le microbe court
                this.controller.setBoutonStartDisable(true);
                this.controller.setBoutonShopDisable(true);
                this.controller.setBtnPotionSoinDisable(true);
                this.controller.setBtnPotionRageDisable(true);
                this.controller.setBtnPotionGelDisable(true);
                this.controller.setBoutonTutoDisable(true);
                this.controller.setBoutonParametresDisable(true);
                this.controller.setBoutonInfoDisable(true);
                for (Button btn : this.controller.getBoutonsInventaire()) {
                    if (btn != null) btn.setDisable(true);
                }
                this.controller.relancerJeuTemporairementTuto();
            }

            javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(2));
            pause.setOnFinished(event -> {
                if (this.controller != null) {
                    this.controller.pauserJeuTuto();
                    this.etapeMontrerShop(this.controller.getBoutonShop());
                }
            });
            pause.play();
        }, posMicrobe.getX() - 280, posMicrobe.getY() - 20);
    }

    public void etapeMontrerShop(javafx.scene.Node boutonShop) {
        if (this.controller != null) {
            this.controller.setBoutonStartDisable(true);
            this.controller.setBoutonShopDisable(false); // Réactivé !
            this.controller.setBtnPotionSoinDisable(true);
            this.controller.setBtnPotionRageDisable(true);
            this.controller.setBtnPotionGelDisable(true);
            this.controller.setBoutonTutoDisable(true);
            this.controller.setBoutonParametresDisable(true);
            this.controller.setBoutonInfoDisable(true);
            for (Button btn : this.controller.getBoutonsInventaire()) {
                if (btn != null) btn.setDisable(true);
            }
        }
        Point2D posShop = boutonShop.localToScene(0, 0);
        Rectangle zoneShop = new Rectangle(posShop.getX() , posShop.getY(), 138, 150);
        String texteExplicatif = "Pour vous défendre, vous allez avoir besoin de tours de défense ! Cliquez sur le bouton SHOP pour ouvrir la boutique.";

        boutonShop.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, new javafx.event.EventHandler<javafx.scene.input.MouseEvent>() {
            @Override
            public void handle(javafx.scene.input.MouseEvent event) {
                boutonShop.removeEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, this);
                masqueSombre.getChildren().clear();
            }
        });

        mettreEnValeurForme(zoneShop, texteExplicatif, false, null, posShop.getX() - 275, posShop.getY() + 10);
    }

    public void etapeMontrerScientifiqueDansShop(javafx.scene.Node boutonScientifique) {
        if (racineGlobale != null && !racineGlobale.getChildren().contains(masqueSombre)) {
            racineGlobale.getChildren().add(masqueSombre);
        }

        if (this.controller != null) {
            this.controller.setBoutonStartDisable(true);
            this.controller.setBoutonShopDisable(true);
            this.controller.setBtnPotionSoinDisable(true);
            this.controller.setBtnPotionRageDisable(true);
            this.controller.setBtnPotionGelDisable(true);
            this.controller.setBoutonTutoDisable(true);
            this.controller.setBoutonParametresDisable(true);
            this.controller.setBoutonInfoDisable(true);
            for (Button btn : this.controller.getBoutonsInventaire()) {
                if (btn != null) btn.setDisable(true);
            }
            boutonScientifique.setDisable(false);
        }

        masqueSombre.getChildren().clear();

        javafx.application.Platform.runLater(() -> {
            if (boutonScientifique.getScene() == null) return;

            javafx.geometry.Point2D posAbsolue = boutonScientifique.localToScene(0, 0);
            double xAbsolu = posAbsolue.getX();
            double yAbsolu = posAbsolue.getY();
            double largeur = boutonScientifique.getBoundsInLocal().getWidth();
            double hauteur = boutonScientifique.getBoundsInLocal().getHeight();

            masqueSombre.toFront();

            double largeurEcran = racineGlobale.getWidth();
            double hauteurEcran = racineGlobale.getHeight();

            Rectangle fondTotal = new Rectangle(largeurEcran, hauteurEcran);
            Rectangle zoneScientifique = new Rectangle(xAbsolu, yAbsolu, largeur, hauteur);
            zoneScientifique.setArcWidth(15);
            zoneScientifique.setArcHeight(15);

            Shape masqueAvecTrou = Shape.subtract(fondTotal, zoneScientifique);
            masqueAvecTrou.setFill(Color.web("#000000", 0.75));

            Pane calqueMasque = new Pane(masqueAvecTrou);
            calqueMasque.setPickOnBounds(false);
            masqueSombre.getChildren().add(calqueMasque);

            VBox bulleAide = new VBox();
            bulleAide.getStyleClass().add("tuto-bulle");
            bulleAide.setPickOnBounds(true);

            Label labelTexte = new Label("Cliquez ici pour acheter le Scientifique ! C'est votre premier unité de défense.");
            labelTexte.getStyleClass().add("tuto-texte");
            labelTexte.setPrefWidth(220);
            labelTexte.setWrapText(true);
            bulleAide.getChildren().add(labelTexte);

            bulleAide.setLayoutX(Math.max(10, xAbsolu + 125));
            bulleAide.setLayoutY(Math.max(10, yAbsolu));
            masqueSombre.getChildren().add(bulleAide);

            Rectangle zoneCliquableTuto = new Rectangle(xAbsolu, yAbsolu, largeur, hauteur);
            zoneCliquableTuto.setFill(Color.TRANSPARENT);
            zoneCliquableTuto.setCursor(javafx.scene.Cursor.HAND);
            masqueSombre.getChildren().add(zoneCliquableTuto);

            zoneCliquableTuto.setOnMouseClicked(event -> {
                zoneCliquableTuto.setDisable(true);
                masqueSombre.getChildren().clear();

                if (boutonScientifique instanceof javafx.scene.control.Button) {
                    ((javafx.scene.control.Button) boutonScientifique).fire();
                } else {
                    if (boutonScientifique.getOnMouseClicked() != null) {
                        boutonScientifique.getOnMouseClicked().handle(event);
                    } else {
                        boutonScientifique.fireEvent(new javafx.scene.input.MouseEvent(
                                javafx.scene.input.MouseEvent.MOUSE_CLICKED,
                                0, 0, 0, 0, javafx.scene.input.MouseButton.PRIMARY, 1,
                                false, false, false, false, true, false, false, true, false, true, null
                        ));
                    }
                }
            });
        });
    }

    public void etapeMontrerInventaire(javafx.scene.Node caseInventaire) {
        if (masqueSombre != null && racineGlobale != null && !racineGlobale.getChildren().contains(masqueSombre)) {
            racineGlobale.getChildren().add(masqueSombre);
        }

        if (this.controller != null) {
            this.controller.setBoutonStartDisable(true);
            this.controller.setBoutonShopDisable(true);
            this.controller.setBtnPotionSoinDisable(true);
            this.controller.setBtnPotionRageDisable(true);
            this.controller.setBtnPotionGelDisable(true);
            this.controller.setBoutonTutoDisable(true);
            this.controller.setBoutonParametresDisable(true);
            this.controller.setBoutonInfoDisable(true);
            for (Button btn : this.controller.getBoutonsInventaire()) {
                if (btn != null) btn.setDisable(true);
            }
            caseInventaire.setDisable(false); // Activée !
        }

        Point2D posCase = caseInventaire.localToScene(0, 0);
        Rectangle zoneCase = new Rectangle(posCase.getX(), posCase.getY(), 80, 90);
        zoneCase.setArcWidth(20);
        zoneCase.setArcHeight(20);

        String texteExplicatif = "Excellent ! Votre Scientifique est maintenant dans votre inventaire. Cliquez directement sur sa case pour le sélectionner !";

        mettreEnValeurForme(zoneCase, texteExplicatif, false, null, posCase.getX() - 80, posCase.getY() - 150);
        masqueSombre.setMouseTransparent(true);

        caseInventaire.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, new javafx.event.EventHandler<javafx.scene.input.MouseEvent>() {
            @Override
            public void handle(javafx.scene.input.MouseEvent event) {
                caseInventaire.removeEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, this);
                masqueSombre.setMouseTransparent(false);
                masqueSombre.getChildren().clear();
                etapeMontrerPlacement();
            }
        });
    }

    public void etapeMontrerPlacement() {
        // Tout reste bloqué le temps de poser la tour
        if (this.controller != null) {
            this.controller.setBoutonStartDisable(true);
            this.controller.setBoutonShopDisable(true);
            this.controller.setBtnPotionSoinDisable(true);
            this.controller.setBtnPotionRageDisable(true);
            this.controller.setBtnPotionGelDisable(true);
            this.controller.setBoutonTutoDisable(true);
            this.controller.setBoutonParametresDisable(true);
            this.controller.setBoutonInfoDisable(true);
            List<Button> inv = this.controller.getBoutonsInventaire();
            for (Button btn : inv) {
                if (btn != null) btn.setDisable(true);
            }
            if (!inv.isEmpty()) {
                inv.get(0).setDisable(false);
            }
        }
        Point2D posSol = calculerPositionCase(26, 3);

        Rectangle zoneSol = new Rectangle(posSol.getX(), posSol.getY(), 34, 34);
        zoneSol.setArcWidth(10);
        zoneSol.setArcHeight(10);

        String texteExplicatif = "Cliquez maintenant sur la case pour placer votre tour Scientifique. Il attaquera automatiquement les microbes à sa portée !";

        // Le clic sur le terrain rendra la main au contrôleur qui appellera l'étape finale
        mettreEnValeurForme(zoneSol, texteExplicatif, false, null, posSol.getX() - 280, posSol.getY() - 40);
        masqueSombre.setMouseTransparent(true);
    }

    // Appelée automatiquement par le contrôleur dès que la tour touche le sol
    public void etapeDidcatitielTermine() {
        if (this.controller != null) {
            this.controller.setBoutonStartDisable(true);
            this.controller.setBoutonShopDisable(true);
            this.controller.setBtnPotionSoinDisable(true);
            this.controller.setBtnPotionRageDisable(true);
            this.controller.setBtnPotionGelDisable(true);
            this.controller.setBoutonTutoDisable(true);
            this.controller.setBoutonParametresDisable(true);
            this.controller.setBoutonInfoDisable(true);
            for (Button btn : this.controller.getBoutonsInventaire()) {
                if (btn != null) btn.setDisable(true);
            }
        }

        String texteFin = "Parfait, votre tour est placée ! Le didacticiel est maintenant terminé, vous êtes désormais prêt à sauver le monde de cette épidémie. L'avenir de notre planète est entre vos mains !";

        genererMessageFinal(texteFin, () -> {
            finDidacticiel(); // Enlève le masque sombre du tuto
            if (this.controller != null) {
                this.controller.arreterJeuApresTuto();
            }
        });
    }

    public void finDidacticiel() {
        if (masqueSombre != null) {
            racineGlobale.getChildren().remove(masqueSombre);
            masqueSombre = null;
        }
    }

}