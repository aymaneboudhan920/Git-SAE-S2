package universite_paris8.iut.aboudhan.saes2javafx.vue.tour;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.InputStream;
import java.util.List;

public class InventaireVue {
    private final List<Button> boutons;
    private final List<ImageView> images;
    private final List<Label> labels;

    // Le constructeur reçoit directement les listes de composants injectés depuis le Controller
    public InventaireVue(List<Button> boutons, List<ImageView> images, List<Label> labels) {
        this.boutons = boutons;
        this.images = images;
        this.labels = labels;
    }

    // Appelé lors de l'achat dans le shop pour équiper une case
    public void installerTour(int index, String typeTour) {
        if (index >= 0 && index < boutons.size()) {
            String labelTexte = "LEVEL 1";

            // On s'adapte au dossier de ressources configuré dans ton FXML
            String nomFichierImage = "/universite_paris8/iut/aboudhan/saes2javafx/vue/tour_" + typeTour.toLowerCase() + ".png";

            // Mise à jour du texte sous la case
            labels.get(index).setText(labelTexte);

            // Chargement de l'image de la tour achetée
            InputStream stream = getClass().getResourceAsStream(nomFichierImage);
            if (stream != null) {
                images.get(index).setImage(new Image(stream));
            } else {
                images.get(index).setImage(null);
            }

            // On réactive le bouton pour qu'il soit cliquable suite à l'achat
            boutons.get(index).setDisable(false);
        }
    }

    public void rafraichirInv(String[] contenuInventaire) {
        // On boucle sur le nombre de boutons de l'interface (les 8 cases)
        for (int i = 0; i < boutons.size(); i++) {

            // Sécurité au cas où le tableau reçu soit plus petit que nos boutons
            String typeTour = (i < contenuInventaire.length) ? contenuInventaire[i] : null;

            if (typeTour == null || typeTour.isEmpty()) {
                images.get(i).setImage(null);
                labels.get(i).setText("");

                boutons.get(i).getStyleClass().removeAll("case-tour-posee", "case-inventaire-selectionnee");
                boutons.get(i).setDisable(true);
            } else {
                installerTour(i, typeTour);
                boutons.get(i).setDisable(false);
            }
        }
    }
}
