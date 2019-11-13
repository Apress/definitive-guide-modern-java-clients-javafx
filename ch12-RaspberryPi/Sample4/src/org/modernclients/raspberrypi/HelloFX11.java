package org.modernclients.raspberrypi;

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class HelloFX11 extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        String javaVersion = System.getProperty("java.version");
        String javafxVersion = System.getProperty("javafx.version");
        String platform = System.getProperty("glass.platform");
        Label l = new Label("Hello, JavaFX " + javafxVersion + ", running on Java " + javaVersion + ".");
        Rectangle2D bounds;
        if ("Monocle".equals(platform)) {
            bounds = Screen.getPrimary().getBounds();
        } else {
            bounds = new Rectangle2D(0, 0, 600, 400);
        }
        Scene scene = new Scene(new StackPane(l), bounds.getWidth(), bounds.getHeight());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
