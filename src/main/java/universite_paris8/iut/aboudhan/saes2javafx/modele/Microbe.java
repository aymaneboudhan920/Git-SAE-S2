package universite_paris8.iut.aboudhan.saes2javafx.modele;

public class Microbe {
    public double x, y, vitesseDeBase, vitesseActu;
    public int pv, pvMax, recompense, infection;
    public Waypoint waypointCible;
    public String nomImage;

    public Microbe(double v, int pm, int r, int i, String nomImage, Waypoint waypointDepart){
        this.x = waypointDepart.getX();
        this.y = waypointDepart.getY();
        this.vitesseDeBase = v;
        this.vitesseActu = v;
        this.pv = pm;
        this.pvMax = pm;
        this.recompense = r;
        this.infection = i;
        this.waypointCible = waypointDepart.obtenirProchainWaypoint();
        this.nomImage = nomImage;
    }

    public void deplacer(){

        if (this.estGele || this.vitesseActu == 0) {
            return;
        }
        
        if (this.waypointCible == null) {
            return;
        }

        double diffX = this.waypointCible.getX() - this.x;
        double diffY = this.waypointCible.getY() - this.y;
        double distance = Math.sqrt(diffX * diffX + diffY * diffY);

        if (distance > this.vitesseActu){
            this.x += (diffX / distance) * this.vitesseActu;
            this.y += (diffY / distance) * this.vitesseActu;
        } else {
            this.x = this.waypointCible.getX();
            this.y = this.waypointCible.getY();

            this.waypointCible = waypointCible.obtenirProchainWaypoint();

            if(waypointCible != null){
                double distanceSaut = Math.sqrt(
                        Math.pow(waypointCible.getX() - this.x, 2) +
                                Math.pow(waypointCible.getY() - this.y, 2)
                );
                if (distanceSaut > 70) {
                    this.x = waypointCible.getX();
                    this.y = waypointCible.getY();
                }
            }
        }
    }

    public void appliquerRalentissement(boolean estRalenti) {
        if (this.estGele) {
            return;
        }
        
        if (estRalenti) {
            this.vitesseActu = this.vitesseDeBase * 0.5;
        } else {
            this.vitesseActu = this.vitesseDeBase;
        }
    }

    public double getRatioPV(){
        return (double) pv / pvMax;
    }

    public void perdreVie(int degats){
        pv = Math.max(0,pv - degats);
    }

    public boolean estMort(){
        return pv <= 0;
    }

    public int getRecompense() {
        return recompense;
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
    public double getVitesseActu() {
        return vitesseActu;
    }
    public void setVitesseActu(double valeur) {
        this.vitesseActu = valeur;
        if (valeur == 0) {
            this.estGele = true;
        }
    }

    public void reinitialiserVitesse() {
        this.estGele = false;
        this.vitesseActu = this.vitesseDeBase;
    }
}
