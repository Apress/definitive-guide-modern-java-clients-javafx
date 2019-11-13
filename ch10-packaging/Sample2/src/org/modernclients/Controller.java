package org.modernclients;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class Controller {

    @FXML
    private Label label;

    public void initialize() {
        String javaVersion = System.getProperty("java.version");
        String javafxVersion = System.getProperty("javafx.version");
        label.setText("Hello, jpackage!\nRunning on JavaFX: " + javafxVersion + " and Java: " + javaVersion + ".");
    }
}
