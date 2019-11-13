import javafx.beans.property.*;
import javafx.scene.paint.Color;

public class ParticleSystemConf {
	IntegerProperty numberOfParticlesProperty = new SimpleIntegerProperty();
	IntegerProperty durationProperty = new SimpleIntegerProperty();
	DoubleProperty sizeProperty = new SimpleDoubleProperty();
	DoubleProperty opacityProperty = new SimpleDoubleProperty();
	BooleanProperty oscilateProperty = new SimpleBooleanProperty();
	BooleanProperty fadeOutProperty = new SimpleBooleanProperty();
	ObjectProperty<Color> colorProperty = new SimpleObjectProperty<>();

	BooleanProperty cloneConfProperty = new SimpleBooleanProperty();

	public ParticleSystemConf clone() {
		ParticleSystemConf newConf = new ParticleSystemConf();
		newConf.numberOfParticlesProperty.set(numberOfParticlesProperty.get());
		newConf.durationProperty.set(durationProperty.get());
		newConf.sizeProperty.set(sizeProperty.get());
		newConf.opacityProperty.set(opacityProperty.get());
		newConf.oscilateProperty.set(oscilateProperty.get());
		newConf.fadeOutProperty.set(fadeOutProperty.get());
		newConf.colorProperty.set(colorProperty.get());
		return newConf;
	}

}

