module com.example.batalla_naval {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.media;
    requires java.desktop;


    opens com.example.batalla_naval to javafx.fxml;
    opens com.example.batalla_naval.controller to javafx.fxml;
    exports com.example.batalla_naval;
}