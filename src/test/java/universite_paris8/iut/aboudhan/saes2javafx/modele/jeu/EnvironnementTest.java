package universite_paris8.iut.aboudhan.saes2javafx.modele.jeu;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import universite_paris8.iut.aboudhan.saes2javafx.modele.microbe.Microbe;

import static org.junit.jupiter.api.Assertions.*;

class EnvironnementTest {

    private Environnement environnement;

    @BeforeEach
    void setUp() {
        environnement = new Environnement();
        environnement.argentProperty().set(100);
        environnement.setGensInfectes(0);
        environnement.getMicrobesActifs().clear();
    }

    @Test
    void ajouterArgent() {
        environnement.ajouterArgent(50);
        assertEquals(150, environnement.getArgent(), "L'ajout d'argent n'a pas correctement incrémenté le solde.");
    }

    @Test
    void reduireArgent() {
        environnement.reduireArgent(40);
        assertEquals(60, environnement.getArgent(), "La réduction d'argent n'a pas correctement calculé le solde restant.");

        environnement.reduireArgent(80);
        assertEquals(0, environnement.getArgent(), "Le solde d'argent aurait dû être bloqué à 0 au lieu de devenir négatif.");
    }

    @Test
    void updateMicrobes() {
        // On force sa cible à null pour faire comme si il était déjà arrivé à la fin du chemin
        Microbe microbe = new Microbe(1.0, 30, 2, 5, "RHINOVIRUS", null);
        microbe.waypointCible = null;
        environnement.getMicrobesActifs().add(microbe);
        assertEquals(0, environnement.getGensInfectes());

        boolean unMicrobeEstSorti = environnement.updateMicrobes();
        assertTrue(unMicrobeEstSorti, "updateMicrobes aurait dû renvoyer true car un microbe a franchi la sortie.");
        assertEquals(5, environnement.getGensInfectes(), "La jauge de personnes infectées n'a pas augmenté de la valeur d'infection du microbe.");
        assertFalse(environnement.getMicrobesActifs().contains(microbe), "Le microbe sorti n'a pas été supprimé de la liste des microbes actifs.");
    }

    @Test
    void creerItineraireAleatoire() {
        Waypoint pointDepart = environnement.creerItineraireAleatoire();
        assertNotNull(pointDepart, "L'itinéraire généré est nul.");
        assertFalse(pointDepart.getSuivants().isEmpty(), "Le point de départ n'est relié à aucun autre Waypoint. Le BFS n'a probablement pas trouvé la sortie.");
    }
}