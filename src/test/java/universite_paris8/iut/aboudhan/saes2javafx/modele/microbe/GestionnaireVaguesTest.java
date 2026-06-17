package universite_paris8.iut.aboudhan.saes2javafx.modele.microbe;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import universite_paris8.iut.aboudhan.saes2javafx.modele.jeu.Environnement;

import static org.junit.jupiter.api.Assertions.*;

class GestionnaireVaguesTest {

    private GestionnaireVagues gestionnaireVagues;
    private Environnement env;

    @BeforeEach
    void setUp() {
        gestionnaireVagues = new GestionnaireVagues();
        env = new Environnement();
    }

    @Test
    void creerListeMicrobes() {
        gestionnaireVagues.initialiserVagues(env);
        Vague vague1 = gestionnaireVagues.listeVagues.get(0);
        assertNotNull(vague1, "La première vague ne devrait pas être nulle.");
        assertFalse(vague1.getFileAttenteMicrobes().isEmpty(), "La file d'attente ne devrait pas être vide.");

        for (Microbe m : vague1.getFileAttenteMicrobes()) {
            if (m.getType().equals("RHINOVIRUS")) {
                assertEquals(0.8, m.vitesseDeBase, 0.01);
                assertEquals(30.0, m.pvMax, 0.01);
                assertEquals(2, m.getRecompense());
                assertEquals(1, m.infection);
            } else if (m.getType().equals("NOROVIRUS")) {
                assertEquals(1.0, m.vitesseDeBase, 0.01);
                assertEquals(40.0, m.pvMax, 0.01);
                assertEquals(3, m.getRecompense());
                assertEquals(1, m.infection);
            } else {
                fail("Un microbe s'est anormalement glissé dans la vague 1.");
            }
        }

        Vague vague10 = gestionnaireVagues.listeVagues.get(9);
        for (Microbe m : vague10.getFileAttenteMicrobes()) {
            if (m.getType().equals("VARIOLE")) {
                assertEquals(0.3, m.vitesseDeBase, 0.01);
                assertEquals(2000.0, m.pvMax, 0.01);
                assertEquals(100, m.getRecompense());
                assertEquals(50, m.infection);
            }
        }
    }

    @Test
    void initialiserVagues() {
        assertTrue(gestionnaireVagues.listeVagues.isEmpty(), "La liste devrait être vide avant l'initialisation.");
        gestionnaireVagues.initialiserVagues(env);
        assertEquals(10, gestionnaireVagues.listeVagues.size(), "Les 10 vagues n'ont pas toutes été correctement initialisées.");

        Vague v1 = gestionnaireVagues.listeVagues.get(0);
        assertEquals(1.0, v1.getTempsIntervalle(), 0.01);
        assertEquals(10, v1.getFileAttenteMicrobes().size(), "La vague 1 devrait contenir 10 microbes.");

        Vague v5 = gestionnaireVagues.listeVagues.get(4);
        assertEquals(1.0, v5.getTempsIntervalle(), 0.01);
        assertEquals(20, v5.getFileAttenteMicrobes().size(), "La vague 5 devrait contenir 20 microbes.");
        for (Microbe m : v5.getFileAttenteMicrobes()) {
            assertNotEquals("RHINOVIRUS", m.getType(), "Le Rhinovirus n'est pas censé pouvoir apparaître dans la vague 5.");
        }
    }

    @Test
    void getVagueActuelle() {
        assertNull(gestionnaireVagues.getVagueActuelle(), "Doit renvoyer null si aucune vague n'est initialisée.");
        gestionnaireVagues.initialiserVagues(env);

        Vague courante = gestionnaireVagues.getVagueActuelle();
        assertNotNull(courante);
        assertEquals(10, courante.getFileAttenteMicrobes().size(), "On devrait récupérer la première vague qui devrait contenir 10 microbes.");

        // Scénario B : On avance d'un cran
        gestionnaireVagues.AugmenterVague();
        Vague suivante = gestionnaireVagues.getVagueActuelle();
        assertEquals(15, suivante.getFileAttenteMicrobes().size(), "On devrait récupérer la 2ème vague qui devrait contenir quant à elle 15 microbes.");

        for (int i = 0; i < 15; i++) {
            gestionnaireVagues.AugmenterVague();
        }
        assertNull(gestionnaireVagues.getVagueActuelle(), "Doit renvoyer null si le joueur a dépassé la dernière vague programmée.");
    }

    @Test
    void augmenterVague() {
        assertEquals(0, gestionnaireVagues.getNumVagueActu());

        gestionnaireVagues.AugmenterVague();
        assertEquals(1, gestionnaireVagues.getNumVagueActu(), "Le numéro de vague actuelle doit passer à 1.");

        gestionnaireVagues.AugmenterVague();
        assertEquals(2, gestionnaireVagues.getNumVagueActu(), "Le numéro de vague actuelle doit passer à 2.");
    }

    @Test
    void estDerniereVague() {
        gestionnaireVagues.initialiserVagues(env);
        assertFalse(gestionnaireVagues.estDerniereVague(), "La vague N°1 ne correspond pas à la fin du jeu.");

        for (int i = 0; i < 8; i++) {
            gestionnaireVagues.AugmenterVague();
        }
        assertFalse(gestionnaireVagues.estDerniereVague(), "La vague n°9 ne correspond pas encore à la dernière vague.");

        gestionnaireVagues.AugmenterVague();
        assertTrue(gestionnaireVagues.estDerniereVague(), "La vague n°10 doit renvoyer true car elle est la dernière vague.");

        gestionnaireVagues.AugmenterVague();
        assertFalse(gestionnaireVagues.estDerniereVague(), "En cas de dépassement hors-norme, le booléen doit repasser à false.");
    }
}