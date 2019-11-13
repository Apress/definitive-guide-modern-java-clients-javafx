module weather {
    requires javafx.controls;

    requires com.gluonhq.connect;
    exports org.modernclients.model to com.gluonhq.connect;

    exports org.modernclients;
}