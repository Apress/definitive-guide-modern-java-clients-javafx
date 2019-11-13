module modernclients {
    requires javafx.controls;
    requires javafx.fxml;

    opens org.modernclients to javafx.fxml;
    exports org.modernclients;
}