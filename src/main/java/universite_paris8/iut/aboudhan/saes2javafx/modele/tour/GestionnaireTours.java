package universite_paris8.iut.aboudhan.saes2javafx.modele.tour;

import universite_paris8.iut.aboudhan.saes2javafx.modele.jeu.Environnement;

public class GestionnaireTours {

    // On garde uniquement une référence vers l'environnement (qui est un modèle !)
    private final Environnement env;

    private boolean modePlacementTour = false;
    private String tourCliquee = "";
    private int indexInventaireActu = -1;

    public GestionnaireTours(Environnement env) {
        this.env = env;
    }

    public boolean gererClicInventaire(int index, String typeTour) {
        if (typeTour == null || typeTour.isEmpty()) {
            return false;
        }

        Tour tourPosee = trouverTourPosee(index);

        if (tourPosee != null) {
            env.rappelerTour(tourPosee);
            return true;
        }
        else {
            modePlacementTour = true;
            tourCliquee = typeTour;
            indexInventaireActu = index;
            return false;
        }
    }

    public Tour gererClicTerrain(double xSurTerrain, double ySurTerrain) {
        if (!modePlacementTour || tourCliquee.isEmpty()) {
            return null;
        }

        int tailleTuile = env.getTailleTuile();
        int caseX = (int) (xSurTerrain / tailleTuile);
        int caseY = (int) (ySurTerrain / tailleTuile);
        int[][] grille = env.getGrille();

        // Vérification des limites de la grille et de la disponibilité de la case
        if (caseY >= 0 && caseY < grille.length && caseX >= 0 && caseX < grille[0].length) {
            if (grille[caseY][caseX] == 0) {
                return genererNouvelleTour(caseX, caseY);
            }
        }
        return null;
    }

    private Tour genererNouvelleTour(int caseX, int caseY) {
        Tour nouvelleTour = switch (tourCliquee) {
            case "scientifique" -> new TourScientifique(caseX, caseY);
            case "chimiste"     -> new TourChimiste(caseX, caseY);
            case "scanner"      -> new TourScanner(caseX, caseY);
            case "rayon_x"      -> new TourRayonX(caseX, caseY);
            default -> null;
        };

        if (nouvelleTour != null) {
            env.enregistrerTourPosee(nouvelleTour, caseX, caseY, indexInventaireActu);
            modePlacementTour = false;
            tourCliquee = "";
        }
        return nouvelleTour;
    }

    public void annulerPlacement() {
        modePlacementTour = false;
        tourCliquee = "";
        indexInventaireActu = -1;
    }

    private Tour trouverTourPosee(int index) {
        for (java.util.Map.Entry<Tour, Integer> association : env.getTourVersIndexInventaire().entrySet()) {
            if (association.getValue() == index) {
                return association.getKey();
            }
        }
        return null;
    }

    public void reinitialiser() {
        this.modePlacementTour = false;
        this.tourCliquee = "";
        this.indexInventaireActu = -1;
    }

    public boolean isModePlacementTour() { return modePlacementTour; }
    public int getIndexInventaireActu() { return indexInventaireActu; }
}
