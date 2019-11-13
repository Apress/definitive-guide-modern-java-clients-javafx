package org.modernclientjava.hello3d;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Sphere;
import javafx.stage.Stage;

public class Shapes extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Sphere sphere = new Sphere(50);
        Box box = new Box(40, 120, 60);
        PhongMaterial material = new PhongMaterial(Color.BLUE);
        material.setSpecularColor(Color.LIGHTBLUE);
        material.setSpecularPower(10.0d);
        box.setMaterial(material);
        box.setTranslateX(20);
        Group root = new Group(sphere, box);
        root.setTranslateX(320);
        root.setTranslateY(240);
        Scene scene = new Scene(root, 640, 480, true, SceneAntialiasing.BALANCED);
        stage.setTitle("JavaFX 3D Shapes");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        System.out.println("Hello, shapes");
        launch();
    }

}

