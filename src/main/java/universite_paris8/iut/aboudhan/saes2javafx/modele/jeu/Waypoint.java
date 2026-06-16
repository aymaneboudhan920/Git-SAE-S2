package universite_paris8.iut.aboudhan.saes2javafx.modele.jeu;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Waypoint {
    private double x, y;
    private List<Waypoint> suivants;
    private static final Random random = new Random();

    public Waypoint(double x, double y){
        this.x = x;
        this.y = y;
        this.suivants = new ArrayList<>();
    }

    public void ajouterSuivant(Waypoint suivant){
        if (!this.suivants.contains(suivant)){
            this.suivants.add(suivant);
        }
    }

    public Waypoint obtenirProchainWaypoint() {
        if (this.suivants.isEmpty())
            return null;
        int indexChoisi = random.nextInt(this.suivants.size());
        return this.suivants.get(indexChoisi);
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public List<Waypoint> getSuivants() {
        return this.suivants;
    }
}
