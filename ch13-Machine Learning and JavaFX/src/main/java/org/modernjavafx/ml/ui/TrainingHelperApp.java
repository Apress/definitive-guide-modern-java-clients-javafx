package org.modernjavafx.ml.ui;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;
import org.modernjavafx.ml.model.AsyncScoreIterationListener;
import org.modernjavafx.ml.model.DL4JHelper;
import org.modernjavafx.ml.model.NeuralNetModel;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Spinner;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * An application to help training neural networks and follow the training progress.
 */
public class TrainingHelperApp extends Application {

    private static final int APP_WIDTH = 800;
    private static final int APP_HEIGHT = 600;

    ServiceLoader<NeuralNetModel> modelsLoader = ServiceLoader.load(NeuralNetModel.class);
    
    private static final int SEED = 1234;

    private XYChart.Series<Number, Number> scoreSeries;
    private XYChart.Series<Number, Number> precisionSeries;
    private XYChart.Series<Number, Number> accuracySeries;
    private XYChart.Series<Number, Number> f1Series;
    
    DirectoryChooser directoryChooser = new DirectoryChooser();
    FileChooser fileChooser = new FileChooser();

    private DoubleProperty progressProperty;
    private StringProperty txtProgress;
    
    final DecimalFormat evalutionFormat = new DecimalFormat("#0.000");
    final BooleanProperty runningProperty = new SimpleBooleanProperty();
    final Alert ERROR_ALERT = new Alert(AlertType.ERROR);
    private boolean stopRequested;
    private ObjectProperty<MultiLayerNetwork> lastModel = new SimpleObjectProperty<>();

    
    public static void main(String[] args) {
        launch();
    }
    
    @Override
    public void start(Stage stage) throws Exception {
        var topPane = buildTopPane();        
        
        var root = new BorderPane();
        var lineChart = buildCenterPane();
        var bottomPane = buildBottomPane();
        
        root.setTop(topPane);
        root.setCenter(lineChart);
        root.setBottom(bottomPane);
        Scene scene = new Scene(root, APP_WIDTH, APP_HEIGHT);
        scene.getStylesheets().add("./style.css");
        stage.setScene(scene);
        stage.setTitle("Training Helper");
        stage.show();
        stage.setOnCloseRequest(e -> System.exit(0));
    }

    private Parent buildCenterPane() {
        var evalutationChart = new LineChart<Number, Number>(new NumberAxis(), new NumberAxis());
        var scoreChart = new LineChart<Number, Number>(new NumberAxis(), new NumberAxis());

        scoreSeries = new XYChart.Series<>();
        scoreSeries.setName("Score");
        scoreChart.getData().add(scoreSeries);
        
        precisionSeries = new XYChart.Series<>();
        precisionSeries.setName("Precision");
        accuracySeries = new XYChart.Series<>();
        accuracySeries.setName("Accuracy");
        f1Series = new XYChart.Series<>();
        f1Series.setName("F1 Score");
        evalutationChart.getData().addAll(List.of(precisionSeries, accuracySeries, f1Series));
        
        return new HBox(10, scoreChart, evalutationChart);
    }
    
    private Parent buildBottomPane() {
        Label lblProgressText = new Label();
        lblProgressText.getStyleClass().add("statusLabel");
        txtProgress = lblProgressText.textProperty();
        txtProgress.set("Not running!");
        
        Pane pane = new Pane(lblProgressText);
        pane.setMinWidth(APP_WIDTH);
        lblProgressText.setMinWidth(APP_WIDTH);
        pane.setTranslateX(10);
        return pane;
    }

    private Parent buildTopPane() {
        var spChannels = new Spinner<Integer>(1, 3, 1);
        var spWidth = new Spinner<Integer>(1, 228, 28);
        var spHeight = new Spinner<Integer>(1, 228, 28);
        
        var hpTraining = new Hyperlink("Select");
        var hpTesting = new Hyperlink("Select");
        
        var spEpochs = new Spinner<Integer>(1, 100, 20);
        var spBatchSize = new Spinner<Integer>(1, 200, 54);
        var cmbNeuralNet = new ComboBox<String>();
        var btnRun = new Button();
        var progress = new ProgressIndicator(0);
        var btnSave = new Button();
        
        btnSave.setOnAction(this::exportModel);
        
        hpTraining.setOnMouseClicked(this::saveSelectedDir);
        hpTesting.setOnMouseClicked(this::saveSelectedDir);
        
        btnRun.setId("btnRun");
        btnSave.setId("btnSave");
        btnSave.disableProperty().bind(runningProperty.or(lastModel.isNull()));
        
        runningProperty.addListener(c -> {
            String newId = runningProperty.get() ? "btnStop" : "btnRun";
            btnRun.setId(newId);
        });
        
        progress.visibleProperty().bind(runningProperty);
        progressProperty = progress.progressProperty();
        modelsLoader.stream().map(Provider::get)
                             .map(NeuralNetModel::getId)
                             .forEach(cmbNeuralNet.getItems()::add);
        cmbNeuralNet.getSelectionModel().select(0);
        
        btnRun.setOnAction(e-> {
            if (runningProperty.get()) {
                stopRequested = true;
            } else {
                String id = cmbNeuralNet.getSelectionModel().getSelectedItem();
                int[] input = {spChannels.getValue(), spWidth.getValue(), spHeight.getValue()};
                File testingDir = (File) hpTesting.getUserData();
                File trainingDir = (File) hpTraining.getUserData();
                
                if (Objects.isNull(trainingDir)) {
                    badInputError("Please select a valid directory for training dataset");
                    return;
                }
                if (Objects.isNull(testingDir)) {
                    badInputError("Please select a valid directory for testing dataset");
                    return;
                }
                prepareForTraining(id, input, spEpochs.getValue(), spBatchSize.getValue(), trainingDir, testingDir);
            }
        });
        
        var trainingConf = new HBox(5, 
                            titledGroup("DataSet Location", new VBox(10), 
                                                            labeledNode("Training", hpTraining), 
                                                            labeledNode("Testing", hpTesting)), 
                            titledGroup("Input Dimensions", new VBox(5), 
                                                            labeledNode("Channels", spChannels), 
                                                            labeledNode("Width", spWidth),
                                                            labeledNode("Height", spHeight)),
                            titledGroup("Training Parameters", new VBox(5), 
                                                               labeledNode("Model", cmbNeuralNet),
                                                               labeledNode("Epochs", spEpochs), 
                                                               labeledNode("Batches", spBatchSize)));
        trainingConf.setAlignment(Pos.CENTER);
        HBox hBox = new HBox(10, btnRun, btnSave, progress);
        hBox.setAlignment(Pos.CENTER_RIGHT);
        return new VBox(5, trainingConf, hBox);
        
    }
    
