package org.modernjavafx.ml.ui;


import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
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

public class DetectObjectsInVideo extends Application {

    private static final double THRESHOLD = 0.4d;
    private static final int APP_WIDTH = 608;
    private static final int APP_HEIGHT = 608;
    
    private final String[] COCO_CLASSES = { "person", "bicycle", "car", "motorbike", "aeroplane", "bus", "train",
            "truck", "boat", "traffic light", "fire hydrant", "stop sign", "parking meter", "bench", "bird", "cat",
            "dog", "horse", "sheep", "cow", "elephant", "bear", "zebra", "giraffe", "backpack", "umbrella", "handbag",
            "tie", "suitcase", "frisbee", "skis", "snowboard", "sports ball", "kite", "baseball bat", "baseball glove",
            "skateboard", "surfboard", "tennis racket", "bottle", "wine glass", "cup", "fork", "knife", "spoon", "bowl",
            "banana", "apple", "sandwich", "orange", "broccoli", "carrot", "hot dog", "pizza", "donut", "cake", "chair",
            "sofa", "pottedplant", "bed", "diningtable", "toilet", "tvmonitor", "laptop", "mouse", "remote", "keyboard",
            "cell phone", "microwave", "oven", "toaster", "sink", "refrigerator", "book", "clock", "vase", "scissors",
            "teddy bear", "hair drier", "toothbrush" };
    
    Map<Long, PredictFrameTask> predictObjectTasks = new ConcurrentHashMap<>();
    
    
    private final int INPUT_WIDTH = 608;
    private final int INPUT_HEIGHT = 608;
    private final int INPUT_CHANNELS = 3;
    private final int GRID_W = INPUT_WIDTH / 32;
    private final int GRID_H = INPUT_HEIGHT / 32;
    
    
    Map<String, Color> colors = new HashMap<>();

    private NativeImageLoader imageLoader;
    private Pane pane;
    
    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        
        for (int i = 0; i < COCO_CLASSES.length; i++) {
            colors.put(COCO_CLASSES[i], Color.hsb((i + 1) * 20, 0.5, 1.0));
        }
        
        var yoloModel = (ComputationGraph)  YOLO2.builder().build().initPretrained();
        String videoPath = DetectObjectsInVideo.class.getResource("/timesquare_small.mp4").toString();
        imageLoader = new NativeImageLoader(INPUT_WIDTH, INPUT_HEIGHT, INPUT_CHANNELS,
                new ColorConversionTransform(4));
        
        var media = new Media(videoPath);
        var mediaPlayer = new MediaPlayer(media);
        var view = new MediaView(mediaPlayer);
        
        view.setFitWidth(APP_WIDTH);
        view.setFitHeight(APP_HEIGHT);
        view.setPreserveRatio(false);
        
        pane = new Pane();
        pane.setMinWidth(APP_WIDTH);
        pane.setMinHeight(APP_HEIGHT);
        
        pane.setMaxWidth(APP_WIDTH);
        pane.setMaxHeight(APP_HEIGHT);
        
        var root = new StackPane(view, pane);
        
        stage.setScene(new Scene(root, APP_WIDTH, APP_HEIGHT));
        stage.show();
        stage.setTitle("Detect Objects");
        
        mediaPlayer.currentTimeProperty().addListener((obs, o, n) -> {
            if(n.toMillis() < 100d) return;
            long millis = Math.round(n.toMillis() / 100d);
            
            System.out.println("Total tasks " + predictObjectTasks.values().size() + 
                               ". Running tasks: " + predictObjectTasks.values().stream().filter(Task::isRunning).count());
            
            pane.getChildren().clear();
            pane.lookup("");
            predictObjectTasks.computeIfAbsent(millis, v -> {
                System.out.println("Scheduling task for " + millis);
                var scaledImage = getScaledImage(view);
                PredictFrameTask target = new PredictFrameTask(yoloModel, scaledImage);
                new Thread(target).start();
                return target;
            });
            PredictFrameTask predictFrameTask = predictObjectTasks.get(millis);
            if(predictFrameTask != null && predictFrameTask.isDone()) {
                try {
                    List<DetectedObject> predictedObjects = predictFrameTask.get();
                    var predictionNodes = getPredictionNodes(predictedObjects);
                    pane.getChildren().addAll(predictionNodes);
                } catch ( Exception e) {
                    e.printStackTrace();
                }
            }
        }); 
        
        pane.setOnMouseClicked(e -> {
            if (mediaPlayer.getStatus() == Status.PLAYING) {
                mediaPlayer.pause();
            } else if (mediaPlayer.getStatus() == Status.PAUSED) {
                mediaPlayer.play();
            }
        });
        mediaPlayer.play();
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        
    }

    private BufferedImage getScaledImage(Node targetNode) {
        var writableImage = new WritableImage((int) targetNode.getBoundsInLocal().getWidth(), (int) targetNode.getBoundsInLocal().getHeight());
        targetNode.snapshot(null, writableImage);
        Image tmp = SwingFXUtils.fromFXImage(writableImage, null).getScaledInstance(INPUT_WIDTH, INPUT_HEIGHT, Image.SCALE_SMOOTH);
        BufferedImage scaledImg = new BufferedImage(INPUT_WIDTH, INPUT_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = scaledImg.getGraphics();
        graphics.drawImage(tmp, 0, 0, null);
        graphics.dispose();
        return scaledImg;
    }
    
    private List<Node> getPredictionNodes(List<DetectedObject> objs) {
        return objs.stream().map(this::createNodesForDetectedObject).flatMap(l -> l.stream()).collect(Collectors.toList());
    }

    private List<Node> createNodesForDetectedObject(DetectedObject obj) {
        double[] xy1 = obj.getTopLeftXY();
        double[] xy2 = obj.getBottomRightXY();
        
        var w  = (APP_WIDTH / INPUT_WIDTH) * INPUT_WIDTH;
        var h  = (APP_HEIGHT / INPUT_HEIGHT) * INPUT_HEIGHT;
        var x1 = w * xy1[0] / GRID_W;
        var y1 = h * xy1[1] / GRID_H;
        var x2 = w * xy2[0] / GRID_W;
        var y2 = h * xy2[1] / GRID_H;
        var rectW = x2 - x1;
        var rectH = y2 - y1;
        
        var label = COCO_CLASSES[obj.getPredictedClass()];
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
