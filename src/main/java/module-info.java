module universite_paris8.iut.aboudhan.saes2javafx {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires java.desktop;

    opens universite_paris8.iut.aboudhan.saes2javafx to javafx.fxml;
    exports universite_paris8.iut.aboudhan.saes2javafx;
    exports universite_paris8.iut.aboudhan.saes2javafx.controller;
    opens universite_paris8.iut.aboudhan.saes2javafx.controller to javafx.fxml;
    exports universite_paris8.iut.aboudhan.saes2javafx.modele.tour;
    opens universite_paris8.iut.aboudhan.saes2javafx.modele.tour to javafx.fxml;
    exports universite_paris8.iut.aboudhan.saes2javafx.modele.microbe;
    opens universite_paris8.iut.aboudhan.saes2javafx.modele.microbe to javafx.fxml;
    exports universite_paris8.iut.aboudhan.saes2javafx.modele.jeu;
    opens universite_paris8.iut.aboudhan.saes2javafx.modele.jeu to javafx.fxml;
}