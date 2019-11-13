import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class HelloCanvasMany extends Application {

	private static final String MSG = "JavaFX Rocks!";
	private static final int WIDTH = 800;
	private static final int HEIGHT = 600;

int RECT_S = 100;

	public static void main(String[] args) {
		launch();
	}
	
	@Override
	public void start(Stage stage) throws Exception {
            Canvas canvas = new Canvas(WIDTH, HEIGHT);
            GraphicsContext gc = canvas.getGraphicsContext2D();
            gc.setFont(Font.font(12));
            gc.setFill(Color.BLACK);
            gc.fillRect(0, 0, WIDTH, HEIGHT);
            gc.setStroke(Color.LIGHTGRAY);
            for (int i = 0; i < WIDTH; i+=RECT_S) {
	        for (int j = 0; j < HEIGHT; j+=RECT_S) {
		    gc.strokeRect(i, j, RECT_S, RECT_S);
		    gc.setFill(Color.WHITE);
		    gc.fillText("x=" + i + ",y=" + j, i + 2, j + 12);
		    gc.setFill(Color.RED);
		    gc.fillOval(i - 4, j - 4, 8, 8);
	        }
            }
	    stage.setScene(new Scene(new StackPane(canvas), WIDTH, HEIGHT));
	    stage.setTitle("Hello Canvas");
	    stage.show();
	}

}

