package org.modernclientjava.javafx3d;
  
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import javafx.util.Duration;

public class PerspectiveCameraDemo extends Application {

    private final Rotate rotateX = new Rotate(-20, Rotate.X_AXIS);
    private final Rotate rotateY = new Rotate(-20, Rotate.Y_AXIS);
    private final Rotate rotateZ = new Rotate(-20, Rotate.Z_AXIS);
    private final Translate translateZ = new Translate(0, 0, -100);

    @Override
    public void start(Stage stage) throws Exception {
        Camera camera = createCamera();
        Box box = new Box(10, 10, 10);
        Group view = new Group(box, camera);
        Scene scene = new Scene(view, 640, 480);
        scene.setCamera(camera);
        stage.setTitle("PerspectiveCamera Example");
        stage.setScene(scene);
        stage.show();
        animate();
    }

    private Camera createCamera() {
        Camera camera = new PerspectiveCamera(true);
        camera.getTransforms().addAll(rotateX, rotateY, rotateZ, translateZ);
        return camera;
    }

    private void animate() {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0),
                        new KeyValue(translateZ.zProperty(), -20),
                        new KeyValue(rotateX.angleProperty(), 90),
                        new KeyValue(rotateY.angleProperty(), 90),
                        new KeyValue(rotateZ.angleProperty(), 90)),
                new KeyFrame(Duration.seconds(5),
                        new KeyValue(translateZ.zProperty(), -80),
                        new KeyValue(rotateX.angleProperty(), -90),
                        new KeyValue(rotateY.angleProperty(), -90),
                        new KeyValue(rotateZ.angleProperty(), -90))
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
