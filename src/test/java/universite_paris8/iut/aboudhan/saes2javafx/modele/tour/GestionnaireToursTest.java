package universite_paris8.iut.aboudhan.saes2javafx.modele.tour;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import universite_paris8.iut.aboudhan.saes2javafx.modele.jeu.Environnement;

import static org.junit.jupiter.api.Assertions.*;

class GestionnaireToursTest {

    private GestionnaireTours gestionnaireTours;
    private Environnement env;

    @BeforeEach
    void setUp() {
        env = new Environnement();

        int[][] grille = env.getGrille();
        for (int y = 0; y < grille.length; y++) {
            for (int x = 0; x < grille[y].length; x++) {
                grille[y][x] = 0;
            }
        }

        env.getTourVersIndexInventaire().clear();
        env.getToursPosees().clear();
        gestionnaireTours = new GestionnaireTours(env);
    }

    @Test
    void gererClicInventaire() {
        assertFalse(gestionnaireTours.gererClicInventaire(0, null));
        assertFalse(gestionnaireTours.gererClicInventaire(0, ""));
        assertFalse(gestionnaireTours.estModePlacementTour());

        // Cas où la tour n'est pas encore sur le terrain lors du clic sur la case de l'inventaire
        boolean resultatSelection = gestionnaireTours.gererClicInventaire(3, "TOUR_CHIMISTE");

        assertFalse(resultatSelection, "La méthode devrait renvoyer false car aucune tour n'était encore posée à cet index.");
        assertTrue(gestionnaireTours.estModePlacementTour(), "Le mode placement devrait être activé.");
        assertEquals(3, gestionnaireTours.getIndexInventaireActu(), "L'index de l'inventaire cliqué doit être enregistré.");

        // Cas où la tour est déjà sur le terrain lors du clic sur la case de l'inventaire
        Tour tourDejaPosee = new TourChimiste(0, 0);
        env.getTourVersIndexInventaire().put(tourDejaPosee, 3);
        env.getToursPosees().add(tourDejaPosee);

        boolean resultatSurTourPosee = gestionnaireTours.gererClicInventaire(3, "TOUR_CHIMISTE");
        assertTrue(resultatSurTourPosee, "La méthode devrait renvoyer true car la tour associée à cet index est déjà active sur la carte.");
    }

    @Test
    void gererClicTerrain() {
        assertNull(gestionnaireTours.gererClicTerrain(34.0, 34.0), "La méthode devrait renvoyer null si le mode placement n'est pas actif.");

        gestionnaireTours.gererClicInventaire(2, "TOUR_CHIMISTE");
        int tailleTuile = env.getTailleTuile();

        assertNull(gestionnaireTours.gererClicTerrain(-10.0, 15.0), "La méthode devrait refuser les coordonnées X négatives.");
        assertNull(gestionnaireTours.gererClicTerrain(15.0, 5000.0), "La méthode devrait refuser les coordonnées Y en dehors de la grille.");

        // Cas invalide : placement sur une case occupée
        env.getGrille()[1][1] = 2;
        assertNull(gestionnaireTours.gererClicTerrain(1 * tailleTuile + 5, 1 * tailleTuile + 5),
                "Le placement devrait être refusé si la case n'est pas libre.");

        // Cas valide : placement sur une case libre
        Tour tourEnAttente = new TourChimiste(0, 0);
        env.getTourVersIndexInventaire().put(tourEnAttente, 2);

        // On simule un clic au milieu de la case située à la ligne 3, colonne 4
        Tour tourPlacee = gestionnaireTours.gererClicTerrain(4 * tailleTuile + 10, 3 * tailleTuile + 10);

        assertNotNull(tourPlacee, "Le placement aurait dû réussir sur une tuile constructible.");
        assertEquals(tourEnAttente, tourPlacee, "La méthode devrait retourner l'instance de la tour posée.");
        assertFalse(gestionnaireTours.estModePlacementTour(), "Le mode placement devrait se couper après avoir posé la tour.");
    }

    @Test
    void annulerPlacement() {
        gestionnaireTours.gererClicInventaire(5, "TOUR_CHIMISTE");
        assertTrue(gestionnaireTours.estModePlacementTour());
        gestionnaireTours.annulerPlacement();

        assertFalse(gestionnaireTours.estModePlacementTour(), "Le mode placement doit repasser à false.");
        assertEquals(-1, gestionnaireTours.getIndexInventaireActu(), "L'index de l'inventaire doit être réinitialisé à -1.");
    }

    @Test
    void trouverTourPosee() {
        Tour tour1 = new TourChimiste(32, 32);
        Tour tour2 = new TourChimiste(64, 64);

        env.getTourVersIndexInventaire().put(tour1, 1);
        env.getTourVersIndexInventaire().put(tour2, 2);

        // Cas où la tour n'est pas posée sur le terrain de jeu
        assertNull(gestionnaireTours.trouverTourPosee(1), "La méthode devrait renvoyer null car la tour n'est pas présente sur la carte.");

        // Cas où la tour est posée
        env.getToursPosees().add(tour1);
        Tour resultat = gestionnaireTours.trouverTourPosee(1);

        assertNotNull(resultat);
        assertEquals(tour1, resultat, "La méthode devrait renvoyer la tour exacte correspondant à l'index demandé.");

        // Cas où l'index est inexistant
        assertNull(gestionnaireTours.trouverTourPosee(99));
    }

    @Test
    void reinitialiser() {
        gestionnaireTours.gererClicInventaire(1, "TOUR_CHIMISTE");
        gestionnaireTours.reinitialiser();

        assertFalse(gestionnaireTours.estModePlacementTour());
        assertEquals(-1, gestionnaireTours.getIndexInventaireActu());
    }
}