package universite_paris8.iut.aboudhan.saes2javafx.modele;

public class Tour {
    private double x, y;
    private int portee;
    private int degats;
    private double v;
    private String nomImage;

    public Tour(double x, double y, int portee, int degats, double vitesse, String nomImage) {
        this.x = x;
        this.y = y;
        this.portee = portee;
        this.degats = degats;
        this.v = v;
        this.nomImage = nomImage;
    }


    public static Tour creerScientifique(double x, double y) {
        return new Tour(x, y, 120, 15, 1.2, "/universite_paris8/iut/aboudhan/saes2javafx/vue/tour_scientifique.png");
    }

    public static Tour creerChimiste(double x, double y) {
        return new Tour(x, y, 90, 25, 0.8, "/universite_paris8/iut/aboudhan/saes2javafx/vue/tour_chimiste.png");
    }
    public double getX() { return x; }
    public double getY() { return y; }
    public int getPortee() { return portee; }
    public double getV() { return v;}
    public int getDegats() { return degats; }
    public String getNomImage() { return nomImage; }
}
