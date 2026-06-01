package universite_paris8.iut.aboudhan.saes2javafx.modele;

public class Tours {
    private double x, y;
    private int portee;
    private int degats;
    private String nomImage;
    private double v;

    public Tours(double x, double y, int portee, int degats, double v, String nomImage) {
        this.x = x;
        this.y = y;
        this.portee = portee;
        this.degats = degats;
        this.v = v;
        this.nomImage = nomImage;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public int getPortee() { return portee; }
    public double getV() { return v;}
    public int getDegats() { return degats; }
    public String getNomImage() { return nomImage; }
}