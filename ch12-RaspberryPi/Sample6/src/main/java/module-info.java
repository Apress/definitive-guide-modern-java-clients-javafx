module org.modernclients.raspberrypi.gps {
    requires javafx.controls;
    requires javafx.fxml;
    requires pi4j.core;
    requires com.gluonhq.maps;
    requires afterburner.fx;
    requires java.annotation;
    requires java.logging;

    opens org.modernclients.raspberrypi.gps.model to afterburner.fx;
    opens org.modernclients.raspberrypi.gps.service to afterburner.fx;
    opens org.modernclients.raspberrypi.gps.view to afterburner.fx, javafx.fxml;

    exports org.modernclients.raspberrypi.gps;
}