package org.modernclientjava.javafx3d;
  
import javafx.application.Application;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import javafx.stage.Stage;

public class SphereCylinderBox extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Sphere sphere= new Sphere(50);
        sphere.setTranslateX(-100);
        Box box = new Box(40,50,60);
        Cylinder cylinder = new Cylinder(50, 80);
        cylinder.setTranslateX(100);
        Group root = new Group(sphere, box, cylinder);
        root.setRotationAxis(new Point3D(.2,.5,.7));
        root.setRotate(45);
        root.setTranslateX(320);
        root.setTranslateY(240);
        Scene scene = new Scene(root, 640, 480);

        stage.setTitle("JavaFX 3D Sphere, Cube, Box");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) { 
        launch();
    }

}

