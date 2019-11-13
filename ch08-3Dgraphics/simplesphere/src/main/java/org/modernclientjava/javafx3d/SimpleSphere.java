package org.modernclientjava.javafx3d;
  
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.shape.Sphere;
import javafx.stage.Stage;

public class SimpleSphere extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Sphere sphere = new Sphere(50);
        Label label = new Label("Hello, JavaFX 3D");
        label.setTranslateY(80);
        Group root = new Group(label, sphere);
        root.setTranslateX(320);
        root.setTranslateY(240);
        Scene scene = new Scene(root, 640, 480);
        stage.setTitle("JavaFX 3D Sphere");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}

