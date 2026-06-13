package universite_paris8.iut.aboudhan.saes2javafx.modele.tour;

import universite_paris8.iut.aboudhan.saes2javafx.modele.microbe.Microbe;

import java.util.List;

public class Projectile {
    private double x, y;
    private final double vitesse;
    private final Microbe cible;
    private final String type;
    private boolean detruit;
    private final int degats;

    public Projectile(double x, double y, double vitesse, Microbe cible, String type, int degats) {
        this.x = x;
        this.y = y;
        this.vitesse = vitesse;
        this.cible = cible;
        this.type = type;
        this.degats = degats;
        this.detruit = false;
    }

    public void deplacer(List<Microbe> microbesActifs) {
        if (detruit || cible == null || cible.estMort()) {
            this.detruit = true;
            return;
        }

        if (type.equals("RAYON_X")) {
            return;
        }

        double diffX = cible.getX() - this.x;
        double diffY = cible.getY() - this.y;
        double distance = Math.sqrt(diffX * diffX + diffY * diffY);

        if (distance <= vitesse) {
            this.x = cible.getX();
            this.y = cible.getY();

            if (type.equals("CHIMISTE")) {
                // Parcourt tous les microbes actifs
                for (int i = 0; i < microbesActifs.size(); i++) {
                    Microbe m = microbesActifs.get(i);

                    // On ne touche que les microbes vivants
                    if (!m.estMort()) {
                        double dX = m.getX() - this.x;
                        double dY = m.getY() - this.y;
                        double distMicrobe = Math.sqrt(dX * dX + dY * dY);

                        // Si le microbe est à portée de la détonation de la tour chimiste
                        if (distMicrobe <= TourChimiste.portee) {
                            m.perdreVie(degats);
                        }
                    }
                }
            } else {
                cible.perdreVie(degats);
            }

            this.detruit = true;
        } else {
            this.x += (diffX / distance) * vitesse;
            this.y += (diffY / distance) * vitesse;
        }
    }

    // Getters & Setters
    public double getX() { return x; }
    public double getY() { return y; }
    public String getType() { return type; }
    public boolean estDetruit() { return detruit; }
    public Microbe getCible() { return cible; }
}