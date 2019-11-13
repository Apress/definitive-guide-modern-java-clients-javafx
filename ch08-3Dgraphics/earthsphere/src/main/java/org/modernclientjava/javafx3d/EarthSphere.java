package org.modernclientjava.javafx3d;
  
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

public class EarthSphere extends Application {

    double anchorX, anchorY;

    private double anchorAngleX = 0;
    private double anchorAngleY = 0;

    private final DoubleProperty angleX = new SimpleDoubleProperty(0);
    private final DoubleProperty angleY = new SimpleDoubleProperty(0);

    PerspectiveCamera scenePerspectiveCamera = new PerspectiveCamera(false);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("EarthSphere");

        Image diffuseMap = new Image(EarthSphere.class
                .getResource("/earth-mercator.jpg")
                .toExternalForm());

        PhongMaterial earthMaterial = new PhongMaterial();
        earthMaterial.setDiffuseMap(diffuseMap);

        final Sphere earth = new Sphere(400);
        earth.setMaterial(earthMaterial);

        final Group parent = new Group(earth);
        parent.setTranslateX(450);
        parent.setTranslateY(450);
        parent.setTranslateZ(100);

        Rotate xRotate;
        Rotate yRotate;
        parent.getTransforms().setAll(
                xRotate = new Rotate(0, Rotate.X_AXIS),
                yRotate = new Rotate(0, Rotate.Y_AXIS)
        );
        xRotate.angleProperty().bind(angleX);
        yRotate.angleProperty().bind(angleY);

        final Group root = new Group();
        root.getChildren().add(parent);

        final Scene scene = new Scene(root, 900, 900, true);
        scene.setFill(Color.BLACK);

        scene.setOnMousePressed((MouseEvent event) -> {
            anchorX = event.getSceneX();
            anchorY = event.getSceneY();

            anchorAngleX = angleX.get();
            anchorAngleY = angleY.get();
        });

        scene.setOnMouseDragged((MouseEvent event) -> {
            angleY.set(anchorAngleY + anchorX - event.getSceneX());
        });

        PointLight pointLight = new PointLight(Color.WHITE);
        pointLight.setTranslateX(400);
        pointLight.setTranslateY(400);
        pointLight.setTranslateZ(-3000);

        scene.setCamera(scenePerspectiveCamera);

        root.getChildren().addAll(pointLight, scenePerspectiveCamera);

        stage.setScene(scene);

        stage.show();
    }
}

