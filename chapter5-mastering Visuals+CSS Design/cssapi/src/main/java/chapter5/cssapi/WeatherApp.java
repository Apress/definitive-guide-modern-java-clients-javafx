package chapter5.cssapi;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class WeatherApp extends Application {
    @Override
    public void start(Stage primaryStage)  {

        WeatherIcon rain = new WeatherIcon();
        rain.getStyleClass().add("rain");

        WeatherIcon thunderstorm = new WeatherIcon();
        thunderstorm.getStyleClass().add("thunderstorm");

        WeatherIcon clouds = new WeatherIcon( WeatherType.CLOUDY);

        VBox root = new VBox(10, rain, thunderstorm, clouds);
        root.setAlignment(Pos.CENTER);

        Scene scene = new Scene( root);
        scene.getStylesheets().add( getClass().getResource("styles.css").toExternalForm());

        primaryStage.setTitle("WeatherType Application");
        primaryStage.setScene(scene);
        primaryStage.show();

    }
}