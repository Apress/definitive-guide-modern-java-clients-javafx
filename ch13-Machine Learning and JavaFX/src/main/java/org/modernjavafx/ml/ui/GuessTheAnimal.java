package org.modernjavafx.ml.ui;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import javafx.application.Application;
import javafx.beans.property.StringProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.datavec.image.loader.NativeImageLoader;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.api.ndarray.INDArray;

import static java.util.stream.Collectors.toMap;

public class GuessTheAnimal extends Application {

    private static final String MODEL_PATH = "/quickdraw-model-15-68.zip";
    private static final String CLASSES[] = {"Bee", "Bird", "Cat", "Dog", "Duck", "Elephant", "Fish", "Frog",
                                             "Horse", "Lion", "Mouse", "Pig", "Rabbit", "Snake", "Spider"};
    private static final int INPUT_WIDTH = 28;
    private static final int INPUT_HEIGHT = 28;
    private static final Double THRESHOLD = 0.1d;

    private static final int APP_WIDTH = 800;
    private static final int APP_HEIGHT = 600;

    private GraphicsContext ctx;
    private NativeImageLoader loader;
    private MultiLayerNetwork model;

    private StringProperty txtOutput;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        initModelAndLoader();
        var root = buildUI();
        stage.setTitle("Guess the Animal");
        stage.setScene(new Scene(root, APP_WIDTH, APP_HEIGHT));
        stage.show();
        clearCanvas();
    }

    private StackPane buildUI() {
        var canvas = new Canvas(APP_WIDTH, APP_HEIGHT);
        var btnGuess = new Button("Guess!");
        var lblOutput = new Label("");
        var root = new StackPane(canvas, btnGuess, lblOutput);

        lblOutput.setTextFill(Color.RED);

        txtOutput = lblOutput.textProperty();
        ctx = canvas.getGraphicsContext2D();
        ctx.setLineWidth(30);

        canvas.setOnMouseDragged(e -> {
            ctx.setFill(Color.BLACK);
            ctx.fillOval(e.getX() - 15, e.getY() - 15, 30, 30);
        });

        canvas.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                clearCanvas();
            }
        });

        btnGuess.setOnAction(evt -> {
            var predictions = predictCanvasContent();
            var pairs = sortAndMap(predictions);
            txtOutput.set(pairs.toString());
        });

        StackPane.setAlignment(btnGuess, Pos.BOTTOM_LEFT);
        StackPane.setAlignment(lblOutput, Pos.BOTTOM_RIGHT);
        return root;
    }

    private List<Pair<String, Double>> sortAndMap(Map<String, Double> predictions) {
        var pairs = predictions.entrySet().stream()
                               .map(e -> new Pair<String, Double>(e.getKey(), e.getValue()))
                               .collect(Collectors.toList());
        Comparator<Pair<String, Double>> comparing = Comparator.comparing(Pair::getValue);
        Collections.sort(pairs, comparing.reversed());
        return pairs;
    }

    private void initModelAndLoader() throws IOException {
        model = ModelSerializer
                               .restoreMultiLayerNetwork(GuessTheAnimal.class.getResourceAsStream(MODEL_PATH));
        model.init();
        loader = new NativeImageLoader(INPUT_WIDTH, INPUT_HEIGHT, 1, true);
    }

    private void clearCanvas() {
        ctx.setFill(Color.WHITE);
        ctx.fillRect(0, 0, ctx.getCanvas().getWidth(), ctx.getCanvas().getHeight());
        txtOutput.set("");
    }

    private Map<String, Double> predictCanvasContent() {
        try {
            var img = getScaledImage();
            INDArray image = loader.asMatrix(img);
            INDArray output = model.output(image);
            double[] doubleVector = output.toDoubleVector();
            var results = new HashMap<String, Double>();
            for (int i = 0; i < doubleVector.length; i++) {
                results.put(CLASSES[i], doubleVector[i]);
            }
            return results.entrySet().stream()
                          .filter(e -> e.getValue() > THRESHOLD)
                          .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private BufferedImage getScaledImage() {
        var canvas = ctx.getCanvas();
        WritableImage writableImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
        canvas.snapshot(null, writableImage);
        Image tmp = SwingFXUtils.fromFXImage(writableImage, null).getScaledInstance(INPUT_WIDTH, INPUT_HEIGHT, Image.SCALE_SMOOTH);
        BufferedImage scaledImg = new BufferedImage(INPUT_WIDTH, INPUT_HEIGHT, BufferedImage.TYPE_BYTE_GRAY);
        Graphics graphics = scaledImg.getGraphics();
        graphics.drawImage(tmp, 0, 0, null);
        graphics.dispose();
        try {
            File outputfile = new File("last_predicted_image.jpg");
            ImageIO.write(scaledImg, "jpg", outputfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return scaledImg;
    }

}
