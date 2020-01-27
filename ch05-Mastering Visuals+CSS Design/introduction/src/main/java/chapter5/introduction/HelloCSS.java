package chapter5.introduction;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class HelloCSS extends Application {

    public static void main(String[] args) {
        Application.launch(HelloCSS.class, args);
    }

    public void start(Stage primaryStage) {

        Label label = new Label("Stylized label");
        VBox root = new VBox(label);
        Scene scene = new Scene(root, 200, 100);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());

        primaryStage.setTitle("My first CSS application");
        primaryStage.setScene(scene);
        primaryStage.show();

    }
}
 