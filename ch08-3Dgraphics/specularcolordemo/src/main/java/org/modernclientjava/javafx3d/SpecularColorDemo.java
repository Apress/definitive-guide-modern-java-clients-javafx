package org.modernclientjava.javafx3d;
  
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.PointLight;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import javafx.stage.Stage;

public class SpecularColorDemo extends Application {
    private View view;

    @Override
    public void start(Stage stage) throws Exception {
        view = new View();
        stage.setTitle("Specular Color Example");
        stage.setScene(view.scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private static class View {
        public Scene scene;
        public Sphere sphere;
        public PointLight light;

        private View() {
            sphere = new Sphere(100);
            PhongMaterial material = new PhongMaterial(Color.BLUE);
            material.setSpecularColor(Color.LIGHTBLUE);
            material.setSpecularPower(1.0d);
            sphere.setMaterial(material);
            sphere.setTranslateZ(300);

            light = new PointLight(Color.WHITE);

            Group group = new Group(sphere, light);
            group.setTranslateY(240);
            group.setTranslateX(320);
            scene = new Scene(group, 640, 480);
        }
    }
}

