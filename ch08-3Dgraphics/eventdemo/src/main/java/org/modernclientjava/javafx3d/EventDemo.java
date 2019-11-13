package org.modernclientjava.javafx3d;
  
import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import javafx.stage.Stage;

import static java.lang.Math.abs;
import static java.lang.Math.min;

public class EventDemo extends Application {
    private Model model;
    private View view;

    public EventDemo() {
        model = new Model();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        view = new View(model);
        primaryStage.setTitle("Sphere with MouseEvents");
        primaryStage.setScene(view.scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private static class Model {
        private ObjectProperty<Material> material = new SimpleObjectProperty<>(
                this, "material", new PhongMaterial());

        public Material getMaterial() {
            return material.get();
        }

        public ObjectProperty<Material> materialProperty() {
            return material;
        }

        public void setMaterial(Material material) {
            this.material.set(material);
        }
    }

    private static class View {
        public static final int SPHERE_RADIUS = 200;
        public Scene scene;
        public Sphere sphere;

        private View(Model model) {
            sphere = new Sphere(SPHERE_RADIUS);
            sphere.materialProperty().bind(model.materialProperty());

            EventHandler<MouseEvent> handler = event -> {
                PickResult pickResult = event.getPickResult();
                Point3D point = pickResult.getIntersectedPoint();
                model.setMaterial(new PhongMaterial(makeColorOutOfPoint3D(point)));
            };

            sphere.setOnMouseClicked(handler);
            sphere.setOnMouseDragged(handler);
            Group group = new Group(sphere);
            group.setTranslateX(320);
            group.setTranslateY(240);
            scene = new Scene(group, 640, 480);
        }

        private Color makeColorOutOfPoint3D(Point3D point) {
            double x = point.getX();
            double y = point.getY();
            double z = point.getZ();
            return Color.color(normalize(x), normalize(y), normalize(z));
        }
        private double normalize(double x) {
            return min(abs(x) / SPHERE_RADIUS, 1);
        }
    }
}

