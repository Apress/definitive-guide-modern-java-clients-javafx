import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.util.Duration;

public abstract class GraphicApp extends Application {

	protected int width = 800;
	protected int height = 600;
	protected GraphicsContext graphicContext;

	private Paint backgroundColor = Color.BLACK;
	private Timeline timeline = new Timeline();
	private int frames = 30;
	private BorderPane root;
	private Stage stage;

	@Override
	public void start(Stage stage) throws Exception {
		this.stage = stage;
		Canvas canvas = new Canvas(width, height);
		graphicContext = canvas.getGraphicsContext2D();
		canvas.requestFocus();
		root = new BorderPane(canvas);
		stage.setScene(new Scene(root));
		setup();
		canvas.setWidth(width);
		canvas.setHeight(height);
		startDrawing();
		stage.show();
		internalDraw();
	}

	public abstract void setup();

	public abstract void draw();

	public void title(String title) {
		stage.setTitle(title);
	}
	
	public void background(Paint color) {
		backgroundColor = color;
	}

	public void frames(int frames) {
		this.frames = frames;
		startDrawing();
	}

	public void setBottom(Node node) {
		root.setBottom(node);
	}

	private void internalDraw() {
		graphicContext.setFill(backgroundColor);
		graphicContext.fillRect(0, 0, width, height);
		draw();
	}

	private void startDrawing() {
		timeline.stop();
		if (frames > 0) {
			timeline.getKeyFrames().clear();
			KeyFrame frame = new KeyFrame(Duration.millis(1000 / frames), e -> internalDraw());
			timeline.getKeyFrames().add(frame);
			timeline.setCycleCount(Timeline.INDEFINITE);
			timeline.play();
		}
	}

	public double map(double value, double start1, double stop1, double start2, double stop2) {
		return start2 + (stop2 - start2) * ((value - start1) / (stop1 - start1));
	}

}

