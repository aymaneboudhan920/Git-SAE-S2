package universite_paris8.iut.aboudhan.saes2javafx.modele;

import javafx.scene.control.Button;
import java.util.List;

/**
 * Interface exposée au modèle pour piloter l'UI pendant le didacticiel.
 * Le Controller l'implémente ; DidactitielVisuel ne connaît que cette interface,
 * ce qui respecte le principe MVC (pas de dépendance du modèle vers le controller).
 */
public interface IControleurTuto {

    /** Active ou désactive le bouton START. */
    void setBoutonStartDisable(boolean disable);

    /** Active ou désactive le bouton SHOP. */
    void setBoutonShopDisable(boolean disable);

    /** Retourne le nœud graphique du bouton SHOP (pour le mettre en valeur dans le tuto). */
    javafx.scene.Node getBoutonShop();

    /** Active ou désactive le bouton POTION SOIN (si présent). */
    void setBtnPotionSoinDisable(boolean disable);

    /** Active ou désactive le bouton POTION RAGE (si présent). */
    void setBtnPotionRageDisable(boolean disable);

    /** Active ou désactive le bouton POTION GEL (si présent). */
    void setBtnPotionGelDisable(boolean disable);

    /** Active ou désactive le bouton TUTO (si présent). */
    void setBoutonTutoDisable(boolean disable);

    /** Active ou désactive le bouton PARAMETRES (si présent). */
    void setBoutonParametresDisable(boolean disable);

    /** Active ou désactive le bouton INFO (si présent). */
    void setBoutonInfoDisable(boolean disable);

    /** Retourne la liste des boutons de l'inventaire. */
    List<Button> getBoutonsInventaire();

    /** Déclenche le spawn du premier microbe (relance la timeline). */
    void declencherSpawnPremierMicrobe();

    /** Relance brièvement la game loop et la timeline pendant le tuto. */
    void relancerJeuTemporairementTuto();

    /** Met en pause la game loop et la timeline pendant le tuto. */
    void pauserJeuTuto();

    /** Arrête et nettoie le jeu à la fin du didacticiel. */
    void arreterJeuApresTuto();

    /** Remet tout à zéro (map, inventaire, env) quand le joueur confirme le lancement du tuto. */
    void reinitialiserPourTuto();

    /** Enregistre le didacticiel actif (appelé au clic OUI pour que la game loop puisse interagir avec lui). */
    void setDidacticielActif(DidactitielVisuel didacticiel);
}