    private void exportModel(ActionEvent event) {
        var source = (Button) event.getSource();
        var modelOutputFile = fileChooser.showSaveDialog(source.getScene().getWindow());
        if (modelOutputFile != null) {
            try {
                ModelSerializer.writeModel(lastModel.get(), modelOutputFile, true);
                status("Model saved to " + modelOutputFile.getAbsolutePath());
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    private HBox labeledNode(String label, Node node) {
        HBox hb = new HBox(10);
        var lbl = new Label(label);
        hb.getStyleClass().add("param-form");
        hb.getChildren().addAll(lbl, node);
        return hb;
    }

    private TitledPane titledGroup(String title, Pane parent, Node...child) {
        parent.getChildren().addAll(child);
        parent.getStyleClass().add("form-parent");
        var tpParameters = new TitledPane(title, new StackPane(parent));
        tpParameters.setCollapsible(false);
        return tpParameters;
    }
    
    private void prepareForTraining(String modelId, int[] inputShape, int epochs, int batchSize, File trainingDir, File testingDir) {
        status("Preparing for training...");
        runningProperty.set(true);
        try {
            DataSetIterator trainingIterator = DL4JHelper.createIterator(trainingDir, inputShape[1], inputShape[2], inputShape[0], batchSize, SEED);
            DataSetIterator testingIterator = DL4JHelper.createIterator(testingDir, inputShape[1], inputShape[2], inputShape[0], batchSize, SEED);
            var currentModel = getNeuralNetById(modelId).getModel(inputShape, trainingIterator.getLabels().size());
            System.out.println(trainingIterator.getLabels());
            lastModel.set(currentModel);
            currentModel.setListeners(new AsyncScoreIterationListener(this::updateScore));
            clearSeries();
            launchTrainingThread(epochs, trainingIterator, testingIterator);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void launchTrainingThread(int epochs, DataSetIterator trainingIterator, DataSetIterator testingIterator) {
        var currentModel = lastModel.get();
        new Thread(() -> {
                var result  = "";
                int epochNum = 0;
                for (int i = 0; i < epochs; i++) {
                    epochNum = (i +1);
                    currentModel.fit(trainingIterator);
                    status("Evaluating...");
                    Evaluation eval = currentModel.evaluate(testingIterator);
                    double progress = (double) i / (double) epochs;
                    var accuracy =  eval.accuracy();
                    var precision = eval.precision();
                    var f1 = eval.f1();
                    updateSeries(accuracySeries, epochNum, accuracy);
                    updateSeries(precisionSeries, epochNum, precision);
                    updateSeries(f1Series, epochNum, f1);
                    testingIterator.reset();
                    trainingIterator.reset();
                    result = "( A: " + evalutionFormat.format(accuracy)  + 
                             ", P: " + evalutionFormat.format(precision) + 
                             ", F1:" + evalutionFormat.format(f1) + " )";
                    if (stopRequested) {
                        status("Stop Requested on epoch "  + epochNum + ". Results: " + result);
                        stopRequested = false;
                        break;
                    } else {
                        status("Epoch " + epochNum  + "/" + epochs + " " + result);
                        setProgress(progress);
                    }
                }
                status("Process stoped at epoch " + epochNum  + ". Results: " + result);
                Platform.runLater(() -> runningProperty.set(false));
        }).start();
    }

    private NeuralNetModel getNeuralNetById(String modelId) {
        return modelsLoader.stream()
                           .map(Provider::get)
                           .filter(model -> model.getId().equals(modelId))
                           .findFirst().get();
    }
    
    private void saveSelectedDir(MouseEvent e) {
        Hyperlink targetLink = (Hyperlink) e.getSource();
        var dir = directoryChooser.showDialog(targetLink.getScene().getWindow());
        if (dir != null) {
             targetLink.setText(dir.getAbsolutePath());
             targetLink.setUserData(dir);
        }
    }

    private void setProgress(final double progress) {
        Platform.runLater(() ->  progressProperty.set(progress));
    }

    private void updateScore(Integer i, Double d) {
        updateSeries(scoreSeries, i, d);
    }
    
    private void updateSeries(XYChart.Series<Number, Number> series, Integer i, Double d) {
        Platform.runLater(() -> series.getData().add(new XYChart.Data<>(i, d)));
    }
    
    private void status(String txt) {
        Platform.runLater(() ->  txtProgress.set(txt));
    }

    private void clearSeries() {
        accuracySeries.getData().clear(); 
        scoreSeries.getData().clear();
    }
    
    private void badInputError(String message) {
        ERROR_ALERT.setTitle("Invalid Value");
        ERROR_ALERT.setHeaderText("Invalid Input");
        ERROR_ALERT.setContentText(message);
        ERROR_ALERT.showAndWait();
    }

}