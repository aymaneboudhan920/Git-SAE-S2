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
        // Ne rien faire si la case est vide
        if (typeTour == null || typeTour.isEmpty())
            return;

        Tour tourPosee = trouverTourPosee(index);

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
        // Sinon, on passe en mode placement
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

        // Ne rien faire si on a pas selectionné de tour à placer
        if (!modePlacementTour || tourCliquee.isEmpty()) return;

        // Calcul de la position exacte du clic par rapport au coin haut-gauche de la grille
        double grilleX = event.getX() - controller.grilleJeu.getLayoutX();
        double grilleY = event.getY() - controller.grilleJeu.getLayoutY();

        // On cherche les coordonnées de la case
        int tailleTuile = controller.env.getTailleTuile();
        int caseX = (int) (grilleX / tailleTuile);
        int caseY = (int) (grilleY / tailleTuile);
        int[][] grille = controller.env.getGrille();

        // Vérifie que le clic n'est pas en dehors du terrain
        if (caseY >= 0 && caseY < grille.length && caseX >= 0 && caseX < grille[0].length) {
            // Si la case est libre alors on pose la tour
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

        // Calcule et donne à la tour ses coordonnées en pixels
        int tailleTuile = controller.env.getTailleTuile();
        nouvelleTour.setX(controller.grilleJeu.getLayoutX() + (caseX * tailleTuile));
        nouvelleTour.setY(controller.grilleJeu.getLayoutY() + (caseY * tailleTuile));

        // Afficher l'image de la tour
        TourVue tourVue = new TourVue(nouvelleTour);
        controller.vuesTours.put(nouvelleTour, tourVue);
        controller.conteneurPrincipal.getChildren().add(tourVue);

        controller.boutonsInventaire.get(indexInventaireActu).getStyleClass().remove("case-inventaire-selectionnee");
        controller.inventaireVue.desactiveBoutonTour(indexInventaireActu);

        // Quitte le mode placement
        modePlacementTour = false;
        tourCliquee = "";
        indexInventaireActu = -1;

        // Si le jeu a pas encore commencé, on ractive le bouton start
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

    private Tour trouverTourPosee(int index) {
        // Parcourt chaque ligne du dictionnaire jusqu'à qu'on trouve l'index correspondant à l'index recherché
        for (java.util.Map.Entry<Tour, Integer> entry : controller.env.getTourVersIndexInventaire().entrySet()) {
            if (entry.getValue() == index)
                return entry.getKey();
        }
        return null;
    }

    public void reinitialiser() {
        this.modePlacementTour = false;
        this.tourCliquee = "";
        this.indexInventaireActu = -1;
    }
}