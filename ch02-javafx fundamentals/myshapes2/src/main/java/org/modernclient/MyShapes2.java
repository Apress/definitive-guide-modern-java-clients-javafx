package org.modernclient;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.When;
import javafx.beans.value.ObservableObjectValue;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Reflection;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Ellipse;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;


public class MyShapes2 extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        // Define a LinearGradient
        Stop[] stops = new Stop[] { new Stop(0, Color.DODGERBLUE),
                new Stop(0.5, Color.LIGHTBLUE),
                new Stop(1.0, Color.LIGHTGREEN)};
        LinearGradient gradient = new LinearGradient(1, 1, 1, 0, true,
                CycleMethod.NO_CYCLE, stops);

        // Create an Ellipse, set dropshadow, fill with gradient
        Ellipse ellipse = new Ellipse(110, 70);
        ellipse.setEffect(new DropShadow(30, 10, 10, Color.GRAY));
        ellipse.setFill(gradient);

        // Create a Text shape
        Text text = new Text("My Shapes");
        text.setFont(new Font("Arial Bold", 24));

        Reflection r = new Reflection();
        r.setFraction(0.8);
        r.setTopOffset(5.0);
        text.setEffect(r);

        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(ellipse, text);

        // Define RotateTransition for stackPane
        RotateTransition rotate = new RotateTransition(Duration.millis(2500), stackPane);
        rotate.setToAngle(360);
        rotate.setFromAngle(0);
        rotate.setInterpolator(Interpolator.LINEAR);

        // text.textProperty().bind(stackPane.rotateProperty().asString("%.1f"));

        // configure mouse click handler
        stackPane.setOnMouseClicked(mouseEvent -> {
            if (rotate.getStatus().equals(Animation.Status.RUNNING)) {
                rotate.pause();
            } else {
                rotate.play();
            }
        });

        Scene scene = new Scene(stackPane, 350, 230, Color.LIGHTYELLOW);
        //scene.getStylesheets().add("/styles/Styles.css");
        
        stage.setTitle("MyShapes with JavaFX");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}