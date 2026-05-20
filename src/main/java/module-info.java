module universite_paris8.iut.aboudhan.saes2javafx {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens universite_paris8.iut.aboudhan.saes2javafx to javafx.fxml;
    exports universite_paris8.iut.aboudhan.saes2javafx;
}