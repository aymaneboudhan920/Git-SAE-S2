package universite_paris8.iut.aboudhan.saes2javafx;

public class Microbe {
    private double x, y, vitesseDeBase, vitesseActu;
    private int pv, pvMax, recompense;
    private Waypoint waypointCible;
    private String nomImage;

    public Microbe(double v, int pm, int r, String nomImage, Waypoint waypointDepart){
        // On place le microbe pile sur les coordonnées du point de départ
        this.x = waypointDepart.getX();
        this.y = waypointDepart.getY();
        this.vitesseDeBase = v;
        this.vitesseActu = v;
        this.pv = pm;
        this.pvMax = pm;
        this.recompense = r;
        // Le microbe apparait au point de départ, il faut donc chercher le point suivant
        this.waypointCible = waypointDepart.obtenirProchainWaypoint();
        this.nomImage = nomImage;
    }

    public void deplacer(){
        // Vérifier si on est à la sortie ou pas
        if (this.waypointCible == null) {
            return;
        }

        // Calcul des distances horizontales et verticales entre le microbe et son prochain point
        double diffX = this.waypointCible.getX() - this.x;
        double diffY = this.waypointCible.getY() - this.y;
        // Calcul de la distance à vol d'oiseau entre un microbe et son point cible
        double distance = Math.sqrt(diffX * diffX + diffY * diffY);

        // Cas où le microbe est encore loin de sa cible
        if (distance > this.vitesseActu){
            // Donne la direction et le fais avancer à sa vitesse
            this.x += (diffX / distance) * this.vitesseActu;
            this.y += (diffY / distance) * this.vitesseActu;
        // Cas où il est a atteint sa cible ou qu'il en est très proche
        } else {
            // Pour éviter toutes erreurs de virages,
            // On le téléporte pile sur les coordonnées de sa cible
            this.x = this.waypointCible.getX();
            this.y = this.waypointCible.getY();

            // Recherche de sa prochaine cible
            this.waypointCible = waypointCible.obtenirProchainWaypoint();

            // Vérfication que le microbe n'est pas encore arrivé à la sortie
            if(waypointCible != null){
                // On recalcule la distance entre le microbe et sa
                // prochaine cible à vol d'oiseau
                double distanceSaut = Math.sqrt(
                    Math.pow(waypointCible.getX() - this.x, 2) +
                    Math.pow(waypointCible.getY() - this.y, 2)
                );
                // Une dalle fait 34 pixels. Si le distance est supérieure à 70 pixels
                // Alors cela signifie que le microbe a emprunté un conduit
                if (distanceSaut > 70) {
                    // On le téléporte donc sur la case de sortie du conduit
                    this.x = waypointCible.getX();
                    this.y = waypointCible.getY();
                }
            }
        }
    }

    public void appliquerRalentissement(boolean estRalenti) {
        // Si le microbe est ralenti
        if (estRalenti) {
            // On divise la vitesse par 2
            this.vitesseActu = this.vitesseDeBase * 0.5;
        } else {
            // Sinon on garde la vitesse initiale
            this.vitesseActu = this.vitesseDeBase;
        }
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public String getNomImage() {
        return this.nomImage;
    }

    public Waypoint getWaypointCible() {
        return this.waypointCible;
    }
}
