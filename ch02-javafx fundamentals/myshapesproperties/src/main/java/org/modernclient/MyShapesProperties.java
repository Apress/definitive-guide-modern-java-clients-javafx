package org.modernclient;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.When;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableObjectValue;
import javafx.beans.value.ObservableValue;
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


public class MyShapesProperties extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Build scene graph
        // Define a LinearGradient
        Stop[] stops = new Stop[] { new Stop(0, Color.DODGERBLUE),
                new Stop(0.5, Color.LIGHTBLUE),
                new Stop(1.0, Color.LIGHTGREEN)};
        LinearGradient gradient = new LinearGradient(1, 1, 1, 0, true,
                CycleMethod.NO_CYCLE, stops);

        // Create an Ellipse, set dropshadow, fill with gradient
        Ellipse ellipse = new Ellipse(110, 70);
        ellipse.setEffect(new DropShadow(30, 10, 10, Color.GRAY));
        //ellipse.setFill(Color.LIGHTBLUE);
        ellipse.setFill(gradient);

        // Create a Text shape
        Text text = new Text("My Shapes");
        text.setFont(new Font("Arial Bold", 24));
        // Create a second Text shape
        Text text2 = new Text("Animation Status: ");
        text2.setFont(new Font("Arial Bold", 18));

        Reflection r = new Reflection();
        r.setFraction(0.8);
        r.setTopOffset(5.0);
        text.setEffect(r);

        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(ellipse, text);
        VBox vBox = new VBox(stackPane, text2);
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(50.0);

        // Define RotateTransition for stackPane
        RotateTransition rotate = new RotateTransition(Duration.millis(2500), stackPane);
        rotate.setToAngle(360);
        rotate.setFromAngle(0);
        rotate.setInterpolator(Interpolator.LINEAR);

        // Invalidation Listener using lambda expression
        /*rotate.statusProperty().addListener(observable -> {
            text2.setText("Animation status: " +
                    ((ObservableObjectValue<Animation.Status>)observable).getValue());
            text2.setText("Animation status: " + rotate.getStatus());
        });*/

        // Invalidation Listener using anonymous class
        /*rotate.statusProperty().addListener(new InvalidationListener() {
            @Override
            public  void invalidated(Observable observable) {
                text2.setText("Animation status: " +
                        ((ObservableObjectValue<Animation.Status>)observable).getValue());
            }

        });*/

        // Change Listener using lambda expression
        rotate.statusProperty().addListener((observableValue, oldValue, newValue) -> {
            text2.setText("Was " + oldValue + ", Now " + newValue);
        });

        // Change Listener using anonymous class
        /*rotate.statusProperty().addListener(new ChangeListener<Animation.Status>() {
            @Override
            public void changed(ObservableValue<? extends Animation.Status> observableValue,
                                Animation.Status oldValue, Animation.Status newValue) {
                text2.setText("Was " + oldValue + ", Now " + newValue);

            }
        });*/

        // Bind expression with When
        text2.strokeProperty().bind(new When(rotate.statusProperty()
                .isEqualTo(Animation.Status.RUNNING)).then(Color.GREEN).otherwise(Color.RED));

        // Bind expression
        //text2.rotateProperty().bind(stackPane.rotateProperty());

        // Bidirectional Bind
        //text2.textProperty().bindBidirectional(text.textProperty());

        // Bind with fluent API
        //text2.textProperty().bind(stackPane.rotateProperty().asString("%.1f"));

        // configure mouse click handler
        stackPane.setOnMouseClicked(mouseEvent -> {
            if (rotate.getStatus().equals(Animation.Status.RUNNING)) {
                rotate.pause();
            } else {
                rotate.play();
            }
        });

        Scene scene = new Scene(vBox, 350, 350, Color.LIGHTYELLOW);
        //scene.getStylesheets().add("/styles/Styles.css");
        
        stage.setTitle("MyShapesProperties");
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