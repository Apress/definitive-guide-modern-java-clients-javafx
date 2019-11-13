package org.modernclients.raspberrypi.gps.view;

import com.gluonhq.maps.MapPoint;
import com.gluonhq.maps.MapView;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.modernclients.raspberrypi.gps.model.GPSPosition;
import org.modernclients.raspberrypi.gps.service.GPSService;

import javax.inject.Inject;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class UIPresenter {

    private static final Logger logger = Logger.getLogger(UIPresenter.class.getName());

    @FXML private BorderPane pane;

    @FXML private Label statusLabel;
    @FXML private MapView mapView;
    @FXML private ListView<String> listView;

    @FXML private Label timeLabel;
    @FXML private Label positionLabel;
    @FXML private Label altitudeLabel;
    @FXML private Label directionLabel;
    @FXML private Label speedLabel;
    @FXML private Label qualityLabel;
    @FXML private Label satellitesLabel;

    @FXML private ToggleButton showLog;

    @FXML private ResourceBundle resources;
    @Inject private GPSService service;
    @Inject private GPSPosition gpsPosition;

    private MapPoint mapPoint;

    public void initialize() {
        logger.info("Platform: " + System.getProperty("embedded"));

        mapView = new MapView();
        mapPoint = new MapPoint(50.0d, 4.0d);
        mapView.setCenter(mapPoint);
        mapView.setZoom(15);

        PoiLayer poiLayer = new PoiLayer();
        poiLayer.addPoint(mapPoint, new Circle(7, Color.RED));
        mapView.addLayer(poiLayer);
        pane.setCenter(mapView);

        service.lineProperty().addListener((obs, ov, nv) -> {
            logger.fine(nv);
            listView.getItems().add(nv);
            listView.scrollTo(listView.getItems().size() - 1);
            if (listView.getItems().size() > 100) {
                listView.getItems().remove(0);
            }
        });

        gpsPosition.timeProperty().addListener((obs, ov, nv) -> {
            statusLabel.setText(MessageFormat.format(resources.getString("label.gps"), gpsPosition.isFixed() ?
                    resources.getString("label.gps.fixed") : resources.getString("label.gps.not-fixed")));
            mapPoint.update(gpsPosition.getLatitude(), gpsPosition.getLongitude());
            mapView.setCenter(mapPoint);
        });

        timeLabel.textProperty().bind(Bindings.createStringBinding(() -> {
            float time = gpsPosition.getTime();
            int hour = (int) (time / 10000f);
            int min = (int) ((time - hour * 10000) / 100f);
            int sec = (int) (time - hour * 10000 - min * 100);
            return String.format("%02d:%02d:%02d UTC", hour, min, sec);
        }, gpsPosition.timeProperty()));
        positionLabel.textProperty().bind(Bindings.format("%.6f, %.6f", gpsPosition.latitudeProperty(), gpsPosition.longitudeProperty()));
        altitudeLabel.textProperty().bind(Bindings.format("%.1f m", gpsPosition.altitudeProperty()));
        speedLabel.textProperty().bind(Bindings.format("%.2f m/s", gpsPosition.velocityProperty()));
        directionLabel.textProperty().bind(Bindings.format("%.2f º", gpsPosition.directionProperty()));
        qualityLabel.textProperty().bind(Bindings.format("%d", gpsPosition.qualityProperty()));
        satellitesLabel.textProperty().bind(Bindings.format("%d", gpsPosition.satellitesProperty()));

        statusLabel.setText(MessageFormat.format(resources.getString("label.gps"), resources.getString("label.gps.not-fixed")));
        listView.managedProperty().bind(listView.visibleProperty());
        listView.visibleProperty().bind(showLog.selectedProperty());
        showLog.setSelected(false);
    }

    public void stop() {
        service.stop();
    }

    @FXML private void onExit(){
        Platform.exit();
    }

    @FXML private void onZoomIn() {
        if (mapView.getZoom() < 19) {
            mapView.setZoom(mapView.getZoom() + 1);
        }
    }

    @FXML private void onZoomOut() {
        if (mapView.getZoom() > 1) {
            mapView.setZoom(mapView.getZoom() - 1);
        }
    }
}
