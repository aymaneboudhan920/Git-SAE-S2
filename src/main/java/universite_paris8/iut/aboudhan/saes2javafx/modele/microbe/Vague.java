package universite_paris8.iut.aboudhan.saes2javafx.modele.microbe;

import java.util.ArrayList;
import java.util.List;

public class Vague {
    private final List<Microbe> fileAttenteMicrobes;
    private final double tempsIntervalle;
    private final int bonus;

    public Vague(double t, int b){
        this.fileAttenteMicrobes = new ArrayList<>();
        this.tempsIntervalle = t;
        this.bonus = b;
    }

    public double getTempsIntervalle() {
        return tempsIntervalle;
    }

    public int getBonus() {
        return bonus;
    }

    public List<Microbe> getFileAttenteMicrobes() {
        return fileAttenteMicrobes;
    }
}
