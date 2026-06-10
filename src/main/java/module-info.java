module universite_paris8.iut.aboudhan.saes2javafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    requires org.controlsfx.controls;

    opens universite_paris8.iut.aboudhan.saes2javafx to javafx.fxml;
    exports universite_paris8.iut.aboudhan.saes2javafx;
    exports universite_paris8.iut.aboudhan.saes2javafx.modele;
    opens universite_paris8.iut.aboudhan.saes2javafx.modele to javafx.fxml;
    exports universite_paris8.iut.aboudhan.saes2javafx.controller;
    opens universite_paris8.iut.aboudhan.saes2javafx.controller to javafx.fxml;
}