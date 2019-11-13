import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class HelloCanvas extends Application {

	private static final String MSG = "JavaFX Rocks!";
	private static final int WIDTH = 800;
	private static final int HEIGHT = 600;

	public static void main(String[] args) {
		launch();
	}
	
	@Override
	public void start(Stage stage) throws Exception {
		Canvas canvas = new Canvas(800, 600);
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.setFill(Color.WHITESMOKE);
		gc.fillRect(0, 0, WIDTH, HEIGHT);
		gc.setFill(Color.DARKBLUE);
		gc.fillRoundRect(100, 200, WIDTH - 200, 180, 90, 90);
		gc.setTextAlign(TextAlignment.CENTER);
		gc.setFont(Font.font(60));
		gc.setFill(Color.LIGHTBLUE);
		gc.fillText(MSG, WIDTH / 2, HEIGHT / 2);
		gc.setStroke(Color.BLUE);
		gc.strokeText(MSG, WIDTH / 2, HEIGHT / 2);
		stage.setScene(new Scene(new StackPane(canvas), WIDTH, HEIGHT));
		stage.setTitle("Hello Canvas");
		stage.show();
	}

}

