package org.modernclientjava.javafx3d;

import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

public class LightDemo extends Application {
    private Model model;
    private View view;

    public LightDemo() {
        model = new Model();
    }

    @Override
    public void start(Stage stage) throws Exception {
        view = new View(model);
        stage.setTitle("Light Example");
        stage.setScene(view.scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private static class Model {
        private DoubleProperty redLightX = new SimpleDoubleProperty(this, "redLightX", 20.0d);
        private DoubleProperty redLightY = new SimpleDoubleProperty(this, "redLightY", -15.0d);
        private DoubleProperty redLightZ = new SimpleDoubleProperty(this, "redLightZ", -20.0d);
        private DoubleProperty blueLightX = new SimpleDoubleProperty(this, "blueLightX", 15.0d);
        private DoubleProperty blueLightY = new SimpleDoubleProperty(this, "blueLightY", -15.0d);
        private DoubleProperty blueLightZ = new SimpleDoubleProperty(this, "blueLightZ", -5.0d);

        public DoubleProperty redLightXProperty() {
            return redLightX;
        }

        public DoubleProperty redLightYProperty() {
            return redLightY;
        }

        public DoubleProperty redLightZProperty() {
            return redLightZ;
        }

        public DoubleProperty blueLightXProperty() {
            return blueLightX;
        }

        public DoubleProperty blueLightYProperty() {
            return blueLightY;
        }

        public DoubleProperty blueLightZProperty() {
            return blueLightZ;
        }
    }

    private static class View {
        public Scene scene;

        public Box box;
        public PerspectiveCamera camera;
        public PointLight redLight;
        public PointLight blueLight;

        private final Rotate rotateX;
        private final Rotate rotateY;
        private final Rotate rotateZ;
        private final Translate translateZ;

        private View(Model model) {
            box = new Box(10, 10, 10);

            camera = new PerspectiveCamera(true);

            rotateX = new Rotate(-20, Rotate.X_AXIS);
            rotateY = new Rotate(-20, Rotate.Y_AXIS);
            rotateZ = new Rotate(-20, Rotate.Z_AXIS);
            translateZ = new Translate(0, 0, -50);

            camera.getTransforms().addAll(rotateX, rotateY, rotateZ, translateZ);

            redLight = new PointLight(Color.RED);
            redLight.translateXProperty().bind(model.redLightXProperty());
            redLight.translateYProperty().bind(model.redLightYProperty());
            redLight.translateZProperty().bind(model.redLightZProperty());

            blueLight = new PointLight(Color.BLUE);
            blueLight.translateXProperty().bind(model.blueLightXProperty());
            blueLight.translateYProperty().bind(model.blueLightYProperty());
            blueLight.translateZProperty().bind(model.blueLightZProperty());


            Group group = new Group(box, camera, redLight, blueLight);
            SubScene subScene = new SubScene(group, 640, 480, true, SceneAntialiasing.BALANCED);
            subScene.setCamera(camera);

            Slider redLightXSlider = createSlider(20);
            Slider redLightYSlider = createSlider(-20);
            Slider redLightZSlider = createSlider(-20);
            redLightXSlider.valueProperty().bindBidirectional(model.redLightXProperty());
            redLightYSlider.valueProperty().bindBidirectional(model.redLightYProperty());
            redLightZSlider.valueProperty().bindBidirectional(model.redLightZProperty());

            HBox hbox1 = new HBox(10, new Label("Red light x:"), redLightXSlider,
                    new Label("y:"), redLightYSlider,
                    new Label("z:"), redLightZSlider);
            hbox1.setPadding(new Insets(10, 10, 10, 10));
            hbox1.setAlignment(Pos.CENTER);

            Slider blueLightXSlider = createSlider(15);
            Slider blueLightYSlider = createSlider(-15);
            Slider blueLightZSlider = createSlider(-15);
            blueLightXSlider.valueProperty().bindBidirectional(model.blueLightXProperty());
            blueLightYSlider.valueProperty().bindBidirectional(model.blueLightYProperty());
            blueLightZSlider.valueProperty().bindBidirectional(model.blueLightZProperty());

            HBox hbox2 = new HBox(10, new Label("Blue light x:"), blueLightXSlider,
                    new Label("y:"), blueLightYSlider,
                    new Label("z:"), blueLightZSlider);
            hbox2.setPadding(new Insets(10, 10, 10, 10));
            hbox2.setAlignment(Pos.CENTER);

            VBox controlPanel = new VBox(10, hbox1, hbox2);
            controlPanel.setPadding(new Insets(10, 10, 10, 10));
            controlPanel.setAlignment(Pos.CENTER);
            BorderPane borderPane = new BorderPane(subScene, null, null, controlPanel, null);
            scene = new Scene(borderPane);
        }

        private Slider createSlider(double value) {
            Slider slider = new Slider(-30, 30, value);
            slider.setShowTickMarks(true);
            slider.setShowTickLabels(true);
            return slider;
        }
    }
}
