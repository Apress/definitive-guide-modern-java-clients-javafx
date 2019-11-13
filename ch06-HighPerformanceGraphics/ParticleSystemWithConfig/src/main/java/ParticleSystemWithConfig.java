import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

public class ParticleSystemWithConfig extends GraphicApp {

    private List<Emitter> emitters = new ArrayList<>();

    Random random = new Random();
    private ParticleSystemConf globalConf;


    public static void main(String[] args) {
        launch();
    }

    @Override
    public void setup() {
        frames(20);
        width = 1200;
        height = 800;
        GridPane gpConfRoot = buildConfigurationRoot();
        TitledPane tpConf = new TitledPane("Configuration", gpConfRoot);
        tpConf.setCollapsible(false);
        setBottom(tpConf);
        graphicContext.getCanvas().setOnMouseClicked(e -> {
            Emitter newEmitter;
            if (globalConf.cloneConfProperty.get()) {
                newEmitter = new Emitter(e.getSceneX(), e.getSceneY(), globalConf.clone());
            } else {
                newEmitter = new Emitter(e.getSceneX(), e.getSceneY(), globalConf);
            }
            emitters.add(newEmitter);
        });
        title("Particle System configurable");
    }

    @Override
    public void draw() {
        for (Emitter emitter : emitters) {
            emitter.emit(graphicContext);
        }
    }

    public class Emitter {

        List<Particle> particles = new ArrayList<>();
        double x, y;
        private ParticleSystemConf conf;

        public Emitter(double x, double y, ParticleSystemConf conf) {
            this.x = x;
            this.y = y;
            this.conf = conf;
        }

        public void emit(GraphicsContext gc) {
            for (int i = 0; i < conf.numberOfParticlesProperty.get(); i++) {
                Particle p = new Particle(x, y, conf);
                particles.add(p);
            }
            for (Particle particle : particles) {
                particle.step();
                particle.show(gc);
            }
            particles = particles.stream().filter(p -> p.duration > 0).collect(Collectors.toList());
        }

    }

    public class Particle {
        int duration, initialDuration;
        double x, y, yDir, xDir, size, opacity, currentOpacity;
        Color color = Color.YELLOW;
        boolean oscilate, fadeOut;

        public Particle(double x, double y, ParticleSystemConf conf) {
            this.x = x;
            this.y = y;
            this.oscilate = conf.oscilateProperty.get();
            this.size = conf.sizeProperty.get();
            this.initialDuration = conf.durationProperty.get() + 1;
            this.yDir = random.nextGaussian() * 2.0 - 1.0;
            this.xDir = random.nextGaussian() * 2.0 + -1.0;
            this.opacity = conf.opacityProperty.get();
            this.fadeOut = conf.fadeOutProperty.get();
            this.duration = initialDuration;
            this.currentOpacity = opacity;

            this.color = conf.colorProperty.get();
        }

        public void step() {
            x += xDir;
            y += yDir;
            if (oscilate) {
                x += Math.sin(duration) * 10;
                y += Math.cos(duration) * 10;
            }
            if (fadeOut) {
                currentOpacity = map(duration, 0, initialDuration, 0, opacity);
            }
            duration--;
        }

        public void show(GraphicsContext gc) {
            Color cl = Color.color(color.getRed(), color.getGreen(), color.getBlue(), currentOpacity);
            gc.setFill(cl);
            gc.fillOval(x, y, size, size);
        }
    }

    private GridPane buildConfigurationRoot() {
        globalConf = new ParticleSystemConf();
        Button btnClear = new Button("Clear");
        Button btnUndo = new Button("Remove Last");
        ToggleButton tbClone = new ToggleButton("Static Configuration");
        Spinner<Integer> spFrames = new Spinner<>(1, 40, 20);
        Slider sldNumberOfParticles = new Slider(1, 1000, 500);
        Slider sldDuration = new Slider(1, 60, 30);
        Slider sldOpacity = new Slider(0, 1.0, 0.5);
        Slider sldPParticleSize = new Slider(1, 50, 25);
        ColorPicker cbColor = new ColorPicker(Color.WHITE);
        ColorPicker cbBackgrounColor = new ColorPicker(Color.BLACK);
        CheckBox cbOscillate = new CheckBox("Oscillate");
        CheckBox cbFadeOut = new CheckBox("Fade Out");
        spFrames.setPrefWidth(100);
        btnClear.setOnAction(e -> emitters = new ArrayList<>());
        spFrames.valueProperty().addListener((a, b, c) -> frames(c));
        btnUndo.setOnAction(e -> removeLast());
        cbBackgrounColor.valueProperty().addListener((a, b, c) -> background(c));
        globalConf.numberOfParticlesProperty.bind(sldNumberOfParticles.valueProperty());
        globalConf.durationProperty.bind(sldDuration.valueProperty());
        globalConf.oscilateProperty.bind(cbOscillate.selectedProperty());
        globalConf.sizeProperty.bind(sldPParticleSize.valueProperty());
        globalConf.opacityProperty.bind(sldOpacity.valueProperty());
        globalConf.fadeOutProperty.bind(cbFadeOut.selectedProperty());
        globalConf.colorProperty.bind(cbColor.valueProperty());
        globalConf.cloneConfProperty.bind(tbClone.selectedProperty());
        GridPane gp = new GridPane();
        gp.add(new Label("Number of Particles"), 0, 0);
        gp.add(sldNumberOfParticles, 1, 0);
        gp.add(new Label("Particle Duration"), 0, 1);
        gp.add(sldDuration, 1, 1);
        gp.add(new Label("Particle Size"), 2, 0);
        gp.add(sldPParticleSize, 3, 0);
        gp.add(new Label("Initial Opacity"), 2, 1);
        gp.add(sldOpacity, 3, 1);
        gp.add(new Label("Color"), 4, 0);
        gp.add(cbColor, 5, 0);
        gp.add(new Label("Background"), 4, 1);
        gp.add(cbBackgrounColor, 5, 1);
        gp.add(cbOscillate, 6, 0);
        gp.add(cbFadeOut, 6, 1);
        gp.add(new Label("Frames"), 7, 0);
        gp.add(spFrames, 8, 0);
        gp.add(btnClear, 9, 0);
        gp.add(tbClone, 8, 1);
        gp.add(btnUndo, 9, 1);
        gp.setHgap(5);
        gp.setVgap(15);
        return gp;
    }

    private void removeLast() {
        if (!emitters.isEmpty()) emitters.remove(emitters.size() - 1);
    }

}

