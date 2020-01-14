package org.modernjavafx.ml.ui;


import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import org.datavec.image.loader.NativeImageLoader;
import org.datavec.image.transform.ColorConversionTransform;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.layers.objdetect.DetectedObject;
import org.deeplearning4j.zoo.model.YOLO2;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.preprocessor.ImagePreProcessingScaler;

public class DetectObjectsInVideoImproved extends Application {

    private static final double APP_WIDTH = 800;
    private static final double APP_HEIGHT = 600;
    
    private static final String TARGET_VIDEO = "/amsterdam_30.mp4";
    
    private static final double THRESHOLD = 0.65d;
    
    private static final String[] LABELS = { "person", "bicycle", "car", "motorbike", "aeroplane", "bus", "train",
            "truck", "boat", "traffic light", "fire hydrant", "stop sign", "parking meter", "bench", "bird", "cat",
            "dog", "horse", "sheep", "cow", "elephant", "bear", "zebra", "giraffe", "backpack", "umbrella", "handbag",
            "tie", "suitcase", "frisbee", "skis", "snowboard", "sports ball", "kite", "baseball bat", "baseball glove",
            "skateboard", "surfboard", "tennis racket", "bottle", "wine glass", "cup", "fork", "knife", "spoon", "bowl",
            "banana", "apple", "sandwich", "orange", "broccoli", "carrot", "hot dog", "pizza", "donut", "cake", "chair",
            "sofa", "pottedplant", "bed", "diningtable", "toilet", "tvmonitor", "laptop", "mouse", "remote", "keyboard",
            "cell phone", "microwave", "oven", "toaster", "sink", "refrigerator", "book", "clock", "vase", "scissors",
            "teddy bear", "hair drier", "toothbrush" };
    
    
    private final int INPUT_WIDTH = 608;
    private final int INPUT_HEIGHT = 608;
    private final int INPUT_CHANNELS = 3;
    private final int GRID_W = INPUT_WIDTH / 32;
    private final int GRID_H = INPUT_HEIGHT / 32;
    
    private final double FRAMES_PER_SECOND = 20d;
    
    Map<String, Boolean> trackTasks = new HashMap<>();
    Map<String, Color> colors = new HashMap<>();

    private WritableImage writableImage;
    private NativeImageLoader imageLoader;
    private Pane pane;
    
    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        
        for (int i = 0; i < LABELS.length; i++) {
            colors.put(LABELS[i], Color.hsb((i + 1) * 20, 0.5, 1.0));
        }
        
        var yoloModel = (ComputationGraph)  YOLO2.builder().build().initPretrained();
        String videoPath = DetectObjectsInVideoImproved.class.getResource(TARGET_VIDEO).toString();
        imageLoader = new NativeImageLoader(INPUT_WIDTH, INPUT_HEIGHT, INPUT_CHANNELS,
                new ColorConversionTransform(4));
        
        var media = new Media(videoPath);
        var mp = new MediaPlayer(media);
        var view = new MediaView(mp);
        
        Label lblProgress = new Label();
        lblProgress.setTextFill(Color.LIGHTGRAY);
        
        view.setFitWidth(APP_WIDTH);
        view.setFitHeight(APP_HEIGHT);
        view.setPreserveRatio(false);
        
        pane = new Pane();
        pane.setMinWidth(APP_WIDTH);
        pane.setMinHeight(APP_HEIGHT);
        
        var root = new StackPane(view, pane, lblProgress);
        
        StackPane.setAlignment(lblProgress, Pos.BOTTOM_CENTER);
        
        stage.setScene(new Scene(root, APP_WIDTH, APP_HEIGHT));
        stage.show();
        stage.setTitle("Detect Objects");
        pane.setOnMouseClicked(e -> {
            if (mp.getStatus() == Status.PLAYING) {
                mp.pause();
            } else if (mp.getStatus() == Status.PAUSED) {
                mp.play();
            } else if (mp.getStatus() == Status.STOPPED) {
                mp.seek(mp.getStartTime());
                mp.play();
            }
        });
        mp.setOnEndOfMedia(() -> { 
            mp.stop();
            pane.getChildren().forEach(c -> c.setVisible(false));
        });
        
        mp.play();
        
