package org.modernclient;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;


public class MyShapes extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        // Create an Ellipse and set fill color
        Ellipse ellipse = new Ellipse(110, 70);
        ellipse.setFill(Color.LIGHTBLUE);

        // Alternate color notations
        //ellipse.setFill(Color.web("#ADD8E680"));
        //ellipse.setFill(Color.web("0xADD8E680"));
        //ellipse.setFill(Color.rgb(173, 216, 230, .5));

        // Create a Text shape with font and size
        Text text = new Text("My Shapes");
        text.setFont(new Font("Arial Bold", 24));

        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(ellipse, text);

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