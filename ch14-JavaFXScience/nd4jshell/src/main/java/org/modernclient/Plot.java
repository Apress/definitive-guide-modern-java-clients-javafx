package org.modernclient;

import javafx.application.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.stage.Stage;

public class Plot {

    public static void scatter(double[] x, double[] y, String title) {
        Platform.startup(() -> {});
        Platform.setImplicitExit(false);
        Platform.runLater( () -> {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        ScatterChart chart = new ScatterChart(xAxis, yAxis);
        ObservableList<XYChart.Series> chartData = FXCollections.observableArrayList();
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        ObservableList<Data<Number, Number>> data = FXCollections.observableArrayList();
        for (int i = 0; i < x.length; i++) {
            Data<Number, Number> d = new Data<>(x[i],y[i]);
            data.add(d);
        }
        series.setData(data);
        chartData.setAll(series);
        chart.setData(chartData);
        Scene s = new Scene(chart, 400, 400);
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.setScene(s);
        stage.show();
        });
    }

    public static void main(String[] args) {
        double[] x = new double[]{0.,1.,2.};
        double[] y = new double[]{0.,10.,16.};
        scatter(x, y, "plot");
    }

}

