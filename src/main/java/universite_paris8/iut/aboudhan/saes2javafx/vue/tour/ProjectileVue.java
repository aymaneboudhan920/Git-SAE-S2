package universite_paris8.iut.aboudhan.saes2javafx.vue.tour;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import universite_paris8.iut.aboudhan.saes2javafx.modele.jeu.Environnement;
import universite_paris8.iut.aboudhan.saes2javafx.modele.tour.Projectile;

public class ProjectileVue extends Pane {
    private final Projectile projectile;
    private Environnement env;

    public ProjectileVue(Projectile projectile, double coordonneeTourX, double coordonneeTourY, Environnement env) {
        this.projectile = projectile;
        this.env = env;
        rafraichirVue(coordonneeTourX, coordonneeTourY);
    }

    public void rafraichirVue(double coordonneeTourX, double coordonneeTourY) {
        this.getChildren().clear();

        switch (projectile.getType()) {
            case "SCIENTIFIQUE":
                ImageView imgSeringue = new ImageView(new Image(getClass().getResourceAsStream("/universite_paris8/iut/aboudhan/saes2javafx/vue/seringue.png")));
                imgSeringue.setFitWidth(16);
                imgSeringue.setFitHeight(16);

                this.setTranslateX(projectile.getX());
                this.setTranslateY(projectile.getY());
                this.getChildren().add(imgSeringue);
                break;

            case "CHIMISTE":
                if (!projectile.estDetruit()) {
                    ImageView imgPotion = new ImageView(new Image(getClass().getResourceAsStream("/universite_paris8/iut/aboudhan/saes2javafx/vue/potion_chimiste.png")));
                    imgPotion.setFitWidth(20);
                    imgPotion.setFitHeight(20);
                    this.setTranslateX(projectile.getX());
                    this.setTranslateY(projectile.getY());
                    this.getChildren().add(imgPotion);
                }
                break;

            case "RAYON_X":
                if (projectile.getCible() != null && !projectile.getCible().estMort()) {
                    Line laser = new Line(
                            coordonneeTourX + (double) env.getTailleTuile() /2, coordonneeTourY + 2,
                            projectile.getCible().getX() + (double) env.getTailleTuile() /2, projectile.getCible().getY() + (double) env.getTailleTuile() /2
                    );
                    laser.getStyleClass().add("rayon-laser");

                    this.setTranslateX(0);
                    this.setTranslateY(0);
                    this.getChildren().add(laser);
                }
                break;
        }
    }

    public boolean doitEtreRetire() {
        return projectile.estDetruit();
    }
}