        var finishedTasks = new AtomicInteger();
        var previousFrame = new AtomicLong(-1);
        mp.currentTimeProperty().addListener((obs, o, n) -> {
            if(n.toMillis() < 50d) return;
            Long millis = Math.round(n.toMillis() / (1000d / FRAMES_PER_SECOND));
            final var nodeId = millis.toString();
            if(millis  == previousFrame.get()) {
                return;
            }
            previousFrame.set(millis);
            trackTasks.computeIfAbsent(nodeId, v -> {
                var scaledImage = getScaledImage(view);
                PredictFrameTask target = new PredictFrameTask(yoloModel, scaledImage);
                target.setOnSucceeded(e -> {
                    var detectedObjectGroup = getNodesForTask(nodeId, target);
                    Platform.runLater(() -> pane.getChildren().add(detectedObjectGroup));
                    updateProgress(lblProgress, trackTasks.size(), finishedTasks.incrementAndGet());
                });
                Thread thread = new Thread(target);
                thread.setDaemon(true);
                thread.start();
                return true;
            });
            updateProgress(lblProgress, trackTasks.size(), finishedTasks.get());
            pane.getChildren().forEach(node -> node.setVisible(false));
            Optional.ofNullable(pane.lookup("#" + nodeId)).ifPresent(node -> node.setVisible(true));
        }); 
    }

    private void updateProgress(Label lblProgress, int total, int progress) {
        if (total != progress) {
            lblProgress.setText("Total tasks / finished tasks " + total + " / " + progress);
        } else if (lblProgress.isVisible()) {
            lblProgress.setText("Finished  " + total + " tasks.");
        }
    }

    private BufferedImage getScaledImage(Node targetNode) {
        writableImage = new WritableImage((int) targetNode.getBoundsInLocal().getWidth(), (int) targetNode.getBoundsInLocal().getHeight());
        targetNode.snapshot(null, writableImage);
        Image tmp = SwingFXUtils.fromFXImage(writableImage, null).getScaledInstance(INPUT_WIDTH, INPUT_HEIGHT, Image.SCALE_SMOOTH);
        BufferedImage scaledImg = new BufferedImage(INPUT_WIDTH, INPUT_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = scaledImg.getGraphics();
        graphics.drawImage(tmp, 0, 0, null);
        graphics.dispose();
        return scaledImg;
    }
    
    private Group getNodesForTask(final String nodeId, PredictFrameTask target) {
        try {
            var predictedObjects = target.get();
            var detectedObjectGroup = getPredictionNodes(predictedObjects);
            detectedObjectGroup.setId(nodeId);
            detectedObjectGroup.setVisible(false);
            return detectedObjectGroup;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } 
    }
    
    private Group getPredictionNodes(List<DetectedObject> objs) {
        Group grpObject = new Group();
        objs.stream().map(this::createNodesForDetectedObject)
                     .flatMap(l -> l.stream())
                     .forEach(grpObject.getChildren()::add);
        return grpObject;
    }

    private List<Node> createNodesForDetectedObject(DetectedObject obj) {
        double[] xy1 = obj.getTopLeftXY();
        double[] xy2 = obj.getBottomRightXY();
        
        var w  = INPUT_WIDTH;
        var h  = INPUT_HEIGHT;
        var wScale  = (APP_WIDTH / w);
        var hScale  = (APP_HEIGHT / h);
        var x1 = (w * xy1[0] / GRID_W) * wScale;
        var y1 = (h * xy1[1] / GRID_H) * hScale;
        var x2 = (w * xy2[0] / GRID_W) * wScale;
        var y2 = (h * xy2[1] / GRID_H) * hScale;
        var rectW = x2 - x1;
        var rectH = y2 - y1;
        
        var label = LABELS[obj.getPredictedClass()];
        Rectangle rect = new Rectangle(x1, y1, rectW, rectH);
        rect.setFill(Color.TRANSPARENT);
        Color color = colors.get(label);
        rect.setStroke(color);
        rect.setStrokeWidth(2);
        Label lbl = new Label(label);
        lbl.setTranslateX(x1 + 2);
        lbl.setTranslateY(y1 + 2);
        lbl.setTextFill(color);
        lbl.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.EXTRA_BOLD, FontPosture.ITALIC, 10));
        return List.of(rect, lbl);
    }
    
    public class PredictFrameTask extends Task<List<DetectedObject>> {
        
        ComputationGraph yoloModel;
        
        BufferedImage scaledImage;
        
        public PredictFrameTask(ComputationGraph yoloModel, BufferedImage scaledImage) {
            this.yoloModel = yoloModel;
            this.scaledImage = scaledImage;
        }

        @Override
        protected List<DetectedObject> call() throws Exception {
            return predictObjects();
        }
        
        private List<DetectedObject> predictObjects() {
            org.deeplearning4j.nn.layers.objdetect.Yolo2OutputLayer yout =
                    (org.deeplearning4j.nn.layers.objdetect.Yolo2OutputLayer)yoloModel.getOutputLayer(0);
            try {
                var imgMatrix = imageLoader.asMatrix(scaledImage);
                var scaler = new ImagePreProcessingScaler(0, 1);
                scaler.transform(imgMatrix);
                INDArray output = yoloModel.outputSingle(imgMatrix);
                return yout.getPredictedObjects(output, THRESHOLD);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        
    }

}
