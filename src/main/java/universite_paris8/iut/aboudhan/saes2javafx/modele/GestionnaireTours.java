package universite_paris8.iut.aboudhan.saes2javafx.modele;

import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import universite_paris8.iut.aboudhan.saes2javafx.controller.Controller;
import universite_paris8.iut.aboudhan.saes2javafx.vue.TourVue;

public class GestionnaireTours {

    private final Controller controller;

    private boolean modePlacementTour = false;
    private String tourCliquee = "";
    private int indexInventaireActu = -1;

    public GestionnaireTours(Controller controller) {
        this.controller = controller;
    }

    public void gererClicInventaire(int index, Button btn) {
        String typeTour = controller.inventaireModele.getTourCase(index);
        if (typeTour == null || typeTour.isEmpty())
            return;

        Tour tourPosee = trouverTourPoseeParIndex(index);

        // On remet la tour dans l'inventaire, si elle est déjà sur le terrain
        if (tourPosee != null) {
            controller.env.rappelerTour(tourPosee);
            TourVue vue = controller.vuesTours.remove(tourPosee);
            if (vue != null) {
                controller.conteneurPrincipal.getChildren().remove(vue);
            }
            btn.getStyleClass().remove("case-tour-posee");
            btn.setDisable(false);
        }
        // Sinon -> On passe en mode placement
        else {
            for (Button b : controller.boutonsInventaire) {
                b.getStyleClass().remove("case-inventaire-selectionnee");
            }
            btn.getStyleClass().add("case-inventaire-selectionnee");

            modePlacementTour = true;
            tourCliquee = typeTour;
            indexInventaireActu = index;
        }
    }

    public void gererClicTerrain(MouseEvent event) {
        // Clic droit = annulation
        if (event.getButton() == MouseButton.SECONDARY) {
            annulerPlacement();
            return;
        }

        if (!modePlacementTour || tourCliquee.isEmpty()) return;

        double xSurGrille = event.getX() - controller.grilleJeu.getLayoutX();
        double ySurGrille = event.getY() - controller.grilleJeu.getLayoutY();

        int tailleTuile = controller.env.getTailleTuile();
        int caseX = (int) (xSurGrille / tailleTuile);
        int caseY = (int) (ySurGrille / tailleTuile);
        int[][] grille = controller.env.getGrille();

        if (caseY >= 0 && caseY < grille.length && caseX >= 0 && caseX < grille[0].length) {
            if (grille[caseY][caseX] == 0) {
                poserTourSurTerrain(caseX, caseY);
            }
        }
    }

    private void poserTourSurTerrain(int caseX, int caseY) {
        Tour nouvelleTour = switch (tourCliquee) {
            case "scientifique" -> new TourScientifique(caseX, caseY);
            case "chimiste"     -> new TourChimiste(caseX, caseY);
            case "scanner"      -> new TourScanner(caseX, caseY);
            case "rayon_x"      -> new TourRayonX(caseX, caseY);
            default -> null;
        };

        if (nouvelleTour == null) return;

        controller.env.enregistrerTourPosee(nouvelleTour, caseX, caseY, indexInventaireActu);

        int tailleTuile = controller.env.getTailleTuile();
        nouvelleTour.setX(controller.grilleJeu.getLayoutX() + (caseX * tailleTuile));
        nouvelleTour.setY(controller.grilleJeu.getLayoutY() + (caseY * tailleTuile));

        TourVue tourVue = new TourVue(nouvelleTour);
        controller.vuesTours.put(nouvelleTour, tourVue);
        controller.conteneurPrincipal.getChildren().add(tourVue);

        controller.boutonsInventaire.get(indexInventaireActu).getStyleClass().remove("case-inventaire-selectionnee");
        controller.inventaireVue.desactiveBoutonTour(indexInventaireActu);

        modePlacementTour = false;
        tourCliquee = "";
        indexInventaireActu = -1;

        if (!controller.jeuDemarre && controller.boutonStart != null) {
            controller.boutonStart.setDisable(false);
        }
    }

    public void annulerPlacement() {
        if (modePlacementTour) {
            for (Button b : controller.boutonsInventaire) {
                b.getStyleClass().remove("case-inventaire-selectionnee");
            }
            modePlacementTour = false;
            tourCliquee = "";
            indexInventaireActu = -1;
        }
    }

    private Tour trouverTourPoseeParIndex(int index) {
        for (java.util.Map.Entry<Tour, Integer> entry : controller.env.getTourVersIndexInventaire().entrySet()) {
            if (entry.getValue() == index) return entry.getKey();
        }
        return null;
    }

    public void reinitialiser() {
        this.modePlacementTour = false;
        this.tourCliquee = "";
        this.indexInventaireActu = -1;
    }
}