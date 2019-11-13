module hellofx {
    requires javafx.controls;
    requires javafx.fxml;
    
    opens org.modernclients.raspberrypi to javafx.fxml;
    exports org.modernclients.raspberrypi;
}
