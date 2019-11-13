module weather {
    requires javafx.controls;

    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    exports org.modernclients.model to com.fasterxml.jackson.databind;

    exports org.modernclients;
}