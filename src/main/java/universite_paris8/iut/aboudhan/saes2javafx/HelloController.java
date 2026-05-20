package universite_paris8.iut.aboudhan.saes2javafx;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;

public class HelloController implements Initializable {

    @FXML
    private TilePane grilleJeu;

    private Terrain terrain = new Terrain();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        creerMap();
    }

    private void creerMap() {
        int[][] grille = terrain.grille;

        int tailleTuile = 34;
        grilleJeu.setPrefColumns(30);
        grilleJeu.setPrefRows(20);

        for (int i = 0; i < grille.length; i++) {
            for (int j = 0; j < grille[i].length; j++) {
                ImageView imageVue = new ImageView();
                imageVue.setFitHeight(tailleTuile);
                imageVue.setFitWidth(tailleTuile);

                switch (grille[i][j]) {
                    case 0: // Sol du labo
                        imageVue.setImage(new Image(getClass().getResourceAsStream("sol.png")));
                        break;
                    case 1: // Chemin des microbes
                        imageVue.setImage(new Image(getClass().getResourceAsStream("chemin.png")));
                        break;
                    case 2: // Entrée
                        imageVue.setImage(new Image(getClass().getResourceAsStream("entree.png")));
                        break;
                    case 3: // Sortie
                        imageVue.setImage(new Image(getClass().getResourceAsStream("sortie.png")));
                        break;
                    case 4: // Entrée bouche d'aération
                    case 5: // Sortie bouche d'aération
                        imageVue.setImage(new Image(getClass().getResourceAsStream("ventilation.png")));
                        break;
                    case 6: // acide
                        imageVue.setImage(new Image(getClass().getResourceAsStream("acide.png")));
                        break;
                }
                grilleJeu.getChildren().add(imageVue);
            }
            System.out.println();
        }
    }
}