package org.modernclients.raspberrypi.gps;

import com.airhacks.afterburner.injection.Injector;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.modernclients.raspberrypi.gps.view.UIPresenter;
import org.modernclients.raspberrypi.gps.view.UIView;


public class MainApp extends Application {

    private UIPresenter controller;

    @Override
    public void start(Stage stage) throws Exception {
        Rectangle2D bounds = Screen.getPrimary().getBounds();

        UIView ui = new UIView();
        controller = (UIPresenter) ui.getPresenter();

        Scene scene = new Scene(ui.getView(), bounds.getWidth(), bounds.getHeight());
        stage.setTitle("Embedded Maps");
        stage.setScene(scene);
        stage.show();
    }
    
    @Override
    public void stop() throws Exception {
        controller.stop();
        Injector.forgetAll();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
