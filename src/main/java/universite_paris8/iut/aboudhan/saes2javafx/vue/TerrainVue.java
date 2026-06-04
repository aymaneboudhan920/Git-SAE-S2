package universite_paris8.iut.aboudhan.saes2javafx.vue;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;

public class TerrainVue {

    private final int[][] grille;
    private final int tailleTuile;

    public TerrainVue(int[][] grille, int tailleTuile) {
        this.grille = grille;
        this.tailleTuile = tailleTuile;
    }

    public void dessinerTerrain(TilePane grilleJeu) {
        grilleJeu.getChildren().clear(); // Nettoyage de sécurité
        grilleJeu.setPrefColumns(30);
        grilleJeu.setPrefRows(20);

        for (int i = 0; i < grille.length; i++) {
            for (int j = 0; j < grille[i].length; j++) {
                ImageView imageVue = new ImageView();
                imageVue.setFitHeight(tailleTuile);
                imageVue.setFitWidth(tailleTuile);

                String nomImage = obtenirNomImage(grille[i][j]);
                if (nomImage != null) {
                    imageVue.setImage(new Image(getClass().getResourceAsStream("/universite_paris8/iut/aboudhan/saes2javafx/vue/" + nomImage)));
                }
                grilleJeu.getChildren().add(imageVue);
            }
        }
    }

    // Centralisation de la logique des textures
    private String obtenirNomImage(int typeDalle) {
        return switch (typeDalle) {
            case 0 -> "sol.png";
            case 1 -> "chemin.png";
            case 2 -> "entree.png";
            case 3 -> "sortie.png";
            case 4, 5 -> "ventilation.png";
            case 6 -> "acide.png";
            case 7 -> "pot_de_fleur.png";
            case 8 -> "plantes.png";
            case 9 -> "incubateur_haut_centre.png";
            case 10 -> "incubateur_milieu_gauche.png";
            case 11 -> "incubateur_milieu_centre.png";
            case 12 -> "incubateur_milieu_droit.png";
            case 13 -> "incubateur_bas_gauche.png";
            case 14 -> "incubateur_bas_centre.png";
            case 15 -> "incubateur_bas_droit.png";
            case 16 -> "echantillon_bas.png";
            case 17 -> "echantillon_haut.png";
            case 18 -> "frigo_haut_gauche.png";
            case 19 -> "frigo_haut_droit.png";
            case 20 -> "frigo_bas_gauche.png";
            case 21 -> "frigo_bas_droit.png";
            case 22 -> "bureau_haut_gauche.png";
            case 23 -> "bureau_haut_droit.png";
            case 24 -> "bureau_bas_gauche.png";
            case 25 -> "bureau_bas_droit.png";
            default -> null;
        };
    }
}