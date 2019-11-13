import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class ParticleSystem extends GraphicApp {

	private List<Emitter> emitters = new ArrayList<>();

	Random random = new Random();

	public static void main(String[] args) {
		launch();
	}

	@Override
	public void setup() {
		frames(50);
		width = 1200;
		height = 800;
		// you can change it to onMouseDragged
		graphicContext.getCanvas().setOnMouseDragged(e -> emitters.add(new Emitter(5, e.getSceneX(), e.getSceneY())));
		title("Simple Particle System");
	}

	@Override
	public void draw() {
		for (Emitter emitter : emitters) {
			emitter.emit(graphicContext);
		}
	}

	public class Emitter {

		List<Particle> particles = new ArrayList<>();
		int n = 1;
		double x, y;

		public Emitter(int n, double x, double y) {
			this.n = n;
			this.x = x;
			this.y = y;
		}

		public void emit(GraphicsContext gc) {
			for (int i = 0; i < n; i++) {
				int duration = random.nextInt(200) + 2;
				double yDir = random.nextDouble() * 2.0 + -1.0;
				double xDir = random.nextDouble() * 2.0 + -1.0;
				Particle p = new Particle(x, y, duration, xDir, yDir);
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
		int duration;
		double x, y, yDir, xDir;

		public Particle(double x, double y, int duration, double yDir, double xDir) {
			this.x = x;
			this.y = y;
			this.duration = duration;
			this.yDir = yDir;
			this.xDir = xDir;
		}

		public void step() {
			x += xDir;
			y += yDir;
			duration--;
		}

		public void show(GraphicsContext gc) {
			gc.setFill(Color.rgb(255, 20, 20, 0.6));
			gc.fillOval(x, y, 3, 3);
		}
	}

}

